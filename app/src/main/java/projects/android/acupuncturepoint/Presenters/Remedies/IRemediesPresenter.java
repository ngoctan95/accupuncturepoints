package projects.android.acupuncturepoint.Presenters.Remedies;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

import projects.android.acupuncturepoint.R;

public interface IRemediesPresenter {
    String loadJSONFromAsset(Context context);
}
