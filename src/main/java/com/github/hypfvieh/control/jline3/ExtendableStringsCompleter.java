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

public class ExtendableStringsCompleter implements Completer {
    protected final Collection<Candidate> candidates = new ArrayList<>();

    public ExtendableStringsCompleter() {

    }

    public ExtendableStringsCompleter(String... _strings) {
        this(Arrays.asList(_strings));
    }

    public ExtendableStringsCompleter(Collection<String> _strings) {
        for (String string : _strings) {
            candidates.add(new Candidate(AttributedString.stripAnsi(string), string, null, null, null, null, true));
        }
    }

    public void addString(String _str) {
        if (!StringUtils.isBlank(_str)) {
            candidates.add(new Candidate(AttributedString.stripAnsi(_str), _str, null, "TODO", null, null, true));
        }
    }

    public void complete(LineReader _reader, final ParsedLine _commandLine, final List<Candidate> _candidates) {
        assert _commandLine != null;
        assert _candidates != null;
        _candidates.addAll(this.candidates);
    }

}
