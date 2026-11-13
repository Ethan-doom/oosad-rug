# R.U.G. — Social Gathering Scheduling App

## Project Information

**Course:** MO-IT115 — Object-oriented System Analysis and Design  
**Section:** H3101  
**Term:** Term 1, SY 2026–2027  
**Team:** Team No Name

### Group Members

- **Ethan Malcolm Porte** — Team Leader
- **Aira Joie Piopongco**

## Description

R.U.G. is a mobile application prototype for helping friend groups agree on a date for a casual gathering. Members can join Circles, view gatherings, submit their availability, and review suggested dates. Organizers can review member availability, finalize a schedule, or cancel a gathering.

The prototype is implemented as an Android application using Kotlin and Jetpack Compose. It demonstrates the main behavioral flow described in the project's use-case and sequence diagrams, including availability validation, suggested-date ranking, attendance status, notifications, Circle invitations, and organizer actions.

## Current Prototype Scope

The current version includes:

- Local login and registration form validation
- Viewing and creating Circles
- Accepting a sample Circle invitation
- Creating gatherings with date options
- Selecting Available, Maybe, or Unavailable
- Validation for incomplete availability responses
- Suggested-date display and ranking
- Attendance statuses: Going, On the way, and Running late
- Organizer review of member availability
- Finalizing or cancelling a gathering
- Viewing and marking notifications as read
- Basic profile and logout screens

This is a functional interface prototype. Data is stored in memory during the session. Backend authentication, persistent storage, real multi-user schedule matching, reminders, invitation links, comments, and external calendar integration are outside the current prototype scope.

## Requirements

- Windows, macOS, or Linux
- IntelliJ IDEA
- Kotlin command-line toolchain
- Android SDK command-line tools
- Android SDK Platform-Tools

Android Studio is not required.

## Project Setup

1. Clone or open this repository in IntelliJ IDEA.
2. Configure the Android SDK path in IntelliJ IDEA.
3. Ensure the SDK contains the required command-line tools and Platform-Tools.
4. On Windows, confirm that this file exists:

   ```text
   C:\Users\<username>\AppData\Local\Android\Sdk\cmdline-tools\latest\bin\sdkmanager.bat
   ```

5. Build the project from the repository root:

   ```powershell
   .\kotlin.bat build
   ```

Generated files are stored in `build/` and are excluded from version control.

## How to Use the Prototype

1. Launch the application from IntelliJ IDEA using an Android emulator or connected Android device.
2. Enter any non-empty email and password, then select **Log in**.
3. On the Circles page, review the pending **Weekend Crew** invitation from Mika.
4. Select **Accept Invitation** to join the Circle.
5. Open **Team Ryzza** and select **View Dinner gathering**.
6. Select an availability status for each date:
   - Friday — Maybe
   - Saturday — Available
   - Sunday — Unavailable
7. Select **Submit Availability**.
8. Review the suggested dates and the confirmation message.
9. Select an attendance status.
10. Select **Review Member Availability** to open the organizer view.
11. Select **Finalize Gathering Schedule** or **Cancel Gathering**.
12. Open **Alerts** to view notifications and mark them as read.
13. Open **Profile** to review account information or log out.

To demonstrate validation, return to the gathering, leave one date unanswered, and select **Submit Availability**. The application displays an error and does not save the incomplete response.

## Relationship to the Behavioral Analysis

The primary demonstrated flow corresponds to:

- **UC-02:** View Circles
- **UC-03:** Create Circle
- **UC-06:** Accept Circle Invitation
- **UC-07:** Create Gathering
- **UC-08:** Add Gathering Options
- **UC-09:** View Gathering Details
- **UC-11:** Finalize Gathering Schedule
- **UC-12:** Submit Availability
- **UC-13:** Match Member Schedules
- **UC-16:** Update Attendance Status
- **UC-17:** View Notifications
- **UC-18:** Mark Notification as Read
- **UC-19:** View Suggested Dates

The implementation represents schedule matching and member availability with local sample data for demonstration purposes.
