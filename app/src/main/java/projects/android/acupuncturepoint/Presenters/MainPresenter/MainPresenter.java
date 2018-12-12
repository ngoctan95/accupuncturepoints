package projects.android.acupuncturepoint.Presenters.MainPresenter;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import projects.android.acupuncturepoint.Models.AccupuncturePointData.AccupuncturePoint;
import projects.android.acupuncturepoint.R;
import projects.android.acupuncturepoint.Views.MainView.IViewMain;
import projects.android.acupuncturepoint.Views.MainView.MainActivity;

public class MainPresenter implements IMainPresenter {

    private IViewMain iViewMain;
    private Context context;

    public MainPresenter(IViewMain iViewMain, Context context) {
        this.iViewMain = iViewMain;
        this.context = context;
    }

    @Override
    public List<AccupuncturePoint> getListAcupuncture(int currentImg) {
        String temp = loadJSONFromAsset(context);
        JSONArray jsonArray;
        JSONObject jsonObject = null;
        List<AccupuncturePoint> accupuncturePointList = new ArrayList<>();
        try {
            jsonObject = new JSONObject(temp);
            jsonArray = jsonObject.getJSONArray("AcupuncturePoints");
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject json = jsonArray.getJSONObject(i);
                String id = json.getString("id");
                String name = json.getString("name");
                String anotherName = json.has("anotherName") ? json.getString("anotherName") : "";
                String position = json.has("position") ? json.getString("position") : "";
                String operation = json.has("operation") ? json.getString("operation") : "";
                String deportment = json.has("deportment") ? json.getString("deportment") : "";
                String fire = json.has("fire") ? json.getString("fire") : "";
                String xPos = json.getString("x");
                String yPos = json.getString("y");
                String delta = json.getString("delta");
                String imgPos = json.getString("imgPos");
                String imgLink = json.getString("imgLink");
                String refer = json.has("refer") ? json.getString("refer") : "";
                String att = json.has("attribute") ? json.getString("attributes") : "";

                AccupuncturePoint accupuncturePoint = new AccupuncturePoint();
                accupuncturePoint.setId(id);
                accupuncturePoint.setName(name);
                accupuncturePoint.setAnotherName(anotherName);
                accupuncturePoint.setPosition(position);
                accupuncturePoint.setOperation(operation);
                accupuncturePoint.setDeportment(deportment);
                accupuncturePoint.setAttributes(att);
                accupuncturePoint.setRefer(refer);
                accupuncturePoint.setFire(fire);
                accupuncturePoint.setX(xPos);
                accupuncturePoint.setY(yPos);
                accupuncturePoint.setDelta(delta);
                accupuncturePoint.setImgPos(imgPos);
                accupuncturePoint.setImgLink(imgLink);

                if (currentImg == -1) {
                    accupuncturePointList.add(accupuncturePoint);
                } else {
                    if (currentImg == Integer.parseInt(accupuncturePoint.getId())) {
                        accupuncturePointList.add(accupuncturePoint);
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return accupuncturePointList;
    }

    @Override
    public int findIdWithLastestPoint(float x, float y, int currentImg) {
        List<AccupuncturePoint> accupuncturePointList = getListAcupuncture(-1);
        Log.d("===", String.valueOf(accupuncturePointList.size()));
        for (int i = 0; i < accupuncturePointList.size(); i++) {
            int delta = Integer.parseInt(accupuncturePointList.get(i).getDelta());
            int xPos = Integer.parseInt(accupuncturePointList.get(i).getX());
            int yPos = Integer.parseInt(accupuncturePointList.get(i).getY());
            int imgPos = Integer.parseInt(accupuncturePointList.get(i).getImgPos());

            if (x > (xPos - delta) && x < (xPos + delta) && y < (yPos + delta) && (y > yPos - delta) && imgPos == currentImg) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void findAcupuncturePoint(float x, float y, int currentImg, int width, int height) {
        List<AccupuncturePoint> accupuncturePointList = getListAcupuncture(-1);
        Log.d("===", x + " - " + y + " -" + currentImg + width + "==" + height);
        float delX = (float) ((1080*1.0) / width);
        float delY = (float) ((2035 * 1.0 )/ height);
        Log.d("=====", delX + " - " + delY + " - " +delX * x + " -" + delY * y);
        for (int i = 0; i < accupuncturePointList.size(); i++) {
            int delta = Integer.parseInt(accupuncturePointList.get(i).getDelta());
            int xPos = Integer.parseInt(accupuncturePointList.get(i).getX());
            int yPos = Integer.parseInt(accupuncturePointList.get(i).getY());
            int imgPos = Integer.parseInt(accupuncturePointList.get(i).getImgPos());

            if (x > (xPos - delta) && x < (xPos + delta) && y * delY < (yPos + delta) && (y * delY > yPos - delta) && imgPos == currentImg) {
                iViewMain.showInfoView(accupuncturePointList.get(i));
                return;
            }
        }
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
