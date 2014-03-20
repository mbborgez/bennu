/*
 * BennuCoreAnnotationInitializer.java
 * 
 * Copyright (c) 2013, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of bennu-core.
 * 
 * bennu-core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * bennu-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with bennu-core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.core.annotation;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.fenixedu.bennu.core.bootstrap.BootstrapperRegistry;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.core.domain.groups.CustomGroup;
import org.fenixedu.bennu.core.rest.JsonAwareResource;

@HandlesTypes({ CustomGroupOperator.class, Path.class, Provider.class, DefaultJsonAdapter.class, Bootstrapper.class })
public class BennuCoreAnnotationInitializer implements ServletContainerInitializer {
    @Override
    @SuppressWarnings("unchecked")
    public void onStartup(Set<Class<?>> classes, ServletContext ctx) throws ServletException {
        if (classes != null) {
            for (Class<?> type : classes) {
                CustomGroupOperator operator = type.getAnnotation(CustomGroupOperator.class);
                if (operator != null) {
                    CustomGroup.registerOperator((Class<? extends CustomGroup>) type);
                }
                DefaultJsonAdapter defaultJsonAdapter = type.getAnnotation(DefaultJsonAdapter.class);
                if (defaultJsonAdapter != null) {
                    JsonAwareResource.setDefault(defaultJsonAdapter.value(), type);
                }
                if (type.isAnnotationPresent(Bootstrapper.class)) {
                    BootstrapperRegistry.register(type);
                }
            }
        }
    }
}
