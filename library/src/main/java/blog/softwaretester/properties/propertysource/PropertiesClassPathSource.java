package blog.softwaretester.properties.propertysource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

public final class PropertiesClassPathSource implements PropertySource {

    /**
     * The standard logger.
     */
    private static final Log LOGGER =
            LogFactory.getLog(PropertiesClassPathSource.class);

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
        URL resource = getClass()
                .getClassLoader()
                .getResource(propertiesFilePath);
        if (resource == null) {
            LOGGER.warn("...ignored: " + propertiesFilePath
                    + " not found in class path.");
            return Collections.emptyMap();
        }
        return new PropertiesFileSource(resource.getPath()).getProperties();
    }
}
