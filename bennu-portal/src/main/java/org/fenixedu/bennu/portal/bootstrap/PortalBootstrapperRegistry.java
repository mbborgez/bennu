package org.fenixedu.bennu.portal.bootstrap;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

public class PortalBootstrapperRegistry {

    private static Map<Class<?>, Iterable<Method>> bootstrapperMethods;

    public static void registerBootstrapper(Class<?> bootstrapper, Iterable<Method> bootstrapMethods) {
        bootstrapperMethods.put(bootstrapper, bootstrapMethods);
    }

    public static Set<Class<?>> getBootstrappers() {
        return bootstrapperMethods.keySet();
    }

    public static Iterable<Method> getBootstrapMethods(Class<?> bootstapper) {
        return bootstrapperMethods.get(bootstapper);
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
}
