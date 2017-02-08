package com.github.hypfvieh.control;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jline.terminal.Terminal;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

/**
 * Helper for visual formatting of shell output text.<br>
 * It takes care about colored terminal output.<br>
 * If no terminal is given (null), colored output will be suppressed.<br>
 *
 * @author spannm
 * @since v8.0.9 - 2016-12-16
 */
public final class ShellFormatter {

    private final Terminal terminal;

    public ShellFormatter() {
        this(null);
    }

    public ShellFormatter(Terminal _terminal) {
        terminal = _terminal;
    }

    /**
     * Center a string taking into account shell control characters.
     * 
     * @param _sb a specialized string builder that contains the plain string as well as a string with control
     *            characters, may be null
     * @param _size the int size of new String, negative treated as zero
     * @return centered String, {@code null} if null String input
     */
    public String center(AttributedStringBuilder _sb, int _size) {
        if (_sb == null || _size <= 0) {
            return null;
        }
        String centered = StringUtils.center(_sb.toString(), _size);
        return terminal == null ? centered : centered.replace(_sb.toString(), _sb.toAnsi(terminal));
    }

    /**
     * Center a string see {@link StringUtils#center(String, int)}.
     * 
     * @param _str
     * @param _size
     * @return
     */
    public String center(String _str, int _width) {
        return StringUtils.center(_str, _width);
    }

    /**
     * Left pad a String with spaces (' ') taking into account shell control characters.
     *
     * @param _sb a specialized string builder that contains the plain string as well as
     *            a string with control characters, may be null
     * @param _size the size to pad to
     * @return padded String or original String if no padding is necessary,
     *         {@code null} if null String input
     */
    public String leftPad(AttributedStringBuilder _sb, int _size) {
        return leftRightPadImpl(_sb, _size, true);
    }

    /**
     * Right pad a String with spaces (' ') taking into account shell control characters.
     *
     * @param _sb a specialized string builder that contains the plain string as well as
     *            a string with control characters, may be null
     * @param _size the size to pad to
     * @return padded String or original String if no padding is necessary,
     *         {@code null} if null String input
     */
    public String rightPad(AttributedStringBuilder _sb, int _size) {
        return leftRightPadImpl(_sb, _size, false);
    }

    String leftRightPadImpl(AttributedStringBuilder _sb, int _size, boolean _leftPad) {
        if (_sb == null) {
            return null;
        }
        int pads = _size - _sb.toString().length();
        if (pads <= 0) {
            return terminal == null ? _sb.toString() : _sb.toAnsi(terminal);
        } else if (_leftPad) {
            return StringUtils.repeat(' ', pads).concat(terminal == null ? _sb.toString() : _sb.toAnsi(terminal));
        } else { // rightpad
            return terminal == null ? _sb.toString().concat(StringUtils.repeat(' ', pads))
                    : _sb.toAnsi(terminal).concat(StringUtils.repeat(' ', pads));
        }
    }

    /**
     * Left-pad a string see {@link StringUtils#leftPad(String, int)}.
     * 
     * @param _str
     * @param _size
     * @return
     */
    public String leftPad(String _str, int _size) {
        return StringUtils.leftPad(_str, _size, ' ');
    }

    /**
     * Right-pad a string see {@link StringUtils#rightPad(String, int)}.
     * 
     * @param _str
     * @param _size
     * @return
     */
    public String rightPad(String _str, int _size) {
        return StringUtils.rightPad(_str, _size, ' ');
    }

    /**
     * Convert content of given {@link AttributedStringBuilder} to a String with escape-sequences for color support.<br>
     * This call will not do any kind of padding or formatting.
     *
     * @param _str
     * @param _size
     * @return
     */
    public String print(AttributedStringBuilder _sb) {
        return terminal == null ? _sb.toString() : _sb.toAnsi(terminal);
    }

    /**
     * Convert content of given {@link AttributedString} to a String with escape-sequences for color support.<br>
     * This call will not do any kind of padding or formatting.
     *
     * @param _str
     * @param _size
     * @return
     */
    public String print(AttributedString _sb) {
        return terminal == null ? _sb.toString() : _sb.toAnsi(terminal);
    }

    /**
     * Add the required escape-sequences to the string to print it in the given style to the terminal.<br>
     * This call will not do any kind of padding or formatting.<br>
     * If no terminal is given, given string is returned.
     *
     * @param _str
     * @param _style
     * @return
     */
    public String printInColor(String _str, AttributedStyle _style) {
        if (terminal == null) {
            return _str;
        }
        if (_str == null) {
            return null;
        }

        return new AttributedString(_str, _style).toAnsi(terminal);
    }

    public static boolean equalsAny(final CharSequence string, final CharSequence... searchStrings) {
        if (ArrayUtils.isNotEmpty(searchStrings)) {
            for (CharSequence next : searchStrings) {
                if (StringUtils.equals(string, next)) {
                    return true;
                }
            }
        }
        return false;
    }
}
