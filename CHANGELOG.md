# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- Basic CLI interface
- Generation of data based on regexes and format properties
- Support unique element generation in arrays
- Generate values without specified type
- Support `multipleOf` for `number` type
- Supports schemas with references
- Support `patternProperties`
- Partial `ReferenceSchema` support
- Limit maximum depth of generated objects
- Setting to generate values only from examples
- Allow restriction of properties used in generation
- Support for `StaticDependenciesProperty`
- Schema validation via CLI
- Allow restricting generation to (in)valid documents

### Changed
- Updated jsonoid-discovery dependency to v0.7.3
- Allow undefined properties to be required
- Greatly simplify supported regexes for string patterns

### Fixed
- Fix assembly task
- Handle cases of missing properties
- Strings are now properly generated
- Stop infinite loop with unique arrays of enums
- Make required property optional

## [0.0.1]
### Added
- Initial release with basic working generator

[Unreleased]: https://github.com/michaelmior/jsonoid-discovery/compare/v0.0.1...HEAD
[0.1.0]: https://github.com/michaelmior/jsonoid-discovery/releases/tag/v0.0.1
