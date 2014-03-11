package org.fenixedu.bennu.portal.servlet;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.fenixedu.bennu.portal.bootstrap.PortalBootstrapperRegistry;
import org.fenixedu.bennu.portal.bootstrap.annotations.Bootstrap;
import org.fenixedu.bennu.portal.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.portal.bootstrap.annotations.Section;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@HandlesTypes(Bootstrapper.class)
public class PortalContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
        if (classes != null) {
            for (Class<?> type : classes) {
                if (type.isAnnotationPresent(Bootstrapper.class)) {
                    ArrayList<Method> allMethods = Lists.newArrayList(type.getMethods());
                    Iterable<Method> bootstrapMethods = Iterables.filter(allMethods, isBootstrapMethod);
                    for (Method method : bootstrapMethods) {
                        validateStaticMethod(method);
                        validateBootstrapSections(method.getParameterTypes());
                        method.setAccessible(true);
                    }
                    PortalBootstrapperRegistry.registerBootstrapper(type, bootstrapMethods);
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

    private static void validateStaticMethod(Method bootstrapMethod) {
        if (!Modifier.isStatic(bootstrapMethod.getModifiers())) {
            throw new UnsupportedOperationException("Bootstrap method '" + bootstrapMethod.getName() + "' must be static.");
        }
    }

    private static void validateBootstrapSections(Class<?>[] bootstrapSections) {
        for (Class<?> bootstrapField : bootstrapSections) {
            if (!bootstrapField.isAnnotationPresent(Section.class)) {
                throw new UnsupportedOperationException(
                        "All the parameters of the Bootstrap method must have a @Section annotation but field '"
                                + bootstrapField.getName() + "' doesn't have it.");
            }
        }
    }

}
