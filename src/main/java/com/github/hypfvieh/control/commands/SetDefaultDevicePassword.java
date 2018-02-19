package com.github.hypfvieh.control.commands;

import java.io.InterruptedIOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jline.terminal.Terminal;

import com.github.hypfvieh.PasswordManager;
import com.github.hypfvieh.control.ShellFormatter;
import com.github.hypfvieh.control.commands.base.AbstractCommand;
import com.github.hypfvieh.control.commands.base.CommandArg;

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
