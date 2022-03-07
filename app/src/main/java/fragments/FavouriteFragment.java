package fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import adapter.QuotationList;
import databases.QuotationDataBase;
import lab.dadm.quotationshake.R;
import model.Quotation;
import thread.FavouriteThread;

public class FavouriteFragment extends Fragment {

    QuotationList adapter;
    boolean isVisible;

    public FavouriteFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite,null);

        QuotationList.OnItemClickListener listener = new QuotationList.OnItemClickListener() {
            @Override
            public void onItemClick(Quotation quotation) {
                if(quotation.getQuoteAuthor() == ""){
                    Toast.makeText(getContext(), R.string.noName, Toast.LENGTH_SHORT).show();
                }else{
                    final Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://en.wikipedia.org/wiki/" + quotation.getQuoteAuthor()));
                    if (intent.resolveActivity(getContext().getPackageManager()) != null){
                        startActivity(intent);
                    }
                }
            }
        };

        // Dialog
        QuotationList.OnItemLongClickListener longListener = new QuotationList.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure that you want to delete this quotation?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                QuotationDataBase.getInstance(getContext()).quotationDAO().deleteQuote(adapter.getQuoation(position));

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.removeListElement(position);
                                        if(adapter.getItemCount() == 0){
                                            isVisible = false;
                                        }else isVisible = true;

                                        getActivity().invalidateOptionsMenu();
                                    }
                                });

                            }
                        }).start();
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
        RecyclerView recycler = view.findViewById(R.id.recyclerViewFav);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);

        recycler.setLayoutManager(manager);
        recycler.addItemDecoration(itemDecoration);

        List<Quotation> listQuotations = new ArrayList<>();
        adapter = new QuotationList(listQuotations,listener,longListener);
        isVisible = adapter.getItemCount() > 0;
        recycler.setAdapter(adapter);


        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.favourite_actionbar,menu);

        MenuItem item = menu.findItem(R.id.itemClearAll);
        item.setVisible(isVisible);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.itemClearAll:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        QuotationDataBase.getInstance(getContext()).quotationDAO().deleteAllQuote();
                    }
                }).start();

                adapter.clearAllElements();
                isVisible = false;
                getActivity().invalidateOptionsMenu();

                return true;
            default:
                return true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FavouriteThread thread = new FavouriteThread(this);
        thread.start();
    }

    public void addFavList(List<Quotation> list){
        adapter.addFavouriteList(list);

        if(list.size() > 0){
            isVisible = true;
            getActivity().invalidateOptionsMenu();
        }

    }
}