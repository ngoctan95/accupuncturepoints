package projects.android.acupuncturepoint.Presenters.MainPresenter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import projects.android.acupuncturepoint.R;
import projects.android.acupuncturepoint.Views.MainView.IViewMain;
import projects.android.acupuncturepoint.Views.MainView.MainActivity;

public class MainPresenter implements IMainPresenter {

    private MainActivity mainActivity;
    private IViewMain iViewMain;
    private Context context;

    public MainPresenter(IViewMain iViewMain, Context context) {
        this.iViewMain = iViewMain;
        this.context = context;
    }

    @Override
    public int findLastestAcupuncturePoints(int x, int y) {
        Log.d("====================", "");
        JSONObject obj=null;
        JSONArray jsonArray = new JSONArray();
        String temp = loadJSONFromAsset(context);
//        Log.d("===", temp);
        try {
            JSONObject jsonObject = new JSONObject(temp);
            JSONObject songsObject = jsonObject.getJSONObject("AcupuncturePoints");
            jsonArray = songsObject.toJSONArray(songsObject.names());

            Toast.makeText(context, jsonArray.length(), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Log.d("===", e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    private String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.huyet);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}
