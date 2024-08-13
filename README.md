# Evenz-Android-App: Event Check-In and Attendance Tracking Application

## Overview

This Android application was developed to efficiently manage the check-in and check-out process for public events, providing a feature-rich platform for event organizers, attendees, and administrators. The application supports multiple user roles and offers a comprehensive suite of tools to enhance event management and user engagement. This project was a collaborative effort by a dedicated team of developers as part of a Software Development Academic Course.

## Project Methodology and Development Process

### Software Development Life Cycle (SDLC)
The application was developed following a rigorous Software Development Life Cycle (SDLC) process, which included phases such as requirement gathering, system design, implementation, testing, deployment, and maintenance. Each phase was meticulously planned and executed to ensure the delivery of a high-quality product.

### Agile Software Development
The project was managed using Agile methodologies, specifically Scrum, to facilitate iterative development, continuous feedback, and rapid adaptation to changes. Sprint cycles were established, with each cycle focusing on delivering incremental updates to the application, allowing for regular user feedback and adjustments.

### Object-Oriented Design (OOD)
The application was designed using Object-Oriented Design principles, leveraging techniques such as Class-Responsibility-Collaborator (CRC) cards and Unified Modeling Language (UML) diagrams. This approach ensured a modular, reusable, and maintainable codebase.

- **CRC Cards**: Used for defining the responsibilities and collaborations of various classes within the system.
- **UML Diagrams**: Employed to visualize the system architecture, including Class Diagrams, Sequence Diagrams, and Activity Diagrams.

### Prototyping and User Interface Design
- **UI Design Tool**: The user interface was designed using Figma, a powerful tool for creating detailed UI prototypes and facilitating collaboration between designers and developers.
- **Prototyping**: Both low-fidelity and high-fidelity prototypes were created in Figma to validate the user interface (UI) and user experience (UX). These prototypes were subjected to user testing to ensure they met the functional and aesthetic requirements of the target audience.

### Architectural Pattern
- **MVC Architecture**: The application followed the Model-View-Controller (MVC) architectural pattern, ensuring a clear separation of concerns, which enhanced the maintainability and scalability of the codebase. The MVC pattern also facilitated easier testing and debugging, contributing to the overall robustness of the application.

## Technical Stack and Architecture

### Platform and Frameworks
- **Platform**: Android (Minimum SDK 21)
- **Programming Language**: Kotlin for Android development, chosen for its modern features and strong type safety.
- **Architecture Pattern**: Model-View-Controller (MVC) was implemented to separate concerns, improve code maintainability, and facilitate easier unit testing.
- **Dependency Injection**: Dagger 2 was utilized to manage dependencies, promoting modularity and ease of testing.

### Data Management
- **Local Database**: SQLite was integrated for local data persistence, allowing for offline access to event details, user profiles, and attendance records.
- **Remote Database**: Firebase Firestore was used as a NoSQL cloud database to store event data, user profiles, and check-in records, ensuring scalability and real-time data synchronization.

### Networking
- **API Communication**: Retrofit was employed to handle RESTful API communication, allowing the app to interact with the backend server for various operations such as event creation, attendee management, and notification delivery.
- **JSON Parsing**: Moshi was used for parsing JSON data received from the server, providing a robust and efficient mechanism for data serialization and deserialization.

### User Interface and Experience
- **UI Framework**: Android Jetpack Components, including LiveData, ViewModel, and Navigation Component, were utilized to build a responsive and dynamic user interface.
- **Material Design**: Adhered to Google's Material Design guidelines to create a clean, intuitive, and consistent user experience across the application.
- **Custom Views**: Implemented custom views for QR code generation and real-time attendance tracking, providing a unique and user-friendly interface.

### QR Code Integration
- **QR Code Generation**: The ZXing library was integrated to generate and decode QR codes, enabling organizers to create unique QR codes for event check-ins, promotions, and other functionalities.
- **QR Code Sharing**: Android's `Intent` system was leveraged to allow users to share QR codes across different apps and platforms, facilitating easy distribution and access.

### Real-Time Features
- **Push Notifications**: Firebase Cloud Messaging (FCM) was used to implement push notifications, ensuring attendees and organizers receive timely updates and alerts about event activities.
- **Real-Time Data Synchronization**: Firestore's real-time capabilities were utilized to keep the attendee lists, event details, and notifications up-to-date across all devices and users.

### Geolocation and Mapping
- **Geolocation Tracking**: Integrated Google Play Services to enable geolocation tracking, allowing organizers to view where attendees are checking in from. This feature could be enabled or disabled by users as per their privacy preferences.
- **Map Visualization**: Google Maps API was used to display real-time check-in locations on a map, providing organizers with a visual overview of event attendance.

## Key Features and Functionalities

### Organizer Role
1. **Event Creation and Management**:
   - Create new events with unique QR codes for attendee check-ins.
   - Option to reuse existing QR codes for multiple events.
   - Upload event posters to provide visual information and promotional material.
   - Generate and share promotion QR codes linking to event details.
   - Limit the number of attendees for each event, if desired.

2. **Attendee Management**:
   - View real-time lists of attendees who have checked in.
   - Track attendee check-ins, including how many times an attendee has checked into events.
   - Receive alerts for significant attendance milestones.
   - Send push notifications to all attendees regarding important updates or changes.

3. **Real-Time Insights**:
   - Monitor real-time attendance with visual data on a map showing check-in locations.
   - Track and analyze attendance patterns and milestones.

### Attendee Role
1. **Event Participation**:
   - Quickly check into events by scanning the provided QR code without the need for login credentials.
   - Sign up for events and view current and future event details.
   - Browse and explore posters and details of other events.

2. **Profile Management**:
   - Upload and manage a profile picture, with options to update or remove the image.
   - Update personal information such as name, homepage, and contact details.
   - Automatically generate a profile picture based on the profile name if no image is uploaded.

3. **Notifications and Updates**:
   - Receive push notifications from organizers with event updates, reminders, and announcements.

4. **Privacy and Control**:
   - Opt in or out of geolocation tracking for event check-ins.

### Administrator Role
1. **System Management**:
   - Remove or manage events, user profiles, and images within the app.
   - Browse and manage all events, profiles, and images, ensuring compliance with platform policies.

## Contributors

This project was a group effort undertaken as part of a Software Development Academic Course. Each team member contributed significantly to the successful development and deployment of this application. Special thanks to the following contributors:

- **Aizaz Tahir**  
  GitHub: [AizazTahir](https://github.com/orgs/CMPUT301W24T17/people/AizazTahir)

- **Ben Gao**  
  GitHub: [Ben8249](https://github.com/orgs/CMPUT301W24T17/people/Ben8249)

- **Hrithick Chakraborty**  
  GitHub: [Hrithickchak](https://github.com/orgs/CMPUT301W24T17/people/Hrithickchak)

- **Omar Fazal**  
  GitHub: [OFazal](https://github.com/orgs/CMPUT301W24T17/people/OFazal)

- **Sand**  
  GitHub: [traindjb](https://github.com/orgs/CMPUT301W24T17/people/traindjb)

- **Mohammad Mohaiminul Islam Maheer**  
  GitHub: [Maheer1207](https://github.com/orgs/CMPUT301W24T17/people/Maheer1207)

## Conclusion

This Android application is a robust solution designed to manage the complexities of event check-ins and attendance tracking. It was developed with a focus on scalability, user experience, and maintainability, incorporating industry-standard practices and advanced technical solutions to meet the needs of organizers, attendees, and administrators. The combination of Agile development, OOD principles, and state-of-the-art technologies ensures that this app is both powerful and user-friendly. The success of this project is a testament to the collaborative effort and dedication of the entire team.
