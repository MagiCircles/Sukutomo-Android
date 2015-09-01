package lu.schoolido.sukutomo.sukutomo;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v8.renderscript.Element;


/**
 * This class is used to edit images. At the moment its utility is images blurring.
 */
public class ImageEdition {
    private Bitmap input;
    private Context context;
    private Bitmap output;

    /** Creates an ImageEdition instance.
     * @param b Bitmap to modify
     * @param c Context
     */
    public ImageEdition(Bitmap b, Context c) {
        input = b;
        context = c;
        output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
    }

    /** Blurs the input image and puts it in the output.
     * @param radius Radius used to blur the image
     */
    public void blurBitmap(float radius) {
        RenderScript renderScript = RenderScript.create(context);
        Allocation blurInput = Allocation.createFromBitmap(renderScript, input);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, output);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.RGBA_8888(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius);
        blur.forEach(blurOutput);
        blurOutput.copyTo(output);
        renderScript.destroy();
    }

    /**
     * @return Modified image
     */
    public Bitmap getOutput() {
        return output;
    }
}
