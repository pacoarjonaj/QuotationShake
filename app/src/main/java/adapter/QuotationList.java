package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lab.dadm.quotationshake.FavouriteActivity;
import lab.dadm.quotationshake.R;
import model.Quotation;

public class QuotationList extends RecyclerView.Adapter<QuotationList.ViewHolder> {
    private List<Quotation> listQuotation;
    private OnItemClickListener item;
    private OnItemLongClickListener itemLongClickListener;
    
    public QuotationList(List<Quotation> list, OnItemClickListener itemClickListener, OnItemLongClickListener itemLongClickList){
        listQuotation = list;
        item = itemClickListener;
        itemLongClickListener = itemLongClickList;
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

    public void removeListElement(int pos){
        listQuotation.remove(pos);
        notifyItemRemoved(pos);
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

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    itemLongClickListener.onItemLongClick(getAdapterPosition());
                    return true;
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
