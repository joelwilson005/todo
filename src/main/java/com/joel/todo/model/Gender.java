package com.joel.todo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

// @JsonFormat annotation is used to specify the shape of the enum when serialized
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Gender {
    FEMALE,
    MALE;

    // Method to convert a string to a Gender enum
    public static Gender fromString(String value) {

        // Check if the value is not null
        if (value != null) {
            // Loop through all the values in the Gender enum
            for (Gender gender : Gender.values()) {
                // If the name of the enum matches the value, ignoring case, return the enum
                if (gender.name().equalsIgnoreCase(value)) {
                    return gender;
                }
            }
        }
        // If no matching enum is found, throw an exception
        throw new IllegalArgumentException("Invalid Gender: " + value);
    }

    // @JsonValue annotation is used to indicate the method to use when serializing the enum
    @JsonValue
    public String getEnumValue() {
        // Return the name of the enum
        return this.toString();
    }
}