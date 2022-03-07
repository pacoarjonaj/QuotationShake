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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import databases.QuotationDataBase;
import model.Quotation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import thread.QuotationThread;
import webservices.QuotationRetrofitInterface;

public class QuotationActivity extends AppCompatActivity {

    boolean addIsVisible = false;
    boolean refreshIsVisible = true;
    String hello,language,httpMethod;
    ProgressBar progressBar;
    public TextView tvAuthorName,tvQuotation;
    QuotationRetrofitInterface retrofitInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);

        // Variables para los textView
        tvQuotation = findViewById(R.id.tvQuot);
        tvAuthorName = findViewById(R.id.tvAuthor);
        progressBar = findViewById(R.id.progressBar);

        // Inicializacion Web Service con Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.forismatic.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(QuotationRetrofitInterface.class);


        if(savedInstanceState != null){
            tvQuotation.setText(savedInstanceState.getString("tvQuotation"));
            tvAuthorName.setText(savedInstanceState.getString("tvAuthor"));
            addIsVisible = savedInstanceState.getBoolean("visibleAdd");

        }else{  // Para mostrar el "Hello ... "
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String username = prefs.getString(getString(R.string.key), getString(R.string.noRegisteredUser));
            hello = getString(R.string.hello,username);
            tvQuotation.setText(hello);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.quoation_actionbar,menu);

        MenuItem add = menu.findItem(R.id.itemAdd);
        add.setVisible(addIsVisible);

        MenuItem refresh = menu.findItem(R.id.itemRefresh);
        refresh.setVisible(refreshIsVisible);

        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tvQuotation", String.valueOf(tvQuotation));
        outState.putString("tvAuthor", String.valueOf(tvAuthorName));
        outState.putBoolean("visibleAdd",addIsVisible);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.itemAdd:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Quotation quotationToSave = new Quotation(String.valueOf(tvQuotation.getText()), String.valueOf(tvAuthorName.getText()));
                        QuotationDataBase.getInstance(QuotationActivity.this).quotationDAO().insertQuote(quotationToSave);
                    }
                }).start();

                addIsVisible = false;
                invalidateOptionsMenu();

                return true;

            case R.id.itemRefresh:

                if(isConnected()){
                    // Mostramos la Progress Bar
                    showPrBar();

                    // Obtenemos los parametros de lenguaje y metodo HTTP
                    getParameters();

                    // Realizamos llamada al Web Service
                    Call<Quotation> call;
                    if(httpMethod == "get"){
                        call = retrofitInterface.getCurrentQuotation(language);
                    }else{
                        call = retrofitInterface.getCurrentQuotationPOST(language,"getQuote","json");
                    }

                    call.enqueue(new Callback<Quotation>() {
                        @Override
                        public void onResponse(Call<Quotation> call, Response<Quotation> response) {
                            displayQuotation(response.body());
                        }

                        @Override
                        public void onFailure(Call<Quotation> call, Throwable t) {
                            displayQuotation(null);
                        }
                    });
                }else{
                    Toast.makeText(QuotationActivity.this, R.string.connectionError,Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return true;
        }
    }

    private void showPrBar(){
        addIsVisible = false;
        refreshIsVisible = false;
        tvQuotation.setVisibility(View.INVISIBLE);
        tvAuthorName.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        invalidateOptionsMenu();
    }

    private void getParameters(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Usamos por defecto english y get
        language = prefs.getString(getString(R.string.chooseLanguage),getString(R.string.en));
        httpMethod = prefs.getString(getString(R.string.httpRequests),getString(R.string.get));
    }

    public void setAddVisibility(boolean isSaved){
        addIsVisible = isSaved;
        invalidateOptionsMenu();
    }

    public void displayQuotation(Quotation quotation){

        progressBar.setVisibility(View.INVISIBLE);
        refreshIsVisible = true;
        invalidateOptionsMenu();

        if(quotation != null){
            tvQuotation.setText(quotation.quoteText);
            tvAuthorName.setText(quotation.quoteAuthor);
            new QuotationThread(this).start();
            tvQuotation.setVisibility(View.VISIBLE);
            tvAuthorName.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(QuotationActivity.this, R.string.notFound, Toast.LENGTH_SHORT).show();
        }

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