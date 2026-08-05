# SimpleEvent Library

A library for Android applications to model, convert, and persist notification events. It provides a structured way to handle system notifications and export them into a standardized CSV format.

## Features

- **Rich Event Modeling**: Supports standard notifications, extended messaging details (people, conversation titles), and removal events.
- **Notification Conversion**: Effortlessly convert Android `StatusBarNotification` objects into clean, serializable Kotlin data classes.
- **CSV Serialization**: Built-in converters for transforming events into CSV lines with customizable separators and timestamp formats.
- **Robust Parsing**: Parse CSV strings back into Event objects, including support for legacy formats.
- **Messaging Integration**: Extracts deep messaging metadata using `NotificationCompat.MessagingStyle`.

## Integration

To use this library in your Android project, include it in your `settings.gradle.kts`:

```kotlin
include(":simpleevent")
project(":simpleevent").projectDir = File("../path/to/libs/kotlin/simpleevent")
```

Then add it as a dependency in your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":simpleevent"))
}
```

## Usage

### 1. Convert Notification to Event

```kotlin
import com.trodevel.simpleevent.ConverterNotificationToEvent

override fun onNotificationPosted(sbn: StatusBarNotification) {
    val event = ConverterNotificationToEvent.convert(sbn)
    // event is either a StandardEvent or ExtendedEvent (for messages)
}
```

### 2. Export Event to CSV

```kotlin
import com.trodevel.simpleevent.ConverterEventToCsv

val converter = ConverterEventToCsv(mustExportTsAsDateTime = false, separator = ";")
val csvLine = converter.toString(event)
```

### 3. Parse Event from CSV

```kotlin
import com.trodevel.simpleevent.ParserCsvStringToEvent

val parser = ParserCsvStringToEvent(separator = ";")
val eventObject = parser.parse(line) // Returns EventObject?
```

## Data Models

### Event Hierarchy
- `EventObject`: Abstract base with a `timestamp`.
    - `RemoveEvent`: Represents a notification removal.
    - `Event`: Base for active notifications.
        - `StandardEvent`: Basic notification (package, title, message).
        - `ExtendedEvent`: Detailed messaging info (list of `Person`, conversation title, etc.).

### Key Components
- `EventBase`: Contains core metadata (package name, title, message, and notification key).
- `Person`: Represents a participant in a conversation (name, uri, bot status, importance).

## License

Copyright © 2026 Sergey Kolevatov.

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
