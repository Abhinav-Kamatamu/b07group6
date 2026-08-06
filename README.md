# CSCB07 Group 6 TAAM Artifacts Android Project

This is an Android application for managing and viewing an extensive artifact collection. The application supports two levels of access: regular users and administrative users.

## User Roles

### Regular User
A regular user has access to the following features:
- **Authentication**: Securely log in using email and password.
- **Home Page**: Browse the entire artifact collection on a dedicated home screen.
- **Search & Browse**: Search for specific artifacts.
- **Detailed View**: Access an expanded view for each artifact to see its full description and metadata.
- **Social Interaction**: View likes and comments from other users.
- **Engagement**: Like or unlike artifacts and add your own comments.
- **Saved Collection**: Save artifacts to a personal collection for quick access later.
- **Profile Management**: View saved artifacts and log out securely.

### Admin User
In addition to all regular user features, an admin user can:
- **Content Management**: Add new artifacts to the collection.
- **Edit Records**: Update information for existing artifacts.
- **Removal**: Delete artifacts from the database.
- **Moderation**: Remove comments on artifacts to maintain community standards.

## Technologies Used
- **Languages**: Java, XML
- **Database**: Firebase Realtime Database
- **Authentication**: Firebase Authentication
- **Cloud Image Storage**: Supabase Storage
- **Version Control**: Git / GitHub
- **Project Management**: Jira (SCRUM model)
- **IDE**: Android Studio

## Requirements
To run the application, ensure you have the following installed:
- **JDK 17+**
- **Git**
- **Android Studio**
- **Android SDK**
- **Android Emulator** or a physical Android device.

*Note: An active internet connection is required to interact with the Firebase and Supabase backends.*

## Setup Instructions
1. **Clone the repository**:
   ```bash
   git clone https://github.com/Abhinav-Kamatamu/b07group6.git
   ```
2. **Open the project**: Launch Android Studio and open the cloned project directory.
3. **Gradle Sync**: Wait for the Gradle project sync and build process to complete.
4. **Configuration**: Ensure `google-services.json` is placed in the `app/` directory and that Supabase keys are set in `local.properties`.
5. **Run the App**: Select an Android device or emulator and click the **Run** button in Android Studio.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
