package com.ids.fixot.adapters;


import android.app.Activity;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.util.Log;
import android.view.Gravity;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by user on 3/30/2017.
 */

public class StockOrderBookByPriceNewAdapter extends RecyclerView.Adapter<StockOrderBookByPriceNewAdapter.ViewHolder> {

    private ArrayList<StockOrderBook> allOrders = new ArrayList<>();
    private Activity context;
    boolean second = false;
    boolean byPrice = false;
    double maxAsk = 0.0;
    double maxBid = 0.0;
    public StockOrderBookByPriceNewAdapter(Activity context, ArrayList<StockOrderBook> allOrdersp, boolean second, boolean byPrice) {
        this.context = context;

        this.allOrders =  allOrdersp;
        this.second = second;
        this.byPrice = byPrice;
    }

    @Override
    public StockOrderBookByPriceNewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if(second)
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_book_by_price_second, viewGroup, false);
        else
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_book_price_first, viewGroup, false);
        return new StockOrderBookByPriceNewAdapter.ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        StockOrderBook order = allOrders.get(position);



        StockOrderBook maxAskStock, maxBidStock;

        try {


           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                maxAskStock = Collections.max(allOrders.subList(0, allOrders.size() - 1), Comparator.comparing(s -> s.getAskQuantity().replaceAll("[^0-9]", "")));
                maxBidStock = Collections.max(allOrders.subList(0, allOrders.size() - 1), Comparator.comparing(s -> s.getBidQuantity().replaceAll("[^0-9]", "")));

                maxAsk = Double.parseDouble(maxAskStock.getAskQuantity().replaceAll("[^0-9]", ""));
                maxBid = Double.parseDouble(maxBidStock.getBidQuantity().replaceAll("[^0-9]", ""));

            } else {*/

                maxAskStock = new StockOrderBook();
                maxBidStock = new StockOrderBook();

                for (int i = 0; i < allOrders.size() - 1; i++) {

                    try{
                    if (Double.parseDouble(allOrders.get(i).getAskQuantity().replaceAll("\\D+","")) > Double.parseDouble(maxAskStock.getAskQuantity().replaceAll("\\D+",""))) {

                        maxAskStock = allOrders.get(i);
                    }

                    if (Double.parseDouble(allOrders.get(i).getBidQuantity().replaceAll("\\D+","")) > Double.parseDouble(maxBidStock.getBidQuantity().replaceAll("\\D+",""))) {

                        maxBidStock = allOrders.get(i);
                    }}catch (Exception e){}

                }

                try {
                    maxAsk = Double.parseDouble(maxAskStock.getAskQuantity().replaceAll("\\D+", ""));
                    maxBid = Double.parseDouble(maxBidStock.getBidQuantity().replaceAll("\\D+", ""));
                }catch (Exception e){
                    maxAsk=0;
                    maxBid=0;
                }
           // }
            Log.wtf("max_bid",maxBid+"bbbb");
            Log.wtf("max_ask",maxAsk+"aaaa");

        } catch (Exception e) {
            Log.wtf("max_bid",e.toString());
            e.printStackTrace();
        }





        try {
            if(!order.getOrderTime().matches("0") && !order.getOrderTime().matches("0.0"))
                holder.tvTimeItem.setText(getTimeFromDate(order.getOrderTime()));
            else
                holder.tvTimeItem.setText("");
        } catch (Exception e) {
            holder.tvTimeItem.setText("");
        }




            if(second){
                try {
                    holder.tvQtyItem.setText(order.getAskQuantity());
                } catch (Exception e) {
                    Log.wtf("maa",e.toString());
                }






                try {
                    if(!order.getAskValue().matches("0.0"))
                        holder.tvTimeItem.setText(order.getAsk()+"");
                    else
                        holder.tvTimeItem.setText("");
                } catch (Exception e) {
                    holder.tvTimeItem.setText("");
                }





                try {
                    holder.tvPriceItem.setText(order.getAskValue()+"");
                } catch (Exception e) {
                    holder.tvPriceItem.setText("");
                }

            }else {


                try {
                    if(!order.getBidValue().matches("0.0"))
                        holder.tvTimeItem.setText(order.getBid()+"");
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
                    holder.tvPriceItem.setText(order.getBidValue()+"");
                } catch (Exception e) {
                    holder.tvPriceItem.setText("");
                }

            }



        // holder.separator.setVisibility(View.GONE);
       // if (MyApplication.lang == MyApplication.ARABIC) {

        if(allOrders.size()>1 && position==allOrders.size()-1){
            holder.separator.setVisibility(View.GONE);
            holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, R.color.colorValues));
            holder.tvQtyItem.setTextColor(ContextCompat.getColor(context, R.color.colorValues));
            holder.tvTimeItem.setTextColor(ContextCompat.getColor(context, R.color.colorValues));
        }else {
            holder.separator.setVisibility(View.VISIBLE);
            if (!second) {
                
                holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, R.color.blue_gig));
                holder.tvQtyItem.setTextColor(ContextCompat.getColor(context, R.color.blue_gig));
                holder.tvTimeItem.setTextColor(ContextCompat.getColor(context, R.color.blue_gig));
            } else {
                holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, R.color.red_color));
                holder.tvQtyItem.setTextColor(ContextCompat.getColor(context, R.color.red_color));
                holder.tvTimeItem.setTextColor(ContextCompat.getColor(context, R.color.red_color));
            }
        }
      /*  }else {

            if (second) {
                holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, R.color.blue_gig));
                holder.tvQtyItem.setTextColor(ContextCompat.getColor(context, R.color.blue_gig));
                holder.tvTimeItem.setTextColor(ContextCompat.getColor(context, R.color.blue_gig));
            } else {
                holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, R.color.red_color));
                holder.tvQtyItem.setTextColor(ContextCompat.getColor(context, R.color.red_color));
                holder.tvTimeItem.setTextColor(ContextCompat.getColor(context, R.color.red_color));
            }
        }*/



/*        if (order.getAskQuantity().length() > 0 && order.getBidQuantity().length() > 0 && !order.getAskQuantity().matches("0")  && !order.getAskQuantity().matches("0.0") && !order.getBidQuantity().matches("0") && !order.getBidQuantity().matches("0.0")) { //all gray

            holder.separator.setVisibility(View.GONE);


        } else {

            holder.separator.setVisibility(View.VISIBLE);*/
            if (second) {

                if (order.getAskQuantity().replaceAll("[^0-9]", "").length() > 0 && !order.getAskQuantity().replaceAll("[^0-9]", "").matches("0") && !order.getAskQuantity().replaceAll("[^0-9]", "").matches("0.0")){

                    double askPercentage=0.0;
                    try{ askPercentage = (Double.parseDouble(order.getAskQuantity().replaceAll("[^0-9]", "")) * 100) / maxAsk;}catch (Exception e){}
                    if (askPercentage < 10.0) {
                        askPercentage = askPercentage + 10;
                    }
                    holder.separator.setBackgroundColor(askPercentage == 100 ? ContextCompat.getColor(context, R.color.red_color) : ContextCompat.getColor(context, R.color.light_red_color));
                    double width = (MyApplication.screenWidth) * ((askPercentage * 0.6) / 100);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) width, 2);
                    params.gravity = Gravity.END;
                    holder.separator.setLayoutParams(params);

                    if (order.getAskQuantity().replaceAll("[^0-9]", "").equals("0") || order.getAskQuantity().replaceAll("[^0-9]", "").equals("0.0") ) {
                        holder.separator.setVisibility(View.GONE);
                    } else {

                        holder.separator.setVisibility(View.VISIBLE);
                    }
              }
            } else {


                   double bidPercentage=0.0;
                    try{ bidPercentage = (Double.parseDouble(order.getBidQuantity().replaceAll("[^0-9]", "")) * 100) / maxBid;}catch (Exception e){}
                    if (bidPercentage < 10.0) {
                        bidPercentage = bidPercentage + 10;
                    }
                    holder.separator.setBackgroundColor(bidPercentage == 100 ? ContextCompat.getColor(context, R.color.blue_gig) : ContextCompat.getColor(context, R.color.blue_gig_light));
                    double width = (MyApplication.screenWidth) * ((bidPercentage * 0.6) / 100);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) width, 2);
                    params.gravity = Gravity.START;
                    holder.separator.setLayoutParams(params);

                    if (order.getBidQuantity().replaceAll("[^0-9]", "").equals("0") || order.getBidQuantity().replaceAll("[^0-9]", "").equals("0.0") ) {
                        holder.separator.setVisibility(View.GONE);
                    } else {

                        holder.separator.setVisibility(View.VISIBLE);
                    }


            }


     //   }

     if(allOrders.size()>1 && position==allOrders.size()-1)
         holder.separator.setVisibility(View.GONE);


        Actions.overrideFonts(context, holder.llItem, false);
        Actions.setTypeface(new TextView[] {holder.tvPriceItem, holder.tvQtyItem, holder.tvTimeItem}, MyApplication.giloryBold);

    }

    public String getTimeFromDate(String dateTime) throws ParseException {
        // Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
        String newString="";
        try {
            Date date = new SimpleDateFormat("M/dd/yyyy HH:mm:ss a", Locale.ENGLISH).parse(dateTime);
            newString = new SimpleDateFormat("HH:mm:ss a", Locale.ENGLISH).format(date); // 9:00
        }catch (Exception e){
            Log.wtf("date_exception_1",e.toString());
            try{
                Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a", Locale.ENGLISH).parse(dateTime);
                newString = new SimpleDateFormat("HH:mm:ss a", Locale.ENGLISH).format(date); // 9:00
            }catch (Exception e1){
                Log.wtf("date_exception_2",e1.toString());
                try{
                    Date date = new SimpleDateFormat("MM/dd/yyyy H:mm:ss a", Locale.ENGLISH).parse(dateTime);
                    newString = new SimpleDateFormat("HH:mm:ss a", Locale.ENGLISH).format(date); // 9:00
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
