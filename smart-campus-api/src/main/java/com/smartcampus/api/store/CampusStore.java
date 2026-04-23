package com.smartcampus.api.store;

import com.smartcampus.api.models.Room;
import com.smartcampus.api.models.Sensor;
import com.smartcampus.api.models.SensorReading;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public final class CampusStore {
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_MAINTENANCE = "MAINTENANCE";

    private static final CampusStore INSTANCE = new CampusStore();

    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SensorReading>> readingsBySensorId = new ConcurrentHashMap<>();

    private CampusStore() {
    }

    public static CampusStore getInstance() {
        return INSTANCE;
    }

    public List<Room> listRooms() {
        return rooms.values().stream()
                .sorted(Comparator.comparing(Room::getId, Comparator.nullsLast(String::compareTo)))
                .map(this::copyRoom)
                .collect(Collectors.toList());
    }

    public Optional<Room> getRoom(String roomId) {
        if (roomId == null) {
            return Optional.empty();
        }
        Room room = rooms.get(roomId);
        return room == null ? Optional.empty() : Optional.of(copyRoom(room));
    }

    public Room createRoom(Room input) {
        Objects.requireNonNull(input, "room");
        Room roomToStore = copyRoom(input);
        if (roomToStore.getId() == null || roomToStore.getId().isBlank()) {
            roomToStore.setId(UUID.randomUUID().toString());
        }
        roomToStore.setSensorIds(new ArrayList<>());
        rooms.put(roomToStore.getId(), roomToStore);
        return copyRoom(roomToStore);
    }

    public Optional<Room> updateRoom(String roomId, Room input) {
        Objects.requireNonNull(roomId, "roomId");
        Objects.requireNonNull(input, "room");
        return Optional.ofNullable(
                rooms.computeIfPresent(
                        roomId,
                        (id, existing) -> {
                            existing.setName(input.getName());
                            existing.setCapacity(input.getCapacity());
                            return existing;
                        }))
                .map(this::copyRoom);
    }

    public boolean roomHasActiveSensors(String roomId) {
        if (roomId == null) {
            return false;
        }
        for (Sensor sensor : sensors.values()) {
            if (roomId.equals(sensor.getRoomId()) && isActive(sensor)) {
                return true;
            }
        }
        return false;
    }

    public boolean deleteRoom(String roomId) {
        Room removed = rooms.remove(roomId);
        if (removed == null) {
            return false;
        }
        for (Sensor sensor : sensors.values()) {
            if (roomId.equals(sensor.getRoomId())) {
                sensor.setRoomId(null);
            }
        }
        return true;
    }

    public List<Sensor> listSensors(String typeFilter) {
        return sensors.values().stream()
                .filter(
                        s -> {
                            if (typeFilter == null || typeFilter.isBlank()) {
                                return true;
                            }
                            return s.getType() != null && s.getType().equalsIgnoreCase(typeFilter);
                        })
                .sorted(Comparator.comparing(Sensor::getId, Comparator.nullsLast(String::compareTo)))
                .map(this::copySensor)
                .collect(Collectors.toList());
    }

    public Optional<Sensor> getSensor(String sensorId) {
        if (sensorId == null) {
            return Optional.empty();
        }
        Sensor sensor = sensors.get(sensorId);
        return sensor == null ? Optional.empty() : Optional.of(copySensor(sensor));
    }

    public Optional<Sensor> getSensorInternal(String sensorId) {
        if (sensorId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(sensors.get(sensorId));
    }

    public boolean roomExists(String roomId) {
        if (roomId == null) {
            return false;
        }
        return rooms.containsKey(roomId);
    }

    public Sensor createSensor(Sensor input) {
        Objects.requireNonNull(input, "sensor");
        Sensor toStore = copySensor(input);
        if (toStore.getId() == null || toStore.getId().isBlank()) {
            toStore.setId(UUID.randomUUID().toString());
        }
        if (toStore.getStatus() == null || toStore.getStatus().isBlank()) {
            toStore.setStatus(STATUS_INACTIVE);
        }
        sensors.put(toStore.getId(), toStore);
        readingsBySensorId.putIfAbsent(toStore.getId(), new CopyOnWriteArrayList<>());

        Room room = rooms.get(toStore.getRoomId());
        if (room != null) {
            synchronized (room) {
                room.addSensorId(toStore.getId());
            }
        }

        return copySensor(toStore);
    }

    public Optional<Sensor> updateSensor(String sensorId, Sensor input) {
        Objects.requireNonNull(sensorId, "sensorId");
        Objects.requireNonNull(input, "sensor");

        return Optional.ofNullable(
                sensors.computeIfPresent(
                        sensorId,
                        (id, existing) -> {
                            String oldRoomId = existing.getRoomId();
                            String newRoomId = input.getRoomId();

                            existing.setType(input.getType());
                            existing.setStatus(input.getStatus());
                            existing.setRoomId(newRoomId);

                            if (oldRoomId != null && !oldRoomId.equals(newRoomId)) {
                                Room oldRoom = rooms.get(oldRoomId);
                                if (oldRoom != null) {
                                    synchronized (oldRoom) {
                                        oldRoom.removeSensorId(existing.getId());
                                    }
                                }
                            }

                            if (newRoomId != null && !newRoomId.equals(oldRoomId)) {
                                Room newRoom = rooms.get(newRoomId);
                                if (newRoom != null) {
                                    synchronized (newRoom) {
                                        newRoom.addSensorId(existing.getId());
                                    }
                                }
                            }

                            return existing;
                        }))
                .map(this::copySensor);
    }

    public boolean deleteSensor(String sensorId) {
        Sensor removed = sensors.remove(sensorId);
        readingsBySensorId.remove(sensorId);
        if (removed == null) {
            return false;
        }

        if (removed.getRoomId() != null) {
            Room room = rooms.get(removed.getRoomId());
            if (room != null) {
                synchronized (room) {
                    room.removeSensorId(removed.getId());
                }
            }
        }

        return true;
    }

    public List<SensorReading> listReadings(String sensorId) {
        List<SensorReading> readings = readingsBySensorId.get(sensorId);
        if (readings == null) {
            return List.of();
        }
        return readings.stream()
                .sorted(Comparator.comparingLong(SensorReading::getTimestamp))
                .map(this::copyReading)
                .collect(Collectors.toList());
    }

    public SensorReading addReading(String sensorId, SensorReading input) {
        Objects.requireNonNull(sensorId, "sensorId");
        Objects.requireNonNull(input, "reading");
        CopyOnWriteArrayList<SensorReading> list = readingsBySensorId.computeIfAbsent(sensorId,
                id -> new CopyOnWriteArrayList<>());

        SensorReading toStore = copyReading(input);
        if (toStore.getId() == null || toStore.getId().isBlank()) {
            toStore.setId(UUID.randomUUID().toString());
        }
        if (toStore.getTimestamp() <= 0) {
            toStore.setTimestamp(System.currentTimeMillis());
        }
        list.add(toStore);

        Sensor sensor = sensors.get(sensorId);
        if (sensor != null) {
            sensor.setCurrentValue(toStore.getValue());
        }

        return copyReading(toStore);
    }

    public static boolean isMaintenance(Sensor sensor) {
        if (sensor == null || sensor.getStatus() == null) {
            return false;
        }
        return STATUS_MAINTENANCE.equalsIgnoreCase(sensor.getStatus());
    }

    private static boolean isActive(Sensor sensor) {
        if (sensor == null || sensor.getStatus() == null) {
            return false;
        }
        return STATUS_ACTIVE.equalsIgnoreCase(sensor.getStatus());
    }

    private Room copyRoom(Room room) {
        Room copy = new Room();
        copy.setId(room.getId());
        copy.setName(room.getName());
        copy.setCapacity(room.getCapacity());
        copy.setSensorIds(room.getSensorIds());
        return copy;
    }

    private Sensor copySensor(Sensor sensor) {
        Sensor copy = new Sensor();
        copy.setId(sensor.getId());
        copy.setType(sensor.getType());
        copy.setStatus(sensor.getStatus());
        copy.setCurrentValue(sensor.getCurrentValue());
        copy.setRoomId(sensor.getRoomId());
        return copy;
    }

    private SensorReading copyReading(SensorReading reading) {
        SensorReading copy = new SensorReading();
        copy.setId(reading.getId());
        copy.setTimestamp(reading.getTimestamp());
        copy.setValue(reading.getValue());
        return copy;
    }

    public Map<String, Room> internalRoomsView() {
        return rooms;
    }
}
