## Full Demo

This example uses all available property sources:

- properties file in classpath
- properties file from filesystem
- system properties
- environment variables
- text files directory (for multiline values)

### Setup

1) Install the library to your local Maven repo:

```
cd ../../library
mvn -DskipTests install
```

2) Set an environment variable for the demo:

```
export DEMO_ENV="env_value"
```

3) Run the demo from this folder:

```
mvn -q exec:java
```

### Notes

- The working directory should be `examples/full-demo` so relative paths resolve.
- To try comma-separated list mode, switch the mode in `DemoApp` to
  `TextFileValueMode.COMMA_JOIN_NON_EMPTY_LINES` and use
  `text-values/demo.list.txt`.
