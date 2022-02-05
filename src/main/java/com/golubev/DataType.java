package com.golubev;

public enum DataType {
    NUM ("-i"),
    STR ("-s");

    private final String command;

    DataType(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}