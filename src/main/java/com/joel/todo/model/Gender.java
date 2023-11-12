package com.joel.todo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Gender {
    FEMALE,
    MALE;

    public static Gender fromString(String value) {

        if (value != null) {
            for (Gender gender : Gender.values()) {
                if (gender.name().equalsIgnoreCase(value)) {
                    return gender;
                }
            }
        }
        throw new IllegalArgumentException("Invalid Gender: " + value);
    }

    @JsonValue
    public String getEnumValue() {
        return this.toString();
    }
}