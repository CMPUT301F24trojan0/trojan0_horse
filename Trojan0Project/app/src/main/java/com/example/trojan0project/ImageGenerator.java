package com.example.trojan0project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import java.util.Random;

// code from https://developer.android.com/develop/ui/views/graphics/drawables#java

public class ImageGenerator extends Drawable {
    private Paint paint;
    private Paint textPaint;
    private String userText = "";

    private static final int[] COLORS = {
            R.color.light_red, R.color.light_green, R.color.light_blue,
            R.color.light_yellow, R.color.light_cyan, R.color.light_magenta,
            R.color.light_gray
    };

    public ImageGenerator(Context context) {
        // Set up color and text size for the circle
        paint = new Paint();
        paint.setColor(generateRandomColor(context));

        // Set up paint for text
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK); // Set text color to white
        textPaint.setTextSize(175); // Set text size
        textPaint.setTextAlign(Paint.Align.CENTER); // Center-align the text
        textPaint.setAntiAlias(true); // Smooth edges of text
    }

    // Method to set user text
    public void setUserText(String text) {
        userText = text;
        invalidateSelf(); // Request redraw with new text
    }

    private int generateRandomColor(Context context) {
        Random random = new Random();
        int colorResId = COLORS[random.nextInt(COLORS.length)];
        return ContextCompat.getColor(context, colorResId);
    }

    @Override
    public void draw(Canvas canvas) {
        // Get the drawable's bounds
        int width = getBounds().width();
        int height = getBounds().height();
        float radius = Math.min(width, height) / 2;

        // Draw a red circle in the center
        canvas.drawCircle(width / 2, height / 2, radius, paint);

        // Draw user-inputted text in the center of the circle
        canvas.drawText(userText, width / 2, height / 2 - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        textPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
        textPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
