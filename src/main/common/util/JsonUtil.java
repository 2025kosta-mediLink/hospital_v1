package common.util;

import java.util.Map;

public class JsonUtil {
    public static String toJson(Map<String, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        int i = 0;
        for (var e : map.entrySet()) {
            if (i++ > 0) sb.append(',');
            sb.append('"').append(e.getKey()).append('"').append(':');
            Object v = e.getValue();

            if (v == null) {
                sb.append("null");
            } else if (v instanceof Boolean || v instanceof Number) {
                sb.append(v.toString()); // 따옴표 없이
            } else if (v instanceof Iterable) {
                sb.append('[');
                int j = 0;
                for (Object o : (Iterable<?>) v) {
                    if (j++ > 0) sb.append(',');
                    appendValue(sb, o);
                }
                sb.append(']');
            } else {
                appendValue(sb, v);
            }
        }
        sb.append('}');
        return sb.toString();
    }

    private static void appendValue(StringBuilder sb, Object v) {
        if (v == null) { sb.append("null"); return; }
        if (v instanceof Boolean || v instanceof Number) { sb.append(v.toString()); return; }
        sb.append('"').append(escape(String.valueOf(v))).append('"');
    }

    private static String escape(String s) {
        // 최소한의 escape
        return s.replace("\\","\\\\").replace("\"","\\\"");
    }
}