package csi.fhict.org.ifindapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gregory on 20-10-2016.
 */

public class PersonalList extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_list);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //SharedPreferences keyValues = getApplicationContext().getSharedPreferences("Your_Shared_Prefs", Context.MODE_PRIVATE);
        HashMap<String, Object> hm = (HashMap<String, Object>) sharedPreferences.getAll();
        ArrayList<HashMap<String, Object>> arr = new ArrayList<HashMap<String, Object>>();
        arr.add(hm);
        arr.size();
//        for (String s : hm.keySet()) {
//            String value= (String)hm.get(s);
//            //Use Value
//            Toast.makeText(this,value, Toast.LENGTH_LONG).show();
//            Log.d(value, value);
//        }

//        Intent intent = getIntent();
//        ArrayList<HashMap<String, Object>> hashMap = (ArrayList<HashMap<String, Object>>)intent.getSerializableExtra("map");
//        //String imgUrl = (String) hashMap.get("img_path");
//        //Log.v("HashMapTest", imgUrl);
//
        String[] from = new String[] { "Title",
                "Desc", "img", "Date", "Prijs", "Loc" };
        int[] to = new int[] { R.id.Title, R.id.Desc,  R.id.Image_product, R.id.Date, R.id.Price, R.id.Loc };
//
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), arr, R.layout.product_listview , from, to);
        //setListAdapter(adapter);
        ListView mListview = (ListView) findViewById(R.id.personal_list);
        mListview.setAdapter(adapter);
    }
}
