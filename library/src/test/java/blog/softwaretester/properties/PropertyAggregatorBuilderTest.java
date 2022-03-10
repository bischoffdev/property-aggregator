package blog.softwaretester.properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.tinylog.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyAggregatorBuilderTest {

    private static final String RESOURCES_DIR = "src/test/resources/";

    @BeforeEach
    public void beforeEach(final TestInfo testInfo) {
        Logger.info("");
        Logger.info("Running test [{}]", testInfo.getDisplayName());
        Logger.info("==================================================");
    }

    @Test
    public void getAllPropertiesTest() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .build();
        propertyAggregator.logAllProperties();
        assertEquals(3, propertyAggregator.getAllProperties().size());
    }

    @Test
    public void validOverrideTest() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withPropertiesFile(RESOURCES_DIR + "Test2.properties")
                .build();
        propertyAggregator.logAllProperties();
        assertEquals(3, propertyAggregator.getAllProperties().size());
    }

    @Test
    public void validEnvironmentProperties() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withEnvironmentProperties()
                .build();
        String pwd = propertyAggregator.getProperty("HOME");
        assertNotNull(pwd);
        assertNotEquals("", pwd);
    }
}
