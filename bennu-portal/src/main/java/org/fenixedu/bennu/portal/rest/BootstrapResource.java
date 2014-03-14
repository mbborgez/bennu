package org.fenixedu.bennu.portal.rest;

import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.portal.bootstrap.PortalBootstrapperRegistry;
import org.fenixedu.bennu.portal.bootstrap.SectionsBootstrapper;
import org.fenixedu.bennu.portal.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.portal.bootstrap.annotations.Field;
import org.fenixedu.bennu.portal.bootstrap.annotations.Section;
import org.fenixedu.bennu.portal.bootstrap.beans.BootstrapException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@Path("/bennu-portal/bootstrap")
public class BootstrapResource extends BennuRestResource {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(final String json) {
        try {
            JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
            System.out.println(jsonObject);
            SectionsBootstrapper.bootstrapAll(jsonObject);
            return Response.status(200).build();
        } catch (BootstrapException e) {
            e.printStackTrace();
            return Response.status(500).entity(e.toJson()).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(new BootstrapException("Internal Error: " + e.getMessage()).toJson()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getData() {
        try {
            JsonObject json = new JsonObject();
            json.add("bootstrappers", getBootstrappers());
            json.add("availableLocales", getLocales());
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private JsonArray getLocales() {
        JsonArray json = new JsonArray();
        for (Locale locale : CoreConfiguration.supportedLocales()) {
            json.add(createLocale(locale));
        }
        return json;
    }

    private JsonObject createLocale(Locale locale) {
        JsonObject localeJson = new JsonObject();
        localeJson.addProperty("name", locale.getDisplayName());
        String key = String.format("%s-%s", locale.getLanguage(), locale.getCountry());
        localeJson.addProperty("key", key);
        return localeJson;
    }

    private JsonArray getBootstrappers() {
        JsonArray bootstrappersJson = new JsonArray();
        for (Class<?> bootstrapperClass : PortalBootstrapperRegistry.getBootstrappers()) {
            bootstrappersJson.add(createBootstrapper(bootstrapperClass, bootstrapperClass.getAnnotation(Bootstrapper.class)));
        }
        return bootstrappersJson;
    }

    private JsonElement createBootstrapper(Class<?> bootstrapperClass, Bootstrapper bootstrapper) {
        List<Class<?>> bootstrapperSections = PortalBootstrapperRegistry.getSections(bootstrapperClass);
        JsonObject json = new JsonObject();
        JsonArray sections = new JsonArray();
        getLocalizedString(bootstrapper.bundle(), bootstrapper.name());
        json.add("name", getLocalizedString(bootstrapper.bundle(), bootstrapper.name()).json());
        json.add("description", getLocalizedString(bootstrapper.bundle(), bootstrapper.description()).json());
        if (bootstrapperSections != null) {
            for (Class<?> sectionClass : bootstrapperSections) {
                sections.add(createSection(sectionClass.getAnnotation(Section.class), sectionClass, bootstrapper.bundle()));
            }
        }
        json.add("sections", sections);
        return json;
    }

    private JsonElement createSection(Section section, Class<?> sectionClass, String bundle) {
        JsonObject json = new JsonObject();
        JsonArray fields = new JsonArray();
        json.add("name", getLocalizedString(bundle, section.name()).json());
        json.add("description", getLocalizedString(bundle, section.description()).json());
        for (Method method : PortalBootstrapperRegistry.getSectionFields(sectionClass)) {
            fields.add(createField(method.getAnnotation(Field.class), bundle));
        }
        json.add("fields", fields);
        return json;
    }

    private JsonElement createField(Field field, String bundle) {
        JsonObject json = new JsonObject();
        JsonArray validValues = new JsonArray();
        json.add("name", getLocalizedString(bundle, field.name()).json());
        json.add("hint", getLocalizedString(bundle, field.hint()).json());
        json.addProperty("isMandatory", field.isMandatory());
        json.addProperty("fieldType", field.fieldType().name());
        for (String validValue : field.validValues()) {
            validValues.add(new JsonPrimitive(validValue));
        }
        json.add("validValues", validValues);
        return json;
    }

}
