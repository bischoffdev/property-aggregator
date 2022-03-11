package blog.softwaretester.properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.tinylog.Logger;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PropertyAggregatorBuilderTest {

    private static final String RESOURCES_DIR = "src/test/resources/";

    @BeforeEach
    public void beforeEach(final TestInfo testInfo) {
        Logger.info("");
        Logger.info("Running test [{}]", testInfo.getDisplayName());
        Logger.info("==================================================");
    }

    @Test
    public void getAllProperties() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .build();
        propertyAggregator.logFinalProperties();
        assertEquals(3, propertyAggregator.getPropertiesCount());
    }

    @Test
    public void validOverride() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withPropertiesFile(RESOURCES_DIR + "Test2.properties")
                .build();
        propertyAggregator.logFinalProperties();
        assertEquals(3, propertyAggregator.getPropertiesCount());
    }

    @Test
    public void validSystemProperties() {
        System.getProperties().put("testProperty1", "testValue1");
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withSystemProperties()
                .build();
        String value = propertyAggregator.getProperty("testProperty1");
        assertEquals("testValue1", value);
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

    @Test
    public void validOverriddenEnvironmentProperty() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withEnvironmentProperties()
                .withPropertiesFile(RESOURCES_DIR + "Test3.properties")
                .build();
        String pwd = propertyAggregator.getProperty("HOME");
        assertNotNull(pwd);
        assertEquals("overridden_home", pwd);
    }

    @Test
    public void validFilteredProperties() {
        List<String> filteredKeys = List.of("property1", "property3");
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withFilteredKeys(filteredKeys)
                .build();
        propertyAggregator.logFinalProperties();
        assertEquals(2, propertyAggregator.getPropertiesCount());
    }

    @Test
    public void validFilteredPropertiesWrongKey() {
        List<String> filteredKeys = List.of("nonexistent_key");
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withFilteredKeys(filteredKeys)
                .build();
        propertyAggregator.logFinalProperties();
        assertEquals(0, propertyAggregator.getPropertiesCount());
    }

    @Test
    public void validPropertyOverridesDefault() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withDefaultValues(Map.of("property1", "default1"))
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .build();
        propertyAggregator.logFinalProperties();
        assertEquals("value1_from_test1",
                propertyAggregator.getProperty("property1"));
    }

    @Test
    public void validPropertiesWithDefaultValuesAndFilter() {
        Map<String, String> defaultValues =
                Map.of("a", "1", "b", "2");
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withDefaultValues(defaultValues)
                .withFilteredKeys(List.of("property2", "b"))
                .build();
        propertyAggregator.logFinalProperties();
        assertEquals(2, propertyAggregator.getPropertiesCount());
        assertNull(propertyAggregator.getProperty("a"));
        assertEquals("2", propertyAggregator.getProperty("b"));
        assertNull(propertyAggregator.getProperty("property1"));
        assertEquals("value2_from_test1",
                propertyAggregator.getProperty("property2"));
        assertNull(propertyAggregator.getProperty("property3"));
    }
}
