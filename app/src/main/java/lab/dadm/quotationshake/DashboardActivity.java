package lab.dadm.quotationshake;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import fragments.AboutFragment;
import fragments.FavouriteFragment;
import fragments.QuotationFragment;
import fragments.SettingsFragment;

public class DashboardActivity extends AppCompatActivity {

    Class<?extends Fragment> fragmentClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_dashboard);

        final Button bGetQuo = findViewById(R.id.buttonGetQuo);
        bGetQuo.setOnClickListener(v -> {
            getSupportActionBar().setTitle(getString(R.string.getQuotations));
            fragmentClass = QuotationFragment.class;
            showFragment();
        });

        final Button bFavQuo = findViewById(R.id.buttonFavQuo);
        bFavQuo.setOnClickListener(v -> {
            getSupportActionBar().setTitle(getString(R.string.favQuotations));
            fragmentClass = FavouriteFragment.class;
            showFragment();
        });

        final Button bSett = findViewById(R.id.buttonSettings);
        bSett.setOnClickListener(v -> {
            getSupportActionBar().setTitle(getString(R.string.settings));
            fragmentClass = SettingsFragment.class;
            showFragment();
        });

        final Button bAbout = findViewById(R.id.buttonAbout);
        bAbout.setOnClickListener(v -> {
            getSupportActionBar().setTitle(getString(R.string.about));
            fragmentClass = AboutFragment.class;
            showFragment();
        });

    }

    private void showFragment(){
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).replace(R.id.idGeneralFragment,fragmentClass,null).commit();
    }
}