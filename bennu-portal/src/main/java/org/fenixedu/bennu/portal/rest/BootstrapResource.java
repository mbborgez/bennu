package org.fenixedu.bennu.portal.rest;

import java.lang.reflect.Method;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fenixedu.bennu.core.rest.BennuRestResource;
import org.fenixedu.bennu.portal.bootstrap.PortalBootstrapperRegistry;
import org.fenixedu.bennu.portal.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.portal.bootstrap.annotations.Field;
import org.fenixedu.bennu.portal.bootstrap.annotations.Section;
import org.fenixedu.bennu.portal.bootstrap.beans.BootstrapperBean;
import org.fenixedu.bennu.portal.bootstrap.beans.FieldBean;
import org.fenixedu.bennu.portal.bootstrap.beans.SectionBean;

import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

@Path("/bennu-portal/bootstrap")
public class BootstrapResource extends BennuRestResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(final String json) {
        try {
            System.out.println(json);
            //TODO - call SectionsBootstrapper.bootstrapAllSections(json);
            return Response.status(200).build();
        } catch (Exception e) {
            JsonObject jsonError = new JsonObject();
            jsonError.addProperty("message", e.getMessage());
            return Response.status(500).entity(jsonError.toString()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getPortalBootstrappers() {
        return new GsonBuilder().serializeNulls().create().toJson(createBootstrapperBeans());
    }

    private List<BootstrapperBean> createBootstrapperBeans() {
        List<BootstrapperBean> bootstrapperBeans = Lists.newArrayList();
        for (Class<?> bootstrapperClass : PortalBootstrapperRegistry.getBootstrappers()) {
            Bootstrapper bootstrapper = bootstrapperClass.getAnnotation(Bootstrapper.class);
            BootstrapperBean bootstrapperBean = new BootstrapperBean(bootstrapper);
            for (Class<?> sectionClass : bootstrapper.sections()) {
                Section section = sectionClass.getAnnotation(Section.class);
                SectionBean sectionBean = new SectionBean(section);
                for (Method method : PortalBootstrapperRegistry.getBootstrapMethods(sectionClass)) {
                    Field field = method.getAnnotation(Field.class);
                    sectionBean.addField(new FieldBean(field));
                }
                bootstrapperBean.addSection(sectionBean);
            }
            bootstrapperBeans.add(bootstrapperBean);
        }
        return bootstrapperBeans;
    }

}
