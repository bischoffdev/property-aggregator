package blog.softwaretester.properties.propertysource;

import blog.softwaretester.properties.PropertyConverter;

import java.util.Map;

public final class SystemPropertiesSource extends PropertySource {
    /**
     * Constructor.
     *
     * @param loggingEnabled If true, logs are shown.
     */
    public SystemPropertiesSource(boolean loggingEnabled) {
        super(loggingEnabled);
        logInfo("Adding system properties.");
    }

    @Override
    public Map<String, String> getProperties() {
        return PropertyConverter.propertiesToMap(System.getProperties());
    }
}
