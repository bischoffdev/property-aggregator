![Property Aggregator Logo](logo.png)

# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to
[Semantic Versioning](http://semver.org/spec/v2.0.0.html)

Back to [Readme](README.md).

[1.2.0]: https://github.com/bischoffdev/property-aggregator/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/bischoffdev/property-aggregator/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/bischoffdev/property-aggregator/tree/v1.0.0

## [1.2.0] - 2022-03-14

### Added
* `getPropertiesWithCustomPredicate` method that returns a subset of
  properties based on a provided predicate
* Added `withPropertiesFileInClassPath` method to set a property file from 
  within the application classpath

## [1.1.0] - 2022-03-11

### Added
* `withFilter` method to only keep properties in the final list that have 
  specific keys
* `withDefaults` method to set defaults for unspecified properties
* `propertiesCount` method to get the size of properties including defaults

## [1.0.0] - 2022-03-11

### Added
* Initial release