package blog.softwaretester.properties;

import blog.softwaretester.properties.propertysource.EnvironmentPropertiesSource;
import blog.softwaretester.properties.propertysource.PropertiesFileSource;
import blog.softwaretester.properties.propertysource.SystemPropertiesSource;
import org.tinylog.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class PropertyAggregator {

    /**
     * This contains the consolidated Properties.
     */
    private final Properties finalProperties;

    /**
     * Private constructor that creates the {@link PropertyAggregator} using
     * the passed in builder.
     *
     * @param builder The {@link PropertyAggregator.Builder}.
     */
    private PropertyAggregator(final Builder builder) {
        Properties tmpProperties = builder.finalProperties;
        if (builder.filteredKeys.size() > 0) {
            for (Map.Entry<Object, Object> entry : tmpProperties.entrySet()) {
                if (!builder.filteredKeys.contains((String) entry.getKey())) {
                    tmpProperties.remove(entry.getKey());
                }
            }
        }
        finalProperties = tmpProperties;
    }

    /**
     * Get the value of a specific properties key.
     *
     * @param key The key of the property.
     * @return The value of the property.
     */
    public String getProperty(final String key) {
        return (String) finalProperties.get(key);
    }

    /**
     * Get all processed properties.
     *
     * @return The {@link Properties}.
     */
    public Properties getAllProperties() {
        return finalProperties;
    }

    /**
     * Log all final processed properties in natural sort order.
     */
    public void logFinalProperties() {
        Logger.info("Properties:");
        getAllProperties()
                .stringPropertyNames()
                .stream()
                .sorted()
                .forEach(key -> Logger.info("- {} => {}",
                        key, getProperty(key)));
    }

    /**
     * Builder used to create a {@link PropertyAggregator} instance.
     */
    public static final class Builder {
        /**
         * This contains the consolidated Properties.
         */
        private final Properties finalProperties = new Properties();

        /**
         * The list of keys to filter properties by.
         */
        private List<String> filteredKeys = Collections.emptyList();

        /**
         * The list of default values for specific properties.
         */
        private Map<String, String> propertyDefaultValues =
                Collections.emptyMap();

        /**
         * Add a system property source to the queue. Each new property source
         * added has a higher priority than the previous one.
         *
         * @return The {@link PropertyAggregator}.
         */
        public Builder withSystemProperties() {
            Logger.info("Added system properties source.");
            finalProperties.putAll(
                    new SystemPropertiesSource()
                            .getProperties());
            return this;
        }

        /**
         * Add an environment property source to the queue. Each new property
         * source added has a higher priority than the previous one.
         *
         * @return The {@link PropertyAggregator}.
         */
        public Builder withEnvironmentProperties() {
            Logger.info("Added environment properties source.");
            finalProperties.putAll(
                    new EnvironmentPropertiesSource()
                            .getProperties());
            return this;
        }

        /**
         * Add a property file source to the queue. This can be added
         * multiple times with different property files.
         *
         * @param propertiesFilePath The path to the properties file.
         * @return The {@link PropertyAggregator}.
         */
        public Builder withPropertiesFile(final String propertiesFilePath) {
            PropertiesFileSource propertiesFileSource =
                    new PropertiesFileSource(propertiesFilePath);
            Logger.info("Added properties file {}.",
                    propertiesFilePath);
            finalProperties.putAll(propertiesFileSource.getProperties());
            return this;
        }

        /**
         * Apply a list of keys to filter the final properties by. All
         * properties whose key does not match any entry will be removed.
         *
         * @param keys A list of property keys to filter by.
         * @return @return The {@link PropertyAggregator}.
         */
        public Builder withFilteredKeys(final List<String> keys) {
            filteredKeys = keys;
            return this;
        }

        /**
         * Apply a map of keys with their default values. If a property with
         * a default value is not set by any property source, it is
         * added with its default value.
         *
         * @param defaultValues A map containing property keys and their
         *                      default values.
         * @return @return The {@link PropertyAggregator}.
         */
        public Builder withDefaultValues(
                final Map<String, String> defaultValues) {
            propertyDefaultValues = defaultValues;
            return this;
        }

        /**
         * Return the configured PropertyAggregator instance.
         *
         * @return The final {@link PropertyAggregator}_
         */
        public PropertyAggregator build() {
            return new PropertyAggregator(this);
        }
    }
}
