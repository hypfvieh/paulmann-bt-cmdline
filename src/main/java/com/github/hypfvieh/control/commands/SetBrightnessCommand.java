package com.github.hypfvieh.control.commands;

import java.io.InterruptedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.jline.terminal.Terminal;

import com.github.hypfvieh.PaulmannDeviceController;
import com.github.hypfvieh.control.ShellFormatter;
import com.github.hypfvieh.control.commands.base.AbstractCommand;
import com.github.hypfvieh.control.commands.base.CommandArg;
import com.github.hypfvieh.control.jline3.ArgWithDescription;
import com.github.hypfvieh.paulmann.devices.AbstractPaulmannDevice;
import com.github.hypfvieh.paulmann.features.BluetoothBrightnessFeature;
import com.github.hypfvieh.paulmann.features.FeatureIdent;

public class SetBrightnessCommand extends AbstractCommand {

    @Override
    public String[] execute(List<String> _arguments, Terminal _terminal) throws InterruptedIOException {
        ShellFormatter formatter = new ShellFormatter(_terminal);

        if (_arguments == null || _arguments.size() < 2) {
            return printError(formatter, "Device (MacAddress) and brightness-level (10-100) are required!");
        }

        if (PaulmannDeviceController.getInstance().getDevices().isEmpty()) {
            return printError(formatter, "No suitable devices found.");
        }

        if (StringUtils.isBlank(_arguments.get(1)) || !StringUtils.isNumeric(_arguments.get(1))) {
            return printError(formatter, "The brightness value has to be numeric");
        }

        int intVal = Integer.parseInt(_arguments.get(1));

        AbstractPaulmannDevice device = PaulmannDeviceController.getInstance().getDevices().get(_arguments.get(0));
        if (device == null) {
            return printError(formatter, "No device with MAC address " + _arguments.get(0) + " found.");
        } else {
            BluetoothBrightnessFeature devFeature = device.getFeature(FeatureIdent.PAULMANN_BRIGHTNESS_FEATURE);
            if (devFeature != null) {
                if (intVal > devFeature.getMaxValue()) {
                    return printError(formatter, "Given brightness value " + intVal + " is higher than the allowed maximum of " + devFeature.getMaxValue());
                } else if (intVal < devFeature.getMinValue()) {
                    return printError(formatter, "Given brightness value " + intVal + " is lower than the required minimum of " + devFeature.getMinValue());
                } else {
                    if (!devFeature.writeByte((byte) intVal)) {
                        return printError(formatter, "Could not change brightness level to " + intVal);
                    } else {
                        return printSuccess(formatter, "Successfully changed brightness level to" + intVal);
                    }
                }
            } else {
                return printError(formatter, "Device does not support brightness level feature!");
            }
        }
    }

    @Override
    public String getCommandName() {
        return "setBrightness";
    }

    @Override
    public List<CommandArg> getCommandArgs() {
        
        CommandArg deviceMacAddress = new CommandArg("deviceMacAddress", true, false, () -> {
            return PaulmannDeviceController.getInstance().getDevices().values().stream()
                    .map(k -> new ArgWithDescription(k.getDevice().getAddress(), k.getDevice().getName()))
                    .collect(Collectors.toList());
        });
        
        CommandArg brightnessLevel = new CommandArg("brightnessLevel", true, true, () -> {
            return IntStream.range(10, 100).mapToObj(i -> new ArgWithDescription(String.valueOf(i), null)).collect(Collectors.toList());
        });
        
        return Arrays.asList(deviceMacAddress, brightnessLevel);
    }

    @Override
    public String getDescription() {
        return "Change brightness of the given device. Values from 10-100 are allowed.";
    }

}
