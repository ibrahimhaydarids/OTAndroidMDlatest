package com.ids.fixot.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.SubAccount;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;

public class SubAccountsSpinnerAdapter extends ArrayAdapter<SubAccount> {

    private Activity context;
    private ArrayList<SubAccount> subAccounts;
    private LayoutInflater inflter;

    public SubAccountsSpinnerAdapter(Activity applicationContext, ArrayList<SubAccount> allSectors) {

        super(applicationContext, R.layout.item_sub_account_new);
        this.context = applicationContext;
        this.subAccounts = allSectors;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return subAccounts.size();
    }

    @Override
    public SubAccount getItem(int i) {
        return subAccounts.get(i);
    }

    public ArrayList<SubAccount> getSubAccounts() {
        return subAccounts;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.item_sub_account_new, null);


        RelativeLayout llItem = view.findViewById(R.id.llItem);
        LinearLayout linearHeader = view.findViewById(R.id.linearHeader);
        LinearLayout linearValues = view.findViewById(R.id.linearValues);
        linearHeader.setVisibility(View.GONE);


        TextView tvIdTitle = view.findViewById(R.id.tvIdTitle);
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvPortfolioName = view.findViewById(R.id.tvPortfolioName);
        TextView tvIdValue = view.findViewById(R.id.tvIdValue);
        TextView tvNameValue = view.findViewById(R.id.tvNameValue);
        TextView tvPortfolioNameValue = view.findViewById(R.id.tvPortfolioNameValue);
        if(!BuildConfig.Enable_Markets){
        tvIdValue.setVisibility(View.GONE);
        tvNameValue.setVisibility(View.GONE);
        }else {
            tvIdValue.setVisibility(View.VISIBLE);
            tvNameValue.setVisibility(View.VISIBLE);
        }


        try{tvIdValue.setText(subAccounts.get(i).getInvestorId());}catch (Exception e){}
        try{tvNameValue.setText((MyApplication.lang == MyApplication.ARABIC ? subAccounts.get(i).getNameAr() : subAccounts.get(i).getName()));}catch (Exception e){}
      /*  try{
            tvPortfolioNameValue.setText(subAccounts.get(i).getPortfolioName());
        }catch (Exception e){}*/


        if(!BuildConfig.Enable_Markets) {
            try {
                tvPortfolioNameValue.setText((MyApplication.lang == MyApplication.ARABIC ? subAccounts.get(i).getPortfolioNameAr() : subAccounts.get(i).getPortfolioName()));
            } catch (Exception e) {
            }
        }else {
            try {
                tvPortfolioNameValue.setText(subAccounts.get(i).getPortfolioName());
            } catch (Exception e) {
            }
        }


        /*if (i%2 == 0 ){
            llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.white  : R.color.colorDarkTheme));
        }else{
            llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.colorLight  : R.color.colorLightInv));
        }*/

        Actions.overrideFonts(context, llItem, false);
        Actions.setTypeface(new TextView[]{tvIdTitle, tvName,tvPortfolioName}, MyApplication.giloryBold);
        return view;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_sub_account_dropdown, null);


        RelativeLayout llItem = view.findViewById(R.id.llItem);

        LinearLayout linearValues = view.findViewById(R.id.linearValues);

        TextView tvIdValue = view.findViewById(R.id.tvIdValue);
        TextView tvNameValue = view.findViewById(R.id.tvNameValue);
        TextView tvPortfolioNameValue = view.findViewById(R.id.tvPortfolioNameValue);

        if(!BuildConfig.Enable_Markets){
        tvIdValue.setVisibility(View.GONE);
        tvNameValue.setVisibility(View.GONE);}else {

            tvIdValue.setVisibility(View.VISIBLE);
            tvNameValue.setVisibility(View.VISIBLE);
        }


        try{tvIdValue.setText(subAccounts.get(i).getInvestorId());}catch (Exception e){}
        try{tvNameValue.setText((MyApplication.lang == MyApplication.ARABIC ? subAccounts.get(i).getNameAr() : subAccounts.get(i).getName()));}catch (Exception e){}

        if(!BuildConfig.Enable_Markets) {
            try {
                tvPortfolioNameValue.setText((MyApplication.lang == MyApplication.ARABIC ? subAccounts.get(i).getPortfolioNameAr() : subAccounts.get(i).getPortfolioName()));
            } catch (Exception e) {
            }
        }else {
            try {
                tvPortfolioNameValue.setText(subAccounts.get(i).getPortfolioName());
            } catch (Exception e) {
            }
        }



        if(MyApplication.selectedSubAccount.getPortfolioId()==subAccounts.get(i).getPortfolioId())
            llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.yellow  : R.color.yellow));
        else
            llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.transparent  : R.color.transparent));


        /*if (i%2 == 0 ){
            llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.white  : R.color.colorDarkTheme));
        }else{
            llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.colorLight  : R.color.colorLightInv));
        }*/

        Actions.overrideFonts(context, llItem, false);
       // Actions.setTypeface(new TextView[]{tvIdTitle, tvName,tvPortfolioName}, MyApplication.giloryBold);

        return view;
    }
}
