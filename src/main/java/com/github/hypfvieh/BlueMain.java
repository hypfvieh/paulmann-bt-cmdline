package com.github.hypfvieh;

import org.freedesktop.dbus.AbstractPropertiesHandler;
import org.freedesktop.dbus.SignalAwareProperties.PropertiesChanged;

import com.github.hypfvieh.control.EmbeddedShell;

public class BlueMain {

    // TODO: Allow choosing of bluetooth adapter if there are multiple adapters


    public static void main(String[] _args) {
        boolean stopLoop = false;

        // Sample on how to use PropertiesChanged callback
        AbstractPropertiesHandler propertiesHandler = new AbstractPropertiesHandler() {
            @Override
            public void handle(PropertiesChanged _s) {
                if (_s.getPropertiesChanged() != null && !_s.getPropertiesChanged().isEmpty()) {
                    System.out.println("Got changed properties: " + _s.getPropertiesChanged());
                }
                if (_s.getPropertiesRemoved() != null && !_s.getPropertiesRemoved().isEmpty()) {
                    System.out.println("Properties removed: " + _s.getPropertiesRemoved());
                }
            }
        };

        PaulmannDeviceController.getInstance().registerPropertyHandler(propertiesHandler);

        while (!stopLoop) {
            try(EmbeddedShell shell = new EmbeddedShell(System.in, System.out, System.err))  {
                shell.start("bleCmd > ");
            } catch (Exception _ex) {
                stopLoop = true;
                System.err.println(_ex.getMessage());
                // system exit is not the proper way, but jline does not stop its pump-thread when calling 'close()'
                System.exit(0);
            }
        }

    }
}
