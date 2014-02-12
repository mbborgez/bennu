package org.fenixedu.bennu.portal.domain;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.portal.model.Application;
import org.fenixedu.bennu.portal.model.Functionality;
import org.fenixedu.commons.i18n.LocalizedString;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;

/**
 * A {@link MenuContainer} represents an inner node in the functionality tree. It may hold {@link MenuFunctionality}s or other
 * containers, forming a tree-like structure.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 * 
 */
public final class MenuContainer extends MenuContainer_Base {

    /**
     * Used to create the {@link MenuContainer} that will represent the root of the
     * functionality tree.
     * 
     * @param configuration
     *            The {@link PortalConfiguration} in which the functionality tree will be installed.
     */
    MenuContainer(PortalConfiguration configuration) {
        super();
        if (configuration.getMenu() != null && getConfiguration().getMenu() != this) {
            throw new RuntimeException("There can be only one root menu!");
        }
        setConfiguration(configuration);
        init(null, false, "anyone", new LocalizedString(), new LocalizedString(), "ROOT_PATH");
    }

    /**
     * Creates a new {@link MenuContainer} based on the given {@link Application}, and inserts it under the given parent.
     * 
     * @throws IllegalArgumentException
     *             If {@code parent} is null.
     */
    public MenuContainer(MenuContainer parent, Application application) {
        super();
        if (parent == null) {
            throw new IllegalArgumentException("MenuContainer requires a parent Container!");
        }
        init(parent, true, application.getAccessGroup(), application.getTitle(), application.getDescription(),
                application.getPath());
        for (Functionality functionality : application.getFunctionalities()) {
            new MenuFunctionality(this, functionality);
        }
    }

    /**
     * Creates a new {@link MenuContainer} based on the provider parameters, and inserts it under the given parent.
     * 
     * @throws IllegalArgumentException
     *             If {@code parent} is null.
     */
    public MenuContainer(MenuContainer parent, boolean visible, String accessGroup, LocalizedString description,
            LocalizedString title, String path) {
        super();
        if (parent == null) {
            throw new IllegalArgumentException("MenuFunctionality cannot be created without a parent!");
        }
        init(parent, visible, accessGroup, title, description, path);
    }

    /**
     * Adds a given {@link MenuItem} as the last child of this container.
     * 
     * @param child
     *            The child element to add to this container
     * 
     * @throws BennuPortalDomainException
     *             If another child with the same path already exists
     */
    @Override
    public void addChild(MenuItem child) throws BennuPortalDomainException {
        addChild(child, getNextOrder());
    }

    private Integer getNextOrder() {
        return getChildSet().size() + 1;
    }

    /**
     * Adds a given {@link MenuItem} as a child of this container, in the given position.
     * 
     * @param child
     *            The child element to add to this container
     * @param order
     *            The position this item will be inserted at
     * 
     * @throws BennuPortalDomainException
     *             If another child with the same path already exists
     */
    public void addChild(MenuItem child, Integer order) throws BennuPortalDomainException {
        child.setOrd(order);
        for (MenuItem existing : getChildSet()) {
            if (existing.getPath().equals(child.getPath())) {
                throw BennuPortalDomainException.childWithPathAlreadyExists(child.getPath());
            }
        }
        super.addChild(child);
    }

    /**
     * Returns the children of this container, sorted by their order
     * 
     * @return
     *         The ordered children of this container
     */
    public Set<MenuItem> getOrderedChild() {
        return Collections.unmodifiableSet(new TreeSet<>(getChildSet()));
    }

    public Set<MenuItem> getUserMenu() {
        return FluentIterable.from(getChildSet()).filter(new Predicate<MenuItem>() {
            @Override
            public boolean apply(MenuItem menu) {
                return menu.isAvailable(Authenticate.getUser());
            }
        }).toSortedSet(Ordering.natural());
    }

    /**
     * Deletes this container, as well as all its children.
     */
    @Override
    public void delete() {
        if (getConfiguration() != null) {
            throw BennuPortalDomainException.cannotDeleteRootContainer();
        }

        for (MenuItem child : getChildSet()) {
            child.delete();
        }
        super.delete();
    }

    /**
     * Returns whether this container is the root of the functionality tree.
     */
    public boolean isRoot() {
        return getConfiguration() != null;
    }

    /**
     * Traverses the functionality tree looking for the branch that matches the given path.
     * Lookup starts with the children of the current Container.
     * 
     * Note that this method is aware of the availability of tree branches, meaning that if
     * the current user doesn't have access to the selected functionality, this method will return null.
     * 
     * @param parts
     *            The path to be looked up
     * @return
     *         The {@link MenuFunctionality} matching the given path, or null if no functionality matches or the user has no
     *         access.
     */
    public final MenuFunctionality findFunctionalityWithPath(String[] parts) {
        return findFunctionalityWithPath(parts, 0);
    }

    /**
     * Helper method that receives the array index to look up, thus avoiding creation of helper objects (such as lists).
     * 
     * The functionality of this method is as follows:
     * 
     * <ol>
     * <li>
     * If there are no more elements in the path, find the initial content ({@link #findInitialContent()}) of this
     * {@link MenuContainer}</li>
     * 
     * <li>
     * For each child of this container:
     * <ol>
     * <li>It checks if the child is available</li>
     * <li>It checks if the child matches the given part of the path</li>
     * <li>If both of the above are true:
     * 
     * <ul>
     * <li>If the selected item is a functionality, return it. Note that it is possible that this element is not the last in the
     * path, allowing custom processing.</li>
     * <li>
     * If the selected item if a container, continue searching, looking for the next element in the path</li>
     * </ul>
     * </li>
     * </ol>
     * </li>
     * <li>
     * If no item was found, return {@code null}.</li>
     * </ol>
     */
    private final MenuFunctionality findFunctionalityWithPath(String[] parts, int startIndex) {
        // 1)
        if (parts.length == startIndex) {
            return findInitialContent();
        }

        // 2)
        for (MenuItem child : getChildSet()) {
            if (child.isAvailableForCurrentUser() && child.getPath().equals(parts[startIndex])) {
                if (child.isMenuFunctionality()) {
                    return child.getAsMenuFunctionality();
                } else {
                    return child.getAsMenuContainer().findFunctionalityWithPath(parts, startIndex + 1);
                }
            }
        }

        // 3)
        return null;
    }

    /**
     * Returns the initial content of this container, i.e., the first {@link MenuFunctionality}.
     * 
     * @return
     */
    public MenuFunctionality findInitialContent() {
        for (MenuItem item : getOrderedChild()) {
            if (item.isAvailableForCurrentUser()) {
                if (item.isMenuFunctionality()) {
                    return item.getAsMenuFunctionality();
                } else {
                    MenuFunctionality functionality = item.getAsMenuContainer().findInitialContent();
                    if (functionality != null) {
                        return functionality;
                    }
                }
            }
        }
        return null;
    }
}
