package csi.fhict.org.ifindapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Gregory on 6-10-2016.
 */

public class ProductList extends AppCompatActivity {

    ListView mListView;
    SimpleAdapter adap;
    String imgurl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list);

        String title = getIntent().getExtras().getString("Title");
        int Minprijs = getIntent().getExtras().getInt("minprijs");
        int Maxprijs = getIntent().getExtras().getInt("maxprijs");
        boolean Desc = getIntent().getExtras().getBoolean("Desc");
        // URL to the JSON data
        String strUrl = "http://95.97.27.12/IFind/JSONview.php?Name=" + title + "&Min_Prijs=" + Minprijs + "&Max_Prijs=" + Maxprijs + "&Desc=" + Desc;

        // Creating a new non-ui thread task to download json data
        DownloadTask downloadTask = new DownloadTask();

        // Starting the download process
        downloadTask.execute(strUrl);

        // Getting a reference to ListView of activity_main
        mListView = (ListView) findViewById(R.id.product_list);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
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
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exceptiondownloadingurl", e.toString());
        }finally{
            iStream.close();
        }

        return data;
    }



    /** AsyncTask to download json data */
    private class DownloadTask extends AsyncTask<String, Integer, String> {
        String data = null;
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);

            }catch(Exception e){
                Log.d("Background Task",e.toString());
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

    /** AsyncTask to parse json data and load ListView */
    private class ListViewLoaderTask extends AsyncTask<String, Void, SimpleAdapter>{

        JSONObject jObject;
        // Doing the parsing of xml data in a non-ui thread
        @Override
        protected SimpleAdapter doInBackground(String... strJson) {
            try{
                jObject = new JSONObject(strJson[0]);
                GetProducts_JSON productJsonParser = new GetProducts_JSON();
                productJsonParser.parse(jObject);
            }catch(Exception e){
                Log.d("JSON Exception1",e.toString());
            }

            // Instantiating json parser class
            GetProducts_JSON productJsonParser = new GetProducts_JSON();

            // A list object to store the parsed countries list
            List<HashMap<String, Object>> product = null;

            try{
                // Getting the parsed data as a List construct
                product = productJsonParser.parse(jObject);
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }

            // Keys used in Hashmap
            String[] from = { "Title",
                    "Desc", "img", "Date", "Prijs", "Loc"};

            // Ids of views in listview_layout
            int[] to = { R.id.Title, R.id.Desc,  R.id.Image_product, R.id.Date, R.id.Price, R.id.Loc};

            // Instantiating an adapter to store each items
            // R.layout.listview_layout defines the layout of each item
            adap = new SimpleAdapter(getBaseContext(), product, R.layout.product_listview, from, to);

            return adap;
        }

        /** Invoked by the Android on "doInBackground" is executed */
        @Override
        protected void onPostExecute(SimpleAdapter adapter) {

            // Setting adapter for the listview
            mListView.setAdapter(adapter);

            for(int i=0;i<adapter.getCount();i++){
                HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(i);
                String imgUrl = (String) hm.get("img_path");
                ImageLoaderTask imageLoaderTask = new ImageLoaderTask();

                HashMap<String, Object> hmDownload = new HashMap<String, Object>();
                hm.put("img_path",imgUrl);
                hm.put("position", i);

                // Starting ImageLoaderTask to download and populate image in the listview
                imageLoaderTask.execute(hm);
            }
        }
    }

    /** AsyncTask to download and load an image in ListView */
    private class ImageLoaderTask extends AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>>{

        @Override
        protected HashMap<String, Object> doInBackground(HashMap<String, Object>... hm) {

            InputStream iStream=null;
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
                File tmpFile = new File(cacheDirectory.getPath() + "/wpta_"+position+".jpg");

                // The FileOutputStream to the temporary file
                FileOutputStream fOutStream = new FileOutputStream(tmpFile);

                // Creating a bitmap from the downloaded inputstream
                Bitmap b = BitmapFactory.decodeStream(iStream);

                // Writing the bitmap to the temporary file as png file
                b.compress(Bitmap.CompressFormat.JPEG,100, fOutStream);

                // Flush the FileOutputStream
                fOutStream.flush();

                //Close the FileOutputStream
                fOutStream.close();

                // Create a hashmap object to store image path and its position in the listview
                HashMap<String, Object> hmBitmap = new HashMap<String, Object>();

                // Storing the path to the temporary image file
                hmBitmap.put("img",tmpFile.getPath());

                // Storing the position of the image in the listview
                hmBitmap.put("position",position);

                // Returning the HashMap object containing the image path and position
                return hmBitmap;


            }catch (Exception e) {
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
            SimpleAdapter adapter = (SimpleAdapter ) mListView.getAdapter();

            // Getting the hashmap object at the specified position of the listview
            HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(position);

            // Overwriting the existing path in the adapter
            hm.put("img",path);

            // Noticing listview about the dataset changes
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
