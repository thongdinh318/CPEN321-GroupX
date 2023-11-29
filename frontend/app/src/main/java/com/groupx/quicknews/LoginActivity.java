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

    private static GoogleSignInAccount account;
    private static String userId;
    private static String token;
    private int RC_SIGN_IN = 1;
    final static String TAG = "LoginActivity";

    // ChatGPT usage: No.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SignInButton googleSignInButton;

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
            // ChatGPT usage: No.
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    // ChatGPT usage: No.
    @Override
    protected void onStart() {
        super.onStart();
        //sign in previously signed in account
        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
        new OnCompleteListener<GoogleSignInAccount>() {
            // ChatGPT usage: No.
            @Override
            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                handleSignInResult(task);
            }
        });
    }
    // ChatGPT usage: No.
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // ChatGPT usage: No.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // ChatGPT usage: No.
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            if (account == null) {
                updateUI();
            }
            else {
                // Signed in successfully, show authenticated UI.
                String idToken = account.getIdToken();
                validateToken(idToken);
            }
        } catch (ApiException e) {

            //AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            //builder.setMessage("Error occurred while Signing in. Please try again").setTitle("Sign In Failed");
            //AlertDialog dialog = builder.create();

            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    // ChatGPT usage: No.
    private void validateToken(String idToken) {
        String url = getString(R.string.server_dns) + "signin";
        try {
            JSONObject json = new JSONObject();
            json.put("idToken", idToken);
            HttpClient.postRequest(url, json.toString(), new HttpClient.ApiCallback(){
                // ChatGPT usage: No.
                @Override
                public void onResponse(Response response) {

                    try{
                        int statusCode = response.code();
                        if (statusCode == 200){
                            String res = response.body().string();
                            Log.d(TAG, res);
                            res = res.replace("\"", "\'");
                            JSONObject jsonRes = new JSONObject(res);
                            //JSONObject user = jsonRes.getJSONObject("user");
                            token = jsonRes.getString("jwt");

                            if (jsonRes.has("userId")){
                                userId = jsonRes.getString("userId");
                                updateUI();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // ChatGPT usage: No.
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

    // ChatGPT usage: No.
    private void updateUI() {
        if (account == null) {
            Log.d(TAG, "No user signed in");
        }
        else {
            Intent signInIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(signInIntent);
        }
    }

    public static GoogleSignInAccount getAccount() {
        return account;
    }

    public static String getUserId() {
        return userId;
    }
    public static String getJWT(){return token;}
}