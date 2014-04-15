package org.fenixedu.bennu.core.rest;

import static org.fenixedu.bennu.core.i18n.BundleUtil.getLocalizedString;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.bootstrap.BootstrapError;
import org.fenixedu.bennu.core.bootstrap.BootstrapException;
import org.fenixedu.bennu.core.bootstrap.BootstrapperRegistry;
import org.fenixedu.bennu.core.bootstrap.SectionsBootstrapper;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.core.bootstrap.annotations.Field;
import org.fenixedu.bennu.core.bootstrap.annotations.Section;
import org.fenixedu.bennu.core.util.CoreConfiguration;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

@Path("/bennu-core/bootstrap")
public class BootstrapResource extends BennuRestResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(final String json) throws Exception {
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        try {
            SectionsBootstrapper.bootstrapAll(jsonObject);
            return Response.status(200).build();
        } catch(BootstrapException e) {
            return Response.status(500).entity(createErrors(e.getErrors()).toString()).build();
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

    private JsonArray createErrors(List<BootstrapError> errors) {
        JsonArray json = new JsonArray();
        for (BootstrapError error : errors) {
            JsonObject errorJson = new JsonObject();
            errorJson.add("fieldKey", new JsonPrimitive(error.getFieldKey()));
            errorJson.add("message", getLocalizedString(error.getBundle(), error.getMessage()).json());
            json.add(errorJson);
        }
        return json;
    }

    private JsonArray getLocales() {
        JsonArray json = new JsonArray();
        for (Locale locale : CoreConfiguration.supportedLocales()) {
            JsonObject localeJson = new JsonObject();
            localeJson.addProperty("name", locale.getDisplayName(Locale.ENGLISH));
            String key = String.format("%s-%s", locale.getLanguage(), locale.getCountry());
            localeJson.addProperty("key", key);
            json.add(localeJson);
        }
        return json;
    }

    private JsonArray getBootstrappers() {
        Set<Class<?>> registeredSections = Sets.newHashSet();
        JsonArray bootstrappersJson = new JsonArray();
        for (Class<?> bootstrapperClass : BootstrapperRegistry.getBootstrappers()) {
            List<Class<?>> bootstrapperSections = BootstrapperRegistry.getSections(bootstrapperClass);
            Bootstrapper bootstrapper = bootstrapperClass.getAnnotation(Bootstrapper.class);
            JsonObject bootstrapperJson = new JsonObject();
            JsonArray sectionsJson = new JsonArray();

            if (bootstrapperSections != null) {
                for (Class<?> sectionClass : bootstrapperSections) {
                    if (!registeredSections.contains(sectionClass)) {
                        sectionsJson.add(createSection(sectionClass.getAnnotation(Section.class), sectionClass));
                        registeredSections.add(sectionClass);
                    }
                }
            }

            bootstrapperJson.add("name", getLocalizedString(bootstrapper.bundle(), bootstrapper.name()).json());
            bootstrapperJson.add("description", getLocalizedString(bootstrapper.bundle(), bootstrapper.description()).json());
            bootstrapperJson.add("sections", sectionsJson);
            bootstrappersJson.add(bootstrapperJson);
        }
        return bootstrappersJson;
    }

    private JsonElement createSection(Section section, Class<?> sectionClass) {
        JsonObject json = new JsonObject();
        JsonArray fields = new JsonArray();
        for (Method method : BootstrapperRegistry.getSectionFields(sectionClass)) {
            fields.add(createField(method.getAnnotation(Field.class), method, section.bundle()));
        }
        json.add("fields", fields);
        json.addProperty("key", sectionClass.getName());
        json.add("name", getLocalizedString(section.bundle(), section.name()).json());
        json.add("description", getLocalizedString(section.bundle(), section.description()).json());
        return json;
    }

    private JsonElement createField(Field field, Method fieldMethod, String bundle) {
        JsonObject json = new JsonObject();
        JsonArray validValues = new JsonArray();
        for (String validValue : field.validValues()) {
            validValues.add(new JsonPrimitive(validValue));
        }
        json.add("validValues", validValues);
        json.addProperty("isMandatory", field.isMandatory());
        json.addProperty("fieldType", field.fieldType().name());
        json.add("name", getLocalizedString(bundle, field.name()).json());
        json.add("hint", getLocalizedString(bundle, field.hint()).json());
        json.addProperty("key", fieldMethod.getDeclaringClass().getName() + "." + fieldMethod.getName());
        return json;
    }

}
