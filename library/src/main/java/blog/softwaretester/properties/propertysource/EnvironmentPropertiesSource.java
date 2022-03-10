package blog.softwaretester.properties.propertysource;

import java.util.Properties;

public final class EnvironmentPropertiesSource implements PropertySource {
    @Override
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.putAll(System.getenv());
        return properties;
    }
}
