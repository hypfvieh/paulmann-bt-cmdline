package com.github.hypfvieh.control.commands;

import java.io.InterruptedIOException;
import java.util.List;

import org.jline.terminal.Terminal;

import com.github.hypfvieh.control.commands.base.AbstractCommand;

public class ExitCommand extends AbstractCommand {
    @Override
    public String[] execute(List<String> _arguments, Terminal _terminal) throws InterruptedIOException {
        throw new InterruptedIOException("User wants to exit");
    }

    @Override
    public String getCommandName() {
        return "exit";
    }

    @Override
    public String getDescription() {
        return "Exit shell";
    }

    @Override
    public String[] getCommandAliases() {

        return new String[] { "quit" };
    }

}
