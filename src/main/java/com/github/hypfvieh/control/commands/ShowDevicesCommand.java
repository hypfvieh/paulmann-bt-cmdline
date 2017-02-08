package com.github.hypfvieh.control.commands;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;

import org.jline.terminal.Terminal;

import com.github.hypfvieh.PaulmannDeviceController;
import com.github.hypfvieh.control.ShellFormatter;
import com.github.hypfvieh.paulmann.devices.AbstractPaulmannDevice;

public class ShowDevicesCommand extends AbstractCommand {

    @Override
    public String[] execute(List<String> _arguments, Terminal _terminal) throws InterruptedIOException {
        ShellFormatter formatter = new ShellFormatter(_terminal);
        List<String> resultText = new ArrayList<>();
        if (PaulmannDeviceController.getInstance().getDevices().isEmpty()) {
            return printError(formatter, "No suitable devices found.");
        }

        for (AbstractPaulmannDevice dev : PaulmannDeviceController.getInstance().getDevices().values()) {
            resultText.add("\tMAC: " + dev.getDevice().getAddress());
            resultText.add("\t" + dev.toString());
        }

        return resultText.toArray(new String[]{});
    }


    @Override
    public String getDescription() {
        return "List all previously found devices";
    }


    @Override
    public String getCommandArgs() {
        return "";
    }


    @Override
    public String getCommandName() {
        return "showDevices";
    }

}
