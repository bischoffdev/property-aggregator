package blog.softwaretester.properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PropertyAggregatorProvenanceTest {

    private static final String RESOURCES_DIR = "src/test/resources/";

    @Test
    public void secondFileOverridesSourceLabel() {
        final String test1 = RESOURCES_DIR + "Test1.properties";
        final String test2 = RESOURCES_DIR + "Test2.properties";
        PropertyAggregator aggregator = new PropertyAggregator.Builder(false)
                .withPropertiesFile(test1)
                .withPropertiesFile(test2)
                .build();

        Assertions.assertEquals("value1_from_test2",
                aggregator.getProperty("property1"));
        Assertions.assertEquals("file:" + test2,
                aggregator.getPropertySource("property1"));
        Assertions.assertEquals("file:" + test1,
                aggregator.getPropertySource("property3"));
    }

    @Test
    public void systemPropertyOverridesFileSource() {
        final String previous =
                System.getProperty("provenance.override.key");
        try {
            System.setProperty("provenance.override.key", "fromSystem");
            PropertyAggregator aggregator = new PropertyAggregator.Builder(false)
                    .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                    .withSystemProperties()
                    .build();

            Assertions.assertEquals("fromSystem",
                    aggregator.getProperty("provenance.override.key"));
            Assertions.assertEquals("system",
                    aggregator.getPropertySource("provenance.override.key"));
        } finally {
            if (previous == null) {
                System.clearProperty("provenance.override.key");
            } else {
                System.setProperty("provenance.override.key", previous);
            }
        }
    }

    @Test
    public void fileOverridesEnvironmentSource() {
        PropertyAggregator aggregator = new PropertyAggregator.Builder(false)
                .withEnvironmentProperties()
                .withPropertiesFile(RESOURCES_DIR + "Test3.properties")
                .build();

        Assertions.assertEquals("overridden_home", aggregator.getProperty("HOME"));
        Assertions.assertEquals(
                "file:" + RESOURCES_DIR + "Test3.properties",
                aggregator.getPropertySource("HOME"));
    }

    @Test
    public void defaultOnlySourceLabel() {
        PropertyAggregator aggregator = new PropertyAggregator.Builder(false)
                .withDefaultValues(Map.of("onlyDefault", "value"))
                .build();

        Assertions.assertEquals("value", aggregator.getProperty("onlyDefault"));
        Assertions.assertEquals("default",
                aggregator.getPropertySource("onlyDefault"));
    }

    @Test
    public void fileValueBeatsDefaultSource() {
        PropertyAggregator aggregator = new PropertyAggregator.Builder(false)
                .withDefaultValues(Map.of("property1", "default1"))
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .build();

        Assertions.assertEquals("file:" + RESOURCES_DIR + "Test1.properties",
                aggregator.getPropertySource("property1"));
    }

    @Test
    public void filteredKeysRemovesSources() {
        PropertyAggregator aggregator = new PropertyAggregator.Builder(false)
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withFilteredKeys(List.of("property1", "property3"))
                .build();

        Assertions.assertNull(aggregator.getProperty("property2"));
        Assertions.assertNull(aggregator.getPropertySource("property2"));
        Assertions.assertEquals(
                aggregator.getAllProperties().keySet(),
                aggregator.getAllPropertySources().keySet());
    }

    @Test
    public void customPredicateRemovesSourcesForDroppedKeys() {
        Predicate<? super Map.Entry<String, String>> predicate1 =
                (Predicate<Map.Entry<String, String>>) entry ->
                        entry.getKey().startsWith("property");

        Predicate<? super Map.Entry<String, String>> predicate2 =
                (Predicate<Map.Entry<String, String>>) entry ->
                        entry.getValue().endsWith("test2");

        PropertyAggregator aggregator = new PropertyAggregator.Builder(false)
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withPropertiesFile(RESOURCES_DIR + "Test2.properties")
                .withPropertiesFile(RESOURCES_DIR + "Test3.properties")
                .withCustomPredicate(predicate1)
                .withCustomPredicate(predicate2)
                .build();

        Assertions.assertNull(aggregator.getPropertySource("HOME"));
        Assertions.assertEquals(
                "file:" + RESOURCES_DIR + "Test2.properties",
                aggregator.getPropertySource("property1"));
    }

    @Test
    public void textDirectoryPrecedenceSource() throws IOException {
        Path directory = createTempDirectory();
        writeFile(directory, "property1.txt", "from_text");

        PropertyAggregator aggregator = new PropertyAggregator.Builder(false)
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .withPropertyValuesFromTextFilesDirectory(directory.toString())
                .build();

        Assertions.assertEquals(
                "textdir:" + directory,
                aggregator.getPropertySource("property1"));
    }

    @Test
    public void getAllPropertySourcesIsUnmodifiable() {
        PropertyAggregator aggregator = new PropertyAggregator.Builder(false)
                .withPropertiesFile(RESOURCES_DIR + "Test1.properties")
                .build();

        Assertions.assertThrows(UnsupportedOperationException.class, () ->
                aggregator.getAllPropertySources().put("x", "y"));
    }

    @Test
    public void getPropertySourceNullForUnknownKey() {
        PropertyAggregator aggregator = new PropertyAggregator.Builder(false)
                .build();

        Assertions.assertNull(aggregator.getPropertySource("missing"));
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
