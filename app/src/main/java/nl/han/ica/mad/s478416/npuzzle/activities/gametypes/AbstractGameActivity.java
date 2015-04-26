package nl.han.ica.mad.s478416.npuzzle.activities.gametypes;

import android.app.Activity;
import android.os.Handler;

import java.util.Arrays;

import nl.han.ica.mad.s478416.npuzzle.model.GameState;
import nl.han.ica.mad.s478416.npuzzle.model.PuzzleModel;
import nl.han.ica.mad.s478416.npuzzle.views.PuzzleView;

public abstract class AbstractGameActivity extends Activity {
	protected static final int SHUFFLE_INTERVAL = 25;
	protected boolean locked;

	protected void shuffle(final PuzzleView view, final PuzzleModel model, int[] sequence){
		if (sequence.length == 0){
			locked = false;
			model.setShuffled();
			model.setMoveCount(0);
			model.resetTimer();
		} else {
			locked = true;

			final int piece = model.getPiece(sequence[0]);
			view.animateSlidePieceToSlot(piece, model.getEmptySlot());
			model.movePieceToEmptySlot(piece);

			final int[] remainingSequence = Arrays.copyOfRange(sequence, 1, sequence.length);
			new Handler().postDelayed(new Runnable() {
				@Override public void run() { shuffle(view, model, remainingSequence); }
			}, (PuzzleView.ANIM_SLIDE_DURATION + SHUFFLE_INTERVAL));
		}
	}
}
