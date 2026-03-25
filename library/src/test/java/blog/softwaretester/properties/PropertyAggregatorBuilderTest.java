package blog.softwaretester.properties;

import blog.softwaretester.properties.propertysource.TextFileValueMode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PropertyAggregatorBuilderTest {

    private static final String RESOURCES_DIR = "src/test/resources/";

    @Test
    public void getAllProperties() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .build();
        propertyAggregator.logFinalProperties();
        Assertions.assertEquals(3, propertyAggregator.getPropertiesCount());
    }

    @Test
    public void invalidPropertiesFile() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withPropertiesFile(RESOURCES_DIR + "Nonexistent.properties")
                .build();
        propertyAggregator.logFinalProperties();
        Assertions.assertEquals(3, propertyAggregator.getPropertiesCount());
    }

    @Test
    public void validOverride() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withPropertiesFile(RESOURCES_DIR + "Test2.properties")
                .build();
        propertyAggregator.logFinalProperties();
        Assertions.assertEquals(3, propertyAggregator.getPropertiesCount());
    }

    @Test
    public void validSystemProperties() {
        System.getProperties().put("testProperty1", "testValue1");
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
                .withSystemProperties()
                .build();
        String value = propertyAggregator.getProperty("testProperty1");
        Assertions.assertEquals("testValue1", value);
    }

    @Test
    public void validEnvironmentProperties() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
                .withEnvironmentProperties()
                .build();
        String pwd = propertyAggregator.getProperty("HOME");
        Assertions.assertNotNull(pwd);
        Assertions.assertNotEquals("", pwd);
    }

    @Test
    public void validOverriddenEnvironmentProperty() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
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
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withFilteredKeys(filteredKeys)
                .build();
        propertyAggregator.logFinalProperties();
        Assertions.assertEquals(2, propertyAggregator.getPropertiesCount());
    }

    @Test
    public void validFilteredPropertiesWrongKey() {
        List<String> filteredKeys = List.of("nonexistent_key");
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withFilteredKeys(filteredKeys)
                .build();
        propertyAggregator.logFinalProperties();
        Assertions.assertEquals(0, propertyAggregator.getPropertiesCount());
    }

    @Test
    public void validPropertyOverridesDefault() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
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
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
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
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
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
        Assertions.assertEquals(3, properties.size());

        predicate = (Predicate<Map.Entry<String, String>>) entry ->
                entry.getValue().endsWith("test2");
        properties =
                propertyAggregator.getPropertiesWithCustomPredicate(predicate);

        Assertions.assertEquals(2, properties.size());
    }

    @Test
    public void validPropertiesWithCustomPredicates() {
        Predicate<? super Map.Entry<String, String>> predicate1 =
                (Predicate<Map.Entry<String, String>>) entry ->
                        entry.getKey().startsWith("property");

        Predicate<? super Map.Entry<String, String>> predicate2 =
                (Predicate<Map.Entry<String, String>>) entry ->
                        entry.getValue().endsWith("test2");

        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withPropertiesFile(RESOURCES_DIR + "Test2.properties")
                .withPropertiesFile(RESOURCES_DIR + "Test3.properties")
                .withCustomPredicate(predicate1)
                .withCustomPredicate(predicate2)
                .build();

        Map<String, String> properties = propertyAggregator.getAllProperties();
        Assertions.assertEquals(2, properties.entrySet().size());
        Assertions.assertEquals("value1_from_test2",
                propertyAggregator.getProperty("property1"));
        Assertions.assertEquals("value2_from_test2",
                propertyAggregator.getProperty("property2"));
    }

    @Test
    public void invalidPropertiesFileInClasspath() {
        new PropertyAggregator.Builder(true)
                .withPropertiesFileInClassPath("invalid.properties")
                .build();
    }

    @Test
    public void validTextDirectoryRawMultiline() throws IOException {
        Path directory = createTempDirectory();
        writeFile(directory, "my.key.txt", "line1\nline2\nline3");

        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
                .withPropertyValuesFromTextFilesDirectory(directory.toString())
                .build();

        Assertions.assertEquals(
                "line1\nline2\nline3",
                propertyAggregator.getProperty("my.key"));
    }

    @Test
    public void validTextDirectoryCommaJoin() throws IOException {
        Path directory = createTempDirectory();
        writeFile(directory, "list.values.txt", "a\n\n b \n c");

        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
                .withPropertyValuesFromTextFilesDirectory(
                        directory.toString(),
                        TextFileValueMode.COMMA_JOIN_NON_EMPTY_LINES)
                .build();

        Assertions.assertEquals(
                "a,b,c",
                propertyAggregator.getProperty("list.values"));
    }

    @Test
    public void validTextDirectoryPrecedence() throws IOException {
        Path directory = createTempDirectory();
        writeFile(directory, "property1.txt", "from_text");

        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withPropertyValuesFromTextFilesDirectory(directory.toString())
                .build();

        Assertions.assertEquals(
                "from_text",
                propertyAggregator.getProperty("property1"));
    }

    @Test
    public void missingTextDirectoryIsIgnored() {
        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
                .withPropertyValuesFromTextFilesDirectory("nonexistent-dir")
                .build();

        Assertions.assertEquals(0, propertyAggregator.getPropertiesCount());
    }

    private Path createTempDirectory() throws IOException {
        Path directory = Files.createTempDirectory("property-aggregator-");
        directory.toFile().deleteOnExit();
        return directory;
    }

    private void writeFile(
            final Path directory,
            final String fileName,
            final String content) throws IOException {
        Path filePath = directory.resolve(fileName);
        Files.writeString(filePath, content, StandardCharsets.UTF_8);
        filePath.toFile().deleteOnExit();
    }
}
