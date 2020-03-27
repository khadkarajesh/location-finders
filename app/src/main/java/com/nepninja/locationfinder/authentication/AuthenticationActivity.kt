package com.nepninja.locationfinder.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.nepninja.locationfinder.R
import com.nepninja.locationfinder.locationreminders.RemindersActivity


/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build()
    )
    private val RC_SIGN_IN = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            login()
        } else {
            finish()
            startActivity(RemindersActivity.newIntent(this))
        }
    }

    private fun login() {
        val layoutBuilder =
            AuthMethodPickerLayout.Builder(R.layout.activity_login)
                .setGoogleButtonId(R.id.btn_gmail)
                .setEmailButtonId(R.id.btn_email)
                .build()

        val builder = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setTheme(R.style.NoActionBarTheme)
            .setIsSmartLockEnabled(false)
            .setAuthMethodPickerLayout(layoutBuilder)
            .setAvailableProviders(providers)
            .build()

        startActivityForResult(builder, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                finish()
                startActivity(RemindersActivity.newIntent(this))
            } else {
                Toast.makeText(this, "" + response?.error?.errorCode, Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, AuthenticationActivity::class.java)
        }
    }
//         TODO: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google

//          TODO: If the user was authenticated, send him to RemindersActivity

//          TODO: a bonus is to customize the sign in flow to look nice using :
    //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

//    }
}
