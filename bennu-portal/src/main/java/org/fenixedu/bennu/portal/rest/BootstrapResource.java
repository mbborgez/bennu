package org.fenixedu.bennu.portal.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.rest.BennuRestResource;

import com.google.gson.Gson;

@Path("/bennu-portal/bootstrap")
public class BootstrapResource extends BennuRestResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBootstrap(final String json) {
        BootstrapBean bean = new Gson().fromJson(json, BootstrapBean.class);
        return Response.status(200).entity(new Gson().toJson(bean)).build();
    }

    static class BootstrapBean {
        String country;
        String universityName;
        String universityAcronym;
        String schoolName;
        String schoolAcronym;
        String schoolDomain;
        String schoolURL;
        String schoolEmailDomain;
        String instalationName;
        String instalationDomain;
        String username;
        String name;
        String email;
        String password;
        String passwordRetyped;
    }

}
