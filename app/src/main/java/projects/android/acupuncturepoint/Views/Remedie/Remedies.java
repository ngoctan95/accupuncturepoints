package projects.android.acupuncturepoint.Views.Remedie;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Objects;

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
    private List<ViThuoc> viThuocListTemp = new ArrayList<>();
    private ViThuocAdapter arrayAdapter;
    private int count = 0;
    private EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remedies);
        init();
        initStatusBar();
//        getWebsite();
        remediesPresenter.loadJSONFromAsset(getApplicationContext());
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edtSearch.getText().toString().isEmpty()) {
                    viThuocListTemp = new ArrayList<>();
                    for (int j = 0; j < viThuocList.size(); j++) {
                        if (viThuocList.get(j).getName().toLowerCase().replace(" ","").trim().contains(edtSearch.getText().toString().toLowerCase().replace(" ","").trim())) {
                            viThuocListTemp.add(viThuocList.get(j));
                        }
                    }
                    arrayAdapter
                            = new ViThuocAdapter(getApplicationContext(), viThuocListTemp);
                    arrayAdapter.notifyDataSetChanged();
                    remediesList.setAdapter(arrayAdapter);
                } else {
                    arrayAdapter
                            = new ViThuocAdapter(getApplicationContext(), viThuocList);
                    arrayAdapter.notifyDataSetChanged();
                    remediesList.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    void init() {
        edtSearch = findViewById(R.id.edtSearch);
        toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        back = findViewById(R.id.back);
        mTitle.setText("Từ điển vị thuốc");
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

        remediesList.setOnItemClickListener((adapterView, view, i, l) -> {
            ViThuoc viThuoc = (ViThuoc) adapterView.getAdapter().getItem(i);
            final Dialog dialog = new Dialog(Remedies.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.layout_dialog_vithuoc);

            // set the custom dialog components - text, image and button
            TextView text = (TextView) dialog.findViewById(R.id.text);
            if (!isNetworkAvailable(getApplicationContext())) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        text.setText("Vui lòng kiểm tra kết nối mạng để đồng bộ dữ liệu với server. Hãy thử lại sau!");
                    }
                }, 1000);
            } else {
                getDataFromLink(viThuoc.getLink(), text);
            }

            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        });
    }

    private void getDataFromLink(String link, TextView text) {
        new Thread(() -> {
            StringBuilder builder = new StringBuilder();
            try {
                Document document = Jsoup.connect(link).get();
                Elements element = document.select("div[id=6]");
                builder.append(element);
            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    text.setText(Html.fromHtml(concatString(builder.toString()), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    text.setText(Html.fromHtml(concatString(builder.toString())));
                }
            });
        }).start();
    }

    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return Objects.requireNonNull(connectivityManager).getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private String concatString(String data) {
        int findPos = data.indexOf("Nơi mua bán vị thuốc");
        return String.valueOf(data.subSequence(0, findPos));
    }

    private void getWebsite() {
        new Thread(() -> {
            final StringBuilder builder = new StringBuilder();

            try {
                JSONObject jsonObject = new JSONObject();
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
                                String string = "{ \"name\": " + "\"" + element3.get(j).text() + "\" " + ",\n" + "\"link\": " + "\"" + element3.get(j).attr("href") + "\" " + "},";
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
