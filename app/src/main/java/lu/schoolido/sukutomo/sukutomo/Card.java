package lu.schoolido.sukutomo.sukutomo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by arukantara on 23/05/15.
 */
public class Card implements Parcelable{
    // General
    private int id = 0;
    private String name = "";
    private String japanese_name = "";
    private String collection = "";
    private String japanese_collection = "";
    private Attribute attribute = Attribute.SMILE;
    private boolean japan_only = false;
    private String card_image = "";
    private String round_card_image = "";
    private Rarity rarity = Rarity.N;
    private Date release_date;

    // IdolCard
    private String event = "";
    private int hp = 0;
    private int[] minimum_statistics = new int[3];
    private int[] non_idolized_maximum_statistics = new int[3];
    private int[] idolized_maximum_statistics = new int[3];
    private String card_idolized_image = "";
    private String video_story = null;
    private String japanese_video_story = null;

    // Support and RareCard
    private String skill = "None";
    private String japanese_skill = "無し";
    private String skill_details = "None";
    private String japanese_skill_details = "無し";

    // RareCard
    private boolean is_promo = false;
    private String promo_item = null;
    private boolean is_special = false;
    private String center_skill = "None";
    private String japanese_center_skill = "無し";
    private String center_skill_details = "None";
    private String japanese_center_skill_details = "無し";
    //private Bitmap bitmap;
    //private ImageView img;

    public Card(JSONObject object) throws JSONException {
        card_image = object.getString("card_image");
        card_idolized_image = object.getString("card_idolized_image");

        id = object.getInt("id");
        name = object.getString("name");
        japanese_name = object.getString("japanese_name");
        //collection = object.getString("collection");
        //japanese_collection = object.getString("japanese_collection");

        String at = object.getString("attribute");
        if(at.equalsIgnoreCase("smile"))
            attribute = Attribute.SMILE;
        else if(at.equalsIgnoreCase("pure"))
            attribute = Attribute.PURE;
        else
            attribute = Attribute.COOL;
        japan_only = object.getBoolean("japan_only");
        card_image = object.getString("card_image");
        round_card_image = object.getString("round_card_image");
        String r = object.getString("rarity");
        if(r.equalsIgnoreCase("r"))
            rarity = Rarity.R;
        else if(r.equalsIgnoreCase("sr"))
            rarity = Rarity.SR;
        else if(r.equalsIgnoreCase("ur"))
            rarity = Rarity.UR;
        else
            rarity = Rarity.N;

        // IdolCard
        //event = "";
        hp = object.getInt("hp");
        minimum_statistics[0] = object.getInt("minimum_statistics_smile");
        minimum_statistics[1] = object.getInt("minimum_statistics_pure");
        minimum_statistics[2] = object.getInt("minimum_statistics_cool");
        non_idolized_maximum_statistics[0] = object.getInt("non_idolized_maximum_statistics_smile");
        non_idolized_maximum_statistics[1] = object.getInt("non_idolized_maximum_statistics_pure");
        non_idolized_maximum_statistics[2] = object.getInt("non_idolized_maximum_statistics_cool");
        idolized_maximum_statistics[0] = object.getInt("idolized_maximum_statistics_smile");
        idolized_maximum_statistics[1] = object.getInt("idolized_maximum_statistics_pure");
        idolized_maximum_statistics[2] = object.getInt("idolized_maximum_statistics_cool");
        card_idolized_image = object.getString("card_idolized_image");
        video_story = object.getString("video_story");
        japanese_video_story = object.getString("japanese_video_story");
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            release_date = format.parse(object.getString("release_date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Support and RareCard
        skill = object.getString("skill");
        japanese_skill = object.getString("japanese_skill");
        skill_details = object.getString("skill_details");
        //japanese_skill_details = object.getString("japanese_skill_details");

        // RareCard
        is_promo = object.getBoolean("is_promo");
        promo_item = object.getString("promo_item");
        is_special = object.getBoolean("is_special");
        center_skill = object.getString("center_skill");
        japanese_center_skill = object.getString("japanese_center_skill");
        //center_skill_details = object.getString("center_skill_details");
        //japanese_center_skill_details = object.getString("japanese_center_skill_details");
    }

    public String getImageURL(boolean idolized) {
        if(idolized)
            return card_idolized_image;
        else
            return card_image;
    }

    public void showImage(boolean idolized, Bitmap bitmap, ImageView view) {
        if(idolized)
            new LoadImage(bitmap, view).execute(card_idolized_image);
        else
            new LoadImage(bitmap, view).execute(card_image);
    }

    public void showRoundImage(Bitmap bitmap, ImageView view) {
        new LoadImage(bitmap, view).execute(round_card_image);
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getJapanese_name() {
        return japanese_name;
    }

    public String getCollection() {
        return collection;
    }

    public String getJapanese_collection() {
        return japanese_collection;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public boolean isJapan_only() {
        return japan_only;
    }

    public String getCard_image() {
        return card_image;
    }

    public String getRound_card_image() {
        return round_card_image;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public String getEvent() {
        return event;
    }

    public int getHp() {
        return hp;
    }

    public int[] getMinimum_statistics() {
        return minimum_statistics;
    }

    public int[] getNon_idolized_maximum_statistics() {
        return non_idolized_maximum_statistics;
    }

    public int[] getIdolized_maximum_statistics() {
        return idolized_maximum_statistics;
    }

    public String getCard_idolized_image() {
        return card_idolized_image;
    }

    public String getVideo_story() {
        return video_story;
    }

    public String getJapanese_video_story() {
        return japanese_video_story;
    }

    public String getSkill() {
        return skill;
    }

    public String getJapanese_skill() {
        return japanese_skill;
    }

    public String getSkill_details() {
        return skill_details;
    }

    public String getJapanese_skill_details() {
        return japanese_skill_details;
    }

    public boolean is_promo() {
        return is_promo;
    }

    public String getPromo_item() {
        return promo_item;
    }

    public boolean is_special() {
        return is_special;
    }

    public String getCenter_skill() {
        return center_skill;
    }

    public String getJapanese_center_skill() {
        return japanese_center_skill;
    }

    public String getCenter_skill_details() {
        return center_skill_details;
    }

    public String getJapanese_center_skill_details() {
        return japanese_center_skill_details;
    }

    public Date getRelease_date() {
        return release_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(japanese_name);
        dest.writeString(collection);
        dest.writeString(japanese_collection);
        dest.writeInt(attribute.ordinal());
        dest.writeInt(japan_only ? 1 : 0);
        dest.writeString(card_image);
        dest.writeString(round_card_image);
        dest.writeInt(rarity.ordinal());

        dest.writeInt(hp);
        dest.writeIntArray(minimum_statistics);
        dest.writeIntArray(non_idolized_maximum_statistics);
        dest.writeIntArray(idolized_maximum_statistics);
        dest.writeString(card_idolized_image);

        dest.writeString(skill);
        dest.writeString(japanese_skill);
        dest.writeString(skill_details);
        dest.writeString(japanese_skill_details);
        dest.writeString(center_skill);
        dest.writeString(japanese_center_skill);
        dest.writeString(center_skill_details);
        dest.writeString(japanese_center_skill_details);
    }

    public static final Parcelable.Creator<Card> CREATOR
            = new Parcelable.Creator<Card>() {
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    private Card(Parcel in) {
        id = in.readInt();
        name = in.readString();
        japanese_name = in.readString();
        collection = in.readString();
        japanese_collection = in.readString();
        int at = in.readInt();
        switch(at) {
            case 0:
                attribute = Attribute.SMILE;
                break;
            case 1:
                attribute = Attribute.PURE;
                break;
            default:
                attribute = Attribute.COOL;
        }
        japan_only = in.readInt() != 0;
        card_image = in.readString();
        round_card_image = in.readString();
        int r = in.readInt();
        switch(r) {
            case 1:
                rarity = Rarity.R;
                break;
            case 2:
                rarity = Rarity.SR;
                break;
            case 3:
                rarity = Rarity.UR;
                break;
            default:
                rarity = Rarity.N;
        }

        hp = in.readInt();
        in.readIntArray(minimum_statistics);
        in.readIntArray(non_idolized_maximum_statistics);
        in.readIntArray(idolized_maximum_statistics);
        card_idolized_image = in.readString();

        skill_details = in.readString();
        japanese_skill = in.readString();
        skill_details = in.readString();
        japanese_skill_details = in.readString();
        center_skill = in.readString();
        japanese_center_skill = in.readString();
        center_skill_details = in.readString();
        japanese_center_skill_details = in.readString();

    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        Bitmap bitmap;
        final WeakReference<ImageView> viewReference;
        protected LoadImage(Bitmap bitmap, ImageView view) {
            bitmap = bitmap;
            viewReference = new WeakReference<ImageView>( view );
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                ImageView imageView = viewReference.get();
                if( imageView != null ) {
                    imageView.setImageBitmap( bitmap );
                }
                //CardBrowser.pDialog.show();
                //CardBrowser.pDialog.dismiss();

            }
        }
    }
}
