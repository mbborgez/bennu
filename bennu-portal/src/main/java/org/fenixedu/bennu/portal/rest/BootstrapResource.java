package org.fenixedu.bennu.portal.rest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.portal.servlet.BootstrapError;
import org.fenixedu.bennu.portal.servlet.BootstrapField;
import org.fenixedu.bennu.portal.servlet.BootstrapField.FieldType;
import org.fenixedu.bennu.portal.servlet.BootstrapSection;
import org.fenixedu.bennu.portal.servlet.PortalBootstrapper;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/bennu-portal/bootstrap")
public class BootstrapResource extends BennuRestResource {

    private final Type INPUT_TYPE = new TypeToken<List<BootstrapSection>>() {
    }.getType();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(final String json) {
        List<BootstrapSection> sections = new Gson().fromJson(json, INPUT_TYPE);
        try {
            ensureMandatoryFields(sections);
            getPortalBootstrapper().boostrap(sections);
            return Response.status(200).build();
        } catch (BootstrapError e) {
            return Response.status(500).entity(e.toJson()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSections() {
        return new GsonBuilder().serializeNulls().create().toJson(getPortalBootstrapper().getBootstrapSections());
    }

    private PortalBootstrapper getPortalBootstrapper() {
        return new DefaultPortalBootstrapper();
    }

    private void ensureMandatoryFields(List<BootstrapSection> sections) throws BootstrapError {
        for (BootstrapSection section : sections) {
            for (BootstrapField field : section.getFields()) {
                if (field.isMandatory() && StringUtils.isEmpty(field.getValue())) {
                    String errorMessage = String.format("The field %s is mandatory.", field.getName());
                    throw new BootstrapError(section, field, errorMessage);
                }
            }
        }
    }

    public static class DefaultPortalBootstrapper implements PortalBootstrapper {

        @Override
        public List<BootstrapSection> getBootstrapSections() {
            List<BootstrapField> firstSectionFields = new ArrayList<>();
            firstSectionFields.add(new BootstrapField("University Name", "Example University"));
            firstSectionFields.add(new BootstrapField("University Acronym", "EU"));
            firstSectionFields.add(new BootstrapField("School Name", "Example Engineering School"));
            firstSectionFields.add(new BootstrapField("School Acronym", "EES"));

            List<BootstrapField> secondSectionFields = new ArrayList<>();
            secondSectionFields.add(new BootstrapField("School Domain", "ees.example.edu"));
            secondSectionFields.add(new BootstrapField("School URL", "http://www.ist.ut.pt"));
            secondSectionFields.add(new BootstrapField("School Email Domain", "ist.utl.pt"));
            secondSectionFields.add(new BootstrapField("Installation Name", "Fenix"));
            secondSectionFields.add(new BootstrapField("Installation Domain", "fenixedu.ees"));

            List<BootstrapField> thirdSectionFields = new ArrayList<>();
            thirdSectionFields.add(new BootstrapField("Username", "admin"));
            thirdSectionFields.add(new BootstrapField("Name", "FenixEdu Administrator"));
            thirdSectionFields.add(new BootstrapField("Email", "admin@ist.utl.pt", true, FieldType.EMAIL));

            thirdSectionFields.add(new BootstrapField("Password", "Your password", true, FieldType.PASSWORD));
            thirdSectionFields.add(new BootstrapField("Password (again)", "Your password", true, FieldType.PASSWORD));

            thirdSectionFields.add(new BootstrapField("thirdField", "asdsadsa", true, FieldType.SELECT_ONE, Lists.newArrayList(
                    "Miguel Borges", "sadasdasdsadasdasdsadasdasdsadasdasd", "asdasdasdasdasdC")));

            ArrayList<BootstrapSection> sections = new ArrayList<>();
            sections.add(new BootstrapSection("School setup",
                    "Let's setup your school. You need to set up your university and school name.", firstSectionFields));
            sections.add(new BootstrapSection("Application information",
                    "Next we need to setup the domain information about your applicatinos.", secondSectionFields));
            sections.add(new BootstrapSection("Account", "Now we need to create an administrator account.", thirdSectionFields));

            return sections;
        }

        @Override
        public void boostrap(List<BootstrapSection> sections) throws BootstrapError {
            BootstrapSection account = null;
            for (BootstrapSection section : sections) {
                if (StringUtils.equals(section.getName(), "Account")) {
                    account = section;
                }
            }

            String pass = account.getField("Password").getValue();
            String retypedPass = account.getField("Password (again)").getValue();

            if (StringUtils.isEmpty(pass)) {
                throw new BootstrapError(account, account.getField("Password"), "Please enter a password");
            }
            if (StringUtils.isEmpty(retypedPass)) {
                throw new BootstrapError(account, account.getField("Password (again)"), "Please retype your password");
            }
            if (!StringUtils.equals(pass, retypedPass)) {
                throw new BootstrapError(account, account.getField("Password (again)"), "Passwords don't match");
            }
        }

    }

}
