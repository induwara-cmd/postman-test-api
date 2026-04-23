package com.smartcampus.api.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Room {
    private String id;
    private String name;
    private int capacity;
    private List<String> sensorIds = new ArrayList<>();

    public Room() {
    }

    public Room(String id, String name, int capacity, List<String> sensorIds) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        setSensorIds(sensorIds);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getSensorIds() {
        return Collections.unmodifiableList(new ArrayList<>(sensorIds));
    }

    public void setSensorIds(List<String> sensorIds) {
        if (sensorIds == null) {
            this.sensorIds = new ArrayList<>();
            return;
        }
        this.sensorIds = new ArrayList<>(sensorIds);
    }

    public void addSensorId(String sensorId) {
        Objects.requireNonNull(sensorId, "sensorId");
        if (!sensorIds.contains(sensorId)) {
            sensorIds.add(sensorId);
        }
    }

    public void removeSensorId(String sensorId) {
        if (sensorId == null) {
            return;
        }
        sensorIds.remove(sensorId);
    }
}
