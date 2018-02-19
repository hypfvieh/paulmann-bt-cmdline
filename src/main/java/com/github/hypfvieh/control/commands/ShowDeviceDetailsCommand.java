package com.github.hypfvieh.control.commands;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;

import org.jline.terminal.Terminal;

import com.github.hypfvieh.PaulmannDeviceController;
import com.github.hypfvieh.PaulmannDeviceController.DeviceDetails;
import com.github.hypfvieh.control.ShellFormatter;
import com.github.hypfvieh.control.commands.base.AbstractCommand;

public class ShowDeviceDetailsCommand extends AbstractCommand {

    @Override
    public String[] execute(List<String> _arguments, Terminal _terminal) throws InterruptedIOException {
        ShellFormatter formatter = new ShellFormatter(_terminal);
        List<String> resultText = new ArrayList<>();
        List<DeviceDetails> listAllRawDevices = PaulmannDeviceController.getInstance().listAllRawDevices(false);
        if (listAllRawDevices.isEmpty()) {
            return printError(formatter, "No suitable devices found.");
        }

        for (DeviceDetails dev : listAllRawDevices) {
            resultText.add("---------------------");
            resultText.add(dev.prettyToString());
            resultText.add("---------------------");
            resultText.add("");
        }

        return resultText.toArray(new String[]{});
    }


    @Override
    public String getDescription() {
        return "List all known bluetooth details of all found bluetooth devices";
    }

    @Override
    public String getCommandName() {
        return "showDeviceDetails";
    }

}
