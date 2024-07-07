package com.example.madprojectnew;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PieChartView extends View{
    private Paint paint = new Paint();
    private float[] data = {10, 20, 30, 40}; // Sample data
    private int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW}; // Sample colors

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float total = 0;
        for (float value : data) {
            total += value;
        }

        float startAngle = 0;
        for (int i = 0; i < data.length; i++) {
            float sweepAngle = (data[i] / total) * 360;
            paint.setColor(colors[i]);
            canvas.drawArc(100, 100, 500, 500, startAngle, sweepAngle, true, paint);
            startAngle += sweepAngle;
        }

    }

    public void setData(float[] newData) {
        data = newData;
        invalidate(); // Redraw the view
    }
}
