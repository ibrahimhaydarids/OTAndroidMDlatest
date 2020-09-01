package com.ids.fixot.adapters;





import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.OrderInfo;


import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import static com.ids.fixot.MyApplication.lang;



public class OrderInfoRecyclerAdapter extends RecyclerView.Adapter<OrderInfoRecyclerAdapter.RecyclerViewHolder> {

    private ArrayList<OrderInfo> infos;
    private Activity context;

    public OrderInfoRecyclerAdapter(Activity context, ArrayList<OrderInfo> info) {
        this.infos = info;
        this.context = context;
    }


    public static void overrideFonts(Context context, final View v, boolean isMowazi) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child, isMowazi);
                }
            } else if (v instanceof TextView) {
                if (lang == MyApplication.ARABIC) {
                    ((TextView) v).setTypeface(MyApplication.droidbold);
                } else {
                    ((TextView) v).setTypeface(MyApplication.giloryBold);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

        holder.tvTitle.setText(infos.get(position).getKey());
        holder.tvValue.setText(infos.get(position).getValue());

        if (position % 2 == 1)
           holder.llItems.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));
        else
           holder.llItems.setBackgroundColor(ContextCompat.getColor(context, R.color.white));


        overrideFonts(context, holder.tvTitle, false);



    }

    @Override
    public int getItemCount() {
        return this.infos.size();
    }

    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView;

        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_info, parent, false);
        RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
        return (rcv);
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle, tvValue;
        protected View itemView;
        LinearLayout llItems;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            llItems = itemView.findViewById(R.id.llItem);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvValue = itemView.findViewById(R.id.tvValue);

        }

    }
}