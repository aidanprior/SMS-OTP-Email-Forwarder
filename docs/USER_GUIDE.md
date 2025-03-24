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
   - Sender Email: Enter the email address that will be used to send the OTPs
   - Password: Enter the password for your email account

   > **Important Note**: Some email providers have additional security requirements:
   >
   > **Gmail users**: You need to use an App Password instead of your regular password.  
   > To create an App Password:
   >
   > 1. Go to [Google Account Security](https://myaccount.google.com/security)
   > 2. Enable 2-Step Verification if not already enabled
   > 3. Go to [App Passwords](https://myaccount.google.com/apppasswords)
   > 4. Select "Other (Custom name)" from the dropdown
   > 5. Enter "OTP Forwarder" and click "Generate"
   > 6. Copy the 16-character password and paste it in the app
   >
   > **Outlook/Hotmail users**: You may need to allow less secure apps in your Microsoft account settings
   >
   > **Yahoo users**: You may need to generate an app password in your account security settings

3. Basic Configuration:

   - The app uses sophisticated pattern matching to detect messages containing OTP codes
   - Detection settings are optimized for common OTP formats

4. Activate the Service:
   - Save your settings using the Save button
   - The app will automatically verify your credentials and display the status
   - Once verified, the service will automatically start processing incoming SMS messages

## Testing the App

After setting up your email credentials:

1. The app will automatically verify your email credentials when you save your settings
2. If verification is successful, you'll see a confirmation message
3. You can test the full functionality by having someone send you an SMS with a code like: "Your verification code is 123456"

## Supported Email Providers

The app automatically detects settings for the following email providers:

- Gmail (smtp.gmail.com)
- Outlook/Hotmail/Live (smtp.office365.com)
- Yahoo Mail (smtp.mail.yahoo.com)
- AOL Mail (smtp.aol.com)
- Zoho Mail (smtp.zoho.com)

For other email providers, the app will attempt to use Gmail's SMTP settings by default, which may not work.
You may need to manually configure your email account to allow access from the app.

## Current Limitations

Please note that this is an early version with the following limitations:

- No advanced filtering options yet (planned for future releases)
- No message history view
- No customizable detection settings
- No backup/restore functionality
- Limited error handling and notifications

## Troubleshooting Email Issues

If you're having trouble with email sending:

1. Check your email provider's security settings:

   - Some providers block apps from sending emails for security reasons
   - You may need to enable "Less secure apps" or generate an app-specific password
   - Check your email provider's documentation for specifics

2. Other email issues:
   - Verify your internet connection is working
   - Check that both email addresses are entered correctly
   - Try using a different sender email address
   - Some email providers may block automated emails

## Battery Optimization

For reliable operation:

1. Go to Settings > Battery > Battery Optimization
2. Find "SMS OTP Email Forwarder"
3. Select "Don't optimize"

## Privacy & Security

- The app only processes SMS messages to detect OTP codes
- Email credentials are stored securely on your device
- No data is shared with third parties

## Support & Feedback

If you encounter any issues or have suggestions for improvement, please open an issue in our [GitHub Issues tracker](https://github.com/yourusername/Android_OTP_SMS_to_email/issues).

## Advanced Features

### Plus Addressing

The app automatically uses plus addressing for supported email providers. This means:

- Your recipient email `you@gmail.com` becomes `you+SMSOTP@gmail.com`
- Your sender email `you@gmail.com` becomes `you+SMSOTPForwarder@gmail.com`

Benefits of plus addressing:

- Helps you identify OTP emails in your inbox
- Allows you to create email filters specifically for these messages
- Makes no difference to authentication - you still use your regular email credentials

Note: Plus addressing is supported by Gmail, Outlook, ProtonMail, FastMail, and several other providers. Yahoo Mail does not fully support plus addressing for sending emails, so it will be disabled automatically when using a Yahoo account as the sender.

## Email Provider Specific Notes

### Yahoo Mail Users

When using Yahoo Mail as your sender email:

- You may need to set up an app password in your Yahoo account security settings
- The app will automatically disable plus addressing for the sender email (but not for the recipient)
- Make sure you have enabled "Less secure apps" access in your Yahoo account settings

This indicator updates in real-time as SMS messages are received and processed.- **Gray "Service Inactive"**: The service is not running (check your settings and credentials)- **Green "Service Active"**: The service is correctly configured and runningThe app includes a prominent status indicator at the top of the screen that shows whether the OTP forwarding service is active:### Service Status Indicator## User Interface## Configuration Options

### Email Settings

The app offers two configuration modes:

In both modes, you'll need to provide the password for the sender email account. For many email providers, this requires an app-specific password (see provider-specific notes below).1. **Send Emails to Myself** (Default): When enabled, you only need to enter one email address which will be used as both sender and recipient. This is the simplest setup for most users.

2. **Separate Sender and Recipient**: When the "Send emails to myself" toggle is turned off, you can specify different email addresses for the sender and recipient. This is useful if you want to forward OTPs from one email account to another.
