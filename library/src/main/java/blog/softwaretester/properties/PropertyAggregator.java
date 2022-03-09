package blog.softwaretester.properties;

import org.tinylog.Logger;

import java.util.PriorityQueue;

public final class PropertyAggregator {

    private PropertyAggregator(final Builder builder) throws Exception {
        if (builder.priorityQueue.size() == 0) {
            throw new Exception("No property sources are specified!");
        }

        Logger.info("Property priorities:");
        boolean isFirstPropertySource = true;
        for (PropertySource propertySource : builder.priorityQueue) {
            if (isFirstPropertySource) {
                Logger.info("Base property source: {}",
                        propertySource);
                isFirstPropertySource = false;
            } else {
                Logger.info("...is overridden by property source {}",
                        propertySource);
            }
        }
    }

    /**
     * Builder used to create a {@link PropertyAggregator} instance.
     */
    public static final class Builder {
        /**
         * The queue of properties sources that override each other.
         */
        private final PriorityQueue<PropertySource> priorityQueue =
                new PriorityQueue<PropertySource>();

        /**
         * Add a property source to the queue. Each new property source added
         * has a higher priority than the previous one.
         *
         * @param propertySource The property source to add.
         * @return The {@link PropertyAggregator}.
         */
        public Builder withPropertySource(final PropertySource propertySource) {
            Logger.debug("Property source {} added.", propertySource);
            if (!priorityQueue.contains(propertySource)) {
                priorityQueue.add(propertySource);
            } else {
                Logger.info("Property source {} was already added before.",
                        propertySource);
            }
            return this;
        }

        /**
         * Return the configured PropertyAggregator instance.
         *
         * @return The final {@link PropertyAggregator}_
         */
        public PropertyAggregator build() throws Exception {
            return new PropertyAggregator(this);
        }
    }
}
