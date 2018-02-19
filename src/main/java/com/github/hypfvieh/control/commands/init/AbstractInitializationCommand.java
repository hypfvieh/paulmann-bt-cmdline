package com.github.hypfvieh.control.commands.init;

import java.io.InterruptedIOException;
import java.util.List;

import org.jline.terminal.Terminal;

import com.github.hypfvieh.control.commands.base.AbstractCommand;

/**
 * Base class for shell initialization commands.
 * 
 */
public abstract class AbstractInitializationCommand extends AbstractCommand {

    @Override
    public final String[] execute(List<String> _arguments, Terminal _terminal) throws InterruptedIOException {
        return execute(_terminal);
    }

    protected abstract String[] execute(Terminal _terminal) throws InterruptedIOException;

    @Override
    public final String getCommandName() {
        return null;
    }

}
