package com.ids.fixot.adapters;


import android.app.Activity;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.StockOrderBook;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by user on 3/30/2017.
 */

public class StockOrderBookByOrderrecyclerNewAdapter extends RecyclerView.Adapter<StockOrderBookByOrderrecyclerNewAdapter.ViewHolder> {

    private ArrayList<StockOrderBook> allOrders = new ArrayList<>();
    private Activity context;
    boolean second = false;
    boolean byPrice = false;

    public StockOrderBookByOrderrecyclerNewAdapter(Activity context, ArrayList<StockOrderBook> allOrdersp, boolean second,boolean byPrice) {
        this.context = context;

        this.allOrders =  allOrdersp;
        this.second = second;
        this.byPrice = byPrice;
    }

    @Override
    public StockOrderBookByOrderrecyclerNewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if(second)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_book_by_order_second_new, viewGroup, false);
        else
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_book_by_order_item_new, viewGroup, false);
        return new StockOrderBookByOrderrecyclerNewAdapter.ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        StockOrderBook order = allOrders.get(position);

        try {
            if(!order.getOrderTime().matches("0") && !order.getOrderTime().matches("0.0"))
                holder.tvTimeItem.setText(getTimeFromDate(order.getOrderTime()));
            else
                holder.tvTimeItem.setText("");
        } catch (Exception e) {
            holder.tvTimeItem.setText("");
        }

        if(!byPrice){
            try {
                holder.tvQtyItem.setText(order.getQuantity());
            } catch (Exception e) {
                Log.wtf("maa",e.toString());
            }

            try {
                holder.tvPriceItem.setText(order.getPrice());
            } catch (Exception e) {
                holder.tvPriceItem.setText("");
            }



        }

        else {

            if(second){
                try {
                    holder.tvQtyItem.setText(order.getAskQuantity());
                } catch (Exception e) {
                    Log.wtf("maa",e.toString());
                }






                try {
                    if(!order.getAskValue().matches("0.0"))
                        holder.tvTimeItem.setText(order.getAskValue()+"");
                    else
                        holder.tvTimeItem.setText("");
                } catch (Exception e) {
                    holder.tvTimeItem.setText("");
                }





                try {
                    holder.tvPriceItem.setText(order.getAsk()+"");
                } catch (Exception e) {
                    holder.tvPriceItem.setText("");
                }

            }else {


                try {
                    if(!order.getBidValue().matches("0.0"))
                        holder.tvTimeItem.setText(order.getBidValue()+"");
                    else
                        holder.tvTimeItem.setText("");
                } catch (Exception e) {
                    holder.tvTimeItem.setText("");
                }

                try {
                    holder.tvQtyItem.setText(order.getBidQuantity());
                } catch (Exception e) {
                    Log.wtf("maa",e.toString());
                }

                try {
                    holder.tvPriceItem.setText(order.getBid()+"");
                } catch (Exception e) {
                    holder.tvPriceItem.setText("");
                }

            }

        }

        // holder.separator.setVisibility(View.GONE);

        if (!second) {
            holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, R.color.blue_gig));
            holder.tvQtyItem.setTextColor(ContextCompat.getColor(context, R.color.blue_gig));
            holder.tvTimeItem.setTextColor(ContextCompat.getColor(context, R.color.blue_gig));
        } else {
            holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, R.color.red_color));
            holder.tvQtyItem.setTextColor(ContextCompat.getColor(context, R.color.red_color));
            holder.tvTimeItem.setTextColor(ContextCompat.getColor(context, R.color.red_color));
        }

        Actions.overrideFonts(context, holder.llItem, false);
        Actions.setTypeface(new TextView[] {holder.tvPriceItem, holder.tvQtyItem, holder.tvTimeItem}, MyApplication.giloryBold);

    }

    public String getTimeFromDate(String dateTime) throws ParseException {
        // Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
        String newString="";
        try {
            Date date = new SimpleDateFormat("M/dd/yyyy HH:mm:ss", Locale.ENGLISH).parse(dateTime);
            newString = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(date); // 9:00
        }catch (Exception e){
            Log.wtf("date_exception_1",e.toString());
            try{
                Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH).parse(dateTime);
                newString = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(date); // 9:00
            }catch (Exception e1){
                Log.wtf("date_exception_2",e1.toString());
                try{
                    Date date = new SimpleDateFormat("MM/dd/yyyy H:mm:ss", Locale.ENGLISH).parse(dateTime);
                    newString = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(date); // 9:00
                }catch (Exception e3){
                    Log.wtf("date_exception_3",e3.toString());
                }
            }

        }

        return newString;
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewHolder(View v) {
            super(v);
        }
    }

    private class ItemViewHolder extends ViewHolder {

        protected View v;
        protected View separator;
        TextView  tvPriceItem, tvQtyItem, tvTimeItem;
        LinearLayout llItem;

        private ItemViewHolder(View v) {
            super(v);
            this.v = v;

            this.separator = v.findViewById(R.id.separator);
            this.llItem = v.findViewById(R.id.llItem);
            this.tvPriceItem = v.findViewById(R.id.tvPriceItem);
            this.tvQtyItem = v.findViewById(R.id.tvQtyItem);
            this.tvTimeItem = v.findViewById(R.id.tvTimeItem);
        }

    }
}