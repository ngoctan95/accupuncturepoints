package projects.android.acupuncturepoint.Views.Books;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import projects.android.acupuncturepoint.R;

public class Books extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        webView = (WebView) findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://drive.google.com/drive/folders/1BBCDbkDd3i2K3RTEqYA_MHE072SS5eQL");

        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}
