package org.fenixedu.bennu.portal.bootstrap;

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
    public void boostrap(List<BootstrapSection> sections) throws BootstrapError;

    /**
     * Returns the name of the Portal Bootstrapper.
     */
    public String getName();

    /**
     * Returns a unique key for the portal
     */
    public String getKey();

}
