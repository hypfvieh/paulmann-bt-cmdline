package com.github.hypfvieh;

import org.jline.reader.EndOfFileException;
import org.jline.reader.UserInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hypfvieh.control.EmbeddedShell;
import com.github.hypfvieh.control.commands.OnOffSwitchCommand;
import com.github.hypfvieh.control.commands.ScanCommand;
import com.github.hypfvieh.control.commands.SelectAdapter;
import com.github.hypfvieh.control.commands.SetBrightnessCommand;
import com.github.hypfvieh.control.commands.SetDefaultDevicePassword;
import com.github.hypfvieh.control.commands.SetDevicePasswordCommand;
import com.github.hypfvieh.control.commands.SetRgbCommand;
import com.github.hypfvieh.control.commands.ShowDeviceDetailsCommand;
import com.github.hypfvieh.control.commands.ShowDevicesCommand;
import com.github.hypfvieh.control.commands.init.ShellDeInitializeCommand;
import com.github.hypfvieh.control.commands.init.ShellInitializeCommand;

public class BlueMain {

    public static void main(String[] _args) {
        Logger logger = LoggerFactory.getLogger(BlueMain.class);
        logger.debug("Initializing Shell");

        try(EmbeddedShell shell = new EmbeddedShell(System.in, System.out, System.err))  {
            // initialize the shell
            shell.initialize(new ShellInitializeCommand(), new ShellDeInitializeCommand());
            // register our commands
            
            // adapter commands
            shell.registerCommand(new SelectAdapter());
            shell.registerCommand(new OnOffSwitchCommand());
            shell.registerCommand(new ScanCommand());
            
            // device commands
            shell.registerCommand(new SetBrightnessCommand());
            shell.registerCommand(new SetDevicePasswordCommand());
            shell.registerCommand(new SetDefaultDevicePassword());
            
            shell.registerCommand(new SetRgbCommand());
            shell.registerCommand(new ShowDeviceDetailsCommand());
            shell.registerCommand(new ShowDevicesCommand());
            
            // start shell
            shell.start("bleCmd > ");
        } catch (Exception _ex) {
            // EndOfFileException will occur when using CTRL+D to exit shell
            // UserInterruptException will occur when using CTRL+C
            if (! (_ex instanceof EndOfFileException) && !(_ex instanceof UserInterruptException)) { 
                System.err.println("Error: (" + _ex.getClass().getSimpleName() + "): " + _ex.getMessage());
            }            
        } finally {
            PaulmannDeviceController.getInstance().deinitialize();
            logger.debug("Deinitializing Shell");
        }
    }
}
