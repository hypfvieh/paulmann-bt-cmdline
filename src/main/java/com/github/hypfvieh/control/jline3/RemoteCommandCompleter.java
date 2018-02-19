package com.github.hypfvieh.control.jline3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.utils.AttributedString;

import com.github.hypfvieh.control.commands.base.ICommand;

public class RemoteCommandCompleter implements Completer {
    protected final Collection<Candidate> candidates = new ArrayList<>();

    public RemoteCommandCompleter() {

    }

    public RemoteCommandCompleter(ICommand... _strings) {
        this(Arrays.asList(_strings));
    }

    public RemoteCommandCompleter(Collection<ICommand> _strings) {
        for (ICommand cmd : _strings) {
            candidates.add(new Candidate(AttributedString.stripAnsi(cmd.getCommandName()), cmd.getCommandName(), cmd.getCmdGroup(), cmd.getDescription(), null, null, true));
        }
    }

    public void addCommand(ICommand _cmd) {
        if (_cmd != null && !StringUtils.isBlank(_cmd.getCommandName())) {
            candidates.add(new Candidate(AttributedString.stripAnsi(_cmd.getCommandName()), _cmd.getCommandName(), _cmd.getCmdGroup(), _cmd.getDescription(), null, null, true));
        }
    }

    public void complete(LineReader _reader, final ParsedLine _commandLine, final List<Candidate> _candidates) {
        assert _commandLine != null;
        assert _candidates != null;
        _candidates.addAll(this.candidates);
    }

}
