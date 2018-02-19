package com.github.hypfvieh.control.commands.base;

import java.io.InterruptedIOException;
import java.util.List;

import org.jline.terminal.Terminal;

public interface ICommand {

    public static final int DEFAULT_SHELL_WIDTH = 105; // minimum characters per line if not overridden by ENV_TERM

    public static final String CMDGRP_GENERAL = "General";

    public static final String NL = System.getProperty("line.separator");
    
    /**
     * Command which will be executed.
     * To terminate the remote shell session throw {@link InterruptedIOException}.
     *
     * @param _arguments
     * @param _terminal virtual terminal, required for shell formatting
     * @param _deviceController
     * @return
     * @throws InterruptedIOException
     */
    String[] execute(List<String> _arguments, Terminal _terminal) throws InterruptedIOException;

    /**
     * The command name (the string you will type to execute the command).<br>
     * Should be no longer than 20 characters!
     * @return
     */
    String getCommandName();

    /**
     * Aliases of this command.
     * This aliases can be used as 'shortcut' or alternative name for this command.
     *
     * @return defaults to empty array
     */
    default String[] getCommandAliases() {
        return new String[] {};
    }

    /**
     * Description of the command.
     * (what does the command do?).
     *
     * @return defaults to "No description available"
     */
    default String getDescription() {
        return "No description available";
    }

    /**
     * List of arguments and the possible values, used to create proper completer.
     *
     * @return defaults to no arguments
     */
    default List<CommandArg> getCommandArgs() {
        return CommandArg.NO_ARGS;
    }

    /**
     * Group where this command belongs to.
     *
     * @return defaults to no group
     */
    default String getCmdGroup() {
        return CMDGRP_GENERAL;
    }

    /**
     * Extended help text displayed when using "help commandName".
     * 
     * @param _terminal
     * @return defaults to 'No additional help available.'
     */
    default String[] getHelpText(Terminal _terminal) {
        return new String[] {"No additional help available."};
    }
   
}
