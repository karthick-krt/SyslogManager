package com.syslogmanager.application.model;

public enum Role {
    ADMIN(1, "admin"),
    TECHNICIAN(2, "technician");

    private final int id;
    private final String label;

    Role(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static Role fromId(int id) {
        for (Role r : values()) {
            if (r.id == id) return r;
        }
        throw new IllegalArgumentException("Unknown Role id: " + id);
    }
}
