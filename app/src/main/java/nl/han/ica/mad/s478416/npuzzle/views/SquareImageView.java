package nl.han.ica.mad.s478416.npuzzle.views;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import nl.han.ica.mad.s478416.npuzzle.R;
import nl.han.ica.mad.s478416.npuzzle.utils.BitmapUtils;


/*
	Custom ImageView that forces a 1:1 aspect ratio
 */
public class SquareImageView extends ImageView {
	public SquareImageView(Context context) {
		super(context, null, 0);
	}

	public SquareImageView(Context context, AttributeSet attrs){
		super(context, attrs, 0);
	}

	public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
	}

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);    // force "container" aspect ratio
    }
}