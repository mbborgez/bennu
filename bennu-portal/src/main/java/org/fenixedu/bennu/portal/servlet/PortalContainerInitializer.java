package org.fenixedu.bennu.portal.servlet;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.apache.commons.lang.ArrayUtils;
import org.fenixedu.bennu.portal.bootstrap.PortalBootstrapperRegistry;
import org.fenixedu.bennu.portal.bootstrap.annotations.Bootstrap;
import org.fenixedu.bennu.portal.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.portal.bootstrap.annotations.Field;
import org.fenixedu.bennu.portal.bootstrap.annotations.Section;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@HandlesTypes(Bootstrapper.class)
public class PortalContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
        if (classes != null) {
            for (Class<?> bootstrapper : classes) {
                if (bootstrapper.isAnnotationPresent(Bootstrapper.class)) {
                    System.out.println("onStartup: " + bootstrapper);
                    ArrayList<Method> allMethods = Lists.newArrayList(bootstrapper.getMethods());
                    Iterable<Method> bootstrapMethods = Iterables.filter(allMethods, isBootstrapMethod);
                    for (Method bootstrapMethod : bootstrapMethods) {
                        System.out.println("onStartup: " + bootstrapMethod);
                        validateStaticMethod(bootstrapMethod);
                        bootstrapMethod.setAccessible(true);
                        validateDeclaredSection(bootstrapper, bootstrapMethod.getParameterTypes());
                        for (Class<?> bootstrapSection : bootstrapMethod.getParameterTypes()) {
                            if (!PortalBootstrapperRegistry.getSections().contains(bootstrapSection)) {
                                System.out.println("onStartup: " + bootstrapSection);
                                validateSection(bootstrapSection);
                                PortalBootstrapperRegistry.registerBootstrapperSection(bootstrapper, bootstrapSection);
                                for (Method fieldMethod : bootstrapSection.getMethods()) {
                                    validateSectionField(fieldMethod);
                                    PortalBootstrapperRegistry.registerSectionFields(bootstrapSection, fieldMethod);
                                }
                            }
                        }
                        PortalBootstrapperRegistry.registerBootstrapMethod(bootstrapper, bootstrapMethod);
                    }
                }
            }
        }
    }

    private static Predicate<Method> isBootstrapMethod = new Predicate<Method>() {
        @Override
        public boolean apply(Method method) {
            return method.isAnnotationPresent(Bootstrap.class);
        }
    };

    private void validateDeclaredSection(Class<?> bootstrapper, Class<?>[] parameterTypes) {
        Class<?>[] declaredSections = bootstrapper.getAnnotation(Bootstrapper.class).sections();
        for (Class<?> bootstrapParamerType : parameterTypes) {
            if (!ArrayUtils.contains(declaredSections, bootstrapParamerType)) {
                throw new UnsupportedOperationException("The parameter '" + bootstrapParamerType.getName()
                        + "' must be declared as a section on the @Bootstrapper at " + bootstrapper.getName());
            }
        }
    }

    private static void validateStaticMethod(Method bootstrapMethod) {
        if (!Modifier.isStatic(bootstrapMethod.getModifiers())) {
            throw new UnsupportedOperationException("Bootstrap method '" + bootstrapMethod.getName() + "' must be static.");
        }
    }

    private static void validateSection(Class<?> bootstrapSection) {
        if (!bootstrapSection.isAnnotationPresent(Section.class)) {
            throw new UnsupportedOperationException(
                    "All the parameters of the Bootstrap method must have a @Section annotation but field '"
                            + bootstrapSection.getName() + "' doesn't have it.");
        }
    }

    private static void validateSectionField(Method fieldMethod) {
        if (fieldMethod.isAnnotationPresent(Field.class)) {
            String fieldName = fieldMethod.getAnnotation(Field.class).name();
            if (PortalBootstrapperRegistry.getField(fieldName) != null) {
                throw new UnsupportedOperationException("Field name must be unique and the field '" + fieldName
                        + "' already exists.");
            }
        }
    }

}
