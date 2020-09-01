package com.ids.fixot.adapters;

import android.app.Activity;
import android.content.Intent;
import androidx.core.content.ContextCompat;
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
import com.ids.fixot.model.BalanceSummary;
import com.ids.fixot.model.BalanceSummaryParent;


import java.util.ArrayList;

/**
 * Created by Amal on 3/31/2017.
 */

public class BalanceSummaryChildAdapter extends RecyclerView.Adapter<BalanceSummaryChildAdapter.ViewHolder> {


    private ArrayList<BalanceSummary> allBalances;
    private Activity context;


    public BalanceSummaryChildAdapter(Activity context, ArrayList<BalanceSummary> allBalances) {
        this.context = context;
        this.allBalances = allBalances;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_balance_child, viewGroup, false);
        return new ItemViewHolder(v);


    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {


        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        Actions.overrideFonts(context, holder.lllayoutChild, false);



        try {
            holder.tvTitle.setText(allBalances.get(position).getKey());
        } catch (Exception e) {
            e.printStackTrace();

        }
        try {
            holder.tvValue.setText(allBalances.get(position).getValue());
        } catch (Exception e) {
            e.printStackTrace();

        }

        try {
            holder.tvSymbolSign.setText(allBalances.get(position).getSymbol());
        } catch (Exception e) {
            e.printStackTrace();

        }



        Actions.setTypeface(new TextView[]{holder.tvTitle,holder.tvValue,holder.tvSymbolSign}, MyApplication.giloryBold);

/*        if (position % 2 == 0) {
            holder.lllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
        } else {
            holder.lllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));
        }*/

    }

    @Override
    public int getItemCount() {
        return allBalances.size();
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
        LinearLayout lllayoutChild;
        TextView tvTitle,tvValue,tvSymbolSign;


        public ItemViewHolder(View v) {
            super(v);
            this.v = v;
            this.v.setOnClickListener(this);
            this.lllayoutChild = v.findViewById(R.id.lllayoutChild);
            this.tvTitle = v.findViewById(R.id.tvTitle);
            this.tvValue = v.findViewById(R.id.tvValue);
            this.tvSymbolSign = v.findViewById(R.id.tvSymbolSign);

        }

        @Override
        public void onClick(View view) {
            
        }
    }
}
