package com.ids.fixot.adapters;


import android.app.Activity;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.SettingsActivity;
import com.ids.fixot.model.OnlineOrder;
import java.util.ArrayList;


public class OrdersPopupTrades extends RecyclerView.Adapter<OrdersPopupTrades.ViewHolder> {


    private ArrayList<OnlineOrder> allOrders;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;
    private RefreshInterface refreshInterface;
    private Boolean showDelete,isDetails,isConfirm;
    private int selectedOrderId;

    public OrdersPopupTrades(Activity context, ArrayList<OnlineOrder> allOrders, RecyclerViewOnItemClickListener itemClickListener, RefreshInterface refreshInterface,Boolean showDelete,int selectedOrderId,Boolean isDetails,Boolean isConfirm) {
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
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.orders_item, viewGroup, false);
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
            for (int i=0;i<MyApplication.stockQuotations.size();i++){
                if(Integer.parseInt(order.getStockID())==MyApplication.stockQuotations.get(i).getStockID()){
                    if(MyApplication.lang == MyApplication.ARABIC )
                       holder.btSymbolItem.setText(order.getStockID()+" "+MyApplication.stockQuotations.get(i).getSymbolAr());
                    else
                        holder.btSymbolItem.setText(order.getStockID()+" "+MyApplication.stockQuotations.get(i).getSymbolEn());
                }
            }
        }catch (Exception e){}



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

                holder.tvStatusItem.setVisibility(View.INVISIBLE);
                holder.btActivateItem.setVisibility(View.VISIBLE);
                break;

            default:

                if(isDetails){
                    holder.tvStatusItem.setVisibility(View.VISIBLE);
                    holder.btActivateItem.setVisibility(View.GONE);
                    holder.tvStatusItem.setTextColor(ContextCompat.getColor(context, R.color.white));
                    holder.indicator.setBackgroundColor(ContextCompat.getColor(context, R.color.colorValues));
                    holder.tvStatusItem.setBackground(ContextCompat.getDrawable(context, R.drawable.other_order_status));
                }else {
                    holder.tvStatusItem.setVisibility(View.GONE);
                    holder.btActivateItem.setVisibility(View.GONE);
                    holder.tvStatusItem.setTextColor(ContextCompat.getColor(context, R.color.white));
                    holder.indicator.setBackgroundColor(ContextCompat.getColor(context, R.color.colorValues));
                    holder.tvStatusItem.setBackground(ContextCompat.getDrawable(context, R.drawable.other_order_status));
                }
                break;
        }



          if(selectedOrderId!=0 && order.getID()==selectedOrderId) {
              holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
          }
         else {
            if (position % 2 == 0) {
                holder.llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
            } else {
                holder.llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));
            }
        }



        try {
            if (order.getArraySubOrders().size() > 0)
                holder.ivHasSub.setVisibility(View.VISIBLE);
            else
                holder.ivHasSub.setVisibility(View.GONE);
        }catch (Exception e){
            holder.ivHasSub.setVisibility(View.GONE);
        }



        try {
            if(order.getAdvancedOrderTypeID()!=0){
                holder.tvAdvancedOrderType.setVisibility(View.VISIBLE);

                if(order.getAdvancedOrderTypeID()==MyApplication.MO && !order.isAdvancedOrder())
                    holder.tvAdvancedOrderType.setVisibility(View.GONE);

                else if(order.getAdvancedOrderTypeID()==MyApplication.MO){
                    holder.tvAdvancedOrderType.setText("MO");
                }else if(order.getAdvancedOrderTypeID()==MyApplication.ICEBERG){
                    holder.tvAdvancedOrderType.setText("ICEBERG");
                }else if(order.getAdvancedOrderTypeID()==MyApplication.OCA){
                    holder.tvAdvancedOrderType.setText("OCA");
                }

                else
                    holder.tvAdvancedOrderType.setVisibility(View.GONE);

            }else {
                holder.tvAdvancedOrderType.setVisibility(View.GONE);
            }

        }catch (Exception e){
            holder.tvAdvancedOrderType.setVisibility(View.GONE);
        }



        if(showDelete) {
            holder.btActivateItem.setVisibility(View.VISIBLE);
            holder.btActivateItem.setBackgroundColor(ContextCompat.getColor(context, R.color.red_color));
            holder.btActivateItem.setText(holder.itemView.getContext().getString(R.string.delete));
            holder.btActivateItem.setOnClickListener(v->{
                allOrders.remove(position);
                notifyDataSetChanged();
            });
            holder.tvExecutedQuantityItem.setVisibility(View.GONE);


            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.8f
            );
            holder.tvRelatedOrder.setLayoutParams(param);

        } else if(isConfirm){
            holder.btActivateItem.setVisibility(View.GONE);
            holder.tvExecutedQuantityItem.setVisibility(View.GONE);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.8f
            );
            holder.tvRelatedOrder.setLayoutParams(param);
            holder.line2end1.setVisibility(View.GONE);
        }

        else {
            holder.btActivateItem.setVisibility(View.GONE);
            holder.tvExecutedQuantityItem.setVisibility(View.VISIBLE);
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



        if (MyApplication.lang == MyApplication.ARABIC) {

            holder.tvInstrumentTitle.setTypeface(MyApplication.droidregular);
            holder.tvInstrumentValue.setTypeface(MyApplication.droidbold);
            holder.btSymbolItem.setTypeface(MyApplication.droidregular);
            holder.btActivateItem.setTypeface(MyApplication.droidbold);
            holder.tvActionItem.setTypeface(MyApplication.droidbold);
            holder.tvOrderType.setTypeface(MyApplication.droidbold);
            holder.tvStatusItem.setTypeface(MyApplication.droidbold);

            holder.btSymbolItem.setTextSize(11);
            holder.tvInstrumentValue.setTextSize(11);
        } else {

            holder.tvInstrumentTitle.setTypeface(MyApplication.giloryItaly);
            holder.tvInstrumentValue.setTypeface(MyApplication.giloryBold);
            holder.btSymbolItem.setTypeface(MyApplication.giloryItaly);
            holder.btActivateItem.setTypeface(MyApplication.giloryBold);
            holder.tvActionItem.setTypeface(MyApplication.giloryBold);
            holder.tvOrderType.setTypeface(MyApplication.giloryBold);
            holder.tvStatusItem.setTypeface(MyApplication.giloryBold);
        }
//holder.tvInstrumentValue , holder.btSymbolItem ,
        Actions.setTypeface(new TextView[]{holder.tvPriceItem, holder.tvPriceSymbol,holder.tvQuantityItem, holder.tvExecutedQuantityItem}, MyApplication.giloryBold);

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
    //</editor-fold>

    //<editor-fold desc="Order Related Async Tasks">

}
