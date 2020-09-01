package com.ids.fixot.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.FavoritesActivity;
import com.ids.fixot.activities.StockDetailActivity;
import com.ids.fixot.activities.StockOrderBookActivity;
import com.ids.fixot.activities.TimeSalesActivity;
import com.ids.fixot.activities.TradesActivity;
import com.ids.fixot.model.StockQuotation;

import java.util.ArrayList;

/**
 * Created by user on 9/28/2017.
 */

public class FavoritesRecyclerAdapter extends RecyclerView.Adapter<FavoritesRecyclerAdapter.ItemViewHolder> implements Filterable {


    private ArrayList<StockQuotation> allStocks;
    private ArrayList<StockQuotation> filteredStocks;
    private ItemFilters itemFilters = new ItemFilters();
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;


    public FavoritesRecyclerAdapter(Activity context, ArrayList<StockQuotation> allStocks, RecyclerViewOnItemClickListener itemClickListener) {
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
        if(BuildConfig.Enable_Markets) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stock_quotation_item_new, viewGroup, false);
        }
        else {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stock_quotation_item_gig_new, viewGroup, false);
        }
       // v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stock_quotation_item, viewGroup, false);
        return new ItemViewHolder(v);


    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {

        final StockQuotation stockQuotation = filteredStocks.get(position);
        String stockID = stockQuotation.getSecurityId() + ""; // getStockId
        String stockChange = stockQuotation.getChange() + "";
        String stockAmount = stockQuotation.getLast() + "";

        String stockHigh="";
        String stockLow="";
        if(BuildConfig.Enable_Markets){
            stockHigh = stockQuotation.getBid() + "";
            stockLow = stockQuotation.getAsk() + "";
        }else {
            stockHigh = stockQuotation.getBidFormatted() + "";
            stockLow = stockQuotation.getAskFormatted() + "";
        }


        String session = MyApplication.lang == MyApplication.ENGLISH ? stockQuotation.getSessionNameEn() : stockQuotation.getSessionNameAr();



        holder.btTrades.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putParcelable("stockQuotation", stockQuotation);
            Intent i = new Intent(context, TradesActivity.class);
            i.putExtras(b);
            context.startActivity(i);
        });

        if (stockQuotation.getSessionId() == null)
            stockQuotation.setSessionId("");

        switch (stockQuotation.getSessionId()) {

            case MyApplication.CIRCUIT_BREAKER:
                holder.btTrades.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
                holder.btTrades.setTextColor(ContextCompat.getColor(context, R.color.white));
                break;

            default: //Trading

//                if (position % 2 == 1)
//                    holder.btTrades.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));
//                else
//                    holder.btTrades.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

        }
        Actions.overrideFonts(context, holder.rllayout, false);
        holder.btTrades.setTypeface(MyApplication.giloryBold);


        if(BuildConfig.Enable_Markets) {


            holder.tvStockId.setText(stockID);
            if (MyApplication.lang == MyApplication.ARABIC) {
                holder.tvStockSymbol.setText(stockQuotation.getSymbolAr());
                holder.tvStockName.setText(stockQuotation.getNameAr());

            } else {
                holder.tvStockSymbol.setText(stockQuotation.getSymbolEn());
                holder.tvStockName.setText(stockQuotation.getNameEn());
            }


            try {
                holder.tvChange.setText(Actions.formatNumber(Double.parseDouble(stockChange), Actions.OneDecimal));
            } catch (Exception e) {
                e.printStackTrace();
                Log.wtf("exc", "exc");
                holder.tvChange.setText(stockChange);
            }


            try{ holder.tvVolumeValue.setText(stockQuotation.getVolume()+"");}catch (Exception e){}
            try{ holder.tvVolumePercentage.setText(Actions.formatNumber(stockQuotation.getVolume(), Actions.OneDecimalThousandsSeparator) + "");}catch (Exception e){}

            try {
                holder.tvBidValue.setText(Actions.formatNumber(stockQuotation.getVolumeBid(), Actions.NoDecimalThousandsSeparator) + "");
            } catch (Exception e) {
                holder.tvSupplyQty.setText("");
            }
            try {
                holder.tvAskQuantityValue.setText(Actions.formatNumber(stockQuotation.getVolumeAsk(), Actions.NoDecimalThousandsSeparator) + "");
            } catch (Exception e) {
                holder.tvOrderQty.setText("");
            }

            holder.tvChange.setBackgroundColor(Actions.textColor(stockChange));
            holder.tvChange.setTextColor(Color.WHITE);
            holder.tvPrice.setText(stockAmount);
            holder.tvHigh.setText(stockHigh);
            holder.tvLow.setText(stockLow);
            holder.tvSession.setText(session);
            holder.tvStockId.setTypeface(MyApplication.giloryBold);
            holder.tvVolumeTitle.setTypeface(MyApplication.giloryBold);
            holder.tvBidQtyTitle.setTypeface(MyApplication.giloryBold);
            holder.tvVolumePercTitle.setTypeface(MyApplication.giloryBold);
            holder.tvAsKQtyTitle.setTypeface(MyApplication.giloryBold);



            holder.rllayout.setOnClickListener(v -> {
                Intent i = new Intent(context, StockDetailActivity.class);
                i.putExtra("stockID", stockQuotation.getStockID());
                context.startActivity(i);
            });

            holder.rllayout.setOnLongClickListener(view -> {
                showDialog(stockQuotation);
                return true;
            });

            //<editor-fold desc="layout coloring">


            //</editor-fold>

            Actions.setTypeface(new TextView[]{holder.tvStockId, holder.tvVolumeTitle, holder.tvBidQtyTitle, holder.tvVolumePercTitle, holder.tvAsKQtyTitle, holder.tvPrice, holder.tvChange}, MyApplication.giloryBold);
            Actions.setTypeface(new TextView[]{holder.tvStockSymbol, holder.tvSession},
                    MyApplication.lang == MyApplication.ENGLISH ? MyApplication.giloryBold : MyApplication.droidbold);


            if (MyApplication.expandedStock.contains(stockQuotation.getStockID())) {
                holder.linearExpendable.setVisibility(View.VISIBLE);
            } else {
                holder.linearExpendable.setVisibility(View.GONE);
            }

            holder.ivExpand.setOnClickListener(v -> {
                if (MyApplication.expandedStock.contains(stockQuotation.getStockID())) {
                    try {
                        MyApplication.expandedStock.remove(MyApplication.expandedStock.indexOf(stockQuotation.getStockID()));
                    } catch (Exception e) {
                    }
                    holder.linearExpendable.setVisibility(View.GONE);
                } else {
                    try {
                        MyApplication.expandedStock.add(stockQuotation.getStockID());
                    } catch (Exception e) {
                    }
                    holder.linearExpendable.setVisibility(View.VISIBLE);
                }
                notifyDataSetChanged();
                //  notifyItemChanged(position);
            });

        }
        else {

            if(MyApplication.STOCK_COLUMN_1==MyApplication.STOCK_TYPE_STOCK_NAME){


                if (MyApplication.lang == MyApplication.ARABIC) {
                    try{holder.tvColum1Value.setText(stockQuotation.getSymbolAr());}catch (Exception e){}
                } else {
                    try{holder.tvColum1Value.setText(stockQuotation.getSymbolEn());}catch (Exception e){}
                }
            }else {

                if (MyApplication.lang == MyApplication.ARABIC) {
                    try{holder.tvColum1Value.setText(stockQuotation.getNameAr());}catch (Exception e){}
                } else {
                    try{holder.tvColum1Value.setText(stockQuotation.getNameEn());}catch (Exception e){}
                }
            }


            if(MyApplication.STOCK_COLUMN_2==MyApplication.STOCK_TYPE_OFFER_PRICE){
                try{holder.tvColum2Value.setText(stockLow);}catch (Exception e){}
            }else {
                try{holder.tvColum2Value.setText(stockQuotation.getVolumeAsk()+"");}catch (Exception e){}
            }



            if(MyApplication.STOCK_COLUMN_3==MyApplication.STOCK_TYPE_CURRENT_PRICE){
                try{holder.tvColum3Value.setText(stockAmount);}catch (Exception e){}
            }else if(MyApplication.STOCK_COLUMN_3==MyApplication.STOCK_TYPE_LOW_PRICE){
                try{holder.tvColum3Value.setText(stockQuotation.getLowlimit()+"");}catch (Exception e){}
            }
            else {
                // if(MyApplication.STOCK_COLUMN_3==MyApplication.STOCK_TYPE_HIGH_PRICE){
                try{holder.tvColum3Value.setText(stockQuotation.getHigh()+"");}catch (Exception e){}
            }

          /*  else {
                try{holder.tvColum3Value.setText(stockQuotation.getPreviousClosing()+"");}catch (Exception e){}
            }*/


            if(MyApplication.STOCK_COLUMN_4==MyApplication.STOCK_TYPE_BID_PRICE){
                try{holder.tvColum4Value.setText(stockHigh);}catch (Exception e){}
            }else {
                try{holder.tvColum4Value.setText(stockQuotation.getVolumeBid()+"");}catch (Exception e){}
            }


            if(MyApplication.STOCK_COLUMN_5==MyApplication.STOCK_TYPE_CHANGE_PER){
                try{
                    if(stockQuotation.getChangePercent()!=null && !stockQuotation.getChangePercent().matches("null"))
                        holder.tvColum5Value.setText(stockQuotation.getChangePercent()+"%");
                    else
                        holder.tvColum5Value.setText("- %");
                }catch (Exception e){
                    holder.tvColum5Value.setText("- %");
                }
            }else {
                try{holder.tvColum5Value.setText(Actions.formatNumber(Double.parseDouble(stockChange), Actions.OneDecimal)+"");}catch (Exception e){}
            }

            try{
                holder.tvColum6Value.setText(stockQuotation.getTrade()+"");
            }catch (Exception e){}



            if(Double.parseDouble(stockChange)>0){
                holder.tvColum5Value.setTextColor(ContextCompat.getColor(context, R.color.green_color));
                holder.tvColum3Value.setTextColor(ContextCompat.getColor(context, R.color.green_color));
            }
            else if(Double.parseDouble(stockChange)< 0){
                holder.tvColum5Value.setTextColor(ContextCompat.getColor(context, R.color.red_color));
                holder.tvColum3Value.setTextColor(ContextCompat.getColor(context, R.color.red_color));
            }
            else {
                holder.tvColum5Value.setTextColor(ContextCompat.getColor(context, R.color.orange));
                holder.tvColum3Value.setTextColor(ContextCompat.getColor(context, R.color.orange));
            }

            Actions.overrideFonts(context, holder.rllayout, false);
            Actions.setTypeface(new TextView[]{holder.tvColum1Value, holder.tvColum2Value,holder.tvColum3Value,holder.tvColum4Value,holder.tvColum5Value,holder.tvColum6Value},
                    MyApplication.lang == MyApplication.ENGLISH ? MyApplication.giloryBold : MyApplication.droidbold);





            holder.rllayout.setOnClickListener(v -> {
                Intent i = new Intent(context, StockDetailActivity.class);
                i.putExtra("stockID", stockQuotation.getStockID());
                context.startActivity(i);
            });

            holder.rllayout.setOnLongClickListener(view -> {
                showDialog(stockQuotation);
                return true;
            });


        }







        //common

        if (stockQuotation.isChanged()) {
            Log.wtf("StockQuotationRecyclerAdapter", stockQuotation.getSymbolEn() + " isChanged");

            holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
            new CountDownTimer(2000, 50) {

                @Override
                public void onTick(long arg0) {
                }

                @Override
                public void onFinish() {

                    stockQuotation.setChanged(false);
//                    if (position % 2 == 1)
//                        holder.rllayout.setBackgroundColor( ContextCompat.getColor(context, R.color.colorLight));
//                    else
//                        holder.rllayout.setBackgroundColor( ContextCompat.getColor(context, R.color.white));
                    if (position % 2 == 0) {
                        holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
                    } else {
                        holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));
                    }
                }
            }.start();

        } else {

            if (position % 2 == 0) {
                holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
            } else {
                holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));
            }
        }


    }

    private void showDialog(StockQuotation stockQuotation) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final ArrayList<String> options = new ArrayList<>();
        options.add(context.getResources().getString(R.string.stock_details));
        options.add(context.getResources().getString(R.string.trades_title));
        options.add(context.getResources().getString(R.string.order_book));

        String[] items = {context.getResources().getString(R.string.stock_details), context.getResources().getString(R.string.trades_title), context.getResources().getString(R.string.order_book)
                , context.getResources().getString(R.string.buy), context.getResources().getString(R.string.sell)};

        builder.setItems(items, (dialog, which) -> {
            switch (which) {

                case 0: //stock details
                    Intent i = new Intent(context, StockDetailActivity.class);
                    i.putExtra("stockID", stockQuotation.getStockID());
                    context.startActivity(i);
                    break;

                case 1: // trades title

                    context.startActivity(new Intent(context, TimeSalesActivity.class)
                            .putExtra("stockId", stockQuotation.getStockID())
                            .putExtra("stockName", MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getNameEn()));
                    break;

                case 2: // order book

                    context.startActivity(new Intent(context, StockOrderBookActivity.class)
                            .putExtra("stockId", stockQuotation.getStockID())
                            .putExtra("last",stockQuotation.getLast())
                            .putExtra("high",stockQuotation.getHiLimit())
                            .putExtra("low",stockQuotation.getLowlimit())
                            .putExtra("volume",stockQuotation.getPreviousClosing())
                            .putExtra("Trades",stockQuotation.getTrade())
                            .putExtra("stockName", MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getNameEn()));
                    break;

                case 3: // buy

                    Bundle buyBundle = new Bundle();
                    buyBundle.putParcelable("stockQuotation", stockQuotation);
                    buyBundle.putInt("action", MyApplication.ORDER_BUY);
                    Intent buyIntent = new Intent(context, TradesActivity.class);
                    buyIntent.putExtras(buyBundle);
                    context.startActivity(buyIntent);
                    break;

                case 4: // sell

                    Bundle sellBundle = new Bundle();
                    sellBundle.putParcelable("stockQuotation", stockQuotation);
                    sellBundle.putInt("action", MyApplication.ORDER_SELL);
                    Intent sellIntent = new Intent(context, TradesActivity.class);
                    sellIntent.putExtras(sellBundle);
                    context.startActivity(sellIntent);
                    break;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return filteredStocks.size();
    }


    public interface RecyclerViewOnItemClickListener {

        void onItemFavClicked(View v, int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView tvStockId, tvStockSymbol, tvStockName, tvLow, tvPrice, tvHigh, tvChange, tvSession,tvVolumeValue,tvVolumePercentage,tvBidValue,tvAskQuantityValue;
        protected View v;
        Button btTrades;
        ImageView ivExpand;
        LinearLayout rllayout; // RelativeLayout // LinearLayout
        TextView tvIndexBoxValue,tvIndexBoxPercentage,tvSupplyQty,tvOrderQty;
        TextView tvColum1Value,tvColum2Value,tvColum3Value,tvColum4Value,tvColum5Value,tvColum6Value;
        TextView tvColumn1Title,tvColumn2Title,tvColumn3Title,tvColumn4Title,tvColumn5Title,tvColumn6Title;
        LinearLayout linearExpendable;
        protected TextView tvVolumeTitle,tvBidQtyTitle,tvVolumePercTitle,tvAsKQtyTitle;

        public ItemViewHolder(View v) {
            super(v);
            this.v = v;
            this.v.setOnClickListener(this);

            this.btTrades = v.findViewById(R.id.btTrades);
            this.tvSession = v.findViewById(R.id.tvSession);
            this.tvStockId = v.findViewById(R.id.tvStockId);
            this.tvStockSymbol = v.findViewById(R.id.tvStockSymbol);
            this.tvStockName = v.findViewById(R.id.tvStockName);
            this.tvLow = v.findViewById(R.id.tvLow);
            this.tvPrice = v.findViewById(R.id.tvPrice);
            this.tvHigh = v.findViewById(R.id.tvHigh);
            this.tvChange = v.findViewById(R.id.tvChange);
            this.rllayout = v.findViewById(R.id.rllayout);


            try{this.ivExpand = v.findViewById(R.id.ivExpand);}catch (Exception e){}
            try{this.tvVolumeValue = v.findViewById(R.id.tvVolumeValue);}catch (Exception e){}
            try{this.tvVolumePercentage = v.findViewById(R.id.tvVolumePercentage);}catch (Exception e){}
            try{this.tvBidValue = v.findViewById(R.id.tvBidValue);}catch (Exception e){}
            try{this.tvAskQuantityValue = v.findViewById(R.id.tvAskQuantityValue);}catch (Exception e){}
            try{this.linearExpendable = v.findViewById(R.id.linearExpendable);}catch (Exception e){}

            try{this.tvIndexBoxValue = v.findViewById(R.id.tvIndexBoxValue);}catch (Exception e){}
            try{this.tvIndexBoxPercentage = v.findViewById(R.id.tvIndexBoxPercentage);}catch (Exception e){}
            try{this.tvSupplyQty = v.findViewById(R.id.tvSupplyQty);}catch (Exception e){}
            try{this.tvOrderQty = v.findViewById(R.id.tvOrderQty);}catch (Exception e){}

            try{this.tvVolumeTitle = v.findViewById(R.id.tvVolumeTitle);}catch (Exception e){}
            try{this.tvBidQtyTitle = v.findViewById(R.id.tvBidQtyTitle);}catch (Exception e){}
            try{this.tvVolumePercTitle = v.findViewById(R.id.tvVolumePercTitle);}catch (Exception e){}
            try{this.tvAsKQtyTitle = v.findViewById(R.id.tvAsKQtyTitle);}catch (Exception e){}

            try{this.tvColum1Value = v.findViewById(R.id.tvColum1Value);}catch (Exception e){}
            try{this.tvColum2Value = v.findViewById(R.id.tvColum2Value);}catch (Exception e){}
            try{this.tvColum3Value = v.findViewById(R.id.tvColum3Value);}catch (Exception e){}
            try{this.tvColum4Value = v.findViewById(R.id.tvColum4Value);}catch (Exception e){}
            try{this.tvColum5Value = v.findViewById(R.id.tvColum5Value);}catch (Exception e){}
            try{this.tvColum6Value = v.findViewById(R.id.tvColum6Value);}catch (Exception e){}


            try{this.tvColumn1Title = v.findViewById(R.id.tvColum1Value);}catch (Exception e){}
            try{this.tvColumn2Title = v.findViewById(R.id.tvColum2Value);}catch (Exception e){}
            try{this.tvColumn3Title = v.findViewById(R.id.tvColum3Value);}catch (Exception e){}
            try{this.tvColumn4Title = v.findViewById(R.id.tvColum4Value);}catch (Exception e){}
            try{this.tvColumn5Title = v.findViewById(R.id.tvColum5Value);}catch (Exception e){}
            try{this.tvColumn6Title = v.findViewById(R.id.tvColum6Value);}catch (Exception e){}
            try{
                if(!BuildConfig.Enable_Markets){
                    tvSession.setVisibility(View.VISIBLE);
                }else {
                    tvSession.setVisibility(View.GONE);
                }}catch (Exception e){}
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemFavClicked(v, getLayoutPosition());
        }
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

                if (FavoritesActivity.instrumentId.length() > 0) {

                    if (list.get(i).getInstrumentId().equals(FavoritesActivity.instrumentId)) {

                        filterableString = list.get(i).getStockID() + list.get(i).getNameAr() + list.get(i).getNameEn()
                                + list.get(i).getSymbolAr() + list.get(i).getSymbolEn();

                        if (filterableString.toLowerCase().contains(filterString)) {
                            nlist.add(list.get(i));
                        }
                    }
                } else {

                    filterableString = list.get(i).getStockID() + list.get(i).getNameAr() + list.get(i).getNameEn()
                            + list.get(i).getSymbolAr() + list.get(i).getSymbolEn();

                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(list.get(i));
                    }
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
}
