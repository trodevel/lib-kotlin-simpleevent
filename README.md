# SimpleEvent Library

A library for Android applications to model, convert, and persist notification events. It provides a structured way to handle system notifications and export them into a standardized CSV format.

## Features

- **Rich Event Modeling**: Supports standard notifications, extended messaging details (people, conversation titles), historical messages (`isHistorical` flag), and removal events.
- **Notification Conversion**: Effortlessly convert Android `StatusBarNotification` objects into clean, serializable Kotlin data classes, extracting both historical and active messages.
- **CSV Serialization**: Built-in serializers for transforming events into CSV lines with customizable separators and timestamp formats.
- **Robust Parsing**: Parse CSV strings back into Event objects, including support for legacy formats.
- **Messaging Integration**: Extracts deep messaging metadata and historical messages using `NotificationCompat.MessagingStyle`.
- **Category Extension**: Captures original Android notification categories and maps them to a structured `Category` enum, while preserving the original category name for non-standard values via `CategoryExt`.

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
    val events = ConverterNotificationToEvent.convert(sbn)
    for (event in events) {
        // event is either a SimpleEvent or ExtendedEvent (for messages)
    }
}
```

### 2. Export Event to CSV

```kotlin
import com.trodevel.simpleevent.CsvSerializer

val serializer = CsvSerializer(mustExportTsAsDateTime = false, separator = ";")
val csvLine = serializer.toString(event)
```

### 3. Parse Event from CSV

```kotlin
import com.trodevel.simpleevent.CsvDeserializer

val deserializer = CsvDeserializer(separator = ";")
val event = deserializer.toObject(line) // Returns Event?
```

## CSV Format (Version 4)

The library uses a versioned CSV format. Version 4 introduced an integer flag (`isHistorical`) to distinguish historical messages from active notification events:

1. **VERSION**: 4
2. **TYPE**: 1 (Simple), 2 (Extended), 3 (Remove)
3. **Timestamp**: Long or Date/Time
4. **Key**: Notification key
5. **channelId**: Android notification channel ID
6. **category**: Integer ID representing the `Category` enum
7. **categoryStr**: Original Android category string (populated if `category` is `OTHER`)
8. **conversationId**: Shortcut ID
9. **parentChannelId**: Parent channel ID
10. **packageName**: Source package
11. **Title**: Notification title
12. **Message**: Notification content
13. **isHistorical**: Integer flag (`1` for historical message, `0` for active event)

Extended events (Type 2) append:
14. **isOneToOne**: Boolean
15. **people**: JSON array of participants
16. **messaging_person**: JSON object of the sender
17. **conversation_title**: Optional conversation name

## Data Models

### Event Hierarchy
- `Event`: Abstract base with a `timestamp`.
    - `RemoveEvent`: Represents a notification removal.
    - `EventBase`: Base for active notifications.
        - `SimpleEvent`: Basic notification (package, title, message).
        - `ExtendedEvent`: Detailed messaging info (list of `Person`, conversation title, etc.).

### Key Components
- `DataEventBase`: Contains core metadata (package name, title, message, notification key, and `isHistorical` flag).
- `Person`: Represents a participant in a conversation (name, uri, bot status, importance).

## License

Copyright © 2026 Sergey Kolevatov.

This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
