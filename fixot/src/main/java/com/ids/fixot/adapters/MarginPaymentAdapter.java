package com.ids.fixot.adapters;



import android.app.Activity;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.StockActivity;
import com.ids.fixot.model.BalanceSummaryParent;
import com.ids.fixot.model.ExecutedOrders;
import com.ids.fixot.model.MarginPayment;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Amal on 3/31/2017.
 */

public class MarginPaymentAdapter extends RecyclerView.Adapter<MarginPaymentAdapter.ViewHolder> {


    private ArrayList<MarginPayment> allPayments;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;

    public MarginPaymentAdapter(Activity context, ArrayList<MarginPayment> allPayments) {
        this.context = context;
        this.allPayments = allPayments;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_margin_payments, viewGroup, false);
        return new ItemViewHolder(v);


    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {


        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        Actions.overrideFonts(context, holder.lllayout, false);



        try {
            holder.tvDueAmount.setText(Actions.formatNumber(Double.parseDouble(allPayments.get(position).getAmounDue()),Actions.TwoDecimalThousandsSeparator));
        } catch (Exception e) {
            e.printStackTrace();

        }

        try {
            holder.tvTradeDate.setText(allPayments.get(position).getTradeDate());
        } catch (Exception e) {
            e.printStackTrace();

        }

        try {
            holder.tvSettelementDate.setText(allPayments.get(position).getSettlementDate());
        } catch (Exception e) {
            e.printStackTrace();

        }

        try {
            holder.tvDays.setText(getDifferenceDate(allPayments.get(position).getSettlementDate())+"");
        } catch (Exception e) {
            e.printStackTrace();

        }


        try{


          if(getDifferenceDate(allPayments.get(position).getSettlementDate())>0){
              holder.tvSettelementDate.setTextColor(ContextCompat.getColor(context,R.color.green_color));
              holder.tvDueAmount.setTextColor(ContextCompat.getColor(context,R.color.green_color));
              holder.tvTradeDate.setTextColor(ContextCompat.getColor(context,R.color.green_color));
              holder.tvDays.setTextColor(ContextCompat.getColor(context,R.color.green_color));
           }else  if(getDifferenceDate(allPayments.get(position).getSettlementDate())==0 ){
              holder.tvSettelementDate.setTextColor(ContextCompat.getColor(context,R.color.orange));
              holder.tvDueAmount.setTextColor(ContextCompat.getColor(context,R.color.orange));
              holder.tvTradeDate.setTextColor(ContextCompat.getColor(context,R.color.orange));
              holder.tvDays.setTextColor(ContextCompat.getColor(context,R.color.orange));

          }
          else
              //if(getDifferenceDate(allPayments.get(position).getSettlementDate())<0)
              {

              holder.tvSettelementDate.setTextColor(ContextCompat.getColor(context,R.color.red_color));
              holder.tvDueAmount.setTextColor(ContextCompat.getColor(context,R.color.red_color));
              holder.tvTradeDate.setTextColor(ContextCompat.getColor(context,R.color.red_color));
              holder.tvDays.setTextColor(ContextCompat.getColor(context,R.color.red_color));
          }

        }catch (Exception e){
            holder.tvSettelementDate.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorValues : R.color.colorValuesInv));
            holder.tvDueAmount.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorValues : R.color.colorValuesInv));
            holder.tvTradeDate.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorValues : R.color.colorValuesInv));

        }





        Actions.setTypeface(new TextView[]{holder.tvDueAmount,holder.tvTradeDate,holder.tvSettelementDate,holder.tvDays}, MyApplication.droidbold);



    }

    private int getDifferenceDate(String settlementDate){
        try{
        Date settDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(settlementDate);
        Date today = new Date();
        long diff =  settDate.getTime()-today.getTime();
        int numOfDays = (int) (diff / (1000 * 60 * 60 * 24));
        return numOfDays;
       // int hours = (int) (diff / (1000 * 60 * 60));
        //int minutes = (int) (diff / (1000 * 60));
       // int seconds = (int) (diff / (1000));
        }catch (Exception e){
          return 10000000;
        }
    }

    @Override
    public int getItemCount() {
        return allPayments.size();
    }

    @Override
    public int getItemViewType(int position) {

        return 1;
    }

    public interface RecyclerViewOnItemClickListener {

        void onItemClicked(View v, int position);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class ItemViewHolder extends ViewHolder implements View.OnClickListener {


        protected View v;
        LinearLayout lllayout;
        TextView tvDueAmount,tvTradeDate,tvSettelementDate,tvDays;


        public ItemViewHolder(View v) {
            super(v);
            this.v = v;
            this.v.setOnClickListener(this);
            this.lllayout = v.findViewById(R.id.lllayout);
            this.tvDueAmount = v.findViewById(R.id.tvDueAmount);
            this.tvTradeDate = v.findViewById(R.id.tvTradeDate);
            this.tvSettelementDate = v.findViewById(R.id.tvSettelementDate);
            this.tvDays = v.findViewById(R.id.tvDays);
            Actions.autofitText(this.tvDueAmount,this.tvTradeDate,this.tvSettelementDate);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }
}
