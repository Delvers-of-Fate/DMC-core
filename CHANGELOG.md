# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/) 
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]
### Added
- You can no longer jump when dead
- You can now prohibit, and adjust the modifier for running and sprinting
- `TriggeredItem`, triggers when the player is holding the same item set in the properties
- You can no longer spring whilst having active effects

### Changed
- Dropping currency changed to 20, from 10

## [0.1.0] - 2017-08-01
### Added
- Currency! Press `U` to access your money! `acceptedCurrency` has also been added to `TriggeredShop` which defaults to `GOLD`
- Platinum, Copper and Silver currency
- Sprinting! Hold `left shift` in order to move faster
- Jumping! Press `space` in order to jump

### Changed
- Major debug overlay overhaul
- Implemented healing ability in debug overlay
- Replaced `DONE` with `BACK` in the debug overlay, and selecting something no longer closes the overlay
- The `escape` and `enter` key behaviour has been changed
- Default FOV is now 80°
