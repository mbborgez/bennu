package org.fenixedu.bennu.portal.servlet;

import java.util.List;

public class BootstrapSection {

    private final String name;
    private final String description;
    private final List<BootstrapField> fields;

    public BootstrapSection(String name, String description, List<BootstrapField> fields) {
        this.name = name;
        this.description = description;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<BootstrapField> getFields() {
        return fields;
    }

}
