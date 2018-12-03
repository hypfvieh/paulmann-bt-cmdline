package com.github.hypfvieh.control.commands;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jline.terminal.Terminal;

import com.github.hypfvieh.PaulmannDeviceController;
import com.github.hypfvieh.control.ShellFormatter;
import com.github.hypfvieh.control.commands.base.AbstractCommand;
import com.github.hypfvieh.control.commands.base.CommandArg;
import com.github.hypfvieh.control.jline3.ArgWithDescription;
import com.github.hypfvieh.paulmann.devices.AbstractPaulmannDevice;
import com.github.hypfvieh.paulmann.features.AbstractByteValFeature;
import com.github.hypfvieh.paulmann.features.OnOffFeature;
import com.github.hypfvieh.paulmann.features.FeatureIdent;

public class OnOffSwitchCommand extends AbstractCommand {

    @Override
    public String[] execute(List<String> _arguments, Terminal _terminal) throws InterruptedIOException {
        ShellFormatter formatter = new ShellFormatter(_terminal);

        if (_arguments == null || _arguments.size() < 2) {
            return printError(formatter, "Device (MacAddress) and on/off/status command are required!");
        }

        if (PaulmannDeviceController.getInstance().getDevices().isEmpty()) {
            return printError(formatter, "No suitable devices found.");
        }

        if (StringUtils.isBlank(_arguments.get(1))
                || !ShellFormatter.equalsAny(_arguments.get(1), "on", "off", "status")) {
            return printError(formatter, "The switch instruction has to be either 'on', 'off' or 'status'");
        }

        boolean switchOn = _arguments.get(1).equals("on") ? true : false;
        boolean status = _arguments.get(1).equals("status");

        AbstractPaulmannDevice device = PaulmannDeviceController.getInstance().getDevices().get(_arguments.get(0));
        if (device == null) {
            return printError(formatter, "No device with MAC address " + _arguments.get(0) + " found.");
        } else {
            OnOffFeature devFeature = device.getFeature(FeatureIdent.PAULMANN_ON_OFF_FEATURE);
            if (devFeature != null) {
                if (status) {
                    int readInt = devFeature.readByte();
                    if (readInt == AbstractByteValFeature.ERROR_RETURN) {
                        return printError(formatter, "Could get device status");
                    } else {
                        return printSuccess(formatter, "Current device status: " + (readInt == 1 ? "on" : "off"));
                    }
                } else {
                    if (!devFeature.toggle(switchOn)) {                       
                        return printError(formatter, "Could not switch device " + (switchOn ? "on" : "off"));
                    } else {
                        return printSuccess(formatter, "Successfully switch device " + (switchOn ? "on" : "off"));
                    }
                }
            } else {
                return printError(formatter, "Device does not support on/off switch feature!");
            }
        }
    }

    @Override
    public String getCommandName() {
        return "switchOnOff";
    }

    @Override
    public List<CommandArg> getCommandArgs() {
        CommandArg cmdArg = new CommandArg("deviceMacAddress", true, true, () -> {
            return PaulmannDeviceController.getInstance().getDevices().entrySet()
                .stream().map(e -> new ArgWithDescription(e.getKey(), e.getValue().getClass().getSimpleName()))
                .collect(Collectors.toList());
        });

        CommandArg cmdArgOnOff = new CommandArg("operation", true, true, () -> {
            List<ArgWithDescription> args = new ArrayList<>();
            args.add(new ArgWithDescription("on", "Switch device on"));
            args.add(new ArgWithDescription("off", "Switch device off"));
            args.add(new ArgWithDescription("status", "Get current device status"));
           return args;
        });

        
        return Arrays.asList(cmdArg, cmdArgOnOff);
    }

    @Override
    public String getDescription() {
        return "Turn on/off switchable device or get the current switch status";
    }

}
