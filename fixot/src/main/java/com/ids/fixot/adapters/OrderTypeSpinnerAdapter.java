package com.ids.fixot.adapters;


import android.app.Activity;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.Ordertypes;

import java.util.ArrayList;

public class OrderTypeSpinnerAdapter extends ArrayAdapter<Ordertypes> {

    Boolean showArrow;
    private Activity context;
    private ArrayList<Ordertypes> Orders;
    private LayoutInflater inflter;

    public OrderTypeSpinnerAdapter(Activity applicationContext, ArrayList<Ordertypes> Orders, Boolean showArrows) {

        super(applicationContext, R.layout.item_ordertype_spinner);
        this.context = applicationContext;
        this.Orders = Orders;
        this.showArrow = showArrows;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return Orders.size();
    }

    @Override
    public Ordertypes getItem(int i) {
        return Orders.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_ordertype_spinner, null);

        TextView tvItem = view.findViewById(R.id.tvItem);
        LinearLayout llItem = view.findViewById(R.id.llItem);
        tvItem.setTextSize((MyApplication.lang == MyApplication.ARABIC) ? 14 : 15);
        if (MyApplication.lang == MyApplication.ARABIC)
            tvItem.setText((Orders.get(i).getDescriptionAr()));
        else
            tvItem.setText(Orders.get(i).getDescriptionEn());
     // try{tvItem.setText(Actions.getStringFromValue(Orders.get(i).getOrderTypeID()));}catch (Exception e){}
        tvItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorValues : R.color.colorValuesInv));

        Actions.overrideFonts(context, llItem, false);
        tvItem.setTypeface(Typeface.DEFAULT_BOLD);
        return view;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_ordertype_spinner, null);

        TextView tvItem = view.findViewById(R.id.tvItem);
        LinearLayout llItem = view.findViewById(R.id.llItem);
        if (MyApplication.lang == MyApplication.ARABIC)
             tvItem.setText(Orders.get(i).getDescriptionAr());
        else
            tvItem.setText(Orders.get(i).getDescriptionEn());
        tvItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorValues : R.color.colorValues));


        Actions.overrideFonts(context, llItem, false);
        return view;
    }

}
