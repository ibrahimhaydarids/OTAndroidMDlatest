package com.ids.fixot.activities;


import android.app.Activity;
import android.content.Intent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.StockActivity;
import com.ids.fixot.classes.ResponseRequestTickets;


import java.util.ArrayList;

import me.grantland.widget.AutofitHelper;

/**
 * Created by Amal on 3/31/2017.
 */

public class RequestRecyclerAdapter extends RecyclerView.Adapter<RequestRecyclerAdapter.ViewHolder> {


    private ArrayList<ResponseRequestTickets> allRequests;
    private Activity context;
    private RequestRecyclerAdapter.RecyclerViewOnItemClickListener itemClickListener;

    public RequestRecyclerAdapter(Activity context, ArrayList<ResponseRequestTickets> allRequests,RequestRecyclerAdapter.RecyclerViewOnItemClickListener itemClickListener) {
        this.context = context;
        this.allRequests = allRequests;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RequestRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.request_recycler_item, viewGroup, false);
        return new RequestRecyclerAdapter.ItemViewHolder(v);


    }

    @Override
    public void onBindViewHolder(RequestRecyclerAdapter.ViewHolder viewHolder, final int position) {


        RequestRecyclerAdapter.ItemViewHolder holder = (RequestRecyclerAdapter.ItemViewHolder) viewHolder;

        Actions.overrideFonts(context, holder.lllayout, false);
        try {

            String price = Actions.formatNumber(Double.parseDouble(allRequests.get(position).getAmount()), Actions.FourDecimal);
            holder.tvAmountValue.setText(price);
        } catch (Exception e) {
            e.printStackTrace();
            //holder.tvIndexValue.setText(String.valueOf(allRequests.get(position).getValue()));
        }

        try {
            holder.tvTypeValue.setText(allRequests.get(position).getTypeDescription());
        } catch (Exception e) {
            e.printStackTrace();
            //holder.tvIndexValue.setText(String.valueOf(allRequests.get(position).getValue()));
        }

        try {
            holder.tvDateValue.setText(allRequests.get(position).getTicketDate());
        } catch (Exception e) {
            e.printStackTrace();
            //holder.tvIndexValue.setText(String.valueOf(allRequests.get(position).getValue()));
        }

        try {
            holder.tvStatusValue.setText(allRequests.get(position).getStatusDescription());
        } catch (Exception e) {
            e.printStackTrace();
            //holder.tvIndexValue.setText(String.valueOf(allRequests.get(position).getValue()));
        }

        try{
        if(allRequests.get(position).getStatusID()==1){
            holder.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.green_color));
        }else {
            holder.tvStatusValue.setTextColor(ContextCompat.getColor(context, R.color.red_color));
        }}catch (Exception e){}

        try{
        if(allRequests.get(position).getCanCancel()){
            holder.btCancelRequest.setVisibility(View.VISIBLE);
        }else {
            holder.btCancelRequest.setVisibility(View.INVISIBLE);
        }}catch (Exception e){
            holder.btCancelRequest.setVisibility(View.INVISIBLE);
        }

        AutofitHelper.create(holder.tvAmountValue);
        AutofitHelper.create(holder.tvStatusValue);
        AutofitHelper.create(holder.tvDateValue);


        Actions.setTypeface(new TextView[]{holder.tvTypeValue, holder.tvAmountValue,holder.tvDateValue,holder.tvStatusValue}, MyApplication.giloryBold);

        if (position % 2 == 0) {
            holder.lllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
        } else {
            holder.lllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));
        }

    }

    @Override
    public int getItemCount() {
        return allRequests.size();
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

    public class ItemViewHolder extends RequestRecyclerAdapter.ViewHolder implements View.OnClickListener {

        protected TextView tvTypeValue, tvAmountValue, tvDateValue,tvStatusValue;
        protected Button btCancelRequest;
        protected View v;
        LinearLayout lllayout;

        public ItemViewHolder(View v) {
            super(v);
            this.v = v;


            this.tvTypeValue = v.findViewById(R.id.tvTypeValue);
            this.tvAmountValue = v.findViewById(R.id.tvAmountValue);
            this.tvDateValue = v.findViewById(R.id.tvDateValue);
            this.tvStatusValue = v.findViewById(R.id.tvStatusValue);
            this.btCancelRequest = v.findViewById(R.id.btCancelRequest);
            this.lllayout = v.findViewById(R.id.lllayout);
            this.btCancelRequest.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }
}
