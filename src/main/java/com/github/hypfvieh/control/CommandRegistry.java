package com.github.hypfvieh.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hypfvieh.control.commands.ExitCommand;
import com.github.hypfvieh.control.commands.base.AbstractCommand;
import com.github.hypfvieh.control.commands.base.CommandArg;
import com.github.hypfvieh.control.commands.base.ICommand;
import com.github.hypfvieh.control.commands.init.AbstractInitializationCommand;
import com.github.hypfvieh.control.jline3.AnsiStringSplit;
import com.github.hypfvieh.control.jline3.ArgumentWithDescriptionCompleter;
import com.github.hypfvieh.control.jline3.RemoteCommandCompleter;
import com.github.hypfvieh.formatter.TableColumnFormatter;

/**
 * Singleton which provides access to all supported remote commands (RMI commands).
 *
 * @author michaelisd
 * @since v1.0.0 - 2016-12-13
 */
public class CommandRegistry {

    private static final CommandRegistry INSTANCE = new CommandRegistry();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, ICommand> supportedCommands = new HashMap<>();

    /** Create the {@link AggregateCompleter} with an empty list to allow adding entries later on. */
    private final AggregateCompleter jlineCompleter = new AggregateCompleter(new ArrayList<>());

    /**
     * Hidden constructor for singleton pattern.
     */
    private CommandRegistry() {
        // always register the help and exit commands
        registerCommand(new BuiltinHelpCommand());
        registerCommand(new ExitCommand());
    }

    /**
     * Gives the one and only instance of CommandRegistry.
     *
     * @return
     */
    static CommandRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Register a new command in the command registry.
     *
     * @param _command
     */
    public void registerCommand(ICommand _command) {
        if (_command == null) {
            throw new IllegalArgumentException("Null is no valid command");
        }

        if (_command instanceof AbstractInitializationCommand) {
            logger.debug("Initialization command cannot be registered: {}", _command.getClass());
            return;
        }
        
        if (StringUtils.isBlank(_command.getCommandName())) {
            throw new IllegalArgumentException("Command '" + _command.getCommandName() + "' is no valid command name!");
        }
        if (supportedCommands.containsKey(_command.getCommandName())) {
            throw new IllegalArgumentException("Command '" + _command.getCommandName() + "' already registered!");
        }
        supportedCommands.put(_command.getCommandName(), _command);

        if (_command.getCommandAliases() != null) {
            for (String alias : _command.getCommandAliases()) {
                if (!supportedCommands.containsKey(alias)) {
                    supportedCommands.put(alias, _command);
                }
            }
        }

        // Completer stuff:
        // This is a bit tricky if you want to have argument completion and command completion.
        // To achieve this behavior, we first have to create a completer for the command itself.
        // Then we need a completer for each argument we want to be completed (one argument = one completer).
        // The command completer and all the arguments completer have to placed inside a ArgumentCompleter object.
        // To allow more than one command, the ArgumentCompleter object has to be put in a AggregateCompleter object.
        // The AggregateCompleter is than bound to the LineReader of the terminal

        // completer for the command
        RemoteCommandCompleter cmdCompleter = new RemoteCommandCompleter(_command);

        // completer for the commands arguments
        ArgumentCompleter argCompleter = new ArgumentCompleter();

        // add the command completer to the argument completer (first parameter is the command)
        argCompleter.getCompleters().add(cmdCompleter);

        // add all completers specified by the command for this command (each completer is responsible for one argument
        // of the command)
        
        if (_command.getCommandArgs() != null && !_command.getCommandArgs().isEmpty()) {
            for (CommandArg arg : _command.getCommandArgs()) {
                if (arg.isRequired()) {
                    argCompleter.getCompleters().add(new ArgumentWithDescriptionCompleter(arg.getArguments()));
                } else if (arg.getArguments() != null && !arg.getArguments().isEmpty()) {
                    argCompleter.getCompleters().add(new ArgumentWithDescriptionCompleter(arg.getArguments()));
                } else {
                    argCompleter.getCompleters().add(new NullCompleter());
                }
            }
            argCompleter.getCompleters().add(new NullCompleter());
        }

        // put the new argument completer in the AggregateCompleter
        jlineCompleter.getCompleters().add(argCompleter);
    }

    /**
     * Unregister a command.
     *
     * @param _commandExecutor
     */
    public void unregisterRemoteCommand(ICommand _commandExecutor) {
        if (_commandExecutor != null && !StringUtils.isBlank(_commandExecutor.getCommandName())) {
            unregisterRemoteCommand(_commandExecutor.getCommandName());
        } else {
            logger.debug("Cannot remove command, given command is either blank or null!");
        }
    }

    /**
     * Unregister a command by name.
     *
     * @param _commandName
     */
    public void unregisterRemoteCommand(String _commandName) {
        if (supportedCommands.containsKey(_commandName)) {
            supportedCommands.remove(_commandName);
            logger.debug("Removed registered command {} from command registry", _commandName);
        } else {
            logger.debug("Could not remove command {}, not registered", _commandName);
        }
    }

    /**
     * Returns a unmodifiable map of all registered commands.
     *
     * @return
     */
    public Map<String, ICommand> getRegisteredCommands() {
        return Collections.unmodifiableMap(supportedCommands);
    }

    /**
     * Get jLine completer for providing completion in terminals.
     *
     * @return
     */
    public Completer getCompleter() {
        return jlineCompleter;
    }

    /**
     * Built-in 'help' command to display all known commands.
     *
     * @author michaelisd
     * @since v1.0.0 - 2016-12-13
     */
    class BuiltinHelpCommand extends AbstractCommand {

        private static final String HDR_COMMAND = "Command";
        private static final String HDR_ALIASES = "Aliases";
        private static final String HDR_ARGUMENTS = "Arguments";
        private static final String HDR_DESCRIPTION = "Description";

        private static final String HDR_SPACER = " ";

        private final int HDR_COMMAND_LEN = 20;
        private final int HDR_ALIASES_LEN = 15;
        private final int HDR_ARGUMENTS_LEN = 29;

        private final int HDR_DESCRIPTION_LEN = ICommand.DEFAULT_SHELL_WIDTH - HDR_COMMAND_LEN - HDR_ALIASES_LEN
                - HDR_ARGUMENTS_LEN - (3 * HDR_SPACER.length());

        @Override
        public String[] execute(List<String> _arguments, Terminal _terminal) {
            // show additional help if argument is set and contains a valid command
            List<String> text = new ArrayList<>();
            if (_arguments != null && _arguments.size() == 1) {
                if (supportedCommands.containsKey(_arguments.get(0))) {
                    return supportedCommands.get(_arguments.get(0)).getHelpText(_terminal);
                } else {
                    text.add("");
                    text.add("Unknown command " + _arguments.get(0));
                }
            }

            TableColumnFormatter tableColumnFormatter = new TableColumnFormatter(AnsiStringSplit::smartWordSplit,
                    HDR_SPACER.charAt(0), HDR_COMMAND_LEN, HDR_ALIASES_LEN, HDR_ARGUMENTS_LEN, HDR_DESCRIPTION_LEN);
            ShellFormatter formatter = new ShellFormatter(_terminal);

            text.add("");
            text.add("Supported Commands:");
            text.add(tableColumnFormatter.fillLine('='));

            text.add(tableColumnFormatter.formatLine(HDR_COMMAND, HDR_ALIASES, HDR_ARGUMENTS, HDR_DESCRIPTION));
            text.add(tableColumnFormatter.fillLine('-'));

            for (Entry<String, ICommand> entry : supportedCommands.entrySet()) {

                String[] aliases = entry.getValue().getCommandAliases();

                if (aliases != null && Arrays.asList(aliases).contains(entry.getKey())) { // skip adding aliases as
                                                                                          // commands
                    continue;
                }

                AttributedStringBuilder commandName = new AttributedStringBuilder();
                commandName.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN));
                commandName.append(entry.getKey());

                // take care of color
                String formattedCommandName = formatter.rightPad(commandName, HDR_COMMAND_LEN);

                String cAliases = "";
                AttributedStyle yellow = AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW);
                if (aliases != null && aliases.length > 0) {
                    for (int i = 0; i < aliases.length; i++) {
                        if (i == 0) {
                            cAliases += new AttributedString("[", yellow).toAnsi(_terminal);
                        }

                        cAliases += new AttributedString(aliases[i],
                                AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE)).toAnsi(_terminal);

                        if (i == aliases.length - 1) {
                            cAliases += new AttributedString("]", yellow).toAnsi(_terminal);
                        } else {
                            cAliases += new AttributedString(", ", yellow).toAnsi(_terminal);
                        }
                    }
                }

                String paddedCommand = StringUtils.rightPad(commandName.toString(), HDR_COMMAND_LEN);
                String line = tableColumnFormatter.formatLine(paddedCommand, cAliases,
                        StringUtils.join(", ", entry.getValue().getCommandArgs()), entry.getValue().getDescription());

                line = line.replace(paddedCommand, formattedCommandName);

                text.add(line);

                text.add("");
            }

            text.add("Use help [command] to get additional help for each command");

            return text.toArray(new String[] {});
        }

        @Override
        public String getCommandName() {
            return "help";
        }

        @Override
        public String getDescription() {
            return "Shows this help. Specifing a command name as first argument shows more help for the given command.";
        }

        @Override
        public String[] getCommandAliases() {
            return new String[] { "h", "?", "man" };
        }

        @Override
        public List<CommandArg> getCommandArgs() {
            return Arrays.asList(new CommandArg("command", true));
        }

        @Override
        public String getCmdGroup() {
            return ICommand.CMDGRP_GENERAL;
        }

    }

}
