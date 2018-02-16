package com.github.hypfvieh;

import org.freedesktop.dbus.AbstractPropertiesHandler;
import org.freedesktop.dbus.SignalAwareProperties.PropertiesChanged;
import org.jline.reader.EndOfFileException;
import org.slf4j.LoggerFactory;

import com.github.hypfvieh.control.EmbeddedShell;

public class BlueMain {

    // TODO: Allow choosing of bluetooth adapter if there are multiple adapters


    public static void main(String[] _args) {
        LoggerFactory.getLogger(BlueMain.class).debug("Initializing Shell");
        
        // Sample on how to use PropertiesChanged callback
        AbstractPropertiesHandler propertiesHandler = new AbstractPropertiesHandler() {
            @Override
            public void handle(PropertiesChanged _s) {
                if (_s.getPropertiesChanged() != null && !_s.getPropertiesChanged().isEmpty()) {
                  //  System.out.println("Got changed properties: " + _s.getPropertiesChanged());
                }
                if (_s.getPropertiesRemoved() != null && !_s.getPropertiesRemoved().isEmpty()) {
                  //  System.out.println("Properties removed: " + _s.getPropertiesRemoved());
                }
            }
        };

        PaulmannDeviceController.getInstance().registerPropertyHandler(propertiesHandler);

        try(EmbeddedShell shell = new EmbeddedShell(System.in, System.out, System.err))  {
            shell.start("bleCmd > ");
        } catch (Exception _ex) {
            if (! (_ex instanceof EndOfFileException)) { // EndOfFileException will occur when using CTRL+D to exit shell
                System.err.println("Error: " + _ex.getMessage());
            }            
        } finally {
            LoggerFactory.getLogger(BlueMain.class).debug("Deinitializing Shell");
            PaulmannDeviceController.getInstance().deinitialize();
        }

    }
}
