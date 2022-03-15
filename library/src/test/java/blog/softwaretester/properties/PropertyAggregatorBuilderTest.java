package blog.softwaretester.properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PropertyAggregatorBuilderTest {

    private static final Log LOGGER =
            LogFactory.getLog(PropertyAggregatorBuilderTest.class);

    private static final String RESOURCES_DIR = "src/test/resources/";

    @BeforeEach
    public void beforeEach(final TestInfo testInfo) {
        LOGGER.info("");
        LOGGER.info("Running test: " + testInfo.getDisplayName());
        LOGGER.info("==================================================");
    }

    @Test
    public void getAllProperties() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .build();
        propertyAggregator.logFinalProperties();
        Assertions.assertEquals(3, propertyAggregator.getPropertiesCount());
    }

    @Test
    public void invalidPropertiesFile() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withPropertiesFile(RESOURCES_DIR + "Nonexistent.properties")
                .build();
        propertyAggregator.logFinalProperties();
        Assertions.assertEquals(3, propertyAggregator.getPropertiesCount());
    }

    @Test
    public void validOverride() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withPropertiesFile(RESOURCES_DIR + "Test2.properties")
                .build();
        propertyAggregator.logFinalProperties();
        Assertions.assertEquals(3, propertyAggregator.getPropertiesCount());
    }

    @Test
    public void validSystemProperties() {
        System.getProperties().put("testProperty1", "testValue1");
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withSystemProperties()
                .build();
        String value = propertyAggregator.getProperty("testProperty1");
        Assertions.assertEquals("testValue1", value);
    }

    @Test
    public void validEnvironmentProperties() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withEnvironmentProperties()
                .build();
        String pwd = propertyAggregator.getProperty("HOME");
        Assertions.assertNotNull(pwd);
        Assertions.assertNotEquals("", pwd);
    }

    @Test
    public void validOverriddenEnvironmentProperty() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withEnvironmentProperties()
                .withPropertiesFile(RESOURCES_DIR + "Test3.properties")
                .build();
        String pwd = propertyAggregator.getProperty("HOME");
        Assertions.assertNotNull(pwd);
        Assertions.assertEquals("overridden_home", pwd);
    }

    @Test
    public void validFilteredProperties() {
        List<String> filteredKeys = List.of("property1", "property3");
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withFilteredKeys(filteredKeys)
                .build();
        propertyAggregator.logFinalProperties();
        Assertions.assertEquals(2, propertyAggregator.getPropertiesCount());
    }

    @Test
    public void validFilteredPropertiesWrongKey() {
        List<String> filteredKeys = List.of("nonexistent_key");
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withFilteredKeys(filteredKeys)
                .build();
        propertyAggregator.logFinalProperties();
        Assertions.assertEquals(0, propertyAggregator.getPropertiesCount());
    }

    @Test
    public void validPropertyOverridesDefault() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withDefaultValues(Map.of("property1", "default1"))
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .build();
        propertyAggregator.logFinalProperties();
        Assertions.assertEquals("value1_from_test1",
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
        Assertions.assertEquals(3, propertyAggregator.getPropertiesCount());
        Assertions.assertEquals("1", propertyAggregator.getProperty("a"));
        Assertions.assertEquals("2", propertyAggregator.getProperty("b"));
        Assertions.assertNull(propertyAggregator.getProperty("property1"));
        Assertions.assertEquals("value2_from_test1",
                propertyAggregator.getProperty("property2"));
        Assertions.assertNull(propertyAggregator.getProperty("property3"));
    }

    @Test
    public void validPropertiesWithKeyFilter() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withPropertiesFile(RESOURCES_DIR + "Test2.properties")
                .withPropertiesFile(RESOURCES_DIR + "Test3.properties")
                .build();

        propertyAggregator.logFinalProperties();
        Predicate<? super Map.Entry<String, String>> predicate =
                (Predicate<Map.Entry<String, String>>) entry ->
                        entry.getKey().startsWith("property");
        Map<String, String> properties =
                propertyAggregator.getPropertiesWithCustomPredicate(predicate);
        Assertions.assertEquals(3, properties.entrySet().size());

        predicate = (Predicate<Map.Entry<String, String>>) entry ->
                entry.getValue().endsWith("test2");
        properties =
                propertyAggregator.getPropertiesWithCustomPredicate(predicate);

        Assertions.assertEquals(2, properties.entrySet().size());
    }
}
