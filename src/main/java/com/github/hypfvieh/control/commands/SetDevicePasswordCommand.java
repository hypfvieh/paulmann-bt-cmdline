package com.github.hypfvieh.control.commands;

import java.io.InterruptedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jline.terminal.Terminal;

import com.github.hypfvieh.PasswordManager;
import com.github.hypfvieh.PaulmannDeviceController;
import com.github.hypfvieh.control.ShellFormatter;
import com.github.hypfvieh.control.commands.base.AbstractCommand;
import com.github.hypfvieh.control.commands.base.CommandArg;
import com.github.hypfvieh.control.jline3.ArgWithDescription;
import com.github.hypfvieh.paulmann.devices.AbstractPaulmannDevice;
import com.github.hypfvieh.paulmann.features.DevicePasswordFeature;
import com.github.hypfvieh.paulmann.features.FeatureIdent;

public class SetDevicePasswordCommand extends AbstractCommand {

    @Override
    public String[] execute(List<String> _arguments, Terminal _terminal) throws InterruptedIOException {
        ShellFormatter formatter = new ShellFormatter(_terminal);

        if (_arguments == null || _arguments.size() < 2) {
            return printError(formatter, "Device (MacAddress) and password which should be set are required!");
        }

        if (PaulmannDeviceController.getInstance().getDevices().isEmpty()) {
            return printError(formatter, "No suitable devices found.");
        }
        if (StringUtils.isBlank(_arguments.get(1)) || !StringUtils.isNumeric(_arguments.get(1))
                || _arguments.get(1).length() > 4) {
            return printError(formatter, "The password should be a numeric value with a maximum of 4 digits.");
        }

        String password = _arguments.get(1);

        PasswordManager.getInstance().putDevicePassword(_arguments.get(0), password);
        
        AbstractPaulmannDevice device = PaulmannDeviceController.getInstance().getDevices().get(_arguments.get(0));
        if (device == null) {
            return printError(formatter, "No device with MAC address " + _arguments.get(0) + " found.");
        } else {
            DevicePasswordFeature devFeature = device
                    .getFeature(FeatureIdent.PAULMANN_DEVICE_PASSWORD_FEATURE);
            if (devFeature != null) {
                if (!devFeature.authenticate(password)) {
                    return printError(formatter, "Could not set/update password!");
                } else {
                    return printSuccess(formatter, "Successfully updated/set password");
                }
            } else {
                return printError(formatter, "Unable to set password, wrong device class!");
            }
        }
    }

    @Override
    public String getCommandName() {
        return "setDevicePassword";
    }

    @Override
    public List<CommandArg> getCommandArgs() {
        
        CommandArg deviceMacAddress = new CommandArg("deviceMacAddress", true, false, () -> {
            return PaulmannDeviceController.getInstance().getDevices().values().stream()
                    .map(k -> new ArgWithDescription(k.getDevice().getAddress(), k.getDevice().getName()))
                    .collect(Collectors.toList());
        });
        
        CommandArg pwToSet = new CommandArg("password_to_set", true);
        
        return Arrays.asList(deviceMacAddress, pwToSet);
    }

    @Override
    public String getDescription() {
        return "Setup the password required to control the device. Password has to be 4 digits.";
    }

}
