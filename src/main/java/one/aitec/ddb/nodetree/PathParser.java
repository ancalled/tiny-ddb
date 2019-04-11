package one.aitec.ddb.nodetree;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class PathParser {
    public static final String DEFAULT_TEMPLATE = "/{class}/{id}";
    private static final Pattern VAR_PATTERN = Pattern.compile("\\{([0-9a-zA-Z_]+)\\}");

    public static List<String> parsePath(String path) {
        return parsePath(path, null);
    }

    public static List<String> parsePath(String path, Object item) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String[] split = path.split("/");
        for (int i = 0; i < split.length; i++) {
            Matcher m = VAR_PATTERN.matcher(split[i]);
            if (m.matches()) {
                String fieldName = m.group(1);
                String replacement;
                if ("class".equals(fieldName)) {
                    replacement = item.getClass().getSimpleName().toLowerCase();
                } else if ("random".equals(fieldName)) {
                    replacement = UUID.randomUUID().toString();
                } else {
                    Object value = ReflectionUtils.fieldValue(item, fieldName);
                    replacement = value != null ? value.toString() : "";
                }
                split[i] = replacement;
            }
        }
        return new ArrayList<>(asList(split));
    }
}
