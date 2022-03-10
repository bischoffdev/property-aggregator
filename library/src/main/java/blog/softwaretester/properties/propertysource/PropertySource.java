package blog.softwaretester.properties.propertysource;

import java.util.Properties;

/**
 * The interface for all property sources.
 */
public interface PropertySource {
    /**
     * Get a {@link Properties} instance containing all properties from the
     * respective {@link PropertySource}.
     *
     * @return The {@link Properties} instance.
     */
    Properties getProperties();
}
