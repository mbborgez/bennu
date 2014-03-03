package org.fenixedu.bennu.portal.servlet;

public class BootstrapError extends Exception {
    private static final long serialVersionUID = 1L;

    private String fieldName;
    private String sectionName;

    public BootstrapError(String sectionName, String fieldName, String errorMessage) {
        super(errorMessage);
        this.sectionName = sectionName;
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }
}
