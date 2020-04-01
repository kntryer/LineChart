package io.github.kntryer.linechart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LineChart mLineChart;
    private TextView tvDay;
    private TextView tvWeek;
    private TextView tvMonth;
    private TextView tvGradient;

    private String[] mDayItems = new String[]{"31日", "1日", "2日", "3日", "4日", "5日", "6日"};
    private int[] mDayPoints = new int[]{0, 2, 7, 4, 0, 1, -1};
    private String[] mWeekItems = new String[]{"日", "一", "二", "三", "四", "五", "六"};
    private int[] mWeekPoints = new int[]{7, 2, 1, 4, 0, 1, -1};
    private String[] mMonthItems = new String[]{"5月", "6月", "7月", "8月", "9月", "10月", "11月"};
    private int[] mMonthPoints = new int[]{0, 2, 1, 0, 0, 0, 8};
    private List<LineChartData> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLineChart = findViewById(R.id.line_chart);
        tvDay = findViewById(R.id.tv_day);
        tvWeek = findViewById(R.id.tv_week);
        tvMonth = findViewById(R.id.tv_month);
        tvGradient = findViewById(R.id.tv_gradient);

        resetTextColor();
        tvDay.setTextColor(getResources().getColor(R.color.colorPrimary));
        for (int i = 0; i < mDayItems.length; i++) {
            LineChartData data = new LineChartData();
            data.setItem(mDayItems[i]);
            data.setPoint(mDayPoints[i]);
            dataList.add(data);
        }
        mLineChart.setData(dataList);
    }

    public void dayClick(View view){
        resetTextColor();
        tvDay.setTextColor(getResources().getColor(R.color.colorPrimary));
        dataList.clear();
        for (int i = 0; i < mDayItems.length; i++) {
            LineChartData data = new LineChartData();
            data.setItem(mDayItems[i]);
            data.setPoint(mDayPoints[i]);
            dataList.add(data);
        }
        mLineChart.setData(dataList);
    }

    public void weekClick(View view){
        resetTextColor();
        tvWeek.setTextColor(getResources().getColor(R.color.colorPrimary));
        dataList.clear();
        for (int i = 0; i < mWeekItems.length; i++) {
            LineChartData data = new LineChartData();
            data.setItem(mWeekItems[i]);
            data.setPoint(mWeekPoints[i]);
            dataList.add(data);
        }
        mLineChart.setData(dataList);
    }

    public void monthClick(View view){
        resetTextColor();
        tvMonth.setTextColor(getResources().getColor(R.color.colorPrimary));
        dataList.clear();
        for (int i = 0; i < mMonthItems.length; i++) {
            LineChartData data = new LineChartData();
            data.setItem(mMonthItems[i]);
            data.setPoint(mMonthPoints[i]);
            dataList.add(data);
        }
        mLineChart.setData(dataList);
    }

    public void gradientClick(View view){
        resetTextColor();
        tvGradient.setTextColor(getResources().getColor(R.color.colorPrimary));
        mLineChart.setIsShowGradient(true)
                .setGradientColor("#f3f6fd", "#fa267b")
                .setHigh(300)
                .refreshView();
    }

    public void resetTextColor(){
        tvDay.setTextColor(getResources().getColor(R.color.text_title));
        tvWeek.setTextColor(getResources().getColor(R.color.text_title));
        tvMonth.setTextColor(getResources().getColor(R.color.text_title));
        tvGradient.setTextColor(getResources().getColor(R.color.text_title));
    }
}
