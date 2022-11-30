package blog.softwaretester.properties.propertysource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.Map;

/**
 * The interface for all property sources.
 */
public abstract class PropertySource {

    /**
     * The standard logger.
     */
    private final Log logger = LogFactory.getLog(this.getClass());

    /**
     * Determines if logs should be shown.
     */
    private final boolean loggingEnabled;


    /**
     * Constructor.
     *
     * @param showLogs If true, logs are shown.
     */
    public PropertySource(final boolean showLogs) {
        this.loggingEnabled = showLogs;
    }

    /**
     * Get a string map containing all properties from the
     * respective {@link PropertySource}.
     *
     * @return The string map.
     */
    Map<String, String> getProperties() {
        return Collections.emptyMap();
    }

    /**
     * Log with level INFO.
     *
     * @param text The text to log.
     */
    void logInfo(final String text) {
        if (loggingEnabled) {
            logger.info(text);
        }
    }

    /**
     * Log with level WARN.
     *
     * @param text The text to log.
     */
    void logWarning(final String text) {
        if (loggingEnabled) {
            logger.warn(text);
        }
    }
}
