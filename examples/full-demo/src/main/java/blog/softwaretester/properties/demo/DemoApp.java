package blog.softwaretester.properties.demo;

import blog.softwaretester.properties.PropertyAggregator;
import blog.softwaretester.properties.propertysource.TextFileValueMode;

import java.nio.file.Files;
import java.nio.file.Path;

public final class DemoApp {

    private DemoApp() {
    }

    public static void main(final String[] args) {
        System.setProperty("demo.system", "system_value");

        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
                .withPropertiesFileInClassPath("application.properties")
                .withPropertiesFile(resolveConfigFile())
                .withSystemProperties()
                .withEnvironmentProperties()
                .withPropertyValuesFromTextFilesDirectory(
                        resolveTextValuesDirectory(),
                        TextFileValueMode.RAW)
                .withPropertyValuesFromTextFilesDirectory(
                        resolveListValuesDirectory(),
                        TextFileValueMode.COMMA_JOIN_NON_EMPTY_LINES)
                .build();

        System.out.println("demo.system=" + propertyAggregator.getProperty("demo.system"));
        System.out.println("DEMO_ENV=" + propertyAggregator.getProperty("DEMO_ENV"));
        System.out.println("demo.value=" + propertyAggregator.getProperty("demo.value"));
        System.out.println("demo.shared=" + propertyAggregator.getProperty("demo.shared"));
        System.out.println("demo.multiline=" + propertyAggregator.getProperty("demo.multiline"));
        System.out.println("demo.list=" + propertyAggregator.getProperty("demo.list"));
    }

    private static String resolveTextValuesDirectory() {
        Path localPath = Path.of("text-values");
        if (Files.isDirectory(localPath)) {
            return localPath.toString();
        }

        Path repoPath = Path.of("examples", "full-demo", "text-values");
        if (Files.isDirectory(repoPath)) {
            return repoPath.toString();
        }

        return localPath.toString();
    }

    private static String resolveListValuesDirectory() {
        Path localPath = Path.of("text-values-list");
        if (Files.isDirectory(localPath)) {
            return localPath.toString();
        }

        Path repoPath = Path.of("examples", "full-demo", "text-values-list");
        if (Files.isDirectory(repoPath)) {
            return repoPath.toString();
        }

        return localPath.toString();
    }

    private static String resolveConfigFile() {
        Path localPath = Path.of("config", "override.properties");
        if (Files.isRegularFile(localPath)) {
            return localPath.toString();
        }

        Path repoPath = Path.of("examples", "full-demo", "config", "override.properties");
        if (Files.isRegularFile(repoPath)) {
            return repoPath.toString();
        }

        return localPath.toString();
    }
}
