package com.ids.fixot.adapters;



import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.StockQuotation;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class stockQuotationPopupNewAdapter extends RecyclerView.Adapter<stockQuotationPopupNewAdapter.ItemViewHolder> implements Filterable {


    private ArrayList<StockQuotation> allStocks;
    private ArrayList<StockQuotation> filteredStocks;
    private ItemFilters itemFilters = new ItemFilters();
    private Activity context;
    private stockQuotationPopupNewAdapter.RecyclerViewOnItemClickListener itemClickListener;

    public stockQuotationPopupNewAdapter(Activity context, ArrayList<StockQuotation> allStocks, stockQuotationPopupNewAdapter.RecyclerViewOnItemClickListener itemClickListener) {

        this.context = context;
        this.allStocks = allStocks;
        this.filteredStocks = allStocks;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public Filter getFilter() {
        return itemFilters;
    }

    public ArrayList<StockQuotation> getFilteredItems() {
        return filteredStocks;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;

        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_popup_stock, viewGroup, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        final StockQuotation stockQuotation = filteredStocks.get(position);
        String stockID = stockQuotation.getSecurityId() + ""; // getStockId
        String stockChange = stockQuotation.getChange() + "";
        String stockAmount = stockQuotation.getLast() + "";

        String stockHigh = stockQuotation.getBid() + "";
        String stockLow = stockQuotation.getAsk() + "";
        String session = MyApplication.lang == MyApplication.ENGLISH ? stockQuotation.getSessionNameEn() : stockQuotation.getSessionNameAr();

        Actions.overrideFonts(context, holder.rllayout, false);

        try{
            if (MyApplication.lang == MyApplication.ARABIC) {
                try{holder.tvStockId.setText(stockQuotation.getSymbolAr());}catch (Exception e){}
                holder.tvStockSymbol.setText(stockQuotation.getNameAr());
                holder.tvStockName.setText(stockQuotation.getNameAr());
                holder.tvSectionTitle.setText(stockQuotation.getNameAr());

            } else {
                try{holder.tvStockId.setText(stockQuotation.getSymbolEn());}catch (Exception e){}
                holder.tvStockSymbol.setText(stockQuotation.getNameEn());
                holder.tvStockName.setText(stockQuotation.getNameEn());
                holder.tvSectionTitle.setText(stockQuotation.getNameEn());
            }}catch (Exception e){}


        if(stockQuotation.getStockID()==-1){
            holder.tvSectionTitle.setVisibility(View.VISIBLE);
        }else {
            holder.tvSectionTitle.setVisibility(View.GONE);
        }


        holder.tvStockId.setTypeface(MyApplication.giloryBold);
        if(MyApplication.selectedStockId==stockQuotation.getStockID()){
            holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.gray_selected_light : R.color.bg_gray_selected_dark));


            if( MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true)) {
                holder.tvStockId.setTextColor(context.getResources().getColor(R.color.colorValues));

                holder.tvStockSymbol.setTextColor(context.getResources().getColor(R.color.colorValues));
                holder.tvStockName.setTextColor(context.getResources().getColor(R.color.colorValues));

            }else {
                holder.tvStockId.setTextColor(context.getResources().getColor(R.color.text_selected_dark));

                holder.tvStockSymbol.setTextColor(context.getResources().getColor(R.color.text_selected_dark));
                holder.tvStockName.setTextColor(context.getResources().getColor(R.color.text_selected_dark));

            }

                Actions.setTypeface(new TextView[]{holder.tvStockSymbol,holder.tvSectionTitle},
                          MyApplication.lang == MyApplication.ENGLISH ? MyApplication.giloryBold : MyApplication.droidbold);



        }else {
            holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
            if( MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true)) {
                holder.tvStockId.setTextColor(context.getResources().getColor(R.color.colorValues));
                holder.tvStockSymbol.setTextColor(context.getResources().getColor(R.color.colorValues));
                holder.tvStockName.setTextColor(context.getResources().getColor(R.color.colorValues));
            }
            else {
                holder.tvStockId.setTextColor(context.getResources().getColor(R.color.colorValuesInv));
                holder.tvStockSymbol.setTextColor(context.getResources().getColor(R.color.colorValuesInv));
                holder.tvStockName.setTextColor(context.getResources().getColor(R.color.colorValuesInv));
            }

            Actions.setTypeface(new TextView[]{holder.tvStockId,holder.tvStockSymbol}, MyApplication.droidregular);
            Actions.setTypeface(new TextView[]{holder.tvSectionTitle},
                    MyApplication.lang == MyApplication.ENGLISH ? MyApplication.giloryBold : MyApplication.droidbold);


        }
     //  if( MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true))
            holder.tvSectionTitle.setTextColor(context.getResources().getColor(R.color.blue_text_color));
     //  else
         //  holder.tvSectionTitle.setTextColor(context.getResources().getColor(R.color.colorValuesInv));

      //  if (position % 2 == 0) {
          //  holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
      //  } else {
      //      holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));
    //    }
        Actions.setTypeface(new TextView[]{holder.tvSectionTitle},
                MyApplication.lang == MyApplication.ENGLISH ? MyApplication.giloryBold : MyApplication.droidbold);




    }


    @Override
    public int getItemCount() {
        return filteredStocks.size();
    }

    private class ItemFilters extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<StockQuotation> list = allStocks;

            int count = list.size();
            final ArrayList<StockQuotation> nlist = new ArrayList<>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {





                filterableString = list.get(i).getSecurityId() + list.get(i).getStockID() + list.get(i).getNameAr() + list.get(i).getNameEn()
                        + list.get(i).getSymbolAr() + list.get(i).getSymbolEn();

                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }


            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredStocks = (ArrayList<StockQuotation>) results.values;
            notifyDataSetChanged();
        }

    }

    public interface RecyclerViewOnItemClickListener {

        void onItemClicked(View v, int position);

    }

    public class ItemViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        protected TextView tvStockId, tvStockSymbol, tvStockName;
        protected View v;
        LinearLayout rllayout,linearSectionValues; // RelativeLayout // LinearLayout
        TextView tvSectionTitle;

        public ItemViewHolder(View v) {
            super(v);
            this.v = v;
            // this.v.setOnClickListener(this);

            this.tvStockId = v.findViewById(R.id.tvStockId);
            this.tvStockId.setVisibility(View.GONE);
            this.tvStockSymbol = v.findViewById(R.id.tvStockSymbol);
            this.tvStockName = v.findViewById(R.id.tvStockName);

            this.tvSectionTitle = v.findViewById(R.id.tvSectionTitle);

            this.rllayout = v.findViewById(R.id.rllayout);
            this.linearSectionValues = v.findViewById(R.id.linearSectionValues);

            this.rllayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }
}