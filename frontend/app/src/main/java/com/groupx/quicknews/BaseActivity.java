package com.groupx.quicknews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.groupx.quicknews.databinding.ActivityBaseBinding;
import com.groupx.quicknews.helpers.HttpClient;

import java.io.IOException;

import okhttp3.Response;

public class BaseActivity extends AppCompatActivity {
    String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityBaseBinding binding = ActivityBaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_fragment_frame);
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

    // ChatGPT usage: No.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_articles, menu);
        return true;
    }

    // ChatGPT usage: No.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if( itemId == R.id.action_manage_subscriptions ) {
            Intent intent = new Intent(BaseActivity.this, SubscriptionActivity.class);
            startActivity(intent);
            return true;
        }
        else if( itemId == R.id.action_view_history ) {
            Intent intent = new Intent(BaseActivity.this, HistoryActivity.class);
            startActivity(intent);
            return true;
        }
        else if( itemId == R.id.action_logout ) {
            signOutServer();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOutServer() {
        String url = getString(R.string.server_dns) + "signout";
        Log.d(TAG,url);
        boolean success;
        HttpClient.deleteRequestWithJWT(url, new HttpClient.ApiCallback() {
            @Override
            public void onResponse(Response response) throws IOException {
                Log.d(TAG, response.body().string());
                signOutGoogle();
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }
    private void signOutGoogle() {
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
    }
}