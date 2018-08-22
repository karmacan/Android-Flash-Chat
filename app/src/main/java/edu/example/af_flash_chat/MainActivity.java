package edu.example.af_flash_chat;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

  private final String DB_MESSAGES = "messages"; // storage place key for messages

  private ListView mLvChat;
  private EditText mEtMessage;
  private ImageButton mBtnSend;

  private String mDisplayName; // get from SharedPreferences

  private DatabaseReference mDbReference; // firebase.database

  private ChatListAdapter mAdapter;

  /************************************************************************/
  /*  */
  /************************************************************************/

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mLvChat = findViewById(R.id.lvChat);
    mEtMessage = findViewById(R.id.etMessage);
    mBtnSend = findViewById(R.id.btnSend);

    // DISPLAY LS CURRENT USERNAME
    setupDisplayName();

    // CHAT DB OBJECT
    mDbReference = FirebaseDatabase.getInstance().getReference();

    // KEYBOARD ENTER LISTENER
    mEtMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        sendMessage();
        return true;
      }
    });

    // MOUSE CLICK LISTENER
    mBtnSend.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        sendMessage();
      }
    });

  }

  /************************************************************************/
  /* SET UP CURRENT USER NAME FROM LS */
  /************************************************************************/

  private void setupDisplayName() {
    SharedPreferences prefs = getSharedPreferences(SignupActivity.LS_STORAGE_FILE, MODE_PRIVATE);
    mDisplayName = prefs.getString(SignupActivity.LS_USERNAME, null);
    if (mDisplayName == null) mDisplayName = "Anonymous";
  }

  /************************************************************************/
  /* SEND MESSAGE */
  /************************************************************************/

  // In firebase site
  // console -> project -> database -> realtime database -> rules
  // set
  // {"rules": {".read": true, ".write": true}}

  private void sendMessage() {
    Log.d("Dev", "\tSending message...");

    String msg = mEtMessage.getText().toString();

    if (msg.equals("")) return;

    MessageInstance message = new MessageInstance(msg, mDisplayName);

    // STORE MESSAGE TO DATABASE
    mDbReference.child(DB_MESSAGES).push().setValue(message, new DatabaseReference.CompletionListener() {
      @Override public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
        if (databaseError != null) {
          Log.d("Dev", "\tError occurred!\n"+databaseError.toString());
        }
      }
    });

    mEtMessage.setText("");
  }

  /************************************************************************/
  /* SET ADAPTER */
  /************************************************************************/

  @Override public void onStart() {
    super.onStart();
    Log.d("Dev", "\tNew list adapter...");

    mAdapter = new ChatListAdapter(this, mDbReference, mDisplayName);
    mLvChat.setAdapter(mAdapter);
  }

  /************************************************************************/
  /* STOP ADAPTER */
  /************************************************************************/

  @Override protected void onStop() {
    super.onStop();

    mAdapter.cleanUp();
  }
}
