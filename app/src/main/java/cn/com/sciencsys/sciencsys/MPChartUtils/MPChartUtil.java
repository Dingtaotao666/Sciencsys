package cn.com.sciencsys.sciencsys.MPChartUtils;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class MPChartUtil {
    /**
     * 坐标线，LineDataSet(坐标点的集合, 线的描述或名称);
     */
    //private static Vector<ILineDataSet> dataSets = new Vector<ILineDataSet>();
    private static List<String> xValues;


    /**
     * @param lineChart
     * @param isYStartAtZero     是否从0开始
     * @param yAxisMinValue      最小值
     * @param yAxisMaxValue      最大值
     * @param isPercentFormatter 是否百分比显示
     */

    private static void setYAxis(LineChart lineChart, boolean isYStartAtZero,
                                 float yAxisMinValue, float yAxisMaxValue, boolean isPercentFormatter) {
        YAxis yAxis = lineChart.getAxisLeft();

        // Y轴刻度值颜色
        yAxis.setTextColor(Color.parseColor("#e0e0e0"));

        // 设置Y轴坐标是否从0开始
        yAxis.setStartAtZero(isYStartAtZero);
        // 设置Y轴坐标最大为多少
        yAxis.setAxisMaxValue(yAxisMaxValue);
        // 设置Y轴坐标最小为多少
        yAxis.setAxisMinValue(yAxisMinValue);

        // Y轴上的刻度竖线的颜色
        yAxis.setGridColor(Color.parseColor("#eeeeee"));

        // 设置Y轴上刻度竖线的宽度
        yAxis.setGridLineWidth(1.2f);

        //Y轴显示百分之几
        if (isPercentFormatter) {
            yAxis.setValueFormatter(new PercentFormatter());
        }

        // Y轴上的刻度的颜色
        yAxis.setTextColor(Color.parseColor("#9a9a9a"));

        yAxis.setDrawAxisLine(true);
    }

    /**
     * 设置X轴相关
     *
     * @param lineChart
     * @param xAxisStartPosition X轴起点
     * @param point              一个X轴点内部有几个数据点
     * @param labelToSkip        间隔位置
     */
    private static void setXAxis(LineChart lineChart, int xAxisStartPosition, int xAxisEndPosition, int point, int
            labelToSkip, String XTitleSuffix) {
        XAxis xAxis = lineChart.getXAxis();
        // X轴上的刻度的颜色
        xAxis.setTextColor(Color.parseColor("#a0a0a0"));

        // X轴颜色
        xAxis.setAxisLineColor(Color.BLUE);

        // X轴上的刻度竖线的颜色
        xAxis.setGridColor(Color.parseColor("#eeeeee"));

        // 设置X轴上刻度竖线的宽度
        xAxis.setGridLineWidth(1.2f);

        // X轴位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // 设置坐标相隔多少，参数是int类型
        //xAxis.set(labelToSkip);

        // 设置为true，则绘制该行旁边的轴线（axis-line）。
        xAxis.setDrawAxisLine(true);

        xValues = new ArrayList<>();
        xValues.clear();
        for (int i = xAxisStartPosition; i <= point * xAxisEndPosition; i++) {
            xValues.add((i / point) + XTitleSuffix);
        }
    }

    /**
     * @param lineChart
     * @param xAxisStartPosition X轴起点
     * @param xAxisEndPosition   X轴终点
     * @param point              一个X轴点内部有几个数据点
     * @param labelToSkip        间隔
     * @param isYStartAtZero     Y轴是否从0开始
     * @param yAxisMinValue      Y轴最小值
     * @param yAxisMaxValue      Y轴最大值
     * @param isPercentFormatter Y轴是否百分比显示
     * @param XTitleSuffix       x轴标题后缀文字
     */
    //2016/09/04 liuwei添加XTitleSuffix，实现x轴标题后缀文字
    public static void initLineChart(LineChart lineChart,
                                     int xAxisStartPosition, int xAxisEndPosition, int point, int labelToSkip,
                                     boolean isYStartAtZero, float yAxisMinValue, float yAxisMaxValue, boolean
                                             isPercentFormatter, String XTitleSuffix, Description description) {
        //dataSets.clear();
        // 设置描述
        lineChart.setDescription(description);
        // 设置透明度
        lineChart.setAlpha(0.8f);
        // 挤压缩放
        lineChart.setPinchZoom(true);
        // 双击缩放
        lineChart.setDoubleTapToZoomEnabled(true);
        // 图例关闭
        lineChart.getLegend().setEnabled(false);
        // 设置是否可以触摸
        lineChart.setTouchEnabled(true);
        // 是否可以拖拽
        lineChart.setDragEnabled(true);
        // 是否可以缩放 x和y轴, 默认是true
        lineChart.setScaleEnabled(true);
        // Y轴右侧禁用
        lineChart.getAxisRight().setEnabled(false);
        // 设置X轴相关
        setXAxis(lineChart, xAxisStartPosition, xAxisEndPosition, point, labelToSkip, XTitleSuffix);
        // 设置Y轴相关
        setYAxis(lineChart, isYStartAtZero, yAxisMinValue, yAxisMaxValue, isPercentFormatter);
    }

    /**
     * @param lineChart
     * @param values      数据点
     * @param offset      X轴开始坐标偏移量
     * @param color       线的颜色
     * @param isDrawCubic 是否圆滑
     */
    public synchronized static void addLineToLineChart(LineChart lineChart,
                                                       float[] values, int[] offset, int color, boolean isDrawCubic) {

        // 坐标点的集合
        ArrayList<Entry> entries = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            // 坐标点的值，Entry(Y坐标，X坐标)
            entries.add(new Entry(values[i], i + offset[i]));
        }
        // 坐标线，LineDataSet(坐标点的集合, 线的描述或名称);
        LineDataSet lineDataSet = new LineDataSet(entries, "");

        // 设置线的颜色
        lineDataSet.setColor(color);
        // 设置线的宽度
        lineDataSet.setLineWidth(1.2f);
        // 设置曲线为圆滑的线
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 取消数据点上的数值显示
        lineDataSet.setDrawValues(false);
        // 取消数据点上的原点
        lineDataSet.setDrawCircles(false);
        // lineChart.getLineData().getDataSets().add(lineDataSet);

        LineData data = null;
        if (lineChart.getData() == null) {
            List<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(lineDataSet);
            //data = new LineData(xValues, dataSets);
        } else {
            lineChart.getData().getDataSets().add(lineDataSet);
            //data = new LineData(xValues, lineChart.getData().getDataSets());
        }
        //  add line
        // dataSets.add(lineDataSet);

        // 为图表添加数据
        lineChart.setData(data);
        // // 重新更新显示
        lineChart.invalidate();
    }


    /**
     * 添加限制线
     *
     * @param lineChart
     * @param value              限制线的值
     * @param description        描述
     * @param lineColor          颜色
     * @param isEnableDashedLine 是否虚线
     */
    public static void addLimitLineToLineChart(LineChart lineChart,
                                               float value, String description, int lineColor,
                                               boolean isEnableDashedLine) {
        LimitLine limitLine = new LimitLine(value, description);
        limitLine.setLineColor(lineColor);
        limitLine.setLineWidth(1f);
        limitLine.setTextColor(lineColor);
        limitLine.setTextSize(10f);
        if (isEnableDashedLine) {
            limitLine.enableDashedLine(10f, 10f, 0f);
        }
        // .. and more styling options
        lineChart.getAxisLeft().addLimitLine(limitLine);
    }

}
