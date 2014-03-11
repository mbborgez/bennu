package org.fenixedu.bennu.portal.bootstrap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.fenixedu.bennu.portal.bootstrap.annotations.Field;
import org.fenixedu.bennu.portal.bootstrap.annotations.Section;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PortalBootstrapperRegistry {

    private static Map<Class<?>, Iterable<Method>> bootstrapperMethods = Maps.newConcurrentMap();

    public static void registerBootstrapper(Class<?> bootstrapper, Iterable<Method> bootstrapMethods) {
        System.out.println("registerBootstrapper bootstrapperClass: " + bootstrapper + ", methods: " + bootstrapMethods);
        bootstrapperMethods.put(bootstrapper, bootstrapMethods);
    }

    public static Set<Class<?>> getBootstrappers() {
        return bootstrapperMethods.keySet();
    }

    public static Iterable<Method> getBootstrapMethods(Class<?> bootstapper) {
        Iterable<Method> methods = bootstrapperMethods.get(bootstapper);
        return methods == null ? new ArrayList<Method>() : methods;
    }

    public static Set<Method> getBootstrapMethods() {
        Set<Method> result = Sets.newHashSet();
        for (Iterable<Method> methods : bootstrapperMethods.values()) {
            for (Method method : methods) {
                result.add(method);
            }
        }
        return result;
    }

    public static Set<Class<?>> getSections() {
        Set<Class<?>> result = Sets.newHashSet();
        for (Method method : getBootstrapMethods()) {
            result.addAll(Sets.newHashSet(method.getParameterTypes()));
        }
        return result;
    }

    public static Set<Class<?>> getSections(Class<?> bootstrapper) {
        Set<Class<?>> result = Sets.newHashSet();
        for (Method method : getBootstrapMethods(bootstrapper)) {
            result.addAll(Sets.newHashSet(method.getParameterTypes()));
        }
        return result;
    }

    public static Set<Method> getBootstrapFields(Class<?> section) {
        if (!section.isAnnotationPresent(Section.class)) {
            throw new UnsupportedOperationException("Class " + section.getClass() + " has no @Section annotation");
        }

        Set<Method> fields = Sets.newHashSet();
        for (Method method : section.getMethods()) {
            if (method.isAnnotationPresent(Field.class)) {
                fields.add(method);
            }
        }
        return fields;
    }

    public static Set<Method> getBootstrapFields() {
        Set<Method> fields = Sets.newHashSet();
        for (Class<?> section : getSections()) {
            fields.addAll(getBootstrapFields(section));
        }
        return fields;
    }

}
