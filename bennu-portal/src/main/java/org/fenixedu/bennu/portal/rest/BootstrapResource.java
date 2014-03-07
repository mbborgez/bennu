package org.fenixedu.bennu.portal.rest;

import java.lang.reflect.Type;
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
import org.fenixedu.bennu.portal.bootstrap.BootstrapError;
import org.fenixedu.bennu.portal.bootstrap.BootstrapField;
import org.fenixedu.bennu.portal.bootstrap.BootstrapSection;
import org.fenixedu.bennu.portal.bootstrap.PortalBootstrapper;
import org.fenixedu.bennu.portal.bootstrap.PortalBootstrapperRegistry;

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
        return Lists.newArrayList(PortalBootstrapperRegistry.getPortalBootstrappers().values()).get(0);
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

}
