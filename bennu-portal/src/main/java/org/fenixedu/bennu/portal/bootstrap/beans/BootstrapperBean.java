package org.fenixedu.bennu.portal.bootstrap.beans;

import java.util.ArrayList;
import java.util.List;

import org.fenixedu.bennu.portal.bootstrap.annotations.Bootstrapper;

public class BootstrapperBean {
    private String name;
    private String description;
    private List<SectionBean> sections;

    public BootstrapperBean(String name, String description, List<SectionBean> sections) {
        this.name = name;
        this.description = description;
        this.sections = sections;
    }

    public BootstrapperBean(String name, String description) {
        this(name, description, new ArrayList<SectionBean>());
    }

    public BootstrapperBean(Bootstrapper bootstrapper) {
        this(bootstrapper.name(), bootstrapper.description());
    }

    public void addSection(SectionBean sectionBean) {
        sections.add(sectionBean);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<SectionBean> getSections() {
        return sections;
    }

    public void setSections(List<SectionBean> sections) {
        this.sections = sections;
    }

}
