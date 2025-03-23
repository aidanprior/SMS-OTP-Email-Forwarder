# SMS OTP Email Forwarder

## Overview

SMS OTP Email Forwarder is an Android application that automatically forwards SMS messages containing One-Time Passwords (OTPs) to a designated email address. This is useful for users who need to receive authentication codes on a device other than their phone.

## Features

- Automatically detects and extracts OTP codes from incoming SMS messages
- Securely forwards OTP codes to a configured email address
- Minimal battery and resource usage
- Works in the background without interfering with normal phone usage
- Simple, user-friendly interface

## Requirements

- Android device running Android 5.0 (Lollipop) or higher
- SMS receiving capabilities
- Internet connection for email forwarding
- Permissions: SMS receiving, Internet access

## Installation

1. Download the APK directly from [here](https://github.com/yourusername/Android_OTP_SMS_to_email/releases/latest/download/sms-otp-email-forwarder.apk)
2. Enable installation from unknown sources in your device settings
3. Install the application
4. Grant the required permissions when prompted

## Setup

1. Launch the application
2. Enter your email credentials (the address where OTPs will be sent)
3. Configure any additional settings (optional)
4. Enable the service using the toggle switch
5. Test the setup by receiving an OTP message

## Privacy & Security

- The application only processes SMS messages that appear to contain OTP codes
- No SMS content is stored permanently on the device
- Email credentials are stored securely using Android's encryption
- No data is sent to third-party servers other than the configured email service

## Support

For issues, feature requests, or questions, please open an issue in our [GitHub Issues tracker](https://github.com/aidanprior/SMS-OTP-Email-Forwarder/issues).

## License

This application is licensed under the GNU General Public License v3.0 (GPL-3.0).

```
SMS OTP Email Forwarder - Forward SMS OTPs to your email
Copyright (C) 2023

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```

The full license text can be found in the [LICENSE](LICENSE) file.
