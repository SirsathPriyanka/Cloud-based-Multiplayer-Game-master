package com.example.vaidi.game3;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.AllRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity implements ConnectionRequestListener {

    private EditText nameEditText;
    private ProgressDialog progressDialog;
    private Handler UIThreadHandler = new Handler();
  //  private RoomFinder roomFinder = new RoomFinder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameEditText = (EditText) findViewById(R.id.editTextName);
    }

    @Override
    public void onStart(){
        super.onStart();
        Utilities.getWarpClient().addConnectionRequestListener(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        Utilities.getWarpClient().removeConnectionRequestListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void onConnectClicked(View view){
        String userName = nameEditText.getText().toString();
        if(userName.length()>0){
            progressDialog = ProgressDialog.show(this, "", "Please wait...");
            progressDialog.setCancelable(true);
            Utilities.getWarpClient().connectWithUserName(userName);
            Utilities.localUsername = userName;

        }else{
               System.out.print("error after progress bar");
        }
    }

    @Override
    public void onConnectDone(ConnectEvent evt) {
        Log.d("AppWarpTrace", "onConnectDone "+evt.getResult());
        if(evt.getResult() == WarpResponseResultCode.SUCCESS){
           // roomFinder.findRoom();
            Intent myIntent = new Intent(MainActivity.this, RoomlistActivity.class);
            startActivity(myIntent);
        }
        else{
            //onRoomFound(false);
            Log.d("GNonConnectfailed","in else part of onConnectDone");
        }

    }

    @Override
    public void onDisconnectDone(ConnectEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onInitUDPDone(byte arg0) {
        // TODO Auto-generated method stub

    }
}
