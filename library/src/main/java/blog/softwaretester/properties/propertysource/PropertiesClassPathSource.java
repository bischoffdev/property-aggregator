package blog.softwaretester.properties.propertysource;

import java.util.Map;
import java.util.Objects;

public final class PropertiesClassPathSource implements PropertySource {

    /**
     * The path to the properties file inside the application's class path.
     */
    private final String propertiesFilePath;

    /**
     * Constructor for the PropertiesClassPathSource.
     *
     * @param propertiesFile The path to the properties file inside the
     *                       application's class path.
     */
    public PropertiesClassPathSource(final String propertiesFile) {
        this.propertiesFilePath = propertiesFile;
    }

    @Override
    public Map<String, String> getProperties() {
        String path = Objects.requireNonNull(
                getClass()
                        .getClassLoader()
                        .getResource(propertiesFilePath)
        ).getPath();

        return new PropertiesFileSource(path).getProperties();
    }
}
