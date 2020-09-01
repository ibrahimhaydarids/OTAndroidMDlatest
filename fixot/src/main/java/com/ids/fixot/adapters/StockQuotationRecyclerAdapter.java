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
import com.ids.fixot.activities.StockDetailActivity;
import com.ids.fixot.activities.StockOrderBookActivity;
import com.ids.fixot.activities.TimeSalesActivity;
import com.ids.fixot.activities.TradesActivity;
import com.ids.fixot.model.StockQuotation;

import java.util.ArrayList;

/**
 * Created by user on 3/29/2017.
 */

public class StockQuotationRecyclerAdapter extends RecyclerView.Adapter<StockQuotationRecyclerAdapter.ItemViewHolder> implements Filterable {


    private ArrayList<StockQuotation> allStocks;
    private ArrayList<StockQuotation> filteredStocks;
    private ItemFilters itemFilters = new ItemFilters();
    private Activity context;


    public StockQuotationRecyclerAdapter(Activity context, ArrayList<StockQuotation> allStocks) {

        this.context = context;
        this.allStocks = allStocks;
        this.filteredStocks = allStocks;
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
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stock_quotation_item_new, viewGroup, false);
    /*    if(BuildConfig.Enable_Markets)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stock_quotation_item_new, viewGroup, false);
        else
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stock_quotation_item_gig, viewGroup, false);
*/

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

        holder.tvStockId.setText(stockID);
        if (MyApplication.lang == MyApplication.ARABIC) {
            holder.tvStockSymbol.setText(stockQuotation.getSymbolAr());
            holder.tvStockName.setText(stockQuotation.getNameAr());

        } else {
            holder.tvStockSymbol.setText(stockQuotation.getSymbolEn());
            holder.tvStockName.setText(stockQuotation.getNameEn());
        }


        try {
           // if(BuildConfig.Enable_Markets)
               holder.tvChange.setText(Actions.formatNumber(Double.parseDouble(stockChange), Actions.OneDecimal));
           // else
             //  holder.tvChange.setText(stockChange);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("exc", "exc");
            holder.tvChange.setText(stockChange);
        }




            holder.tvVolumeValue.setText("0.0");
            holder.tvVolumePercentage.setText("0.0%");
            try{holder.tvBidValue.setText(stockQuotation.getBid()+"");}catch (Exception e){holder.tvSupplyQty.setText("");}
            try{holder.tvAskQuantityValue.setText(stockQuotation.getAsk()+"");}catch (Exception e){holder.tvOrderQty.setText("");}




/*        if(!BuildConfig.Enable_Markets){
            holder.tvIndexBoxValue.setText("0.0");
            holder.tvIndexBoxPercentage.setText("0.0%");
            try{holder.tvSupplyQty.setText(stockQuotation.getBid()+"");}catch (Exception e){holder.tvSupplyQty.setText("");}
            try{holder.tvOrderQty.setText(stockQuotation.getAsk()+"");}catch (Exception e){holder.tvOrderQty.setText("");}

        }*/


        /*if (Double.parseDouble(stockChange) == 0){

            holder.tvChange.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
        }else if (Double.parseDouble(stockChange) > 0){


            holder.tvChange.setBackgroundColor(ContextCompat.getColor(context, R.color.green_color));
        }else{

            holder.tvChange.setBackgroundColor(ContextCompat.getColor(context, R.color.red_color));
        }*/
       // if(BuildConfig.Enable_Markets) {
            holder.tvChange.setBackgroundColor(Actions.textColor(stockChange));
            holder.tvChange.setTextColor(Color.WHITE);
       // }
   /*     else {
            holder.tvChange.setTextColor(Actions.textColor(stockChange));
        }*/

        holder.tvPrice.setText(stockAmount);
        holder.tvHigh.setText(stockHigh);
        holder.tvLow.setText(stockLow);
        holder.tvSession.setText(session);

        holder.tvStockId.setTypeface(MyApplication.giloryBold);

        holder.tvVolumeTitle.setTypeface(MyApplication.giloryBold);
        holder.tvBidQtyTitle.setTypeface(MyApplication.giloryBold);
        holder.tvVolumePercTitle.setTypeface(MyApplication.giloryBold);
        holder.tvAsKQtyTitle.setTypeface(MyApplication.giloryBold);

        holder.btTrades.setOnClickListener(v -> {
            Bundle b = new Bundle();
            b.putParcelable("stockQuotation", stockQuotation);
            Intent i = new Intent(context, TradesActivity.class);
            i.putExtras(b);
            context.startActivity(i);
        });

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
//            if (position % 2 == 1)
//                holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));
//            else
//                holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

            if (position % 2 == 0) {
                holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
            } else {
                holder.rllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));
            }
        }

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
        //</editor-fold>

        Actions.overrideFonts(context, holder.rllayout, false);
        holder.btTrades.setTypeface(MyApplication.giloryBold);
        Actions.setTypeface(new TextView[]{holder.tvStockId,holder.tvVolumeTitle,holder.tvBidQtyTitle,holder.tvVolumePercTitle,holder.tvAsKQtyTitle, holder.tvPrice, holder.tvChange}, MyApplication.giloryBold);
        Actions.setTypeface(new TextView[]{holder.tvStockSymbol, holder.tvSession},
                MyApplication.lang == MyApplication.ENGLISH ? MyApplication.giloryBold : MyApplication.droidbold);



        if(MyApplication.expandedStock.contains(stockQuotation.getStockID())) {
            holder.linearExpendable.setVisibility(View.VISIBLE);
        }
        else {
            holder.linearExpendable.setVisibility(View.GONE);
        }

        holder.ivExpand.setOnClickListener(v->{
            if(MyApplication.expandedStock.contains(stockQuotation.getStockID())) {
                try{MyApplication.expandedStock.remove(MyApplication.expandedStock.indexOf(stockQuotation.getStockID()));}catch (Exception e){}
                holder.linearExpendable.setVisibility(View.GONE);
            }
            else {
                try{MyApplication.expandedStock.add(stockQuotation.getStockID());}catch (Exception e){}
                holder.linearExpendable.setVisibility(View.VISIBLE);
            }
            notifyDataSetChanged();
            //  notifyItemChanged(position);
        });



/*        if(stockQuotation.isExpanded()) {
            holder.linearExpendable.setVisibility(View.VISIBLE);
        }
        else {
            holder.linearExpendable.setVisibility(View.GONE);
        }

        holder.ivExpand.setOnClickListener(v->{
            if(stockQuotation.isExpanded()) {
                stockQuotation.setExpanded(false);
                holder.linearExpendable.setVisibility(View.GONE);
            }
            else {
                stockQuotation.setExpanded(true);
                holder.linearExpendable.setVisibility(View.VISIBLE);
            }
            notifyDataSetChanged();
          //  notifyItemChanged(position);
         });*/

//        holder.tvSession.setGravity(MyApplication.lang == MyApplication.ARABIC ? Gravity.LEFT : Gravity.RIGHT);
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
                            .putExtra("last", stockQuotation.getLast())
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

                if (MyApplication.instrumentId.length() > 0) {

                    if (list.get(i).getInstrumentId().equals(MyApplication.instrumentId)) {

                        filterableString = list.get(i).getSecurityId() /*+ list.get(i).getStockID()*/ + list.get(i).getNameAr() + list.get(i).getNameEn()
                                + list.get(i).getSymbolAr() + list.get(i).getSymbolEn();

                        if (filterableString.toLowerCase().contains(filterString)) {
                            Log.wtf("filter_string_1",filterableString);
                            nlist.add(list.get(i));
                        }
                    }
                } else {

                    filterableString = list.get(i).getSecurityId() /*+ list.get(i).getStockID()*/ + list.get(i).getNameAr() + list.get(i).getNameEn()
                            + list.get(i).getSymbolAr() + list.get(i).getSymbolEn();

                    if (filterableString.toLowerCase().contains(filterString)) {
                        Log.wtf("filter_string_2",filterableString);
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

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvStockId, tvStockSymbol, tvStockName, tvLow, tvPrice, tvHigh, tvChange, tvSession,tvVolumeValue,tvVolumePercentage,tvBidValue,tvAskQuantityValue;
        protected View v;
        Button btTrades;
        ImageView ivExpand;
        LinearLayout rllayout; // RelativeLayout // LinearLayout
        TextView tvIndexBoxValue,tvIndexBoxPercentage,tvSupplyQty,tvOrderQty;
        LinearLayout linearExpendable;
        protected TextView tvVolumeTitle,tvBidQtyTitle,tvVolumePercTitle,tvAsKQtyTitle;


        public ItemViewHolder(View v) {
            super(v);
            this.v = v;

            try{this.btTrades = v.findViewById(R.id.btTrades);}catch (Exception e){}
            try{this.tvStockId = v.findViewById(R.id.tvStockId);}catch (Exception e){}
            try{this.tvStockSymbol = v.findViewById(R.id.tvStockSymbol);}catch (Exception e){}
            try{this.tvStockName = v.findViewById(R.id.tvStockName);}catch (Exception e){}
            try{this.tvLow = v.findViewById(R.id.tvLow);}catch (Exception e){}
            try{this.tvPrice = v.findViewById(R.id.tvPrice);}catch (Exception e){}
            try{this.tvHigh = v.findViewById(R.id.tvHigh);}catch (Exception e){}
            try{this.tvChange = v.findViewById(R.id.tvChange);}catch (Exception e){}
            try{this.tvSession = v.findViewById(R.id.tvSession);}catch (Exception e){}
            try{this.rllayout = v.findViewById(R.id.rllayout);}catch (Exception e){}

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

            try{
            if(BuildConfig.Enable_Markets){
                tvSession.setVisibility(View.VISIBLE);
            }else {
                tvSession.setVisibility(View.GONE);
            }}catch (Exception e){}

        }
    }
}