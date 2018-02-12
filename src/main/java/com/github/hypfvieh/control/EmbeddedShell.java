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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hypfvieh.control.commands.IRemoteCommand;

public class EmbeddedShell implements Closeable {
    private final Logger             logger                   = LoggerFactory.getLogger(this.getClass());
    private LineReader         reader;
    private Terminal           terminal;

    private InputStream        inStream;
    private OutputStream       outStream;
    private OutputStream       errStream;

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

        try {
            createAndConfigureTerminal();

            reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(CommandRegistry.getInstance().getCompleter())
                    .build();

            reader.unsetOpt(Option.INSERT_TAB); // disable tab insertion when no letter was given (allows using tab to list all commands)
            reader.setOpt(Option.GROUP); // enable command grouping in menu

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
     * Creates a {@link Terminal} instance and configures the environment and key mapping settings.
     *
     * @throws IOException
     */
    void createAndConfigureTerminal() throws IOException {

        terminal = TerminalBuilder.builder()
                .system(false)
                .streams(getInStream(), getOutStream())
                .signalHandler(Terminal.SignalHandler.SIG_IGN)
                .nativeSignals(true)
                .build();

//        String envCols = getEnvironment().getEnv().get(Environment.ENV_COLUMNS);
//        String envRows = getEnvironment().getEnv().get(Environment.ENV_LINES);
//        if (envCols != null && envRows != null) {
//            terminal.setSize(new Size(Integer.parseInt(envCols),
//                Integer.parseInt(envRows)));
//        }

        Attributes attr = terminal.getAttributes();
//        for (Map.Entry<PtyMode, Integer> e : getEnvironment().getPtyModes().entrySet()) {
//            switch (e.getKey()) {
//                case VINTR:
//                    attr.setControlChar(ControlChar.VINTR, e.getValue());
//                    break;
//                case VQUIT:
//                    attr.setControlChar(ControlChar.VQUIT, e.getValue());
//                    break;
//                case VERASE:
//                    attr.setControlChar(ControlChar.VERASE, e.getValue());
//                    break;
//                case VKILL:
//                    attr.setControlChar(ControlChar.VKILL, e.getValue());
//                    break;
//                case VEOF:
//                    attr.setControlChar(ControlChar.VEOF, e.getValue());
//                    break;
//                case VEOL:
//                    attr.setControlChar(ControlChar.VEOL, e.getValue());
//                    break;
//                case VEOL2:
//                    attr.setControlChar(ControlChar.VEOL2, e.getValue());
//                    break;
//                case VSTART:
//                    attr.setControlChar(ControlChar.VSTART, e.getValue());
//                    break;
//                case VSTOP:
//                    attr.setControlChar(ControlChar.VSTOP, e.getValue());
//                    break;
//                case VSUSP:
//                    attr.setControlChar(ControlChar.VSUSP, e.getValue());
//                    break;
//                case VDSUSP:
//                    attr.setControlChar(ControlChar.VDSUSP, e.getValue());
//                    break;
//                case VREPRINT:
//                    attr.setControlChar(ControlChar.VREPRINT, e.getValue());
//                    break;
//                case VWERASE:
//                    attr.setControlChar(ControlChar.VWERASE, e.getValue());
//                    break;
//                case VLNEXT:
//                    attr.setControlChar(ControlChar.VLNEXT, e.getValue());
//                    break;
//                /*
//                case VFLUSH:
//                    attr.setControlChar(ControlChar.VMIN, e.getValue());
//                    break;
//                case VSWTCH:
//                    attr.setControlChar(ControlChar.VTIME, e.getValue());
//                    break;
//                */
//                case VSTATUS:
//                    attr.setControlChar(ControlChar.VSTATUS, e.getValue());
//                    break;
//                case VDISCARD:
//                    attr.setControlChar(ControlChar.VDISCARD, e.getValue());
//                    break;
//                case ECHO:
//                    attr.setLocalFlag(LocalFlag.ECHO, e.getValue() != 0);
//                    break;
//                case ICANON:
//                    attr.setLocalFlag(LocalFlag.ICANON, e.getValue() != 0);
//                    break;
//                case ISIG:
//                    attr.setLocalFlag(LocalFlag.ISIG, e.getValue() != 0);
//                    break;
//                case ICRNL:
//                    attr.setInputFlag(InputFlag.ICRNL, e.getValue() != 0);
//                    break;
//                case INLCR:
//                    attr.setInputFlag(InputFlag.INLCR, e.getValue() != 0);
//                    break;
//                case IGNCR:
//                    attr.setInputFlag(InputFlag.IGNCR, e.getValue() != 0);
//                    break;
//                case OCRNL:
//                    attr.setOutputFlag(OutputFlag.OCRNL, e.getValue() != 0);
//                    break;
//                case ONLCR:
//                    attr.setOutputFlag(OutputFlag.ONLCR, e.getValue() != 0);
//                    break;
//                case ONLRET:
//                    attr.setOutputFlag(OutputFlag.ONLRET, e.getValue() != 0);
//                    break;
//                case OPOST:
//                    attr.setOutputFlag(OutputFlag.OPOST, e.getValue() != 0);
//                    break;
//                default:
//                    break;
//            }
//        }

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
            Map<String, IRemoteCommand> registeredCommands = CommandRegistry.getInstance().getRegisteredCommands();
            if (registeredCommands.containsKey(split[0])) {
                result = registeredCommands.get(split[0]).execute(argList, terminal);
            } else {
                terminal.writer().println("Unknown command: " + split[0]);
                terminal.flush();
            }

            if (result != null) {
                for (String line : result) {
                    terminal.writer().println(line);
                }
                terminal.flush();
            }

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
        terminal.close();
    }


}
