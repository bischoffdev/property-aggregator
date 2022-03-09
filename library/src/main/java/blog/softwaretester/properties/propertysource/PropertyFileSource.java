package blog.softwaretester.properties.propertysource;

public class PropertyFileSource implements PropertySource {
    @Override
    public String getPropertyValue(String key) {
        return "BLA";
    }
}
