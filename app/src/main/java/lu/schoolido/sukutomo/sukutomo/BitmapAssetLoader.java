package lu.schoolido.sukutomo.sukutomo;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class used to resize bitmaps before loading them and get Drawable from assets in order to avoid
 * OutOfMemory errors.
 */
public class BitmapAssetLoader {
    /**
     * @param res Application resources
     * @param resId Id of the Resource we want to load
     * @param reqWidth Required width
     * @param reqHeight Required height
     * @return Sampled image
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    /**
     * @param options
     * @param reqWidth Required width
     * @param reqHeight Required height
     * @return Reduction factor needed to adjust a Bitmap to the specified dimensions without losing aspect ratio.
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Original height and width of the image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /** Method use to get Drawables from assets.
     * @param path Path of the asset within main/assets folder.
     * @param res Application resources.
     * @return Drawable with the asset.
     */
    public static Drawable getAsset(String path, Resources res) {
        try {
            InputStream is = res.getAssets().open(path);
            return Drawable.createFromStream(is, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
