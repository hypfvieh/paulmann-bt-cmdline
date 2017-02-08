package com.github.hypfvieh.control.jline3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.utils.AttributedString;

public class ArgumentWithDescriptionCompleter implements Completer {
    protected final Collection<Candidate> candidates = new ArrayList<>();

    public ArgumentWithDescriptionCompleter() {

    }

    public ArgumentWithDescriptionCompleter(ArgWithDescription... _argWithHelp) {
        this(Arrays.asList(_argWithHelp));
    }

    public ArgumentWithDescriptionCompleter(Collection<ArgWithDescription> _argWithHelp) {
        for (ArgWithDescription arg : _argWithHelp) {
            candidates.add(new Candidate(AttributedString.stripAnsi(arg.getArgument()), arg.getArgument(), null, arg.getHelp(), null, null, true));
        }
    }

    public void complete(LineReader _reader, final ParsedLine _commandLine, final List<Candidate> _candidates) {
        assert _commandLine != null;
        assert _candidates != null;
        _candidates.addAll(this.candidates);
    }

    public static class ArgWithDescription {
        private final String argument;
        private final String description;

        public ArgWithDescription(String _argument, String _desc) {
            argument = _argument;
            description = _desc;
        }

        public String getArgument() {
            return argument;
        }

        public String getHelp() {
            return description;
        }

    }
}
