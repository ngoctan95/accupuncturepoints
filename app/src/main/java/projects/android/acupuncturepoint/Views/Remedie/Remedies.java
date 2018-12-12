package projects.android.acupuncturepoint.Views.Remedie;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import projects.android.acupuncturepoint.Models.WebHTML.ViThuoc;
import projects.android.acupuncturepoint.Presenters.Remedies.RemediesPresenter;
import projects.android.acupuncturepoint.Presenters.Remedies.ViThuocAdapter;
import projects.android.acupuncturepoint.R;
import projects.android.acupuncturepoint.Views.MainView.MainActivity;

public class Remedies extends AppCompatActivity implements IViewRemedie {
    private ImageView back;
    private TextView mTitle;
    private Toolbar toolbarTop;
    private ListView remediesList;
    private RemediesPresenter remediesPresenter;
    private List<ViThuoc> viThuocList = new ArrayList<>();
    private ViThuocAdapter arrayAdapter;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remedies);
        init();
        initStatusBar();
//        getWebsite();
        remediesPresenter.loadJSONFromAsset(getApplicationContext());
    }

    void init() {
        toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        back = findViewById(R.id.back);
        mTitle.setText("Từ điển bài thuốc");
        remediesList = findViewById(R.id.remediesList);
        arrayAdapter
                = new ViThuocAdapter(this, viThuocList);
        remediesList.setAdapter(arrayAdapter);

        back.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        remediesPresenter = new RemediesPresenter(this, getApplicationContext());
        remediesPresenter.loadJSONFromAsset(getApplicationContext());
    }


    private void getWebsite() {
        new Thread(() -> {
            final StringBuilder builder = new StringBuilder();

            try {
                JSONObject jsonObject =new JSONObject();
                JSONArray jsonArray = new JSONArray();
                Document doc = Jsoup.connect("https://www.thaythuoccuaban.com/vithuoc/index.html").get();
                Elements element = doc.select("div[id=1]");
                Log.d("===", String.valueOf(element.size()));
                for (int i = 0; i < element.size(); i++) {
                    Element list = element.get(i).select("div[id=1] > div[id=4] > div[id=5] > table[id=table10]").first();
                    Element element1 = list.select("tbody").first();
                    Element element2 = element1.select("tr").first();
                    Elements elements = element2.select("td");
                    for (int i1 = 0; i1 < elements.size(); i1++) {
                        Elements element3 = elements.get(i1).select("a");
                        for (int j = 0; j < element3.size(); j++) {
                            builder.append(element3.get(j).attr("href")).append(" ").append(element3.get(j).text()).append("\n");
                            count++;
                            ViThuoc viThuoc = new ViThuoc();
                            viThuoc.setName(element3.get(j).text());
                            viThuoc.setLink(element3.get(j).attr("href"));
                            viThuocList.add(viThuoc);
                            try {
                                String string = "{ \"name\": " + "\"" + element3.get(j).text() + "\" "+",\n" +"\"link\": " + "\"" + element3.get(j).attr("href") +"\" "+ "}";
                               Log.d("~~~~~~~~~", string);
                                JSONObject jsonObject1 = new JSONObject(string);
                                jsonArray.put(jsonObject1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                JSONObject jsonObject1 = new JSONObject();
                try {
                    jsonObject1.put("ViThuoc", jsonArray);
//                    Log.d("===============", jsonObject1.toString());
//                    writeStringAsFile(getApplicationContext(), jsonObject1.toString(), "vithuoc.json");
                    logLargeString(jsonObject1.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                builder.append("Error : ").append(e.getMessage()).append("\n");
            }

//            runOnUiThread(() -> Log.d("=======" + count, builder.toString()));
            runOnUiThread(() -> arrayAdapter.notifyDataSetChanged());
        }).start();
    }

    public void logLargeString(String str) {
        final int chunkSize = 3000;
        for (int i = 0; i < str.length(); i += chunkSize) {
            Log.d("====", str.substring(i, Math.min(str.length(), i + chunkSize)));
        }
    }
    public static void writeStringAsFile(Context context, final String fileContents, String fileName) {
        try {
            FileWriter out = new FileWriter(new File(context.getFilesDir(), fileName));
            out.write(fileContents);
            out.close();
        } catch (IOException e) {
//Log.d("=====================","");
        }
    }


    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    void initStatusBar() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    @Override
    public void showListViThuoc(String vithuoc) {
        JSONArray jsonArray;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(vithuoc);
            jsonArray = jsonObject.getJSONArray("ViThuoc");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                ViThuoc viThuoc = new ViThuoc();
                viThuoc.setName(json.getString("name"));
                viThuoc.setLink(json.getString("link"));
                viThuocList.add(viThuoc);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        arrayAdapter.notifyDataSetChanged();
    }
}
