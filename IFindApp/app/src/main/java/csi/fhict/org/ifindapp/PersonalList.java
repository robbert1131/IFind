package csi.fhict.org.ifindapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Gregory on 20-10-2016.
 */

public class PersonalList extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_list);

        Intent intent = getIntent();
        HashMap<String, Object> hashMap = (HashMap<String, Object>)intent.getSerializableExtra("map");
        String imgUrl = (String) hashMap.get("img_path");
        Log.v("HashMapTest", imgUrl);
    }
}
