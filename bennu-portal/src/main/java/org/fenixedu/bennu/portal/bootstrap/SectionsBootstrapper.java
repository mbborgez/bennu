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

import com.google.common.base.Joiner;
import com.google.gson.JsonObject;

public class SectionsBootstrapper {

    public static void bootstrapAllSections(JsonObject json) {
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
            throw new RuntimeException(e);
        }
    }

    private static void validateFields(JsonObject json) {
        for (Method method : PortalBootstrapperRegistry.getBootstrapFields()) {
            Field field = method.getAnnotation(Field.class);
            validateMandatoryField(json, field);
            validateValidValue(json, field);
        }
    }

    private static void validateValidValue(JsonObject json, Field field) {
        boolean isMultipleOption = !ArrayUtils.isEmpty(field.validValues());
        boolean isValidValue = ArrayUtils.contains(field.validValues(), json.get(field.name()).getAsString());
        if (isMultipleOption && !isValidValue) {
            String validValues = Joiner.on(", ").join(field.validValues());
            String errorMessage =
                    String.format("Invalid value for field '%s', please enter a valid value ( %s )", field.name(), validValues);
            throw new BootstrapException(field.name(), errorMessage);
        }
    }

    private static void validateMandatoryField(JsonObject json, Field field) {
        boolean isEmpty = !json.has(field.name()) || StringUtils.isEmpty(json.get(field.name()).getAsString());
        if (isEmpty && field.isMandatory()) {
            throw new BootstrapException(field.name(), "The field '" + field.name() + "' is mandatory and has no value.");
        }
    }
}
