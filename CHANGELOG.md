![Property Aggregator Logo](logo.png)

# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to
[Semantic Versioning](http://semver.org/spec/v2.0.0.html)

Back to [Readme](README.md).

[1.4.1]: https://github.com/bischoffdev/property-aggregator/compare/v1.3.0...v1.4.0
[1.4.0]: https://github.com/bischoffdev/property-aggregator/compare/v1.3.0...v1.4.0
[1.3.0]: https://github.com/bischoffdev/property-aggregator/compare/v1.2.1...v1.3.0
[1.2.1]: https://github.com/bischoffdev/property-aggregator/compare/v1.2.0...v1.2.1
[1.2.0]: https://github.com/bischoffdev/property-aggregator/compare/v1.1.0...v1.2.0
[1.1.0]: https://github.com/bischoffdev/property-aggregator/compare/v1.0.0...v1.1.0
[1.0.0]: https://github.com/bischoffdev/property-aggregator/tree/v1.0.0

## [1.4.1] - 2022-11-30

### Added
* 

## [1.4.0] - 2022-03-21

### Added
* New `withPredicate` method to specify a custom filter that is processed on 
  the properties during the final list processing

### Changed
* More resilient handling of missing properties files in the classpath.

## [1.3.0] - 2022-03-15

### Changed
* Use commons logging for compatibility
* Changed internal property storage to String map

## [1.2.1] - 2022-03-14

### Fixed
* Setting default without a filter removed all properties with default values

### Changed
* Default values are set regardless of filters.

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