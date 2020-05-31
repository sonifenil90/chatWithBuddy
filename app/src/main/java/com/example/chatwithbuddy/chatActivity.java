package com.example.chatwithbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class chatActivity extends AppCompatActivity {

    ListView chatList;
    ArrayList<String> messages = new ArrayList<>();
    EditText msgTxt;
    ArrayAdapter arrayAdapter;
    String key,currentKey;
    String buddyname,currentName;
    String sender="",receiver="";
    RelativeLayout relativeLayout;
    TextView tv;
    AlertDialog.Builder builder;
    ArrayList<DataSnapshot> msgs = new ArrayList<>();
    ArrayList<DataSnapshot> currentmsgs = new ArrayList<>();


    public void send(View view){

        Map<String, String> chatMap = new HashMap<>();

        chatMap.put("sender", currentName);
        chatMap.put("receiver", buddyname);
        chatMap.put("Message",msgTxt.getText().toString());


        FirebaseDatabase.getInstance().getReference().child("users").child(key).child("messages").push().setValue(chatMap);
        FirebaseDatabase.getInstance().getReference().child("users").child(currentKey).child("messages").push().setValue(chatMap);


        msgTxt.setText("");

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        builder = new AlertDialog.Builder(this);


        relativeLayout = findViewById(R.id.relativeLayout);

        key = getIntent().getStringExtra("key");
        currentKey = getIntent().getStringExtra("currentkey");

        currentName = getIntent().getStringExtra("currentName");


        buddyname = getIntent().getStringExtra("buddyname");
        setTitle("Chat With "+buddyname);

        chatList = findViewById(R.id.chatList);
        msgTxt = findViewById(R.id.msgTxt);

        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,messages){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                tv = (TextView) super.getView(position,convertView,parent);
                if(tv.getText().toString().charAt(0)=='>') {
                    tv.setGravity(Gravity.LEFT);
                }
                else {
                    tv.setGravity(Gravity.RIGHT);
                }
                return tv;
            }
        };
        chatList.setAdapter(arrayAdapter);

        FirebaseDatabase.getInstance().getReference().child("users").child(currentKey).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                currentmsgs.add(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index=0;
                for(int i=0 ; i<currentmsgs.size() ; ++i)
                {
                    DataSnapshot snap = currentmsgs.get(i);
                    if(snap.getKey().equals(dataSnapshot.getKey()))
                    {
                        currentmsgs.remove(index);
                        messages.remove(index);

                    }
                    index++;
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("users").child(key).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                msgs.add(dataSnapshot);

                if(dataSnapshot.hasChild("sender")==true)
                    sender = dataSnapshot.child("sender").getValue().toString();

                if(dataSnapshot.hasChild("receiver")==true)
                    receiver = dataSnapshot.child("receiver").getValue().toString();


                if(sender.equals(buddyname) && receiver.equals(currentName))
                {
                    messages.add(">"+dataSnapshot.child("Message").getValue().toString());
                    arrayAdapter.notifyDataSetChanged();
                }

                if(receiver.equals(buddyname) && sender.equals(currentName))
                {
                    messages.add(dataSnapshot.child("Message").getValue().toString());
                    arrayAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index=0;
                for(int i=0 ; i<msgs.size() ; ++i)
                {
                    DataSnapshot snap = msgs.get(i);
                    if(snap.getKey().equals(dataSnapshot.getKey()))
                    {
                        msgs.remove(index);
                    }
                    index++;
                }
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        chatList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                DataSnapshot snapshot = msgs.get(position);

                builder.setMessage("Do you want delete this message?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(messages.get(position).toString().charAt(0)!='>') {
                                    FirebaseDatabase.getInstance().getReference().child("users").child(currentKey).child("messages").child(currentmsgs.get(position).getKey()).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("users").child(key).child("messages").child(msgs.get(position).getKey()).removeValue();
                                    Toast.makeText(chatActivity.this,"Message Successfully Deleted!",Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(chatActivity.this,"You cannot delete "+buddyname+"'s sent messages",Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("Alert!");
                alertDialog.show();
                return false;
            }
        });

    }
}
