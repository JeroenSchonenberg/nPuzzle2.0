package nl.han.ica.mad.s478416.npuzzle.activities.gametypes;

import android.app.Activity;
import android.os.Handler;

import java.util.Arrays;

import nl.han.ica.mad.s478416.npuzzle.model.PuzzleModel;
import nl.han.ica.mad.s478416.npuzzle.views.PuzzleView;

public abstract class AbstractGameActivity extends Activity {
	protected static final int SHUFFLE_INTERVAL = 25;
	protected boolean locked;

	protected void shuffle(final PuzzleView view, final PuzzleModel model, int[] shuffleSequence){
		if (shuffleSequence.length == 0){
			locked = false;
			model.setMoveCount(0);
			model.resetTimer();
		} else {
			locked = true;

			final int piece = shuffleSequence[0];
			view.animateSlidePieceToSlot(piece, model.getEmptySlot());
			model.movePieceToEmptySlot(piece);

			final int[] newShuffleSequence = Arrays.copyOfRange(shuffleSequence, 1, shuffleSequence.length);
			new Handler().postDelayed(new Runnable() {
				@Override public void run() {
					shuffle(view, model, newShuffleSequence);
				}
			}, (view.ANIM_SLIDE_DURATION + SHUFFLE_INTERVAL));
		}
	}
}
