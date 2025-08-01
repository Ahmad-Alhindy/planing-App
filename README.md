Planning App
Hi! I built this calendar app as a training project and also for personal use. It's simple, works offline, and does exactly what I need.

What is this app?
It's a calendar app for Android, built with Jetpack Compose. You can create appointments, use templates for recurring events, and get reminders so you don't miss anything important.

Everything runs locally on your device — no cloud, no data collection, just your schedule stored safely on your phone.

Features
Monthly calendar view – clean and easy to navigate

Weekly schedule – see your week hour by hour

Appointment templates – create once, reuse anytime

Smart notifications – reminders 1 day, 1 hour, and 15 minutes before

Dark mode

Fully offline – no internet required

Tech stack
Kotlin

Jetpack Compose for UI

Room Database for local storage

MVVM architecture

WorkManager for background notifications

The notification system was probably the trickiest part — getting WorkManager to behave consistently across Android versions took some work.

Installation
Requirements:

Android Studio

Android SDK 24+

Kotlin support

Steps:

bash
Copy
Edit
git clone <repository-url>
cd planning-app
./gradlew build
Run the app on an emulator or device via Android Studio.

How to use it
Open the app

Tap a date on the calendar

Choose a template or create a new appointment

Add title, time, description

Save

The template system is especially useful — create your recurring items once (like “Daily Standup”) and just reuse them.

Notifications
When you launch the app the first time, it’ll ask for notification permissions. If you allow it, you’ll get reminders:

24 hours before

1 hour before

15 minutes before

You can tap the notification to open the app directly.

Dark mode
You’ll find the option in the settings (menu → Settings). Your choice is saved automatically.

Project structure
bash
Copy
Edit
app/src/main/java/com/example/planingapp/
├── db/         # Local database
├── logic/      # ViewModels and business logic  
├── views/      # Main UI screens
├── subView/    # Reusable UI components
└── ui/theme/   # Theming
This was built during a bunch of late nights with too much coffee.
If it’s helpful for you, feel free to give it a star — always nice to know it made a difference.
