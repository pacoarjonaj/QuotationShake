package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lab.dadm.quotationshake.R;
import model.Quotation;

public class QuotationList extends RecyclerView.Adapter<QuotationList.ViewHolder> {
    private List<Quotation> listQuotation;
    private OnItemClickListener item;
    
    public QuotationList(List<Quotation> list, OnItemClickListener itemClickListener){
        listQuotation = list;
        item = itemClickListener;
    }

    @NonNull
    @Override
    public QuotationList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quotation_item, parent, false);
        QuotationList.ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuotationList.ViewHolder holder, int position) {
        holder.author.setText(listQuotation.get(position).getQuoteAuthor());
        holder.quote.setText(listQuotation.get(position).getQuoteText());
    }

    @Override
    public int getItemCount() {
        return listQuotation.size();
    }

    public void addFavouriteList(List<Quotation> list){
        listQuotation.clear();
        listQuotation.addAll(list);
        notifyDataSetChanged();
    }

    public void removeListElement(int pos){
        listQuotation.remove(pos);
        notifyItemRemoved(pos);
    }

    public Quotation getQuoation(int pos){
        return listQuotation.get(pos);
    }

    public void clearAllElements(){
        listQuotation.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView quote;
        public TextView author;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    item.onItemClick(listQuotation.get(getAdapterPosition()));
                }
            });

            quote = itemView.findViewById(R.id.textQuote);
            author = itemView.findViewById(R.id.textAuthor);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Quotation quotation);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
}
