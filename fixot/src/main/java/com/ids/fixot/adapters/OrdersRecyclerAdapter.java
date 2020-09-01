package com.ids.fixot.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ids.fixot.Actions;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.OrderDetailsActivity;
import com.ids.fixot.activities.StockDetailActivity;
import com.ids.fixot.activities.StockOrderBookActivity;
import com.ids.fixot.activities.TimeSalesActivity;
import com.ids.fixot.activities.TradeConfirmationActivity;
import com.ids.fixot.activities.TradesActivity;
import com.ids.fixot.enums.enums;
import com.ids.fixot.model.OnlineOrder;
import com.ids.fixot.model.StockQuotation;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;


public class OrdersRecyclerAdapter extends RecyclerView.Adapter<OrdersRecyclerAdapter.ViewHolder>{


    private ArrayList<OnlineOrder> allOrders;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;
    private RefreshInterface refreshInterface;

    public OrdersRecyclerAdapter(Activity context, ArrayList<OnlineOrder> allOrders, RecyclerViewOnItemClickListener itemClickListener, RefreshInterface refreshInterface) {
        this.context = context;
        this.allOrders = allOrders;
        this.itemClickListener = itemClickListener;
        this.refreshInterface = refreshInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orders_item, viewGroup, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        final OnlineOrder order = allOrders.get(position);

        try {
            if(BuildConfig.Enable_Markets){
            holder.tvInstrumentValue.setText(MyApplication.lang == MyApplication.ENGLISH ?
                    MyApplication.instrumentsHashmap.get(order.getInstrumentID()).getInstrumentNameEn() :
                    MyApplication.instrumentsHashmap.get(order.getInstrumentID()).getInstrumentNameAr());
            }else {
                holder.tvInstrumentValue.setText(MyApplication.lang == MyApplication.ENGLISH ?
                        order.getStockName():
                        order.getStockName());
            }
        }catch (Exception e){}

        holder.btSymbolItem.setText(BuildConfig.Enable_Markets ?order.getStockSymbol()+" "+order.getStockID():order.getStockSymbol());

        try{
            holder.tvOrderType.setText(order.getOrderTypeDescription());
            // holder.tvOrderType.setText(Actions.getStringFromValue(order.getOrderTypeID()));
        }catch (Exception e){}


        if(BuildConfig.Enable_Markets)
           holder.tvPriceItem.setText(String.valueOf(order.getPrice()));
        else
            holder.tvPriceItem.setText(String.valueOf(order.getPriceFormatted()));
       /*  if(order.getOrderTypeID()==MyApplication.LIMIT || order.getOrderTypeID()==MyApplication.MARKET_OPEN){
            holder.tvPriceItem.setText(String.valueOf(order.getPrice()));
            holder.tvPriceSymbol.setText(context.getResources().getString(R.string.price));
        }
        else {
            holder.tvPriceSymbol.setText(context.getResources().getString(R.string.trigger_price));
            holder.tvPriceItem.setText(String.valueOf(order.getTriggerPrice()));
        }*/


        holder.tvQuantityItem.setText(Actions.formatNumber(order.getQuantity(), Actions.NoDecimalThousandsSeparator));
//        holder.tvQuantityItem.setText(String.valueOf(order.getQuantity()));

      //
        holder.tvExecutedQuantityItem.setText(Actions.formatNumber(order.getQuantityExecuted(), Actions.NoDecimalThousandsSeparator));

        try{
        if(!BuildConfig.Enable_Markets) {
            holder.tvRemaininQtyValue.setVisibility(View.VISIBLE);
            holder.tvRemaininQtyValue.setText(Actions.formatNumber(order.getQuantity(), Actions.NoDecimalThousandsSeparator));

        }else {
            holder.tvRemaininQtyValue.setVisibility(View.GONE);
        }}catch (Exception e){}



        //        holder.tvExecutedQuantityItem.setText(String.valueOf(order.getQuantityExecuted()));

        holder.tvStatusItem.setText(order.getStatusDescription());

        switch (order.getStatusID()) {

            case MyApplication.STATUS_EXECUTED:

                holder.tvStatusItem.setVisibility(View.VISIBLE);
                holder.btActivateItem.setVisibility(View.GONE);
                holder.tvStatusItem.setTextColor(ContextCompat.getColor(context, R.color.white));


                holder.indicator.setBackgroundColor(ContextCompat.getColor(context, R.color.green_color));
                holder.tvStatusItem.setBackground(ContextCompat.getDrawable(context, R.drawable.open_market_status));

                break;

            case MyApplication.STATUS_REJECTED:

                holder.tvStatusItem.setVisibility(View.VISIBLE);
                holder.btActivateItem.setVisibility(View.GONE);
                holder.tvStatusItem.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.indicator.setBackgroundColor(ContextCompat.getColor(context, R.color.red_color));
                holder.tvStatusItem.setBackground(ContextCompat.getDrawable(context, R.drawable.closed_market_status));
                break;

            case MyApplication.STATUS_PRIVATE:

                holder.tvStatusItem.setVisibility(View.GONE);
                holder.btActivateItem.setVisibility(View.VISIBLE);
                break;

            default:
                holder.tvStatusItem.setVisibility(View.VISIBLE);
                holder.btActivateItem.setVisibility(View.GONE);
                holder.tvStatusItem.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.indicator.setBackgroundColor(ContextCompat.getColor(context, R.color.colorValues));
                holder.tvStatusItem.setBackground(ContextCompat.getDrawable(context, R.drawable.other_order_status));
                break;
        }

      //  holder.linearSubOrder.setVisibility(View.GONE);


        if(BuildConfig.expanded_order && order.getArraySubOrders().size()>0){
            holder.linearSubOrder.setVisibility(View.VISIBLE);
            LinearLayoutManager llm = new LinearLayoutManager(context);
            AdapterSubOrder adapterOrders = new AdapterSubOrder(context, order.getArraySubOrders(), null, null,false,0,false,false);
            holder.rvSubOrders.setLayoutManager(llm);
            holder.rvSubOrders.setAdapter(adapterOrders);
            holder.rvSubOrders.setNestedScrollingEnabled(false);

        }else {
            holder.linearSubOrder.setVisibility(View.GONE);
        }
      //  if(!order.isAdvancedOrder() && order.getAdvancedOrderTypeID()==MyApplication.MO)

       try {
           if (order.getArraySubOrders().size() > 0)
               holder.ivHasSub.setVisibility(View.VISIBLE);
           else
               holder.ivHasSub.setVisibility(View.GONE);
       }catch (Exception e){
           holder.ivHasSub.setVisibility(View.GONE);
       }

     try{
         if(order.getAdvancedOrderTypeID()==MyApplication.MO){
             holder.tvRelatedOrder.setVisibility(View.VISIBLE);
             try{holder.tvRelatedOrder.setText(context.getString(R.string.related_order)+" : "+order.getRelatedOnlineOrderNumber());}catch (Exception e){
                 holder.tvRelatedOrder.setText("0");
             }
             Log.wtf("related_order",order.getRelatedOnlineOrderID()+"aaa");
         }else if(order.getAdvancedOrderTypeID()==MyApplication.ICEBERG){
             holder.tvRelatedOrder.setVisibility(View.VISIBLE);
             try{holder.tvRelatedOrder.setText(context.getString(R.string.max_floor)+" : "
                     +Actions.formatNumber(order.getMaxfloor(), Actions.ThreeDecimalThousandsSeparator))
                    ;}catch (Exception e){
                 holder.tvRelatedOrder.setText("0");
             }
             Log.wtf("related_order",order.getRelatedOnlineOrderID()+"aaa");
         }


         else {
             holder.tvRelatedOrder.setVisibility(View.INVISIBLE);

         }}catch (Exception e){
         holder.tvRelatedOrder.setVisibility(View.INVISIBLE);
     }

     try {
         if(order.getAdvancedOrderTypeID()!=0){
             holder.tvAdvancedOrderType.setVisibility(View.VISIBLE);
            holder.tvAdvancedOrderType.setText(order.getAdvancedOrderTypeDescription()+"");
    /*         if(order.getAdvancedOrderTypeID()==MyApplication.MO && !order.isAdvancedOrder())
                 holder.tvAdvancedOrderType.setVisibility(View.GONE);
             else
                 holder.tvAdvancedOrderType.setVisibility(View.VISIBLE);*/

             // else
     /*             if(order.getAdvancedOrderTypeID()==MyApplication.MO){
                 holder.tvAdvancedOrderType.setText("MO");
             }else if(order.getAdvancedOrderTypeID()==MyApplication.ICEBERG){
                 holder.tvAdvancedOrderType.setText("ICEBERG");
             }else if(order.getAdvancedOrderTypeID()==MyApplication.OCA){
                 holder.tvAdvancedOrderType.setText("OCA");
             }*/

         }else {
             holder.tvAdvancedOrderType.setVisibility(View.GONE);
         }

     }catch (Exception e){
         holder.tvAdvancedOrderType.setVisibility(View.GONE);
     }




        switch (order.getTradeTypeID()) {

            case MyApplication.ORDER_BUY:

                holder.tvActionItem.setText(context.getResources().getString(R.string.buy));
                if(BuildConfig.Enable_Markets) {
                    holder.tvActionItem.setTextColor(ContextCompat.getColor(context, R.color.green_color));
                }else {
                    holder.tvActionItem.setTextColor(ContextCompat.getColor(context, R.color.blue_gig));
                }
                break;

            case MyApplication.ORDER_SELL:

                holder.tvActionItem.setText(context.getResources().getString(R.string.sell));
                holder.tvActionItem.setTextColor(ContextCompat.getColor(context, R.color.red_color));
                break;

            default:
                holder.tvActionItem.setTextColor(ContextCompat.getColor(context, R.color.colorValues));

        }

//        if (position % 2 == 1) {
//
//            holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));
//            holder.btSymbolItem.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));
//        } else {
//
//            holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
//            holder.btSymbolItem.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
//        }

        if(!BuildConfig.expanded_order){

        if (position % 2 == 0) {
            holder.llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
//            holder.llStock.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.white  : R.color.colorValues));
        } else {
            holder.llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));
//            holder.llStock.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.colorLight  : R.color.colorValues));
        }}else {

            holder.llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));

        }




        holder.llItem.setOnLongClickListener(view -> {
            showDialog(order);
            return true;
        });

        holder.llItem.setOnClickListener(view -> {
            Bundle b = new Bundle();


            b.putParcelable("order", order);
            Intent i = new Intent();
            i.putExtras(b);
            i.putExtra("isSubOrder",false);
            i.setClass(context, OrderDetailsActivity.class);
            context.startActivity(i);
        });

        holder.btSymbolItem.setOnClickListener(v -> {
            Intent intent = new Intent(context, StockDetailActivity.class);
            intent.putExtra("stockID", Integer.parseInt(order.getStockID()));
            context.startActivity(intent);
        });

        holder.btActivateItem.setOnClickListener(v -> showActivateDialog(context, order));

        if (MyApplication.lang == MyApplication.ARABIC) {

            holder.tvInstrumentTitle.setTypeface(MyApplication.droidregular);
            holder.tvInstrumentValue.setTypeface(MyApplication.droidbold);
            holder.btSymbolItem.setTypeface(MyApplication.droidregular);
            holder.btActivateItem.setTypeface(MyApplication.droidbold);
            holder.tvActionItem.setTypeface(MyApplication.droidbold);
            holder.tvOrderType.setTypeface(MyApplication.droidbold);
            holder.tvStatusItem.setTypeface(MyApplication.droidbold);
            holder.tvRelatedOrder.setTypeface(MyApplication.droidbold);
            holder.tvAdvancedOrderType.setTypeface(MyApplication.droidbold);

            holder.btSymbolItem.setTextSize(11);
            holder.tvInstrumentValue.setTextSize(11);
            holder.tvRelatedOrder.setTextSize(11);
            holder.tvAdvancedOrderType.setTextSize(11);
        } else {

            holder.tvInstrumentTitle.setTypeface(MyApplication.giloryItaly);
            holder.tvInstrumentValue.setTypeface(MyApplication.giloryBold);
            holder.btSymbolItem.setTypeface(MyApplication.giloryItaly);
            holder.btActivateItem.setTypeface(MyApplication.giloryBold);
            holder.tvActionItem.setTypeface(MyApplication.giloryBold);
            holder.tvOrderType.setTypeface(MyApplication.giloryBold);
            holder.tvStatusItem.setTypeface(MyApplication.giloryBold);
            holder.tvRelatedOrder.setTypeface(MyApplication.giloryBold);
            holder.tvAdvancedOrderType.setTypeface(MyApplication.giloryBold);
        }
//holder.tvInstrumentValue , holder.btSymbolItem ,
        Actions.setTypeface(new TextView[]{holder.tvPriceItem, holder.tvPriceSymbol,holder.tvQuantityItem, holder.tvExecutedQuantityItem,holder.tvRemaininQtyValue}, MyApplication.giloryBold);


    }

    @Override
    public int getItemCount() {
        return allOrders.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    private void showActivateDialog(Activity context, OnlineOrder order) {

        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.activate_order))
                .setMessage(context.getResources().getString(R.string.activate_order_text))
                .setPositiveButton(android.R.string.yes,
                        (dialog, which) -> new ActivateOrder(context, order).execute())
                .setNegativeButton(android.R.string.no,
                        (dialog, which) -> {
                            // do nothing
                        })
                .show();
    }

    private void showDialog(OnlineOrder onlineOrder) {

        StockQuotation stockQuotation = Actions.getStockQuotationById(MyApplication.stockQuotations, Integer.parseInt(onlineOrder.getStockID()));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final ArrayList<String> options = new ArrayList<>();
        options.add(context.getResources().getString(R.string.stock_details));
        options.add(context.getResources().getString(R.string.trades_title));
        options.add(context.getResources().getString(R.string.order_book));
        options.add(context.getResources().getString(R.string.order_book));

        String[] items = {context.getResources().getString(R.string.trades_title), context.getResources().getString(R.string.order_book), context.getResources().getString(R.string.edit)};

        builder.setItems(items, (dialog, which) -> {
            switch (which) {

                case 0: //trades
                    context.startActivity(new Intent(context, TimeSalesActivity.class)
                            .putExtra("stockId", stockQuotation.getStockID())
                            .putExtra("stockName", MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getNameEn()));
                    break;

                case 1: // order book

                    context.startActivity(new Intent(context, StockOrderBookActivity.class)
                            .putExtra("stockId", stockQuotation.getStockID())
                            .putExtra("last", stockQuotation.getLast())
                            .putExtra("high",stockQuotation.getHiLimit())
                            .putExtra("low",stockQuotation.getLowlimit())
                            .putExtra("volume",stockQuotation.getPreviousClosing())
                            .putExtra("Trades",stockQuotation.getTrade())
                            .putExtra("stockName", MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getNameEn()));
                    break;

                case 2: // edit

                    if (onlineOrder.isCanUpdate()) {

                        try {

                            Bundle b = new Bundle();
                            b.putInt("action", onlineOrder.getOrderTypeID());
                            b.putParcelable("stockQuotation", stockQuotation);
                            Intent i = new Intent(context, TradesActivity.class);
                            i.putExtras(b);
                            context.startActivity(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }/*else{
                        Toast.makeText(context,"cant", Toast.LENGTH_LONG).show();
                    }*/

                    break;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    public interface RecyclerViewOnItemClickListener {

        void onItemClicked(View v, int position);

    }

    public interface RefreshInterface {
        void refreshData();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewHolder(View v) {
            super(v);
        }
    }

    private class ItemViewHolder extends ViewHolder implements View.OnClickListener {

        protected View v, indicator;
        TextView tvPriceItem,btSymbolItem,tvRelatedOrder,tvRemaininQtyValue,tvAdvancedOrderType,tvPriceSymbol, tvQuantityItem, tvExecutedQuantityItem,tvOrderType, tvActionItem, tvStatusItem, tvInstrumentValue, tvInstrumentTitle;
        LinearLayout llItem;
        LinearLayout llTop, llInstruments, llStock;
        Button  btActivateItem;
        RecyclerView rvSubOrders;
        LinearLayout linearSubOrder;
        ImageView ivHasSub;

        private ItemViewHolder(View v) {
            super(v);
            this.v = v;
            this.v.setOnClickListener(this);

            this.indicator = v.findViewById(R.id.indicator);
            this.llTop = v.findViewById(R.id.llTop);
            this.llInstruments = v.findViewById(R.id.llInstrument);
            this.llStock = v.findViewById(R.id.llStock);
            this.llItem = v.findViewById(R.id.llItem);
            this.tvInstrumentValue = v.findViewById(R.id.tvInstrumentValue);
            this.btSymbolItem = v.findViewById(R.id.tvSymbolItem);
            this.btActivateItem = v.findViewById(R.id.tvActivateItem);
            this.tvInstrumentTitle = v.findViewById(R.id.tvInstrumentTitle);
            this.tvPriceItem = v.findViewById(R.id.tvPriceItem);
            this.tvPriceSymbol = v.findViewById(R.id.tvPriceSymbol);
            this.tvQuantityItem = v.findViewById(R.id.tvQuantityItem);
            this.tvExecutedQuantityItem = v.findViewById(R.id.tvExecutedQuantityItem);
            this.tvActionItem = v.findViewById(R.id.tvActionItem);
            this.tvOrderType = v.findViewById(R.id.tvOrderType);
            this.tvStatusItem = v.findViewById(R.id.tvStatusItem);
            this.rvSubOrders=v.findViewById(R.id.rvSubOrders);
            this.linearSubOrder=v.findViewById(R.id.linearSubOrder);
            this.ivHasSub=v.findViewById(R.id.ivHasSub);
            this.tvRelatedOrder = v.findViewById(R.id.tvRelatedOrder);
            this.tvAdvancedOrderType = v.findViewById(R.id.tvAdvancedOrderType);
            this.tvRemaininQtyValue = v.findViewById(R.id.tvRemaininQtyValue);

        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }
    //</editor-fold>

    //<editor-fold desc="Order Related Async Tasks">
    private class ActivateOrder extends AsyncTask<Void, Void, String> {

        OnlineOrder order;
        Activity context;

        public ActivateOrder(Activity context, OnlineOrder order) {

            this.context = context;
            this.order = order;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.ActivateOrder.getValue();


            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("ApplicationTypeID").value(Actions.getApplicationType())
                        .key("PlacementUserID").value(MyApplication.currentUser.getId())
                        .key("Reference").value(order.getReference())
                        .key("key").value(MyApplication.currentUser.getKey())
                        .key("StockID").value(String.valueOf(order.getStockID()))
                        .key("MarketID").value(Actions.getMarketType(Integer.parseInt(MyApplication.marketID)))
                        .key("TradingSession").value(Actions.getMarketSegmentParameter(order.getTradingSessionID()))


                        .endObject();
                Log.wtf("activate_order",stringer.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                context.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, context.getResources().getString(R.string.error_code) + MyApplication.ActivateOrder.getKey(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            result = ConnectionRequests.POSTWCF(url, stringer);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject object = null;
                try {
                    object = new JSONObject(result);
                    String status = object.getString("Status");
                    String messageAr = object.getString("MessageAr");
                    String messageEn = object.getString("MessageEn");


                Toast.makeText(context, MyApplication.lang == MyApplication.ENGLISH ? messageEn : messageAr, Toast.LENGTH_SHORT).show();
                }catch (Exception e){}
                MyApplication.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                context.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, context.getResources().getString(R.string.error_code) + MyApplication.ActivateOrder.getKey(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            refreshInterface.refreshData();
        }
    }
}
