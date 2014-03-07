package org.fenixedu.bennu.portal.bootstrap;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Central place for {@link PortalBootstrapper} to register themselves.
 * 
 * Upon application startup, the portals that need to be bootstrapped must register a Portal Bootstrapper here.
 * 
 **/
public class PortalBootstrapperRegistry {

    private static Map<String, PortalBootstrapper> portalBootstrappers = Maps.newConcurrentMap();

    /**
     * Registers a new {@link PortalBootstrapper}.
     * 
     * @param type
     *            The type of the PortalBootstrapper to register
     * @throws IllegalArgumentException
     *             If another bootstrapper with the same key is already registered.
     */
    public static void registerBootstrapper(Class<PortalBootstrapper> type) {
        PortalBootstrapper bootstrapper = null;

        try {
            bootstrapper = type.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Impossible to create a new instance for the bootstrapper " + type.getName());
        }

        if (portalBootstrappers.containsKey(bootstrapper.getKey())) {
            throw new IllegalArgumentException("Another PortalBootstrapper with key " + bootstrapper.getKey()
                    + " is already registered");
        }

        portalBootstrappers.put(bootstrapper.getKey(), bootstrapper);
    }

    /**
     * Returns all the {@link PortalBootstrapper} and the respective keys.
     * 
     * @return
     *         all the {@link PortalBootstrapper} and the respective keys.
     */
    public static Map<String, PortalBootstrapper> getPortalBootstrappers() {
        return portalBootstrappers;
    }

    /**
     * Returns the {@link PortalBootstrapper} for the given key.
     * 
     * @param key
     *            The portal key
     * @return
     *         The {@link PortalBootstrapper} for the given key
     * @throws UnsupportedOperationException
     *             If no bootstrapper is found for the given key
     */
    public static PortalBootstrapper getPortalBootstrapper(String key) {
        PortalBootstrapper bootstrapper = portalBootstrappers.get(key);
        if (bootstrapper == null) {
            throw new UnsupportedOperationException("No bootstrapper for key " + key);
        }
        return bootstrapper;
    }
}
