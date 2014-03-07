package org.fenixedu.bennu.portal.servlet;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.fenixedu.bennu.portal.bootstrap.PortalBootstrapper;
import org.fenixedu.bennu.portal.bootstrap.PortalBootstrapperRegistry;

@HandlesTypes(PortalBootstrapper.class)
public class PortalContainerInitializer implements ServletContainerInitializer {

    @SuppressWarnings("unchecked")
    @Override
    public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
        if (classes != null) {
            for (Class<?> type : classes) {
                if (PortalBootstrapper.class.isAssignableFrom(type)) {
                    PortalBootstrapperRegistry.registerBootstrapper((Class<PortalBootstrapper>) type);
                }
            }
        }
    }

}
