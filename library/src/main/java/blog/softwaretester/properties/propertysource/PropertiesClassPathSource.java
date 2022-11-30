package blog.softwaretester.properties.propertysource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

public final class PropertiesClassPathSource extends PropertySource {

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
     * Determines if logs should be shown.
     */
    private final boolean loggingEnabled;

    /**
     * Constructor.
     *
     * @param propertiesFile The path to the properties file inside the
     *                       application's class path.
     * @param showLogs       If true, logs are shown.
     */
    public PropertiesClassPathSource(
            final String propertiesFile, final boolean showLogs) {
        super(showLogs);
        this.loggingEnabled = showLogs;
        this.propertiesFilePath = propertiesFile;
        logInfo("Adding properties file in classpath "
                + propertiesFilePath + ".");
    }

    @Override
    public Map<String, String> getProperties() {
        URL resource = getClass()
                .getClassLoader()
                .getResource(propertiesFilePath);
        if (resource == null) {
            logWarning("...ignored: " + propertiesFilePath
                    + " not found in class path.");
            return Collections.emptyMap();
        }
        return new PropertiesFileSource(
                resource.getPath(), loggingEnabled).getProperties();
    }
}
