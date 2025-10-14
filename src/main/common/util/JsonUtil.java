package common.util;

import java.util.Map;

public class JsonUtil {
    public static String toJson(Map<String, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        int i=0;
        for (var e: map.entrySet()) {
            if (i++>0) sb.append(',');
            sb.append('"').append(e.getKey()).append('"').append(':');
            Object v = e.getValue();
            if (v instanceof Iterable) {
                sb.append('[');
                int j=0;
                for (Object o: (Iterable<?>) v) {
                    if (j++>0) sb.append(',');
                    sb.append('"').append(o.toString()).append('"');
                }
                sb.append(']');
            } else {
                sb.append('"').append(String.valueOf(v)).append('"');
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
