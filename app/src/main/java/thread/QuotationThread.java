package thread;

import java.lang.ref.WeakReference;

import databases.QuotationDataBase;
import lab.dadm.quotationshake.QuotationActivity;
import model.Quotation;

public class QuotationThread extends Thread{
    private final WeakReference<QuotationActivity> reference;

    public QuotationThread(QuotationActivity quotationActivity){
        super();
        this.reference = new WeakReference<>(quotationActivity);
    }

    @Override
    public void run() {
        QuotationActivity activity = reference.get();

        if(activity != null){
            Quotation quotation = QuotationDataBase.getInstance(activity).quotationDAO().getQuote(String.valueOf(activity.tvQuotation.getText()));
            boolean isSaved = quotation != null;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.setAddVisibility(!isSaved);
                }
            });
        }
    }
}
