package com.example.thoughtsharingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class SigninActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = SigninActivity.class.getSimpleName();
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        firebaseAuth = FirebaseAuth.getInstance();


        //This code starts main activity if user is signed in
        if (firebaseAuth.getUid() != null) {
            //User is already signed in and therefore start MainActivity
            Intent intent = new Intent(SigninActivity.this, MainActivity.class);
            startActivity(intent);

        }


    }

    public void tapToSignIn(View view) {
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false, true)
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build()
                                    )).build()
                    , RC_SIGN_IN);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                //User is just signed in and therefore start MainActivity

                Log.e(TAG, "user is signed in");
                Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                startActivity(intent);

            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Log.e(TAG, "User pressed back button");
                    //TODO: What are we planning to do here?
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.e(TAG, "user is signed inNo network");
                    //TODO: Notify user that there is no network
                    return;
                }

                //If it fails for any other unknown reason Sign in
                //signIn();
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }
}
