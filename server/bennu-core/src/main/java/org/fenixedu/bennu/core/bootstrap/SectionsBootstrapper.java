package org.fenixedu.bennu.core.bootstrap;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrap;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

public class SectionsBootstrapper {

    /**
     * Invokes all the declared methods with a {@link Bootstrap} annotation.
     * 
     * @param json
     *            the json that for each declared {@link Field} maps its key on its value.
     * 
     * @return all the errors that occurred on the bootstrap process
     */
    @Atomic(mode = TxMode.WRITE)
    public static void bootstrapAll(JsonObject json) throws Exception {
        Class<?>[] sections = BootstrapperRegistry.getSections().toArray(new Class<?>[0]);
        Object superSection = SectionInvocationHandler.newInstance(json, sections);
        for (Method method : BootstrapperRegistry.getBootstrapMethods()) {
            Object[] args = new Object[method.getParameterTypes().length];
            Arrays.fill(args, superSection);
            List<BootstrapError> errors = invokeIt(method, args);
            if (!errors.isEmpty()) {
                throw new BootstrapException(errors);
            }
        }
    }

    private static List<BootstrapError> invokeIt(Method method, Object[] args) throws Exception {
        List<BootstrapError> errors = Lists.newArrayList();
        Object result = method.invoke(null, args);
        if (result != null) {
            if (List.class.isAssignableFrom(result.getClass())) {
                for (Object item : (List<Object>) result) {
                    if (BootstrapError.class.isAssignableFrom(item.getClass())) {
                        errors.add((BootstrapError) item);
                    }
                }
            } else if (BootstrapError.class.isAssignableFrom(result.getClass())) {
                errors.add((BootstrapError) result);
            }
        }

        return errors;
    }

}
