package blog.softwaretester.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Used to convert properties to maps of strings.
 */
public final class PropertyConverter {
    private PropertyConverter() {
    }

    /**
     * Convert {@link Properties} to a string map.
     * @param properties The source {@link Properties}.
     * @return A string map representation of the {@link Properties}.
     */
    public static Map<String, String> propertiesToMap(
            final Properties properties) {
        return properties.entrySet().stream().collect(
                Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        e -> String.valueOf(e.getValue()),
                        (prev, next) -> next, HashMap::new
                ));
    }
}
