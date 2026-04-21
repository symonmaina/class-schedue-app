# Class Schedule Android Application

## Project Overview
The Class Schedule Application is an Android app designed to help students manage their class schedules efficiently. The application provides an intuitive interface for students to add, view, and modify their class schedules.

## Features
- Add and edit classes
- View class schedules by day or week
- Set reminders for classes
- Dark mode support
- Sync schedules with a calendar

## Technical Stack
- **Programming Language**: Kotlin
- **Framework**: Android SDK
- **Database**: Room Database
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Networking**: Retrofit

## Installation Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/symonmaina/class-schedue-app.git
   ```
2. Open the project in Android Studio.
3. Ensure you have the necessary SDKs installed.
4. Build and run the application on an Android device or emulator.

## Usage Guide
- Upon launching the app, you will be greeted with a simple interface.
- Use the "Add Class" button to enter details about your classes.
- Access the schedule view to see your classes for the week.
- Set reminders by tapping on a class and selecting the reminder option.

## Architecture
The application follows the MVVM architecture, separating the UI, business logic, and data layers. The UI interacts with the ViewModel, which communicates with the Repository for data access.

## Contributing Guidelines
We welcome contributions! Please follow these steps:
1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and commit them with clear messages.
4. Push your changes and submit a pull request.

## License
This project is licensed under the MIT License. See the LICENSE file for details.