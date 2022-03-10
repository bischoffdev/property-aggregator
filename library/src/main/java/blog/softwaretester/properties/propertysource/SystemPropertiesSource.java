package blog.softwaretester.properties.propertysource;

import blog.softwaretester.properties.propertysource.PropertySource;

import java.util.Properties;

public final class SystemPropertiesSource implements PropertySource {
    @Override
    public Properties getProperties() {
        return System.getProperties();
    }
}
