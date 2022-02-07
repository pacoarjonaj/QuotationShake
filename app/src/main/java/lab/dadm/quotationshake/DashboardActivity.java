package lab.dadm.quotationshake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        final Button bGetQuo = findViewById(R.id.buttonGetQuo);
        bGetQuo.setOnClickListener(v -> {
            final Intent intent = new Intent(DashboardActivity.this, QuotationActivity.class);
            startActivity(intent);
        });

        final Button bFavQuo = findViewById(R.id.buttonFavQuo);
        bFavQuo.setOnClickListener(v -> {
            final Intent intent = new Intent(DashboardActivity.this, FavouriteActivity.class);
            startActivity(intent);
        });

        final Button bSett = findViewById(R.id.buttonSettings);
        bSett.setOnClickListener(v -> {
            final Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        final Button bAbout = findViewById(R.id.buttonAbout);
        bAbout.setOnClickListener(v -> {
            final Intent intent = new Intent(DashboardActivity.this, AboutActivity.class);
            startActivity(intent);
        });


    }
}