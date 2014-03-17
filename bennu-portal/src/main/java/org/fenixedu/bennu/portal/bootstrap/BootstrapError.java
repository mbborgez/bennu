package org.fenixedu.bennu.portal.bootstrap;

public class BootstrapError {

    private final String fieldName;
    private String message;
    private String bundle;

    public BootstrapError(String fieldName, String message, String bundle) {
        this.message = message;
        this.fieldName = fieldName;
        this.bundle = bundle;
    }

    public BootstrapError(String fieldName, String message) {
        this(fieldName, message, null);
    }

    public BootstrapError(String message) {
        this(null, message);
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }
}
