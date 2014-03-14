package org.fenixedu.bennu.portal.bootstrap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.fenixedu.bennu.portal.bootstrap.annotations.Field;
import org.fenixedu.bennu.portal.bootstrap.beans.BootstrapException;
import org.fenixedu.bennu.portal.bootstrap.handlers.SectionInvocationHandler;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.gson.JsonObject;

public class SectionsBootstrapper {

    public static void bootstrapAll(JsonObject json) {
        validateFields(json);
        Class<?>[] sections = PortalBootstrapperRegistry.getSections().toArray(new Class<?>[0]);
        Object superSection = SectionInvocationHandler.newInstance(json, sections);
        for (Method method : PortalBootstrapperRegistry.getBootstrapMethods()) {
            Object[] args = new Object[method.getParameterTypes().length];
            Arrays.fill(args, superSection);
            invokeIt(method, args);
        }
    }

    @Atomic(mode = TxMode.WRITE)
    private static void invokeIt(Method method, Object[] args) {
        try {
            method.invoke(null, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            if (e.getCause() != null && e.getCause() instanceof BootstrapException) {
                throw (BootstrapException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }

    private static void validateFields(JsonObject json) {
        for (Method method : PortalBootstrapperRegistry.getFields()) {
            Field field = method.getAnnotation(Field.class);
            validateMandatoryField(json, field);
            validateValidValue(json, field);
        }
    }

    private static void validateValidValue(JsonObject json, Field field) {
        boolean isMultipleOption = !ArrayUtils.isEmpty(field.validValues());
        boolean isValidValue = ArrayUtils.contains(field.validValues(), json.get(field.name()).getAsString());
        if (isMultipleOption && !isValidValue) {
            throw new BootstrapException(field.name(), "Invalid value for field '" + field.name() + "'");
        }
    }

    private static void validateMandatoryField(JsonObject json, Field field) {
        boolean isEmpty = !json.has(field.name()) || StringUtils.isEmpty(json.get(field.name()).getAsString());
        if (isEmpty && field.isMandatory()) {
            throw new BootstrapException(field.name(), "The field '" + field.name() + "' is mandatory and has no value.");
        }
    }
}
