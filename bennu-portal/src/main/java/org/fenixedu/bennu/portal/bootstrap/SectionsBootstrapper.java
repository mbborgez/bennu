package org.fenixedu.bennu.portal.bootstrap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.fenixedu.bennu.portal.bootstrap.handlers.SectionInvocationHandler;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

public class SectionsBootstrapper {

    public static void bootstrapAllSections(JsonObject json) {

        Class<?>[] sections = (Class<?>[]) Lists.newArrayList(PortalBootstrapperRegistry.getSections()).toArray();

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

}
