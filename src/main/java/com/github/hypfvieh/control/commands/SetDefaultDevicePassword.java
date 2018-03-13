package com.github.hypfvieh.control.commands;

import java.io.InterruptedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jline.terminal.Terminal;

import com.github.hypfvieh.PasswordManager;
import com.github.hypfvieh.PaulmannDeviceController;
import com.github.hypfvieh.control.ShellFormatter;
import com.github.hypfvieh.control.commands.base.AbstractCommand;
import com.github.hypfvieh.control.commands.base.CommandArg;
import com.github.hypfvieh.paulmann.devices.AbstractPaulmannDevice;
import com.github.hypfvieh.paulmann.features.FeatureIdent;

public class SetDefaultDevicePassword extends AbstractCommand {

    @Override
    public String[] execute(List<String> _arguments, Terminal _terminal) throws InterruptedIOException {
        ShellFormatter formatter = new ShellFormatter(_terminal);

        if (StringUtils.isBlank(_arguments.get(0)) || !StringUtils.isNumeric(_arguments.get(0))
                || _arguments.get(0).length() > 4) {
            return printError(formatter, "The password should be a numeric value with a maximum of 4 digits.");
        }

        String password = _arguments.get(0);
        PasswordManager.getInstance().setDefaultPassword(password);
        
        Map<String, AbstractPaulmannDevice> devices = PaulmannDeviceController.getInstance().getDevices();
        try {
            for (AbstractPaulmannDevice d : devices.values()) {
                d.getFeature(FeatureIdent.PAULMANN_DEVICE_PASSWORD_FEATURE).writeString(password);
                Thread.sleep(200L);
                d.getFeature(FeatureIdent.PAULMANN_DEVICE_PASSWORD_FEATURE).writeString(password);
                Thread.sleep(200L);
                d.getFeature(FeatureIdent.PAULMANN_DEVICE_PASSWORD_FEATURE).writeString(password);
            }
        } catch (InterruptedException _ex) {
            // TODO Auto-generated catch block
            _ex.printStackTrace();
        }
        
        return printSuccess(formatter, "Successfully updated/set default password");

    }

    @Override
    public String getCommandName() {
        return "setDefaultDevicePassword";
    }

    @Override
    public List<CommandArg> getCommandArgs() {        
        CommandArg pwToSet = new CommandArg("password_to_set", true);
        
        return Arrays.asList(pwToSet);
    }

    @Override
    public String getDescription() {
        return "Setup the default password used for controlling a device if no specific password was set. Password has to be 4 digits.";
    }

}
