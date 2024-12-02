
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
/**
 * Purpose:
 * This class generates a drawable that displays a colored circle with text  in it.
 *
 * Design Rationale:
 * The drawable is used for specific visuals that require a circle around the text
 * for exmaple: a profile picture
 *
 * Outstanding Issues:
 * - No Issues.
 */

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
    /**
     * Constructor that initializes the drawable with random background color and text properties.
     *
     * @param context The context for accessing color resources.
     */
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

    /**
     * Sets the user text to display in the center of the circle.
     *
     * @param text The text to display.
     */
    public void setUserText(String text) {
        userText = text;
        invalidateSelf(); // Request redraw with new text
    }
    /**
     * Generates a random color from a predefined array of colors.
     *
     * @param context The context for accessing color resources.
     * @return A randomly selected color.
     */
    public int generateRandomColor(Context context) {
        Random random = new Random();
        int colorResId = COLORS[random.nextInt(COLORS.length)];
        return ContextCompat.getColor(context, colorResId);
    }
    /**
     * Draws the circular drawable and centered text.
     *
     * @param canvas The Canvas to draw on.
     */
    @Override
    public void draw(Canvas canvas) {
        // Get the drawable's bounds
        int width = getBounds().width();
        int height = getBounds().height();

        if (width <= 0 || height <= 0) {
            return;
        }

        float radius = Math.min(width, height) / 2;

        // Draw a red circle in the center
        canvas.drawCircle(width / 2, height / 2, radius, paint);

        // Draw user-inputted text in the center of the circle
        canvas.drawText(userText, width / 2, height / 2 - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint);
    }
    /**
     * Sets the alpha (transparency) level for the drawable.
     *
     * @param alpha The alpha value to apply.
     */
    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        textPaint.setAlpha(alpha);
    }
    /**
     * Sets a color filter on the drawable.
     *
     * @param colorFilter The color filter to apply.
     */
    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
        textPaint.setColorFilter(colorFilter);
    }
    /**
     * Returns the opacity/transparency mode for the drawable.
     *
     * @return The opacity setting.
     */
    @Override
    public int getOpacity() {
        return 0;
    }
}
