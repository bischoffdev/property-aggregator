package blog.softwaretester.properties.propertysource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Property source that reads values from text files in a directory.
 * Each filename must end with .txt and maps to a property key.
 */
public final class PropertyTextFilesDirectorySource extends PropertySource {

    private static final String FILE_SUFFIX = ".txt";

    private final String directoryPath;
    private final TextFileValueMode mode;

    /**
     * Constructor.
     *
     * @param directoryPath The directory path containing .txt files.
     * @param mode          The text file parsing mode.
     * @param showLogs      If true, logs are shown.
     */
    public PropertyTextFilesDirectorySource(
            final String directoryPath,
            final TextFileValueMode mode,
            final boolean showLogs) {
        super(showLogs);
        this.directoryPath = directoryPath;
        this.mode = mode;
        logInfo("Adding text file properties from " + directoryPath + ".");
    }

    @Override
    public Map<String, String> getProperties() {
        Path path = Path.of(directoryPath);
        if (!Files.isDirectory(path)) {
            logWarning("...ignored: " + directoryPath
                    + " is not a readable directory.");
            return Map.of();
        }

        Map<String, String> properties = new HashMap<>();
        try (DirectoryStream<Path> stream =
                     Files.newDirectoryStream(path, "*" + FILE_SUFFIX)) {
            for (Path file : stream) {
                if (!Files.isRegularFile(file)) {
                    continue;
                }
                String fileName = file.getFileName().toString();
                String key = fileName.substring(
                        0, fileName.length() - FILE_SUFFIX.length());
                properties.put(key, readValue(file));
            }
        } catch (IOException e) {
            logWarning("...ignored: " + e.getMessage());
            return Map.of();
        }

        return properties;
    }

    private String readValue(final Path file) throws IOException {
        if (mode == TextFileValueMode.RAW) {
            return Files.readString(file, StandardCharsets.UTF_8);
        }

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        return lines.stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.joining(","));
    }
}
