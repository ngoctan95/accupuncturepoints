package projects.android.acupuncturepoint.Views.Drug;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
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

import projects.android.acupuncturepoint.Models.WebHTML.BaiThuoc;
import projects.android.acupuncturepoint.Models.WebHTML.ViThuoc;
import projects.android.acupuncturepoint.Presenters.Remedies.RemediesPresenter;
import projects.android.acupuncturepoint.Presenters.Remedies.ViThuocAdapter;
import projects.android.acupuncturepoint.R;
import projects.android.acupuncturepoint.Views.MainView.MainActivity;
import projects.android.acupuncturepoint.Views.Remedie.Remedies;

public class Drug extends AppCompatActivity {
    private ImageView back;
    private TextView mTitle, txtContent;
    private Toolbar toolbarTop;
    private ListView drugList;
    private List<BaiThuoc> baiThuocList = new ArrayList<>();
    private List<BaiThuoc> baiThuocListTemp = new ArrayList<>();
    private DrugAdapter arrayAdapter;
    private List<String> listLink = new ArrayList<>();
    private int count = 0;
    private EditText edtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug);
        initList();
        init();
        initStatusBar();
        String a = loadJSONFromAsset(getApplicationContext());
        showListBaiThuoc(a);
        listen();
        edtSearch = findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edtSearch.getText().toString().isEmpty()) {
                    baiThuocListTemp = new ArrayList<>();
                    for (int j = 0; j < baiThuocList.size(); j++) {
                        if (baiThuocList.get(j).getName().toLowerCase().replace(" ", "").trim().contains(edtSearch.getText().toString().toLowerCase().replace(" ", "").trim())) {
                            baiThuocListTemp.add(baiThuocList.get(j));
                        }
                    }
                    arrayAdapter
                            = new DrugAdapter(getApplicationContext(), baiThuocListTemp);
                    arrayAdapter.notifyDataSetChanged();
                    drugList.setAdapter(arrayAdapter);
                } else {
                    arrayAdapter
                            = new DrugAdapter(getApplicationContext(), baiThuocList);
                    arrayAdapter.notifyDataSetChanged();
                    drugList.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (v != null) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        } else {
            //deprecated in API 26
            if (v != null) {
                v.vibrate(500);
            }
        }
    }

    private void listen() {
        drugList.setOnItemClickListener((adapterView, view, i, l) -> {
            BaiThuoc baiThuoc = (BaiThuoc) adapterView.getAdapter().getItem(i);
            final Dialog dialog = new Dialog(Drug.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.layout_dialog_baithuoc);

            // set the custom dialog components - text, image and button
            TextView text = (TextView) dialog.findViewById(R.id.text);
            if (!isNetworkAvailable(getApplicationContext())) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        text.setText("Vui lòng kiểm tra kết nối mạng để đồng bộ dữ liệu với server. Hãy thử lại sau!");
                        vibrate();
                    }
                }, 1000);
            } else {
                getDataFromLink(baiThuoc.getLink(), text);
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

    @Override
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }

    private void initList() {
        String a = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanA.html";
        String b = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanB.html";
        String c = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanC.html";
        String d = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanD.html";
        String g = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanG.html";
        String h = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanH.html";
        String i = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanI.html";
        String k = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanK.html";
        String l = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanL.html";
        String m = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanM.html";
        String n = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanN.html";
        String o = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanO.html";
        String p = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanP.html";
        String q = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanQ.html";
        String s = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanS.html";
        String u = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanU.html";
        String v = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanV.html";
        String x = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanX.html";
        String y = "https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/BangtratheovanY.html";
        listLink.add(a);
        listLink.add(b);
        listLink.add(c);
        listLink.add(d);
        listLink.add(g);
        listLink.add(h);
        listLink.add(l);
        listLink.add(m);
        listLink.add(n);
        listLink.add(o);
        listLink.add(p);
        listLink.add(q);
        listLink.add(k);
        listLink.add(s);
        listLink.add(u);
        listLink.add(v);
        listLink.add(x);
        listLink.add(y);
        listLink.add(i);

    }

    void initStatusBar() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    public String loadJSONFromAsset(Context context) {

        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.baithuoc);
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

    public void showListBaiThuoc(String baithuoc) {
        JSONArray jsonArray;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(baithuoc);
            jsonArray = jsonObject.getJSONArray("BaiThuoc");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                BaiThuoc baiThuoc = new BaiThuoc();
                baiThuoc.setName(json.getString("name"));
                baiThuoc.setLink(json.getString("link"));
                baiThuocList.add(baiThuoc);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        arrayAdapter.notifyDataSetChanged();
    }

    public void logLargeString(String str) {
        final int chunkSize = 4000;
        for (int i = 0; i < str.length(); i += chunkSize) {
            Log.d("====", str.substring(i, Math.min(str.length(), i + chunkSize)));
        }
    }

    private void getWebsite() {
        new Thread(() -> {
            final StringBuilder builder = new StringBuilder();

            try {
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                Document doc = Jsoup.connect("https://www.thaythuoccuaban.com/bai_thuoc_chua_benh_viem_dau/danhmucbaithuoctheovan.html").get();
                Element iframe = doc.select("table").first();
                Element element = iframe.select("tbody").first();
                Element element1 = element.select("tr").first();
                Elements element2 = element1.select("td");
                builder.append(doc);
//                for (int i = 0; i < element2.size(); i++) {
//                    Elements h2 = element2.get(i).select("h2");
//                    builder.append(h2);
//                }
//                String iframeSrc = iframe.attr("src");
//                if(iframeSrc != null) {
//                    iframeContentDoc = Jsoup.connect(iframeSrc).get();
//                }
//                Element element1 = element.select("")
//                builder.append(iframe);
//                for (int i = 0; i < element.size(); i++) {
//                    Element list = element.get(i).select("div[id=1] > div[id=4] > div[id=5] > table[id=table10]").first();
//                    Element element1 = list.select("tbody").first();
//                    Element element2 = element1.select("tr").first();
//                    Elements elements = element2.select("td");
//                    for (int i1 = 0; i1 < elements.size(); i1++) {
//                        Elements element3 = elements.get(i1).select("a");
//                        for (int j = 0; j < element3.size(); j++) {
//                            builder.append(element3.get(j).attr("href")).append(" ").append(element3.get(j).text()).append("\n");
////                            count++;
////                            ViThuoc viThuoc = new ViThuoc();
////                            viThuoc.setName(element3.get(j).text());
////                            viThuoc.setLink(element3.get(j).attr("href"));
////                            viThuocList.add(viThuoc);
////                            try {
////                                String string = "{ \"name\": " + "\"" + element3.get(j).text() + "\" " + ",\n" + "\"link\": " + "\"" + element3.get(j).attr("href") + "\" " + "}";
////                                Log.d("~~~~~~~~~", string);
////                                JSONObject jsonObject1 = new JSONObject(string);
////                                jsonArray.put(jsonObject1);
////                            } catch (JSONException e) {
////                                e.printStackTrace();
////                            }
//                        }
//                    }
//                }
//                JSONObject jsonObject1 = new JSONObject();
//                try {
//                    jsonObject1.put("ViThuoc", jsonArray);
////                    Log.d("===============", jsonObject1.toString());
////                    writeStringAsFile(getApplicationContext(), jsonObject1.toString(), "vithuoc.json");
////                    logLargeString(jsonObject1.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            } catch (IOException e) {
                builder.append("Error : ").append(e.getMessage()).append("\n");
            }

            runOnUiThread(() -> Log.d("=======", builder.toString()));
//            runOnUiThread(() -> arrayAdapter.notifyDataSetChanged());
        }).

                start();

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
        int findPos = data.indexOf("Tham khảo mua bán bài thuốc");
        if (findPos > 0) {
            return String.valueOf(data.subSequence(0, findPos));
        } else {
            return data;
        }
    }

    void init() {
        toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
//        txtContent = findViewById(R.id.txtContent);
        back = findViewById(R.id.back);
        mTitle.setText("Từ điển bài thuốc");
        drugList = findViewById(R.id.drugList);
        arrayAdapter = new DrugAdapter(this, baiThuocList);
        drugList.setAdapter(arrayAdapter);

        back.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });
    }
}
