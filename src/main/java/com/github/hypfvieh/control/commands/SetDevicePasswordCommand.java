package com.github.hypfvieh.control.commands;

import java.io.InterruptedIOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;

import com.github.hypfvieh.PaulmannDeviceController;
import com.github.hypfvieh.control.ShellFormatter;
import com.github.hypfvieh.paulmann.devices.AbstractPaulmannDevice;
import com.github.hypfvieh.paulmann.features.BluetoothDevicePasswordFeature;
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

        AbstractPaulmannDevice device = PaulmannDeviceController.getInstance().getDevices().get(_arguments.get(0));
        if (device == null) {
            return printError(formatter, "No device with MAC address " + _arguments.get(0) + " found.");
        } else {
            BluetoothDevicePasswordFeature devFeature = device
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
    public String getCommandArgs() {
        return "deviceMacAddress password_to_set";
    }

    @Override
    public String getDescription() {
        return "Setup the password required to control the device. Password has to be 4 digits.";
    }

    @Override
    public List<Completer> getArgCompleters() {
        return Arrays.asList(new StringsCompleter(PaulmannDeviceController.getInstance().getDevices().keySet()));
    }

}
