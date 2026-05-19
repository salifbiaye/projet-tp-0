package util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static String quote(String value) {
        if (value == null) {
            return "null";
        }

        StringBuilder result = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"':
                    result.append("\\\"");
                    break;
                case '\\':
                    result.append("\\\\");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                default:
                    result.append(c);
            }
        }
        result.append('"');
        return result.toString();
    }

    public static Map<String, String> parseObject(String json) {
        Map<String, String> values = new HashMap<>();
        if (json == null) {
            return values;
        }

        int i = 0;
        while (i < json.length()) {
            i = skipUntil(json, i, '"');
            if (i >= json.length()) {
                break;
            }

            ParseResult key = readString(json, i);
            i = skipUntil(json, key.nextIndex, ':') + 1;
            while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
                i++;
            }

            String value;
            if (i < json.length() && json.charAt(i) == '"') {
                ParseResult parsedValue = readString(json, i);
                value = parsedValue.value;
                i = parsedValue.nextIndex;
            } else {
                int start = i;
                while (i < json.length() && json.charAt(i) != ',' && json.charAt(i) != '}') {
                    i++;
                }
                value = json.substring(start, i).trim();
            }

            values.put(key.value, value);
        }
        return values;
    }

    public static String readStringField(String json, String field) {
        return parseObject(json).get(field);
    }

    public static int readIntField(String json, String field, int defaultValue) {
        String value = readStringField(json, field);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static String[] readStringArrayField(String json, String field) {
        String pattern = "\"" + field + "\"";
        int fieldIndex = json.indexOf(pattern);
        if (fieldIndex < 0) {
            return new String[0];
        }

        int arrayStart = json.indexOf('[', fieldIndex);
        int arrayEnd = findArrayEnd(json, arrayStart);
        if (arrayStart < 0 || arrayEnd < 0) {
            return new String[0];
        }

        List<String> values = new ArrayList<>();
        int i = arrayStart + 1;
        while (i < arrayEnd) {
            while (i < arrayEnd && Character.isWhitespace(json.charAt(i))) {
                i++;
            }
            if (i < arrayEnd && json.charAt(i) == '"') {
                ParseResult parsed = readString(json, i);
                values.add(parsed.value);
                i = parsed.nextIndex;
            } else {
                i++;
            }
        }
        return values.toArray(new String[0]);
    }

    public static Map<String, String> parseQuery(String query) {
        Map<String, String> values = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return values;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=", 2);
            String key = decode(parts[0]);
            String value = parts.length > 1 ? decode(parts[1]) : "";
            values.put(key, value);
        }
        return values;
    }

    private static int skipUntil(String text, int start, char target) {
        int i = start;
        while (i < text.length() && text.charAt(i) != target) {
            i++;
        }
        return i;
    }

    private static ParseResult readString(String json, int startQuote) {
        StringBuilder value = new StringBuilder();
        int i = startQuote + 1;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                switch (next) {
                    case 'n':
                        value.append('\n');
                        break;
                    case 'r':
                        value.append('\r');
                        break;
                    case 't':
                        value.append('\t');
                        break;
                    default:
                        value.append(next);
                }
                i += 2;
            } else if (c == '"') {
                return new ParseResult(value.toString(), i + 1);
            } else {
                value.append(c);
                i++;
            }
        }
        return new ParseResult(value.toString(), i);
    }

    private static int findArrayEnd(String json, int arrayStart) {
        boolean inString = false;
        boolean escaped = false;
        for (int i = arrayStart + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escaped) {
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else if (c == '"') {
                inString = !inString;
            } else if (!inString && c == ']') {
                return i;
            }
        }
        return -1;
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    private static class ParseResult {
        private final String value;
        private final int nextIndex;

        private ParseResult(String value, int nextIndex) {
            this.value = value;
            this.nextIndex = nextIndex;
        }
    }
}
