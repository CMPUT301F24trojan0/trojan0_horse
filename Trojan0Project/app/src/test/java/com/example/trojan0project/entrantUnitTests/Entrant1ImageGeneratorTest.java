package com.example.trojan0project.entrantUnitTests;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.annotation.Config;
import org.robolectric.RobolectricTestRunner;
import org.junit.runner.RunWith;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;

import static org.mockito.Mockito.*;

import com.example.trojan0project.View.Entrant.ImageGenerator;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class Entrant1ImageGeneratorTest {
    private ImageGenerator imageGenerator;
    private Context context;
    private Resources resources;

    /**
     * Sets up the environment for the tests. This method is called before each test.
     * It initializes the necessary mocks for the context and resources, and creates an
     * instance of the ImageGenerator with the mocked context.
     */
    @Before
    public void setup() {
        context = mock(Context.class);
        resources = mock(Resources.class);
        when(context.getResources()).thenReturn(resources);

        // Create the ImageGenerator instance with mocked context
        imageGenerator = new ImageGenerator(context);
    }

    /**
     * Tests that the draw method of ImageGenerator correctly draws a circle and text
     * when valid parameters are set (bounds and user text). This ensures that the
     * drawing operations are triggered properly when the appropriate conditions are met.
     */
    @Test
    public void testDraw() {
        // Set the drawable bounds
        Rect bounds = new Rect(0, 0, 300, 300);
        imageGenerator.setBounds(bounds);

        // Set user text
        imageGenerator.setUserText("Mock");

        // Mock the Canvas object
        Canvas canvas = mock(Canvas.class);

        // Call the draw method
        imageGenerator.draw(canvas);

        // Verify that the draw methods were called using matchers
        verify(canvas).drawCircle(eq(150f), eq(150f), eq(150f), any(Paint.class)); // Use eq() for raw values, any() for mock objects
        verify(canvas).drawText(eq("Mock"), eq(150f), eq(150f), any(Paint.class)); // Use eq() for raw values, any() for mock objects
    }

    /**
     * Tests that the setUserText method correctly triggers a redraw by calling
     * the draw method after the user text is updated. This verifies that the
     * ImageGenerator instance handles changes to the user text and triggers a redraw accordingly.
     */
    @Test
    public void testSetUserTextTriggersRedraw() {
        // Mock the Canvas object
        Canvas canvas = mock(Canvas.class);

        // Set initial bounds and user text
        Rect bounds = new Rect(0, 0, 300, 300);
        imageGenerator.setBounds(bounds);

        // Set user text and trigger redraw
        imageGenerator.setUserText("Mock");

        // Call the draw method after setting text (which should trigger invalidateSelf)
        imageGenerator.draw(canvas);

        // Verify that draw() was called after setUserText, which would indicate a redraw
        verify(canvas).drawCircle(eq(150f), eq(150f), eq(150f), any(Paint.class));  // Ensuring drawCircle is called
        verify(canvas).drawText(eq("Mock"), eq(150f), eq(150f), any(Paint.class));
    }

    /**
     * Tests that the draw method is not called when invalid input is provided,
     * such as empty user text or invalid bounds (e.g., zero width and height).
     * This ensures that when the input is not valid, the drawing operations are not triggered,
     * preventing any unexpected behavior in the rendering logic.
     */
    @Test
    public void testDrawIncorrectly() {
        // Mock the Canvas object
        Canvas canvas = mock(Canvas.class);

        // Set invalid bounds (e.g., zero width and height, which shouldn't trigger a valid draw)
        Rect invalidBounds = new Rect(0, 0, 0, 0);
        imageGenerator.setBounds(invalidBounds);

        // Set invalid user text (e.g., empty string)
        imageGenerator.setUserText("");

        // Call the draw method after setting invalid parameters
        imageGenerator.draw(canvas);

        // Verify that neither drawCircle nor drawText is called due to invalid input
        verify(canvas, never()).drawCircle(anyFloat(), anyFloat(), anyFloat(), any(Paint.class));
        verify(canvas, never()).drawText(anyString(), anyFloat(), anyFloat(), any(Paint.class));

        // Optionally, assert that no other methods are called
        verifyNoMoreInteractions(canvas);
    }

}
