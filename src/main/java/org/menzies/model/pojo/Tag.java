package org.menzies.model.pojo;

import javax.persistence.*;
import java.io.Serializable;


public class Tag implements Serializable {

    private String field;
    private String value;

    public Tag(String field, String value) {
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("TagField: %s, " +
                "Value: %s%n", field, value);
    }
}
