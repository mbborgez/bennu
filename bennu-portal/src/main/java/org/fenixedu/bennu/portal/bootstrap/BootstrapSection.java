package org.fenixedu.bennu.portal.bootstrap;

import java.util.List;

import org.apache.commons.lang.StringUtils;

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

    public BootstrapField getField(String fieldId) {
        for (BootstrapField field : getFields()) {
            if (StringUtils.equals(field.getName(), fieldId)) {
                return field;
            }
        }
        return null;
    }

}
