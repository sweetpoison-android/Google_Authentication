 package com.example.googleauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;

 public class MainActivity extends AppCompatActivity {

     GoogleSignInClient mGoogleSignInClient;
     FirebaseAuth mauth;
     FirebaseUser currentUser;

     TextView name,email;
     ImageView img;
     SignInButton signInButton;
     Button signout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.main_textview1);
        email = findViewById(R.id.main_textview2);
        img = findViewById(R.id.main_imageview);
        signInButton = findViewById(R.id.sign_in_button);
        signout = findViewById(R.id.sign_out_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        mauth = FirebaseAuth.getInstance();
        currentUser = mauth.getCurrentUser();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

         mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signinintent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signinintent,0);

            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          mauth.getInstance().signOut();
          updateUI(null);
          signInButton.setVisibility(View.VISIBLE);
          signout.setVisibility(View.GONE);
          img.setVisibility(View.GONE);
            }
        });

    }


     @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
         if (requestCode == 0) {
             Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
             try {
                 // Google Sign In was successful, authenticate with Firebase
                 GoogleSignInAccount account = task.getResult(ApiException.class);
                 Toast.makeText(this, "firebase auth with google :" +account.getId().toString(), Toast.LENGTH_SHORT).show();
                 firebaseAuthWithGoogle(account.getIdToken());

             } catch (ApiException e) {
                 // Google Sign In failed, update UI appropriately
                 // ...
                 Toast.makeText(this, "google sign in failed :" +e.getMessage(), Toast.LENGTH_SHORT).show();
             }
         }
     }
     private void firebaseAuthWithGoogle(String idToken) {
         AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
         mauth.signInWithCredential(credential)
                 .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if (task.isSuccessful()) {
                             // Sign in success, update UI with the signed-in user's information
                             Log.d("TAG", "signInWithCredential:success");
                             currentUser = mauth.getCurrentUser();
                             updateUI(currentUser);
                             signInButton.setVisibility(View.GONE);
                             signout.setVisibility(View.VISIBLE);

                             //updateUI(user);
                         } else {
                             // If sign in fails, display a message to the user.
                             Log.w("TAG", "signInWithCredential:failure", task.getException());
                             updateUI(null);

                         }

                     }
                 });
     }

     @Override
     public void onStart() {
         super.onStart();
         // Check if user is signed in (non-null) and update UI accordingly.
           if (currentUser != null) {

               updateUI(currentUser);
               signInButton.setVisibility(View.GONE);
               signout.setVisibility(View.VISIBLE);
           }
           else
           {
               updateUI(null);
               signout.setVisibility(View.GONE);
               signInButton.setVisibility(View.VISIBLE);
           }
     }

     public void updateUI(FirebaseUser firebaseUser )
     {
         if (firebaseUser != null)
         {
             name.setText(firebaseUser.getDisplayName());
             email.setText(firebaseUser.getEmail());

             if (firebaseUser.getPhotoUrl() != null)
             {
                 String photourl = firebaseUser.getPhotoUrl().toString();
                 photourl = photourl + "?type=album";
                 Picasso.get().load(photourl).into(img);
                 img.setVisibility(View.VISIBLE);
                // Glide.with(this).load(photourl).into(img);
             }
         }
         else
         {
             name.setText("");
             email.setText("");
             img.setImageResource(R.drawable.ic_launcher_background);

         }
     }

 }