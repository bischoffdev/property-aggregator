package blog.softwaretester.properties;

import blog.softwaretester.properties.propertysource.EnvironmentPropertiesSource;
import blog.softwaretester.properties.propertysource.PropertiesClassPathSource;
import blog.softwaretester.properties.propertysource.PropertiesFileSource;
import blog.softwaretester.properties.propertysource.SystemPropertiesSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class PropertyAggregator {

    /**
     * The standard logger.
     */
    private static final Log LOGGER =
            LogFactory.getLog(PropertyAggregator.class);

    /**
     * This contains the consolidated Properties.
     */
    private final Map<String, String> finalProperties;

    /**
     * Private constructor that creates the {@link PropertyAggregator} using
     * the passed in builder.
     *
     * @param builder The {@link PropertyAggregator.Builder}.
     */
    private PropertyAggregator(final Builder builder) {
        Map<String, String> processedProperties = filterPropertyKeys(builder);
        processedProperties =
                applyCustomPredicates(builder, processedProperties);
        addDefaultValues(builder, processedProperties);
        finalProperties = processedProperties;
    }

    /**
     * Adds default values to unset properties.
     *
     * @param builder             The {@link Builder}.
     * @param processedProperties The processed properties including defaults.
     */
    private void addDefaultValues(
            final Builder builder,
            final Map<String, String> processedProperties) {
        // Process default values
        for (Map.Entry<String, String> entry
                : builder.propertyDefaultValues.entrySet()) {
            if (!processedProperties.containsKey(entry.getKey())) {
                processedProperties.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Apply custom predicates to the processed property list.
     *
     * @param builder            The {@link Builder}.
     * @param filteredProperties The current filtered properties.
     * @return The new property list with the predicates applied.
     */
    private Map<String, String> applyCustomPredicates(
            final Builder builder,
            final Map<String, String> filteredProperties) {
        Map<String, String> processedProperties = filteredProperties;
        for (Predicate<? super Map.Entry<String, String>> predicate
                : builder.predicates) {
            processedProperties = processedProperties.entrySet().stream()
                    .filter(predicate)
                    .collect(
                            Collectors.toMap(
                                    e -> String.valueOf(e.getKey()),
                                    e -> String.valueOf(e.getValue()),
                                    (prev, next) -> next, HashMap::new
                            ));
        }
        return processedProperties;
    }

    /**
     * Filters out a specific list of property keys.
     *
     * @param builder The {@link Builder}.
     * @return The processed list of properties.
     */
    private Map<String, String> filterPropertyKeys(final Builder builder) {
        final Map<String, String> processedProperties = new HashMap<>();
        // Filter property keys
        builder.finalProperties.forEach((key, value) -> {
            if (builder.filteredKeys.size() == 0
                    || builder.filteredKeys.contains(key)) {
                processedProperties.put(key, value);
            }
        });
        return processedProperties;
    }

    /**
     * Get the value of a specific properties key.
     *
     * @param key The key of the property.
     * @return The value of the property.
     */
    public String getProperty(final String key) {
        return finalProperties.get(key);
    }

    /**
     * Get the size of all stored property key value pairs including the ones
     * that are only set by defaults.
     *
     * @return The number of stored properties.
     */
    public int getPropertiesCount() {
        return getAllProperties().size();
    }

    /**
     * Get all processed properties.
     *
     * @return The processed properties.
     */
    public Map<String, String> getAllProperties() {
        return finalProperties;
    }

    /**
     * Returns a subset of properties that match a provided predicate.
     *
     * @param predicate The filter to apply to the properties.
     * @return The filtered properties.
     */
    public Map<String, String> getPropertiesWithCustomPredicate(
            final Predicate<? super Map.Entry<String, String>> predicate) {
        return finalProperties.entrySet().stream()
                .filter(predicate)
                .collect(
                        Collectors.toMap(
                                e -> String.valueOf(e.getKey()),
                                e -> String.valueOf(e.getValue()),
                                (prev, next) -> next, HashMap::new
                        ));
    }

    /**
     * Log all final processed properties in natural sort order.
     */
    public void logFinalProperties() {
        LOGGER.info("Properties:");
        getAllProperties()
                .keySet()
                .stream()
                .sorted()
                .forEach(key -> LOGGER.info("- "
                        + key + " => " + getProperty(key)));
    }

    /**
     * Builder used to create a {@link PropertyAggregator} instance.
     */
    @SuppressWarnings("unused")
    public static final class Builder {
        /**
         * This contains the consolidated Properties.
         */
        private final Map<String, String> finalProperties =
                new HashMap<>();

        /**
         * The list of passed in predicates to filter the final list by.
         */
        private final List<Predicate<? super Map.Entry<String, String>>>
                predicates = new ArrayList<>();

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
            LOGGER.info("Added system properties source.");
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
            LOGGER.info("Added environment properties source.");
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
            LOGGER.info("Added properties file "
                    + propertiesFilePath + ".");
            finalProperties.putAll(propertiesFileSource.getProperties());
            return this;
        }

        /**
         * Add a property file source inside the application classpath to the
         * queue. This can be added multiple times with different property
         * files.
         *
         * @param propertiesFilePath The path to the properties file in the
         *                           application classpath.
         * @return The {@link PropertyAggregator}.
         */
        public Builder withPropertiesFileInClassPath(
                final String propertiesFilePath) {
            PropertiesClassPathSource propertiesFileSource =
                    new PropertiesClassPathSource(propertiesFilePath);
            LOGGER.info("Added properties file in classpath "
                    + propertiesFilePath + ".");
            finalProperties.putAll(propertiesFileSource.getProperties());
            return this;
        }

        /**
         * Apply a list of keys to filter the final properties by. All
         * properties whose key does not match any entry will be removed.
         *
         * @param keys A list of property keys to filter by.
         * @return The {@link PropertyAggregator}.
         */
        public Builder withFilteredKeys(final List<String> keys) {
            filteredKeys = keys;
            return this;
        }

        /**
         * Apply a custom filter to the final properties. All properties that
         * do not match the filter will be removed.
         *
         * @param predicate The filter to apply to the properties.
         * @return The {@link PropertyAggregator}.
         */
        public Builder withCustomPredicate(
                final Predicate<? super Map.Entry<String, String>> predicate) {
            predicates.add(predicate);
            return this;
        }

        /**
         * Apply a map of keys with their default values. If a property with
         * a default value is not set by any property source, it is
         * added with its default value.
         *
         * @param defaultValues A map containing property keys and their
         *                      default values.
         * @return The {@link PropertyAggregator}.
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
