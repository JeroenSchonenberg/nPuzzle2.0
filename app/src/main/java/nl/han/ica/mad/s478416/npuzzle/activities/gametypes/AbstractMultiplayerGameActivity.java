package nl.han.ica.mad.s478416.npuzzle.activities.gametypes;

import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.internal.constants.RoomStatus;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.plus.Plus;

import java.util.List;

import butterknife.InjectView;
import nl.han.ica.mad.s478416.npuzzle.R;
import nl.han.ica.mad.s478416.npuzzle.utils.ByteUtils;

public abstract class AbstractMultiplayerGameActivity extends AbstractGameActivity implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, RealTimeMessageReceivedListener, RoomUpdateListener, RoomStatusUpdateListener{
	private static int RC_SIGN_IN = 9001;
	private static final char READY 	= 'R';
	private static final char SHUFFLE	= 'S';
	private static final char MOVE 		= 'M';
	private static final char FINISHED 	= 'F';
	private static final char QUIT 		= 'Q';

	protected GoogleApiClient googleApiClient;
	private String roomId;
	private Participant opponent;

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
		super.onStart();

		if (googleApiClient != null && googleApiClient.isConnected()) {
			Log.w("MAD", "GoogleApiClient was already connected on onStart()");
		} else {
			Log.d("MAD","Connecting Google Api Client.");
			googleApiClient.connect();
		}
		super.onStart();
	}

	public void onConnected(Bundle bundle){
		Log.d("MAD","Connected to Google Api Client!!");
		startQuickGame();
	}

	public void onConnectionSuspended(int raar){}

	public void onConnectionFailed(ConnectionResult result){
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

	protected void startQuickGame(){
		final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;

		Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS, MAX_OPPONENTS, 0);

		RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
		rtmConfigBuilder.setMessageReceivedListener(this);
		rtmConfigBuilder.setRoomStatusUpdateListener(this);
		rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);

		Games.RealTimeMultiplayer.create(googleApiClient, rtmConfigBuilder.build());
	}



	@Override
	public void onRoomAutoMatching(Room room) {
		Log.d("MAD", "Room automatching"); }

	@Override
	public void onRoomConnecting(Room room) {
		Log.d("MAD", "Room connecting");
	}


	@Override
	public void onRoomConnected(int i, Room room) {
		Log.d("MAD", "Room connected!!");
	}

	@Override
	public void onRoomCreated(int i, Room room) {
		Log.d("MAD", "Room connecting");
	}

	@Override
	public void onJoinedRoom(int i, Room room) {
		Log.d("MAD", "Room joined");
	}

	@Override
	public void onDisconnectedFromRoom(Room room) { }

	@Override
	public void onConnectedToRoom(Room room) {
		Log.d("MAD", "Player connected to the room");
	}

	@Override
	public void onLeftRoom(int i, String s) {

	}

	@Override
	public void onPeerLeft(Room room, List<String> strings) {}

	@Override
	public void onPeerDeclined(Room room, List<String> strings) {}

	@Override
	public void onPeerInvitedToRoom(Room room, List<String> strings) {}

	@Override
	public void onPeerJoined(Room room, List<String> strings) {
		Log.d("MAD", "Peer joined");
	}

	@Override
	public void onPeersConnected(Room room, List<String> strings) {

		Log.d("MAD", "Peer connected");
	}

	@Override
	public void onPeersDisconnected(Room room, List<String> strings) {}

	protected RoomConfig.Builder makeBasicRoomConfigBuilder() {
		return RoomConfig.builder(this)
				.setMessageReceivedListener(this)
				.setRoomStatusUpdateListener(this);
	}

	@Override
	public void onRealTimeMessageReceived(RealTimeMessage rtm) {
		byte[] data = rtm.getMessageData();

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


	@Override public void onP2PDisconnected(String participant) {}
	@Override public void onP2PConnected(String participant) {}
}
