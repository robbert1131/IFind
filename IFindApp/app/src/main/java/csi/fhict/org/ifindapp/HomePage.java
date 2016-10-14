package csi.fhict.org.ifindapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HomePage extends AppCompatActivity {
    Button bZoeken;
    Button Bspecefiek;
    TextView prijs;
    TextView aftstand;
    TextView desc;
    LinearLayout layout;
    LinearLayout layout1;
    RadioButton radiodesc;
    RadioButton radiodesc2;
    String artname = "";
    Integer count = 0;
    String R2 = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        prijs = (TextView) findViewById(R.id.tbPrijs);
        prijs.setVisibility(View.GONE);

        aftstand = (TextView) findViewById(R.id.tvAfstand);
        aftstand.setVisibility(View.GONE);

        desc = (TextView) findViewById(R.id.tvzoeken);
        desc.setVisibility(View.GONE);

        radiodesc2 = (RadioButton) findViewById(R.id.Rnee);
        radiodesc2.setVisibility(View.GONE);

        radiodesc = (RadioButton) findViewById(R.id.Rja);
        radiodesc.setVisibility(View.GONE);

        // Setup the new range seek bar
        final RangeSeekBar<Integer> rangeSeekBar = new RangeSeekBar<Integer>(this);
        final RangeSeekBar<Integer> RangeSeekBarafstand = new RangeSeekBar<Integer>(this);
        // Set the range
        rangeSeekBar.setRangeValues(0, 2000);
        rangeSeekBar.setSelectedMinValue(0);
        rangeSeekBar.setSelectedMaxValue(2000);

        RangeSeekBarafstand.setRangeValues(0, 301);
        RangeSeekBarafstand.setSelectedMinValue(0);
        RangeSeekBarafstand.setSelectedMaxValue(300);
        // Add to layout
        layout = (LinearLayout) findViewById(R.id.Rprijs);
        layout.addView(rangeSeekBar);

        layout1 = (LinearLayout) findViewById(R.id.RAfstand);
        layout1.addView(RangeSeekBarafstand);

        rangeSeekBar.setVisibility(View.GONE);
        RangeSeekBarafstand.setVisibility(View.GONE);

        bZoeken = (Button) findViewById(R.id.bZoeken);
        Bspecefiek = (Button) findViewById(R.id.Bspecefiek);
        bZoeken.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
             if (count == 0) {
                 EditText searchterm = (EditText) findViewById(R.id.etZoek);
                 String zoekterm = searchterm.getText().toString();
                 Intent i = new Intent(getApplicationContext(), ProductList.class);
                 i.putExtra("Title", zoekterm);
                 startActivity(i);
             }
                else {
                 EditText searchterm = (EditText) findViewById(R.id.etZoek);
                 String zoekterm = searchterm.getText().toString();
                 radiodesc = (RadioButton) findViewById(R.id.Rja);
                 boolean Desc = radiodesc.isChecked();
                 Integer afstand = 0;
                 Integer minprijs = rangeSeekBar.getSelectedMinValue();
                 Integer maxprijs = rangeSeekBar.getSelectedMaxValue();
                 Intent i = new Intent(getApplicationContext(), ProductList.class);
                 i.putExtra("Title", zoekterm);
                 i.putExtra("Desc", Desc);
                 i.putExtra("km", afstand);
                 i.putExtra("minprijs", minprijs);
                 i.putExtra("maxprijs", maxprijs);
                 startActivity(i);
             }
            }
        });

        Bspecefiek.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                count++;
                R2 = "";
                //if (count == 1){
                    desc.setVisibility(View.VISIBLE);
                    prijs.setVisibility(View.VISIBLE);
                    aftstand.setVisibility(View.VISIBLE);
                    rangeSeekBar.setVisibility(View.VISIBLE);
                    RangeSeekBarafstand.setVisibility(View.VISIBLE);
                    radiodesc.setVisibility(View.VISIBLE);
                    radiodesc2.setVisibility(View.VISIBLE);
                    TextView criteria = (TextView) findViewById(R.id.etZoek);
                    artname = (String) criteria.getText().toString();

                    new GetData().execute();
                    while(R2 == "")
                    {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    String[] separated = R2.split(",");
                    rangeSeekBar.setRangeValues(Integer.valueOf(separated[0]), Integer.valueOf(separated[1]) + 1);
                    rangeSeekBar.setSelectedMinValue(Integer.valueOf(separated[0]));
                    rangeSeekBar.setSelectedMaxValue(Integer.valueOf(separated[1]));

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                //}
                /*else {
                    EditText searchterm = (EditText) findViewById(R.id.etZoek);
                    String zoekterm = searchterm.getText().toString();
                    radiodesc = (RadioButton) findViewById(R.id.Rja);
                    boolean Desc = radiodesc.isChecked();
                    Integer afstand = 0;
                    Integer minprijs = rangeSeekBar.getSelectedMinValue();
                    Integer maxprijs = rangeSeekBar.getSelectedMaxValue();
                    Intent i = new Intent(getApplicationContext(), ProductList.class);
                    i.putExtra("Title", zoekterm);
                    i.putExtra("Desc", Desc);
                    i.putExtra("km", afstand);
                    i.putExtra("minprijs", minprijs);
                    i.putExtra("maxprijs", maxprijs);
                    startActivity(i);
                }*/
            }
        });
    }
    class GetData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            String result = "";
            try {
                String sUrl = "http://95.97.27.12/IFind/JSONpresearch.php?Name=";
                sUrl = sUrl + artname;
                URL url = new URL(sUrl);
                urlConnection = (HttpURLConnection) url.openConnection();

                int code = urlConnection.getResponseCode();

                if(code==200){
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    if (in != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                        String line = "";

                        while ((line = bufferedReader.readLine()) != null)
                            result += line;
                    }
                    in.close();
                }
                R2 = result;
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            finally {
                urlConnection.disconnect();
            }
            R2 = result;

            return R2;
        }

        @Override
        protected void onPostExecute(String result) {
            //yourTextView.setText(result);
            R2 = result;

            //super.onPostExecute(s);
        }
    }
}
