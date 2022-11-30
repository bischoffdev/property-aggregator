package blog.softwaretester.properties.propertysource;

import blog.softwaretester.properties.PropertyConverter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public final class PropertiesFileSource extends PropertySource {

    /**
     * The path to the properties file.
     */
    private final String propertiesFilePath;

    /**
     * Constructor.
     *
     * @param propertiesFile The path to the properties file.
     * @param showLogs If true, logs are shown.
     */
    public PropertiesFileSource(
            final String propertiesFile, final boolean showLogs) {
        super(showLogs);
        this.propertiesFilePath = propertiesFile;
    }

    @Override
    public Map<String, String> getProperties() {
        Properties properties = new Properties();
        logInfo("Loading " + propertiesFilePath + "...");
        try (InputStream propertiesFileInputStream =
                     new FileInputStream(propertiesFilePath)) {
            properties.load(propertiesFileInputStream);
            logInfo("...loaded successfully");
        } catch (IOException e) {
            logWarning("...ignored: " + e.getMessage());
        }
        return PropertyConverter.propertiesToMap(properties);
    }
}
