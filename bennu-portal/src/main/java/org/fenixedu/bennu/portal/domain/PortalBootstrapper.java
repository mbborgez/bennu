package org.fenixedu.bennu.portal.domain;

import java.util.List;

import org.fenixedu.bennu.core.bootstrap.AdminUserBootstrapper;
import org.fenixedu.bennu.core.bootstrap.BootstrapError;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrap;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.core.bootstrap.annotations.Field;
import org.fenixedu.bennu.core.bootstrap.annotations.Section;
import org.fenixedu.bennu.portal.domain.PortalBootstrapper.PortalSection;
import org.fenixedu.bennu.portal.model.Application;
import org.fenixedu.bennu.portal.model.ApplicationRegistry;
import org.fenixedu.commons.i18n.I18N;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.common.collect.Lists;

@Bootstrapper(bundle = "resources.BennuPortalResources", name = "bootstrapper.name", sections = { PortalSection.class },
        after = AdminUserBootstrapper.class)
public class PortalBootstrapper {

    @Bootstrap
    public static List<BootstrapError> bootstrapPortal(PortalSection portalSection) {
        PortalConfiguration portalConfiguration = PortalConfiguration.getInstance();
        Application app = ApplicationRegistry.getAppByKey("bennu-admin");
        new MenuContainer(portalConfiguration.getMenu(), app);
        portalConfiguration.setApplicationCopyright(new LocalizedString(I18N.getLocale(), portalSection.getOrganizationName()));
        portalConfiguration.setApplicationTitle(new LocalizedString(I18N.getLocale(), portalSection.getApplicationTitle()));
        portalConfiguration.setApplicationSubTitle(new LocalizedString(I18N.getLocale(), portalSection.getApplicationTitle()));
        return Lists.newArrayList();
    }

    @Section(name = "bootstrapper.portalSection.name", description = "bootstrapper.portalSection.description",
            bundle = "resources.BennuPortalResources")
    public static interface PortalSection {

        @Field(name = "bootstrapper.portalSection.installationName", hint = "bootstrapper.portalSection.installationName.hint",
                order = 4)
        public String getApplicationTitle();

        @Field(name = "bootstrapper.portalSection.organizationName", hint = "bootstrapper.portalSection.organizationName.hint",
                order = 3)
        public String getOrganizationName();

    }
}
