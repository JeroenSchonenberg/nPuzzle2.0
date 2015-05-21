package nl.han.ica.mad.s478416.npuzzle.activities.gametypes;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.internal.constants.RoomStatus;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.InjectView;
import nl.han.ica.mad.s478416.npuzzle.R;
import nl.han.ica.mad.s478416.npuzzle.activities.MainMenuActivity;
import nl.han.ica.mad.s478416.npuzzle.utils.ByteUtils;

public abstract class AbstractMultiplayerGameActivity extends AbstractGameActivity implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, RealTimeMessageReceivedListener, RoomUpdateListener, RoomStatusUpdateListener {
	private static String TAG = "AbstractMultiplayerGameActivity";

	private static int RC_SIGN_IN = 9001;
	final static int RC_WAITING_ROOM = 10002;
	private static final char READY = 'R';
	private static final char SHUFFLE = 'S';
	private static final char MOVE = 'M';
	private static final char FINISHED = 'F';
	private static final char QUIT = 'Q';
	private static final int NUMBER_OF_OPPONENTS = 2;

	protected GoogleApiClient googleApiClient;

	private String roomId;
	private String myId = null;
	private Participant opponent = null;

	private TextView connectionStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		googleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
				.addApi(Games.API).addScope(Games.SCOPE_GAMES)
				.build();
	}

	@Override
	protected void onStart() {
		if (googleApiClient == null || !googleApiClient.isConnected()) {
			Log.d(TAG, "Connecting to Google API");
			googleApiClient.connect();
		}

		super.onStart();
	}

	@Override
	protected void onStop() {
		leaveRoom();
		super.onStop();
	}

	public void onConnected(Bundle bundle) {
		Log.d(TAG, "onConnected");
		startQuickGame();
	}

	public void onConnectionSuspended(int cause) {
		Log.d(TAG, "onConnectionSuspended. Trying to reconnect...");
		googleApiClient.connect();
	}

	public void onConnectionFailed(ConnectionResult result) {
		Log.d("MAD", "Connection to Google Api Client failed" + result.toString());

		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, RC_SIGN_IN);
			} catch (IntentSender.SendIntentException e) {
				Log.d("MAD", e.getMessage());
				googleApiClient.connect();
			}
		}
	}

	protected void startQuickGame() {
		final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;

		Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS, MAX_OPPONENTS, 0);

		RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
		rtmConfigBuilder.setMessageReceivedListener(this);
		rtmConfigBuilder.setRoomStatusUpdateListener(this);
		rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);

		Games.RealTimeMultiplayer.create(googleApiClient, rtmConfigBuilder.build());
	}


	@Override public void onDisconnectedFromRoom(Room room) {
		roomId = null;
		//goToMainMenu();
	}

	@Override
	public void onLeftRoom(int i, String s) {
		goToMainMenu();
	}

	// room events
	@Override public void onRoomCreated(int i, Room room) 						{ updateRoom(room); }
	@Override public void onRoomAutoMatching(Room room) 						{ updateRoom(room); }
	@Override public void onRoomConnecting(Room room) 							{ updateRoom(room); }
	// peer events
	@Override public void onPeerLeft(Room room, List<String> strings) 			{ updateRoom(room); }
	@Override public void onPeerDeclined(Room room, List<String> strings) 		{ updateRoom(room); }
	@Override public void onPeerInvitedToRoom(Room room, List<String> strings) 	{ updateRoom(room); }
	@Override public void onPeerJoined(Room room, List<String> strings) 		{ updateRoom(room); }
	@Override public void onPeersConnected(Room room, List<String> strings) 	{ updateRoom(room); }
	@Override public void onPeersDisconnected(Room room, List<String> strings) 	{ updateRoom(room); }
	@Override public void onP2PDisconnected(String participant) {}
	@Override public void onP2PConnected(String participant) {}

	public void updateRoom(Room room) {
		roomId = room.getRoomId();
	}

	@Override
	public void onJoinedRoom(int statusCode, Room room) {
		Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
		if (statusCode != GamesStatusCodes.STATUS_OK) {
			Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
		}
	}

	@Override
	public void onConnectedToRoom(Room room) {
		Log.d(TAG, "onConnectedToRoom");

		roomId = room.getRoomId();
		myId = room.getParticipantId(Games.Players.getCurrentPlayerId(googleApiClient));
		opponent = room.getParticipants().get(0).getParticipantId() == myId ? room.getParticipants().get(1) : room.getParticipants().get(0);

		Log.d(TAG, "roomId = " + roomId + " - myID = " + myId + " opponentId = " + opponent.getParticipantId());
	}

	@Override
	public void onRoomConnected(int statusCode, Room room) {
		if (statusCode != GamesStatusCodes.STATUS_OK) {
			Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
		} else {
			updateRoom(room);
		}

		Log.d(TAG, "ON ROOM CONNECTED ON ROOM CONNECTED ON ROOM CONNECTED");

		Log.d(TAG, "roomId = " + roomId + " - myID = " + myId + " opponentId = " + opponent.getParticipantId());
		sendReady();
	}

	@Override
	public void onRealTimeMessageReceived(RealTimeMessage rtm) {
		byte[] data = rtm.getMessageData();

		Log.d("MAD", "REAL TIME MESSAGED RECEIVED : " + data[0]);

		switch(data[0]){
			case READY:
				onOpponentReady();
				break;
			case SHUFFLE:
				int[] sequence = {3, 4, 5};
				// decode shuffle sequence from byte[]
				onReceivedShuffle(sequence);
				break;
			case MOVE:
				int pieceId = (int) data[1];
				onOpponentMove(pieceId);
				break;
			case FINISHED:
				int time = 3;
				onOpponentFinished(time);
				break;
			case QUIT:
				onOpponentQuit();
				break;
		}
	}

	protected abstract void onOpponentReady();
	protected abstract void onReceivedShuffle(int[] sequence);
	protected abstract void onOpponentMove(int pieceId);
	protected abstract void onOpponentFinished(int time);
	protected abstract void onOpponentQuit();

	protected void sendReady(){
		byte[] msg = { (byte) READY };
		Log.d("MAD", "SENDING MESSAGE: READY - TO: " + opponent.getDisplayName());
		Games.RealTimeMultiplayer.sendReliableMessage(googleApiClient, null, msg, roomId, opponent.getParticipantId());
	}

	protected void sendShuffleSequence(int[] sequence){
		byte[] msg = new byte[sequence.length + 1];
		msg[0] = (byte) SHUFFLE;
		for(int i = 0; i <= sequence.length; i++){
			msg[i + 1] = (byte) sequence[i];	// shuffle id's wont be larger than 127, no risk for overflow
		}
		Games.RealTimeMultiplayer.sendReliableMessage(googleApiClient, null, msg, roomId, opponent.getParticipantId());
	}

	protected void sendMove(int pieceId){
		byte[] msg = { (byte) MOVE, (byte) pieceId };
		Games.RealTimeMultiplayer.sendReliableMessage(googleApiClient, null, msg, roomId, opponent.getParticipantId());
	}

	protected void sendFinished(int time){
		byte[] timeByteArray = ByteUtils.intToByteArray(time);
		byte[] msg = { (byte) FINISHED, timeByteArray[0], timeByteArray[1], timeByteArray[2], timeByteArray[3] };
		Games.RealTimeMultiplayer.sendReliableMessage(googleApiClient, null, msg, roomId, opponent.getParticipantId());
	}

	protected void sendQuit(){
		byte[] msg = { (byte) QUIT };
		Games.RealTimeMultiplayer.sendReliableMessage(googleApiClient, null, msg, roomId, opponent.getParticipantId());
	}

	private void goToMainMenu(){
		Log.d(TAG, "CRASHED --> TO MAIN MENU");
		startActivity(new Intent(this, MainMenuActivity.class));
	}

	private void leaveRoom() {
		Log.d(TAG, "Leaving room.");

		if (roomId != null) {
			Games.RealTimeMultiplayer.leave(googleApiClient, this, roomId);
			roomId = null;
		}
	}
}
