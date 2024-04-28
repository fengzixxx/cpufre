package com.example.app5;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.gsls.gt.GT;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@GT.Annotations.GT_AnnotationFloatingWindow(R.layout.demo_floating_window)
public class DemoFloatingWindow extends GT.GT_FloatingWindow.AnnotationFloatingWindow {

    private TextView memoryuse;
    private LineChart cpuLineChart;
    private LineChart memoryLineChart;
    private Handler handler;
    private static final int REFRESH_INTERVAL = 1000; // 刷新频率，单位毫秒
    private static final String CPU_FILENAME = "cpu_info.txt";
    private static final String MEMORY_FILENAME = "memory_info.txt";
    private static final int MAX_ENTRIES = 10;
    @Override
    protected void initView(View view) {
        super.initView(view);
        setDrag(true); // 设置可拖动

        cpuLineChart = view.findViewById(R.id.cpuLineChart);
        memoryLineChart = view.findViewById(R.id.memoryLineChart);
        memoryuse=view.findViewById(R.id.memoryTextView);

        setLineChartData();//当视图初始化时会设置折线图的数据和样式

        handler = new Handler(Looper.getMainLooper());
        startMonitoring();

    }


    private void setLineChartData() {
        //禁用描述
        cpuLineChart.getDescription().setEnabled(false);
        // 禁用 X 轴
        XAxis cpuxAxis = cpuLineChart.getXAxis();
        cpuxAxis.setEnabled(false);
        //设置cpu Y 轴
        YAxis cpuleftAxis = cpuLineChart.getAxisLeft();
        cpuleftAxis.setValueFormatter(new MyYAxisValueFormatter());
        cpuleftAxis.setTextSize(10f);

        YAxis cpurightAxis = cpuLineChart.getAxisRight();
        cpurightAxis.setEnabled(false); //禁用右Y轴

        // 内存图表初始化
        List<Entry> memoryEntries = new ArrayList<>();
        // 将初始值设置为 0，表示没有数据
        memoryEntries.add(new Entry(0, 0));
        LineDataSet memoryDataSet = new LineDataSet(memoryEntries, "内存使用");
        memoryDataSet.setValueTextSize(10f);
        memoryDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        memoryDataSet.setDrawCircles(false);

        memoryDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        List<ILineDataSet> memoryDataSets = new ArrayList<>();
        memoryDataSets.add(memoryDataSet);
        LineData memoryLineData = new LineData(memoryDataSets);
        //禁用描述
        memoryLineChart.getDescription().setEnabled(false);
        // 禁用 X 轴
        XAxis memoryxAxis = memoryLineChart.getXAxis();
        memoryxAxis.setEnabled(false);
        //设置内存 Y 轴
        YAxis memoryleftAxis = memoryLineChart.getAxisLeft();
        memoryleftAxis.setTextSize(10f);
        memoryleftAxis.setValueFormatter(new MyYAxisValueFormatter());

        YAxis memoryrightAxis = memoryLineChart.getAxisRight();
        memoryrightAxis.setEnabled(false); //禁用右Y轴
        // 设置不显示数据点的值
        memoryDataSet.setDrawValues(false);
        //设置折线图数据并更新
        memoryLineChart.setData(memoryLineData);
        memoryLineChart.invalidate();
    }
    // 自定义 Y 轴坐标格式器
    private class MyYAxisValueFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // 在这里返回自定义的 Y 轴坐标值
            // 这里 value 是 y 轴的数值，可以根据自己的需求进行转换或者格式化
            // 例如，你可以返回百分比、单位等
            int intValue = (int) value;
            switch (intValue) {
//                case 0:
//                    return "0";
//                case 20:
//                    return "20";
//                case 40:
//                    return "40";
//                case 60:
//                    return "60";
//                case 80:
//                    return "80";
                case 100:
                    return "100";
                default:
                    return "";
            }
        }
    }


    private void startMonitoring() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 获取并记录 CPU 信息
                String cpuInfo = getAllCPUFrequencies();
                saveToFile(CPU_FILENAME, cpuInfo);
                // 更新CPU折线图
                updateCpuChart();

                // 获取并记录内存信息
                String memoryInfo = getMemoryInfo();
                saveToFile(MEMORY_FILENAME, memoryInfo);

                // 更新内存折线图
                updateMemoryChart();

                // 继续定时查询
                handler.postDelayed(this, REFRESH_INTERVAL);
            }
        }, REFRESH_INTERVAL);
    }

    private void updateCpuChart() {
        List<Entry> cpuEntries = getCpuEntries();
        LineDataSet cpuDataSet = new LineDataSet(cpuEntries, "CPU 频率");
        cpuDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        cpuDataSet.setValueTextSize(10f); // 设置为你想要的字体大小
        LineData cpuData = new LineData(cpuDataSet);
        cpuData.notifyDataChanged();
        cpuLineChart.notifyDataSetChanged();
        cpuLineChart.setData(cpuData);
        cpuLineChart.invalidate(); // 刷新图表
        // 更新数据后重新设置 Y 轴的最小值和最大值
        cpuLineChart.getAxisLeft().setAxisMinimum(0f);
        cpuLineChart.getAxisLeft().setAxisMaximum(3000f);
    }

    private void updateMemoryChart() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final float memoryUsagePercentage = getMemoryUsagePercentage();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 更新内存折线图
                        LineData lineData = memoryLineChart.getData();
                        ILineDataSet dataSet = lineData.getDataSetByIndex(0);

                        // 添加新的数据点
                        Entry newEntry = new Entry(dataSet.getEntryCount(), memoryUsagePercentage);
                        dataSet.addEntry(newEntry);

                        // 更新数据后重新设置 Y 轴的最小值和最大值
                        memoryLineChart.getAxisLeft().setAxisMinimum(0f);
                        memoryLineChart.getAxisLeft().setAxisMaximum(100f);

                        // 调整 X 轴范围
                        memoryLineChart.setVisibleXRangeMaximum(MAX_ENTRIES);
                        memoryLineChart.moveViewToX(Math.max(0, dataSet.getEntryCount() - MAX_ENTRIES));

                        lineData.notifyDataChanged();
                        memoryLineChart.notifyDataSetChanged();
                        memoryLineChart.invalidate();
                        memoryuse.setText(String.format("%.2f",memoryUsagePercentage)+ "%");

                    }
                });
            }
        }).start();
    }



    private List<Entry> getCpuEntries() {
        List<Entry> entries = new ArrayList<>();
        int cpuCoreCount = getCpuCoreCount();
        for (int cpuNumber = 0; cpuNumber < cpuCoreCount; cpuNumber++) {
            String cpuFrequencyPath = String.format(Locale.getDefault(), "/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq", cpuNumber);
            long cpuFrequency = readCpuFrequency(cpuFrequencyPath);
            entries.add(new Entry(cpuNumber, cpuFrequency));
        }
        return entries;
    }



    private float getMemoryUsagePercentage() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            long totalMemory = memoryInfo.totalMem; // 总内存，单位 bytes
            long freeMemory = memoryInfo.availMem; // 可用内存，单位 bytes
            long usedMemory = totalMemory - freeMemory; // 已使用内存，单位 bytes
            float memoryUsagePercentage = (float) (usedMemory * 100) / totalMemory; // 内存使用率
            return memoryUsagePercentage;
        } else {
            return 0;
        }
    }
    private int getCpuCoreCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    private String getAllCPUFrequencies() {
        StringBuilder allCoresInfo = new StringBuilder();
        int cpuCoreCount = getCpuCoreCount();
        for (int cpuNumber = 0; cpuNumber < cpuCoreCount; cpuNumber++) {
            String cpuFrequencyPath = String.format("/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq", cpuNumber);
            long cpuFrequency = readCpuFrequency(cpuFrequencyPath);
            allCoresInfo.append(getCurrentTime()).append("-")
                    .append("CPU").append(cpuNumber).append(" 频率: ").append(cpuFrequency).append(" MHz\n");
        }
        return allCoresInfo.toString();
    }


    private long readCpuFrequency(String cpuFrequencyPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(cpuFrequencyPath))) {
            String line;
            if ((line = br.readLine()) != null) {
                // 将 CPU 频率转换成 MHz
                long freqInKHz = Long.parseLong(line.trim());
                return freqInKHz / 1000;
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getMemoryInfo() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            long totalMemory = memoryInfo.totalMem / (1024 * 1024); // 转换为 MB
            long freeMemory = memoryInfo.availMem / (1024 * 1024); // 转换为 MB
            // 将内存信息存储到列表中
            return getCurrentTime() + " - " +
                    "总内存: " + totalMemory + " MB\n可用内存: " + freeMemory + " MB";
        } else {
            return "无法获取内存信息";
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void saveToFile(String filename, String data) {
        try {
            File file = new File(getFilesDir(), filename);
            FileWriter fileWriter = new FileWriter(file, true); // 设置为 true，以便将数据追加到文件末尾
            fileWriter.append(data).append("\n"); // 使用 append 方法追加数据
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GT.Annotations.GT_Click({R.id.btn_ok, R.id.tv_back, R.id.btn_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                GT.toast("单击了ok");
                break;
            case R.id.tv_back:
            case R.id.btn_cancel:
                finish(); // 关闭当前悬浮窗
                break;
        }
    }
}
