package blog.softwaretester.properties.propertysource;

import blog.softwaretester.properties.PropertyConverter;

import java.util.Map;
import java.util.Properties;

public final class EnvironmentPropertiesSource implements PropertySource {
    @Override
    public Map<String, String> getProperties() {
        Properties properties = new Properties();
        properties.putAll(System.getenv());
        return PropertyConverter.propertiesToMap(properties);
    }
}
