package com.golubev;

public enum SortingOrder {
    ASC("-a"),
    DESC("-d");
    private final String command;

    SortingOrder(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}