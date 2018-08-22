package edu.example.af_flash_chat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

  AutoCompleteTextView mAtvEmail;
  EditText mEtPassword;
  Button mBtnLogin;
  Button mBtnSignup;

  private FirebaseAuth mAuth;

  /************************************************************************/
  /*  */
  /************************************************************************/

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    mAtvEmail = findViewById(R.id.atvEmail);
    mEtPassword = findViewById(R.id.etPassword);
    mBtnLogin = findViewById(R.id.btnLogin);
    mBtnSignup = findViewById(R.id.btnSignup);

    mAuth = FirebaseAuth.getInstance();

    // ENTER KEY PRESSED
    mEtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) /* IME_NULL is the generic key for ENTER */ {
        if (id == R.id.login_form_completed || id == EditorInfo.IME_NULL) {
          attemptLogin();
          return true;
        }
        return false;
      }
    });

  }

  public void onClickLoginUser(View view) {
    attemptLogin();
  }

  /************************************************************************/
  /* ATTEMPT LOGIN */
  /************************************************************************/

  private void attemptLogin() {
    String email = mAtvEmail.getText().toString();
    String password = mEtPassword.getText().toString();

    if (email.equals("") || password.equals("")) return;
    Toast.makeText(this, "Login in progress...", Toast.LENGTH_SHORT).show();

    signinFirebaseUser(email, password);

  }

  /************************************************************************/
  /* SIGN IN EXISTED FIREBASE USER */
  /************************************************************************/

  private void signinFirebaseUser(String email, String password) {

    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
      @Override public void onComplete(@NonNull Task<AuthResult> task) {
        Log.d("Dev", "\tLogin user on complete: "+task.isSuccessful());
        if (!task.isSuccessful()) {
          Log.d("Dev", "\tLogin user failed!\n"+task.getException());
          showErrorDialog("Login attempt was failed!");
        }
        else {
          Intent intentChat = new Intent(LoginActivity.this, MainActivity.class);
          finish();
          startActivity(intentChat);
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
  /* SIGNUP ACTIVITY */
  /************************************************************************/

  public void onClickSignupUser(View view) {
    Intent intentSignup = new Intent(LoginActivity.this, SignupActivity.class);
    finish();
    startActivity(intentSignup);
  }

}
