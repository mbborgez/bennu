package org.fenixedu.bennu.portal.bootstrap;

import java.util.List;

import com.google.gson.GsonBuilder;

public class PortalBootstrapperBean {
    private List<BootstrapSection> sections;
    private String key;
    private String name;

    public PortalBootstrapperBean() {
    }

    public PortalBootstrapperBean(PortalBootstrapper bootstrapper) {
        this.setSections(bootstrapper.getBootstrapSections());
        this.setKey(bootstrapper.getKey());
        this.setName(bootstrapper.getName());
    }

    public List<BootstrapSection> getSections() {
        return sections;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public void setSections(List<BootstrapSection> sections) {
        this.sections = sections;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toJson() {
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }
}