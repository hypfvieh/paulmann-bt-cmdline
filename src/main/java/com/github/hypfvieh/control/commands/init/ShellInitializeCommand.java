package com.github.hypfvieh.control.commands.init;

import static org.jline.utils.AttributedStyle.BLUE;
import static org.jline.utils.AttributedStyle.DEFAULT;
import static org.jline.utils.AttributedStyle.YELLOW;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;

import org.freedesktop.dbus.exceptions.DBusException;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;

import com.github.hypfvieh.bluetooth.DeviceManager;
import com.github.hypfvieh.bluetooth.wrapper.BluetoothAdapter;
import com.github.hypfvieh.control.ShellFormatter;

public class ShellInitializeCommand extends AbstractInitializationCommand {

    @Override
    protected String[] execute(Terminal _terminal) throws InterruptedIOException {
        List<String> results = new ArrayList<>();
        try {
            // initialize dbus connection to system bus
            DeviceManager.createInstance(false);
            BluetoothAdapter btAdapter = DeviceManager.getInstance().getAdapter();
            if (btAdapter == null) {
                throw new InterruptedIOException("No bluetooth adapter installed");
            }
            
            ShellFormatter sf = new ShellFormatter(_terminal);
            AttributedStringBuilder sb = new AttributedStringBuilder();
            
            sb.append("Using ");
            sb.style(DEFAULT.foreground(YELLOW));
            sb.append(btAdapter.getDeviceName());
            sb.style(DEFAULT.foregroundDefault());
            sb.style(DEFAULT.foreground(BLUE));
            sb.append(" [");
            sb.style(DEFAULT.foreground(YELLOW));
            sb.append(btAdapter.getAddress());
            sb.style(DEFAULT.foreground(BLUE));
            sb.append("]");
            sb.style(DEFAULT.foregroundDefault());
            sb.append(" as bluetooth adapter");
            
            results.add(sf.print(sb));

            return results.toArray(new String[0]);
        } catch (DBusException _ex) { // if DBUS failed, we are screwed!
            throw new RuntimeException(_ex);
        }
    }

}
