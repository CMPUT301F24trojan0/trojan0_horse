/**
 * Purpose:
 * This loads profiles from Firebase and displays them in a list. This way admin can click on any
 * profile which opens a dialog and chooses to delete it.
 *
 * Design Rationale:
 * This uses a ProfileAdapter to display the profile list.
 * It also uses RemoveProfileFragment to ask to confirm before deleting a profile.
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
     * @param profileImage The URL of the user's profile image.
     */
    public Profile(String username, String profileImage){
        this.username = username;
        this.profileImage = profileImage;
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

    public String toString(){
        return firstName + " " + lastName + " (" + email + ")";
    }
}
