package org.fenixedu.bennu.portal.bootstrap.beans;

import org.fenixedu.bennu.portal.bootstrap.annotations.Field;
import org.fenixedu.bennu.portal.bootstrap.annotations.FieldType;

public class FieldBean {
    private String name;
    private String[] validValues;
    private String hint;
    private boolean isMandatory;
    private FieldType fieldType;

    public FieldBean(String name, String[] validValues, String hint, boolean isMandatory, FieldType fieldType) {
        this.name = name;
        this.validValues = validValues;
        this.hint = hint;
        this.isMandatory = isMandatory;
        this.fieldType = fieldType;
    }

    public FieldBean(Field field) {
        this(field.name(), field.validValues(), field.hint(), field.isMandatory(), field.fieldType());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getValidValues() {
        return validValues;
    }

    public void setValidValues(String[] validValues) {
        this.validValues = validValues;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

}
