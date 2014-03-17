package org.fenixedu.bennu.portal.bootstrap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

public class SectionsBootstrapper {

    public static List<BootstrapError> bootstrapAll(JsonObject json) {
        Class<?>[] sections = PortalBootstrapperRegistry.getSections().toArray(new Class<?>[0]);
        Object superSection = SectionInvocationHandler.newInstance(json, sections);
        List<BootstrapError> errors = Lists.newArrayList();
        for (Method method : PortalBootstrapperRegistry.getBootstrapMethods()) {
            Object[] args = new Object[method.getParameterTypes().length];
            Arrays.fill(args, superSection);
            errors.addAll(invokeIt(method, args));
        }
        return errors;
    }

    @Atomic(mode = TxMode.WRITE)
    private static List<BootstrapError> invokeIt(Method method, Object[] args) {
        try {
            List<BootstrapError> errors = Lists.newArrayList();
            Object result = method.invoke(null, args);

            System.out.println("invokeIt - result: " + result);
            if (result != null) {
                System.out.println("invokeIt - result.class: " + result.getClass());
                if (List.class.isAssignableFrom(result.getClass())) {
                    System.out.println("invokeIt - result is assignable");
                    for (Object item : (List<Object>) result) {
                        System.out.println("invokeIt - item: " + item + ", class: " + item.getClass());
                        if (BootstrapError.class.isAssignableFrom(item.getClass())) {
                            System.out.println("invokeIt - item is assignable");
                            errors.add((BootstrapError) item);
                        }
                    }
                } else if (BootstrapError.class.isAssignableFrom(result.getClass())) {
                    errors.add((BootstrapError) result);
                }
            }

            return errors;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
