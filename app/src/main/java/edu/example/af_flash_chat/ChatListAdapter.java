package edu.example.af_flash_chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {

  private Activity mActivity;
  private DatabaseReference mDatabaseReference;
  private String mDisplayName;
  private ArrayList<DataSnapshot> mSnapshotList; // !!! contains data from a Firebase Database location

  /********************************************************************************/
  /* DB LISTENER */
  /********************************************************************************/

  private ChildEventListener mListener = new ChildEventListener() {

    @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
      mSnapshotList.add(dataSnapshot);
      notifyDataSetChanged();
    }

    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {  }
    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {  }
    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {  }
    @Override public void onCancelled(DatabaseError databaseError) {  }
  };

  /********************************************************************************/
  /*  */
  /********************************************************************************/

  public ChatListAdapter(Activity mainActivity, DatabaseReference dbReference, String displayName) {

    mActivity = mainActivity;
    mDisplayName = displayName;
    mDatabaseReference = dbReference.child("messages"); // !!! specified child

    mSnapshotList = new ArrayList<>();

    // SET DB LISTENER
    mDatabaseReference.addChildEventListener(mListener);
  }

  // HELPER CLASS INTRODUCING LAYOUT ITEM CHAT
  private static class ViewHolder{
    TextView authorName;
    TextView messageBody;
    LinearLayout.LayoutParams layoutParams;
  }

  ////////////////////////////////////////////////////////////////////////////////
  // BASEADAPTER ABSTRACT METHODS
  ////////////////////////////////////////////////////////////////////////////////

  @Override public long getItemId(int position) { return 0; }

  @Override public int getCount() {
    return mSnapshotList.size();
  }

  @Override public MessageInstance getItem(int position) {
    Log.d("Dev", "\tGet item...");

    DataSnapshot snapshot = mSnapshotList.get(position);

    Log.d("Dev", "\tSnapshot:\n"+snapshot.toString());

    return snapshot.getValue(MessageInstance.class); // returns the data contained in this snapshot
  }

  /********************************************************************************/
  /* GET VIEW (creates view from layout item chat filling with data) */
  /********************************************************************************/

  // LayoutInflater takes your layout XML files and creates different View objects from its contents.
  //
  // The adapters are built to reuse Views (layout),
  // so when a View is scrolled till it is no longer visible,
  // it can be used for one of the new Views appearing.
  //
  // This reused View is the convertView.
  // If this is null it means that there is no recycled View and we have to create a new one,
  // otherwise we should use it to avoid creating a new.
  //
  // The viewGroup is provided so you can inflate your view into that for proper layout parameters.

  @Override public View getView(int position, View convertView, ViewGroup viewGroup) {
    Log.d("Dev", "\tGet view...");

    // PREPARE LAYOUT
    if (convertView == null) {
      Log.d("Dev", "\tConvert view is null!");

      LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.layout_item_chat, viewGroup, false);

      final ViewHolder holder = new ViewHolder();
      holder.authorName = convertView.findViewById(R.id.tvAuthor);
      holder.messageBody = convertView.findViewById(R.id.tvMessage);

      holder.layoutParams = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();

      convertView.setTag(holder);
    }

    Log.d("Dev", "\tConvert view:\n"+convertView.toString());

    // GET DATA OBJECT FROM DB
    final MessageInstance messageInstance = getItem(position);

    // GET DATA FROM RETRIEVED OBJECT
    String author = messageInstance.getAuthor();
    String message = messageInstance.getMessage();

    // FILL LAYOUT
    final ViewHolder holder = (ViewHolder) convertView.getTag();

    holder.authorName.setText(author);
    holder.messageBody.setText(message);

    // CUSTOMIZE VIEW
    boolean isMe = messageInstance.getAuthor().equals(mDisplayName);
    setChatRowAppearance(isMe, holder);

    return convertView; // for next getView() until getSize()
  }

  /********************************************************************************/
  /* CUSTOMIZE VIEW */
  /********************************************************************************/

  private void setChatRowAppearance(boolean isItMe, ViewHolder holder) {

    if (isItMe) {
      holder.layoutParams.gravity = Gravity.END;
      holder.authorName.setTextColor(Color.GREEN);
      holder.messageBody.setBackgroundResource(R.drawable.bubble2);
    }
    else {
      holder.layoutParams.gravity = Gravity.START;
      holder.authorName.setTextColor(Color.BLUE);
      holder.messageBody.setBackgroundResource(R.drawable.bubble1);
    }

    holder.authorName.setLayoutParams(holder.layoutParams);
    holder.messageBody.setLayoutParams(holder.layoutParams);

  }

  /********************************************************************************/
  /* FREE LISTENER */
  /********************************************************************************/

  void cleanUp() {
    mDatabaseReference.removeEventListener(mListener);
  }

}