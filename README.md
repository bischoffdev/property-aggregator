![Property Aggregator Logo](logo.png)

### Aggregate all properties into a single source of truth!

[![Apache V2 License](http://img.shields.io/badge/license-Apache%20V2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/blog.softwaretester/property-aggregator.svg)](https://repo1.maven.org/maven2/blog/softwaretester/property-aggregator/)
[![Twitter URL](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/bischoffdev)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [Property Aggregator](#property-aggregator)
- [Changelog](#changelog)
- [Maven dependency](#maven-dependency)
- [Usage](#usage)
  - [General usage](#general-usage)
    - [Building a PropertyAggregator](#building-a-propertyaggregator)
    - [Retrieving properties](#retrieving-properties)
  - [Specifying property sources](#specifying-property-sources)
    - [System properties](#system-properties)
    - [Environment variables](#environment-variables)
    - [Properties files](#properties-files)
  - [Specifying property hierarchies](#specifying-property-hierarchies)
  - [Querying properties](#querying-properties)
  - [Filtering properties](#filtering-properties)
  - [Setting default values](#setting-default-values)
  - [Logging the final properties](#logging-the-final-properties)
  - [Checking the size of the final properties](#checking-the-size-of-the-final-properties)
  - [Retrieving all properties](#retrieving-all-properties)
- [Appendix](#appendix)
  - [Building](#building)
  - [License](#license)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Property Aggregator

This project takes care of various properties sources in Java like system and 
environment variables as well as properties files. It allows you to specify 
which sources override others, filter them and give them default values in order 
to have one single aggregated source of application properties.

# Changelog

All changes can be seen in the linked [changelog](CHANGELOG.md).

# Maven dependency

```xml
<dependency>
    <groupId>blog.softwaretester</groupId>
    <artifactId>property-aggregator</artifactId>
    <version>Check the version number above</version>
</dependency>
```

# Usage

## General usage

### Building a PropertyAggregator

The Property Aggregator uses a simple builder pattern to specify property 
sources and options. 

```
PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
    .withEnvironmentProperties()
    .withPropertiesFile("path/to/Test1.properties")
    .build();
```

In this case, the first property source specified is environment properties 
(aka environment variables). The second property source is a custom 
properties file called `Test1.properties` which overrides existing environment 
properties.

You can specify multiple sources in any order to create your custom 
property hierarchies.

When the final `build()` method is called, all property sources are combined,
filters are applied and default values are set.

### Retrieving properties

To query for certain properties, use it as shown below:

```
propertyAggregator.getProperty("property1")
```

## Specifying property sources

### System properties

To use system properties as a property source, use the `withSystemProperties()` option:

```
PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
    .withSystemProperties()
    .build();
```

### Environment variables

To use system properties as a property source, use the `withEnvironmentProperties()` option:

```
PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
    .withEnvironmentProperties()
    .build();
```

### Properties files

To use system properties as a property source, use the `withPropertiesFile()` option:

```
PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
    .withPropertiesFile("path/to/custom.properties")
    .build();
```

## Specifying property hierarchies

The power of Property Aggregator is the ability to specify which properties 
have a higher order than others. This enables hierarchies such as

The applications's standard _property file_ `application.properties` can be 
overwritten with _system properties_ which in turn can be overwritten with 
_environment properties_.

This example would look like this in code:

```
PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
    .withPropertiesFile("path/to/application.properties")
    .withSystemProperties()
    .withEnvironmentProperties()
    .build();
```

If properties do not exist in a _higher_ source but only in a _lower_ one, 
the _lower_ one is added to the final properties. That means that _higher_ 
sources overwrite properties that already exist in a _lower_ one.

## Querying properties

To retrieve a property value by key, just use

```
String propertyValue = propertyAggregator.getProperty("propertyKey"));
```

This returns the property value from the final processed and filtered set of 
properties or an appropriate default value if it exists. Otherwise, the 
return value will be `null`.

## Filtering properties

Especially when using environment properties, they can be quite large and 
not all of them may be relevant for your application. It is possible to add 
a filter in this case to filter out all properties that are not required.

```
List<String> filteredKeys = List.of("property1", "property2");

PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
    .withEnvironmentProperties()
    .withFilteredKeys(filteredKeys)
    .build();
```

Here, all environment properties that are not called "property1" or 
"property2" are stripped out of the `PropertyAggregator` instance.

__Note:__ The filter is applied when the final `build()` is called on the 
`PropertyAggregator` builder. That means that it is applied after all 
property sources are combined.

## Setting default values

Setting default values can be beneficial if certain properties _must_ exist 
and can optionally be overwritten.

```
Map<String, String> defaults = Map.of("property1", "default1");

PropertyAggregator propertyAggregator = new PropertyAggregator.Builder()
    .withDefaultValues(defaults)
    .withPropertiesFile("path/to/application.properties")
    .build();
```

Here, the key "property1" is set with a default value of "default1". If any 
of the property sources (in this example a properties file) does not include 
"property1", it is added to the final list of properties with the default 
value. If, however, "property1" __is__ specified in a property source, the 
default value is replaced by the actual value. 

__Note:__ If you use defaults and filters together, make sure that 
properties with defaults also exist in the list of filter keys!

## Logging the final properties

If you want to check the properties after all applied operations, you can 
log all properties like this:

```
propertyAggregator.logFinalProperties();
```

## Checking the size of the final properties

In order to get the size of processed properties including properties that 
are only specified via default values, use 

```
propertyAggregator.getPropertiesCount()
```

__Note:__ Only this method takes default values into account. Using 
`propertyAggregator.getAllProperties().size()` returns only the count of 
properties that are set by property sources!

## Retrieving all properties

This method returns all processed property keys and values that are set by 
property sources or defaults and are not filtered out:

```
propertyAggregator.getAllProperties()
```

# Appendix

## Building

Property Aggregator requires Java >= 11 and Maven >= 3.3.9.

It is available in [Maven central](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22blog.softwaretester%22%20AND%20a%3A%22property-aggregator%22).

## License

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
