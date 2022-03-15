package blog.softwaretester.properties.propertysource;

import java.util.Map;

/**
 * The interface for all property sources.
 */
public interface PropertySource {
    /**
     * Get a string map containing all properties from the
     * respective {@link PropertySource}.
     *
     * @return The string map.
     */
    Map<String, String> getProperties();
}
