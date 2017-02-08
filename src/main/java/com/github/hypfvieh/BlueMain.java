package com.github.hypfvieh;

import com.github.hypfvieh.control.EmbeddedShell;

public class BlueMain {

    // TODO: Allow choosing of bluetooth adapter if there are multiple adapters


    public static void main(String[] _args) {
        boolean stopLoop = false;

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
