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
import com.github.hypfvieh.paulmann.features.RgbFeature;
import com.github.hypfvieh.paulmann.features.FeatureIdent;

public class SetRgbCommand extends AbstractCommand {

    @Override
    public String[] execute(List<String> _arguments, Terminal _terminal) throws InterruptedIOException {
        ShellFormatter formatter = new ShellFormatter(_terminal);

        if (_arguments == null || _arguments.size() < 4) {
            return printError(formatter, "Device (MacAddress), color level for red, green and blue (0-255) are required!");
        }

        if (PaulmannDeviceController.getInstance().getDevices().isEmpty()) {
            return printError(formatter, "No suitable devices found.");
        }

        if (StringUtils.isBlank(_arguments.get(1)) || !StringUtils.isNumeric(_arguments.get(1))) {
            return printError(formatter, "The value for the red channel has to be numeric");
        }

        if (StringUtils.isBlank(_arguments.get(2)) || !StringUtils.isNumeric(_arguments.get(2))) {
            return printError(formatter, "The value for the green channel has to be numeric");
        }

        if (StringUtils.isBlank(_arguments.get(3)) || !StringUtils.isNumeric(_arguments.get(3))) {
            return printError(formatter, "The value for the blue channel has to be numeric");
        }

        int red = Integer.parseInt(_arguments.get(1));
        int green = Integer.parseInt(_arguments.get(2));
        int blue = Integer.parseInt(_arguments.get(3));

        AbstractPaulmannDevice device = PaulmannDeviceController.getInstance().getDevices().get(_arguments.get(0));
        if (device == null) {
            return printError(formatter, "No device with MAC address " + _arguments.get(0) + " found.");
        } else {
            RgbFeature feature = device.getFeature(FeatureIdent.PAULMANN_RGB_FEATURE);
            if (feature != null) {
                if (feature.getMaxValue(null) < red || feature.getMaxValue(null) < green || feature.getMaxValue(null) < blue) {
                    return printError(formatter, "One of the given color channel values (red = " + red + ", green = " + green + ", blue = " + blue + ") are higher than the allowed maximum of " + feature.getMaxValue(null));
                } else if (red < feature.getMinValue(null) || green < feature.getMinValue(null) || blue < feature.getMinValue(null)) {
                    return printError(formatter, "One of the given color channel values (red = " + red + ", green = " + green + ", blue = " + blue + ") are lower than the required minimum of " + feature.getMinValue(null));
                }
                if (!feature.setAllColors((byte) red, (byte) green, (byte) blue)) {
                    return printError(formatter, "Could not change RGB channels to level to red = " + red + ", green = " + green + ", blue = " + blue);
                } else {
                    return printSuccess(formatter, "Successfully changed RGB levels to red = " + red + ", green = " + green + ", blue = " + blue);
                }
            } else {
                return printError(formatter, "Device does not support RGB feature!");
            }
        }
    }

    @Override
    public String getCommandName() {
        return "setRGB";
    }

    @Override
    public List<CommandArg> getCommandArgs() {
        CommandArg deviceMacAddress = new CommandArg("deviceMacAddress", true, false, () -> {
            return PaulmannDeviceController.getInstance().getDevices().values().stream()
                    .map(k -> new ArgWithDescription(k.getDevice().getAddress(), k.getDevice().getName()))
                    .collect(Collectors.toList());
        });
        
        CommandArg brightnessRed = new CommandArg("red-value", true, true, () -> {
            return IntStream.range(0, 255).mapToObj(i -> new ArgWithDescription(String.valueOf(i), null)).collect(Collectors.toList());
        });

        CommandArg brightnessGreen = new CommandArg("green-value", true, true, () -> {
            return IntStream.range(0, 255).mapToObj(i -> new ArgWithDescription(String.valueOf(i), null)).collect(Collectors.toList());
        });

        CommandArg brightnessBlue = new CommandArg("blue-value", true, true, () -> {
            return IntStream.range(0, 255).mapToObj(i -> new ArgWithDescription(String.valueOf(i), null)).collect(Collectors.toList());
        });

        return Arrays.asList(deviceMacAddress, brightnessRed, brightnessGreen, brightnessBlue);
    }

    @Override
    public String getDescription() {
        return "Set the RGB colors of the given device. Values from 0-255 are allowed.";
    }

}
