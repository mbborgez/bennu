package org.fenixedu.bennu.portal.bootstrap;

import java.util.ArrayList;
import java.util.List;

public class BootstrapField {
    private final String name;
    private final String hint;
    private final boolean isMandatory;
    private final List<String> validValues;
    private String value;
    private final FieldType type;

    public BootstrapField(String name, String hint, boolean isMandatory, FieldType type, List<String> validValues) {
        this.name = name;
        this.hint = hint;
        this.type = type;
        this.isMandatory = isMandatory;
        this.validValues = validValues;
    }

    public BootstrapField(String name, String description, boolean isMandatory, FieldType type) {
        this(name, description, isMandatory, type, new ArrayList<String>());
    }

    public BootstrapField(String name, String description, boolean isMandatory) {
        this(name, description, isMandatory, FieldType.TEXT, new ArrayList<String>());
    }

    public BootstrapField(String name, String description) {
        this(name, description, true);
    }

    public String getName() {
        return name;
    }

    public String getHint() {
        return hint;
    }

    public String getValue() {
        return value;
    }

    public List<String> getValidValues() {
        return validValues;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public FieldType getType() {
        return type;
    }

    public enum FieldType {
        TEXT("text"), PASSWORD("password"), EMAIL("email"), SELECT_ONE("selectOne");

        private String fieldType;

        private FieldType(String fieldType) {
            this.fieldType = fieldType;
        }

        public String getFieldType() {
            return fieldType;
        }
    }

}
