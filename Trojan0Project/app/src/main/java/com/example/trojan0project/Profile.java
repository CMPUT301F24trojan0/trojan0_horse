/**
 * Purpose:
 * This class represents a user profile, storing essential details like name, email, username,
 * profile image, and device ID. It is used to load and display profile information and use
 * Firestore Firebase to manage it.
 *
 * Design Rationale:
 * The class provides constructors for flexibility in creating Profile objects based on the context.
 * For example, some scenarios require a username and device ID, while others need full user details
 * like name and email.
 *
 * Outstanding Issues:
 * No Issues.
 */

package com.example.trojan0project;

public class Profile {
    private String firstName;
    private String lastName;
    private String email;
    private String profileImage;
    private String username;
    private String deviceId;
    /**
     * Constructs a Profile with the specified first name, last name, and email.
     *
     * @param firstName The first name of the user.
     * @param lastName  The last name of the user.
     * @param email     The email address of the user.
     */
    public Profile(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    /**
     * Constructs a Profile with the specified username and profile image.
     *
     * @param username    The username of the user.
     *  The URL of the user's profile image.
     */
    public Profile(String username, String deviceId){
        this.username = username;
        this.deviceId = deviceId;
    }

    /**
     * Gets the device id of the user.
     *
     * @return The device is of the user.
     */
    public String getDeviceId(){
        return deviceId;
    }

    /**
     * Sets the device id of the user
     *
     * @param deviceId
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    /**
     * Gets the first name of the user.
     *
     * @return The first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     * Sets the first name of the user.
     *
     * @param firstName The new first name for the user.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**
     * Gets the last name of the user.
     *
     * @return The last name of the user.
     */
    public String getLastName() {
        return lastName;
    }
    /**
     * Sets the last name of the user.
     *
     * @param lastName The new last name for the user.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /**
     * Gets the email address of the user.
     *
     * @return The email address of the user.
     */
    public String getEmail() {
        return email;
    }
    /**
     * Sets the email address of the user.
     *
     * @param email The new email address for the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Gets the username of the user.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }
    /**
     * Sets the username of the user.
     *
     * @param username The new username for the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the profile image URL of the user.
     *
     * @return The profile image URL of the user.
     */
    public String getProfileImage() {
        return profileImage;
    }
    /**
     * Sets the profile image URL of the user.
     *
     * @param profileImage The new profile image URL for the user.
     */
    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
