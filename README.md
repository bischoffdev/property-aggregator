[![Apache V2 License](http://img.shields.io/badge/license-Apache%20V2-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/blog.softwaretester/property-aggregator.svg)](https://repo1.maven.org/maven2/blog/softwaretester/property-aggregator/)
[![Run Unit Tests](https://github.com/bischoffdev/property-aggregator/actions/workflows/unit-tests.yml/badge.svg)](https://github.com/bischoffdev/property-aggregator/actions/workflows/unit-tests.yml)
[![Mastodon Follow](https://img.shields.io/mastodon/follow/109619788534969171?domain=https%3A%2F%2Fhachyderm.io&style=social)](https://hachyderm.io/invite/acrCWhtk)

![Property Aggregator Logo](logo.png)

# Property Aggregator

_Simplify Java property handling and create a single source of truth!_

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
      - [Properties file in a fixed path](#properties-file-in-a-fixed-path)
      - [Properties file in the application's class path](#properties-file-in-the-applications-class-path)
  - [Specifying property hierarchies](#specifying-property-hierarchies)
  - [Querying properties](#querying-properties)
  - [Filtering property keys during the build](#filtering-property-keys-during-the-build)
  - [Specifying custom filters during the build](#specifying-custom-filters-during-the-build)
  - [Retrieving subsets of properties from the final list](#retrieving-subsets-of-properties-from-the-final-list)
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
PropertyAggregator propertyAggregator =
    new PropertyAggregator.Builder(true)
    .withEnvironmentProperties()
    .withPropertiesFile("path/to/Test1.properties")
    .build();
```

The boolean parameter inside `Builder()` determines if logs should be shown or not.
The `false` option can be useful if the PropertyAggregator should be a "silent part" of another library.

In this case,

1. environment properties (aka environment variables) are loaded first
2. a `Test1.properties` file is loaded which overrides existing properties from the step before

You can specify multiple sources like this in any order to create your custom 
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
PropertyAggregator propertyAggregator =
    new PropertyAggregator.Builder(true)
    .withSystemProperties()
    .build();
```

### Environment variables

To use environment properties as a property source, use the `withEnvironmentProperties()` option:

```
PropertyAggregator propertyAggregator =
    new PropertyAggregator.Builder(true)
    .withEnvironmentProperties()
    .build();
```

### Properties files

#### Properties file in a fixed path

To use a properties file as a property source, use the `withPropertiesFile()` option:

```
PropertyAggregator propertyAggregator = 
    new PropertyAggregator.Builder(true)
    .withPropertiesFile("path/to/custom.properties")
    .build();
```

#### Properties file in the application's class path

If the properties file can be anywhere inside the application's class path, 
use this method to retrieve them:

```
PropertyAggregator propertyAggregator = 
    new PropertyAggregator.Builder(true)
    .withPropertiesFileInClassPath(pathToProperties)
    .build();
```

## Specifying property hierarchies

The power of Property Aggregator is the ability to specify which properties 
have a higher order than others.

This enables hierarchies such as:

1. the application's standard _property file_ `application.properties`
2. this can be overwritten with _system properties_ 
3. this can be overwritten with _environment properties_.

This example would look like this in code:

```
PropertyAggregator propertyAggregator =
    new PropertyAggregator.Builder(true)
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
String propertyValue =
    propertyAggregator.getProperty("propertyKey"));
```

This returns the property value from the final processed and filtered set of 
properties or an appropriate default value if it exists. Otherwise, the 
return value will be `null`.

## Filtering property keys during the build

Especially when using environment properties, they can be quite large and 
not all of them may be relevant for your application. It is possible to add 
a filter in this case to remove all properties whose keys are not required.

```
List<String> filteredKeys =
    List.of("property1", "property2");

PropertyAggregator propertyAggregator =
    new PropertyAggregator.Builder(true)
    .withEnvironmentProperties()
    .withFilteredKeys(filteredKeys)
    .build();
```

Here, all environment properties that are not called "property1" or 
"property2" are stripped out of the `PropertyAggregator` instance.

__Note:__ The filter is applied when the final `build()` is called on the 
`PropertyAggregator` builder. That means that it is applied after all 
property sources are combined.

## Specifying custom filters during the build

If you want to permanently filter properties in the final list based on 
_custom_ predicates, you can do it like this:

```
Predicate<? super Map.Entry<String, String>> myAppFilter =
        (Predicate<Map.Entry<String, String>>) entry ->
                entry.getKey().startsWith("myApp");

PropertyAggregator propertyAggregator = new PropertyAggregator.Builder(true)
        .withPropertiesFile(RESOURCES_DIR + "application.properties")
        .withCustomPredicate(myAppFilter)
        .build();
```

This example permanently alters the property list and only keep properties 
whose key starts with "myApp".

__Note:__ It is possible to use `withCustomPredicate` multiple times.

## Retrieving subsets of properties from the final list

It is possible to get subsets of properties that match custom predicates. 
One use case for this is having different sets of properties that have to be 
passed on to application plugins.

```
Predicate<? super Map.Entry<String, String>> filterXProperties =
    (Predicate<Map.Entry<String, String>>) entry ->
        entry.getKey().startsWith("X");

Properties properties =
        propertyAggregator.getPropertiesWithCustomPredicate(filterXProperties);
```

This example would return properties that have keys starting with the letter
"X".

## Setting default values

Setting default values can be beneficial if certain properties _must_ exist 
and can optionally be overwritten.

```
Map<String, String> defaults =
    Map.of("property1", "default1");

PropertyAggregator propertyAggregator =
    new PropertyAggregator.Builder(true)
    .withDefaultValues(defaults)
    .withPropertiesFile("path/to/application.properties")
    .build();
```

Here, the key "property1" is set with a default value of "default1". If any 
of the property sources (in this example a properties file) does not include 
"property1", it is added to the final list of properties with the default 
value. If, however, "property1" __is__ specified in a property source, the 
default value is replaced by the actual value. 

__Note:__ If you use defaults and filters together, properties with default 
values are added to the final set regardless of filters!

## Logging the final properties

If you want to check the properties after all applied operations, you can 
log all properties like this:

```
propertyAggregator.logFinalProperties();
```

__Note:__ These logs are always shown regardless of the log settings of the builder!

## Checking the size of the final properties

In order to get the size of processed properties including properties that 
are only specified via default values, use 

```
propertyAggregator.getPropertiesCount()
```

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
