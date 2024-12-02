package com.example.trojan0project;

/**
 * Purpose:
 * The Image class represents an image object and the ability to get and set image id's.
 *
 * Design Rationale:
 * Uses constructors for getting and setting image id's.
 *
 * Outstanding Issues:
 * - No Issues.
 */

public class Image {
    private String imageId;
    /**
     * Constructs an Image object with a specified image ID.
     *
     * @param imageId The unique identifier for the image.
     */
    public Image (String imageId){
        this.imageId = imageId;
    }
    /**
     * Retrieves the image ID.
     *
     * @return The unique identifier for the image.
     */
    public String getImageId() {
        return imageId;
    }
    /**
     * Sets a new image ID.
     *
     * @param imageId The new unique identifier for the image.
     */
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
