package org.fenixedu.bennu.core.bootstrap;

import java.lang.reflect.Method;

public class BootstrapError {

    private final String message;
    private final String bundle;
    private final Method fieldMethod;

    public BootstrapError(Method fieldMethod, String message, String bundle) {
        this.fieldMethod = fieldMethod;
        this.message = message;
        this.bundle = bundle;
    }

    public BootstrapError(Class<?> sectionClass, String fieldMethodName, String message, String bundle) {
        this(getMethod(sectionClass, fieldMethodName), message, bundle);
    }

    public String getMessage() {
        return message;
    }

    public String getBundle() {
        return bundle;
    }

    public Method getFieldMethod() {
        return fieldMethod;
    }

    private static Method getMethod(Class<?> clazz, String methodName) {
        try {
            return clazz.getMethod(methodName, null);
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFieldKey() {
        return String.format("%s.%s", getFieldMethod().getDeclaringClass().getName(), getFieldMethod().getName());
    }
}
