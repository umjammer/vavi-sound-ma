/*
 * https://github.com/but80/smaf825
 */

package vavi.sound.yamaha.smaf.util;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;


public class TextUtil {

    static Gson gson = new Gson().newBuilder().create();

    static final String indentRe = "(?m)^";

    public static String indent(String text, String indent) {
        if (text.isEmpty()) {
            return text;
        }
        return text.replaceAll(indentRe, indent);
    }

    public static String hex(byte[] stream) {
        if (stream.length == 0) {
            return "[]";
        }
        StringBuilder s = new StringBuilder();
        for (var b : stream) {
            s.append(" %02X".formatted(b));
        }
        return "[" + s.substring(1) + "]";
    }

    public static String escape(byte[] stream) {
        var j = gson.toJson(new String(stream));
        return j;
    }

    public static String zeroPadSliceToString(byte[] s) {
        var i = s.length;
        while (0 < i && s[i - 1] == 0) i--;
        return new String(s, 0, i);
    }

    public static String decodeShiftJIS(byte[] s) {
        var reader = new Scanner(new InputStreamReader(new ByteArrayInputStream(s), Charset.forName("Shift_JIS")));
        var result = new ArrayList<String>();
        while (reader.hasNextLine()) {
            var line = reader.nextLine();
            result.add(line);
        }
        return String.join("\n", result);
    }

    static final Pattern splitOptionalDataRe1 = Pattern.compile("([^\\,]|\\.)+");
    static final Pattern splitOptionalDataRe2 = Pattern.compile("\\.");

    public static Map<String, String> splitOptionalData(String s) {
        Map<String, String> result = new HashMap<>();

        Matcher matcher = splitOptionalDataRe1.matcher(s);

        while (matcher.find()) {
            String pair = matcher.group();
            String[] parts = pair.split(":", 2);

            if (parts.length == 2) {
                result.put(parts[0], unescapeValue(parts[1]));
            }
        }

        return result;
    }

    /** Helper method to replace escaped characters */
    private static String unescapeValue(String value) {
        return splitOptionalDataRe2.matcher(value).replaceAll(matchResult ->
                matchResult.group().substring(1)
        );
    }
}