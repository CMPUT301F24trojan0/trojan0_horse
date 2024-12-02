
package com.example.trojan0project;

import java.io.Serializable;
/**
 * Purpose:
 * Represents a facility with a name
 *
 * Design Rationale:
 * Implements `Serializable` so instances can be passed between activities or fragments
 * A constructor initializes the facility's name, and a getter method allows access to it
 *
 * Outstanding Issues:
 * No issues
 */

public class Facility implements Serializable {
    private String facilityName;

    /**
     * Constructs a `Facility` instance with the specified name.
     *
     * @param eventName The name of the facility.
     */
    public Facility(String eventName){
        this.facilityName = eventName;
    }

    /**
     * Retrieves the facility name.
     *
     * @return The name of the facility.
     */
    public String getFacilityName(){
        return facilityName;
    }


}
