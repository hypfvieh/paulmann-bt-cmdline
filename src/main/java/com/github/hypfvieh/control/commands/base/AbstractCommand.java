package com.github.hypfvieh.control.commands.base;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hypfvieh.control.ShellFormatter;
import com.github.hypfvieh.util.StringUtil;

public abstract class AbstractCommand implements ICommand {

    protected static final String HDR_IDX        = "Idx";

    protected static final String HDR_SPACER     = "  ";

    protected static final int    HDR_IDX_LEN    = 3;
    protected static final int    HDR_SPACER_LEN = HDR_SPACER.length();

    protected static final String MATCH_ALL  = "all";

    //CHECKSTYLE:OFF
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    //CHECKSTYLE:ON

    protected String trimToLength(String _str, int _len, int _indentFirst) {
        if (!StringUtils.isBlank(_str) && _str.length() > _len) {
            List<String> splitEqually = StringUtil.splitEqually(_str, _len);
            _str = indent(_len, _indentFirst, splitEqually);
        }
        return StringUtils.trimToEmpty(_str);
    }

    protected String wordAwareTrimToLength(String _str, int _len, int _indentFirst) {

        if (!StringUtils.isBlank(_str) && _str.length() > _len) {
            List<String> smartWordSplit = StringUtil.smartWordSplit(_str, _len);
            _str = indent(_len, _indentFirst, smartWordSplit);
        }

        return StringUtils.trimToEmpty(_str);
    }

    private String indent(int _len, int _indentFirst, List<String> splitEqually) {
        String str = "";
        for (int i = 0; i < splitEqually.size(); i++) {
            if (i > 0) {
                str += StringUtils.repeat(" ", _indentFirst);
            }
            str += StringUtils.rightPad(splitEqually.get(i).trim(), _len) + "\r\n";
        }
        str = str.replaceFirst("\r\n$", "");
        return str;
    }

    protected String[] printError(ShellFormatter _formatter, String _msg) {
        AttributedStringBuilder sb = new AttributedStringBuilder();

        sb.style(AttributedStyle.BOLD.foreground(AttributedStyle.RED));
        sb.append(_msg);

        return new String[] {"", _formatter.print(sb), ""};
    }

    protected String[] printSuccess(ShellFormatter _formatter, String _msg) {
        AttributedStringBuilder sb = new AttributedStringBuilder();

        sb.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
        sb.append(_msg);

        return new String[] {"", _formatter.print(sb), ""};
    }

    /**
     * Returns 'Y' or 'N' for a boolean value.
     * @param _bool any boolean value
     * @return short String representation
     */
    protected final String booleanToString(boolean _bool) {
        return _bool ? "Y" : "N";
    }
}
