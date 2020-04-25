package org.menzies.model.pojo;

import javax.persistence.*;


@Entity
@Table(name = "tag_template")
public class TagTemplate {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "field")
    private String field;

    @Column(name = "regex")
    private String regex;

    private TagTemplate() {

    }

    public TagTemplate(String field, String regex) {
        this.field = field;
        this.regex = regex;
    }

    public int getId() {
        return id;
    }

    public String getField() {
        return field;
    }

    public String getRegex() {
        return regex;
    }
}
