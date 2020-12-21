package com.ids.fixot.adapters;


import android.app.Activity;
import android.content.Intent;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.TradesActivity;
import com.ids.fixot.model.StockSummary;

import java.util.ArrayList;

/**
 * Created by Amal on 7/25/2017.
 */

public class PortfolioStockNewAdapter extends RecyclerView.Adapter<PortfolioStockNewAdapter.RecyclerTickerViewHolder> {

    private ArrayList<StockSummary> allObjects;
    private Activity context;
    private PortfolioStockNewAdapter.RecyclerViewOnItemClickListener itemClickListener;


    public PortfolioStockNewAdapter(Activity context, ArrayList<StockSummary> allObjects, RecyclerViewOnItemClickListener itemClickListener) {
        this.allObjects = allObjects;
        this.context = context;
        this.itemClickListener = itemClickListener;

    }

    public RecyclerTickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = null;

        if(BuildConfig.Enable_Markets) {
            if (viewType == 0)
                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio_stock_header, parent, false);
            else if (viewType == 2)
                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio_stock_item, parent, false);
            else
                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nodatalayout, parent, false);
        }else {
            if (viewType == 0)
                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio_gig_header, parent, false);
            else if (viewType == 2)
                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio_item_gig, parent, false);
            else
                layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nodatalayout, parent, false);
        }
        RecyclerTickerViewHolder rcv = new RecyclerTickerViewHolder(layoutView);

        return rcv;
    }

    @Override
    public int getItemViewType(int position) {

        return position == 0 ? 0 : 2;
    }

    @Override
    public void onBindViewHolder(PortfolioStockNewAdapter.RecyclerTickerViewHolder holder, final int position) {

        if (position > 0) { //stock item_spinner
          final StockSummary stock = allObjects.get(position);

          try{
              holder.trades_button.setOnClickListener(v -> {
                  context.startActivity(new Intent(context, TradesActivity.class)
                          .putExtra("stockID", Integer.parseInt(stock.getStockId()))
                          .putExtra("action", MyApplication.ORDER_SELL));
              });
              holder.trades_button.setTypeface(MyApplication.giloryBold);

          }catch (Exception e){}

          if(BuildConfig.Enable_Markets) {

              try {


                  holder.symbol_title.setText(MyApplication.lang == MyApplication.ARABIC ? stock.getSymbolAr() : stock.getSymbolEn());
                  holder.symbol_code.setText(stock.getSecurityId()); //getStockId
                  holder.quantity_cost_title.setText(String.valueOf(stock.getShareCount()));
                  holder.volume_cost_title.setText(stock.getTotalMarket());
                  holder.unrealized_title.setText(String.valueOf(stock.getUnrealized()));
                  holder.unrealized_title.setTextColor(Actions.textColor(String.valueOf(stock.getUnrealized())));
                  holder.unrealized_percent.setText(stock.getUnrealizedPercent());
                  holder.unrealized_percent.setTextColor(Actions.textColor(stock.getUnrealizedPercent()));

                  String bidNumber = stock.getAverageCost() + "";
                  holder.bid_ask_title.setText(bidNumber);
                  String last = stock.getLast() + "";
                  holder.tvLast.setText(last);

              } catch (Exception e) {
                  e.printStackTrace();
              }

              if (position % 2 == 0) {
                  holder.rlItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));

              } else {
                  holder.rlItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
              }

              Actions.overrideFonts(context, holder.rlItem, false);
              holder.symbol_title.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
              holder.symbol_code.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidregular : MyApplication.giloryItaly);
              holder.tvLast.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidregular : MyApplication.giloryItaly);

              Actions.setTypeface(new TextView[]{holder.quantity_cost_title, holder.bid_ask_title, holder.unrealized_title}
                      , MyApplication.giloryBold);

              Actions.setTypeface(new TextView[]{holder.volume_cost_title, holder.unrealized_percent}
                      , MyApplication.giloryItaly);

          }else {

              try{
                  if (position % 2 == 0) {
                      holder.rlItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));

                  } else {
                      holder.rlItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
                  }




              }catch (Exception e){}

              Actions.setTypeface(new TextView[]{holder.tvColum1Value, holder.tvColum2Value,holder.tvColum3Value,holder.tvColum4Value,holder.tvColum5Value},
                      MyApplication.lang == MyApplication.ENGLISH ? MyApplication.giloryBold : MyApplication.droidbold);

              Actions.setTypeface(new TextView[]{holder.tvColum1_2Value, holder.tvColum2_2Value,holder.tvColum3_2Value,holder.tvColum4_2Value,holder.tvColum5_2Value},
                      MyApplication.lang == MyApplication.ENGLISH ? MyApplication.giloryBold : MyApplication.droidbold);


             // if(MyApplication.STOCK_COLUMN_1==MyApplication.STOCK_TYPE_STOCK_NAME){
                  holder.tvColum1Value.setText(MyApplication.lang == MyApplication.ARABIC ? stock.getNameAr() : stock.getNameEn());

             //  }else {
                  holder.tvColum1_2Value.setText(MyApplication.lang == MyApplication.ARABIC ? stock.getSymbolAr() : stock.getSymbolEn());

             // }


/*
              holder.symbol_title.setText(MyApplication.lang == MyApplication.ARABIC ? stock.getSymbolAr() : stock.getSymbolEn());
              holder.symbol_code.setText(stock.getSecurityId()); //getStockId
              holder.quantity_cost_title.setText(String.valueOf(stock.getShareCount()));
              holder.volume_cost_title.setText(stock.getTotalMarket());
              holder.unrealized_title.setText(String.valueOf(stock.getUnrealized()));
              holder.unrealized_title.setTextColor(Actions.textColor(String.valueOf(stock.getUnrealized())));
              holder.unrealized_percent.setText(stock.getUnrealizedPercent());
              holder.unrealized_percent.setTextColor(Actions.textColor(stock.getUnrealizedPercent()));

              String bidNumber = stock.getAverageCost() + "";
              holder.bid_ask_title.setText(bidNumber);
              String last = stock.getLast() + "";
              holder.tvLast.setText(last);*/


try{
            //  if(MyApplication.STOCK_COLUMN_2==MyApplication.STOCK_TYPE_STOCK_NAME){
                  holder.tvColum2Value.setText(String.valueOf(stock.getShareCount()));

             // }else {
                  holder.tvColum2_2Value.setText(String.valueOf(stock.getAvailableShares()));
             // }

} catch (Exception e){}


              try{
                //  if(MyApplication.STOCK_COLUMN_3==MyApplication.PORTFOLIO_MARKET){
                      holder.tvColum3Value.setText(String.valueOf(stock.getLast()));

                //  }else {
                      holder.tvColum3_2Value.setText(String.valueOf(stock.getAverageCost()));
                 // }
              } catch (Exception e){}



       /*       try{
                  if(Double.parseDouble(stock.getLast().replaceAll(",","").trim())>0){
                      holder.tvColum3Value.setTextColor(ContextCompat.getColor(context, R.color.green_color));
                  }else  if(Double.parseDouble(stock.getLast().replaceAll(",","").trim())<0){
                      holder.tvColum3Value.setTextColor(ContextCompat.getColor(context, R.color.red_color));
                  }else {
                      holder.tvColum3Value.setTextColor(ContextCompat.getColor(context, R.color.orange));
                  }
              }catch (Exception e){

              }*/



              try{
                 // if(MyApplication.STOCK_COLUMN_4==MyApplication.PORTFOLIO_MARKET_VALUE){
                      holder.tvColum4Value.setText(String.valueOf(stock.getTotalMarket()));

                //  }else {
                      holder.tvColum4_2Value.setText(String.valueOf(stock.getTotalCost()));
                //  }
              } catch (Exception e){}



     /*         try{
                  if(Double.parseDouble(stock.getTotalMarket().replaceAll(",","").trim())>0){
                      holder.tvColum4Value.setTextColor(ContextCompat.getColor(context, R.color.green_color));
                  }else  if(Double.parseDouble(stock.getTotalMarket().replaceAll(",","").trim())<0){
                      holder.tvColum4Value.setTextColor(ContextCompat.getColor(context, R.color.red_color));
                  }else {
                      holder.tvColum4Value.setTextColor(ContextCompat.getColor(context, R.color.orange));
                  }
              }catch (Exception e){

              }*/


              try{
                //  if(MyApplication.STOCK_COLUMN_5==MyApplication.PORTFOLIO_BALANCE_PRICE){
                      holder.tvColum5Value.setText(String.valueOf(stock.getUnrealized()));



                //  }else {
                      holder.tvColum5_2Value.setText(String.valueOf(stock.getBreakPrice()));

                //  }
              } catch (Exception e){}


              Log.wtf("unrealize","aaa"+stock.getUnrealizedPercent().replaceAll("%","").replaceAll(",","").trim());

              try{
                  if(Double.parseDouble(stock.getUnrealizedPercent().replaceAll("%","").replaceAll(",","").trim())>0){
                      holder.tvColum5Value.setTextColor(ContextCompat.getColor(context, R.color.green_color));
                      holder.tvColum4Value.setTextColor(ContextCompat.getColor(context, R.color.green_color));
                      holder.tvColum3Value.setTextColor(ContextCompat.getColor(context, R.color.green_color));
                  }else  if(Double.parseDouble(stock.getUnrealizedPercent().replaceAll("%","").replaceAll(",","").trim())<0){
                      holder.tvColum5Value.setTextColor(ContextCompat.getColor(context, R.color.red_color));
                      holder.tvColum4Value.setTextColor(ContextCompat.getColor(context, R.color.red_color));
                      holder.tvColum3Value.setTextColor(ContextCompat.getColor(context, R.color.red_color));
                  }else {
                      holder.tvColum5Value.setTextColor(ContextCompat.getColor(context, R.color.orange));
                      holder.tvColum4Value.setTextColor(ContextCompat.getColor(context, R.color.orange));
                      holder.tvColum3Value.setTextColor(ContextCompat.getColor(context, R.color.orange));
                  }
              }catch (Exception e){

              }

      /*        try{
                  if(Double.parseDouble(stock.getUnrealizedPercent().replaceAll("%",""))>0){
                      holder.tvColum5Value.setTextColor(ContextCompat.getColor(context, R.color.green_color));
                  }else  if(Double.parseDouble(stock.getAverageCost())<0){
                      holder.tvColum5Value.setTextColor(ContextCompat.getColor(context, R.color.red_color));
                  }else {
                      holder.tvColum5Value.setTextColor(ContextCompat.getColor(context, R.color.colorValues));
                  }
              }catch (Exception e){}*/



           }


        } else {

            if(BuildConfig.Enable_Markets) {
                Actions.setTypeface(new TextView[]{holder.symbol_title, holder.quantity_cost_title, holder.bid_ask_title, holder.tvLast, holder.unrealized_title, holder.unrealized_title_prcnt, holder.cost_titles},
                        MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
            }else {


                   Actions.setTypeface(new TextView[]{holder.tvColumn1Title, holder.tvColumn2Title, holder.tvColumn3Title, holder.tvColumn4Title, holder.tvColumn5Title},
                           MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

                Actions.setTypeface(new TextView[]{holder.tvColumn1_2Title, holder.tvColumn2_2Title, holder.tvColumn3_2Title, holder.tvColumn4_2Title, holder.tvColumn5_2Title},
                        MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

               // if (MyApplication.STOCK_COLUMN_1 == MyApplication.STOCK_TYPE_STOCK_NAME) {
                    holder.tvColumn1Title.setText(context.getString(R.string.stocks_name));

               // } else {
                    holder.tvColumn1_2Title.setText(context.getString(R.string.stock));

               // }


                 //  if (MyApplication.STOCK_COLUMN_2 == MyApplication.PORTFOLIO_SHARES) {
                       holder.tvColumn2Title.setText(context.getString(R.string.quantity_full));
                  // } else {
                       holder.tvColumn2_2Title.setText(context.getString(R.string.available));
                 //  }

                 //  if (MyApplication.STOCK_COLUMN_3 == MyApplication.PORTFOLIO_MARKET) {
                       holder.tvColumn3Title.setText(context.getString(R.string.market_price_title));
                 //  } else {
                       holder.tvColumn3_2Title.setText(context.getString(R.string.its_cost));
                  // }

                 //  if (MyApplication.STOCK_COLUMN_4 == MyApplication.PORTFOLIO_MARKET_VALUE) {
                       holder.tvColumn4Title.setText(context.getString(R.string.market_value));
                  // } else {
                       holder.tvColumn4_2Title.setText(context.getString(R.string.portfolio_title_3_2));
                  // }

                  // if (MyApplication.STOCK_COLUMN_5 == MyApplication.PORTFOLIO_BALANCE_PRICE) {
                       holder.tvColumn5Title.setText(context.getString(R.string.ungain_loss));

                  // } else {
                       holder.tvColumn5_2Title.setText(context.getString(R.string.balance_loss));
                  // }



                  /* holder.tvColumn1Title.setOnClickListener(v -> {
                       if (MyApplication.STOCK_COLUMN_1 == MyApplication.STOCK_TYPE_STOCK_NAME) {
                           MyApplication.STOCK_COLUMN_1 = MyApplication.STOCK_TYPE_STOCK_SYMBOL;
                       } else {
                           MyApplication.STOCK_COLUMN_1 = MyApplication.STOCK_TYPE_STOCK_NAME;
                       }

                       notifyDataSetChanged();
                   });

                   holder.tvColumn2Title.setOnClickListener(v -> {
                       if (MyApplication.STOCK_COLUMN_2 == MyApplication.PORTFOLIO_SHARES) {
                           MyApplication.STOCK_COLUMN_2=MyApplication.PORTFOLIO_AVAILABLE;
                       } else {
                           MyApplication.STOCK_COLUMN_2=MyApplication.PORTFOLIO_SHARES;
                       }
                       notifyDataSetChanged();
                   });


                   holder.tvColumn3Title.setOnClickListener(v -> {

                       if (MyApplication.STOCK_COLUMN_3 == MyApplication.PORTFOLIO_MARKET) {
                           MyApplication.STOCK_COLUMN_3=MyApplication.PORTFOLIO_AVG_COST;
                       } else {
                           MyApplication.STOCK_COLUMN_3=MyApplication.PORTFOLIO_MARKET;
                       }
                       notifyDataSetChanged();
                   });

                   holder.tvColumn4Title.setOnClickListener(v -> {
                       if (MyApplication.STOCK_COLUMN_4 == MyApplication.PORTFOLIO_MARKET_VALUE) {
                           MyApplication.STOCK_COLUMN_4=MyApplication.PORTFOLIO_COST_VALUE;
                       } else {
                           MyApplication.STOCK_COLUMN_4=MyApplication.PORTFOLIO_MARKET_VALUE;
                       }
                       notifyDataSetChanged();
                   });

                   holder.tvColumn5Title.setOnClickListener(v -> {
                       if (MyApplication.STOCK_COLUMN_5 == MyApplication.PORTFOLIO_BALANCE_PRICE) {
                         MyApplication.STOCK_COLUMN_5=MyApplication.PORTFOLIO_GAIN_LOSS;
                       } else {
                           MyApplication.STOCK_COLUMN_5=MyApplication.PORTFOLIO_BALANCE_PRICE;
                       }
                       notifyDataSetChanged();
                   });*/

               }



        }

        if (position == 0 && MyApplication.lang == MyApplication.ENGLISH) {
            if(BuildConfig.Enable_Markets)
               holder.symbol_title.setPadding(10, 10, 10, 10);
        }

    }

    private void setTitles(){

    }

    @Override
    public int getItemCount() {
        return this.allObjects.size();
    }

    public interface RecyclerViewOnItemClickListener {
        void onItemClicked(View v, int position);
    }

    public class RecyclerTickerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected View itemView;
        TextView unrealized_title, unrealized_percent, bid_ask_title, tvLast, quantity_cost_title, volume_cost_title, symbol_title, symbol_code, unrealized_title_prcnt, cost_titles;
        TextView rem_cost, due_date, cost, b_price, org_qtty, rem_qtty, num, date, symbol;
        Button trades_button;
        private LinearLayout rlitem, rlItem,rlInsideItem;

        TextView tvColum1Value,tvColum2Value,tvColum3Value,tvColum4Value,tvColum5Value;
        TextView tvColum1_2Value,tvColum2_2Value,tvColum3_2Value,tvColum4_2Value,tvColum5_2Value;
        TextView tvColumn1Title,tvColumn2Title,tvColumn3Title,tvColumn4Title,tvColumn5Title;
        TextView tvColumn1_2Title,tvColumn2_2Title,tvColumn3_2Title,tvColumn4_2Title,tvColumn5_2Title;

        //define and instantiate the constituents of the viewHolder (the gridItem)
        public RecyclerTickerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.itemView.setOnClickListener(this);


            try{trades_button = itemView.findViewById(R.id.trades_button);}catch (Exception e){}
            try{unrealized_title = itemView.findViewById(R.id.unrealized_title);}catch (Exception e){}
            try{unrealized_percent = itemView.findViewById(R.id.unrealized_percent);}catch (Exception e){}
            try{rlItem = itemView.findViewById(R.id.rlItem);}catch (Exception e){}
            try{rlitem = itemView.findViewById(R.id.linear_layout);}catch (Exception e){}
            try{bid_ask_title = itemView.findViewById(R.id.bid_ask_title);}catch (Exception e){}
            try{tvLast = itemView.findViewById(R.id.tvLast);}catch (Exception e){}
            try{ quantity_cost_title = itemView.findViewById(R.id.quantity_cost_title);}catch (Exception e){}
            try{volume_cost_title = itemView.findViewById(R.id.volume_cost_title);}catch (Exception e){}
            try{symbol_title = itemView.findViewById(R.id.symbol_title);}catch (Exception e){}
            try{symbol_code = itemView.findViewById(R.id.symbol_code);}catch (Exception e){}
            try{rem_cost = itemView.findViewById(R.id.rem_cost);}catch (Exception e){}
            try{unrealized_title_prcnt = itemView.findViewById(R.id.unrealized_title_prcnt);}catch (Exception e){}
            try{cost_titles = itemView.findViewById(R.id.cost_titles);}catch (Exception e){}
            try{cost = itemView.findViewById(R.id.cost);}catch (Exception e){}
            try{due_date = itemView.findViewById(R.id.due_date);}catch (Exception e){}
            try{b_price = itemView.findViewById(R.id.b_price);}catch (Exception e){}
            try{ org_qtty = itemView.findViewById(R.id.org_qtty);}catch (Exception e){}
            try{rem_qtty = itemView.findViewById(R.id.rem_qtty);}catch (Exception e){}
            try{num = itemView.findViewById(R.id.num);}catch (Exception e){}
            try{date = itemView.findViewById(R.id.date);}catch (Exception e){}
            try{symbol = itemView.findViewById(R.id.symbol);}catch (Exception e){}



            try{this.tvColum1Value = itemView.findViewById(R.id.tvColum1Value);}catch (Exception e){}
            try{this.tvColum2Value = itemView.findViewById(R.id.tvColum2Value);}catch (Exception e){}
            try{this.tvColum3Value = itemView.findViewById(R.id.tvColum3Value);}catch (Exception e){}
            try{this.tvColum4Value = itemView.findViewById(R.id.tvColum4Value);}catch (Exception e){}
            try{this.tvColum5Value = itemView.findViewById(R.id.tvColum5Value);}catch (Exception e){}


            try{this.tvColum1_2Value = itemView.findViewById(R.id.tvColum1_2Value);}catch (Exception e){}
            try{this.tvColum2_2Value = itemView.findViewById(R.id.tvColum2_2Value);}catch (Exception e){}
            try{this.tvColum3_2Value = itemView.findViewById(R.id.tvColum3_2Value);}catch (Exception e){}
            try{this.tvColum4_2Value = itemView.findViewById(R.id.tvColum4_2Value);}catch (Exception e){}
            try{this.tvColum5_2Value = itemView.findViewById(R.id.tvColum5_2Value);}catch (Exception e){}


            try{this.tvColumn1Title = itemView.findViewById(R.id.tvColumn1Title);}catch (Exception e){}
            try{this.tvColumn2Title = itemView.findViewById(R.id.tvColumn2Title);}catch (Exception e){}
            try{this.tvColumn3Title = itemView.findViewById(R.id.tvColumn3Title);}catch (Exception e){}
            try{this.tvColumn4Title = itemView.findViewById(R.id.tvColumn4Title);}catch (Exception e){}
            try{this.tvColumn5Title = itemView.findViewById(R.id.tvColumn5Title);}catch (Exception e){}

            try{this.tvColumn1_2Title = itemView.findViewById(R.id.tvColumn1_2Title);}catch (Exception e){}
            try{this.tvColumn2_2Title = itemView.findViewById(R.id.tvColumn2_2Title);}catch (Exception e){}
            try{this.tvColumn3_2Title = itemView.findViewById(R.id.tvColumn3_2Title);}catch (Exception e){}
            try{this.tvColumn4_2Title = itemView.findViewById(R.id.tvColumn4_2Title);}catch (Exception e){}
            try{this.tvColumn5_2Title = itemView.findViewById(R.id.tvColumn5_2Title);}catch (Exception e){}


        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(view, getLayoutPosition());
        }

    }

}
