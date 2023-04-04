package com.mrh0.createaddition.compat.computercraft;

import net.minecraft.core.Direction;

public class Helpers {
    public static Direction nameToDir(String name) {
        return switch (name) {
            case "down", "bottom" -> Direction.DOWN;
            case "up", "top" -> Direction.UP;
            //case "north" -> Direction.NORTH;
            case "south" -> Direction.SOUTH;
            case "east" -> Direction.EAST;
            case "west" -> Direction.WEST;
            default -> Direction.NORTH;
        };
    }
}
