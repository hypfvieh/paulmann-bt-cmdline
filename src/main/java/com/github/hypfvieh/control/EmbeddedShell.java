package com.github.hypfvieh.control;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReader.Option;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Attributes;
import org.jline.terminal.Attributes.OutputFlag;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hypfvieh.control.commands.base.CommandArg;
import com.github.hypfvieh.control.commands.base.ICommand;
import com.github.hypfvieh.control.commands.init.AbstractDeInitializationCommand;
import com.github.hypfvieh.control.commands.init.AbstractInitializationCommand;

public class EmbeddedShell implements Closeable {
    private final Logger       logger                   = LoggerFactory.getLogger(this.getClass());
    private LineReader         reader;
    private Terminal           terminal;

    private InputStream        inStream;
    private OutputStream       outStream;
    private OutputStream       errStream;
    
    private CommandRegistry    commandRegistry = CommandRegistry.getInstance();
    private AbstractDeInitializationCommand deInitCommand;
    
    public EmbeddedShell(InputStream _inStream, OutputStream _outStream, OutputStream _errStream) {
        if (_inStream == null) {
            throw new IllegalArgumentException("Input-Stream cannot be null");
        }
        if (_outStream == null) {
            throw new IllegalArgumentException("Output-Stream cannot be null");
        }

        if (_errStream == null) {
            _errStream = _outStream;
        }

        inStream = _inStream;
        outStream = _outStream;
        errStream = _errStream;
    }

    public void start(String prompt) throws IOException {

        if (reader == null) {
            throw new IOException("LineReader not initialized. Did you call initialize(_initCommand, _deInitCommand) first?");
        }
        
        try {
            String readline;
            while ((readline = reader.readLine(prompt)) != null) {
                handleUserInput(readline);
            }

        } catch (InterruptedIOException _ex) {
            throw _ex;
        } catch (EndOfFileException | UserInterruptException _ex) {
            // thrown to terminate session using CTRL+D/CTRL+C
            throw _ex;
        } catch (Exception _ex) {
            logger.error("Error in " + getClass().getSimpleName(), _ex);
        }
    }


    /**
     * Initialize the terminal and optionally run the given init command.
     * 
     * @param _initCommand
     * @throws IOException
     */
    public void initialize(AbstractInitializationCommand _initCommand, AbstractDeInitializationCommand _deinitCommand) throws IOException {
        createAndConfigureTerminal();

        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(CommandRegistry.getInstance().getCompleter())
                .build();

        reader.unsetOpt(Option.INSERT_TAB); // disable tab insertion when no letter was given (allows using tab to list all commands)
        reader.setOpt(Option.GROUP); // enable command grouping in menu

        if (_deinitCommand != null) {
            deInitCommand = _deinitCommand;
        }

        if (_initCommand != null) {
            printToConsole(_initCommand.execute(null, terminal));
        }
    }

    /**
     * Prints and flushes the stream to show output on console.
     * @param _lines
     */
    private void printToConsole(String... _lines) {
        if (_lines != null) {
            for (String line : _lines) {
                terminal.writer().println(line);
            }
            terminal.flush();
        }
    }

    
    /**
     * Creates a {@link Terminal} instance and configures the environment and key mapping settings.
     *
     * @throws IOException
     */
    void createAndConfigureTerminal() throws IOException {

        terminal = TerminalBuilder.builder()
                .system(true)
                .streams(getInStream(), getOutStream())
                .signalHandler(Terminal.SignalHandler.SIG_IGN)
                .nativeSignals(true)
                .build();

        Attributes attr = terminal.getAttributes();

        // enable output processing (required for all output flags)
        attr.setOutputFlag(OutputFlag.OPOST, true);
        // map newline to carriage return + newline
        attr.setOutputFlag(OutputFlag.ONLCR, true);

        terminal.setAttributes(attr);
    }


    /**
     * Method which will handle the command (strings) send by the client.
     *
     * @param _msg
     * @throws InterruptedIOException
     */
    private void handleUserInput(String _msg) throws InterruptedIOException {
        if (_msg == null) {
            return;
        }
        _msg = StringUtils.trim(_msg);

        // parse given command
        String[] split = StringUtils.split(_msg, " ");
        if ((split != null) && (split.length >= 1)) {
            List<String> argList = new ArrayList<>();
            if (split.length > 1) {
                List<String> splitArgsList = Arrays.asList(split);
                argList = splitArgsList.subList(1, splitArgsList.size());
            }

            String[] result = null;
            Map<String, ICommand> registeredCommands = CommandRegistry.getInstance().getRegisteredCommands();
            if (registeredCommands.containsKey(split[0])) {
                ICommand iCommand = registeredCommands.get(split[0]);
                List<CommandArg> requiredArgs = iCommand.getCommandArgs().stream().filter(a -> !a.isRequired()).collect(Collectors.toList());
                if (requiredArgs.size() > split.length -1) {
                    ShellFormatter sf = new ShellFormatter(terminal);
                    printToConsole(sf.printInColor("Arguments missing, expecting " + requiredArgs.size() + " but got " + (split.length-1), AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)),
                            sf.printInColor("These arguments are required: " + StringUtils.join(requiredArgs, ", "), AttributedStyle.DEFAULT.foreground(AttributedStyle.RED)));
                } else {
                    result = registeredCommands.get(split[0]).execute(argList, terminal);
                }
            } else {
                printToConsole("Unknown command: " + split[0]);
            }

            printToConsole(result);

        }
    }

    public InputStream getInStream() {
        return inStream;
    }

    public OutputStream getOutStream() {
        return outStream;
    }

    public OutputStream getErrStream() {
        return errStream;
    }

    @Override
    public void close() throws IOException {
        if (deInitCommand != null) {
            printToConsole(deInitCommand.execute(null, terminal));
        }
        terminal.close();
    }

    public void registerCommand(ICommand _command) {
        if (terminal == null) {
            throw new RuntimeException("Tried to register command " + _command.getClass() + " before shell initialization!");
        }
        commandRegistry.registerCommand(_command);
    }
}
