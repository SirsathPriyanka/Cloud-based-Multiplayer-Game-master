package com.example.vaidi.game3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.AllRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveUserInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;
import com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener;


public class RoomlistActivity extends Activity implements ZoneRequestListener, RoomRequestListener, ConnectionRequestListener{

    //	private WarpClient theClient;
    private RoomlistAdapter roomlistAdapter;
    private TextView textViewRoomSearch;
    private ListView listView;
    private ProgressDialog progressDialog;
    private Handler UIThreadHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_list);
        listView = (ListView)findViewById(R.id.roomList);
        textViewRoomSearch = (TextView)findViewById(R.id.textViewRoomSearch);
        roomlistAdapter = new RoomlistAdapter(this);
        //	init();

    }
    private void init(){
        try {
            Utilities.theClient = WarpClient.getInstance();
        } catch (Exception ex) {
            Utils.showToastAlert(this, "Exception in Initilization");
        }
    }
    public void onStart(){
        super.onStart();
        Utilities.getWarpClient().addConnectionRequestListener(this);
        // theClient.addZoneRequestListener(this);
        //theClient.getRoomInRange(1, 1);
    }
    public void onStop(){
        super.onStop();
        Utilities.getWarpClient().removeConnectionRequestListener(this);
        // theClient.removeZoneRequestListener(this);
        //theClient.removeRoomRequestListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utilities.theClient.disconnect();
    }
    private void onRoomFound(final boolean success){
        // yay go to game scene

        UIThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                if(success){
                    Intent myIntent = new Intent(RoomlistActivity.this, GameActivity.class);
                    startActivity(myIntent);
                }
            }
        });
    }
    @Override
    public void onConnectDone(ConnectEvent evt) {
        Log.d("AppWarpTrace", "onConnectDone "+evt.getResult());
        if(evt.getResult() == WarpResponseResultCode.SUCCESS){
            findRoom();
        }
        else{
            onRoomFound(false);
        }

    }
    public void joinRoom(String roomId){
        if(roomId!=null && roomId.length()>0){
            Utilities.theClient.joinRoom(roomId);
            Utilities.theClient.addRoomRequestListener(this);
            if(progressDialog!=null){
                progressDialog.setMessage("joining room...");
            }else{
                progressDialog = ProgressDialog.show(this, "", "joining room...");
            }
        }else{
            Log.d("joinRoom", "failed:"+roomId);
        }
    }

    public void onJoinNewRoomClicked(View view){
        progressDialog = ProgressDialog.show(this,"","Please wait...");
        progressDialog.setCancelable(true);
        //theClient.createRoom(""+System.currentTimeMillis(), "Vaidehi", 2, null);
        //Utilities.getWarpClient().createTurnRoom("dynamic", "dev", 2, null, 10);
        findRoom();
    }

    public void findRoom() {
        Utilities.getWarpClient().addRoomRequestListener(this);
        Utilities.getWarpClient().addZoneRequestListener(this);
        Utilities.getWarpClient().joinRoomInRange(1, 1, true);
        Utilities.isLocalPlayerX = true;
    }

    @Override
    public void onCreateRoomDone(final RoomEvent event) {
        Log.d("AppWarpTrace", "onCreateRoomDone "+event.getResult());
        if(event.getResult() == WarpResponseResultCode.SUCCESS){
            Utilities.isLocalPlayerX = false;
            Utilities.getWarpClient().joinRoom(event.getData().getId());
        }
    }

    @Override
    public void onDeleteRoomDone(RoomEvent event) {

    }
    @Override
    public void onGetAllRoomsDone(AllRoomsEvent event) {

    }
    @Override
    public void onGetLiveUserInfoDone(LiveUserInfoEvent event) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onGetMatchedRoomsDone(final MatchedRoomsEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RoomData[] roomDataList = event.getRoomsData();
                if(roomDataList.length>0){
                    textViewRoomSearch.setText("Please select any room.");
                    roomlistAdapter.setData(roomDataList);
                    listView.setAdapter(roomlistAdapter);
                }else{
                    textViewRoomSearch.setText("No room found.");
                    roomlistAdapter.clear();
                }
            }
        });
    }
    @Override
    public void onGetOnlineUsersDone(AllUsersEvent arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onSetCustomUserDataDone(LiveUserInfoEvent arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onGetLiveRoomInfoDone(LiveRoomInfoEvent arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onJoinRoomDone(final RoomEvent event) {
        Log.d("AppWarpTrace", "onJoinRoomDone "+event.getResult());
        if(event.getResult() == WarpResponseResultCode.SUCCESS){
            Utilities.getWarpClient().subscribeRoom(event.getData().getId());
        }
        else{
            Utilities.getWarpClient().createTurnRoom("dynamic", "dev", 2, null, 10);
        }
    }
    private void goToGameScreen(String roomId){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("roomId", roomId);
        startActivity(intent);
    }

    @Override
    public void onLeaveRoomDone(RoomEvent arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onSetCustomRoomDataDone(LiveRoomInfoEvent arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onSubscribeRoomDone(RoomEvent arg0) {
        Log.d("AppWarpTrace", "onSubscribeRoomDone "+arg0.getResult());
        if(arg0.getResult() == WarpResponseResultCode.SUCCESS){
            Utilities.game_room_id = arg0.getData().getId();
            onRoomFound(true);
        }
    }
    @Override
    public void onUnSubscribeRoomDone(RoomEvent arg0) {
        // TODO Auto-generated method stub


    }
    @Override
    public void onUpdatePropertyDone(LiveRoomInfoEvent arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onLockPropertiesDone(byte arg0) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onUnlockPropertiesDone(byte arg0) {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener#onGetRoomsCountDone(com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent)
     */
    @Override
    public void onGetRoomsCountDone(RoomEvent arg0) {
        // TODO Auto-generated method stub

    }
    /* (non-Javadoc)
     * @see com.shephertz.app42.gaming.multiplayer.client.listener.ZoneRequestListener#onGetUsersCountDone(com.shephertz.app42.gaming.multiplayer.client.events.AllUsersEvent)
     */
    @Override
    public void onGetUsersCountDone(AllUsersEvent arg0) {
        // TODO Auto-generated method stub

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
