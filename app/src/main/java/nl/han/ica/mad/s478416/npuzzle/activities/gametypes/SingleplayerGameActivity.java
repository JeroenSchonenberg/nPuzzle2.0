package nl.han.ica.mad.s478416.npuzzle.activities.gametypes;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.han.ica.mad.s478416.npuzzle.*;
import nl.han.ica.mad.s478416.npuzzle.activities.GameFinishedActivity;
import nl.han.ica.mad.s478416.npuzzle.model.*;
import nl.han.ica.mad.s478416.npuzzle.utils.*;
import nl.han.ica.mad.s478416.npuzzle.views.*;

/**
 * Created by Jeroen Schonenberg (478416) on 27/03/15.
 */
public class SingleplayerGameActivity extends AbstractGameActivity implements IPuzzleModelObserver {
    private static final int TO_YOU_WIN_ACTIVITY_DELAY = 1500;
	private static final int HIDE_COMPLETED_PUZZLE_DELAY = 1000;                            // in ms
	private static final int INITIAL_SHUFFLE_DELAY = HIDE_COMPLETED_PUZZLE_DELAY + 1250;    // in ms

	@InjectView(R.id.container)
	RelativeLayout container;

	PuzzleView view;
	PuzzleModel model;
    SavegameManager savegameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer_game);
		ButterKnife.inject(this);

		this.savegameManager = new SavegameManager(getApplicationContext());

		// check if we're resuming a game or starting a new one
        Intent intent = getIntent();
        Boolean resumeGame = intent.getBooleanExtra(getString(R.string.key_resume_game), false);
		// collect parameters needed to (re)construct the puzzle
        int imgResId 			= resumeGame ? savegameManager.getSavedImgResId() 		: intent.getIntExtra(getString(R.string.key_image), 0);
		int moveCount 			= resumeGame ? savegameManager.getSavedMoveCount() 		: 0;
		Difficulty difficulty 	= resumeGame ? savegameManager.getSavedDifficulty() 	: (Difficulty) intent.getSerializableExtra(getString(R.string.key_difficulty));
		GameState gameState 	= resumeGame ? GameState.SHUFFLED 						: GameState.UNSHUFFLED;

		initPuzzle(imgResId, difficulty, gameState, moveCount);
    }

	private void initPuzzle(int imgResId, Difficulty difficulty, GameState gameState, int moveCount){
		this.model = new PuzzleModel(imgResId, difficulty, gameState, moveCount, null);
		this.model.addObserver(this);

		this.view = new PuzzleView(this, imgResId, difficulty.getGridSize());
		this.view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		this.view.setOnPieceClickListener(onPuzzlePieceClick);
		this.container.addView(view);

		switch (gameState){
			case UNSHUFFLED:
				locked = true;
				new Handler().postDelayed(new Runnable() {
					@Override public void run() { view.hideCompletedPuzzle();	}
				}, HIDE_COMPLETED_PUZZLE_DELAY);
				new Handler().postDelayed(new Runnable() {
					@Override public void run() { shuffle(view, model, ShuffleUtil.genShuffleSequence(0, 0, 0)); }
				}, INITIAL_SHUFFLE_DELAY);
				break;
			case SHUFFLED:
				locked = false;
				view.hideCompletedPuzzle();
				break;
		}
	}

	View.OnClickListener onPuzzlePieceClick = new View.OnClickListener() {
		@Override public void onClick(View v) {
			if (locked) return;

			int pieceNumber = ((IPuzzlePiece) v).getNumber();
			if(model.pieceNeighboursEmptySlot(pieceNumber)){
				view.animateSlidePieceToSlot(pieceNumber, model.getEmptySlot());
				model.movePieceToEmptySlot(pieceNumber);
			}
		}
	};

	@Override
	protected void onPause(){
		super.onPause();

		savegameManager.save(
			model.getImageResourceId(),
			model.getDifficulty(),
			model.getArrangement(),
			model.getMoveCount()
		);
	}

	public void onPuzzleFinished(final PuzzleModel model){
		view.showCompletedPuzzle();
		locked = true;
		savegameManager.deleteSavegame();

		new Handler().postDelayed(new Runnable() {
			@Override public void run() { StartYouWinActivity(); }
		}, TO_YOU_WIN_ACTIVITY_DELAY);
	}

	private void StartYouWinActivity(){
		Intent i = new Intent(SingleplayerGameActivity.this, GameFinishedActivity.class);
		i.putExtra(getString(R.string.key_image), model.getImageResourceId());
		i.putExtra(getString(R.string.key_difficulty), model.getDifficulty());
		i.putExtra(getString(R.string.key_moves_count), model.getMoveCount());
		i.putExtra(getString(R.string.key_time), model.getTime());
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
		finish();
	}
}
