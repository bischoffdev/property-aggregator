package blog.softwaretester.properties;

import org.tinylog.Logger;

public class PropertyAggregator {
    private PropertySource[] propertySources;

    public PropertyAggregator addPropertySource(final int priority, final PropertySource propertySource) {

        Logger.info("Property source {} added with priority {}", propertySource, priority);
        return this;
    }

    public PropertyAggregator build() {
        return this;
    }
}
