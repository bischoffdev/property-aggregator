package blog.softwaretester.properties.propertysource;

/**
 * Defines how text file contents should be converted to property values.
 */
public enum TextFileValueMode {
    /**
     * Use the full file contents as-is.
     */
    RAW,

    /**
     * Read lines, trim each, drop empty lines, then join with commas.
     */
    COMMA_JOIN_NON_EMPTY_LINES
}
