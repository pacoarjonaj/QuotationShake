package lab.dadm.quotationshake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import model.Quotation;

public class QuotationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs.getString(getString(R.string.key), getString(R.string.noRegisteredUser));
        String hello = getString(R.string.hello,username);

        TextView aux = findViewById(R.id.textView6);
        aux.setText(hello);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.quoation_actionbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.itemAdd:
                return true;
            case R.id.itemRefresh:
                Quotation quotation = new Quotation("prueba","yo");
                final TextView textView = findViewById(R.id.textView6);
                textView.setText("Cita: " + quotation.getQuoteText() + " Autor: " + quotation.quoteAuthor);
                return true;
            default:
                return true;
        }
    }
}