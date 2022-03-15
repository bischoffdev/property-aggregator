package blog.softwaretester.properties.propertysource;

import blog.softwaretester.properties.PropertyConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public final class PropertiesFileSource implements PropertySource {

    /**
     * The standard logger.
     */
    private static final Log LOGGER =
            LogFactory.getLog(PropertiesFileSource.class);

    /**
     * The path to the properties file.
     */
    private final String propertiesFilePath;

    /**
     * Constructor for the PropertiesFileSource.
     *
     * @param propertiesFile The path to the properties file.
     */
    public PropertiesFileSource(final String propertiesFile) {
        this.propertiesFilePath = propertiesFile;
    }

    @Override
    public Map<String, String> getProperties() {
        Properties properties = new Properties();
        try (InputStream propertiesFileInputStream =
                     new FileInputStream(propertiesFilePath)) {
            properties.load(propertiesFileInputStream);
            LOGGER.info("...loaded successfully");
        } catch (IOException e) {
            LOGGER.warn("...ignored: " + e.getMessage());
        }
        return PropertyConverter.propertiesToMap(properties);
    }
}
