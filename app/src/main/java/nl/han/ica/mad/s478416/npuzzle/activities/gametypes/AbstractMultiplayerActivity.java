package nl.han.ica.mad.s478416.npuzzle.activities.gametypes;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.*;
import com.google.android.gms.plus.Plus;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.han.ica.mad.s478416.npuzzle.R;
import nl.han.ica.mad.s478416.npuzzle.activities.MainMenuActivity;
import nl.han.ica.mad.s478416.npuzzle.activities.SelectDifficultyActivity;
import nl.han.ica.mad.s478416.npuzzle.activities.SelectImageActivity;
import nl.han.ica.mad.s478416.npuzzle.model.Difficulty;
import nl.han.ica.mad.s478416.npuzzle.utils.ByteUtils;
import nl.han.ica.mad.s478416.npuzzle.utils.PuzzleImageUtils;

public abstract class AbstractMultiplayerActivity extends AbstractGameActivity implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, RealTimeMessageReceivedListener, RoomUpdateListener, RoomStatusUpdateListener {
	private static String TAG = "AbstractMultiplayerGameActivity";

	private static final int RC_SIGN_IN = 9001;
	private static final int SELECT_DIFFICULTY_REQUEST = 1;
	private static final int SELECT_IMAGE_REQUEST = 2;

	private static final char READY = 'R';
	private static final char DICE_ROLL = 'A'; // r and d are both occupied ):
	private static final char IMAGE_CHOICE = 'I';
	private static final char DIFFICULTY_CHOICE = 'D';
	private static final char SHUFFLE = 'S';
	private static final char MOVE = 'M';
	private static final char FINISHED = 'F';
	private static final char CONGRATULATIONS = 'C';

	@InjectView(R.id.gameLayout) RelativeLayout gameView;

	@InjectView(R.id.connectionStatusLayout) RelativeLayout connectionStatusView;
	@InjectView(R.id.connectionStatusText) TextView connectionStatusText;

	@InjectView(R.id.connectionErrorLayout) LinearLayout connectionErrorView;
	@InjectView(R.id.btn_try_again) Button buttonTryAgain;
	@InjectView(R.id.btn_main_menu) Button buttonMainMenu;

	protected GoogleApiClient googleApiClient;

	private String roomId;
	protected Participant me;
	protected Participant opponent;
	protected Participant gameLeader;

	private Integer myDiceRoll;
	private Integer opponentsDiceRoll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_abstract_multiplayer_game);
		ButterKnife.inject(this);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		connectionStatusText.setText("");
		switchToView(connectionStatusView);

		buttonTryAgain.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				AbstractMultiplayerActivity.this.recreate();
			}
		});

		buttonMainMenu.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				goToMainMenu();
			}
		});

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
			updateConnectionStatus("Connecting to servers...");
			googleApiClient.connect();
		}

		super.onStart();
	}

	@Override
	protected void onStop() {
		//leaveRoom();
		super.onStop();
	}

	public void onConnected(Bundle bundle) {
		updateConnectionStatus("Connected succesfully!");
		startAutoMatching();
	}

	public void onConnectionSuspended(int cause) {
		Log.d(TAG, "onConnectionSuspended. Trying to reconnect...");
		googleApiClient.connect();
	}

	public void onConnectionFailed(ConnectionResult result) {
		Log.d(TAG, "Connection to Google Api Client failed" + result.toString());

		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, RC_SIGN_IN);
			} catch (IntentSender.SendIntentException e) {
				Log.d(TAG, e.getMessage());
				googleApiClient.connect();
			}
		}
	}

	protected void startAutoMatching(){
		final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;

		RoomConfig.Builder roomBuilder = RoomConfig.builder(this);
		roomBuilder.setMessageReceivedListener(this);
		roomBuilder.setRoomStatusUpdateListener(this);
		roomBuilder.setAutoMatchCriteria( RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS, MAX_OPPONENTS, 0) );

		Games.RealTimeMultiplayer.create(googleApiClient, roomBuilder.build());
		updateConnectionStatus("Looking for an opponent...");
	}

	private void updateRoom(Room room) {
		roomId = room.getRoomId();
	}
	@Override public void onP2PDisconnected(String participant) {}
	@Override public void onP2PConnected(String participant) {}
	@Override public void onJoinedRoom(int statusCode, Room room) 				{ Log.d(TAG, "onJoinedRoom");			updateRoom(room); }
	@Override public void onRoomCreated(int i, Room room) 						{ Log.d(TAG, "onRoomCreated"); 			updateRoom(room); }
	@Override public void onRoomAutoMatching(Room room) 						{ Log.d(TAG, "onRoomAutoMatching"); 	updateRoom(room); }
	@Override public void onRoomConnecting(Room room) 							{ Log.d(TAG, "onRoomConnecting"); 		updateRoom(room); }
	@Override public void onPeerLeft(Room room, List<String> strings) 			{ Log.d(TAG, "onPeerLeft");				updateRoom(room); }
	@Override public void onPeerDeclined(Room room, List<String> strings) 		{ Log.d(TAG, "onPeerDeclined");			updateRoom(room); }
	@Override public void onPeerInvitedToRoom(Room room, List<String> strings) 	{ Log.d(TAG, "onPeerInvited");			updateRoom(room); }
	@Override public void onPeerJoined(Room room, List<String> strings) 		{ Log.d(TAG, "onPeerJoined");			updateRoom(room); }
	@Override public void onPeersConnected(Room room, List<String> strings) 	{ Log.d(TAG, "onPeersConnected");		updateRoom(room); }
	@Override public void onPeersDisconnected(Room room, List<String> strings) 	{ Log.d(TAG, "onPeersDisconnected");	updateRoom(room); }
	@Override public void onLeftRoom(int i, String s) 							{ Log.d(TAG, "onLeftRoom");				connectionLost(); }
	@Override public void onDisconnectedFromRoom(Room room) 					{ Log.d(TAG, "onDisconnectedFromRoom"); connectionLost(); }

	@Override public void onConnectedToRoom(Room room) {
		Log.d(TAG, "onConnectedToRoom");
		updateConnectionStatus("Found an opponent!");
		updateRoom(room);
	}

	@Override
	public void onRoomConnected(int statusCode, Room room) {
		if (statusCode != GamesStatusCodes.STATUS_OK) {
			Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
			return; // TODO: implement error handling
		}

		String myId = room.getParticipantId( Games.Players.getCurrentPlayerId(googleApiClient) );
		this.me = room.getParticipant(myId);
		this.opponent = room.getParticipants().get(0) == me ? room.getParticipants().get(1) : room.getParticipants().get(0);
		this.roomId = room.getRoomId();

		updateConnectionStatus("Picking a gameleader...");
		determineGameLeader();
	}

	/* nPuzzle logic*/

	private void determineGameLeader() {
		if (this.myDiceRoll == null) rollMyDice();
		if (this.opponentsDiceRoll == null) return;

		if (myDiceRoll == opponentsDiceRoll) {
			resetDiceRolls();
			return;
		}

		this.gameLeader = (myDiceRoll > opponentsDiceRoll) ? me : opponent;

		if (this.gameLeader == this.me) {
			updateConnectionStatus("You are the leader and get to choose an image and difficulty!");
			startActivityForResult(new Intent(this, SelectDifficultyActivity.class), SELECT_DIFFICULTY_REQUEST);
		} else {
			updateConnectionStatus("Your opponent is the game leader");
			updateConnectionStatus("Waiting for your opponent to configure the game...");
		}
	}

	private void rollMyDice(){
		this.myDiceRoll = new Random().nextInt(127);
		sendDiceRoll(myDiceRoll);
	}

	private void onDiceRollReceived(int opponentsDiceRoll){
		this.opponentsDiceRoll = opponentsDiceRoll;
		determineGameLeader();
	}

	private void resetDiceRolls() {
		myDiceRoll = null;
		opponentsDiceRoll = null;
		determineGameLeader();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case SELECT_DIFFICULTY_REQUEST:
				if (resultCode == RESULT_OK) {
					Difficulty difficulty = (Difficulty) data.getSerializableExtra( getString(R.string.key_difficulty) );
					onDifficultySelected(difficulty);
				}
				break;
			case SELECT_IMAGE_REQUEST:
				if (resultCode == RESULT_OK) {
					int imgResId = data.getIntExtra(getString(R.string.key_image), 0);
					onImageSelected(imgResId);
				}
				break;
			default: break;
		}
	}

	private void onDifficultySelected(Difficulty difficulty){
		sendDifficultyChoice(difficulty);
		onDifficultyChoiceReceived(difficulty);
		startActivityForResult(new Intent(this, SelectImageActivity.class), SELECT_IMAGE_REQUEST);
	}

	private void onImageSelected(int imgResId) {
		int index = PuzzleImageUtils.getImgResIds().indexOf(imgResId);
		this.sendImageChoice(index);
		onImageChoiceReceived(imgResId);
	}

	@Override
	public void onRealTimeMessageReceived(RealTimeMessage rtm) {
		byte[] data = rtm.getMessageData();

		switch (data[0]) {
			case READY:
				onOpponentReady();
				break;
			case DICE_ROLL:
				onDiceRollReceived( (int)data[1] );
				break;
			case IMAGE_CHOICE:
				int imgResId = PuzzleImageUtils.getImgResIds().get( (int)data[1] );
				onImageChoiceReceived(imgResId);
				break;
			case DIFFICULTY_CHOICE:
				String difficultyStr = new String( Arrays.copyOfRange(data, 1, data.length) );
				onDifficultyChoiceReceived( Difficulty.valueOf(difficultyStr) );
				break;
			case SHUFFLE:
				int[] sequence = new int[data.length - 1];
				for (int i = 0; i < sequence.length; i++) { sequence[i] = (int)data[i + 1]; }
				onShuffleReceived(sequence);
				break;
			case MOVE:
				onOpponentMove( (int)data[1] );
				break;
			case FINISHED:
				byte[] timeBytes = Arrays.copyOfRange(data, 1, data.length);
				int time = ByteUtils.byteArrayToInt(timeBytes);
				onOpponentFinished(time);
				break;
			case CONGRATULATIONS:
				onCongratulationsReceived();
				break;
		}
	}

	protected abstract void onOpponentReady();
	protected abstract void onDifficultyChoiceReceived(Difficulty difficulty);
	protected abstract void onImageChoiceReceived(int imageIndex);
	protected abstract void onShuffleReceived(int[] sequence);
	protected abstract void onOpponentMove(int pieceId);
	protected abstract void onOpponentFinished(int time);
	protected abstract void onCongratulationsReceived();

	protected void sendReady() {
		sendMessage(new byte[]{ READY });
	}

	protected void sendDiceRoll(int number) {
		sendMessage(new byte[]{ DICE_ROLL, (byte) number });
	}

	protected void sendImageChoice(int imageIndex) {
		sendMessage(new byte[]{ IMAGE_CHOICE, (byte) imageIndex });
	}

	protected void sendMove(int pieceId) {
		sendMessage(new byte[]{ MOVE, (byte) pieceId });
	}

	protected void sendCongratulations() {
		sendMessage(new byte[]{ CONGRATULATIONS });
	}

	protected void sendDifficultyChoice(Difficulty difficulty){
		byte[] difficultyBytes = difficulty.name().getBytes();

		byte[] msg = new byte[difficultyBytes.length + 1];
		msg[0] = DIFFICULTY_CHOICE;
		for (int i = 0; i < difficultyBytes.length; i++){ msg[i + 1] = difficultyBytes[i]; }

		sendMessage(msg);
	}

	protected void sendShuffleSequence(int[] sequence){
		byte[] msg = new byte[sequence.length + 1];
		msg[0] = SHUFFLE;
		for(int i = 0; i < sequence.length; i++){ msg[i + 1] = (byte) sequence[i]; }

		sendMessage(msg);
	}

	protected void sendFinished(int time){
		byte[] timeByteArray = ByteUtils.intToByteArray(time);
		byte[] msg = { FINISHED, timeByteArray[0], timeByteArray[1], timeByteArray[2], timeByteArray[3] };
		sendMessage(msg);
	}

	private void sendMessage(byte[] msg){
		Games.RealTimeMultiplayer.sendReliableMessage(googleApiClient, null, msg, roomId, opponent.getParticipantId());
	}

	// * MISC* //

	private void connectionLost() {
		roomId = null;
		me = null;
		opponent = null;
		gameLeader = null;

		switchToView(connectionErrorView);
	}

	protected void switchToView(View v){
		gameView.setVisibility(View.INVISIBLE);
		connectionErrorView.setVisibility(View.INVISIBLE);
		connectionStatusView.setVisibility(View.INVISIBLE);

		v.setVisibility(View.VISIBLE);
	}

	private void updateConnectionStatus(String newStatus){
		String currentText = connectionStatusText.getText().toString();
		connectionStatusText.setText(currentText + "\n" + newStatus);
	}

	private void leaveRoom() {
		if (roomId != null) {
			Games.RealTimeMultiplayer.leave(googleApiClient, this, roomId);
			roomId = null;
		}
	}

	private void goToMainMenu(){
		Intent i = new Intent(this, MainMenuActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
		finish();
	}
}
