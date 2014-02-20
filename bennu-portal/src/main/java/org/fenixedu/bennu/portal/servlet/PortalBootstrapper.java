package org.fenixedu.bennu.portal.servlet;

import java.util.List;

/**
 * Portal bootstrapper represents the application specific information needed to bootstrap the application
 * 
 * 
 */
public interface PortalBootstrapper {

    /**
     * Returns all the {@link BoostrapSection} needed to bootstrap the application.
     */
    public List<BootstrapSection> getBootstrapSections();

    /**
     * This method will be called with all the {@link BoostrapSection} with required values
     */
    public String boostrap(List<BootstrapSection> sections);

}
