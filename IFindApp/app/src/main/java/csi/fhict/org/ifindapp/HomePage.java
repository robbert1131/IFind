package csi.fhict.org.ifindapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import org.florescu.android.rangeseekbar.RangeSeekBar;

public class HomePage extends AppCompatActivity {
    Button bZoeken;
    Button Bspecefiek;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Setup the new range seek bar
        final RangeSeekBar<Integer> rangeSeekBar = new RangeSeekBar<Integer>(this);
        RangeSeekBar<Integer> RangeSeekBarafstand = new RangeSeekBar<Integer>(this);
        // Set the range
        rangeSeekBar.setRangeValues(0, 2000);
        rangeSeekBar.setSelectedMinValue(0);
        rangeSeekBar.setSelectedMaxValue(2000);

        RangeSeekBarafstand.setRangeValues(0, 1000);
        RangeSeekBarafstand.setSelectedMinValue(0);
        RangeSeekBarafstand.setSelectedMaxValue(1000);
        // Add to layout
        LinearLayout layout = (LinearLayout) findViewById(R.id.Rprijs);
        layout.addView(rangeSeekBar);

        final LinearLayout layout1 = (LinearLayout) findViewById(R.id.RAfstand);
        layout1.addView(RangeSeekBarafstand);

        bZoeken = (Button) findViewById(R.id.bZoeken);
        Bspecefiek = (Button) findViewById(R.id.Bspecefiek);
        bZoeken.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText searchterm = (EditText) findViewById(R.id.etZoek);
                String zoekterm = searchterm.getText().toString();
                Intent i = new Intent(getApplicationContext(), ProductList.class);
                i.putExtra("Title", zoekterm);
                startActivity(i);
            }
        });

        Bspecefiek.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText searchterm = (EditText) findViewById(R.id.etZoek);
                String zoekterm = searchterm.getText().toString();
                RadioButton radiodesc = (RadioButton) findViewById(R.id.Rja);
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
        });
    }
}
