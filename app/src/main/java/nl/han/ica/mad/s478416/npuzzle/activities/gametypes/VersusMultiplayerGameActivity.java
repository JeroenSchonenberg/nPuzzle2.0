package nl.han.ica.mad.s478416.npuzzle.activities.gametypes;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;

import nl.han.ica.mad.s478416.npuzzle.R;
import nl.han.ica.mad.s478416.npuzzle.model.Difficulty;

public class VersusMultiplayerGameActivity extends AbstractMultiplayerGameActivity {
	private static String TAG = "VersusMultiplayerGameActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_versus_multiplayer_game);
	}

	protected void onOpponentReady(){
		Log.d(TAG, "OPPONENT IS READY!!");
	}

	protected void onImageChoiceReceived(int imgResId){
		Log.d(TAG, "IMAGE CHOICE RECEIVED" + imgResId);
	}

	protected void onDifficultyChoiceReceived(Difficulty difficulty){
		Log.d(TAG, "RECEIVED DIFFICULTY " + difficulty.toString());
	}

	protected void onShuffleReceived(int[] sequence){
		Log.d(TAG, "RECEIVED SHUFFLE!!!");
	}

	protected void onOpponentMove(int pieceId){

	}

	protected void onOpponentFinished(int time){

	}

	protected void onOpponentQuit() {

	}
}
