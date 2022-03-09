package blog.softwaretester.properties;

import org.junit.jupiter.api.Test;

public class PropertyAggregatorBuilderTest {

    @Test
    public void validPropertyAggregatorConfiguration() throws Exception {
        new PropertyAggregator.Builder()
                .withPropertySource(PropertySource.SYSTEM_PROPERTIES)
                .withPropertySource(PropertySource.ENVIRONMENT_VARIABLES)
                .build();
    }

    @Test
    public void validPropertyAggregatorConfigurationWithDoubledSources() throws Exception {
        new PropertyAggregator.Builder()
                .withPropertySource(PropertySource.SYSTEM_PROPERTIES)
                .withPropertySource(PropertySource.ENVIRONMENT_VARIABLES)
                .withPropertySource(PropertySource.SYSTEM_PROPERTIES)
                .build();
    }
}
