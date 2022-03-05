package thread;

import java.lang.ref.WeakReference;
import java.util.List;

import adapter.QuotationList;
import databases.QuotationDataBase;
import lab.dadm.quotationshake.FavouriteActivity;
import model.Quotation;

public class FavouriteThread extends Thread{
    private final WeakReference<FavouriteActivity> reference;

    public FavouriteThread(FavouriteActivity favouriteActivity){
        super();
        this.reference = new WeakReference<>(favouriteActivity);
    }

    public void run(){
        if(reference.get() != null){
            List<Quotation> quotations = QuotationDataBase.getInstance(reference.get()).quotationDAO().getAllQuotations();

            reference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    reference.get().addFavList(quotations);
                }
            });
        }
    }
}
