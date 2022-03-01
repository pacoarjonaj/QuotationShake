package lab.dadm.quotationshake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapter.QuotationList;
import databases.QuotationDataBase;
import model.Quotation;

public class FavouriteActivity extends AppCompatActivity {

    QuotationList adapter;
    boolean isVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        QuotationList.OnItemClickListener listener = new QuotationList.OnItemClickListener() {
            @Override
            public void onItemClick(Quotation quotation) {
                if(quotation.getQuoteAuthor() == ""){
                    Toast.makeText(FavouriteActivity.this, R.string.noName, Toast.LENGTH_SHORT).show();
                }else{
                    final Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://en.wikipedia.org/wiki/" + quotation.getQuoteAuthor()));
                    if (intent.resolveActivity(getPackageManager()) != null){
                        startActivity(intent);
                    }
                }
            }
        };

        // Dialog
        QuotationList.OnItemLongClickListener longListener = new QuotationList.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FavouriteActivity.this);
                builder.setMessage("Are you sure that you want to delete this quotation?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                       new Thread(new Runnable() {
                           @Override
                           public void run() {
                               QuotationDataBase.getInstance(FavouriteActivity.this).quotationDAO().deleteQuote(adapter.getQuoation(position));
                           }
                       }).start();

                        adapter.removeListElement(position);
                        if(adapter.getItemCount() == 0){
                            isVisible = false;
                            invalidateOptionsMenu();
                        }else isVisible = true;
                    }
                });
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.create().show();
            }
        };


        // Recycler
        RecyclerView recycler = findViewById(R.id.recyclerViewFav);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);

        recycler.setLayoutManager(manager);
        recycler.addItemDecoration(itemDecoration);

        List<Quotation> listQuotations = QuotationDataBase.getInstance(this).quotationDAO().getAllQuotations(); // Obtenemos desde la bbdd
        adapter = new QuotationList(listQuotations,listener,longListener);
        isVisible = adapter.getItemCount() > 0;
        recycler.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.favourite_actionbar,menu);

        MenuItem item = menu.findItem(R.id.itemClearAll);
        item.setVisible(isVisible);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.itemClearAll:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        QuotationDataBase.getInstance(FavouriteActivity.this).quotationDAO().deleteAllQuote();
                    }
                }).start();

                adapter.clearAllElements();
                isVisible = false;
                invalidateOptionsMenu();

                return true;
            default:
                return true;
        }
    }
}