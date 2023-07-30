package com.mrh0.createaddition.blocks.connector;

import com.mrh0.createaddition.config.Config;

public enum ConnectorType {
    Small("small"),
    Large("large");

    public final String name;

    ConnectorType(String name) {
        this.name = name;
    }

    public int getMaxWireLength() {
        return switch (this) {
            case Small -> Config.SMALL_CONNECTOR_MAX_LENGTH.get();
            case Large -> Config.LARGE_CONNECTOR_MAX_LENGTH.get();
        };
    }
}
