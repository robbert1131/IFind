package csi.fhict.org.ifindapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by Gregory on 6-10-2016.
 */

public class ProductView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_viewer);

        String position = getIntent().getExtras().getString("product");
        WebView webview = (WebView) findViewById(R.id.webView);
        webview.loadUrl(position);
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);

    }
}
