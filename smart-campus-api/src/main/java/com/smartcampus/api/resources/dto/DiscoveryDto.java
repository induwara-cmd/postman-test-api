package com.smartcampus.api.resources.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DiscoveryDto {
    private String name;
    private String version;
    private List<LinkDto> links = new ArrayList<>();

    public DiscoveryDto() {
    }

    public DiscoveryDto(String name, String version, List<LinkDto> links) {
        this.name = name;
        this.version = version;
        setLinks(links);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<LinkDto> getLinks() {
        return Collections.unmodifiableList(new ArrayList<>(links));
    }

    public void setLinks(List<LinkDto> links) {
        if (links == null) {
            this.links = new ArrayList<>();
            return;
        }
        this.links = new ArrayList<>(links);
    }
}
