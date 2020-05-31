package com.example.chatwithbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UsersList extends AppCompatActivity {
    ListView usersList;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> keys = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String currentName,currentkey , currentEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);


        usersList = findViewById(R.id.usersList);
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,names);
        usersList.setAdapter(arrayAdapter);


        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.child("email").getValue().toString().toLowerCase().equals(getIntent().getStringExtra("email").toLowerCase()))
                {

                  currentName = dataSnapshot.child("name").getValue().toString();
                  currentkey = dataSnapshot.getKey();
                }

                if(!(dataSnapshot.child("email").getValue().equals(getIntent().getStringExtra("email")))) {
                    String name = (String) dataSnapshot.child("name").getValue();
                    names.add(name);
                    keys.add(dataSnapshot.getKey());
                }

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(UsersList.this,chatActivity.class);
                intent.putExtra("buddyname",names.get(position));
                intent.putExtra("key",keys.get(position));
                intent.putExtra("currentkey",currentkey);
                intent.putExtra("currentName",currentName);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {

        mAuth.signOut();
        finish();
        super.onBackPressed();
    }
}
