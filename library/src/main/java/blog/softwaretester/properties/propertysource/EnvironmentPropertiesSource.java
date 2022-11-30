package blog.softwaretester.properties.propertysource;

import blog.softwaretester.properties.PropertyConverter;

import java.util.Map;
import java.util.Properties;

public final class EnvironmentPropertiesSource extends PropertySource {
    /**
     * Constructor.
     *
     * @param showLogs If true, logs are shown.
     */
    public EnvironmentPropertiesSource(final boolean showLogs) {
        super(showLogs);
        logInfo("Adding environment properties.");
    }

    @Override
    public Map<String, String> getProperties() {
        Properties properties = new Properties();
        properties.putAll(System.getenv());
        return PropertyConverter.propertiesToMap(properties);
    }
}
