package lab.dadm.quotationshake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import databases.QuotationDataBase;
import model.Quotation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import webservices.QuotationRetrofitInterface;

public class QuotationActivity extends AppCompatActivity {

    boolean addIsVisible;
    boolean refreshIsVisible;
    boolean progressbarIsVisible;
    String hello;
    String quotationPhrase;
    Quotation quotation;
    QuotationRetrofitInterface retrofitInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);

        if(savedInstanceState != null){
            hello = savedInstanceState.getString(getString(R.string.key));
            quotationPhrase = savedInstanceState.getString("phraseKey");
            addIsVisible = savedInstanceState.getBoolean("visibleAdd");
            refreshIsVisible = savedInstanceState.getBoolean("visibleRefresh");
            progressbarIsVisible = savedInstanceState.getBoolean("visibleProgressBar");
        }else{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String username = prefs.getString(getString(R.string.key), getString(R.string.noRegisteredUser));
            hello = getString(R.string.hello,username);
        }

        TextView aux = findViewById(R.id.textView6);
        aux.setText(hello);
        hideAllOptions();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.forismatic.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(QuotationRetrofitInterface.class);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.quoation_actionbar,menu);

        MenuItem add = menu.findItem(R.id.itemAdd);
        add.setVisible(addIsVisible);

        MenuItem refresh = menu.findItem(R.id.itemRefresh);
        refresh.setVisible(refreshIsVisible);

        MenuItem progressBar = menu.findItem(R.id.progressBar);
        progressBar.setVisible(refreshIsVisible);

        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.key),hello);
        outState.putString("phraseKey",quotationPhrase);
        outState.putBoolean("visibleAdd",addIsVisible);
        outState.putBoolean("visibleRefresh",refreshIsVisible);
        outState.putBoolean("visibleProgressBar",progressbarIsVisible);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.itemAdd:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        QuotationDataBase.getInstance(QuotationActivity.this).quotationDAO().insertQuote(quotation);
                    }
                }).start();

                addIsVisible = false;
                invalidateOptionsMenu();

                return true;
            case R.id.itemRefresh:

                if(isConnected()){
                    Call<Quotation> call = retrofitInterface.getCurrentQuotation("english");

                    call.enqueue(new Callback<Quotation>() {
                        @Override
                        public void onResponse(Call<Quotation> call, Response<Quotation> response) {
                            displayQuotation(response.body());
                        }

                        @Override
                        public void onFailure(Call<Quotation> call, Throwable t) {
                            Toast.makeText(QuotationActivity.this, R.string.notFound,Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(QuotationActivity.this, R.string.connectionError,Toast.LENGTH_SHORT).show();
                }

                final TextView textView = findViewById(R.id.textView6);
                quotationPhrase = "Cita " + ": " + quotation.getQuoteText() + " Autor " + ": " + quotation.quoteAuthor;
                textView.setText(quotationPhrase);

                return true;
            default:
                return true;
        }
    }

    public void displayQuotation(Quotation quot){
        quotation.setQuoteText(quot.quoteText);
        quotation.setQuoteAuthor(quot.quoteAuthor);

        if(quot != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Quotation aux = QuotationDataBase.getInstance(QuotationActivity.this).quotationDAO().getQuote(quotation.getQuoteText());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(aux == null){
                                addIsVisible = true;
                            }else addIsVisible = false;

                            progressbarIsVisible = false;
                            invalidateOptionsMenu();
                        }
                    });
                }
            }).start();
        }else{
            Toast.makeText(QuotationActivity.this, R.string.notFound, Toast.LENGTH_SHORT).show();
        }
    }

    public void hideAllOptions(){
        addIsVisible = false;
        refreshIsVisible = false;
        progressbarIsVisible = true;
        invalidateOptionsMenu();
    }

    public boolean isConnected(){
        boolean result = false;

        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        if(Build.VERSION.SDK_INT > 22){
            final Network activeNetwork = manager.getActiveNetwork();

            if(activeNetwork != null){
                final NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(activeNetwork);
                result = networkCapabilities != null && (
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
            }
        }else{
            NetworkInfo info = manager.getActiveNetworkInfo();
            result = ((info != null) && (info.isConnected()));
        }

        return result;
    }
}