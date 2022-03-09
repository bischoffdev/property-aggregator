package blog.softwaretester.properties.propertysource;

public class EnvironmentProperties implements PropertySource {
    @Override
    public String getPropertyValue(String key) {
        return System.getenv(key);
    }
}
