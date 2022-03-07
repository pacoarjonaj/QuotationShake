package fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.drm.DrmStore;
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
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import adapter.QuotationList;
import databases.QuotationDataBase;
import kotlin.reflect.KFunction;
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



        // Recycler
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewFav);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);

        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(itemDecoration);

        List<Quotation> listQuotations = new ArrayList<>();
        adapter = new QuotationList(listQuotations,listener);
        isVisible = adapter.getItemCount() > 0;
        recyclerView.setAdapter(adapter);

        // ItemTouchHelper
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_IDLE,ItemTouchHelper.RIGHT) | makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE,ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

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

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        FragmentResultListener fragmentResultListener = (new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                adapter.clearAllElements();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        QuotationDataBase.getInstance(getContext()).quotationDAO().deleteAllQuote();
                    }
                }).start();

                isVisible = false;
                getActivity().invalidateOptionsMenu();
            }
        });

      getChildFragmentManager().setFragmentResultListener("remove_all",this,fragmentResultListener);
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
                DialogClassFragment dialogClassFragment = new DialogClassFragment();
                dialogClassFragment.show(getChildFragmentManager(),null);
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