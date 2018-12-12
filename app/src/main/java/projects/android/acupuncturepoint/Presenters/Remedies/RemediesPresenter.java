package projects.android.acupuncturepoint.Presenters.Remedies;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

import projects.android.acupuncturepoint.R;
import projects.android.acupuncturepoint.Views.Remedie.IViewRemedie;

public class RemediesPresenter implements IRemediesPresenter {
    private IViewRemedie iViewRemedie;
    private Context context;

    public RemediesPresenter(IViewRemedie iViewRemedie, Context context) {
        this.iViewRemedie = iViewRemedie;
        this.context = context;
    }

    @Override
    public String loadJSONFromAsset(Context context) {

        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.vithuoc);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        iViewRemedie.showListViThuoc(json);
        return json;
    }
}
