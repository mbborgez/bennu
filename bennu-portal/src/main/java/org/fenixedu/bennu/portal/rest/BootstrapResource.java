package org.fenixedu.bennu.portal.rest;

import java.util.Collection;
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
import org.fenixedu.bennu.portal.bootstrap.PortalBootstrapperBean;
import org.fenixedu.bennu.portal.bootstrap.PortalBootstrapperRegistry;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;

@Path("/bennu-portal/bootstrap")
public class BootstrapResource extends BennuRestResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(final String json) {
        PortalBootstrapperBean bean = new Gson().fromJson(json, PortalBootstrapperBean.class);
        try {
            ensureMandatoryFields(bean.getSections());
            PortalBootstrapper portalBootstrapper = PortalBootstrapperRegistry.getPortalBootstrapper(bean.getKey());
            portalBootstrapper.boostrap(bean.getSections());
            PortalBootstrapperRegistry.unregisterBootstrapper(bean.getKey());
            return Response.status(200).build();
        } catch (BootstrapError e) {
            return Response.status(500).entity(e.toJson()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPortalBootstrapper() {
        Collection<PortalBootstrapper> portalBootstrappers = PortalBootstrapperRegistry.getPortalBootstrappers();
        PortalBootstrapper bootstrapper = Iterables.getFirst(portalBootstrappers, null);
        return bootstrapper != null ? new PortalBootstrapperBean(bootstrapper).toJson() : null;
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
