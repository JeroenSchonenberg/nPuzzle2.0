package nl.han.ica.mad.s478416.npuzzle.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils {
    public static int calcInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap sampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calcInSampleSize(options, reqWidth, reqHeight);  // Calculate inSampleSize
        options.inJustDecodeBounds = false;                                     // Decode bitmap with inSampleSize set

        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap scaledBitmapFromResource(Resources res, int resId, int width, int height){
        Bitmap b = BitmapUtils.sampledBitmapFromResource(res, resId, width, height);
        return Bitmap.createScaledBitmap(b, width, height, false);
    }

	public static Bitmap[] sliceAsGrid(Bitmap source, int gridSize){
		Bitmap[] result = new Bitmap[gridSize * gridSize];
		int pieceWidth = source.getWidth() / gridSize;

		for(int i = 0; i < result.length; i++){
			int row = i / gridSize;
			int col = i % gridSize;
			result[i] = Bitmap.createBitmap(source, pieceWidth * col, pieceWidth * row, pieceWidth, pieceWidth);
		}

		return result;
	}
}
