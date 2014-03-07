package org.fenixedu.bennu.portal.bootstrap;

import com.google.gson.GsonBuilder;

public class BootstrapError extends Exception {
    private static final long serialVersionUID = 1L;

    private BootstrapSection section;
    private BootstrapField field;

    public BootstrapError(BootstrapSection section, BootstrapField field, String errorMessage) {
        super(errorMessage);
        this.section = section;
        this.field = field;
    }

    public BootstrapSection getSection() {
        return section;
    }

    public void setSection(BootstrapSection section) {
        this.section = section;
    }

    public BootstrapField getField() {
        return field;
    }

    public void setField(BootstrapField field) {
        this.field = field;
    }

    public String toJson() {
        BootstrapErrorBean bean = new BootstrapErrorBean(getSection(), getField(), getMessage());
        return new GsonBuilder().serializeNulls().create().toJson(bean);
    }

    private class BootstrapErrorBean {
        private BootstrapSection section;
        private BootstrapField field;
        private String message;

        BootstrapErrorBean(BootstrapSection section, BootstrapField field, String message) {
            this.section = section;
            this.field = field;
            this.message = message;
        }
    }

}
