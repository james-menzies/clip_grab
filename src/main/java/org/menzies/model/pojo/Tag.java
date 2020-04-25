package org.menzies.model.pojo;

import javax.persistence.*;

@Entity
@Table(name = "tag")
public class Tag {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "field")
    private String field;

    @Column(name = "value")
    private String value;

    private Tag() {

    }

    public Tag(String field, String value) {
        this.field = field;
        this.value = value;
    }

    public int getId() {
        return id;
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
