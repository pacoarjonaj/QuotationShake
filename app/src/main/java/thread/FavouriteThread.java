package thread;

import java.lang.ref.WeakReference;
import java.util.List;

import databases.QuotationDataBase;
import fragments.FavouriteFragment;
import model.Quotation;

public class FavouriteThread extends Thread{
    private final WeakReference<FavouriteFragment> reference;

    public FavouriteThread(FavouriteFragment favouriteFragment){
        super();
        this.reference = new WeakReference<>(favouriteFragment);
    }

    public void run(){
        FavouriteFragment fragment = reference.get();

        if(reference.get() != null){
            List<Quotation> quotations = QuotationDataBase.getInstance(fragment.getContext()).quotationDAO().getAllQuotations();

            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    reference.get().addFavList(quotations);
                }
            });
        }
    }
}
