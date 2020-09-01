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


import java.util.ArrayList;

/**
 * Created by Amal on 3/31/2017.
 */

public class ExecutedAdapter extends RecyclerView.Adapter<ExecutedAdapter.ViewHolder> {


    private ArrayList<ExecutedOrders> allExecuted;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;

    public ExecutedAdapter(Activity context, ArrayList<ExecutedOrders> allExecuted) {
        this.context = context;
        this.allExecuted = allExecuted;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_executed_orders, viewGroup, false);
        return new ItemViewHolder(v);


    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {


        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        Actions.overrideFonts(context, holder.lllayout, false);



        try {
            holder.tvSymbolValue.setText(allExecuted.get(position).getSecurityID());
        } catch (Exception e) {
            e.printStackTrace();

        }

        try {
            holder.tvActionValue.setText(allExecuted.get(position).getAction());
        } catch (Exception e) {
            e.printStackTrace();

        }

        try {
            holder.tvQtyExecutedValue.setText(allExecuted.get(position).getQuantityExecuted());
        } catch (Exception e) {
            e.printStackTrace();

        }

        try {
            holder.tvAvgPriceValue.setText(allExecuted.get(position).getAvgPrice());
        } catch (Exception e) {
            e.printStackTrace();

        }


try{
        switch (Integer.parseInt(allExecuted.get(position).getTradeTypeID())) {

            case MyApplication.ORDER_BUY:

                //holder.tvActionValue.setText(context.getResources().getString(R.string.buy));
                holder.tvActionValue.setTextColor(ContextCompat.getColor(context, R.color.green_color));
                break;

            case MyApplication.ORDER_SELL:

               // holder.tvActionValue.setText(context.getResources().getString(R.string.sell));
                holder.tvActionValue.setTextColor(ContextCompat.getColor(context, R.color.red_color));
                break;

            default:
                holder.tvActionValue.setTextColor(ContextCompat.getColor(context, R.color.colorValues));

        }}catch (Exception e){}



        Actions.setTypeface(new TextView[]{holder.tvSymbolValue,holder.tvActionValue,holder.tvQtyExecutedValue,holder.tvAvgPriceValue}, MyApplication.giloryBold);



    }

    @Override
    public int getItemCount() {
        return allExecuted.size();
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
        TextView tvSymbolValue,tvActionValue,tvQtyExecutedValue,tvAvgPriceValue;


        public ItemViewHolder(View v) {
            super(v);
            this.v = v;
            this.v.setOnClickListener(this);
            this.lllayout = v.findViewById(R.id.lllayout);
            this.tvSymbolValue = v.findViewById(R.id.tvSymbolValue);
            this.tvActionValue = v.findViewById(R.id.tvActionValue);
            this.tvQtyExecutedValue = v.findViewById(R.id.tvQtyExecutedValue);
            this.tvAvgPriceValue = v.findViewById(R.id.tvAvgPriceValue);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }
}
