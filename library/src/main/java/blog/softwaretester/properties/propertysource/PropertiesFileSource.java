package blog.softwaretester.properties.propertysource;

import org.tinylog.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesFileSource implements PropertySource {

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
    public Properties getProperties() {
        Properties properties = new Properties();
        try (InputStream propertiesFileInputStream =
                     new FileInputStream(propertiesFilePath)) {
            properties.load(propertiesFileInputStream);
            Logger.info("...loaded successfully",
                    propertiesFilePath);
        } catch (IOException e) {
            Logger.warn("...ignored: {}",
                    propertiesFilePath, e.getMessage());
        }
        return properties;
    }
}
