package org.fenixedu.bennu.portal.domain;

import java.util.ArrayList;
import java.util.List;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.groups.Group;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.Atomic;

/**
 * Base class for items that are presented in an application's menu.
 * 
 * {@code MenuItem}s are either a {@link MenuContainer}, aggregating other items or a {@link MenuFunctionality}, representing a
 * concrete entry in the menu.
 * 
 * Note that a {@code MenuItem} is immutable, meaning that once it is installed in the menu, it is not possible to modify any of
 * its properties. If you desire to do so, {@code delete} the item and create a new one with the new values.
 * 
 * {@code MenuItem}s are {@link Comparable} according to their order in the menu.
 * 
 * @see MenuContainer
 * @see MenuFunctionality
 * 
 */
public abstract class MenuItem extends MenuItem_Base implements Comparable<MenuItem> {

    protected MenuItem() {
        super();
        setOrd(1);
    }

    protected final void init(MenuContainer parent, boolean visible, String accessGroup, LocalizedString title,
            LocalizedString description, String path) {
        setVisible(visible);
        setAccessGroup(Group.parse(accessGroup));
        setDescription(description);
        setTitle(title);
        setPath(path);
        if (parent != null) {
            parent.addChild(this);
        }
        setFullPath(computeFullPath());
    }

    private String computeFullPath() {
        StringBuilder builder = new StringBuilder();
        builder.append("/");
        builder.append(this.getPath());
        MenuContainer current = getParent();
        while (current != null && current.getConfiguration() == null) {
            builder.insert(0, current.getPath());
            builder.insert(0, "/");
            current = current.getParent();
        }
        return builder.toString();
    }

    /**
     * Compares this {@link MenuItem} with another, taking into account the order of both items.
     * 
     * Note that it only makes sense to invoke this method for items at the same level, as there are no ordering guarantees across
     * multiple levels.
     */
    @Override
    public int compareTo(MenuItem o) {
        return getOrd().compareTo(o.getOrd());
    }

    /**
     * Deletes this item, removing it from the menu.
     */
    @Atomic
    public void delete() {
        setParent(null);
        setAccessGroup(null);
        deleteDomainObject();
    }

    /**
     * Determines whether this {@link MenuItem} is available for the given {@link User}, according to this item's access group.
     * 
     * @param user
     *            The user to verify
     * @return
     *         Whether the given user can access this item
     */
    public boolean isAvailable(User user) {
        return getAccessGroup().isMember(user);
    }

    /**
     * Determines whether this {@link MenuItem} is available for the currently logged user.
     * This method is a shorthand for <code>isAvailable(Authenticate.getUser())</code>.
     * 
     * @return
     *         Whether the currently logged user can access this item
     */
    public boolean isAvailableForCurrentUser() {
        return isAvailable(Authenticate.getUser());
    }

    /**
     * Returns whether the Item should be visible when rendering a menu.
     */
    public boolean isVisible() {
        return getVisible();
    }

    @Override
    public MenuContainer getParent() {
        //FIXME: remove when the framework enables read-only slots
        return super.getParent();
    }

    @Override
    public Integer getOrd() {
        //FIXME: remove when the framework enables read-only slots
        return super.getOrd();
    }

    @Override
    public String getPath() {
        //FIXME: remove when the framework enables read-only slots
        return super.getPath();
    }

    @Override
    public String getFullPath() {
        //FIXME: remove when the framework enables read-only slots
        return super.getFullPath();
    }

    @Override
    public LocalizedString getTitle() {
        //FIXME: remove when the framework enables read-only slots
        return super.getTitle();
    }

    @Override
    public LocalizedString getDescription() {
        //FIXME: remove when the framework enables read-only slots
        return super.getDescription();
    }

    @Override
    public Group getAccessGroup() {
        //FIXME: remove when the framework enables read-only slots
        return super.getAccessGroup();
    }

    public boolean isMenuContainer() {
        return this instanceof MenuContainer;
    }

    public boolean isMenuFunctionality() {
        return this instanceof MenuFunctionality;
    }

    public MenuContainer getAsMenuContainer() {
        if (isMenuContainer()) {
            return (MenuContainer) this;
        }
        throw new IllegalStateException("Not a MenuContainer");
    }

    public MenuFunctionality getAsMenuFunctionality() {
        if (isMenuFunctionality()) {
            return (MenuFunctionality) this;
        }
        throw new IllegalStateException("Not a MenuFunctionality");
    }

    public List<MenuItem> getPathFromRoot() {
        List<MenuItem> result = new ArrayList<MenuItem>();

        MenuItem current = this;
        while (current.getParent() != null) {
            result.add(0, current);
            current = current.getParent();
        }
        return result;
    }
}
