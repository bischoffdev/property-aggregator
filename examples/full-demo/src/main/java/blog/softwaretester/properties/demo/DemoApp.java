package blog.softwaretester.properties.demo;

import blog.softwaretester.properties.PropertyAggregator;
import blog.softwaretester.properties.propertysource.TextFileValueMode;

public final class DemoApp {

    private DemoApp() {
    }

    public static void main(final String[] args) {
        System.setProperty("demo.system", "system_value");

        PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
                .withPropertiesFileInClassPath("application.properties")
                .withPropertiesFile("config/override.properties")
                .withSystemProperties()
                .withEnvironmentProperties()
                .withPropertyValuesFromTextFilesDirectory(
                        "text-values",
                        TextFileValueMode.RAW)
                .build();

        System.out.println("demo.system=" + propertyAggregator.getProperty("demo.system"));
        System.out.println("DEMO_ENV=" + propertyAggregator.getProperty("DEMO_ENV"));
        System.out.println("demo.value=" + propertyAggregator.getProperty("demo.value"));
        System.out.println("demo.shared=" + propertyAggregator.getProperty("demo.shared"));
        System.out.println("demo.multiline=" + propertyAggregator.getProperty("demo.multiline"));
    }
}
