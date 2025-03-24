# Developer Documentation - SMS OTP Email Forwarder

## Current Implementation Status

This application is currently in early development stage with basic functionality implemented:

1. **SMS Receiver**: Basic SMS message reception
2. **Simple Email Forwarding**: Basic implementation with JavaMail
3. **UI Layer**: Minimal UI for configuration
4. **Settings Storage**: Basic SharedPreferences implementation

## Architecture Overview

The SMS OTP Email Forwarder app follows a simple architecture with these components:

1. **SMS Receiver**: Intercepts incoming SMS messages.
2. **Email Service**: Handles the forwarding of messages to the configured email address.
3. **UI Layer**: Provides a basic user interface for configuration.
4. **Preference Manager**: Handles storage and retrieval of user settings.

## Key Components

### AndroidManifest.xml

The manifest declares necessary permissions (RECEIVE_SMS, INTERNET) and registers components including the main activity and SMS broadcast receiver.

### SMS Receiver

The app uses a `BroadcastReceiver` to detect incoming SMS messages.

```java
public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract SMS message
        // Forward via email
    }
}
```

### Email Service

Basic implementation using JavaMail API to send emails through SMTP.

### Current Data Flow

1. SMS received → BroadcastReceiver triggered
2. Message forwarded to configured email address
3. No advanced OTP detection logic implemented yet

## Building the Project

### Prerequisites

- Android Studio 4.0+
- JDK 8 or higher
- Android SDK with minimum API level 21 (Android 5.0)

### Build Instructions

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle dependencies
4. Build using Gradle wrapper: `./gradlew assembleDebug`

### Build Variants

The app supports different build types:

1. **Debug Build**:

   - Package name suffix: `.debug`
   - Verbose logging enabled
   - No code optimization
   - Useful for development and testing

2. **Release Build**:
   - Full package name
   - Optimized with R8
   - Code minification and resource shrinking enabled
   - Reduced logging
   - ProGuard rules applied to maintain functionality with third-party libraries

To build both variants:

```bash
./gradlew buildDebugAndRelease
```

This will generate APKs in the project's `app/build/outputs/apk/` directory.

### Dependency Management

Key dependencies include:

- JavaMail API for email functionality
- AndroidX components for UI and background processing

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/otpforwarder/
│   │   │       ├── OTPForwarderApp.java    # Main application class
│   │   │       └── SMSReceiver.java        # SMS broadcast receiver
│   │   ├── res/                            # Resources
│   │   └── AndroidManifest.xml             # App Manifest
│   └── test/                               # Unit Tests
└── build.gradle                            # App-level build file
```

## Testing

Basic testing infrastructure is in place:

- Unit tests: `./gradlew test`
- Instrumented tests: `./gradlew connectedAndroidTest`

## Known Issues and Limitations

- No advanced OTP detection algorithms implemented yet
- Limited error handling
- No background service reliability improvements
- No persistent message storage
- Basic UI only
- Limited configuration options

## Planned Improvements

- Implement proper OTP detection algorithms
- Improve UI with material design
- Add background service reliability
- Implement proper error handling and notifications
- Add advanced filtering options
- Add backup/restore functionality
- Support for multiple email configurations
