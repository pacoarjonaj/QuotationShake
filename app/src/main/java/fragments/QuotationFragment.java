package fragments;

import static android.content.Context.CONNECTIVITY_SERVICE;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import databases.QuotationDataBase;
import lab.dadm.quotationshake.R;
import model.Quotation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import thread.QuotationThread;
import webservices.QuotationRetrofitInterface;

public class QuotationFragment extends Fragment {

    boolean refreshIsVisible = true;
    String hello,language,httpMethod;
    ProgressBar progressBar;
    public TextView tvAuthorName,tvQuotation;
    QuotationRetrofitInterface retrofitInterface;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton floatingActionButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quotation,null);

        // Variables para los textView
        tvQuotation = view.findViewById(R.id.tvQuot);
        tvAuthorName = view.findViewById(R.id.tvAuthor);

        // Inicializacion Web Service con Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.forismatic.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = retrofit.create(QuotationRetrofitInterface.class);

        // Referencia al SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.srLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getQuotationSwipeListener();
            }
        });

        // Referencia Floating button
        floatingActionButton = view.findViewById(R.id.floatingActionBn);
        floatingActionButton.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Quotation quotationToSave = new Quotation(String.valueOf(tvQuotation.getText()), String.valueOf(tvAuthorName.getText()));
                        QuotationDataBase.getInstance(getContext()).quotationDAO().insertQuote(quotationToSave);
                    }
                }).start();

                floatingActionButton.setVisibility(View.INVISIBLE);
                tvAuthorName.setVisibility(View.INVISIBLE);
                getActivity().invalidateOptionsMenu();
            }
        });


        if(savedInstanceState != null){
            tvQuotation.setText(savedInstanceState.getString("tvQuotation"));
            tvAuthorName.setText(savedInstanceState.getString("tvAuthor"));
        }else{  // Para mostrar el "Hello ... "
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            String username = prefs.getString(getString(R.string.key), getString(R.string.noRegisteredUser));
            hello = getString(R.string.hello,username);
            tvQuotation.setText(hello);
            floatingActionButton.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public QuotationFragment(){

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.quoation_actionbar,menu);

        MenuItem refresh = menu.findItem(R.id.itemRefresh);
        refresh.setVisible(refreshIsVisible);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tvQuotation", String.valueOf(tvQuotation));
        outState.putString("tvAuthor", String.valueOf(tvAuthorName));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.itemRefresh:
                getQuotationSwipeListener();
                return true;

            default:
                return true;
        }
    }

    private void getQuotationSwipeListener(){

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
            Toast.makeText(getContext(), R.string.connectionError,Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            tvAuthorName.setVisibility(View.INVISIBLE);
        }

    }

    private void showPrBar(){
        floatingActionButton.setVisibility(View.INVISIBLE);
        refreshIsVisible = false;
        tvQuotation.setVisibility(View.INVISIBLE);
        tvAuthorName.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setRefreshing(true);
        getActivity().invalidateOptionsMenu();
    }

    private void getParameters(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Usamos por defecto english y get
        language = prefs.getString(getString(R.string.chooseLanguage),getString(R.string.en));
        httpMethod = prefs.getString(getString(R.string.httpRequests),getString(R.string.get));
    }

    public void setAddVisibility(boolean isSaved){

        if(isSaved){
            floatingActionButton.setVisibility(View.VISIBLE);
        }else floatingActionButton.setVisibility(View.INVISIBLE);

    }

    public void displayQuotation(Quotation quotation){

        swipeRefreshLayout.setRefreshing(false);
        refreshIsVisible = true;
        getActivity().invalidateOptionsMenu();

        if(quotation != null){
            tvQuotation.setText(quotation.quoteText);
            tvAuthorName.setText(quotation.quoteAuthor);
            new QuotationThread(this).start();
            tvQuotation.setVisibility(View.VISIBLE);
            tvAuthorName.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(getContext(), R.string.notFound, Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isConnected(){
        boolean result = false;

        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);

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