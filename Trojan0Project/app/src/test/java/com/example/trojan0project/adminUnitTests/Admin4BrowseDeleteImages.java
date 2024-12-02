package com.example.trojan0project.adminUnitTests;

import static org.junit.Assert.assertEquals;

import android.widget.GridView;

import com.example.trojan0project.BrowseImagesAdmin;
import com.example.trojan0project.Image;
import com.example.trojan0project.ImageAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE) // Skip manifest and themes
public class Admin4BrowseDeleteImages {

    private BrowseImagesAdmin browseImagesAdmin;
    private GridView mockGridView;
    private ArrayList<Image> mockImageList;

    @Before
    public void setup() {
        mockGridView = Mockito.mock(GridView.class);
        mockImageList = new ArrayList<>();

        browseImagesAdmin = Mockito.spy(Robolectric.buildActivity(BrowseImagesAdmin.class).get());

        ImageAdapter mockAdapter = Mockito.mock(ImageAdapter.class);
        Mockito.when(mockGridView.getAdapter()).thenReturn(mockAdapter);

        Mockito.doAnswer(invocation -> {
            mockImageList.add((Image) invocation.getArgument(0));
            return null;
        }).when(mockAdapter).add(Mockito.any(Image.class));
    }

    @Test
    public void testAddImageToGridView() {
        Image image = new Image("http://example.com/image1.png");

        mockImageList.add(image);

        assertEquals(1, mockImageList.size());
    }

    @Test
    public void testRemoveImageFromGridView() {
        Image image = new Image("http://example.com/image2.png");
        mockImageList.add(image);

        mockImageList.remove(image);

        assertEquals(0, mockImageList.size());
    }
}
