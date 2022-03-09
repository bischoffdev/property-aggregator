package blog.softwaretester.properties.propertysource;

public class SystemProperties implements PropertySource {
    @Override
    public String getPropertyValue(String key) {
        return System.getProperties().getProperty(key);
    }
}
