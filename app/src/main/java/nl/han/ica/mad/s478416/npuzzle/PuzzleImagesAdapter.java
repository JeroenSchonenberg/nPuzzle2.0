package nl.han.ica.mad.s478416.npuzzle;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import nl.han.ica.mad.s478416.npuzzle.views.SquareImageView;

/**
 * Created by jeroen on 21/03/15.
 */
public class PuzzleImagesAdapter extends BaseAdapter {
    private Context context;
    private List<Integer> puzzleImages;

    public PuzzleImagesAdapter(Context context){
        this.context = context;
        this.puzzleImages = findAvailablePuzzleImages();
    }

    public View getView(int position, View convertView, ViewGroup parents){
		SquareImageView squareImageView = (convertView == null) ? new SquareImageView(context) : (SquareImageView) convertView;

		Picasso.with(context)
		    .load(puzzleImages.get(position))
			.placeholder(R.drawable.placeholder_puzzle_image)
			.resize(400, 400)
			.into(squareImageView);

		return squareImageView;
    }

    @Override
    public int getCount() {
        return puzzleImages.size();
    }

    @Override
    public Object getItem(int position){
        return puzzleImages.get(position);
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    private List<Integer> findAvailablePuzzleImages(){
        List<Integer> imageResIds = new ArrayList<Integer>();

        Field[] drawables = R.drawable.class.getFields();
        for (Field drawable : drawables) {
            try {
                if(drawable.getName().startsWith("puzzle_")){
                    imageResIds.add(drawable.getInt(null));
                }
            } catch (Exception e) {
                Log.e("MAD", "### OOPS", e);
            }
        }

        return imageResIds;
    }
}