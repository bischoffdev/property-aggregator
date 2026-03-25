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

    /**
     * The expected file suffix for property files.
     **/
    private static final String FILE_SUFFIX = ".txt";

    /**
     * The directory path containing .txt files and the text file parsing mode.
     **/
    private final String path;

    /**
     * The text file parsing mode.
     */
    private final TextFileValueMode mode;

    /**
     * Constructor.
     *
     * @param directoryPath The directory path containing .txt files.
     * @param textFileValueMode          The text file parsing mode.
     * @param showLogs      If true, logs are shown.
     */
    public PropertyTextFilesDirectorySource(
            final String directoryPath,
            final TextFileValueMode textFileValueMode,
            final boolean showLogs) {
        super(showLogs);
        this.path = directoryPath;
        this.mode = textFileValueMode;
        logInfo("Adding text file properties from " + directoryPath + ".");
    }

    @Override
    public Map<String, String> getProperties() {
        Path sourcePath = Path.of(this.path);
        if (!Files.isDirectory(sourcePath)) {
            logWarning("Loading of " + this.path
                    + " ignored: not a readable directory.");
            return Map.of();
        }

        Map<String, String> properties = new HashMap<>();
        try (DirectoryStream<Path> stream =
                     Files.newDirectoryStream(sourcePath, "*" + FILE_SUFFIX)) {
            for (Path file : stream) {
                if (!Files.isRegularFile(file)) {
                    continue;
                }
                String fileName = file.getFileName().toString();
                String key = fileName.substring(
                        0, fileName.length() - FILE_SUFFIX.length());
                properties.put(key, readValue(file));
            }
            logInfo("Loading of " + this.path + ": successful");
        } catch (IOException e) {
            logWarning("Loading of " + this.path
                    + " ignored: " + e.getMessage());
            return Map.of();
        }

        return properties;
    }

    /**
     * Read a single file and convert it to a property value.
     *
     * @param file The file to read.
     * @return The parsed value based on the configured mode.
     * @throws IOException If the file cannot be read.
     */
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
