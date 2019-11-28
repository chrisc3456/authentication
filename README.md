# Android Authentication
Example Android application, written in Kotlin, to demonstrate use of device biometrics for authentication

BiometricManager is used to first check whether biometric hardware is present on the device and biometrics have been enrolled by the user

BiometricPrompt is then used to launch the standard biometrics prompt for checking fingerprints with the user also able to opt to use an alternative form of authentication (password/passcode etc) if appropriate
