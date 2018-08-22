package edu.example.af_flash_chat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

  private final int FB_MIN_PAS_LEN = 6; // firebase minimal password length (system)

  static final String LS_STORAGE_FILE = "ChatPrefs"; // storage file name (in SharedPreferences)
  static final String LS_USERNAME = "username"; // access key for stored username value (in ChatPrefs)

  AutoCompleteTextView mAtvUsername;
  AutoCompleteTextView mAtvEmail;
  EditText mEtPassword;
  EditText mEtRepassword;
  Button mBtnRegister;

  private FirebaseAuth mAuth; // firebase.auth

  /************************************************************************/
  /*  */
  /************************************************************************/

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);

    mAtvUsername = findViewById(R.id.atvUsername);
    mAtvEmail = findViewById(R.id.atvEmail);
    mEtPassword = findViewById(R.id.etPassword);
    mEtRepassword = findViewById(R.id.etRepassword);
    mBtnRegister = findViewById(R.id.btnRegister);

    mAuth = FirebaseAuth.getInstance();

    // ENTER KEY PRESSED
    mEtRepassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.register_form_finished || id == EditorInfo.IME_NULL) /* IME_NULL is the generic key for ENTER */ {
          attemptRegistration();
          return true;
        }
        return false;
      }
    });

  }

  public void onClickRegisterUser(View view) {
    attemptRegistration();
  }

  /************************************************************************/
  /* ATTEMPT REGISTRATION */
  /************************************************************************/

  private boolean isEmailValid(String email) {
    return email.contains("@");
  }

  private boolean isPasswordValid(String password) {
    String confirmPassword = mEtRepassword.getText().toString();
    return confirmPassword.equals(password) && password.length() > FB_MIN_PAS_LEN;
  }

  private void attemptRegistration() {

    // Reset errors displayed in the form.
    mAtvEmail.setError(null);
    mEtPassword.setError(null);

    // GET VIEW VALUES
    String email = mAtvEmail.getText().toString();
    String password = mEtPassword.getText().toString();

    boolean cancel = false;
    View focusView = null;

    // EMAIL LOCAL VALIDATION
    if (TextUtils.isEmpty(email)) {
      mAtvEmail.setError(getString(R.string.error_field_required));
      focusView = mAtvEmail;
      cancel = true;
    } else if (!isEmailValid(email)) {
      mAtvEmail.setError(getString(R.string.error_invalid_email));
      focusView = mAtvEmail;
      cancel = true;
    }

    // PASSWORD LOCAL VALIDATION
    if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
      mEtPassword.setError(getString(R.string.error_invalid_password));
      focusView = mEtPassword;
      cancel = true;
    }

    if (cancel) {
      // FOCUS TO ERROR VIEW
      focusView.requestFocus();
    }
    else {
      // CREATE FIREBASE USER
      createFirebaseUser();
    }
  }

  /************************************************************************/
  /* CREATE NEW FIREBASE USER */
  /************************************************************************/

  private void createFirebaseUser() {
    String email = mAtvEmail.getText().toString();
    String password = mEtPassword.getText().toString();

    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
      @Override public void onComplete(@NonNull Task<AuthResult> task) {
        Log.d("Dev", "\tCreate user on complete: "+task.isSuccessful());
        if (!task.isSuccessful()) {
          Log.d("Dev", "\tUser creation failed!\n"+task.getException());
          // ERROR DIALOG
          showErrorDialog("Registration attempt failed!");
        }
        else {
          Log.d("Dev", "\tUser creation success!");
          // STORE USERNAME
          saveDisplayName();
          // LOGIN ACTIVITY
          Intent intentLogin = new Intent(SignupActivity.this, LoginActivity.class);
          finish();
          startActivity(intentLogin);
        }
      }
    });

  }

  /************************************************************************/
  /* SHOW ERROR DIALOG */
  /************************************************************************/

  private void showErrorDialog(String message) {
    new AlertDialog.Builder(this)
      .setTitle("Error")
      .setMessage(message)
      .setPositiveButton(android.R.string.ok, null)
      .setIcon(android.R.drawable.ic_dialog_alert)
      .show();
  }

  /************************************************************************/
  /* STORE DATA IN LOCAL STORAGE (SHARED PREFERENCES) */
  /************************************************************************/

  private void saveDisplayName() {
    String displayName = mAtvUsername.getText().toString();

    // STORE DATA
    SharedPreferences prefs = getSharedPreferences(LS_STORAGE_FILE, MODE_PRIVATE);
    prefs.edit().putString(LS_USERNAME, displayName).apply();
  }



}
