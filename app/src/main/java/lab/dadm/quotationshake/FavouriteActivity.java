package lab.dadm.quotationshake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

public class FavouriteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);


        final Button bAuthor = findViewById(R.id.buttonAuthor);
        bAuthor.setOnClickListener(v -> {
            final Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://en.wikipedia.org/wiki/Albert_Einstein"));
            if (intent.resolveActivity(getPackageManager()) != null){
                startActivity(intent);
            }
        });
    }

}