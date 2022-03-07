package thread;

import java.lang.ref.WeakReference;

import databases.QuotationDataBase;
import fragments.QuotationFragment;
import model.Quotation;

public class QuotationThread extends Thread{
    private final WeakReference<QuotationFragment> reference;

    public QuotationThread(QuotationFragment quotationActivity){
        super();
        this.reference = new WeakReference<>(quotationActivity);
    }

    @Override
    public void run() {
        QuotationFragment fragment = reference.get();

        if(fragment != null){
            Quotation quotation = QuotationDataBase.getInstance(fragment.getContext()).quotationDAO().getQuote(String.valueOf(fragment.tvQuotation.getText()));
            boolean isSaved = quotation != null;
            fragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragment.setAddVisibility(!isSaved);
                }
            });
        }
    }
}
