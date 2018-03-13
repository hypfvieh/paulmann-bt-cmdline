package com.github.hypfvieh.control.commands.init;

import java.io.IOException;
import java.io.InterruptedIOException;

import org.jline.terminal.Terminal;
import org.slf4j.LoggerFactory;

import com.github.hypfvieh.bluetooth.DeviceManager;
import com.github.hypfvieh.control.ShellFormatter;

public class ShellDeInitializeCommand extends AbstractDeInitializationCommand {

    @Override
    protected String[] execute(Terminal _terminal) throws InterruptedIOException {
        try {
            DeviceManager.getInstance().close();
            return printSuccess(new ShellFormatter(_terminal), "Closed bluez session");
        } catch (IOException _ex) {
            LoggerFactory.getLogger(getClass()).error("Could not close bluez session", _ex);
            return printError(new ShellFormatter(_terminal), "Could not close bluez session: " + _ex.getMessage());
        }
        
    }

}
