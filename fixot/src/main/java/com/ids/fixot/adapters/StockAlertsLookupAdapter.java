package com.ids.fixot.adapters;


import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;

public class StockAlertsLookupAdapter extends ArrayAdapter<com.ids.fixot.model.Lookups> {

    Boolean showArrow;
    private Activity context;
    private ArrayList<com.ids.fixot.model.Lookups> Lookups;
    private LayoutInflater inflter;

    public StockAlertsLookupAdapter(Activity applicationContext, ArrayList<com.ids.fixot.model.Lookups> Lookups, Boolean showArrows) {

        super(applicationContext, R.layout.item_ordertype_spinner);
        this.context = applicationContext;
        this.Lookups = Lookups;
        this.showArrow = showArrows;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return Lookups.size();
    }

    @Override
    public com.ids.fixot.model.Lookups getItem(int i) {
        return Lookups.get(i);
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
            tvItem.setText((Lookups.get(i).getDescriptionAr()));
        else
            tvItem.setText(Lookups.get(i).getDescriptionEn());
        // try{tvItem.setText(Actions.getStringFromValue(Lookups.get(i).getOrderTypeID()));}catch (Exception e){}
        tvItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));

        Actions.overrideFonts(context, llItem, false);
        return view;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_ordertype_spinner, null);

        TextView tvItem = view.findViewById(R.id.tvItem);
        LinearLayout llItem = view.findViewById(R.id.llItem);
        if (MyApplication.lang == MyApplication.ARABIC)
            tvItem.setText(Lookups.get(i).getDescriptionAr());
        else
            tvItem.setText(Lookups.get(i).getDescriptionEn());
        tvItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));


        Actions.overrideFonts(context, llItem, false);
        return view;
    }

}