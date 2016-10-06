package csi.fhict.org.ifindapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class HomePage extends AppCompatActivity {
    Button bZoeken;
    Button Bspecefiek;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);



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
                Integer afstand = 50;
                Integer minprijs = 0;
                Integer maxprijs = 500;
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
