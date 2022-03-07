package lab.dadm.quotationshake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

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

        final BottomNavigationView bnView = findViewById(R.id.btnView);
        bnView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return itemSelected(item.getItemId());
            }
        });

        if(savedInstanceState == null){
            getSupportActionBar().setTitle(getString(R.string.getQuotations));
            fragmentClass = QuotationFragment.class;
            showFragment();
        }

    }

    private boolean itemSelected(int itemId){
        switch (itemId){
            case R.id.itemNavGet:
                getSupportActionBar().setTitle(getString(R.string.getQuotations));
                fragmentClass = QuotationFragment.class;
                break;
            case R.id.itemNavFav:
                getSupportActionBar().setTitle(getString(R.string.favQuotations));
                fragmentClass = FavouriteFragment.class;
                break;
            case R.id.itemNavSet:
                getSupportActionBar().setTitle(getString(R.string.settings));
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.itemNavAbout:
                getSupportActionBar().setTitle(getString(R.string.about));
                fragmentClass = AboutFragment.class;
                break;
        }
        showFragment();

        return true;
    }


    private void showFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.idGeneralFragment,fragmentClass,null)
                .commit();
    }
}