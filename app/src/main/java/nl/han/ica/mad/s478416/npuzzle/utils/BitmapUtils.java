package nl.han.ica.mad.s478416.npuzzle.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils {
	public static Bitmap[] sliceAsGrid(Bitmap source, int gridSize){
		Bitmap[] pieces = new Bitmap[gridSize * gridSize];
		int pieceWidth = source.getWidth() / gridSize;

		for(int i = 0; i < pieces.length; i++){
			int row = i / gridSize;
			int col = i % gridSize;
			pieces[i] = Bitmap.createBitmap(source, pieceWidth * col, pieceWidth * row, pieceWidth, pieceWidth);
		}

		return pieces;
	}

	public static Bitmap scaledBitmapFromResource(Resources res, int resId, int width, int height){
		// decode original bitmap
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// decode sampled bitmap
		options.inSampleSize = calcInSampleSize(options, width, height);  // Calculate inSampleSize
		options.inJustDecodeBounds = false;                                     // Decode bitmap with inSampleSize set
		Bitmap sampledBitmap = BitmapFactory.decodeResource(res, resId, options);

		// scale sampled bitmap and return
		return Bitmap.createScaledBitmap(sampledBitmap, width, height, false);
	}

	private static int calcInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

}
