package projects.android.acupuncturepoint.Views.ChamCuu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.facebook.share.widget.ShareButton;

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

import projects.android.acupuncturepoint.Models.WebHTML.ChamCuuModel;
import projects.android.acupuncturepoint.Models.WebHTML.HoiChungBenhModel;
import projects.android.acupuncturepoint.Models.WebHTML.ViThuoc;
import projects.android.acupuncturepoint.R;
import projects.android.acupuncturepoint.Views.DongyTriBenh.DongYTrIBenhAdapter;
import projects.android.acupuncturepoint.Views.DongyTriBenh.DongYTriBenh;
import projects.android.acupuncturepoint.Views.MainView.MainActivity;

public class ChamCuu extends AppCompatActivity {
    private ImageView back;
    private TextView mTitle;
    private Toolbar toolbarTop;
    private ListView chamCuuList;
    private EditText edtSearch;
    private List<ChamCuuModel> chamCuuModels = new ArrayList<>();
    private ChamCuuAdapter chamCuuAdapter;
    private List<ChamCuuModel> chamCuuModelsTemp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cham_cuu);
        initStatusBar();
        edtSearch = findViewById(R.id.edtSearch);
        toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        back = findViewById(R.id.back);
        mTitle.setText("Châm cứu");
        chamCuuList = findViewById(R.id.drugList);
        back.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });
        chamCuuAdapter = new ChamCuuAdapter(getApplicationContext(), chamCuuModels);
        chamCuuList.setAdapter(chamCuuAdapter);
        loadJSONFromAsset(getApplicationContext());
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edtSearch.getText().toString().isEmpty()) {
                    chamCuuModelsTemp = new ArrayList<>();
                    for (int j = 0; j < chamCuuModels.size(); j++) {
                        if (chamCuuModels.get(j).getName().toLowerCase().replace(" ", "").trim().contains(edtSearch.getText().toString().toLowerCase().replace(" ", "").trim())) {
                            chamCuuModelsTemp.add(chamCuuModels.get(j));
                        }
                    }
                    chamCuuAdapter
                            = new ChamCuuAdapter(getApplicationContext(), chamCuuModelsTemp);
                    chamCuuAdapter.notifyDataSetChanged();
                    chamCuuList.setAdapter(chamCuuAdapter);
                } else {
                    chamCuuAdapter
                            = new ChamCuuAdapter(getApplicationContext(), chamCuuModels);
                    chamCuuAdapter.notifyDataSetChanged();
                    chamCuuList.setAdapter(chamCuuAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        chamCuuList.setOnItemClickListener((adapterView, view, i, l) -> {
            ChamCuuModel viThuoc = (ChamCuuModel) adapterView.getAdapter().getItem(i);
            final Dialog dialog = new Dialog(ChamCuu.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.layout_dialog_vithuoc);
            ShareButton btnShare = dialog.findViewById(R.id.btnShare);
            btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
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

    void initStatusBar() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    private void getWebsite() {
        new Thread(() -> {
            final StringBuilder builder = new StringBuilder();

            try {
                JSONArray jsonArray = new JSONArray();
                Document doc = Jsoup.connect("https://www.thaythuoccuaban.com/chamcuu/index.htm").get();
                Element element1 = doc.select("div[id=6]").last();
                Elements element = element1.getElementsByTag("p");
                for (int i = 0; i < element.size(); i++) {
//                    Log.d("=====", String.valueOf(element.get(i)));
//
                    String string = "{ \"name\": " + "\"" + element.get(i).text() + "\" " + ",\n" + "\"link\": " + "\"" + element.get(i).getElementsByTag("a").attr("href") + "\" " + "},";
                    Log.d("=====", string);

                }

            } catch (IOException e) {
                builder.append("Error : ").append(e.getMessage()).append("\n");
            }

//            runOnUiThread(() -> Log.d("=======" + count, builder.toString()));
//            runOnUiThread(() -> arrayAdapter.notifyDataSetChanged());
        }).start();
    }

    private String loadJSONFromAsset(Context context) {

        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.chamcuu);
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
            jsonArray = jsonObject.getJSONArray("ChamCuu");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                ChamCuuModel chamCuuModel = new ChamCuuModel();
                chamCuuModel.setName(jsonObject1.getString("name"));
                chamCuuModel.setLink(jsonObject1.getString("link"));
                chamCuuModels.add(chamCuuModel);
                chamCuuAdapter
                        = new ChamCuuAdapter(getApplicationContext(), chamCuuModels);
                chamCuuAdapter.notifyDataSetChanged();
                chamCuuList.setAdapter(chamCuuAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
