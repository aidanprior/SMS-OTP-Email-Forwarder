# Developer Documentation - SMS OTP Email Forwarder

## Architecture Overview

The SMS OTP Email Forwarder app follows a modular architecture with these main components:

1. **SMS Receiver**: Intercepts incoming SMS messages and processes them to detect OTPs.
2. **OTP Parser**: Extracts OTP codes from message content using pattern recognition.
3. **Email Service**: Handles the forwarding of detected OTPs to the configured email address.
4. **UI Layer**: Provides user interface for configuration and status monitoring.
5. **Preference Manager**: Handles storage and retrieval of user settings.

## Key Components

### AndroidManifest.xml

The manifest declares necessary permissions (RECEIVE_SMS, INTERNET) and registers components including the main activity and SMS broadcast receiver.

### SMS Receiver

The app uses a `BroadcastReceiver` to detect incoming SMS messages, which triggers the OTP detection logic.

```java
public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Extract SMS message
        // Process for OTP
        // Forward if detected
    }
}
```

### OTP Detection Logic

The app employs regular expressions and keyword matching to identify OTP patterns within SMS messages.

### Email Service

Implements JavaMail or similar libraries to send emails through SMTP with the extracted OTP.

### Data Flow

1. SMS received → BroadcastReceiver triggered
2. Message parsed for OTP → Detection algorithms applied
3. If OTP found → Email service prepares message
4. Email credentials retrieved from secure storage
5. Email sent to configured address

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

### Dependency Management

Key dependencies include:

- JavaMail API for email functionality
- AndroidX components for UI and background processing
- Room database (optional) for message history

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/otpforwarder/
│   │   │       ├── activities/       # UI Activities
│   │   │       ├── receivers/        # BroadcastReceivers
│   │   │       ├── services/         # Background Services
│   │   │       ├── utils/            # Helper Classes
│   │   │       └── models/           # Data Models
│   │   ├── res/                      # Resources
│   │   └── AndroidManifest.xml       # App Manifest
│   └── test/                         # Unit Tests
└── build.gradle                      # App-level build file
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Implement changes with appropriate tests
4. Submit a pull request with detailed description

## Testing

- Unit tests: `./gradlew test`
- Instrumented tests: `./gradlew connectedAndroidTest`
- Manual testing should verify:
  - SMS reception in various formats
  - OTP detection accuracy
  - Email delivery reliability
  - Battery consumption

## Security Considerations

- Use Android Keystore for credential encryption
- Implement proper input validation
- Consider obfuscation with ProGuard/R8
- Handle email password with secure methods
- Test for potential data leakage

## Known Issues and Limitations

- SMS receiving limitations on newer Android versions
- Background processing restrictions on Android 8+
- Possible email delivery delays due to network conditions
- Battery optimization may interfere with service reliability

## Future Improvements

- Cloud backup for settings
- Multiple email recipient support
- Advanced OTP detection algorithms
- Integration with password managers
- Support for OTPs received via other channels (e.g., WhatsApp)
