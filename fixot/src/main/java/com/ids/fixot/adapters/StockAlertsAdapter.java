package com.ids.fixot.adapters;


import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.enums.enums;
import com.ids.fixot.model.StockAlerts;


import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class StockAlertsAdapter extends RecyclerView.Adapter<StockAlertsAdapter.ViewHolder> {


    private ArrayList<StockAlerts> allStockAlerts;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;

    public StockAlertsAdapter(Activity context, ArrayList<StockAlerts> allStockAlerts, RecyclerViewOnItemClickListener itemClickListener) {
        this.context = context;
        this.allStockAlerts = allStockAlerts;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_stock_alert, viewGroup, false);
        return new ItemViewHolder(v);


    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {


        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        Actions.overrideFonts(context, holder.lllayout, false);
        if (MyApplication.lang == MyApplication.ARABIC) {

            try {
                holder.tvStockAlertOperator.setText(allStockAlerts.get(position).getOperatorDescriptionAr());
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.tvStockAlertOperator.setTypeface(MyApplication.droidbold);
            holder.tvStockId.setTypeface(MyApplication.droidbold);
        } else {

            try {
                holder.tvStockAlertOperator.setText(allStockAlerts.get(position).getOperatorDescriptionEn());
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.tvStockAlertOperator.setTypeface(MyApplication.giloryBold);
            holder.tvStockId.setTypeface(MyApplication.giloryBold);
        }


        if (MyApplication.lang == MyApplication.ARABIC) {

            try {
                holder.tvStockAlertType.setText(allStockAlerts.get(position).getTypeDescriptionAr());
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.tvStockAlertType.setTypeface(MyApplication.droidbold);
        } else {

            try {
                holder.tvStockAlertType.setText(allStockAlerts.get(position).getTypeDescriptionEn());
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.tvStockAlertType.setTypeface(MyApplication.giloryBold);
        }



        if (MyApplication.lang == MyApplication.ARABIC) {

            try {
                holder.tvStockName.setText(allStockAlerts.get(position).getSybmolAr());
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.tvStockName.setTypeface(MyApplication.droidbold);
        } else {

            try {
                holder.tvStockName.setText(allStockAlerts.get(position).getSybmolEn());
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.tvStockName.setTypeface(MyApplication.giloryBold);
        }

        try {
            holder.tvStockId.setText(allStockAlerts.get(position).getStockID()+"");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            holder.tvStockAlertValue.setText(allStockAlerts.get(position).getValue()+"");
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            if (MyApplication.lang == MyApplication.ARABIC) {

                try {
                    holder.tvStockAlertStatus.setText(allStockAlerts.get(position).getStatusDescriptionAr()+"");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.tvStockAlertStatus.setTypeface(MyApplication.droidbold);

            }else {

                try {
                    holder.tvStockAlertStatus.setText(allStockAlerts.get(position).getStatusDescriptionEn());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.tvStockAlertStatus.setTypeface(MyApplication.giloryBold);

            }

            if(allStockAlerts.get(position).getStatusID()==1){
                holder.tvStockAlertStatus.setTextColor(ContextCompat.getColor(context, R.color.green_color));
            }else {
                holder.tvStockAlertStatus.setTextColor(ContextCompat.getColor(context, R.color.red_color));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (position % 2 == 0) {
            holder.lllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
        } else {
            holder.lllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));
        }

        if(allStockAlerts.get(position).getStatusID()== enums.AlertStatus.ACTIVE.getValue()) {
            holder.btActivate.setText(context.getString(R.string.delete));
            holder.btActivate.setTextColor(ContextCompat.getColor(context, R.color.red_color));
        }
        else {
            holder.btActivate.setText(context.getString(R.string.activate));
            holder.btActivate.setTextColor(ContextCompat.getColor(context, R.color.green_color));
        }
        holder.btActivate.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));

    }

    @Override
    public int getItemCount() {
        return allStockAlerts.size();
    }

    @Override
    public int getItemViewType(int position) {

        return 1;
    }

    public ArrayList<StockAlerts> getAlerts() {
        return allStockAlerts;
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

        protected TextView tvStockId,tvStockName,tvStockAlertOperator,tvStockAlertType,tvStockAlertValue,tvStockAlertStatus;
        protected Button btActivate;

        protected View v;
        LinearLayout lllayout;

        public ItemViewHolder(View v) {
            super(v);
            this.v = v;
          //  this.v.setOnClickListener(this);

            this.tvStockId = v.findViewById(R.id.tvStockId);
            this.tvStockName = v.findViewById(R.id.tvStockName);
            this.tvStockAlertOperator = v.findViewById(R.id.tvStockAlertOperator);
            this.tvStockAlertType = v.findViewById(R.id.tvStockAlertType);
            this.tvStockAlertValue = v.findViewById(R.id.tvStockAlertValue);
            this.tvStockAlertStatus = v.findViewById(R.id.tvStockAlertStatus);


            this.btActivate = v.findViewById(R.id.btActivate);
            btActivate.setOnClickListener(this);
            this.lllayout = v.findViewById(R.id.lllayout);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }
}
