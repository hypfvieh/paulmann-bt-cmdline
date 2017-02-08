package com.github.hypfvieh.control.jline3;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

import com.github.hypfvieh.formatter.StringSplitOperator;
import com.github.hypfvieh.util.StringUtil;

/**
 * Splits an ansi string (containing terminal control characters) into substrings
 * of up to the given length.
 * Internally uses a word split algorithm to produce human-readable split results.
 * Compatible with the {@link StringSplitOperator} functional interface.
 *
 * @author spannm
 * @since v8.0.9 - 2017-01-18
 */
public class AnsiStringSplit {

    /**
     * Hidden utility constructor.
     */
    private AnsiStringSplit() {
    }

    public static List<String> smartWordSplit(String _ansiString, int _len) {
        return smartWordSplit(_ansiString, _len, true);
    }

    public static List<String> smartWordSplit(String _ansiString, int _len, boolean _rightPad) {
        AttributedString wholeAttrStr = AttributedString.fromAnsi(_ansiString);
        String wholeRawStr = AttributedString.stripAnsi(_ansiString);
        List<String> rawStrList = StringUtil.smartWordSplit(wholeRawStr, _len);
        List<String> ansiStrList = new ArrayList<>();

        for (String rawSubStr : rawStrList) {
            // find raw substring in the complete string, after trimming the substring as it may be right-padded (with space).
            int wordBgnIdx = wholeRawStr.indexOf(rawSubStr.trim());
            StringBuilder sb = new StringBuilder();
            // loop through sub string character by character
            for (int i = 0; i < rawSubStr.length(); i++) {
                AttributedStyle style = wholeAttrStr.styleAt(wordBgnIdx + i);
                sb.append(new AttributedString(rawSubStr.substring(i, i + 1), style).toAnsi());
            }
            if (_rightPad && rawSubStr.length() < _len) {
                sb.append(StringUtils.repeat(' ', _len - rawSubStr.length()));
            }

            ansiStrList.add(sb.toString());
        }

        return ansiStrList;
    }

}
