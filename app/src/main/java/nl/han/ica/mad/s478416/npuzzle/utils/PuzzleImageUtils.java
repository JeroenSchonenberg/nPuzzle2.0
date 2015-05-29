package nl.han.ica.mad.s478416.npuzzle.utils;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import nl.han.ica.mad.s478416.npuzzle.R;

/**
 * Created by jeroen on 29/05/15.
 */
public class PuzzleImageUtils {
	private static String TAG = "PuzzleImageUtils";

	public static List<Integer> getImgResIds(){
		List<Integer> imageResIds = new ArrayList<Integer>();

		Field[] drawables = R.drawable.class.getFields();
		for (Field drawable : drawables) {
			try {
				if(drawable.getName().startsWith("puzzle_")){
					imageResIds.add(drawable.getInt(null));
				}
			} catch (Exception e) {
				Log.e(TAG, "### OOPS", e);
			}
		}

		return imageResIds;
	}
}
