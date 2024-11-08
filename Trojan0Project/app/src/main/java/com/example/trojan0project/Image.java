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
package com.example.trojan0project;

public class Image {
    private String imageId;

    public Image (String imageId){
        this.imageId = imageId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
