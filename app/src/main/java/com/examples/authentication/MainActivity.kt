package com.examples.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check the hardware supports the use of biometric authentication
        when (BiometricManager.from(applicationContext).canAuthenticate()) {
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> updateAuthenticationResultsOnScreen("No biometric hardware on device", false)
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> updateAuthenticationResultsOnScreen("Biometric hardware unavailable", false)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> updateAuthenticationResultsOnScreen("Biometrics have not been enrolled", false)
            else -> setupBiometrics()
        }
    }

    private fun setupBiometrics() {
        val promptInfo = getBiometricPromptInfo()
        val prompt = getBiometricPrompt()

        buttonAuthenticate.visibility = Button.VISIBLE
        buttonAuthenticate.setOnClickListener {
            prompt.authenticate(promptInfo)
        }
    }

    /**
     * Set up the parameters for the standard biometrics prompt
     * Using 'setDeviceCredentialAllowed' to true means the user can choose to use their password
     * as an alternative. Otherwise we can use 'setNegativeButtonText' to provide a 'Cancel' option
     */
    private fun getBiometricPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authentication Example")
            .setSubtitle("Additional security required")
            .setDeviceCredentialAllowed(true)
            .build()
    }

    /**
     * Set up the prompt itself to run on a separate thread. Includes callback handlers for the
     * different authentication results
     */
    private fun getBiometricPrompt(): BiometricPrompt {
        val executor = Executors.newSingleThreadExecutor()
        return BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                updateAuthenticationResultsOnScreen("Authentication error\n($errorCode) $errString", false)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                updateAuthenticationResultsOnScreen("Authentication successful", true)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                updateAuthenticationResultsOnScreen("Authentication failed", false)
            }
        })
    }

    /**
     * Update the text field and image based on the results. Note - this runs on the UI thread
     * (runOnUiThread) rather than the one used for the authentication
     */
    private fun updateAuthenticationResultsOnScreen(text: String, success: Boolean) = runOnUiThread {
        textViewResults.text = text
        imageViewResult.visibility = View.VISIBLE

        if (success) {
            imageViewResult.setImageResource(R.drawable.icon_tick)
        }
        else {
            imageViewResult.setImageResource(R.drawable.icon_cross)
        }
    }
}
