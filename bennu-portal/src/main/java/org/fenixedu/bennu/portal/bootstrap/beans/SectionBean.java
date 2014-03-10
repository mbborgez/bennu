package org.fenixedu.bennu.portal.bootstrap.beans;

import java.util.ArrayList;
import java.util.List;

import org.fenixedu.bennu.portal.bootstrap.annotations.Section;

public class SectionBean {
    private String name;
    private String description;
    private List<FieldBean> fields;

    public SectionBean(String name, String description, List<FieldBean> fields) {
        this.name = name;
        this.description = description;
        this.fields = fields;
    }

    public SectionBean(String name, String description) {
        this(name, description, new ArrayList<FieldBean>());
    }

    public SectionBean(Section section) {
        this(section.name(), section.description());
    }

    public void addField(FieldBean fieldBean) {
        fields.add(fieldBean);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FieldBean> getFields() {
        return fields;
    }

    public void setFields(List<FieldBean> fields) {
        this.fields = fields;
    }

}
