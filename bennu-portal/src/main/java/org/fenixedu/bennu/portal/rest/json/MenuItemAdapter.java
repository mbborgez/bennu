package org.fenixedu.bennu.portal.rest.json;

import org.fenixedu.bennu.core.annotation.DefaultJsonAdapter;
import org.fenixedu.bennu.core.json.JsonBuilder;
import org.fenixedu.bennu.core.json.JsonViewer;
import org.fenixedu.bennu.portal.domain.MenuContainer;
import org.fenixedu.bennu.portal.domain.MenuItem;
import org.fenixedu.bennu.portal.domain.PortalConfiguration;

import pt.ist.fenixframework.FenixFramework;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@DefaultJsonAdapter(MenuItem.class)
public class MenuItemAdapter implements JsonViewer<MenuItem> {
    /*
        {   "order" : 1, 
            "path": "/xpto", 
            "title" : { "pt" : "xpto" }, 
            "description" : { "pt" : "this is a xpto functionality" }, 
            "menu" : [
                        {
                          "order" : 1,
                          "path" : "/xpto/1",
                          "title" : { "pt" : "xpto sub 1" },
                          "description" : { "pt" : "this is a xpto sub 1 functionality" }
                        },
                        {
                          "order" : 2,
                          "path" : "/xpto/2",
                          "title" : { "pt" : "xpto sub 2" },
                          "description" : { "pt" : "this is a xpto sub 2 functionality" }
                        }
                    ]
        }
     */

//    @Override
//    public MenuItem update(JsonElement json, MenuItem menuItem, JsonBuilder ctx) {
//        JsonObject jsonObj = json.getAsJsonObject();
//        setMenuInfo(jsonObj, menuItem);
//
//        if (jsonObj.has("menu")) {
//            final JsonArray menu = jsonObj.get("menu").getAsJsonArray();
//            for (JsonElement menuEl : menu) {
//                final MenuItem childMenuItem = getMenuItem(menuEl);
//                childMenuItem.setParent(menuItem);
//                update(menuEl, childMenuItem, ctx);
//            }
//        }
//
//        return menuItem;
//    }

    private MenuItem getMenuItem(JsonElement menuJson) {
        return FenixFramework.getDomainObject(menuJson.getAsJsonObject().get("id").getAsString());
    }

    @Override
    public JsonElement view(MenuItem obj, JsonBuilder ctx) {
        JsonObject json = new JsonObject();
        json.addProperty("id", obj.getExternalId());
        json.addProperty("order", obj.getOrd());
        json.addProperty("path", obj.getPath());
        json.addProperty("accessExpression", obj.getAccessGroup().expression());
        json.add("description", ctx.view(obj.getDescription()));
        json.add("title", ctx.view(obj.getTitle()));

        if (obj instanceof MenuContainer) {
            MenuContainer container = (MenuContainer) obj;
            if (container.isRoot()) {
                json.add("title", ctx.view(PortalConfiguration.getInstance().getApplicationTitle()));
            }
            json.add("menu", ctx.view(container.getOrderedChild()));
        }
        return json;
    }

//    @Override
//    public MenuItem create(JsonElement json, JsonBuilder ctx) {
//        JsonObject jsonObj = json.getAsJsonObject();
//        MenuItem menuItem = new MenuItem();
//        setMenuInfo(jsonObj, menuItem);
//
//        if (jsonObj.has("menu")) {
//            final JsonArray menu = jsonObj.get("menu").getAsJsonArray();
//            for (JsonElement menuEl : menu) {
//                menuItem.addChild(create(menuEl, ctx));
//            }
//        }
//        return menuItem;
//    }
//
//    public void setMenuInfo(JsonObject jsonObj, MenuItem menuItem) {
//        if (jsonObj.has("title")) {
//            menuItem.setTitle(LocalizedString.fromJson(jsonObj.get("title").getAsJsonObject()));
//        }
//        if (jsonObj.has("path")) {
//            final String path = jsonObj.get("path").getAsString();
//            final FunctionalityInfo functionalityInfo = AppServer.getFunctionalityInfo(path);
//            if (functionalityInfo != null) {
//                menuItem.setAccessExpression(functionalityInfo.getGroup());
//            }
//            menuItem.setPath(path);
//        }
//        if (jsonObj.has("description")) {
//            menuItem.setDescription(LocalizedString.fromJson(jsonObj.get("description").getAsJsonObject()));
//        }
//        if (jsonObj.has("order")) {
//            menuItem.setOrd(jsonObj.get("order").getAsInt());
//        }
//    }

}
