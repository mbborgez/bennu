package org.fenixedu.bennu.core.bootstrap;

import java.util.List;

public class BootstrapException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final List<BootstrapError> errors;

    public BootstrapException(List<BootstrapError> errors) {
        this.errors = errors;
    }

    public List<BootstrapError> getErrors() {
        return errors;
    }
}
