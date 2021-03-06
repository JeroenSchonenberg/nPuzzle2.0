package nl.han.ica.mad.s478416.npuzzle.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nl.han.ica.mad.s478416.npuzzle.utils.BitmapUtils;

/**
 * Created by Jeroen Schonenberg (478416) on 27/03/15.
 */
public class PuzzleView extends RelativeLayout {
	private static final int BG_COLOR = Color.argb(225,0,0,0);
	private static final int PIECE_MARGIN = 2;                        					// in px
    public static final int ANIM_SLIDE_DURATION = 60;                 					// in ms
	public static final int COMPLETED_PUZZLE_FADE_DURATION = 500;						// in ms
	private static final int PICASSO_FADE_DURATION = 200;								// in ms
	private static final int PUZZLE_PIECE_FADE_DURATION = PICASSO_FADE_DURATION + 50;	// in ms

	private ArrayList<IPuzzleViewObserver> observers;
	public boolean doneLoading = false;

	private Integer[] customArrangement;
    private boolean bitmapsInitialized;

    private int gridSize;
	private int pieceWidth;

	private int imgResId;
	private ImageView completedPuzzle;
	private ImageView[] pieces;

    public PuzzleView(Context context, int imgResId, int gridSize) {
        super(context, null, 0);

		observers = new ArrayList<>();
		this.bitmapsInitialized = false;

        this.setBackgroundColor(BG_COLOR);
        this.gridSize = gridSize;
        this.imgResId = imgResId;

		this.completedPuzzle = new ImageView(getContext());
		this.addView(completedPuzzle);

		this.pieces = new ImageView[gridSize * gridSize - 1];
		for(int i = 0; i < pieces.length; i++){
			this.pieces[i] = new ImageView(getContext());
		}
    }

	public PuzzleView(Context context, int baseImageResId, int gridSize, Integer[] customArrangement) {
		this(context, baseImageResId, gridSize);
		this.customArrangement = customArrangement;
	}

    @Override
    public void onSizeChanged (int width, int height, int oldWidth, int oldHeight){
		super.onSizeChanged(width, height, oldWidth, oldHeight);

		LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
		params.width = width;
		params.height = width;
        this.setLayoutParams(params);

        initBitmaps();
    }

	public void addLayoutRule(int verb) {
		LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
		params.addRule(verb);
		this.setLayoutParams(params);
	}

    private void initBitmaps(){
		if (bitmapsInitialized) return;

		Log.d("PuzzleView", "initBitmaps");

		Picasso.with(getContext())
			.load(imgResId)
			.resize(getLayoutParams().width, getLayoutParams().width)
			.into(completedPuzzle, new Callback.EmptyCallback() {
				@Override public void onSuccess() {
					completedPuzzleLoaded();
				}
			});

		int baseImageWidth = getLayoutParams().width - (PIECE_MARGIN * (gridSize - 1));
		Bitmap baseBitmap = BitmapUtils.scaledBitmapFromResource(getResources(), imgResId, baseImageWidth, baseImageWidth);

		this.pieceWidth = baseImageWidth / gridSize;
		Bitmap[] pieceBitmaps = BitmapUtils.sliceAsGrid(baseBitmap, gridSize);

		for(int i = 0; i < pieces.length; i++){
			pieces[i].setImageBitmap(pieceBitmaps[i]);

			int initialSlot = (customArrangement != null) ? java.util.Arrays.asList(customArrangement).indexOf(i) : i;
			setPiecePosition(pieces[i], calcSlotX(initialSlot), calcSlotY(initialSlot));
		}

		bitmapsInitialized = true;
    }

	private void completedPuzzleLoaded(){
		for(ImageView p : pieces){
			this.addView(p);

			// fade in the pieces almost simultaneously with Picasso's fade of completedImage
			AlphaAnimation a = new AlphaAnimation(0.0f, 1.0f);
			a.setDuration(PUZZLE_PIECE_FADE_DURATION);
			a.setFillAfter(true);
			p.startAnimation(a);
		}

		this.doneLoading = true;

		notifyObservers();
	}

	public void setOnPieceClickListener(OnClickListener listener){
		for (ImageView p : pieces) p.setOnClickListener(listener);
	}

	public int getPieceNumber(View piece){
		return java.util.Arrays.asList(pieces).indexOf(piece);
	}

	public void animateSlidePieceToSlot(int pieceNumber, int targetSlot){
		final ImageView piece = pieces[pieceNumber];

		// get current coordinates and delta between current and old coordinates
		final int oldX = piece.getLeft();
		final int oldY = piece.getTop();
		final int deltaX = calcSlotX(targetSlot) - oldX;
		final int deltaY = calcSlotY(targetSlot) - oldY;

		// construct and start animation
		Animation a = new Animation() {
			@Override protected void applyTransformation(float interpolatedTime, Transformation t) {
				int animatedX = (int)(oldX + (deltaX * interpolatedTime));
				int animatedY = (int)(oldY + (deltaY * interpolatedTime));
				setPiecePosition(piece, animatedX, animatedY);
			}
		};
		a.setDuration(ANIM_SLIDE_DURATION);
		piece.startAnimation(a);
	}

	private int calcSlotX(int slot){
		return (pieceWidth + PIECE_MARGIN) * (slot % gridSize);
	}

	private int calcSlotY(int slot){
		return (pieceWidth + PIECE_MARGIN) * (slot / gridSize);
	}

	private void setPiecePosition(ImageView piece, int x, int y){
		LayoutParams params = new LayoutParams(pieceWidth, pieceWidth);
		params.setMargins(x, y, 0, 0);
		piece.setLayoutParams(params);
	}

	/* showing / hiding the completed puzzle */

	public void hideCompletedPuzzle(){
		completedPuzzle.setVisibility(INVISIBLE);
	}

	public void fadeInCompletedPuzzle(){
		fadeCompletedPuzzle( new AlphaAnimation(0.0f, 1.0f) );
	}

	public void fadeOutCompletedPuzzle(){
		fadeCompletedPuzzle( new AlphaAnimation(1.0f, 0.0f) );
	}

	private void fadeCompletedPuzzle(AlphaAnimation baseAnimation){
		completedPuzzle.setVisibility(VISIBLE);
		baseAnimation.setDuration(COMPLETED_PUZZLE_FADE_DURATION);
		baseAnimation.setFillAfter(true);
		completedPuzzle.startAnimation(baseAnimation);
	}

	/* OBSERVER / OBSERVABLE */

	public void addObserver(IPuzzleViewObserver observer){
		observers.add(observer);
	}

	private void notifyObservers(){
		for(IPuzzleViewObserver observer : observers){
			observer.onPuzzleViewLoaded(this);
		}
	}
}
