# SMS OTP Email Forwarder - User Guide

## Getting Started

### Installation

1. Download the application directly from [here](https://github.com/yourusername/Android_OTP_SMS_to_email/releases/latest/download/sms-otp-email-forwarder.apk)
2. Install the application on your Android device
3. Open the application

### Initial Setup

1. When you first open the application, you'll be prompted to grant permissions:

   - SMS receiving permission: Required to detect incoming OTP messages
   - Internet permission: Required to send emails

2. Configure Email Settings:

   - Recipient Email: Enter the email address where you want to receive OTP codes
   - SMTP Server Settings:
     - Server address (e.g., smtp.gmail.com)
     - Port number (e.g., 587 for TLS)
     - Username (typically your email address)
     - Password (for Gmail, you may need to use an App Password)
   - Test the configuration by tapping "Test Email Settings"

3. OTP Detection Settings:

   - Choose which types of messages should be forwarded:
     - All messages containing numbers
     - Only messages with specific OTP formats
     - Messages from specific senders
   - Set custom keywords that identify OTP messages (optional)

4. Activate the Service:
   - Toggle the main switch to enable OTP forwarding
   - The app will now run in the background

## Using the App

### Main Dashboard

The main screen displays:

- Service status (Active/Inactive)
- Last forwarded OTP timestamp
- Number of successfully forwarded messages
- Quick toggle for enabling/disabling the service

### Viewing Forwarded Messages

1. Tap on "Message History" to view a log of forwarded messages
2. Each entry shows:
   - Date and time
   - Sender (phone number)
   - Detected OTP code
   - Forwarding status

### Troubleshooting

- If you're not receiving emails:

  1. Check your email settings
  2. Verify the service is activated
  3. Make sure battery optimization is disabled for the app
  4. Check your spam folder

- If OTPs aren't being detected:
  1. Review your OTP detection settings
  2. Add specific keywords that appear in your OTP messages
  3. Check that SMS permission is granted

### Battery Optimization

To ensure reliable operation:

1. Go to Settings > Battery > Battery Optimization
2. Find "SMS OTP Email Forwarder"
3. Select "Don't optimize"

## Advanced Settings

### Filtering Options

- Sender Whitelist: Only forward OTPs from specific numbers
- Keyword Filtering: Customize which messages get identified as OTPs
- Custom Regex: Define advanced patterns for OTP detection

### Notification Settings

- Silent Mode: Hide notification for forwarded messages
- Detailed Notifications: Show full message content in notifications
- Status Notifications: Show persistent notification when service is active

### Backup & Restore

- Export Settings: Save your configuration
- Import Settings: Restore previously saved configuration
- Reset to Default: Clear all settings

## Privacy & Security

- The app only processes SMS messages that match OTP patterns
- No message content is stored permanently
- Email credentials are encrypted on your device
- No data is shared with third parties

## Support & Feedback

If you encounter any issues or have suggestions for improvement, please open an issue in our [GitHub Issues tracker](https://github.com/yourusername/Android_OTP_SMS_to_email/issues).
