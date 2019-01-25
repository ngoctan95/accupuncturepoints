package projects.android.acupuncturepoint.Views.DongyTriBenh;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import projects.android.acupuncturepoint.Models.WebHTML.ViThuoc;
import projects.android.acupuncturepoint.Presenters.Remedies.RemediesPresenter;
import projects.android.acupuncturepoint.Presenters.Remedies.ViThuocAdapter;
import projects.android.acupuncturepoint.R;
import projects.android.acupuncturepoint.Views.MainView.MainActivity;
import projects.android.acupuncturepoint.Views.Remedie.Remedies;

public class DongYTriBenh extends AppCompatActivity {
    private ImageView back;
    private TextView mTitle;
    private Toolbar toolbarTop;
    private ListView dongyTriBenhList;
    private List<ViThuoc> viThuocList = new ArrayList<>();
    private EditText edtSearch;
    private DongYTrIBenhAdapter dongYTrIBenhAdapter;
    private List<projects.android.acupuncturepoint.Models.WebHTML.DongYTriBenh> dongYTriBenhs = new ArrayList<>();
    private List<projects.android.acupuncturepoint.Models.WebHTML.DongYTriBenh> dongYTriBenhsTemp = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dong_ytri_benh);
        initStatusBar();
        edtSearch = findViewById(R.id.edtSearch);
        toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        back = findViewById(R.id.back);
        mTitle.setText("Đông y trị bệnh");
        back.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });
        dongyTriBenhList = findViewById(R.id.drugList);
        dongYTrIBenhAdapter = new DongYTrIBenhAdapter(getApplicationContext(), dongYTriBenhs);
        dongyTriBenhList.setAdapter(dongYTrIBenhAdapter);
        loadJSONFromAsset(getApplicationContext());
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edtSearch.getText().toString().isEmpty()) {
                    dongYTriBenhsTemp = new ArrayList<>();
                    for (int j = 0; j < dongYTriBenhs.size(); j++) {
                        if (dongYTriBenhs.get(j).getName().toLowerCase().replace(" ", "").trim().contains(edtSearch.getText().toString().toLowerCase().replace(" ", "").trim())) {
                            dongYTriBenhsTemp.add(dongYTriBenhs.get(j));
                        }
                    }
                    dongYTrIBenhAdapter
                            = new DongYTrIBenhAdapter(getApplicationContext(), dongYTriBenhsTemp);
                    dongYTrIBenhAdapter.notifyDataSetChanged();
                    dongyTriBenhList.setAdapter(dongYTrIBenhAdapter);
                } else {
                    dongYTrIBenhAdapter
                            = new DongYTrIBenhAdapter(getApplicationContext(), dongYTriBenhs);
                    dongYTrIBenhAdapter.notifyDataSetChanged();
                    dongyTriBenhList.setAdapter(dongYTrIBenhAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dongyTriBenhList.setOnItemClickListener((adapterView, view, i, l) -> {
            projects.android.acupuncturepoint.Models.WebHTML.DongYTriBenh viThuoc = (projects.android.acupuncturepoint.Models.WebHTML.DongYTriBenh) adapterView.getAdapter().getItem(i);
            final Dialog dialog = new Dialog(DongYTriBenh.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
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

    private String concatString(String data) {
        int findPos = data.indexOf("Nơi mua bán vị thuốc");
        if (findPos > 0) {
            return String.valueOf(data.subSequence(0, findPos));
        }
        return data;
    }

    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return Objects.requireNonNull(connectivityManager).getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    private String loadJSONFromAsset(Context context) {

        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.dongytribenh);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        JSONArray jsonArray;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            jsonArray = jsonObject.getJSONArray("DongyTriBenh");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                projects.android.acupuncturepoint.Models.WebHTML.DongYTriBenh dongYTriBenh = new projects.android.acupuncturepoint.Models.WebHTML.DongYTriBenh();
                dongYTriBenh.setName(jsonObject1.getString("name"));
                dongYTriBenh.setLink(jsonObject1.getString("link"));
                dongYTriBenhs.add(dongYTriBenh);
                dongYTrIBenhAdapter
                        = new DongYTrIBenhAdapter(getApplicationContext(), dongYTriBenhs);
                dongYTrIBenhAdapter.notifyDataSetChanged();
                dongyTriBenhList.setAdapter(dongYTrIBenhAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private void getWebsite() {
        new Thread(() -> {
            final StringBuilder builder = new StringBuilder();

            try {
                JSONArray jsonArray = new JSONArray();
                Document doc = Jsoup.connect("https://www.thaythuoccuaban.com/thuoc_chua_benh_viem_dau/index.html").get();
                Elements element = doc.select("div[id=6] > div[id=1]");
                for (int i = 0; i < element.size(); i++) {
                    Element list = element.get(i).select("div[id=4] > div[id=5] > table[id=table10]").first();
                    Element element1 = list.select("tbody").first();
                    Element element2 = element1.select("tr").first();
                    Elements elements = element2.select("td");
                    for (int i1 = 0; i1 < elements.size(); i1++) {
                        Elements element3 = elements.get(i1).select("a");
                        for (int j = 0; j < element3.size(); j++) {
                            builder.append(element3.get(j).attr("href")).append(" ").append(element3.get(j).text()).append("\n");
                            ViThuoc viThuoc = new ViThuoc();
                            viThuoc.setName(element3.get(j).text());
                            viThuoc.setLink(element3.get(j).attr("href"));
                            viThuocList.add(viThuoc);
                            try {
                                String string = "{ \"name\": " + "\"" + element3.get(j).text() + "\" " + ",\n" + "\"link\": " + "\"" + element3.get(j).attr("href") + "\" " + "},";
                                Log.d("=====", string);
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
//            runOnUiThread(() -> arrayAdapter.notifyDataSetChanged());
        }).start();
    }

    public void logLargeString(String str) {
        final int chunkSize = 3000;
        for (int i = 0; i < str.length(); i += chunkSize) {
//            Log.d("====", str.substring(i, Math.min(str.length(), i + chunkSize)));
        }
    }


    void initStatusBar() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }
}
