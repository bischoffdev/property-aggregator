package blog.softwaretester.properties.propertysource;

import blog.softwaretester.properties.PropertyConverter;

import java.util.Map;

public final class SystemPropertiesSource implements PropertySource {
    @Override
    public Map<String, String> getProperties() {
        return PropertyConverter.propertiesToMap(System.getProperties());
    }
}
