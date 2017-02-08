package com.github.hypfvieh.control.commands;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;

import org.jline.reader.Completer;
import org.jline.terminal.Terminal;

public interface IRemoteCommand {

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
     * Expected arguments as String (used for display in help).
     *
     * @return defaults to no arguments
     */
    default String getCommandArgs() {
        return "";
    }

    /**
     * Completer for arguments.
     * Each entry in the returned array should be a completer for one argument of the command.
     * You have to use the correct order to match your commands argument order, as the completers are used in array sort order!
     *
     * @return defaults to no arguments completion
     */
    default List<Completer> getArgCompleters() {
        return new ArrayList<>();
    }

    /**
     * Group where this command belongs to.
     *
     * @return defaults to no group
     */
    default String getCmdGroup() {
        return CMDGRP_GENERAL;
    }

    default String[] getHelpText(Terminal _terminal) {
        return new String[] {"No additional help available."};
    }
}
