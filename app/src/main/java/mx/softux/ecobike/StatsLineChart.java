package mx.softux.ecobike;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;

import org.paoloconte.smoothchart.SmoothLineChart;

/**
 * Created by gianpa on 11/16/14.
 */
public class StatsLineChart extends SmoothLineChart {
    public StatsLineChart(Context context) {
        super(context);
    }

    public StatsLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatsLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setData(StationModel.Stat[] stats) {
        PointF[] points = new PointF[stats.length];
        for(int i = 0; i < stats.length; i++) {
            float x = i;
            float y = stats[i].bikes;

            points[i] = new PointF(x, y);
        }
        setData(points);
    }
}
