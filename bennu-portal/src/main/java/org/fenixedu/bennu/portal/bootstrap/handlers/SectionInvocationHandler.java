package org.fenixedu.bennu.portal.bootstrap.handlers;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.fenixedu.bennu.portal.bootstrap.annotations.Field;

import com.google.common.reflect.AbstractInvocationHandler;
import com.google.gson.JsonObject;

public class SectionInvocationHandler extends AbstractInvocationHandler {

    private final JsonObject json;

    public SectionInvocationHandler(JsonObject json) {
        this.json = json;
    }

    public static Object newInstance(JsonObject json, Class<?>... interfaces) {
        ClassLoader classLoader = SectionInvocationHandler.class.getClassLoader();
        SectionInvocationHandler invocationHandler = new SectionInvocationHandler(json);

        return Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
    }

    @Override
    protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
        Field field = method.getAnnotation(Field.class);
        if (field == null) {
            throw new UnsupportedOperationException(
                    "The class must have a @Section annotation and the field must have a @Field annotation");
        }
        return json.get(field.name()).getAsString();
    }

}
