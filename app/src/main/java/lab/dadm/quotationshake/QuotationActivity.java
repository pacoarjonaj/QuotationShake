package lab.dadm.quotationshake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class QuotationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation);

        // Asociar el nombre al TextView

        final ImageButton ibQuo = findViewById(R.id.imageButton);
        ibQuo.setOnClickListener(v -> {
            // Modificar Strings
        });
    }
}