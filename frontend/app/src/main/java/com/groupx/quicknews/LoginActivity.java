package com.groupx.quicknews;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.groupx.quicknews.helpers.HttpClient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton googleSignInButton;
    private int RC_SIGN_IN = 1;
    final static String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInButton = findViewById(R.id.sign_in_button);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
                        new OnCompleteListener<GoogleSignInAccount>() {
                            @Override
                            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                                handleSignInResult(task);
                            }
                        });
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            String idToken = account.getIdToken();
            validateToken(idToken, account);
        } catch (ApiException e) {

            //AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            //builder.setMessage("Error occurred while Signing in. Please try again").setTitle("Sign In Failed");
            //AlertDialog dialog = builder.create();

            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void validateToken(String idToken, GoogleSignInAccount account) {
        String url = getString(R.string.server_dns) + "signin";
        try {
            JSONObject json = new JSONObject();
            json.put("idToken", idToken);
            HttpClient.postRequest(url, json.toString(), new HttpClient.ApiCallback(){
                @Override
                public void onResponse(Response response) {
                    Log.d(TAG, response.toString());
                    updateUI(account);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "exception", e);
                }
            });
        }
        catch(Exception e) {
            Log.e(TAG, "exception", e);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            Log.d(TAG, "No user signed in");
        }
        else {
            //send token to back end
            Intent signInIntent = new Intent(LoginActivity.this, MainActivity.class);
            //SignInIntent.putExtra("user", account.getGivenName() + " " + account.getFamilyName());
            startActivity(signInIntent);
        }
    }
}