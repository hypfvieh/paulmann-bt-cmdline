package com.github.hypfvieh.control.commands;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

import com.github.hypfvieh.PaulmannDeviceController;
import com.github.hypfvieh.PaulmannDeviceController.DeviceDetails;
import com.github.hypfvieh.control.ShellFormatter;
import com.github.hypfvieh.control.commands.base.AbstractCommand;
import com.github.hypfvieh.control.commands.base.CommandArg;
import com.github.hypfvieh.control.commands.base.ICommand;
import com.github.hypfvieh.control.jline3.ArgWithDescription;
import com.github.hypfvieh.util.ConverterUtil;
import com.github.hypfvieh.util.TypeUtil;

public class ScanCommand extends AbstractCommand {

    @Override
    public String[] execute(List<String> _arguments, Terminal _terminal) throws InterruptedIOException {
        AttributedStringBuilder sb = new AttributedStringBuilder();
        ShellFormatter formatter = new ShellFormatter(_terminal);

        int timeout = 5;
        boolean showUnsupported = false;
        if (_arguments != null && !_arguments.isEmpty()) {
            timeout = TypeUtil.defaultIfNotInteger(_arguments.get(0), timeout);
            if (_arguments.size() >= 2) {
                showUnsupported = ConverterUtil.strToBool(_arguments.get(1));
            }
        }

        List<String> resultText = new ArrayList<>();
        try {
            PaulmannDeviceController.getInstance().scanForDevices(timeout);
            List<DeviceDetails> listAllRawDevices = PaulmannDeviceController.getInstance().listAllRawDevices(showUnsupported);
            if (listAllRawDevices.isEmpty()) {
                resultText.add("No devices found");
            } else {
                for (DeviceDetails devDetails : listAllRawDevices) {
                    resultText.add(devDetails.prettyToString());
                    resultText.add("");
                }
            }
        } catch (InterruptedException _ex) {
            sb.style(AttributedStyle.BOLD.foreground(AttributedStyle.RED));
            sb.append(wordAwareTrimToLength("Error while scanning for new devices: " + _ex.getMessage(), ICommand.DEFAULT_SHELL_WIDTH, 0));

            return new String[] {"", formatter.print(sb), ""};
        }

        return resultText.toArray(new String[]{});
    }


    @Override
    public String getDescription() {
        return "Scan for bluetooth devices. You can specify the scan time, if none is given 5 seconds will be used.";
    }


    @Override
    public List<CommandArg> getCommandArgs() {
        
        CommandArg scanTimeInSecs = new CommandArg("scanTimeInSeconds", true);
        CommandArg showUnsupported = new CommandArg("scanTimeInSeconds", true, true, () -> {
            return Arrays.asList(new ArgWithDescription("true", "Show unsupported"), new ArgWithDescription("false", "Do not show unsupported (default)"));
        });
                
        return Arrays.asList(scanTimeInSecs, showUnsupported);
    }


    @Override
    public String getCommandName() {
        return "scan";
    }

}
