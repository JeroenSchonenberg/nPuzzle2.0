package nl.han.ica.mad.s478416.npuzzle.activities.gametypes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import nl.han.ica.mad.s478416.npuzzle.R;
import nl.han.ica.mad.s478416.npuzzle.activities.VersusGameFinished;
import nl.han.ica.mad.s478416.npuzzle.model.Difficulty;
import nl.han.ica.mad.s478416.npuzzle.model.IPuzzleModelObserver;
import nl.han.ica.mad.s478416.npuzzle.model.PuzzleModel;
import nl.han.ica.mad.s478416.npuzzle.utils.ShuffleUtil;
import nl.han.ica.mad.s478416.npuzzle.views.IPuzzleViewObserver;
import nl.han.ica.mad.s478416.npuzzle.views.PuzzleView;

public class VersusMultiplayerActivity extends AbstractMultiplayerActivity implements IPuzzleModelObserver, IPuzzleViewObserver {
	private static String TAG = "VersusMultiplayerGameActivity";

	private static final int TO_YOU_WIN_ACTIVITY_DELAY = 1500;
	private static final int HIDE_COMPLETED_PUZZLE_DELAY = 1000;                            // in ms
	private static final int INITIAL_SHUFFLE_DELAY = HIDE_COMPLETED_PUZZLE_DELAY + 1250;    // in ms

	private Integer imgResId;
	private Difficulty difficulty;

	private PuzzleView myPuzzleView;
	private PuzzleModel myPuzzleModel;
	private Integer myFinalTime;

	private PuzzleView opponentsPuzzleView;
	private PuzzleModel opponentsPuzzleModel;
	private boolean opponentReady = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ButterKnife.inject(this);
	}

	protected void onImageChoiceReceived(int imgResId){
		this.imgResId = imgResId;
		tryInitGameView();
	}

	protected void onDifficultyChoiceReceived(Difficulty difficulty){
		this.difficulty = difficulty;
		tryInitGameView();
	}

	private void tryInitGameView(){
		if (imgResId == null) return;
		if (difficulty == null) return;

		this.myPuzzleModel = new PuzzleModel(imgResId, difficulty, false, 0, null);
		this.myPuzzleModel.addObserver(this);

		this.myPuzzleView = new PuzzleView(this, this.imgResId, this.difficulty.getGridSize(), null);
		this.myPuzzleView.setOnPieceClickListener(onMyPuzzlePieceClick);
		this.myPuzzleView.setLayoutParams(new RelativeLayout.LayoutParams(gameView.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
		this.myPuzzleView.addLayoutRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		this.myPuzzleView.addLayoutRule(RelativeLayout.CENTER_HORIZONTAL);
		this.myPuzzleView.addObserver(this);
		gameView.addView(myPuzzleView);

		this.opponentsPuzzleModel = new PuzzleModel(imgResId, difficulty, false, 0, null);

		this.opponentsPuzzleView = new PuzzleView(this, this.imgResId, this.difficulty.getGridSize(), null);
		this.opponentsPuzzleView.setLayoutParams(new RelativeLayout.LayoutParams((int) (gameView.getWidth() / 2), ViewGroup.LayoutParams.WRAP_CONTENT));
		this.opponentsPuzzleView.addLayoutRule(RelativeLayout.ALIGN_PARENT_TOP);
		this.opponentsPuzzleView.addLayoutRule(RelativeLayout.CENTER_HORIZONTAL);
		this.opponentsPuzzleView.addObserver(this);
		gameView.addView(opponentsPuzzleView);
	}

	public void onPuzzleViewLoaded(PuzzleView puzzleView){
		if (myPuzzleView.doneLoading && opponentsPuzzleView.doneLoading) {
			switchToView(gameView);
			sendReady();
			checkBothReady();
		}
	}

	protected void onOpponentReady(){
		this.opponentReady = true;
		checkBothReady();
	}

	private void checkBothReady() {
		if (!opponentReady || !(myPuzzleView.doneLoading && opponentsPuzzleView.doneLoading)) return;

		new Handler().postDelayed(new Runnable() {
			@Override public void run() {
				myPuzzleView.fadeOutCompletedPuzzle();
				opponentsPuzzleView.fadeOutCompletedPuzzle();
			}
		}, HIDE_COMPLETED_PUZZLE_DELAY);

		if (gameLeader == me) {
			new Handler().postDelayed(new Runnable() {
				@Override public void run() {
					int[] shuffleSequence = ShuffleUtil.genShuffleSequence(50, myPuzzleModel.getGridSize(), myPuzzleModel.getEmptySlot());
					onShuffleReceived(shuffleSequence);
					sendShuffleSequence(shuffleSequence);
				}
			}, INITIAL_SHUFFLE_DELAY);
		}
	}

	protected void onShuffleReceived(int[] sequence){
		shuffle(myPuzzleView, myPuzzleModel, sequence);
		shuffle(opponentsPuzzleView, opponentsPuzzleModel, sequence);
	}

	View.OnClickListener onMyPuzzlePieceClick = new View.OnClickListener() {
		@Override public void onClick(View v) {
			if (interactionDisabled) return;

			int pieceNumber = myPuzzleView.getPieceNumber(v);

			if(myPuzzleModel.pieceNeighboursEmptySlot(pieceNumber)){
				myPuzzleView.animateSlidePieceToSlot(pieceNumber, myPuzzleModel.getEmptySlot());
				myPuzzleModel.movePieceToEmptySlot(pieceNumber);
				sendMove(pieceNumber);
			}
		}
	};

	protected void onOpponentMove(int pieceId){
		opponentsPuzzleView.animateSlidePieceToSlot(pieceId, opponentsPuzzleModel.getEmptySlot());
		opponentsPuzzleModel.movePieceToEmptySlot(pieceId);
	}

	public void onPuzzleFinished(final PuzzleModel model) {
		myFinalTime = myPuzzleModel.getTime();

		myPuzzleView.fadeInCompletedPuzzle();
		interactionDisabled = true;

		sendFinished(myFinalTime);
	}

	protected void onOpponentFinished(int opponentsFinalTime) {
		opponentsPuzzleView.fadeInCompletedPuzzle();
		interactionDisabled = true;

		checkForVictory(opponentsFinalTime);
	}

	private void checkForVictory(final int opponentsFinalTime) {
		int myTime = myFinalTime != null ? myFinalTime : myPuzzleModel.getTime();

		if (opponentsFinalTime < myTime) {
			opponentWins();
		} else {
			if (myFinalTime == null) {
				int timeLeft = opponentsFinalTime - myTime;
				new Handler().postDelayed(new Runnable() {
					@Override public void run() {
						checkForVictory(opponentsFinalTime);
					}
				}, timeLeft);
			}
		}
	}

	private void opponentWins() {
		sendCongratulations();
		goToVersusGameFinished(false);
	}

	protected void onCongratulationsReceived() {
		goToVersusGameFinished(true);
	}

	protected void goToVersusGameFinished(final boolean iWon) {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(VersusMultiplayerActivity.this, VersusGameFinished.class);
				i.putExtra(getString(R.string.key_victory_indicator), iWon);
				i.putExtra(getString(R.string.key_my_avatar), me.getPlayer().getHiResImageUrl());
				i.putExtra(getString(R.string.key_opponents_avatar), opponent.getHiResImageUrl());
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);
				finish();
			}
		}, TO_YOU_WIN_ACTIVITY_DELAY);
	}
}
