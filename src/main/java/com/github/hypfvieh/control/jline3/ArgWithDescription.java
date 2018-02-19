package com.github.hypfvieh.control.jline3;

import java.util.Objects;

public class ArgWithDescription {
    private final String argument;
    private final String description;

    public ArgWithDescription(String _argument, String _desc) {
        Objects.requireNonNull(_argument);
        argument = _argument;
        description = _desc;
    }

    public String getArgument() {
        return argument;
    }

    public String getHelp() {
        return description;
    }

}