package csi.fhict.org.ifindapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;


import static java.security.AccessController.getContext;

/**
 * Created by Gregory on 6-10-2016.
 */

public class ProductList extends AppCompatActivity implements ShakeEventManager.ShakeListener, SimpleGestureFilter.SimpleGestureListener {

    ListView mListView;
    SimpleAdapter adap;
    String imgurl;
    String RandomJa = "ja";
    String title;
    int Minprijs;
    int Maxprijs;
    boolean Desc;
    String X;
    String Y;
    String minkm;
    String maxkm;
    private ShakeEventManager sd;
    private SimpleGestureFilter detector;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list);

        detector = new SimpleGestureFilter(this, this);

        title = getIntent().getExtras().getString("Title");
        Minprijs = getIntent().getExtras().getInt("minprijs");
        Maxprijs = getIntent().getExtras().getInt("maxprijs");
        Desc = getIntent().getExtras().getBoolean("Desc");
        X = getIntent().getExtras().getString("x");
        Y = getIntent().getExtras().getString("y");
        minkm = getIntent().getExtras().getString("minkm");
        maxkm = getIntent().getExtras().getString("maxkm");
        // URL to the JSON data
        String strUrl = "http://95.97.27.12/IFind/JSONview.php?Name=" + title + "&Min_Prijs=" + Minprijs + "&Max_Prijs=" + Maxprijs + "&Desc=" + Desc + "&Rand=" + RandomJa + "&X=" +
                X + "&Y=" + Y + "&minkm=" + minkm + "&maxkm=" + maxkm;

        // Creating a new non-ui thread task to download json data
        DownloadTask downloadTask = new DownloadTask();

        // Starting the download process
        downloadTask.execute(strUrl);

        // Getting a reference to ListView of activity_main
        mListView = (ListView) findViewById(R.id.product_list);
        //createData();
        sd = new ShakeEventManager();
        sd.setListener(this);
        sd.init(this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(final AdapterView<?> parent, View view,
                                    final int position, long id) {
                //Get the name from the array that is in the same position as the chosen listitem.
                //Todo start intent and pass name using putExtra

                SimpleAdapter adapter = adap;
                int pos = position;

                HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(pos);
                imgurl = (String) hm.get("Title_url");

                Intent intent = new Intent(getApplicationContext(), ProductView.class);
                intent.putExtra("product", imgurl);

                startActivity(intent);
            }
        });


        //Button pbutton = (Button) findViewById(R.id.btn_personal);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void myClickHandler(View v) {
        //get the row the clicked button is in
        LinearLayout vwParentRow = (LinearLayout) findViewById(R.id.layoutbutton);
        Button btnChild = (Button) vwParentRow.getChildAt(1);
        final int position = mListView.getPositionForView(v);
        SimpleAdapter adapter = adap;

        HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(position);
//        ArrayList<HashMap<String, Object>> arr = new ArrayList<HashMap<String, Object>>();
//        arr.add(hm);
//        Intent intent = new Intent(this, PersonalList.class);
//        intent.putExtra("map", arr);
//        startActivity(intent);
        //vwParentRow.refreshDrawableState();
        hm.remove("position");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = sharedPreferences.edit();
//        SharedPreferences keyValues = getApplicationContext().getSharedPreferences("Your_Shared_Prefs", Context.MODE_PRIVATE);
//        SharedPreferences.Editor keyValuesEditor = keyValues.edit();
        for (String s : hm.keySet()) {
            editor.putString(s, (String)hm.get(s));
        }
        editor.commit();
        Intent intent = new Intent(this, PersonalList.class);
        startActivity(intent);
    }

    public void myShareHandler(View v){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Delen met: ");
        builder1.setCancelable(true);
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder1.setView(input);
        builder1.setPositiveButton(
                "Ja",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "Nee",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exceptiondownloadingurl", e.toString());
        } finally {
            iStream.close();
        }

        return data;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ProductList Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    /**
     * AsyncTask to download json data
     */
    private class DownloadTask extends AsyncTask<String, Integer, String> {
        String data = null;

        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {

            // The parsing of the xml data is done in a non-ui thread
            ListViewLoaderTask listViewLoaderTask = new ListViewLoaderTask();

            // Start parsing xml data
            listViewLoaderTask.execute(result);

        }
    }

    /**
     * AsyncTask to parse json data and load ListView
     */
    private class ListViewLoaderTask extends AsyncTask<String, Void, SimpleAdapter> {

        JSONObject jObject;

        // Doing the parsing of xml data in a non-ui thread
        @Override
        protected SimpleAdapter doInBackground(String... strJson) {
            try {
                jObject = new JSONObject(strJson[0]);
                GetProducts_JSON productJsonParser = new GetProducts_JSON();
                productJsonParser.parse(jObject);
            } catch (Exception e) {
                Log.d("JSON Exception1", e.toString());
            }

            // Instantiating json parser class
            GetProducts_JSON productJsonParser = new GetProducts_JSON();

            // A list object to store the parsed countries list
            List<HashMap<String, Object>> product = null;

            try {
                // Getting the parsed data as a List construct
                product = productJsonParser.parse(jObject);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }

            // Keys used in Hashmap
            String[] from = {"Title",
                    "Desc", "img", "Date", "Prijs", "Loc"};

            // Ids of views in listview_layout
            int[] to = {R.id.Title, R.id.Desc, R.id.Image_product, R.id.Date, R.id.Price, R.id.Loc};

            // Instantiating an adapter to store each items
            // R.layout.listview_layout defines the layout of each item
            adap = new SimpleAdapter(getBaseContext(), product, R.layout.product_listview, from, to);

            return adap;


        }

        /**
         * Invoked by the Android on "doInBackground" is executed
         */
        @Override
        protected void onPostExecute(SimpleAdapter adapter) {

            // Setting adapter for the listview
            mListView.setAdapter(adapter);

            for (int i = 0; i < adapter.getCount(); i++) {
                HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(i);
                String imgUrl = (String) hm.get("img_path");
                ImageLoaderTask imageLoaderTask = new ImageLoaderTask();

                HashMap<String, Object> hmDownload = new HashMap<String, Object>();
                hm.put("img_path", imgUrl);
                hm.put("position", i);

                // Starting ImageLoaderTask to download and populate image in the listview
                imageLoaderTask.execute(hm);
            }
        }
    }

    /**
     * AsyncTask to download and load an image in ListView
     */
    private class ImageLoaderTask extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {

        @Override
        protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hm) {

            InputStream iStream = null;
            String imgUrl = (String) hm[0].get("img_path");
            int position = (Integer) hm[0].get("position");

            URL url;
            try {
                url = new URL(imgUrl);

                // Creating an http connection to communicate with url
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                // Getting Caching directory
                File cacheDirectory = getBaseContext().getCacheDir();

                // Temporary file to store the downloaded image
                File tmpFile = new File(cacheDirectory.getPath() + "/wpta_" + position + ".jpg");

                // The FileOutputStream to the temporary file
                FileOutputStream fOutStream = new FileOutputStream(tmpFile);

                // Creating a bitmap from the downloaded inputstream
                Bitmap b = BitmapFactory.decodeStream(iStream);

                // Writing the bitmap to the temporary file as png file
                b.compress(Bitmap.CompressFormat.JPEG, 100, fOutStream);

                // Flush the FileOutputStream
                fOutStream.flush();

                //Close the FileOutputStream
                fOutStream.close();

                // Create a hashmap object to store image path and its position in the listview
                HashMap<String, Object> hmBitmap = new HashMap<String, Object>();

                // Storing the path to the temporary image file
                hmBitmap.put("img", tmpFile.getPath());

                // Storing the position of the image in the listview
                hmBitmap.put("position", position);

                // Returning the HashMap object containing the image path and position
                return hmBitmap;


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> result) {
            // Getting the path to the downloaded image
            String path = (String) result.get("img");

            // Getting the position of the downloaded image
            int position = (Integer) result.get("position");

            // Getting adapter of the listview
            SimpleAdapter adapter = (SimpleAdapter) mListView.getAdapter();

            // Getting the hashmap object at the specified position of the listview
            HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(position);

            // Overwriting the existing path in the adapter
            hm.put("img", path);

            // Noticing listview about the dataset changes
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void createData() {
        String strUrl = "http://95.97.27.12/IFind/JSONview.php?Name=" + title + "&Min_Prijs=" + Minprijs + "&Max_Prijs=" + Maxprijs + "&Desc=" + Desc + "&Rand=" + RandomJa + "&X=" +
                X + "&Y=" + Y + "&minkm=" + minkm + "&maxkm=" + maxkm;

        // Creating a new non-ui thread task to download json data
        DownloadTask downloadTask = new DownloadTask();

        // Starting the download process
        downloadTask.execute(strUrl);

    }

    @Override
    public void onShake() {
        createData();
        //adpt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        Toast.makeText(this, "Refresh data...", Toast.LENGTH_SHORT).show();
        //mListView.setAdapter(adap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sd.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sd.deregister();
    }

    @Override
    public void onSwipe(int direction) {
        String str = "";

        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT : str = "Swipe Right";
                break;
            case SimpleGestureFilter.SWIPE_LEFT :  str = "Swipe Left";
                Intent i = new Intent(getApplicationContext(), PersonalList.class);
                startActivity(i);
                break;
            case SimpleGestureFilter.SWIPE_DOWN :  str = "Swipe Down";
                break;
            case SimpleGestureFilter.SWIPE_UP :    str = "Swipe Up";
                break;

        }
    }

    @Override
    public void onDoubleTap() {
        Intent i = new Intent(getApplicationContext(), PersonalList.class);
        startActivity(i);
        Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }
}
