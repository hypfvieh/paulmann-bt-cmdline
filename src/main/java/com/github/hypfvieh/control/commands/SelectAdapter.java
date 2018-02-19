package com.github.hypfvieh.control.commands;

import java.io.InterruptedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bluez.exceptions.BluezDoesNotExistsException;
import org.jline.terminal.Terminal;

import com.github.hypfvieh.bluetooth.DeviceManager;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothAdapter;
import com.github.hypfvieh.control.ShellFormatter;
import com.github.hypfvieh.control.commands.base.AbstractCommand;
import com.github.hypfvieh.control.commands.base.CommandArg;
import com.github.hypfvieh.control.jline3.ArgWithDescription;

public class SelectAdapter extends AbstractCommand {

    @Override
    public String[] execute(List<String> _arguments, Terminal _terminal) throws InterruptedIOException {
        ShellFormatter sf = new ShellFormatter(_terminal);
        
        if (_arguments == null || _arguments.isEmpty()) {            
            return printError(sf, "No adapter to switch to given");
        }
        
        List<BluetoothAdapter> foundAdapters = DeviceManager.getInstance().getAdapters();
        Optional<BluetoothAdapter> findFirst = foundAdapters.stream().filter(a -> a.getAddress().equals(_arguments.get(0)) || a.getDeviceName().equals(_arguments.get(0))).findFirst();
        if (!findFirst.isPresent()) {            
            return printError(sf, "No adapter with identifier '" + _arguments.get(0) + "' found.");
        }
        try {
            DeviceManager.getInstance().setDefaultAdapter(findFirst.get());            
            return printSuccess(sf, "Adapter successfully set to " + _arguments.get(0));
        } catch (BluezDoesNotExistsException _ex) {            
            return printError(sf, "Adapter with identifier '" + _arguments.get(0) + "' does not exist.");
        }
    }

    @Override
    public String getCommandName() {
        return "selectAdapter";
    }

    @Override
    public List<CommandArg> getCommandArgs() {
        CommandArg commandArg = new CommandArg("DeviceNameOrMac", true, false, () -> {
            List<BluetoothAdapter> adapters = DeviceManager.getInstance().getAdapters();
            
            List<ArgWithDescription> args = adapters.stream().map(a -> {
                return new ArgWithDescription(a.getDeviceName(), a.getAddress());
            }).collect(Collectors.toList());
            return args;
        });
        
        return Arrays.asList(commandArg);
    }

    @Override
    public String getCmdGroup() {
        return "Adapter Action";
    }

    @Override
    public String getDescription() {
        return "Set the bluetooth adapter to use for communication by either the adapters' MAC address or the adapters' name (e.g. hci0)";
    }
    
}
