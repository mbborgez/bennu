package org.fenixedu.bennu.core.bootstrap.annotations;

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
