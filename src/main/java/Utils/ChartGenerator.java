package Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ChartGenerator {

    public static String generateBarChartUrl(Map<String, Integer> dataMap, String chartTitle) {
        return generateCustomBarChartUrl(dataMap, chartTitle, "#85c20a", false, "black", "bar");
    }

    public static String generateCustomBarChartUrl(Map<String, ? extends Number> dataMap, String chartTitle, String barColor, boolean integerTicks, String fontColor, String chartType) {
        StringBuilder labels = new StringBuilder();
        StringBuilder data = new StringBuilder();

        for (Map.Entry<String, ? extends Number> entry : dataMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase("Rupture")) continue; // exclusion de "Rupture"
            labels.append("'").append(entry.getKey()).append("',");
            data.append(entry.getValue()).append(",");
        }

        if (labels.length() > 0) labels.setLength(labels.length() - 1);
        if (data.length() > 0) data.setLength(data.length() - 1);

        String chartConfig = String.format("""
        {
          type: '%s',
          data: {
            labels: [%s],
            datasets: [{
              label: '%s',
              data: [%s],
              backgroundColor: %s
            }]
          },
          options: {
            scales: %s,
            plugins: {
              legend: {
                labels: {
                  color: '%s'
                }
              }
            }
          }
        }
        """,
                chartType,
                labels.toString(),
                chartTitle,
                data.toString(),
                chartTitle.equals("État du stock") && chartType.equals("doughnut") ? "['#FFB524','#4CAF50']" : generateColors(dataMap.size(), barColor),
                chartType.equals("bar") || chartType.equals("line") ? "{ yAxes: [{ ticks: { beginAtZero: true, stepSize: " + (integerTicks ? 1 : 0) + " } }] }" : "{}",
                fontColor
        );

        try {
            return "https://quickchart.io/chart?c=" + URLEncoder.encode(chartConfig, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String generateColors(int size, String baseColor) {
        StringBuilder colors = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            colors.append("'").append(baseColor).append("'");
            if (i < size - 1) colors.append(",");
        }
        colors.append("]");
        return colors.toString();
    }
}
