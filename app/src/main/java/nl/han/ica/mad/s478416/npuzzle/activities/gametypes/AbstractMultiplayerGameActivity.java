package nl.han.ica.mad.s478416.npuzzle.activities.gametypes;

import android.os.Bundle;
import android.view.WindowManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.plus.Plus;

import nl.han.ica.mad.s478416.npuzzle.R;
import nl.han.ica.mad.s478416.npuzzle.utils.ByteUtils;

public abstract class AbstractMultiplayerGameActivity extends AbstractGameActivity implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, RealTimeMessageReceivedListener{
	private static final char READY 	= 'R';
	private static final char SHUFFLE	= 'S';
	private static final char MOVE 		= 'M';
	private static final char FINISHED 	= 'F';
	private static final char QUIT 		= 'Q';

	private GoogleApiClient googleApiClient;
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
}
