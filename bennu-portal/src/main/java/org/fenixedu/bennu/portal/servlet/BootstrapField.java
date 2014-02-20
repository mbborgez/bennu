package org.fenixedu.bennu.portal.servlet;

import java.util.HashSet;
import java.util.Set;

public class BootstrapField {
    private final String name;
    private final String hint;
    private final boolean isMandatory;
    private final Set<String> validValues;
    private String value;

    public BootstrapField(String name, String hint, boolean isMandatory, String type, Set<String> validValues) {
        this.name = name;
        this.hint = hint;
        this.isMandatory = isMandatory;
        this.validValues = validValues;
    }

    public BootstrapField(String name, String description, boolean isMandatory) {
        this(name, description, isMandatory, "text", new HashSet<String>();
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

    public Set<String> getValidValues() {
        return validValues;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
