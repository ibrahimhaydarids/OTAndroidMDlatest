package com.ids.fixot.adapters;

import android.app.Activity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.OrderDetailsActivity;
import com.ids.fixot.activities.PortfolioActivity;
import com.ids.fixot.activities.SectorDetailActivity;
import com.ids.fixot.model.ValueItem;

import java.util.ArrayList;

/**
 * Created by DEV on 4/30/2018.
 */

public class ValuesGridRecyclerAdapter extends RecyclerView.Adapter<ValuesGridRecyclerAdapter.RecyclerTickerViewHolder> {

    private ArrayList<ValueItem> allObjects;
    private Activity context;

    public ValuesGridRecyclerAdapter(Activity context, ArrayList<ValueItem> allObjects) {
        this.allObjects = allObjects;
        this.context = context;

    }

    public RecyclerTickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = null;

        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.value_grid_item, parent, false);
        RecyclerTickerViewHolder rcv = new RecyclerTickerViewHolder(layoutView);
        return rcv;
    }


    @Override
    public void onBindViewHolder(RecyclerTickerViewHolder holder, final int position) {

        ValueItem v = allObjects.get(position);

        holder.tvTitle.setText(v.getTitle());
        holder.tvValue.setText(v.getValue());

        if (context instanceof OrderDetailsActivity) {

            holder.tvValue.setTextColor(ContextCompat.getColor(context, v.getColor()));

        } else if (context instanceof PortfolioActivity || context instanceof SectorDetailActivity) {

            //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int)context.getResources().getDimension(R.dimen.profile_item));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            holder.rlitem.setLayoutParams(params);

        }

        holder.tvTitle.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidregular : MyApplication.giloryItaly);
        holder.tvValue.setTypeface(MyApplication.giloryBold);
        //Actions.overrideFonts(context, holder.rlitem, false);

    }

    @Override
    public int getItemCount() {
        return this.allObjects.size();
    }

    public class RecyclerTickerViewHolder extends RecyclerView.ViewHolder {

        protected View itemView;
        private TextView tvTitle;
        private TextView tvValue;
        private TextView tvValue2;
        private LinearLayout rlitem;

        //define and instantiate the constituents of the viewHolder (the gridItem)
        public RecyclerTickerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            rlitem = itemView.findViewById(R.id.linear_layout);

            tvValue2 = itemView.findViewById(R.id.tvvalue2);
            tvValue = itemView.findViewById(R.id.tvValue);
        }


    }

}
