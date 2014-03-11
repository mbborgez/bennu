package org.fenixedu.bennu.portal.bootstrap.beans;

import com.google.gson.JsonObject;

public class BootstrapException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final String fieldName;

    public BootstrapException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public BootstrapException(String message) {
        this(null, message);
    }

    public BootstrapException(Exception e) {
        this(null, e.getMessage());
    }

    public String getFieldName() {
        return fieldName;
    }

    public String toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("fieldName", getFieldName());
        json.addProperty("message", getMessage());
        return json.toString();
    }
}
