package lu.schoolido.sukutomo.sukutomo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by arukantara on 13/10/15.
 */
public class APIUtils {
    // Apart from the card browser caché, it's better to have a caché for all the downloaded images.
    private static LruCache<String, Bitmap> imagesMemoryCache = new LruCache<String, Bitmap>(10);

    /**
     * @param list     List to which we wish to add the paged elements. If it's null, this method only returns the number of elements.
     * @param baseURL  URL used to download the elements, without the page option.
     * @param property Property of the JSONObject we want to return. If this is null, the array will contain the entire object.
     *
     * @return The total number of elements inside schoolido.lu database.
     */
    public static int iteratePages(ArrayList list, String baseURL, String property, int pageSize) {
        String data = "";
        int count = 0;
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        try {
            HttpClient Client = new DefaultHttpClient();
            data = Client.execute(new HttpGet(baseURL), responseHandler);
            JSONObject object = new JSONObject(data);
            count = object.getInt("count");
            if (list != null) {
                for (int i = 1; i <= Math.ceil(count / pageSize); i++) {
                    data = Client.execute(new HttpGet(baseURL + "?page=" + i), responseHandler);
                    object = new JSONObject(data);
                    JSONArray array = object.getJSONArray("results");
                    for (int j = 0; j < array.length(); j++) {
                        if (property != null)
                            list.add(array.getJSONObject(j).getString(property));
                        else
                            list.add(array.getJSONObject(j));
                    }
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Adds a downloaded bitmap to the Caché
     *
     * @param key    Image identificator. Usually its URL.
     * @param bitmap Downloaded bitmap.
     */
    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            imagesMemoryCache.put(key, bitmap);
        }
    }

    /**
     * Retrieves an image from the caché.
     *
     * @param key Image identificator in the caché. Usually its URL.
     * @return Desired bitmap.
     */
    public static Bitmap getBitmapFromMemCache(String key) {
        return imagesMemoryCache.get(key);
    }

    public void loadImage(ImageView view, String url) {
        ImageDownloader downloader = new ImageDownloader(view);
        downloader.execute(url);
    }

    /**
     * Class used to download images.
     */
    private class ImageDownloader extends AsyncTask<String, String, Bitmap> {
        Bitmap bitmap;
        final WeakReference<ImageView> viewReference;

        protected ImageDownloader(ImageView view) {
            viewReference = new WeakReference<>(view);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = getBitmapFromMemCache(args[0]);
                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
                    addBitmapToMemoryCache(args[0], bitmap);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        protected void onPostExecute(Bitmap image) {

            if (image != null) {
                ImageView imageView = viewReference.get();
                // Printing image to the desired view.
                if (imageView != null) {
                    imageView.setImageBitmap(image);
                }
            }
        }
    }

    public static String saveToInternalStorage(Context context, Bitmap bitmapImage, String dir, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(dir, Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, name);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    public static void loadImageFromStorage(ImageView view, String path, String name) {
        try {
            File f = new File(path, name);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            view.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmap(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }
}
