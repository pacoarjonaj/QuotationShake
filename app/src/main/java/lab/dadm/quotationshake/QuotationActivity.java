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

import databases.QuotationDataBase;
import model.Quotation;

public class QuotationActivity extends AppCompatActivity {

    int numQuotations = 0;
    boolean addIsVisbile = true;
    String hello;
    String quotationPhrase;
    Quotation quotationFake = new Quotation("prueba","yo");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);

        if(savedInstanceState != null){
            hello = savedInstanceState.getString(getString(R.string.key));
            quotationPhrase = savedInstanceState.getString("phraseKey");
            numQuotations = savedInstanceState.getInt("numQuotes");
            addIsVisbile = savedInstanceState.getBoolean("visibleAdd");
        }else{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String username = prefs.getString(getString(R.string.key), getString(R.string.noRegisteredUser));
            hello = getString(R.string.hello,username);
        }

        TextView aux = findViewById(R.id.textView6);
        aux.setText(hello);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.quoation_actionbar,menu);

        MenuItem item = menu.findItem(R.id.itemAdd);
        item.setVisible(addIsVisbile);
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.key),hello);
        outState.putString("phraseKey",quotationPhrase);
        outState.putInt("numQuotes",numQuotations);
        outState.putBoolean("visibleAdd",addIsVisbile);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.itemAdd:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        QuotationDataBase.getInstance(QuotationActivity.this).quotationDAO().insertQuote(quotationFake);
                    }
                }).start();

                addIsVisbile = false;
                invalidateOptionsMenu();

                return true;
            case R.id.itemRefresh:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Quotation aux = QuotationDataBase.getInstance(QuotationActivity.this).quotationDAO().getQuote(quotationFake.getQuoteText());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(aux == null){
                                    addIsVisbile = true;
                                }else addIsVisbile = false;
                                invalidateOptionsMenu();
                            }
                        });
                    }
                }).start();

                final TextView textView = findViewById(R.id.textView6);
                quotationPhrase = "Cita " + numQuotations + ": " + quotationFake.getQuoteText() + " Autor " + numQuotations + ": " + quotationFake.quoteAuthor;
                textView.setText(quotationPhrase);

                numQuotations++;

                return true;
            default:
                return true;
        }
    }
}