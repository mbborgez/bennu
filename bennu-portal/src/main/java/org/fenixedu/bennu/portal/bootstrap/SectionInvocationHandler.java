package org.fenixedu.bennu.portal.bootstrap;

import static org.fenixedu.bennu.core.i18n.BundleUtil.getString;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Locale;

import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.bootstrap.annotations.Field;
import org.fenixedu.bennu.portal.bootstrap.annotations.Section;

import com.google.common.collect.Iterables;
import com.google.common.reflect.AbstractInvocationHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SectionInvocationHandler extends AbstractInvocationHandler {

    public static final Locale LOCALE = Iterables.getFirst(CoreConfiguration.supportedLocales(), null);
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
        Section section = method.getDeclaringClass().getAnnotation(Section.class);
        Field field = method.getAnnotation(Field.class);

        if (field == null) {
            throw new UnsupportedOperationException(
                    "The class must have a @Section annotation and the field must have a @Field annotation.");
        }
        String fieldName = getString(section.bundle(), LOCALE, field.name());
        JsonElement fieldElement = json.get(fieldName);

        return fieldElement != null ? fieldElement.getAsString() : new String();
    }
}
