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


import java.util.ArrayList;

/**
 * Created by Amal on 3/31/2017.
 */

public class BalanceSummaryAdapter extends RecyclerView.Adapter<BalanceSummaryAdapter.ViewHolder> {


    private ArrayList<BalanceSummaryParent> allBalances;
    private Activity context;


    public BalanceSummaryAdapter(Activity context, ArrayList<BalanceSummaryParent> allBalances) {
        this.context = context;
        this.allBalances = allBalances;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_balance_summary, viewGroup, false);
        return new ItemViewHolder(v);


    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {


        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        Actions.overrideFonts(context, holder.lllayout, false);



        try {
            holder.tvSectionTitle.setText(allBalances.get(position).getKey());
        } catch (Exception e) {
            e.printStackTrace();

        }
        try{
            BalanceSummaryChildAdapter childAdapter=new BalanceSummaryChildAdapter(context,allBalances.get(position).getArraySummary());
            GridLayoutManager glm=new GridLayoutManager(context,1);
            holder.rvBalanceData.setLayoutManager(glm);
            holder.rvBalanceData.setAdapter(childAdapter);
        }catch (Exception e){}






        Actions.setTypeface(new TextView[]{holder.tvSectionTitle}, MyApplication.giloryBold);

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

        protected TextView tvSecto;

        protected View v;
        LinearLayout lllayout;
        TextView tvSectionTitle;
        RecyclerView rvBalanceData;

        public ItemViewHolder(View v) {
            super(v);
            this.v = v;
            this.v.setOnClickListener(this);
            this.lllayout = v.findViewById(R.id.lllayout);
            this.tvSectionTitle = v.findViewById(R.id.tvSectionTitle);
            this.rvBalanceData = v.findViewById(R.id.rvBalanceData);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
