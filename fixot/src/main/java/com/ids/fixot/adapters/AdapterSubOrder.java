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
import androidx.recyclerview.widget.RecyclerView;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.OrderDetailsActivity;
import com.ids.fixot.activities.SettingsActivity;
import com.ids.fixot.activities.StockDetailActivity;
import com.ids.fixot.activities.StockOrderBookActivity;
import com.ids.fixot.activities.TimeSalesActivity;
import com.ids.fixot.activities.TradesActivity;
import com.ids.fixot.model.OnlineOrder;
import com.ids.fixot.model.StockQuotation;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;


public class AdapterSubOrder extends RecyclerView.Adapter<AdapterSubOrder.ViewHolder> {


    private ArrayList<OnlineOrder> allOrders;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;
    private RefreshInterface refreshInterface;
    private Boolean showDelete,isDetails,isConfirm;
    private int selectedOrderId;

    public AdapterSubOrder(Activity context, ArrayList<OnlineOrder> allOrders, RecyclerViewOnItemClickListener itemClickListener, RefreshInterface refreshInterface,Boolean showDelete,int selectedOrderId,Boolean isDetails,Boolean isConfirm) {
        this.context = context;
        this.allOrders = allOrders;
        this.itemClickListener = itemClickListener;
        this.refreshInterface = refreshInterface;
        this.showDelete=showDelete;
        this.selectedOrderId=selectedOrderId;
        this.isDetails=isDetails;
        this.isConfirm=isConfirm;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sub_merged_order, viewGroup, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        final OnlineOrder order = allOrders.get(position);

        try {
            holder.tvInstrumentValue.setText(MyApplication.lang == MyApplication.ENGLISH ?
                    MyApplication.instrumentsHashmap.get(order.getInstrumentID()).getInstrumentNameEn() :
                    MyApplication.instrumentsHashmap.get(order.getInstrumentID()).getInstrumentNameAr());
        }catch (Exception e){}

        try{

            holder.btSymbolItem.setText(order.getStockID()+" "+order.getStockSymbol());
        /*    for (int i=0;i<MyApplication.stockQuotations.size();i++){
                if(Integer.parseInt(order.getStockID())==MyApplication.stockQuotations.get(i).getStockID()){
                    if(MyApplication.lang == MyApplication.ARABIC )
                        holder.btSymbolItem.setText(order.getStockID()+" "+MyApplication.stockQuotations.get(i).getSymbolAr());
                    else
                        holder.btSymbolItem.setText(order.getStockID()+" "+MyApplication.stockQuotations.get(i).getSymbolEn());
                }
            }*/
        }catch (Exception e){
            Log.wtf("exception_1",e.toString());
        }



        try{
            holder.tvOrderType.setText(order.getOrderTypeDescription());
            // holder.tvOrderType.setText(Actions.getStringFromValue(order.getOrderTypeID()));
        }catch (Exception e){}


        holder.tvPriceItem.setText(String.valueOf(order.getPrice()));
        holder.tvQuantityItem.setText(Actions.formatNumber(order.getQuantity(), Actions.NoDecimalThousandsSeparator));
        holder.tvExecutedQuantityItem.setText(Actions.formatNumber(order.getQuantityExecuted(), Actions.NoDecimalThousandsSeparator));

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



        if(selectedOrderId!=0 && order.getID()==selectedOrderId) {
            holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
        }
        else {

                holder.llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));

        }




                holder.ivHasSub.setVisibility(View.GONE);




        try {
            if(order.getAdvancedOrderTypeID()!=0){
                holder.tvAdvancedOrderType.setVisibility(View.VISIBLE);
                holder.tvAdvancedOrderType.setText(order.getAdvancedOrderTypeDescription()+"");

            }else {
                holder.tvAdvancedOrderType.setVisibility(View.GONE);
            }
        }catch (Exception e){
            holder.tvAdvancedOrderType.setVisibility(View.GONE);
        }








        switch (order.getTradeTypeID()) {

            case MyApplication.ORDER_BUY:

                holder.tvActionItem.setText(context.getResources().getString(R.string.buy));
                holder.tvActionItem.setTextColor(ContextCompat.getColor(context, R.color.green_color));
                break;

            case MyApplication.ORDER_SELL:

                holder.tvActionItem.setText(context.getResources().getString(R.string.sell));
                holder.tvActionItem.setTextColor(ContextCompat.getColor(context, R.color.red_color));
                break;

            default:
                holder.tvActionItem.setTextColor(ContextCompat.getColor(context, R.color.colorValues));

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
        Actions.setTypeface(new TextView[]{holder.tvPriceItem, holder.tvPriceSymbol,holder.tvQuantityItem, holder.tvExecutedQuantityItem}, MyApplication.giloryBold);


    }

    private void showActivateDialog(Activity context, OnlineOrder order) {

        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.activate_order))
                .setMessage(context.getResources().getString(R.string.activate_order_text))
                .setPositiveButton(android.R.string.yes,
                        (dialog, which) -> new AdapterSubOrder.ActivateOrder(context, order).execute())
                .setNegativeButton(android.R.string.no,
                        (dialog, which) -> {
                            // do nothing
                        })
                .show();
    }

    @Override
    public int getItemCount() {
        return allOrders.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }



    public interface RecyclerViewOnItemClickListener {

        void onItemClicked(View v, int position);

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
        TextView tvPriceItem,btSymbolItem,tvRelatedOrder,tvPriceSymbol,tvAdvancedOrderType, tvQuantityItem, tvExecutedQuantityItem,tvOrderType, tvActionItem, tvStatusItem, tvInstrumentValue, tvInstrumentTitle;
        LinearLayout llItem;
        LinearLayout llTop, llInstruments, llStock,line2end1,line2end2;
        Button  btActivateItem;
        ImageView ivHasSub;



        private ItemViewHolder(View v) {
            super(v);
            this.v = v;
            //this.v.setOnClickListener(this);

            this.indicator = v.findViewById(R.id.indicator);
            this.llTop = v.findViewById(R.id.llTop);
            this.llInstruments = v.findViewById(R.id.llInstrument);
            this.llStock = v.findViewById(R.id.llStock);
            this.llItem = v.findViewById(R.id.llItem);
            llItem.setId(R.id.linearPopupOrder);
            llItem.setOnClickListener(this);
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
            this.ivHasSub=v.findViewById(R.id.ivHasSub);
            this.tvAdvancedOrderType = v.findViewById(R.id.tvAdvancedOrderType);
            this.tvRelatedOrder = v.findViewById(R.id.tvRelatedOrder);
            line2end1 = v.findViewById(R.id.line2end1);
            line2end2 = v.findViewById(R.id.line2end2);



        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }

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
                     /*   .key("ApplicationType").value(Actions.getApplicationType())
                        .key("Reference").value(order.getReference())
                        .key("key").value(MyApplication.currentUser.getKey())
                        .key("StockID").value(String.valueOf(order.getStockID()))
                        .key("MarketID").value(MyApplication.marketID)
                        .key("TradingSession").value(order.getTradingSessionID())*/
                        .key("ApplicationTypeID").value(Actions.getApplicationType())
                        .key("PlacementUserID").value(MyApplication.currentUser.getId())
                        .key("Reference").value(order.getReference())
                        .key("key").value(MyApplication.currentUser.getKey())
                        .key("StockID").value(String.valueOf(order.getStockID()))
                        .key("MarketID").value(Actions.getMarketType(Integer.parseInt(MyApplication.marketID)))
                        .key("TradingSession").value(Actions.getMarketSegmentParameter(order.getTradingSessionID()))

                        .endObject();
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
    //</editor-fold>

    //<editor-fold desc="Order Related Async Tasks">

}
