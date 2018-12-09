package projects.android.acupuncturepoint.Views.Remedie;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import projects.android.acupuncturepoint.R;
import projects.android.acupuncturepoint.Views.MainView.MainActivity;

public class Remedies extends AppCompatActivity {
    private ImageView back;
    private TextView mTitle;
    private Toolbar toolbarTop;
    private ListView remediesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remedies);
        init();
        initStatusBar();
        getWebsite();
    }

    void init() {
        toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
        mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        back = findViewById(R.id.back);
        mTitle.setText("Từ điển bài thuốc");
        remediesList = findViewById(R.id.remedies);
        back.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });
    }


    private void getWebsite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {
                    Document doc = Jsoup.connect("https://www.thaythuoccuaban.com/vithuoc/vienchi.htm").get();
                    Element element = doc.getElementById("6");
                    builder.append(element);
                } catch (IOException e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("=======", builder.toString());
                    }
                });
            }
        }).start();
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
