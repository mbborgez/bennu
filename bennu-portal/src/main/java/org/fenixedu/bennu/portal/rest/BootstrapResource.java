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

import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.portal.servlet.BootstrapField;
import org.fenixedu.bennu.portal.servlet.BootstrapSection;
import org.fenixedu.bennu.portal.servlet.PortalBootstrapper;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/bennu-portal/bootstrap")
public class BootstrapResource extends BennuRestResource {

    @POST
    @SuppressWarnings("serial")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(final String json) {
        Type type = new TypeToken<List<BootstrapSection>>() {
        }.getType();

        List<BootstrapSection> sections = new Gson().fromJson(json, type);
        if (validate(sections)) {
            return Response.status(200).build();
        }
        return Response.status(500).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getSections() {
        return new GsonBuilder().serializeNulls().create().toJson(getPortalBootstrapper().getBootstrapSections());
    }

    private PortalBootstrapper getPortalBootstrapper() {
        return new DefaultPortalBootstrapper();
    }

    private boolean validate(List<BootstrapSection> sections) {
        return true;
    }

    public static class DefaultPortalBootstrapper implements PortalBootstrapper {

        @Override
        public List<BootstrapSection> getBootstrapSections() {
            List<BootstrapField> firstSectionFields = new ArrayList<>();
            firstSectionFields.add(new BootstrapField("firstField", "firstFieldDescription"));
            firstSectionFields.add(new BootstrapField("secondField", "secondFieldDescription"));

            List<BootstrapField> secondSectionFields = new ArrayList<>();
            secondSectionFields.add(new BootstrapField("firstField", "first hint"));
            secondSectionFields.add(new BootstrapField("secondField", "hint"));

            List<BootstrapField> thirdSectionFields = new ArrayList<>();
            thirdSectionFields.add(new BootstrapField("firstField", "hint"));
            thirdSectionFields.add(new BootstrapField("secondField", "second hint"));

            ArrayList<BootstrapSection> sections = new ArrayList<>();
            sections.add(new BootstrapSection("firstSection", "description", firstSectionFields));
            sections.add(new BootstrapSection("secondSectionFields", "description", secondSectionFields));
            sections.add(new BootstrapSection("thirdSectionFields", "description", thirdSectionFields));

            return sections;
        }

        @Override
        public String boostrap(List<BootstrapSection> sections) {
            // TODO Auto-generated method stub
            return null;
        }

    }

}
