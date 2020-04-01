package io.github.kntryer.linechart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by kn on 2016/11/1.
 * <p/>
 * 折线图
 */
public class LineChart extends View {

    private int mWidth, mHeight;//View 的宽和高

    private float mFontSize = 12;//字体的大小
    private float mStrokeWidth = 1.5f;//线条的宽度
    private float mPointRadius = 2;//点的半径
    private int mDateTextColor = Color.parseColor("#cfcfcf");//日期字体颜色
    private int mDarkColor = Color.parseColor("#5b7fdf");//点、线的颜色(深色)
    private int mLightColor = Color.parseColor("#d5d8f7");//点、线的颜色(浅色)
    private int mShapeColor = Color.parseColor("#f3f6fd");//阴影的颜色

    private int mGradientLightColor = Color.parseColor("#d5d8f7");//渐变浅色
    private int mGradientDarkColor = Color.parseColor("#5b7fdf");//渐变深色
    private boolean mIsShowGradient = false;//是否显示渐变

    private String[] mXItems;//X轴的文字
    private int[] mPoints;//点的数组，-1表示该日还没到
    private int mLength = 7;//最大比例

    private Paint mDatePaint = new Paint();//日期画笔
    private Paint mPointPaint = new Paint();//点画笔
    private Paint mLinePaint = new Paint();//线条画笔
    private Paint mShapePaint = new Paint();//阴影部分画笔

    private int max = 7;
    private Context mContext;

    public LineChart(Context context) {
        this(context, null);
    }

    public LineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray typedArray = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.LineChart, 0, 0);
        try {
            mDateTextColor = typedArray.getColor(R.styleable.LineChart_DateTextColor, mDateTextColor);
            mDarkColor = typedArray.getColor(R.styleable.LineChart_DarkColor, mDarkColor);
            mLightColor = typedArray.getColor(R.styleable.LineChart_LightColor, mLightColor);
            mShapeColor = typedArray.getColor(R.styleable.LineChart_ShapeColor, mShapeColor);
            mFontSize = typedArray.getDimensionPixelSize(R.styleable.LineChart_FontSize,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mFontSize, mContext.getResources().getDisplayMetrics()));
            mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.LineChart_StrokeWidth,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mStrokeWidth, mContext.getResources().getDisplayMetrics()));
            mPointRadius = typedArray.getDimensionPixelSize(R.styleable.LineChart_PointRadius,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mPointRadius, mContext.getResources().getDisplayMetrics()));
            mGradientLightColor = typedArray.getColor(R.styleable.LineChart_GradientLightColor, mGradientLightColor);
            mGradientDarkColor = typedArray.getColor(R.styleable.LineChart_GradientDarkColor, mGradientDarkColor);
            mIsShowGradient = typedArray.getBoolean(R.styleable.LineChart_IsShowGradient, mIsShowGradient);
        } finally {
            typedArray.recycle();
        }
        initPaint();
    }

    private void initPaint() {
        //日期画笔
        mDatePaint.setTextSize(mFontSize);
        mDatePaint.setColor(mDateTextColor);
        //点画笔
        mPointPaint.setTextSize(mFontSize);
        mPointPaint.setColor(mDarkColor);
        //先画笔
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(mStrokeWidth);//设置线条宽度
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(mDarkColor);
        //阴影部分画笔
        mShapePaint.setAntiAlias(true);
        mShapePaint.setStyle(Paint.Style.FILL);
        mShapePaint.setColor(mShapeColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        mWidth = widthSize;
        mHeight = heightSize;
//        if (widthMode == MeasureSpec.EXACTLY) {
//            mWidth = widthSize;
//        } else if (widthMode == MeasureSpec.AT_MOST) {
//            mWidth = widthSize;
//        }
//
//        if (heightMode == MeasureSpec.EXACTLY) {
//            mHeight = heightSize;
//        } else if (heightMode == MeasureSpec.AT_MOST) {
//            mHeight = mWidth / 7 * 3;
//        }

        if (heightMode == MeasureSpec.AT_MOST) {
            mHeight = mWidth / 7 * 3;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    private float mAnimatedValue = 0f;

    protected void OnAnimationUpdate(ValueAnimator valueAnimator) {
        mAnimatedValue = (float) valueAnimator.getAnimatedValue();
        invalidate();
    }

    public ValueAnimator valueAnimator;

    private ValueAnimator startViewAnim(float startF, final float endF, long time) {
        valueAnimator = ValueAnimator.ofFloat(startF, endF);
        valueAnimator.setDuration(time);
        valueAnimator.setInterpolator(new LinearInterpolator());

//        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);

//        valueAnimator.setRepeatMode(ValueAnimator.RESTART);

        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                OnAnimationUpdate(valueAnimator);

            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }
        });
        if (!valueAnimator.isRunning()) {
            valueAnimator.start();
        }

        return valueAnimator;
    }

    // 阴影部分梯形路径
    Path path = new Path();
    // 渐变
    Shader mShader = new LinearGradient(0, 0, 0, mHeight,
            new int[]{mGradientLightColor, mGradientDarkColor}, null, Shader.TileMode.CLAMP);

    int[] xPoints = new int[mLength];//x轴的刻度集合
    int[] yPoints = new int[mLength];//y轴的刻度集合

    @Override
    protected void onDraw(Canvas canvas) {

        if (mXItems == null) {
            mXItems = new String[]{"日", "一", "二", "三", "四", "五", "六"};
            mPoints = new int[]{0, -1, -1, -1, -1, -1, -1};
            mLength = mXItems.length;
            xPoints = new int[mLength];
            yPoints = new int[mLength];
        }

        //最大比例
        for (int i = 0; i < mLength; i++) {
            if (mPoints[i] > max) {
                max = mPoints[i];
            }
        }

        //原点坐标
        int xOrigin = (int) (0.5 * (mWidth / mLength) - mFontSize / 2);
        int yOrigin = (int) (max * ((mHeight - mLength * mFontSize) / max) + 4 * mFontSize);

        canvas.save();

        for (int i = 0; i < mLength; i++) {

            //获取点的坐标
            xPoints[i] = (int) ((i + 0.5) * (mWidth / mLength));
            yPoints[i] = (int) ((max - (mPoints[i] == -1 ? 0 : mPoints[i]) * mAnimatedValue) * ((mHeight - mLength * mFontSize) / max) + 4 * mFontSize);

            if (i > 0) {
                //画一个实心梯形,阴影部分
                path.moveTo(xPoints[i - 1], yOrigin + mPointRadius / 2);
                path.lineTo(xPoints[i - 1], yPoints[i - 1]);
                path.lineTo(xPoints[i], yPoints[i]);
                path.lineTo(xPoints[i], yOrigin + mPointRadius / 2);
                path.close();

                if (mIsShowGradient) {
                    //添加线性渐变
                    mShapePaint.setShader(mShader);
                }
                canvas.drawPath(path, mShapePaint);
                path.reset();
            }
            //画出日期
            canvas.drawText(mXItems[i], (int) ((i + 0.5) * mWidth / mLength) - mFontSize / 2, mHeight - mFontSize, mDatePaint);
        }

        for (int i = 0; i < mLength; i++) {
            mLinePaint.setColor(mPoints[i] == -1 ? mLightColor : mDarkColor);
            mPointPaint.setColor(mPoints[i] == -1 ? mLightColor : mDarkColor);
            if (i > 0) {
                //画连线
                canvas.drawLine(xPoints[i - 1], yPoints[i - 1], xPoints[i], yPoints[i], mLinePaint);
            }
            //画点的数值
            canvas.drawText(mPoints[i] == -1 ? " " : String.valueOf((int) Math.floor(mPoints[i] * mAnimatedValue))
                    , xPoints[i] - mFontSize / 4, yPoints[i] - mFontSize, mPointPaint);
        }

        // 顶点
        for (int i = 0; i < mLength; i++) {
            mPointPaint.setColor(mPoints[i] == -1 ? mLightColor : mDarkColor);
            mLinePaint.setColor(Color.parseColor("#FFFFFF"));
            //画大点
            canvas.drawCircle(xPoints[i], yPoints[i], mPointRadius + 2, mPointPaint);
            //画小点
            canvas.drawCircle(xPoints[i], yPoints[i], mPointRadius, mLinePaint);
        }

        canvas.restore();
    }

    public void setData(List<LineChartData> dataList) {
        mLength = dataList.size();
        if (mLength > 0) {
            mXItems = new String[mLength];
            mPoints = new int[mLength];
            for (int i = 0; i < mLength; i++) {
                mPoints[i] = dataList.get(i).getPoint();
                mXItems[i] = dataList.get(i).getItem();
            }
        }
        startViewAnim(0f, 1f, 1000);
    }

    //设置是否显示渐变
    public LineChart setIsShowGradient(boolean mIsShowGradient) {
        this.mIsShowGradient = mIsShowGradient;
        return this;
    }

    //设置渐变颜色
    public LineChart setGradientColor(String mLightColor, String mDarkColor) {
        this.mGradientLightColor = Color.parseColor(mLightColor);
        this.mGradientDarkColor = Color.parseColor(mDarkColor);
        mShader = new LinearGradient(0, 0, 0, mHeight,
                new int[]{mGradientLightColor, mGradientDarkColor}, null, Shader.TileMode.CLAMP);
        return this;
    }

    //设置 view 高度
    public LineChart setHigh(int mHeight) {
        mHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mHeight, mContext.getResources().getDisplayMetrics());
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        if (layoutParams.height != mHeight) {
            layoutParams.height = mHeight;
            this.setLayoutParams(layoutParams);
        }
        this.mHeight = mHeight;
        mShader = new LinearGradient(0, 0, 0, mHeight,
                new int[]{mGradientLightColor, mGradientDarkColor}, null, Shader.TileMode.CLAMP);
        return this;
    }

    //刷新view
    public void refreshView() {
        startViewAnim(0f, 1f, 1000);
    }

}
