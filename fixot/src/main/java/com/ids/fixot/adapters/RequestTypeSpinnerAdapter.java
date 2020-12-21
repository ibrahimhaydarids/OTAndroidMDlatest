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
import com.ids.fixot.classes.RequestTicketTypes;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;

import me.grantland.widget.AutofitHelper;

public class RequestTypeSpinnerAdapter extends ArrayAdapter<RequestTicketTypes> {

    Boolean showArrow;
    private Activity context;
    private ArrayList<RequestTicketTypes> RequestTicketTypes;
    private LayoutInflater inflter;

    public RequestTypeSpinnerAdapter(Activity applicationContext, ArrayList<RequestTicketTypes> RequestTicketTypes, Boolean showArrows) {

        super(applicationContext, R.layout.item_ordertype_spinner);
        this.context = applicationContext;
        this.RequestTicketTypes = RequestTicketTypes;
        this.showArrow = showArrows;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return RequestTicketTypes.size();
    }

    @Override
    public RequestTicketTypes getItem(int i) {
        return RequestTicketTypes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_check_request_spinner, null);

        TextView tvItem = view.findViewById(R.id.tvItem);
        AutofitHelper.create(tvItem);
        LinearLayout llItem = view.findViewById(R.id.llItem);
        tvItem.setTextSize((MyApplication.lang == MyApplication.ARABIC) ? 14 : 15);
        if (MyApplication.lang == MyApplication.ARABIC)
            tvItem.setText((RequestTicketTypes.get(i).getDescriptionAr()));
        else
            tvItem.setText(RequestTicketTypes.get(i).getDescriptionEn());
        // try{tvItem.setText(Actions.getStringFromValue(RequestTicketTypes.get(i).getOrderTypeID()));}catch (Exception e){}
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
            tvItem.setText(RequestTicketTypes.get(i).getDescriptionAr());
        else
            tvItem.setText(RequestTicketTypes.get(i).getDescriptionEn());
        tvItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));


        Actions.overrideFonts(context, llItem, false);
        return view;
    }

}
