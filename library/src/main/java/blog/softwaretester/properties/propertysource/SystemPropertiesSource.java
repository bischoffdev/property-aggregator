package blog.softwaretester.properties.propertysource;

import java.util.Properties;

public final class SystemPropertiesSource implements PropertySource {
    @Override
    public Properties getProperties() {
        return System.getProperties();
    }
}
