# Firebase Auth Next Steps

## Current app state
- Login/Register UI is implemented
- Session handling is implemented locally
- Logout flow is implemented
- Build is stable without `google-services.json`

## Final Firebase hookup
1. Create a Firebase project
2. Add an Android app with package name:
   - `com.mindmatrix.siridhanyahub`
3. Download `google-services.json`
4. Place it here:
   - `app/google-services.json`
5. Sync the project. The Google Services plugin is now auto-enabled when that file exists.
6. Swap the local auth repository logic to Firebase Auth calls

## Firebase methods to wire later
- `FirebaseAuth.getInstance().createUserWithEmailAndPassword(...)`
- `FirebaseAuth.getInstance().signInWithEmailAndPassword(...)`
- `FirebaseAuth.getInstance().signOut()`

## Reason for current local auth layer
This keeps the app runnable and testable before the Firebase console configuration is ready.
