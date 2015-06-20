package nl.han.ica.mad.s478416.npuzzle.activities.gametypes;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;
import nl.han.ica.mad.s478416.npuzzle.*;
import nl.han.ica.mad.s478416.npuzzle.activities.GameFinishedActivity;
import nl.han.ica.mad.s478416.npuzzle.activities.SelectDifficultyActivity;
import nl.han.ica.mad.s478416.npuzzle.activities.SelectImageActivity;
import nl.han.ica.mad.s478416.npuzzle.model.*;
import nl.han.ica.mad.s478416.npuzzle.utils.*;
import nl.han.ica.mad.s478416.npuzzle.views.*;

public class SingleplayerActivity extends AbstractGameActivity implements IPuzzleModelObserver, IPuzzleViewObserver {
	private static final int SELECT_DIFFICULTY_REQUEST = 1;
	private static final int SELECT_IMAGE_REQUEST = 2;

    private static final int TO_YOU_WIN_ACTIVITY_DELAY = 1500;
	private static final int HIDE_COMPLETED_PUZZLE_DELAY = 1000;                            // in ms
	private static final int INITIAL_SHUFFLE_DELAY = HIDE_COMPLETED_PUZZLE_DELAY + 1250;    // in ms

	@InjectView(R.id.container)
	RelativeLayout container;

	PuzzleView view;
	PuzzleModel model;
    SavegameManager savegameManager;

	private Difficulty chosenDifficulty;
	private Integer chosenImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer_game);
		ButterKnife.inject(this);

		this.savegameManager = new SavegameManager(getApplicationContext());

		// check if we're resuming a game or starting a new one
        Intent i = getIntent();
        Boolean resumeGame = i.getBooleanExtra(getString(R.string.key_resume_game), false);

		if (resumeGame) {
			int imgResId 			= savegameManager.getSavedImgResId();
			Difficulty difficulty 	= savegameManager.getSavedDifficulty();
			int moveCount 			= savegameManager.getSavedMoveCount();
			Integer[] arrangement	= savegameManager.getSavedArrangement();
			initPuzzle(imgResId, difficulty, true, moveCount, arrangement);
		} else {
			tryInitPuzzle();
		}
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("TESTEST", "Godverdomme: " + requestCode + "  " + resultCode);
		if (resultCode == RESULT_CANCELED) return;

		switch (requestCode) {
			case SELECT_DIFFICULTY_REQUEST:
				this.chosenDifficulty = (Difficulty) data.getSerializableExtra( getString(R.string.key_difficulty) );
				tryInitPuzzle();
				break;

			case SELECT_IMAGE_REQUEST:
				this.chosenImage = data.getIntExtra(getString(R.string.key_image), 0);
				tryInitPuzzle();
				break;

			default:
				break;
		}
	}

	private void tryInitPuzzle() {
		if (chosenDifficulty == null) {
			startActivityForResult(new Intent(this, SelectDifficultyActivity.class), SELECT_DIFFICULTY_REQUEST);
			return;
		}

		if (chosenImage == null) {
			startActivityForResult(new Intent(this, SelectImageActivity.class), SELECT_IMAGE_REQUEST);
			return;
		}

		initPuzzle(chosenImage, chosenDifficulty, false, 0, null);
	}

	private void initPuzzle(int imgResId, Difficulty difficulty, boolean isShuffled, int moveCount, Integer[] arrangement){
		this.model = new PuzzleModel(imgResId, difficulty, isShuffled, moveCount, arrangement);
		this.model.addObserver(this);

		this.view = new PuzzleView(this, imgResId, difficulty.getGridSize(), arrangement);
		this.view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		this.view.setOnPieceClickListener(onPuzzlePieceClick);
		this.view.addObserver(this);
		this.view.setTransitionName("completedPuzzle");
		this.container.addView(view);
	}

	public void onPuzzleViewLoaded(PuzzleView puzzleView){
		if(model.isShuffled()){
			interactionDisabled = false;
			view.hideCompletedPuzzle();
		} else {
			interactionDisabled = true;
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					view.fadeOutCompletedPuzzle();
				}
			}, HIDE_COMPLETED_PUZZLE_DELAY);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					int[] shuffleSequence = ShuffleUtil.genShuffleSequence(50, model.getGridSize(), model.getEmptySlot());
					shuffle(view, model, shuffleSequence);
				}
			}, INITIAL_SHUFFLE_DELAY);
		}
	}

	View.OnClickListener onPuzzlePieceClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (interactionDisabled) return;

			int pieceNumber = view.getPieceNumber(v);

			if(model.pieceNeighboursEmptySlot(pieceNumber)){
				view.animateSlidePieceToSlot(pieceNumber, model.getEmptySlot());
				model.movePieceToEmptySlot(pieceNumber);
			}
		}
	};

	@Override
	protected void onPause(){
		super.onPause();

		if(model != null && model.isShuffled()) {	// saving the game only makes sense if it has been shuffled
			savegameManager.save(
					model.getImageResourceId(),
					model.getDifficulty(),
					model.getArrangement(),
					model.getMoveCount()
			);
		}
	}

	public void onPuzzleFinished(final PuzzleModel model){
		view.fadeInCompletedPuzzle();
		interactionDisabled = true;

		new Handler().postDelayed(new Runnable() {
			@Override public void run() { StartYouWinActivity(); }
		}, TO_YOU_WIN_ACTIVITY_DELAY);
	}

	private void StartYouWinActivity(){
		Intent i = new Intent(this, GameFinishedActivity.class);
		i.putExtra(getString(R.string.key_image), model.getImageResourceId());
		i.putExtra(getString(R.string.key_difficulty), model.getDifficulty());
		i.putExtra(getString(R.string.key_moves_count), model.getMoveCount());
		i.putExtra(getString(R.string.key_time), model.getTime());
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
		finish();
	}
}
