package com.github.hypfvieh.control.commands.init;

import java.io.InterruptedIOException;

import org.jline.terminal.Terminal;

import com.github.hypfvieh.bluetooth.DeviceManager;
import com.github.hypfvieh.control.ShellFormatter;

public class ShellDeInitializeCommand extends AbstractDeInitializationCommand {

    @Override
    protected String[] execute(Terminal _terminal) throws InterruptedIOException {
        DeviceManager.getInstance().closeConnection();
        return printSuccess(new ShellFormatter(_terminal), "Closed bluez session");
    }

}
