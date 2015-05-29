package nl.han.ica.mad.s478416.npuzzle.activities.gametypes;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.han.ica.mad.s478416.npuzzle.R;
import nl.han.ica.mad.s478416.npuzzle.model.Difficulty;
import nl.han.ica.mad.s478416.npuzzle.model.IPuzzleModelObserver;
import nl.han.ica.mad.s478416.npuzzle.model.PuzzleModel;
import nl.han.ica.mad.s478416.npuzzle.utils.ShuffleUtil;
import nl.han.ica.mad.s478416.npuzzle.views.IPuzzleViewObserver;
import nl.han.ica.mad.s478416.npuzzle.views.PuzzleView;

public class VersusMultiplayerGameActivity extends AbstractMultiplayerGameActivity implements IPuzzleModelObserver, IPuzzleViewObserver {
	private static String TAG = "VersusMultiplayerGameActivity";

	@InjectView(R.id.gameLayout) RelativeLayout gameLayout;
	PuzzleModel myPuzzleModel;
	PuzzleView myPuzzleView;

	PuzzleModel opponentsPuzzleModel;
	PuzzleView opponentsPuzzleView;

	private Integer imgResId;
	private Difficulty difficulty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ButterKnife.inject(this);
	}

	private void tryInitGameView(){
		if (imgResId == null) return;
		if (difficulty == null) return;

		this.myPuzzleModel = new PuzzleModel(imgResId, difficulty, false, 0, null);
		this.myPuzzleModel.addObserver(this);

		this.myPuzzleView = new PuzzleView(this, this.imgResId, this.difficulty.getGridSize(), null);
		this.myPuzzleView.setLayoutParams(new RelativeLayout.LayoutParams(gameLayout.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
		this.myPuzzleView.addLayoutRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		this.myPuzzleView.addLayoutRule(RelativeLayout.CENTER_HORIZONTAL);
		this.myPuzzleView.addObserver(this);
		gameLayout.addView(myPuzzleView);

		this.opponentsPuzzleModel = new PuzzleModel(imgResId, difficulty, false, 0, null);

		this.opponentsPuzzleView = new PuzzleView(this, this.imgResId, this.difficulty.getGridSize(), null);
		this.opponentsPuzzleView.setLayoutParams(new RelativeLayout.LayoutParams((int) (gameLayout.getWidth() / 2), ViewGroup.LayoutParams.WRAP_CONTENT));
		this.opponentsPuzzleView.addLayoutRule(RelativeLayout.ALIGN_PARENT_TOP);
		this.opponentsPuzzleView.addLayoutRule(RelativeLayout.CENTER_HORIZONTAL);
		this.opponentsPuzzleView.addObserver(this);
		gameLayout.addView(opponentsPuzzleView);
	}

	public void onPuzzleViewLoaded(PuzzleView puzzleView){
		puzzleView.fadeOutCompletedPuzzle();

		if (myPuzzleView.doneLoading && opponentsPuzzleView.doneLoading) {
			hideConnectionStatusView();
			sendReady();
		}
	}

	public void onPuzzleFinished(final PuzzleModel model) {
	}

	protected void onOpponentReady(){
		if (gameLeader == me) {
			int[] shuffleSequence = ShuffleUtil.genShuffleSequence(50, myPuzzleModel.getGridSize(), myPuzzleModel.getEmptySlot());

			//shuffle(myPuzzleView, myPuzzleModel, shuffleSequence);
			//shuffle(opponentsPuzzleView, opponentsPuzzleModel, shuffleSequence);
			sendShuffleSequence(shuffleSequence);
		}

		// if leader -> throw shuffle
		// else do no thing
	}

	protected void onImageChoiceReceived(int imgResId){
		this.imgResId = imgResId;
		tryInitGameView();
	}

	protected void onDifficultyChoiceReceived(Difficulty difficulty){
		this.difficulty = difficulty;
		tryInitGameView();
	}

	protected void onShuffleReceived(int[] sequence){
		Log.d(TAG, "RECEIVED SHUFFLE: " + sequence);
	}

	protected void onOpponentMove(int pieceId){

	}

	protected void onOpponentFinished(int time){

	}

	protected void onOpponentQuit() {

	}
}
