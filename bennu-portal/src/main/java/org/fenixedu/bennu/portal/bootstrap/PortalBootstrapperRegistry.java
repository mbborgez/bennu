package org.fenixedu.bennu.portal.bootstrap;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.fenixedu.bennu.portal.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.portal.bootstrap.annotations.Field;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PortalBootstrapperRegistry {

    private static Map<Class<?>, List<Method>> bootstrapperMethods = Maps.newConcurrentMap();
    private static Map<Class<?>, List<Class<?>>> bootstrapperSections = Maps.newConcurrentMap();
    private static Map<Class<?>, List<Method>> sectionFields = Maps.newConcurrentMap();

    /***********************************************************************************
     * 
     * Register Operations
     * 
     **********************************************************************************/
    public static void registerBootstrapMethod(Class<?> bootstrapper, Method bootstrapMethod) {
        if (!bootstrapperMethods.containsKey(bootstrapper)) {
            bootstrapperMethods.put(bootstrapper, new ArrayList<Method>());
        }
        bootstrapperMethods.get(bootstrapper).add(bootstrapMethod);
    }

    public static void registerBootstrapperSection(Class<?> bootstrapper, Class<?> section) {
        System.out.println("registerBootstrapperSection " + bootstrapper + " -> " + section);
        if (!bootstrapperSections.containsKey(bootstrapper)) {
            bootstrapperSections.put(bootstrapper, new ArrayList<Class<?>>());
        }
        bootstrapperSections.get(bootstrapper).add(section);
    }

    public static void registerSectionFields(Class<?> section, Method fieldMethod) {
        if (!sectionFields.containsKey(section)) {
            sectionFields.put(section, new ArrayList<Method>());
        }
        sectionFields.get(section).add(fieldMethod);
    }

    public static List<Class<?>> getBootstrappers() {
        List<Class<?>> bootstrappers = Lists.newArrayList(bootstrapperMethods.keySet());
        Collections.sort(bootstrappers, BOOTSTRAPPERS_COMPARATOR);
        return bootstrappers;
    }

    public static List<Method> getBootstrapMethods() {
        return expand(bootstrapperMethods);
    }

    public static List<Class<?>> getSections() {
        return expand(bootstrapperSections);
    }

    public static List<Method> getFields() {
        List<Method> fields = expand(sectionFields);
        Collections.sort(fields, FIELDS_COMPARATOR);
        return fields;
    }

    public static List<Method> getBootstrapMethods(Class<?> bootstapper) {
        return bootstrapperMethods.get(bootstapper);
    }

    public static List<Class<?>> getSections(Class<?> bootstrapper) {
        return bootstrapperSections.get(bootstrapper);
    }

    public static List<Method> getSectionFields(Class<?> section) {
        List<Method> fields = sectionFields.get(section);
        Collections.sort(fields, FIELDS_COMPARATOR);
        return fields;
    }

    public static Method getField(String fieldName) {
        for (Method bootstrapField : getFields()) {
            String actualFieldName = bootstrapField.getAnnotation(Field.class).name();
            if (StringUtils.equalsIgnoreCase(actualFieldName, fieldName)) {
                return bootstrapField;
            }
        }
        return null;
    }

    private static final <T> List<T> expand(Map<?, List<T>> expandable) {
        List<T> expandedList = Lists.newArrayList();
        for (List<T> values : expandable.values()) {
            expandedList.addAll(values);
        }
        return expandedList;
    }

    /***************************************************************************************
     * 
     * Comparators
     * 
     ***************************************************************************************/

    private static final Comparator<Method> FIELDS_COMPARATOR = new Comparator<Method>() {
        @Override
        public int compare(Method method1, Method method2) {
            return method1.getAnnotation(Field.class).order() - method2.getAnnotation(Field.class).order();
        }
    };

    private static final Comparator<? super Class<?>> BOOTSTRAPPERS_COMPARATOR = new Comparator<Class<?>>() {
        @Override
        public int compare(Class<?> class1, Class<?> class2) {
            Bootstrapper bootstrapper1 = class1.getAnnotation(Bootstrapper.class);
            Bootstrapper bootstrapper2 = class2.getAnnotation(Bootstrapper.class);
            boolean bootstrapper1First = ArrayUtils.contains(bootstrapper2.after(), class1);
            boolean bootstrapper2First = ArrayUtils.contains(bootstrapper1.after(), class2);
            return bootstrapper1First ? 1 : (bootstrapper2First ? -1 : 0);
        }
    };
}
