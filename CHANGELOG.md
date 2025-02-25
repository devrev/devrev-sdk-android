# Changelog

All notable changes to this project will be documented in this file.

## 1.1.1

### Added 
- Added new methods that allow tracking of screen transitions to understand user navigation within your app.

### Fixed
- Fix the session recording upload bug. 

## 1.1.0

### Added
- Introducing a logout mechanism that clears the user's credentials, unregisters the device from receiving push notifications, and stops the session recording.
- Added a boolean flag to check if the SDK has been configured.
- Added a boolean flag to check if the user has been identified.
- Added a boolean flag to check if the session is being recorded.
- Added a boolean flag to check if the monitoring is enabled.
- Added a boolean flag to check if the on-demand sessions are enabled.

## 1.0.3

### Added
- A new flag `prefersDialogMode` to open the support chat in the app's main task/activity.

### Fixed
- Fix crash when application theme is not a descendant of `Theme.AppCompat`
- Fix back navigation handling in support chat on physical back button press.
- Fix data retention in editable views in case of configuration changes like rotation, keyboard switch, etc.

## 1.0.2

### Fixed
- Improved the push notifications for verified users
- Fix the start recording failures
- Fix masking for React Native

## 1.0.1

### Changed
- Improved the user identification mechanism.

## 1.0.0

### Added
- Introducing the Session Analytics feature. This feature allows you to monitor the health of your application and its components.
- Added support for Push Notifications for the PLuG support chat.
- Added support to create new conversations in the PLuG support chat.
- Added support for in-app link handling.
