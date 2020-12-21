package com.ids.fixot;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ids.fixot.classes.ReportPageSetup;
import com.ids.fixot.classes.RequestTicketTypes;
import com.ids.fixot.classes.ResponseRequestTickets;
import com.ids.fixot.model.ApplicationList;
import com.ids.fixot.model.BalanceSummary;
import com.ids.fixot.model.BrokerageFee;
import com.ids.fixot.model.CashSummary;
import com.ids.fixot.model.ChartData;
import com.ids.fixot.model.ChartValue;
import com.ids.fixot.model.ExecutedOrders;
import com.ids.fixot.model.ForwardContract;
import com.ids.fixot.model.Instrument;
import com.ids.fixot.model.Lookups;
import com.ids.fixot.model.MarginPayment;
import com.ids.fixot.model.MarketStatus;
import com.ids.fixot.model.NewsItem;
import com.ids.fixot.model.NotificationItem;
import com.ids.fixot.model.NotificationSettings;
import com.ids.fixot.model.OffMarketQuotes;
import com.ids.fixot.model.OnlineOrder;
import com.ids.fixot.model.OrderDurationType;
import com.ids.fixot.model.Ordertypes;
import com.ids.fixot.model.Parameter;
import com.ids.fixot.model.Portfolio;
import com.ids.fixot.model.Sector;
import com.ids.fixot.model.Stock;
import com.ids.fixot.model.StockAlerts;
import com.ids.fixot.model.StockOrderBook;
import com.ids.fixot.model.StockQuotation;
import com.ids.fixot.model.StockSummary;
import com.ids.fixot.model.SubAccount;
import com.ids.fixot.model.SubscriberUser;
import com.ids.fixot.model.TimeSale;
import com.ids.fixot.model.Trade;
import com.ids.fixot.model.Unit;
import com.ids.fixot.model.User;
import com.ids.fixot.model.ValueItem;
import com.ids.fixot.model.WebItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Amal on 3/9/2017.
 * ِEdit by MK on 31/1/2019 :).
 */

public class GlobalFunctions {

    public static ArrayList<BrokerageFee> GetBrokerageFeeList(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        JSONObject object = new JSONObject(JsonString);

        ArrayList<BrokerageFee> brokerageFees = new ArrayList<>();

        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");
        if (success.equals("Success")) {

            try {

                JSONArray jsonArray = object.getJSONArray("BrokerageFees");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject json_data = jsonArray.getJSONObject(i);
                    BrokerageFee brokerageFee = new BrokerageFee();

                    try {
                        brokerageFee.setBrokerageFeeId(json_data.getDouble("BrokerageFeeID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        brokerageFee.setBrokerageFeeId(0.0);
                    }

                    try {
                        brokerageFee.setBrokerageLimit(json_data.getDouble("BrokerageLimit"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        brokerageFee.setBrokerageLimit(0.0);
                    }

                    try {
                        brokerageFee.setBrokerageLimitDiscount(json_data.getDouble("BrokerageLimitDiscount"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        brokerageFee.setBrokerageLimitDiscount(0.0);
                    }

                    try {
                        brokerageFee.setClearing(json_data.getDouble("Clearing"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        brokerageFee.setClearing(0.0);
                    }

                    try {
                        brokerageFee.setCompanyBrokerageFree(json_data.getDouble("CompanyBrokerageFee"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        brokerageFee.setCompanyBrokerageFree(0.0);
                    }

                    try {
                        brokerageFee.setInstrumentId(json_data.getString("InstrumentId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        brokerageFee.setInstrumentId("");
                    }

                    try {
                        brokerageFee.setMarketBrokerageFee(json_data.getDouble("MarketBrokerageFee"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        brokerageFee.setMarketBrokerageFee(0.0);
                    }

                    try {
                        brokerageFee.setMarketId(json_data.getDouble("MarketID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        brokerageFee.setMarketId(0.0);
                    }

                    try {
                        brokerageFee.setMarketSegmentId(json_data.getDouble("MarketSegmentID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        brokerageFee.setMarketSegmentId(0.0);
                    }

                    try {
                        brokerageFee.setMinimumBrokerageFee(json_data.getDouble("MinimumBrokerageFee"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        brokerageFee.setMinimumBrokerageFee(0.0);
                    }

                    try {
                        brokerageFee.setTotalBrokerageFee(json_data.getDouble("TotalBrokerageFee"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        brokerageFee.setTotalBrokerageFee(0.0);
                    }

                    brokerageFees.add(brokerageFee);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return brokerageFees;
    }

    public static User GetUserInfo(String JsonString)
            throws JSONException, UnsupportedEncodingException {
        User user = new User();
        ArrayList<SubAccount> subAccounts = new ArrayList<>();

        JSONObject object = new JSONObject(JsonString);

        String msgdata = "";

        try {
            msgdata = object.getString("ResponseMessage");
        } catch (Exception e) {
            e.printStackTrace();
            msgdata = object.getString("Message");
        }
        JSONObject jsondata_msg = new JSONObject(msgdata);

        try {
            //String success = jsondata_msg.getString("MessageEn");
            String success = jsondata_msg.getString("EnglishMessage");
            user.setMessageEn(success);
        }catch (Exception e){}

        try {
          //  String successAr = jsondata_msg.getString("MessageAr");
            String successAr = jsondata_msg.getString("ArabicMessage");
            user.setMessageAr(successAr);
        }catch (Exception e){}

        int status = jsondata_msg.getInt("Status");
        user.setStatus(status);

        try {
            int clientId = jsondata_msg.getInt("ClientID");
            user.setClientID(clientId);
        } catch (Exception e) {
        }

        try {
            int moaziStatus = jsondata_msg.getInt("MowaziStatus");
            user.setMowaziStatus(moaziStatus);
        } catch (Exception e) {}


        try{
            String arabicMessage = jsondata_msg.getString("ArabicMessage");
            user.setArabicMessage(arabicMessage);
        }catch (Exception e){}

        try{
            String englishMessage = jsondata_msg.getString("EnglishMessage");
            user.setEnglishMessage(englishMessage);
        }catch (Exception e){}

        try{
            String arabicLink = jsondata_msg.getString("ArabicLink");
            user.setArabicLink(arabicLink);
        }catch (Exception e){}

        try{
            String englishLink = jsondata_msg.getString("EnglishLink");
            user.setEnglishLink(englishLink);
        }catch (Exception e){}



        try {

            try {
                user.setAllowPlacement(object.getBoolean("AllowPlacement"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setCurrencyNameAr(object.getString("CurrencyNameAr"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setCurrencyNameEn(object.getString("CurrencyNameEn"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setHasWeeklyCheck(object.getBoolean("IsRecycledAccount"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setResetPassword(object.getBoolean("IsResetPassword"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setTradingPasswordMandatory(object.getBoolean("IsTradingPasswordMandatory"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setNameAr(object.getString("NameAr"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setNameEn(object.getString("NameEn"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setPermissionId(object.getInt("PermissionId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }



            try {
                user.setParentUserId(object.getInt("ParentUserId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setUserId(object.getInt("UserId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }



            try {
                user.setClientTypeID(object.getInt("ClientTypeID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                user.setPortfolioNumber(object.getInt("PortfolioNumber"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setInvestorId(object.getInt("InvestorId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setPortfolioId(object.getInt("PortfolioId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setPreferableBankNameAr(object.getString("PreferableBankNameAr"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setGetPreferableBankNameEn(object.getString("PreferableBankNameEn"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setSessionID(object.getString("SessionID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setId(object.getInt("UserId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                user.setHasOtc(object.getBoolean("hasOtc"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setUserTypeID(object.getInt("UserTypeId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                user.setKey(object.getString("key"));
                Log.wtf("user", "setkey = " + user.getKey());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray json_sub_accounts = object.getJSONArray("SubAccounts");

            for (int j = 0; j < json_sub_accounts.length(); j++) {

                JSONObject json_data = json_sub_accounts.getJSONObject(j);
                SubAccount subAccount = new SubAccount();

                try {
                    subAccount.setPortfolioId(json_data.getInt("PortfolioID"));
                } catch (Exception e) {
                    e.printStackTrace();
                    subAccount.setPortfolioId(0);
                }

                try {
                    subAccount.setUserId(json_data.getInt("UserID"));
                } catch (Exception e) {
                    e.printStackTrace();
                    subAccount.setUserId(0);
                }


                try {
                    Log.wtf("subAccounts",json_data.getInt("MarketID")+"aaaaa");
                    subAccount.setMarketID(json_data.getInt("MarketID"));

                } catch (Exception e) {
                    e.printStackTrace();
                    subAccount.setMarketID(0);
                }

                try {
                    subAccount.setInvestorId(json_data.getString("InvestorID"));
                } catch (Exception e) {
                    e.printStackTrace();
                    subAccount.setInvestorId("");
                }

                try {
                    subAccount.setName(json_data.getString("Name"));
                } catch (Exception e) {
                    e.printStackTrace();
                    subAccount.setName("");
                }

                try {
                    subAccount.setSubAccount(json_data.getString("SubAccount"));
                } catch (Exception e) {
                    e.printStackTrace();
                    subAccount.setSubAccount("");
                }

                try {
                    subAccount.setNameAr(json_data.getString("NameAr"));
                } catch (Exception e) {
                    e.printStackTrace();
                    subAccount.setNameAr("");
                }

                try {
                    subAccount.setPortfolioName(json_data.getString("PortfolioName"));
                } catch (Exception e) {
                    e.printStackTrace();
                    subAccount.setPortfolioName("");
                }

                try {
                    subAccount.setPortfolioNameAr(json_data.getString("PortfolioNameAr"));
                } catch (Exception e) {
                    e.printStackTrace();
                    subAccount.setPortfolioName("");
                }

                try {
                    subAccount.setPortfolioNumber(json_data.getString("PortfolioNumber"));
                } catch (Exception e) {
                    e.printStackTrace();
                    subAccount.setPortfolioNumber("");
                }

                try {
                    subAccount.setDefault(json_data.getBoolean("IsDefault"));
                } catch (Exception e) {
                    e.printStackTrace();
                    subAccount.setDefault(false);
                }





                subAccounts.add(subAccount);
            }

            user.setSubAccounts(subAccounts);


        } catch (Exception e) {
            e.printStackTrace();

        }
        return user;
    }

    public static ArrayList<WebItem> GetSiteMapData(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        JSONObject object = new JSONObject(JsonString);
        ArrayList<WebItem> webItemsList = new ArrayList<>();

        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");
        if (success.equals("Success")) {
            try {
                JSONArray json_instruments = object.getJSONArray("WebSiteContents");
                for (int i = 0; i < json_instruments.length(); i++) {

                    JSONObject json_data = json_instruments.getJSONObject(i);
                    WebItem webItem = new WebItem();

                    try {
                        webItem.setID(Integer.parseInt(json_data.getString("ID")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        webItem.setTitleAr(json_data.getString("TitleAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        webItem.setTitleEn(json_data.getString("TitleEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        webItem.setContentAr(json_data.getString("ContentAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        webItem.setContentEn(json_data.getString("ContentEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    webItemsList.add(0, webItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return webItemsList;
    }


    public static SubscriberUser GetSubscriber(String JsonString) throws JSONException, UnsupportedEncodingException {
        SubscriberUser subscriber=new SubscriberUser();

        JSONObject object = new JSONObject(JsonString);
        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            MyApplication.isSubscriber=true;

            String userSubscriber = object.getString("Subscriber");
            JSONObject jsondata_suscriber = new JSONObject(userSubscriber);

            try {
                subscriber.setID(jsondata_suscriber.getInt("ID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                subscriber.setDeviceID(jsondata_suscriber.getInt("DeviceID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                subscriber.setEmail(jsondata_suscriber.getString("Email"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                subscriber.setInvestorID(jsondata_suscriber.getInt("InvestorID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                subscriber.setStatus(jsondata_suscriber.getInt("Status"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                subscriber.setKey(jsondata_suscriber.getString("key"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return subscriber;


    }




    public static ChartData GetStockChartData(String JsonString) throws JSONException, UnsupportedEncodingException {

        ChartData chartdata = new ChartData();
        JSONObject object = new JSONObject(JsonString);
        ArrayList<ChartValue> chartsv = new ArrayList<>();

        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONArray("stockQuotationSnapshotList");
                for (int i = 0; i < jarray.length(); i++) {

                    ChartValue chartv = new ChartValue();

                    JSONObject json_data = jarray.getJSONObject(i);

                    try {
                        chartv.setSecurityId(json_data.getString("SecurityID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        chartv.setSecurityId(json_data.getString(""));
                    }

                    try {
                        chartv.setDate(json_data.getString("Date"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        chartv.setValue(json_data.getDouble("Last"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    chartsv.add(chartv);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        chartdata.setChartvalues(chartsv);
        return chartdata;
    }

    public static ChartData GetChartData(boolean isHomePage, String JsonString) throws JSONException, UnsupportedEncodingException {
        ChartData chartdata = new ChartData();
        JSONObject object = new JSONObject(JsonString);
        ArrayList<ChartValue> chartsv = new ArrayList<>();

        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONArray("SectorIndexSnapshotList");
                for (int i = 0; i < jarray.length(); i++) {
                    ChartValue chartv = new ChartValue();
                    JSONObject json_data = jarray.getJSONObject(i);

                    try {
                        chartv.setDate(json_data.getString("Date"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        //chartv.setValue(isHomePage ? json_data.getInt("Value") : json_data.getInt("Last"));
                        chartv.setValue(json_data.getInt("Value"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    chartsv.add(chartv);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        chartdata.setChartvalues(chartsv);
        return chartdata;

    }

    public static String GetWebserviceUrl(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        String url = "";
        JSONObject object = new JSONObject(JsonString);
        ArrayList<WebItem> webItemsList = new ArrayList<>();

        try {
            JSONArray json_instruments = object.getJSONArray("brokers");
            for (int i = 0; i < json_instruments.length(); i++) {

                JSONObject json_data = json_instruments.getJSONObject(i);

                try {
                    if ((MyApplication.appLabel.equals("waseet") && json_data.getString("label").equals("WaseetAndroid"))
                            || (MyApplication.appLabel.equals("sharq") && json_data.getString("label").equals("SharqAndroid"))
                            || (MyApplication.appLabel.equals("tijari") && json_data.getString("label").equals("TijariAndroid"))
                            || (MyApplication.appLabel.equals("oula") && json_data.getString("label").equals("OulaAndroid"))
                            || (MyApplication.appLabel.equals("kic") && json_data.getString("label").equals("KICAndroid"))) {
                        url = json_data.getString("url");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.wtf("GetWebserviceUrl ", "error parse data : " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("GetWebserviceUrl ", "error parse data : " + e.getMessage());
        }

        return url;
    }


    public static Parameter GetParameters(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        Parameter parameter = new Parameter();
        JSONObject object = new JSONObject(JsonString);


        try {
            parameter.setAlmowaziRegistrationLink(object.getString("AlMowaziRegistrationLink").equals("null") ? "" : object.getString("AlMowaziRegistrationLink"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setForgotPasswordUrl("");
        }
        try {
            parameter.setAlmowaziPolicyLink(object.getString("AlmowaziPolicyLink").equals("null") ? "" : object.getString("AlmowaziPolicyLink"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setForgotPasswordUrl("");
        }

        try {
            parameter.setMobileReportingPath(object.getString("MobileReportingPath").equals("null") ? "" : object.getString("MobileReportingPath"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setForgotPasswordUrl("");
        }

        try {
            parameter.setEnableMowazi(object.getBoolean("EnableMowazi"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setForgotPasswordUrl("");
        }

        try {
            parameter.setEnableOTC(object.getBoolean("EnableOTC"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setForgotPasswordUrl("");
        }

        try {
            parameter.setActivateChequeRequest(object.getBoolean("ActivateChequeRequest"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setActivateChequeRequest(false);
        }


        try {
            parameter.setShowCashTransactionRequest(object.getBoolean("ShowCashTransactionRequest"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setShowCashTransactionRequest(false);
        }

        try {
            parameter.setShowBalanceDetails(object.getBoolean("ShowBalanceDetails"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setShowBalanceDetails(false);
        }

        try {
            parameter.setShowStockTransactionRequest(object.getBoolean("ShowStockTransactionRequest"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setShowStockTransactionRequest(false);
        }






        try {
            parameter.setEnableNotification(object.getBoolean("EnableNotification"));
            MyApplication.editor.putBoolean("EnableNotification", object.getBoolean("EnableNotification")).apply();
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setEnableNotification(true);
        }


        try {
            parameter.setShowChangePin(object.getBoolean("ShowChangePIN"));
            MyApplication.editor.putBoolean("ShowChangePIN", object.getBoolean("ShowChangePIN")).apply();
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setShowChangePin(false);
        }

        try {
            parameter.setEnableAdvancedTypeSection(object.getBoolean("EnableAdvancedTypeSection"));
            MyApplication.editor.putBoolean("EnableAdvancedTypeSection", object.getBoolean("EnableAdvancedTypeSection")).apply();
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setEnableAdvancedTypeSection(true);
        }

/*
        try {
            parameter.setBroker_ID(object.getInt("broker_ID"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setForgotPasswordUrl("");
        }
*/


        try {
            parameter.setContactUsUrl(object.getString("ContactUsUrl").equals("null") ? "" : object.getString("ContactUsUrl"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setContactUsUrl("");
        }



        try {
            parameter.setMaximumPasswordLength(object.getInt("MaximumPasswordLength"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setMaximumPasswordLength(10);
        }

        try {
            parameter.setMinimumPasswordLength(object.getInt("MinimumPasswordLength"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setMinimumPasswordLength(0);
        }

        try {
            parameter.setDefaultDMABrokerEmployeeID(object.getInt("DefaultDMABrokerEmployeeID"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setDefaultDMABrokerEmployeeID(0);
        }

        try {
            parameter.setComplexPasswordEnabled(object.getBoolean("ComplexPasswordEnabled"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setComplexPasswordEnabled(false);
        }

        try {
            parameter.setCanUserManageTraderOrder(object.getBoolean("CanUserManageTraderOrder"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setCanUserManageTraderOrder(false);
        }

        try {
            parameter.setMessageEn(object.getString("MessageEn"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setMessageEn("");
        }

        try {
            parameter.setDefaultPriceOnTrade("" + Integer.parseInt(object.getString("DefaultPriceOnTrade")));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setDefaultPriceOnTrade("0");
        }

        try {
            parameter.setMessageAr(object.getString("MessageAr"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setMessageAr("");
        }

        try {
            parameter.setStatus(object.getString("Status"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setStatus("");
        }

        try {
            parameter.setServerVersionNumber(object.getString("ServerVersionNumber"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setServerVersionNumber("");
        }


        try {
            parameter.setForceUpdate(Boolean.parseBoolean(object.getString("ForceUpdate")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            parameter.setEnableMowazi(Boolean.parseBoolean(object.getString("EnableMowazi")));
        } catch (JSONException e) {
            e.printStackTrace();
            parameter.setEnableMowazi(false);
        }

        try {
            parameter.setMowaziCompanyDetail(Boolean.parseBoolean(object.getString("MowaziCompanyDetail")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            parameter.setMowaziBrokerId(object.getString("MowaziBrokerId").equals("null") ? "" : object.getString("MowaziBrokerId"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setMowaziBrokerId("");
        }

        try {
            parameter.setMowaziServiceLink(object.getString("MowaziServiceLink").equals("null") ? "" : object.getString("MowaziServiceLink"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setMowaziServiceLink("");
        }

        try {
            parameter.setRenewPasswordHashing(Boolean.parseBoolean(object.getString("RenewPasswordHashing")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            parameter.setTradeOnlyIslamicStocks(Boolean.parseBoolean(object.getString("TradeOnlyIslamicStocks")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            parameter.setForgotPasswordUrl(object.getString("ForgotPasswordUrl").equals("null") ? "" : object.getString("ForgotPasswordUrl"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setForgotPasswordUrl("");
        }


        try {
            parameter.setForgotUsernameUrl(object.getString("ForgotUsernameUrl").equals("null") ? "" : object.getString("ForgotUsernameUrl"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setForgotUsernameUrl("");
        }

        try {
            parameter.setUnlockUserUrl(object.getString("UnlockUserUrl").equals("null") ? "" : object.getString("UnlockUserUrl"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setUnlockUserUrl("");
        }

        try {
            parameter.setClientRegistrationUrl(object.getString("ClientRegistrationUrl").equals("null") ? "" : object.getString("ClientRegistrationUrl"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setClientRegistrationUrl("");
        }

        try {
            parameter.setFacebookLink(object.getString("FacebookLink").equals("null") ? "" : object.getString("FacebookLink"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setFacebookLink("");
        }

        try {
            parameter.setRssLink(object.getString("RSSLink").equals("null") ? "" : object.getString("RSSLink"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setRssLink("");
        }

        try {
            parameter.setTwitterLink(object.getString("TwitterLink").equals("null") ? "" : object.getString("TwitterLink"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setTwitterLink("");
        }

        try {
            parameter.setYouTubeLink(object.getString("YouTubeLink").equals("null") ? "" : object.getString("YouTubeLink"));
        } catch (Exception e) {
            e.printStackTrace();
            parameter.setYouTubeLink("");
        }
        return parameter;
    }

    public static MarketStatus GetMarketStatus(String JsonString)
            throws JSONException, UnsupportedEncodingException {
        MarketStatus marketStatus = new MarketStatus();

        JSONObject object = new JSONObject(JsonString);

        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");
        marketStatus.setMessageEn(success);
        String successAr = jsondata_msg.getString("MessageAr");
        marketStatus.setMessageAr(successAr);

        try{
        int statusMessage = jsondata_msg.getInt("Status");
        marketStatus.setMessageStatus(statusMessage);
        }catch (Exception e){

        }

        try {

            try {
                marketStatus.setServerTime(object.getString("ServerTime"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                marketStatus.setSessionChanged(object.getBoolean("SessionChanged"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                marketStatus.setStatusDescriptionAr(object.getString("StatusDescriptionAr"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                marketStatus.setStatusDescriptionEn(object.getString("StatusDescriptionEn"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                marketStatus.setStatusID(object.getInt("StatusID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }



            try {
                marketStatus.setStatusName(object.getString("StatusName"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();

        }
        return marketStatus;

    }

    public static ArrayList<Stock> GetStockTops(String JsonString)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<Stock> stocks = new ArrayList<Stock>();

        JSONObject object = new JSONObject(JsonString);


        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");



        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONArray("StockList");
                for (int i = 0; i < jarray.length(); i++) {
                    Stock stock = new Stock();
                    JSONObject json_data = jarray.getJSONObject(i);




                    try {
                        stock.setTopType(json_data.getInt("TopType"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    try {
                        stock.setAmount(json_data.getString("Amount"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setChange(json_data.getString("Change"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setChangePercent(json_data.getString("ChangePercent"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setLast(json_data.getString("Last"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setTrades(json_data.getString("Trades"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setVolume(json_data.getString("Volume"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setId(json_data.getString("ID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSectorID(json_data.getString("SectorID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSymbolAr(json_data.getString("SymbolAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSymbolEn(json_data.getString("SymbolEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stock.setInstrumentId(json_data.getString("InstrumentID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stock.setTradingSession(json_data.getString("TradingSession"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stock.setMarketID(json_data.getString("MarketID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSecurityId(json_data.getString("SecurityID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




                    stocks.add(stock);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stocks;
    }

    public static ArrayList<Sector> GetSectorIndex(String JsonString)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<Sector> sectors = new ArrayList<Sector>();

        JSONObject object = new JSONObject(JsonString);

        if (!object.getString("MaxTimeStamp").equals("0"))
            MyApplication.sectorsTimesTamp = object.getString("MaxTimeStamp");

        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONArray("Sectors");
                for (int i = 0; i < jarray.length(); i++) {
                    Sector sectorIndex = new Sector();
                    JSONObject json_data = jarray.getJSONObject(i);

                    try {
                        sectorIndex.setAmount(json_data.getInt("Amount"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setChange(json_data.getString("Change"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setChangePercent(json_data.getString("ChangePercent"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setChangeWeight(json_data.getInt("ChangeWeight"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setChangeWeightPercent(json_data.getInt("ChangeWeightPercent"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setHigh(json_data.getInt("High"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setHigh52(json_data.getDouble("High52"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setHighWeight(json_data.getInt("HighWeight"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setLow(json_data.getInt("Low"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setSectorID(json_data.getString("ID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setLastWeight(json_data.getInt("LastWeight"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        sectorIndex.setLow52(json_data.getDouble("Low52"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setLastWeight(json_data.getInt("LowWeight"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setNameAr(json_data.getString("NameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setNameEn(json_data.getString("NameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setOpen(json_data.getInt("Open"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setOpenWeight(json_data.getInt("OpenWeight"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setReferencePrice(json_data.getDouble("ReferencePrice"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setTrades(json_data.getInt("Trades"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setValue(json_data.getString("Value"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        sectorIndex.setVolume(json_data.getInt("Volume"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    sectors.add(sectorIndex);


                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return sectors;
    }

    public static ArrayList<StockQuotation> GetStockQuotation(String JsonString,boolean enableTstamp)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<StockQuotation> stocks = new ArrayList<StockQuotation>();
        String CurrentMarketId=MyApplication.marketID;

        JSONObject object = new JSONObject(JsonString);

        String msgdata = object.getString("ResponseMessage");


        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONArray("StockQuotations");
                StockQuotation stock;

                int len = jarray.length();

                for (int i = 0; i < len; i++) {
                    JSONObject json_data = jarray.getJSONObject(i);
                    stock = new StockQuotation();

                    //<editor-fold desc="Stock Quotation">


                    try{if(i==0){
                        try
                        {Log.wtf("marketid_int",json_data.getInt("MarketId")+"");
                        }
                        catch (Exception e){
                            {Log.wtf("marketid_int_exception",e.toString());
                            }
                        }


                        try
                        {Log.wtf("marketid_string",json_data.getString("MarketId")+"");
                        }
                        catch (Exception e){
                            {Log.wtf("marketid_string_exception",e.toString());
                            }
                        }


                      if(json_data.getString("MarketId")!=null && !json_data.getString("MarketId").trim().toLowerCase().matches("null") && !json_data.getString("MarketId").trim().toLowerCase().matches("0")) {
                          CurrentMarketId = json_data.getString("MarketId");
                      }

                    }else {
                        CurrentMarketId = MyApplication.marketID;
                    }}catch (Exception e){}



                    try {
                        stock.setAmount(json_data.getInt("Amount"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSecurityId(json_data.getString("SecurityID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stock.setSecurityId("");
                    }

                    try {
                        stock.setAsk(json_data.getDouble("Ask"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setBid(json_data.getDouble("Bid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setChange(json_data.getString("Change").equals("0") ? "0.0" : json_data.getString("Change"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stock.setNormalMarketSize(json_data.getInt("NormalMarketSize"));
                    } catch (JSONException e) {
                        Log.wtf("Trade json_data", "error :" + e.getMessage());
                        stock.setNormalMarketSize(0);
                    }

                    try {
                        stock.setHiLimit(json_data.getDouble("HiLimit"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setHigh(json_data.getString("High"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stock.setTradingSession(json_data.getInt("TradingSession"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stock.setIslamic(json_data.getBoolean("IsIslamic"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setLast(json_data.getDouble("Last"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setPreviousClosing(json_data.getDouble("PreviousClosing"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setLow(json_data.getString("Low"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setLowlimit(json_data.getDouble("LowLimit"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setOpen(json_data.getInt("Open"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setStockID(json_data.getInt("StockID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setStockTradingStatus(json_data.getInt("StockTradingStatus"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setTrade(json_data.getInt("Trades"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setVolume(json_data.getDouble("Volume"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stock.setChangePercent(json_data.getString("ChangePercent"));
                    } catch (JSONException e) {
                     //   e.printStackTrace();
                    }

                    try {
                        stock.setChangePercentFormatted(json_data.getString("ChangePercentFormatted"));
                    } catch (JSONException e) {
                        //   e.printStackTrace();
                    }


                    try {
                        stock.setChangeFormatted(json_data.getString("ChangeFormatted"));
                    } catch (JSONException e) {
                        //   e.printStackTrace();
                    }






                    try {
                        stock.setSectorNameEn(json_data.getString("SectorNameEn"));
                    } catch (JSONException e) {
                        //   e.printStackTrace();
                    }


                    try {
                        stock.setSectorNameAr(json_data.getString("SectorNameAr"));
                    } catch (JSONException e) {
                        //   e.printStackTrace();
                    }

                    try {
                        stock.setSectorSymbolEn(json_data.getString("SectorSymbolEn"));
                    } catch (JSONException e) {
                        //   e.printStackTrace();
                    }

                    try {
                        stock.setSectorSymbolAr(json_data.getString("SectorSymbolAr"));
                    } catch (JSONException e) {
                        //   e.printStackTrace();
                    }

                    try {
                        stock.setVolumeBidFormatted(json_data.getString("VolumeBidFormatted"));
                    } catch (JSONException e) {
                        //   e.printStackTrace();
                    }

                    try {
                        stock.setVolumeAskFormatted(json_data.getString("VolumeAskFormatted"));
                    } catch (JSONException e) {
                        //   e.printStackTrace();
                    }

                    try {
                        stock.setLastFormatted(json_data.getString("LastFormatted"));
                    } catch (JSONException e) {
                        //   e.printStackTrace();
                    }

                    try {
                        stock.setVolumeFormatted(json_data.getString("VolumeFormatted"));
                    } catch (JSONException e) {
                        //   e.printStackTrace();
                    }

                    try {
                        stock.setEquilibriumPriceFormatted(json_data.getString("EquilibriumPriceFormatted"));
                    } catch (JSONException e) {
                        //   e.printStackTrace();
                    }


                    try {
                        stock.setHighFormatted(json_data.getString("HighFormatted"));
                    } catch (JSONException e) {
                        //   e.printStackTrace();
                    }
                    try {
                        stock.setLowFormatted(json_data.getString("LowFormatted"));
                    } catch (JSONException e) {
                        //   e.printStackTrace();
                    }
                    try {
                        stock.setHiLimitFormatted(json_data.getString("HiLimitFormatted"));
                    } catch (JSONException e) {
                        //   e.printStackTrace();
                    }
                    try {
                        stock.setLowLimitFormatted(json_data.getString("LowLimitFormatted"));
                    } catch (JSONException e) {
                        //   e.printStackTrace();
                    }






                    try {
                        stock.setVolumeAsk(json_data.getInt("VolumeAsk"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setVolumeBid(json_data.getInt("VolumeBid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stock.setNumberOfOrders(json_data.getInt("NumberOfOrders")+"");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setNameAr(json_data.getString("NameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setNameEn(json_data.getString("NameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSymbolAr(json_data.getString("SymbolAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSymbolEn(json_data.getString("SymbolEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setInstrumentId(json_data.getString("InstrumentId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setInstrumentNameAr(json_data.getString("InstrumentNameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setInstrumentNameEn(json_data.getString("InstrumentNameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSessionId(json_data.getString("SessionId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSessionNameAr(json_data.getString("SessionNameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSessionNameEn(json_data.getString("SessionNameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSectorID(json_data.getString("SectorID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setMarketId(json_data.getInt("MarketId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

/*

                    try {
                        stock.setTickDirection(Double.parseDouble(json_data.getString("TickDirection")));
                    } catch (Exception e) {
                        e.printStackTrace();
                        stock.setTickDirection(0.0);
                    }
*/


                    try {
                        stock.setBidFormatted(json_data.getString("BidFormatted"));
                    } catch (JSONException e) {
                        stock.setBidFormatted("");
                        e.printStackTrace();
                    }

                    try {
                        stock.setAskFormatted(json_data.getString("AskFormatted"));
                    } catch (JSONException e) {
                        stock.setAskFormatted("");
                        e.printStackTrace();
                    }


                    try {
                        stock.setReferencePrice(json_data.getDouble("ReferencePrice"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //</editor-fold>



                    stocks.add(stock);


                }


            } catch (Exception e) {
                e.printStackTrace();

            }

          if(enableTstamp) {
              try {

                  if (!object.getString("MaxTStamp").equals("0") && !object.getString("MaxTStamp").trim().equals("null") && object.getString("MaxTStamp") != null) {

                      MyApplication.stockTimesTamp = object.getString("MaxTStamp");
                      MyApplication.stockTimesTampMap.put(CurrentMarketId, object.getString("MaxTStamp"));
                  }
              } catch (Exception e) {
              }
          }

        }
        return stocks;
    }


    public static ArrayList<StockQuotation> GetStockQuotationBonds(String JsonString,boolean enableTstamp)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<StockQuotation> stocks = new ArrayList<StockQuotation>();
        String CurrentMarketId=MyApplication.marketID;

        JSONObject object = new JSONObject(JsonString);

        String msgdata = object.getString("ResponseMessage");


        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONArray("Bonds");
                StockQuotation stock;

                int len = jarray.length();

                for (int i = 0; i < len; i++) {
                    JSONObject json_data = jarray.getJSONObject(i);
                    stock = new StockQuotation();

                    //<editor-fold desc="Stock Quotation">


                    try{if(i==0){
                        try
                        {Log.wtf("marketid_int",json_data.getInt("MarketId")+"");
                        }
                        catch (Exception e){
                            {Log.wtf("marketid_int_exception",e.toString());
                            }
                        }


                        try
                        {Log.wtf("marketid_string",json_data.getString("MarketId")+"");
                        }
                        catch (Exception e){
                            {Log.wtf("marketid_string_exception",e.toString());
                            }
                        }


                        if(json_data.getString("MarketId")!=null && !json_data.getString("MarketId").trim().toLowerCase().matches("null") && !json_data.getString("MarketId").trim().toLowerCase().matches("0")) {
                            CurrentMarketId = json_data.getString("MarketId");
                        }

                    }else {
                        CurrentMarketId = MyApplication.marketID;
                    }}catch (Exception e){}



                    try {
                        stock.setAmount(json_data.getInt("Amount"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSecurityId(json_data.getString("SecurityID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stock.setSecurityId("");
                    }

                    try {
                        stock.setAsk(json_data.getDouble("Ask"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setBid(json_data.getDouble("Bid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setChange(json_data.getString("Change").equals("0") ? "0.0" : json_data.getString("Change"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stock.setNormalMarketSize(json_data.getInt("NormalMarketSize"));
                    } catch (JSONException e) {
                        Log.wtf("Trade json_data", "error :" + e.getMessage());
                        stock.setNormalMarketSize(0);
                    }

                    try {
                        stock.setHiLimit(json_data.getDouble("HiLimit"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setHigh(json_data.getString("High"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stock.setTradingSession(json_data.getInt("TradingSession"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stock.setIslamic(json_data.getBoolean("IsIslamic"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setLast(json_data.getDouble("Last"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setPreviousClosing(json_data.getDouble("PreviousClosing"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setLow(json_data.getString("Low"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setLowlimit(json_data.getDouble("LowLimit"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setOpen(json_data.getInt("Open"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setStockID(json_data.getInt("StockID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setStockTradingStatus(json_data.getInt("StockTradingStatus"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setTrade(json_data.getInt("Trades"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setVolume(json_data.getInt("Volume"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stock.setChangePercent(json_data.getString("ChangePercent"));
                    } catch (JSONException e) {
                        //   e.printStackTrace();
                    }



                    try {
                        stock.setVolumeAsk(json_data.getInt("VolumeAsk"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setVolumeBid(json_data.getInt("VolumeBid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stock.setNumberOfOrders(json_data.getInt("NumberOfOrders")+"");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setNameAr(json_data.getString("NameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setNameEn(json_data.getString("NameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSymbolAr(json_data.getString("SymbolAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSymbolEn(json_data.getString("SymbolEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setInstrumentId(json_data.getString("InstrumentId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setInstrumentNameAr(json_data.getString("InstrumentNameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setInstrumentNameEn(json_data.getString("InstrumentNameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSessionId(json_data.getString("SessionId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSessionNameAr(json_data.getString("SessionNameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSessionNameEn(json_data.getString("SessionNameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSectorID(json_data.getString("SectorID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setMarketId(json_data.getInt("MarketId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

/*

                    try {
                        stock.setTickDirection(Double.parseDouble(json_data.getString("TickDirection")));
                    } catch (Exception e) {
                        e.printStackTrace();
                        stock.setTickDirection(0.0);
                    }
*/



                    try {
                        stock.setReferencePrice(json_data.getDouble("ReferencePrice"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //</editor-fold>



                    stocks.add(stock);


                }


            } catch (Exception e) {
                e.printStackTrace();

            }

            if(enableTstamp) {
                try {

                    if (!object.getString("MaxTStamp").equals("0") && !object.getString("MaxTStamp").trim().equals("null") && object.getString("MaxTStamp") != null) {

                        MyApplication.bondsTimesTamp = object.getString("MaxTStamp");
                        MyApplication.bondsTimesTampMap.put(CurrentMarketId, object.getString("MaxTStamp"));
                    }
                } catch (Exception e) {
                }
            }

        }
        return stocks;
    }

    public static ArrayList<StockQuotation> GetFavoriteStockQuotation(String JsonString)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<StockQuotation> stocks = new ArrayList<StockQuotation>();

        JSONObject object = new JSONObject(JsonString);


        String msgdata = object.getString("ResponseMessage");

        //MyApplication.stockTimesTamp = object.getString("MaxTStamp");

        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONArray("FavoriteStockList");
                for (int i = 0; i < jarray.length(); i++) {
                    StockQuotation stock = new StockQuotation();
                    JSONObject json_data = jarray.getJSONObject(i);


                    try {
                        stock.setSecurityId(json_data.getString("SecurityID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stock.setSecurityId("");
                    }

                    try {
                        stock.setAmount(json_data.getInt("Amount"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setAsk(json_data.getDouble("Ask"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setBid(json_data.getDouble("Bid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setChange(json_data.getString("Change"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setHiLimit(json_data.getInt("HiLimit"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setHigh(json_data.getString("High"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setIslamic(json_data.getBoolean("IsIslamic"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setLast(json_data.getDouble("Last"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setLow(json_data.getString("Low"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setLowlimit(json_data.getInt("LowLimit"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setOpen(json_data.getInt("Open"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setStockID(json_data.getInt("StockID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setStockTradingStatus(json_data.getInt("StockTradingStatus"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setTrade(json_data.getInt("Trades"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setVolume(json_data.getInt("Volume"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setVolumeAsk(json_data.getInt("VolumeAsk"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setVolumeBid(json_data.getInt("VolumeBid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stock.setNumberOfOrders(json_data.getInt("NumberOfOrders")+"");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stock.setNameAr(json_data.getString("NameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setNameEn(json_data.getString("NameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSymbolAr(json_data.getString("SymbolAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setSymbolEn(json_data.getString("SymbolEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setReferencePrice(json_data.getDouble("ReferencePrice"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    stocks.add(stock);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stocks;
    }

    public static ArrayList<Integer> GetFavoriteStocks(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        ArrayList<Integer> stocksIds = new ArrayList<>();

        JSONObject object = new JSONObject(JsonString);


        String msgdata = object.getString("ResponseMessage");

        //MyApplication.stockTimesTamp = object.getString("MaxTStamp");

        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONArray("FavoriteStockIds");
                for (int i = 0; i < jarray.length(); i++) {

                    stocksIds.add(Integer.parseInt(jarray.getString(i).replace("\"", "")));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stocksIds;
    }

    public static ArrayList<OrderDurationType> GetOrderDurationList(String JsonString)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<OrderDurationType> orderDurationTypes = new ArrayList<OrderDurationType>();

        JSONObject object = new JSONObject(JsonString);


        try {
            JSONArray jarray = object.getJSONArray("OrderDurationList");
            for (int i = 0; i < jarray.length(); i++) {
                OrderDurationType type = new OrderDurationType();
                JSONObject json_data = jarray.getJSONObject(i);

                try {
                    type.setID(json_data.getInt("ID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    type.setDescriptionAr(json_data.getString("DescriptionAr"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    type.setDescriptionEn(json_data.getString("DescriptionEn"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                orderDurationTypes.add(type);


            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return orderDurationTypes;
    }

    public static ArrayList<Instrument> GetInstrumentsList(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        JSONObject object = new JSONObject(JsonString);

        ArrayList<Instrument> instruments = new ArrayList<>();

        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");
        if (success.equals("Success")) {
            try {
                JSONArray json_instruments = object.getJSONArray("InstrumentList");
                for (int i = 0; i < json_instruments.length(); i++) {

                    JSONObject json_data = json_instruments.getJSONObject(i);
                    Instrument instrument = new Instrument();

                    try {
                        instrument.setId(json_data.getString("ID"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        instrument.setInstrumentNameAr(json_data.getString("InstrumentNameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        instrument.setInstrumentNameEn(json_data.getString("InstrumentNameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        instrument.setInstrumentState(json_data.getString("InstrumentState"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        instrument.setInstrumentCode(json_data.getString("InstrumentCode"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        instrument.setInstrumentSymbol(json_data.getString("InstrumentSymbol"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        instrument.setSecurityClassAr(json_data.getString("SecurityClassAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        instrument.setSecurityClassEn(json_data.getString("SecurityClassEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        instrument.setMarketSegmentID(json_data.getInt("MarketSegmentID"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        instrument.setMarketID(json_data.getInt("MarketID"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    instruments.add(instrument);
                    MyApplication.instrumentsHashmap.put(instrument.getInstrumentCode(), instrument);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instruments;
    }





    public static ArrayList<Unit> GetUnitsList(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        JSONObject object = new JSONObject(JsonString);

        ArrayList<Unit> units = new ArrayList<>();

        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");
        if (success.equals("Success")) {
            try {
                JSONArray json_instruments = object.getJSONArray("UnitList");
                for (int i = 0; i < json_instruments.length(); i++) {

                    JSONObject json_data = json_instruments.getJSONObject(i);
                    Unit unit = new Unit();

                    try {
                        unit.setQuantityUnit(json_data.getInt("QuantityUnit"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        unit.setFromPrice(Double.parseDouble(json_data.getString("FromPrice")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        unit.setPriceUnit(Double.parseDouble(json_data.getString("PriceUnit")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        unit.setToPrice(Double.parseDouble(json_data.getString("ToPrice")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    units.add(unit);
//                    MyApplication.instrumentsHashmap.put(units.getInstrumentCode(), instrument);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return units;
    }




    public static ArrayList<ApplicationList> GetApplicationList(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        JSONObject object = new JSONObject(JsonString);

        ArrayList<ApplicationList> applist = new ArrayList<>();

        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");
        if (success.equals("Success")) {
            try {
                JSONArray json_app_list = object.getJSONArray("ApplicationList");
                for (int i = 0; i < json_app_list.length(); i++) {

                    JSONObject json_data = json_app_list.getJSONObject(i);
                    ApplicationList app = new ApplicationList();

                    try {
                        app.setDefaultAdminID(json_data.getInt("DefaultAdminID"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        app.setDefaultBrokerEmployeeID(json_data.getInt("DefaultBrokerEmployeeID"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        app.setId(json_data.getInt("ID"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        app.setTechnicalOrderType(json_data.getInt("TechnicalOrderType"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        app.setName(json_data.getString("Name"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        app.setEditable(json_data.getBoolean("IsEditable"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    applist.add(app);
//                    MyApplication.instrumentsHashmap.put(units.getInstrumentCode(), instrument);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return applist;
    }






    public static ArrayList<ReportPageSetup> GetReportPageSetup(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        JSONObject object = new JSONObject(JsonString);

        ArrayList<ReportPageSetup> reportList = new ArrayList<>();

        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");
        if (success.equals("Success")) {
            try {
                JSONArray json_report_list = object.getJSONArray("ReportPageSetupList");
                for (int i = 0; i < json_report_list.length(); i++) {

                    JSONObject json_data = json_report_list.getJSONObject(i);
                    ReportPageSetup report = new ReportPageSetup();

                    try {
                        report.setReportLink(json_data.getString("ReportLink"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }



                    try {
                        report.setID(json_data.getInt("ID"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        report.setTypeID(json_data.getInt("TypeID"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        report.setNameEn(json_data.getString("NameEn"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        report.setNameAr(json_data.getString("NameAr"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    try {
                        report.setHasFromDate(json_data.getBoolean("HasFromDate"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        report.setHasTodate(json_data.getBoolean("HasTodate"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    reportList.add(report);
//                    MyApplication.instrumentsHashmap.put(units.getInstrumentCode(), instrument);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return reportList;
    }








    public static ArrayList<NewsItem> GetNews(String JsonString)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<NewsItem> newsItems = new ArrayList<NewsItem>();

        JSONObject object = new JSONObject(JsonString);


        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONArray("StockNewsList");
                Log.wtf("StockNewsList", "Count = " + jarray.length());
                for (int i = 0; i < jarray.length(); i++) {
                    NewsItem newsItem = new NewsItem();
                    JSONObject json_data = jarray.getJSONObject(i);

                    try {
                        newsItem.setId(json_data.getInt("ID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        newsItem.setSecurityId(json_data.getString("SecurityID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        newsItem.setSecurityId("");
                    }

                    try {
                        newsItem.setHead(json_data.getString("Head"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        newsItem.setDetails(json_data.getString("Details"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        newsItem.setCreationDate(json_data.getString("CreationDate"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        newsItem.setLink(json_data.getString("Link"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        newsItem.setTime(json_data.getString("Time"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    newsItems.add(newsItem);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return newsItems;
    }




    public static ArrayList<BalanceSummary> getBalancedFields(String JsonString)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<BalanceSummary> balanceItems = new ArrayList<BalanceSummary>();

        JSONObject object = new JSONObject(JsonString);


        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONArray("Fields");
                for (int i = 0; i < jarray.length(); i++) {
                    BalanceSummary balanceSummaryItem = new BalanceSummary();
                    JSONObject json_data = jarray.getJSONObject(i);

                    try {
                        balanceSummaryItem.setKey(json_data.getString("Key"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        balanceSummaryItem.setSymbol(json_data.getString("Symbol"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        balanceSummaryItem.setGroupId(json_data.getString("GroupId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        balanceSummaryItem.setGroupId("");
                    }


                    try {
                        balanceSummaryItem.setValue(json_data.getString("Value"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        balanceSummaryItem.setValue("");
                    }




                    balanceItems.add(balanceSummaryItem);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return balanceItems;
    }




    public static ArrayList<ValueItem> getBalanceDetailsValues(String JsonString)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<ValueItem> valueItems = new ArrayList<ValueItem>();

        JSONObject object = new JSONObject(JsonString);


        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONObject("CashSummary").getJSONArray("Fields");
                for (int i = 0; i < jarray.length(); i++) {
                    ValueItem item = new ValueItem();
                    JSONObject json_data = jarray.getJSONObject(i);

                    try {
                        item.setTitle(json_data.getString("Key"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


          /*          try {
                        item.setSymbol(json_data.getString("Symbol"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        item.setGroupId(json_data.getString("GroupId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        item.setGroupId("");
                    }*/


                    try {
                        item.setValue(json_data.getString("Value"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        item.setValue("");
                    }




                    valueItems.add(item);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return valueItems;
    }


    public static ArrayList<BalanceSummary> getBalancedGroups(String JsonString)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<BalanceSummary> balanceItems = new ArrayList<BalanceSummary>();

        JSONObject object = new JSONObject(JsonString);


        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONArray("Groups");
                for (int i = 0; i < jarray.length(); i++) {
                    BalanceSummary balanceSummaryItem = new BalanceSummary();
                    JSONObject json_data = jarray.getJSONObject(i);

                    try {
                        balanceSummaryItem.setKey(json_data.getString("Key"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        balanceSummaryItem.setSymbol(json_data.getString("Symbol"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        balanceSummaryItem.setGroupId(json_data.getString("GroupId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        balanceSummaryItem.setSymbol("");
                    }



                    try {
                        balanceSummaryItem.setValue(json_data.getString("Value"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        balanceSummaryItem.setValue("");
                    }



                    balanceItems.add(balanceSummaryItem);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return balanceItems;
    }


    public static ArrayList<ExecutedOrders> getBalancedExecuted(String JsonString)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<ExecutedOrders> executedOrders = new ArrayList<ExecutedOrders>();

        JSONObject object = new JSONObject(JsonString);


        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONArray("ExecutedOrdersGrouped");
                for (int i = 0; i < jarray.length(); i++) {
                    ExecutedOrders executedOrder = new ExecutedOrders();
                    JSONObject json_data = jarray.getJSONObject(i);

                    try {
                        executedOrder.setAction(json_data.getString("Action"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        executedOrder.setAvgPrice(json_data.getString("AvgPrice"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        executedOrder.setQuantityExecuted(json_data.getString("QuantityExecuted"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        executedOrder.setQuantityExecuted("");
                    }

                    try {
                        executedOrder.setSecurityID(json_data.getString("SecurityID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        executedOrder.setSecurityID("");
                    }
                    try {
                        executedOrder.setStockID(json_data.getString("StockID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        executedOrder.setStockID("");
                    }
                    try {
                        executedOrder.setStockName(json_data.getString("StockName"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        executedOrder.setStockName("");
                    }
                    try {
                        executedOrder.setStockSymbol(json_data.getString("StockSymbol"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        executedOrder.setStockSymbol("");
                    }

                    try {
                        executedOrder.setTradeTypeID(json_data.getString("tradeTypeID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        executedOrder.setTradeTypeID("");
                    }




                    executedOrders.add(executedOrder);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return executedOrders;
    }



    public static ArrayList<MarginPayment> getMarginPayments(String JsonString)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<MarginPayment> arrayMarginPayments = new ArrayList<MarginPayment>();

        JSONObject object = new JSONObject(JsonString);


        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONArray("MarginPayment");
                for (int i = 0; i < jarray.length(); i++) {
                    MarginPayment marginPayment = new MarginPayment();
                    JSONObject json_data = jarray.getJSONObject(i);


                    try {
                        marginPayment.setAmounDue(json_data.getString("AmounDue"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marginPayment.setAmounDue("");
                    }

                    try {
                        marginPayment.setTradeDate(json_data.getString("TradeDate"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marginPayment.setTradeDate("");
                    }

                    try {
                        marginPayment.setSettlementDate(json_data.getString("SettlementDate"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marginPayment.setSettlementDate("");
                    }



                    arrayMarginPayments.add(marginPayment);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return arrayMarginPayments;
    }



    public static ArrayList<WebItem> GetSiteMap(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        ArrayList<WebItem> siteMapList = new ArrayList<>();

        JSONObject object = new JSONObject(JsonString);

        try {

            JSONArray jarray = object.getJSONArray("SubMenuList");
            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jsonObject = jarray.getJSONObject(i);
                WebItem webItem = new WebItem();

                //integers
                try {
                    webItem.setID(jsonObject.getInt("SubMenuID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    webItem.setTitleAr(jsonObject.getString("TitleAr"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    webItem.setTitleEn(jsonObject.getString("TitleEn"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    webItem.setUrl(jsonObject.getString("Url"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                siteMapList.add(webItem);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return siteMapList;
    }

    public static ArrayList<WebItem> GetWebItems(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        ArrayList<WebItem> webItems = new ArrayList<>();

        JSONObject object = new JSONObject(JsonString);

        try {

            JSONArray jarray = object.getJSONArray("WebSiteContents");
            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jsonObject = jarray.getJSONObject(i);
                WebItem webItem = new WebItem();

                //integers
                try {
                    webItem.setID(jsonObject.getInt("ID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //strings
                try {
                    webItem.setContentAr(jsonObject.getString("ContentAr"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    webItem.setContentEn(jsonObject.getString("ContentEn"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    webItem.setTitleAr(jsonObject.getString("TitleAr"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    webItem.setTitleEn(jsonObject.getString("TitleEn"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                webItems.add(webItem);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return webItems;
    }


    public static ArrayList<TimeSale> GetTimeSales(Context context,String JsonString, boolean isTimeSaleTrade)
            throws JSONException, UnsupportedEncodingException {

        ArrayList<TimeSale> trades = new ArrayList<TimeSale>();

        JSONObject jsonobj = new JSONObject(JsonString);



   //  MyApplication.timeSalesTimesTamp = (Integer.parseInt(MyApplication.timeSalesTimesTamp)-100)+"";

        String CurrentMarketId=MyApplication.marketID;

        try {

            JSONArray jarray = jsonobj.getJSONArray("ExecutedOrderList");
            TimeSale trade;
            int len = jarray.length();

            for (int i = 0; i < len; i++) {
                trade = new TimeSale();
                JSONObject json_data = jarray.getJSONObject(i);

                try {
                    trade.setSecurityId(json_data.getString("SecurityID"));
                } catch (Exception e) {
                    e.printStackTrace();
                    trade.setSecurityId(json_data.getString(""));
                }

                try {
                    trade.setChange(String.valueOf(json_data.getDouble("Change")));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    trade.setChangeIndicator(json_data.getInt("ChangeIndicator"));
                } catch (Exception e) {
                    e.printStackTrace();
                    trade.setChangeIndicator(0);
                }

                try {
                    trade.setQuantity(json_data.getString("Quantity"));
                } catch (Exception e) {
                    e.printStackTrace();
                    trade.setQuantity("0");
                }

                //trade.setOrderTypeId("0");

                try {
                    trade.setPrice(json_data.getString("Price"));
                } catch (Exception e) {
                    trade.setPrice("0");
                }

                trade.setStockID(Integer.parseInt(json_data.getString("StockID")));

                try {
                    trade.setMarketId(json_data.getInt("MarketId"));
                } catch (Exception e) {
                    e.printStackTrace();
                    trade.setMarketId(0);
                }

                trade.setStockSymbolAr(json_data.getString("StockSymbolAr"));

                trade.setStockSymbolEn(json_data.getString("StockSymbolEn"));

                try {
                    trade.setTradeTime(json_data.getString("TradeTime"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    trade.setTradeDate(json_data.getString("TradeDate"));
                } catch (Exception e) {
                    trade.setTradeDate("");
                    e.printStackTrace();
                }

                try {
                    trade.setInstrumentId(json_data.getString("InstrumentId"));
                } catch (Exception e) {
                    e.printStackTrace();
                    trade.setInstrumentId("");
                }

                try {
                    trade.setOrderType(json_data.getInt("ExecuteType"));
                } catch (Exception e) {
                    e.printStackTrace();
                    trade.setInstrumentId("");
                }

                try {
                    trade.setId(String.valueOf(json_data.getInt("TradeID")));
                } catch (Exception e) {
                    e.printStackTrace();
                    trade.setInstrumentId("");
                }



                try{
                  if(i==0){
                    if(json_data.getString("MarketID")!=null && !json_data.getString("MarketID").trim().toLowerCase().matches("null") && !json_data.getString("MarketID").trim().toLowerCase().matches("0"))
                    CurrentMarketId= json_data.getString("MarketID");
                }else {
                    CurrentMarketId = MyApplication.marketID;
                }}catch (Exception e){}


                trades.add(trade);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Log.wtf("GlobaleFunctions GetTimeSales Timestamp", "is: " + jsonobj.getString("MaxTimeStamp"));

            if (!jsonobj.getString("MaxTimeStamp").equals("0") && isTimeSaleTrade && !jsonobj.getString("MaxTimeStamp").trim().matches("null") && jsonobj.getString("MaxTimeStamp")!=null){
                MyApplication.timeSalesTimesTamp = jsonobj.getString("MaxTimeStamp");
                MyApplication.timeSalesTimesTampMap.put(CurrentMarketId,jsonobj.getString("MaxTimeStamp"));
                Actions.saveMap(context,MyApplication.timeSalesTimesTampMap);
            }
        } catch (JSONException e) {
            try{

            }catch (Exception e1){}
            // MyApplication.timeSalesTimesTamp = "0";
            e.printStackTrace();
        }

        return trades;
    }






    public static ArrayList<TimeSale> GetTradeTicker(String JsonString,boolean isTimeSaleTrade)
            throws JSONException, UnsupportedEncodingException {

        ArrayList<TimeSale> trades = new ArrayList<TimeSale>();

        JSONObject jsonobj = new JSONObject(JsonString);



        //  MyApplication.timeSalesTimesTamp = (Integer.parseInt(MyApplication.timeSalesTimesTamp)-100)+"";

        String CurrentMarketId=MyApplication.marketID;

        try {

            JSONArray jarray = jsonobj.getJSONArray("ExecutedOrderList");
            TimeSale trade;
            int len = jarray.length();

            for (int i = 0; i < len; i++) {
                trade = new TimeSale();
                JSONObject json_data = jarray.getJSONObject(i);

                try {
                    trade.setSecurityId(json_data.getString("SecurityID"));
                } catch (Exception e) {
                    e.printStackTrace();
                    trade.setSecurityId(json_data.getString(""));
                }

                try {
                    trade.setChange(String.valueOf(json_data.getDouble("Change")));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    trade.setChangeIndicator(json_data.getInt("ChangeIndicator"));
                } catch (Exception e) {
                    e.printStackTrace();
                    trade.setChangeIndicator(0);
                }

                try {
                    trade.setQuantity(json_data.getString("Quantity"));
                } catch (Exception e) {
                    e.printStackTrace();
                    trade.setQuantity("0");
                }

                //trade.setOrderTypeId("0");

                try {
                    trade.setPrice(json_data.getString("Price"));
                } catch (Exception e) {
                    trade.setPrice("0");
                }

                trade.setStockID(Integer.parseInt(json_data.getString("StockID")));

                try {
                    trade.setMarketId(json_data.getInt("MarketId"));
                } catch (Exception e) {
                    e.printStackTrace();
                    trade.setMarketId(0);
                }

                trade.setStockSymbolAr(json_data.getString("StockSymbolAr"));

                trade.setStockSymbolEn(json_data.getString("StockSymbolEn"));

                try {
                    trade.setTradeTime(json_data.getString("TradeTime"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    trade.setInstrumentId(json_data.getString("InstrumentId"));
                } catch (Exception e) {
                    e.printStackTrace();
                    trade.setInstrumentId("");
                }

                try {
                    trade.setOrderType(json_data.getInt("ExecuteType"));
                } catch (Exception e) {
                    e.printStackTrace();
                    trade.setInstrumentId("");
                }

                try {
                    trade.setId(String.valueOf(json_data.getInt("TradeID")));
                } catch (Exception e) {
                    e.printStackTrace();
                    trade.setInstrumentId("");
                }



                try{if(i==0){
                    if(json_data.getString("MarketID")!=null && !json_data.getString("MarketID").trim().toLowerCase().matches("null") && !json_data.getString("MarketID").trim().toLowerCase().matches("0"))
                        CurrentMarketId= json_data.getString("MarketID");
                }else {
                    CurrentMarketId = MyApplication.marketID;
                }}catch (Exception e){}


                trades.add(trade);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        return trades;
    }

    public static ArrayList<OnlineOrder> GetOnlineOrders(String JsonString)
            throws JSONException, UnsupportedEncodingException {


        ArrayList<OnlineOrder> allOnlineOrders = new ArrayList<>();

        JSONObject object = new JSONObject(JsonString);

        try {

            JSONArray jarray = object.getJSONArray("OnlineOrderList");
            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jsonObject = jarray.getJSONObject(i);
                OnlineOrder onlineOrder = new OnlineOrder();
                ArrayList<ValueItem> allvalueItems = new ArrayList<ValueItem>();


                try {
                    onlineOrder.setSecurityId(jsonObject.getString("SecurityID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    onlineOrder.setSecurityId(jsonObject.getString(""));
                }

                //integers
                try {
                    onlineOrder.setAdminID(jsonObject.getInt("AdminID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setApplication(jsonObject.getInt("Application"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setBrokerEmployeeID(jsonObject.getInt("BrokerEmployeeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setBrokerID(jsonObject.getInt("BrokerID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setDurationID(jsonObject.getInt("DurationID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setForwardContractID(jsonObject.getInt("ForwardContractID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setID(jsonObject.getInt("ID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setOperationTypeID(jsonObject.getInt("OperationTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setOrderTypeID(jsonObject.getInt("OrderTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setOrderStatusTypeID(jsonObject.getInt("OrderStatusTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                try {
                    onlineOrder.setUserTypeID(jsonObject.getInt("UserTypeId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setUserID(jsonObject.getInt("UserID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setTriggerPriceTypeID(jsonObject.getInt("TriggerPriceTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setTriggerPriceDirectionID(jsonObject.getInt("TriggerPriceDirectionID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setTradingSessionID(jsonObject.getInt("TradingSessionID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setTradeTypeID(jsonObject.getInt("TradeTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.settStamp(jsonObject.getInt("TStamp"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setStatusID(jsonObject.getInt("StatusID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setReference(jsonObject.getInt("Reference"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setQuantity(jsonObject.getInt("Quantity"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setQuantityExecuted(jsonObject.getInt("QuantityExecuted"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //returned as string in service need to be int

                //doubles
                try {
                    onlineOrder.setAveragePrice(jsonObject.getDouble("AveragePrice"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setOldPrice(jsonObject.getDouble(("OldPrice")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setTriggerPrice(jsonObject.getDouble("TriggerPrice"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setPrice(jsonObject.getDouble("Price"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //booleans
                try {
                    onlineOrder.setCanDelete(jsonObject.getInt("CanDelete") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setCanUpdate(jsonObject.getInt("CanUpdate") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setHasErrors(jsonObject.getInt("HasErrors") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setHasUsersRestricted(jsonObject.getInt("HasUsersRestricted") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setInvalidOrder(jsonObject.getInt("IsInvalidOrder") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //strings newly added
                try {
                    onlineOrder.setInvestorID(jsonObject.getString("InvestorID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setStockID(jsonObject.getString("StockID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setTradeID(jsonObject.getString("TradeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setTargetSubID(jsonObject.getString("TargetSubID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setPortfolioNumber(jsonObject.getString("PortfolioNumber"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setOrderNumber(jsonObject.getString("OrderNumber"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setStockName(jsonObject.getString("StockName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setOrderTypeDescription(jsonObject.getString("OrderTypeDescription"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setStockSymbol(jsonObject.getString("StockSymbol"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //strings
                try {
                    onlineOrder.setExecutedDateTime(jsonObject.getString("ExecutedDateTime"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setGoodUntilDate(jsonObject.getString("GoodUntilDate"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    onlineOrder.setGoodUntilDate("");
                }

                try {
                    onlineOrder.setOrderDateTime(jsonObject.getString("OrderDateTime"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setKey(jsonObject.getString("key"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setStatusDescription(jsonObject.getString("StatusDescription"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setRejectionCause(jsonObject.getString("RejectionCause"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setInstrumentID(jsonObject.getString("InstrumentID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                try {
                    onlineOrder.setRelatedOnlineOrderID(jsonObject.getInt("RelatedOnlineOrderID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setAdvancedOrderTypeID(jsonObject.getInt("AdvancedOrderTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setMaxfloor(jsonObject.getInt("MaxFloor"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setSubOrder(jsonObject.getInt("IsSubOrder") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setParentOrder(jsonObject.getInt("IsParentOrder") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setAdvancedOrder(jsonObject.getInt("IsAdvancedOrder") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                try {
                    onlineOrder.setAdvancedOrderTypeDescription(jsonObject.getString("AdvancedOrderTypeDescription"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setRelatedOnlineOrderNumber(jsonObject.getString("RelatedOnlineOrderNumber\n"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JSONArray json_fields = jsonObject.getJSONArray("Fields");
                for (int j = 0; j < json_fields.length(); j++) {

                    JSONObject field = json_fields.getJSONObject(j);
                    ValueItem stockValueItem = new ValueItem(field.getString("Key"), field.getString("Value"));
                    allvalueItems.add(stockValueItem);

                    if (field.getString("Key").equals("Good Until") || field.getString("Key").equals("صالح لغاية")) {

                        try {
                            onlineOrder.setGoodUntilDate(field.getString("Value"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onlineOrder.setGoodUntilDate("");
                        }
                    }

                }
                onlineOrder.setAllvalueItems(allvalueItems);
                allOnlineOrders.add(onlineOrder);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return allOnlineOrders;
    }








    public static ArrayList<OnlineOrder> GetUserOrders(String JsonString)
            throws JSONException, UnsupportedEncodingException {


        ArrayList<OnlineOrder> allOnlineOrders = new ArrayList<>();

        JSONObject object = new JSONObject(JsonString);

        try {

            JSONArray jarray = object.getJSONArray("UserOrders");
            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jsonObject = jarray.getJSONObject(i);
                OnlineOrder onlineOrder = new OnlineOrder();
                ArrayList<ValueItem> allvalueItems = new ArrayList<ValueItem>();


                try {
                    onlineOrder.setSecurityId(jsonObject.getString("SecurityID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    onlineOrder.setSecurityId(jsonObject.getString(""));
                }


                try {
                    onlineOrder.setDurationID(jsonObject.getInt("DurationID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                try {
                    onlineOrder.setID(jsonObject.getInt("ID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setOperationTypeID(jsonObject.getInt("OperationTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setOrderTypeID(jsonObject.getInt("OrderTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setOrderStatusTypeID(jsonObject.getInt("OrderStatusTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                try {
                    onlineOrder.setTradingSessionID(jsonObject.getInt("TradingSessionID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }




                try {
                    onlineOrder.setTradeTypeID(jsonObject.getInt("TradeTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.settStamp(jsonObject.getInt("TStamp"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setStatusID(jsonObject.getInt("StatusID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setReference(jsonObject.getInt("Reference"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setQuantity(jsonObject.getInt("Quantity"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setQuantityExecuted(jsonObject.getInt("QuantityExecuted"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //returned as string in service need to be int

                try {
                    onlineOrder.setStockID(jsonObject.getString("StockID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setPriceFormatted(jsonObject.getString("PriceFormatted"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setOrderNumber(jsonObject.getString("OrderNumber"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setStockName(jsonObject.getString("StockName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setOrderTypeDescription(jsonObject.getString("OrderTypeDescription"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setStockSymbol(jsonObject.getString("StockSymbol"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                try {
                    onlineOrder.setOrderDateTime(jsonObject.getString("OrderDateTime"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                try {
                    onlineOrder.setStatusDescription(jsonObject.getString("StatusDescription"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                try {
                    onlineOrder.setInstrumentID(jsonObject.getString("InstrumentID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                try {
                    onlineOrder.setRelatedOnlineOrderID(jsonObject.getInt("RelatedOnlineOrderID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setAdvancedOrderTypeID(jsonObject.getInt("AdvancedOrderTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setMaxfloor(jsonObject.getInt("MaxFloor"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setNameEn(jsonObject.getString("nameEn"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    onlineOrder.setNameAr(jsonObject.getString("nameAr"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setSubOrder(jsonObject.getInt("IsSubOrder") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setParentOrder(jsonObject.getInt("IsParentOrder") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setAdvancedOrder(jsonObject.getInt("IsAdvancedOrder") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                try {
                    onlineOrder.setAdvancedOrderTypeDescription(jsonObject.getString("AdvancedOrderTypeDescription"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setRelatedOnlineOrderNumber(jsonObject.getString("RelatedOnlineOrderNumber"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setPrice(jsonObject.getDouble("Price"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setTradeTypeDescription(jsonObject.getString("TradeTypeDescription"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setMarketID(jsonObject.getInt("MarketID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }



           /*     Price
                TradeTypeDescription
                MarketID*/


                allOnlineOrders.add(onlineOrder);
            }
            String CurrentMarketId=MyApplication.marketID;
            try {
                Log.wtf("GlobaleFunctions GetTimeSales Timestamp", "is: " + object.getString("MaxTimeStamp"));

                if (!object.getString("MaxTimeStamp").equals("0")  && !object.getString("MaxTimeStamp").trim().matches("null") && object.getString("MaxTimeStamp")!=null){

                    MyApplication.userOrderTimesTamMap.put(CurrentMarketId,object.getString("MaxTimeStamp"));
                }
            } catch (JSONException e) {
                try{

                }catch (Exception e1){}
                // MyApplication.timeSalesTimesTamp = "0";
                e.printStackTrace();
            }



        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return allOnlineOrders;
    }




    public static OnlineOrder LoadOnlineOrder(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        JSONObject jsonObject=new JSONObject();
        OnlineOrder onlineOrder = new OnlineOrder();

        JSONObject object = new JSONObject(JsonString);
        jsonObject = object.getJSONObject("OnlineOrder");
        onlineOrder=getOnlineOrder(jsonObject);
        ArrayList<OnlineOrder> allChildsOrder = new ArrayList<OnlineOrder>();


        JSONArray json_childs = jsonObject.getJSONArray("Childs");
        for (int j = 0; j < json_childs.length(); j++) {
            OnlineOrder childOrder = new OnlineOrder();
            childOrder=getOnlineOrder(json_childs.getJSONObject(j));
            allChildsOrder.add(childOrder);


        }
        onlineOrder.setArraySubOrders(allChildsOrder);

/*        try {

             jsonObject = object.getJSONObject("OnlineOrder");




                ArrayList<ValueItem> allvalueItems = new ArrayList<ValueItem>();


                try {
                    onlineOrder.setSecurityId(jsonObject.getString("SecurityID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    onlineOrder.setSecurityId(jsonObject.getString(""));
                }

                //integers
                try {
                    onlineOrder.setAdminID(jsonObject.getInt("AdminID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setApplication(jsonObject.getInt("Application"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setBrokerEmployeeID(jsonObject.getInt("BrokerEmployeeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setBrokerID(jsonObject.getInt("BrokerID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setDurationID(jsonObject.getInt("DurationID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setForwardContractID(jsonObject.getInt("ForwardContractID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setID(jsonObject.getInt("ID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setOperationTypeID(jsonObject.getInt("OperationTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setOrderTypeID(jsonObject.getInt("OrderTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setOrderStatusTypeID(jsonObject.getInt("OrderStatusTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                try {
                    onlineOrder.setUserTypeID(jsonObject.getInt("UserTypeId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setUserID(jsonObject.getInt("UserID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setTriggerPriceTypeID(jsonObject.getInt("TriggerPriceTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setTriggerPriceDirectionID(jsonObject.getInt("TriggerPriceDirectionID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setTradingSessionID(jsonObject.getInt("TradingSessionID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setTradeTypeID(jsonObject.getInt("TradeTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.settStamp(jsonObject.getInt("TStamp"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setStatusID(jsonObject.getInt("StatusID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setReference(jsonObject.getInt("Reference"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setQuantity(jsonObject.getInt("Quantity"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setQuantityExecuted(jsonObject.getInt("QuantityExecuted"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //returned as string in service need to be int

                //doubles
                try {
                    onlineOrder.setAveragePrice(jsonObject.getDouble("AveragePrice"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setOldPrice(jsonObject.getDouble(("OldPrice")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setTriggerPrice(jsonObject.getDouble("TriggerPrice"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setPrice(jsonObject.getDouble("Price"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //booleans
                try {
                    onlineOrder.setCanDelete(jsonObject.getInt("CanDelete") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setCanUpdate(jsonObject.getInt("CanUpdate") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setHasErrors(jsonObject.getInt("HasErrors") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setHasUsersRestricted(jsonObject.getInt("HasUsersRestricted") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setInvalidOrder(jsonObject.getInt("IsInvalidOrder") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //strings newly added
                try {
                    onlineOrder.setInvestorID(jsonObject.getString("InvestorID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setStockID(jsonObject.getString("StockID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setTradeID(jsonObject.getString("TradeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setTargetSubID(jsonObject.getString("TargetSubID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setPortfolioNumber(jsonObject.getString("PortfolioNumber"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setOrderNumber(jsonObject.getString("OrderNumber"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setStockName(jsonObject.getString("StockName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setOrderTypeDescription(jsonObject.getString("OrderTypeDescription"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setStockSymbol(jsonObject.getString("StockSymbol"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //strings
                try {
                    onlineOrder.setExecutedDateTime(jsonObject.getString("ExecutedDateTime"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setGoodUntilDate(jsonObject.getString("GoodUntilDate"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    onlineOrder.setGoodUntilDate("");
                }

                try {
                    onlineOrder.setOrderDateTime(jsonObject.getString("OrderDateTime"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setKey(jsonObject.getString("key"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setStatusDescription(jsonObject.getString("StatusDescription"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setRejectionCause(jsonObject.getString("RejectionCause"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setInstrumentID(jsonObject.getString("InstrumentID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                try {
                    onlineOrder.setRelatedOnlineOrderID(jsonObject.getInt("RelatedOnlineOrderID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setAdvancedOrderTypeID(jsonObject.getInt("AdvancedOrderTypeID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setMaxfloor(jsonObject.getInt("MaxFloor"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setSubOrder(jsonObject.getInt("IsSubOrder") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setParentOrder(jsonObject.getInt("IsParentOrder") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    onlineOrder.setAdvancedOrder(jsonObject.getInt("IsAdvancedOrder") != 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                try {
                    onlineOrder.setAdvancedOrderTypeDescription(jsonObject.getString("AdvancedOrderTypeDescription"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    onlineOrder.setRelatedOnlineOrderNumber(jsonObject.getString("RelatedOnlineOrderNumber\n"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                JSONArray json_fields = jsonObject.getJSONArray("Fields");
                for (int j = 0; j < json_fields.length(); j++) {

                    JSONObject field = json_fields.getJSONObject(j);
                    ValueItem stockValueItem = new ValueItem(field.getString("Key"), field.getString("Value"));
                    allvalueItems.add(stockValueItem);

                    if (field.getString("Key").equals("Good Until") || field.getString("Key").equals("صالح لغاية")) {

                        try {
                            onlineOrder.setGoodUntilDate(field.getString("Value"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onlineOrder.setGoodUntilDate("");
                        }
                    }

                }
                onlineOrder.setAllvalueItems(allvalueItems);




        } catch (Exception ex) {
            ex.printStackTrace();
        }*/


        return onlineOrder;
    }



    public static OnlineOrder getOnlineOrder(JSONObject json){
        JSONObject jsonObject=new JSONObject();
        OnlineOrder onlineOrder = new OnlineOrder();



        try {

            jsonObject =json;




            ArrayList<ValueItem> allvalueItems = new ArrayList<ValueItem>();


            try {
                onlineOrder.setSecurityId(jsonObject.getString("SecurityID"));
            } catch (JSONException e) {
                e.printStackTrace();
                onlineOrder.setSecurityId(jsonObject.getString(""));
            }

            //integers
            try {
                onlineOrder.setAdminID(jsonObject.getInt("AdminID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setApplication(jsonObject.getInt("Application"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setBrokerEmployeeID(jsonObject.getInt("BrokerEmployeeID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setBrokerID(jsonObject.getInt("BrokerID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setDurationID(jsonObject.getInt("DurationID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setForwardContractID(jsonObject.getInt("ForwardContractID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setID(jsonObject.getInt("ID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setOperationTypeID(jsonObject.getInt("OperationTypeID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setOrderTypeID(jsonObject.getInt("OrderTypeID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                onlineOrder.setOrderStatusTypeID(jsonObject.getInt("OrderStatusTypeID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }



            try {
                onlineOrder.setUserTypeID(jsonObject.getInt("UserTypeId"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setUserID(jsonObject.getInt("UserID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setTriggerPriceTypeID(jsonObject.getInt("TriggerPriceTypeID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setTriggerPriceDirectionID(jsonObject.getInt("TriggerPriceDirectionID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setTradingSessionID(jsonObject.getInt("TradingSessionID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setTradeTypeID(jsonObject.getInt("TradeTypeID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.settStamp(jsonObject.getInt("TStamp"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setStatusID(jsonObject.getInt("StatusID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setReference(jsonObject.getInt("Reference"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setQuantity(jsonObject.getInt("Quantity"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setQuantityExecuted(jsonObject.getInt("QuantityExecuted"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //returned as string in service need to be int

            //doubles
            try {
                onlineOrder.setAveragePrice(jsonObject.getDouble("AveragePrice"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setOldPrice(jsonObject.getDouble(("OldPrice")));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setTriggerPrice(jsonObject.getDouble("TriggerPrice"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setPrice(jsonObject.getDouble("Price"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //booleans
            try {
                onlineOrder.setCanDelete(jsonObject.getInt("CanDelete") != 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setCanUpdate(jsonObject.getInt("CanUpdate") != 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setHasErrors(jsonObject.getInt("HasErrors") != 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setHasUsersRestricted(jsonObject.getInt("HasUsersRestricted") != 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setInvalidOrder(jsonObject.getInt("IsInvalidOrder") != 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //strings newly added
            try {
                onlineOrder.setInvestorID(jsonObject.getString("InvestorID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setStockID(jsonObject.getString("StockID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setTradeID(jsonObject.getString("TradeID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setTargetSubID(jsonObject.getString("TargetSubID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setPortfolioNumber(jsonObject.getString("PortfolioNumber"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setOrderNumber(jsonObject.getString("OrderNumber"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setStockName(jsonObject.getString("StockName"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                onlineOrder.setOrderTypeDescription(jsonObject.getString("OrderTypeDescription"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                onlineOrder.setStockSymbol(jsonObject.getString("StockSymbol"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //strings
            try {
                onlineOrder.setExecutedDateTime(jsonObject.getString("ExecutedDateTime"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setGoodUntilDate(jsonObject.getString("GoodUntilDate"));
            } catch (JSONException e) {
                e.printStackTrace();
                onlineOrder.setGoodUntilDate("");
            }

            try {
                onlineOrder.setOrderDateTime(jsonObject.getString("OrderDateTime"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setKey(jsonObject.getString("key"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setStatusDescription(jsonObject.getString("StatusDescription"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setRejectionCause(jsonObject.getString("RejectionCause"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setInstrumentID(jsonObject.getString("InstrumentID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }



            try {
                onlineOrder.setRelatedOnlineOrderID(jsonObject.getInt("RelatedOnlineOrderID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setAdvancedOrderTypeID(jsonObject.getInt("AdvancedOrderTypeID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                onlineOrder.setMaxfloor(jsonObject.getInt("MaxFloor"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                onlineOrder.setSubOrder(jsonObject.getInt("IsSubOrder") != 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                onlineOrder.setParentOrder(jsonObject.getInt("IsParentOrder") != 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                onlineOrder.setAdvancedOrder(jsonObject.getInt("IsAdvancedOrder") != 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }



            try {
                onlineOrder.setAdvancedOrderTypeDescription(jsonObject.getString("AdvancedOrderTypeDescription"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                onlineOrder.setRelatedOnlineOrderNumber(jsonObject.getString("RelatedOnlineOrderNumber\n"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            JSONArray json_fields = jsonObject.getJSONArray("Fields");
            for (int j = 0; j < json_fields.length(); j++) {

                JSONObject field = json_fields.getJSONObject(j);
                ValueItem stockValueItem = new ValueItem(field.getString("Key"), field.getString("Value"));
                allvalueItems.add(stockValueItem);

                if (field.getString("Key").equals("Good Until") || field.getString("Key").equals("صالح لغاية")) {

                    try {
                        onlineOrder.setGoodUntilDate(field.getString("Value"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        onlineOrder.setGoodUntilDate("");
                    }
                }

            }
            onlineOrder.setAllvalueItems(allvalueItems);




        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return onlineOrder;
    }


    public static ArrayList<StockOrderBook> GetStockOrderBooks(String JsonString)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<StockOrderBook> stockOrderBooks = new ArrayList<StockOrderBook>();


        JSONObject jsonobj = new JSONObject(JsonString);
        try {
            JSONArray jarray = jsonobj.getJSONArray("PendingOrderDisplayList");
            for (int i = 0; i < jarray.length(); i++) {

                try {

                    StockOrderBook stock = new StockOrderBook();
                    JSONObject json_data = jarray.getJSONObject(i);

                    try {
                        stock.setSecurityId(json_data.getString("SecurityID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stock.setSecurityId(json_data.getString(""));
                    }

                    try {
                        stock.setId(json_data.getInt("ID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setAsk(json_data.getString("Ask"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setBid(json_data.getString("Bid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setTradeType(json_data.getInt("TradeType"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setPrice(json_data.getString("Price"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setStockid(json_data.getString("StockID").equals("null") ? "0" : json_data.getString("StockID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setQuantity(json_data.getString("Quantity").equals("null") ? "0" : json_data.getString("Quantity"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setAskQuantity(json_data.getString("AskQuantity").equals("null") ? "0" : json_data.getString("AskQuantity"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setAskValue(stock.getAskQuantity().replace(",", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                        stock.setAskValue("");
                    }

                    try {
                        stock.setBidQuantity(json_data.getString("BidQuantity").equals("null") ? "0" : json_data.getString("BidQuantity"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setBidValue(stock.getBidQuantity().replace(",", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                        stock.setBidValue("");
                    }


                    try {
                        stock.setOrderTime(json_data.getString("OrderTime").equals("null") ? "" : json_data.getString("OrderTime"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    stockOrderBooks.add(stock);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return stockOrderBooks;
    }







    public static ArrayList<StockOrderBook> GetStockOrderBooksByPrice(String JsonString)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<StockOrderBook> stockOrderBooks = new ArrayList<StockOrderBook>();


        JSONObject jsonobj = new JSONObject(JsonString);
        try {
            JSONArray jarray = jsonobj.getJSONArray("PendingOrderByPriceDisplayList");
            for (int i = 0; i < jarray.length(); i++) {

                try {

                    StockOrderBook stock = new StockOrderBook();
                    JSONObject json_data = jarray.getJSONObject(i);



                    try {
                        stock.setAsk(json_data.getString("Ask"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setBid(json_data.getString("Bid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




                    try {
                        stock.setStockid(json_data.getString("StockID").equals("null") ? "0" : json_data.getString("StockID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




                    try {
                        stock.setBidQuantity(json_data.getString("BidQuantity").equals("null") ? "0" : json_data.getString("BidQuantity"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stock.setAskQuantity(json_data.getString("AskQuantity").equals("null") ? "0" : json_data.getString("AskQuantity"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stock.setBidValue(json_data.getString("BidPrice").equals("null") ? "0.0" : json_data.getString("BidPrice"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        stock.setBidValue("");
                    }


                    try {
                        stock.setAskValue(json_data.getString("AskPrice").equals("null") ? "0.0" : json_data.getString("AskPrice"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        stock.setAskValue("");
                    }



                    stockOrderBooks.add(stock);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return stockOrderBooks;
    }




    public static Portfolio GetPortfolio(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        Portfolio portfolio = new Portfolio();
        CashSummary cashSummary = new CashSummary();
        ArrayList<ForwardContract> forwardContracts = new ArrayList<>();
        ArrayList<StockSummary> stockSummaries = new ArrayList<>();
        ArrayList<ValueItem> allvalueItems = new ArrayList<ValueItem>();

        JSONObject object = new JSONObject(JsonString);
        String msgdata = object.getString("ResponseMessage");

        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");
        portfolio.setMessageEn(success);
        String successAr = jsondata_msg.getString("MessageAr");
        portfolio.setMessageAr(successAr);

        try {

            portfolio.setLastStatementFilePath(object.getString("LastStatementFilePath"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (success.equals("Success")) {

            try {

                String cashSummaryString = object.getString("CashSummary");

                JSONObject jsondata_cashSummary = new JSONObject(cashSummaryString);

                JSONArray json_fields = jsondata_cashSummary.getJSONArray("Fields");
                for (int j = 0; j < json_fields.length(); j++) {

                    JSONObject field = json_fields.getJSONObject(j);
                    ValueItem stockValueItem = new ValueItem(field.getString("Key"), field.getString("Value"));
                    allvalueItems.add(stockValueItem);
                }
                cashSummary.setAllvalueItems(allvalueItems);

                try {
                    cashSummary.setAllowBanks(jsondata_cashSummary.getBoolean("AllowBanks"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    cashSummary.setBankAccountBalance(jsondata_cashSummary.getString("BankAccountBalance"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    cashSummary.setCredit(jsondata_cashSummary.getString("Credit"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    cashSummary.setDebit(jsondata_cashSummary.getString("Debit"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    cashSummary.setLimit(jsondata_cashSummary.getString("Limit"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    cashSummary.setPendingPurchaseOrders(jsondata_cashSummary.getString("PendingPurchaseOrders"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    cashSummary.setPurchasePower(jsondata_cashSummary.getString("PurchasePower"));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                portfolio.setCashSummary(cashSummary);

            } catch (Exception e) {

                e.printStackTrace();
            }

            //<editor-fold desc = "forward contract">
            /*try {

                JSONArray jarray = object.getJSONArray("ForwardContractList");
                for (int i = 0; i < jarray.length(); i++) {

                    JSONObject jsonObject = jarray.getJSONObject(i);
                    ForwardContract forwardContract = new ForwardContract();

                    try {
                        forwardContract.setId(jsonObject.getInt("ID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        forwardContract.setStockId(jsonObject.getInt("StockID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        forwardContract.setContractNo(jsonObject.getInt("ContractNo"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        forwardContract.setOriginalQty(jsonObject.getInt("OriginalQty"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        forwardContract.setRemainingQty(jsonObject.getInt("RemainingQty"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        forwardContract.setBreakPrice(jsonObject.getDouble("BreakPrice"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        forwardContract.setCostPrice(jsonObject.getDouble("CostPrice"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        forwardContract.setDebit(jsonObject.getDouble("Debit"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        forwardContract.setStockSymbolEn(jsonObject.getString("StockSymbolEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        forwardContract.setStockSymbolEn("");
                    }

                    try {
                        forwardContract.setStockSymbolAr(jsonObject.getString("StockSymbolAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        forwardContract.setStockSymbolAr("");
                    }

                    try {
                        forwardContract.setContactDate(jsonObject.getString("ContractDate"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        forwardContract.setDueDate(jsonObject.getString("DueDate"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    forwardContracts.add(forwardContract);
                }
                portfolio.setAllForwardContracts(forwardContracts);

            } catch (Exception ex) {
                ex.printStackTrace();
            }*/
            //</editor-fold>


            try {

                JSONArray jarray = object.getJSONArray("StockSummaryList");
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jsonObject = jarray.getJSONObject(i);
                    StockSummary stockSummary = new StockSummary();

                    //stockSummary.setId(object.getInt("ID"));

                    try {
                        stockSummary.setAvailableShares(jsonObject.getString("AvailableShares"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockSummary.setStockId(jsonObject.getString("StockID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockSummary.setSecurityId(jsonObject.getString("SecurityID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockSummary.setSecurityId(jsonObject.getString(""));
                    }

                    try {
                        stockSummary.setShareCount(jsonObject.getString("ShareCount"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockSummary.setAverageCost(jsonObject.getString("AverageCost"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockSummary.setBid(jsonObject.getString("Bid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockSummary.setLast(jsonObject.getString("Last"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stockSummary.setPendingOrders(jsonObject.getString("PendingOrders"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockSummary.setTotalCost(jsonObject.getString("TotalCost"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockSummary.setTotalMarket(jsonObject.getString("TotalMarket"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockSummary.setUnrealized(jsonObject.getString("Unrealized"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockSummary.setBreakPrice(jsonObject.getString("BreakPrice"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        stockSummary.setUnrealizedPercent(jsonObject.getString("UnrealizedPercent"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockSummary.setSymbolEn(jsonObject.getString("StockSymbolEn").equals("null") ? "" : jsonObject.getString("StockSymbolEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockSummary.setSymbolEn("");
                    }
                    try {
                        stockSummary.setSymbolAr(jsonObject.getString("StockSymbolAr").equals("null") ? "" : jsonObject.getString("StockSymbolAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockSummary.setSymbolAr("");
                    }

                    try {
                        stockSummary.setNameAr(jsonObject.getString("StockNameAr").equals("null") ? "" : jsonObject.getString("StockNameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockSummary.setNameAr("");
                    }


                    try {
                        stockSummary.setNameEn(jsonObject.getString("StockNameEn").equals("null") ? "" : jsonObject.getString("StockNameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockSummary.setNameEn("");
                    }
                    stockSummaries.add(stockSummary);
                }

                portfolio.setAllStockSummaries(stockSummaries);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return portfolio;
    }


    public static Trade GetTradeInfo(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        Trade trade = new Trade();
        StockQuotation stockQuotation = new StockQuotation();

        Log.wtf("trade", "result = " + JsonString);

        JSONObject object = new JSONObject(JsonString);
        String msgdata = object.getString("ResponseMessage");

        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");
        String successAr = jsondata_msg.getString("MessageAr");

        if (success.equals("Success")) {

            try {

                try {
                    trade.setAvailableShareCount(object.getInt("AvailableShareCount"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    trade.setAvailableShareCount(0);
                }

                try {
                    trade.setPurchasePower(object.getDouble("PurchasePower"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    trade.setPurchasePower(0);
                }

                try {


                    JSONObject json_data = object.getJSONObject("StockQuotation");


                    //<editor-fold desc="Stock Quotation">
                    try {
                        stockQuotation.setSecurityId(json_data.getString("SecurityID"));
                    } catch (JSONException e) {
                        Log.wtf("Trade json_data", "error :" + e.getMessage());
                        stockQuotation.setSecurityId("");
                    }

                    try {
                        stockQuotation.setNormalMarketSize(json_data.getInt("NormalMarketSize"));
                    } catch (JSONException e) {
                        Log.wtf("Trade json_data", "error :" + e.getMessage());
                        stockQuotation.setNormalMarketSize(0);
                    }

                    try {
                        stockQuotation.setAmount(json_data.getInt("Amount"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setAsk(json_data.getDouble("Ask"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setBid(json_data.getDouble("Bid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setChange(json_data.getString("Change").equals("0") ? "0.0" : json_data.getString("Change"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setHiLimit(json_data.getDouble("HiLimit"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setHigh(json_data.getString("High"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setIslamic(json_data.getBoolean("IsIslamic"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setLast(json_data.getDouble("Last"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setPreviousClosing(json_data.getDouble("PreviousClosing"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setLow(json_data.getString("Low"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setLowlimit(Double.parseDouble(json_data.getString("LowLimit")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setOpen(json_data.getInt("Open"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setStockID(json_data.getInt("StockID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setStockTradingStatus(json_data.getInt("StockTradingStatus"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setTrade(json_data.getInt("Trades"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setVolume(json_data.getInt("Volume"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setVolumeAsk(json_data.getInt("VolumeAsk"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setVolumeBid(json_data.getInt("VolumeBid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setNameAr(json_data.getString("NameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setNameEn(json_data.getString("NameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setSymbolAr(json_data.getString("SymbolAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setSymbolEn(json_data.getString("SymbolEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setInstrumentId(json_data.getString("InstrumentId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setInstrumentNameAr(json_data.getString("InstrumentNameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setInstrumentNameEn(json_data.getString("InstrumentNameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setSessionId(json_data.getString("SessionId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setTickDirection(Double.parseDouble(json_data.getString("TickDirection")));
                    } catch (Exception e) {
                        e.printStackTrace();
                        stockQuotation.setTickDirection(0.0);
                    }

                    try {
                        stockQuotation.setSessionNameAr(json_data.getString("SessionNameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setSessionNameEn(json_data.getString("SessionNameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setSessionNameEn(json_data.getString("SessionNameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setSectorID(json_data.getString("SectorID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        stockQuotation.setReferencePrice(json_data.getDouble("ReferencePrice"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //</editor-fold>


                    trade.setStockQuotation(stockQuotation);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return trade;
    }




    public static ArrayList<Ordertypes> GetOrderTypes(String JsonString)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<Ordertypes> AllOrderTypes = new ArrayList<Ordertypes>();

        JSONObject object = new JSONObject(JsonString);

        String msgdata = object.getString("ResponseMessage");

        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONArray("orderTypeList");
                Ordertypes orderTypes;

                int len = jarray.length();

                for (int i = 0; i < len; i++) {
                    JSONObject json_data = jarray.getJSONObject(i);
                    orderTypes = new Ordertypes();


                    try {
                        orderTypes.setDescriptionEn(json_data.getString("DescriptionEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        orderTypes.setDescriptionEn("");
                    }



                    try {
                        orderTypes.setDescriptionAr(json_data.getString("DescriptionAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        orderTypes.setDescriptionAr("");
                    }


                    try {
                        orderTypes.setOrderTypeID(json_data.getInt("OrderTypeID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        orderTypes.setEnabled(json_data.getInt("Enabled"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        orderTypes.setIsEditable(json_data.getInt("IsEditable"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    try {
                        orderTypes.setIsAdvanced(json_data.getInt("IsAdvanced"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //</editor-fold>

                    AllOrderTypes.add(orderTypes);


                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return AllOrderTypes;
    }











    public static ArrayList<OffMarketQuotes> GetOffMarketQuotes(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        ArrayList<OffMarketQuotes> listMarketQuotes = new ArrayList<>();

        Log.wtf("GetOffMarketQuotes", "result = " + JsonString);

        JSONObject object = new JSONObject(JsonString);
        String msgdata = object.getString("ResponseMessage");

        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {

            try {
                JSONArray jarray = object.getJSONArray("OffMarketQuotes");

                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jsonObject = jarray.getJSONObject(i);
                    OffMarketQuotes marketQuotes = new OffMarketQuotes();

                    try {
                        marketQuotes.setInstrumentId(jsonObject.getString("InstrumentId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setInstrumentId("0");
                    }

                    try {
                        marketQuotes.setInstrumentNameAr(jsonObject.getString("InstrumentNameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setInstrumentNameAr("");
                    }

                    try {
                        marketQuotes.setInstrumentNameEn(jsonObject.getString("InstrumentNameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setInstrumentNameEn("");
                    }

                    try {
                        marketQuotes.setMarketType(jsonObject.getString("MarketType"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setMarketType("");
                    }

                    try {
                        marketQuotes.setNameAr(jsonObject.getString("NameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setNameAr("");
                    }

                    try {
                        marketQuotes.setNameEn(jsonObject.getString("NameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setNameEn("");
                    }

                    try {
                        marketQuotes.setOffMarketLastPrice(jsonObject.getString("OffMarketLastPrice"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setOffMarketLastPrice("");
                    }

                    try {
                        marketQuotes.setOffMarketLastQuantity(jsonObject.getString("OffMarketLastQuantity"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setOffMarketLastQuantity("");
                    }

                    try {
                        marketQuotes.setOffMarketValueToday(jsonObject.getString("OffMarketValueToday"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setOffMarketValueToday("");
                    }

                    try {
                        marketQuotes.setOffMarketVolumeToday(jsonObject.getString("OffMarketVolumeToday"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setOffMarketVolumeToday("");
                    }

                    try {
                        marketQuotes.setSectorID(jsonObject.getString("SectorID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setSectorID("");
                    }

                    try {
                        marketQuotes.setSectorNameAr(jsonObject.getString("SectorNameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setSectorNameAr("");
                    }
                    try {
                        marketQuotes.setSectorNameEn(jsonObject.getString("SectorNameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setSectorNameEn("");
                    }

                    try {
                        marketQuotes.setSectorID(jsonObject.getString("SecurityID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setSectorID("");
                    }

                    try {
                        marketQuotes.setStockID(jsonObject.getString("StockID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setStockID("");
                    }

                    try {
                        marketQuotes.setSymbolAr(jsonObject.getString("SymbolAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setSymbolAr("");
                    }

                    try {
                        marketQuotes.setSymbolEn(jsonObject.getString("SymbolEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        marketQuotes.setSymbolEn("");
                    }

                    listMarketQuotes.add(marketQuotes);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return listMarketQuotes;
    }

    public static NotificationSettings GetUserNotificationSettings(String JsonString) throws JSONException, UnsupportedEncodingException {


        NotificationSettings notification=new NotificationSettings();

        JSONObject object = new JSONObject(JsonString);
        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {

            String notificationObject = object.getString("NotificationSettings");
            JSONObject jsondata_suscriber = new JSONObject(notificationObject);

            try {
                notification.setBuy(jsondata_suscriber.getBoolean("Buy"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                notification.setCashEvents(jsondata_suscriber.getBoolean("CashEvents"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                notification.setSell(jsondata_suscriber.getBoolean("Sell"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                notification.setStockEvents(jsondata_suscriber.getBoolean("StockEvents"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                notification.setLang(jsondata_suscriber.getInt("Lang"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                notification.setUserID(jsondata_suscriber.getInt("UserID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                notification.setKey(jsondata_suscriber.getString("key"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return notification;


    }



    public static ArrayList<StockAlerts> GetStockAlerts(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        JSONObject object = new JSONObject(JsonString);

        ArrayList<StockAlerts> stockAlerts = new ArrayList<>();

        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");
        if (success.equals("Success")) {

            try {

                JSONArray jsonArray = object.getJSONArray("StockAlerts");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject json_data = jsonArray.getJSONObject(i);
                    StockAlerts stockAlert = new StockAlerts();

                    try {
                        stockAlert.setDate(json_data.getString("Date"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setDate("");
                    }


                    try {
                        stockAlert.setExecutionDate(json_data.getString("ExecutionDate"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setExecutionDate("");
                    }

                    try {
                        stockAlert.setID(json_data.getInt("ID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setID(0);
                    }


                    try {
                        stockAlert.setOperatorDescriptionAr(json_data.getString("OperatorDescriptionAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setOperatorDescriptionAr("");
                    }


                    try {
                        stockAlert.setOperatorDescriptionEn(json_data.getString("OperatorDescriptionEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setOperatorDescriptionEn("");
                    }


                    try {
                        stockAlert.setOperatorID(json_data.getInt("OperatorID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setOperatorID(0);
                    }


                    try {
                        stockAlert.setStatusDescriptionAr(json_data.getString("StatusDescriptionAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setStatusDescriptionAr("");
                    }


                    try {
                        stockAlert.setStatusDescriptionEn(json_data.getString("StatusDescriptionEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setStatusDescriptionEn("");
                    }


                    try {
                        stockAlert.setStatusID(json_data.getInt("StatusID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setStatusID(0);
                    }


                    try {
                        stockAlert.setStockID(json_data.getString("StockID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setStockID("");
                    }


                    try {
                        stockAlert.setSybmolAr(json_data.getString("SybmolAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setSybmolAr("");
                    }

                    try {
                        stockAlert.setSybmolEn(json_data.getString("SybmolEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setSybmolEn("");
                    }

                    try {
                        stockAlert.setTypeDescriptionEn(json_data.getString("TypeDescriptionEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setTypeDescriptionEn("");
                    }

                    try {
                        stockAlert.setTypeDescriptionAr(json_data.getString("TypeDescriptionAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setTypeDescriptionAr("");
                    }

                    try {
                        stockAlert.setTypeID(json_data.getInt("TypeID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setTypeID(0);
                    }

                    try {
                        stockAlert.setUserID(json_data.getInt("UserID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setUserID(0);
                    }

                    try {
                        stockAlert.setValue(json_data.getInt("Value"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        stockAlert.setValue(0);
                    }




                    stockAlerts.add(stockAlert);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stockAlerts;
    }





    public static ArrayList<RequestTicketTypes> GetClientTypes(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        JSONObject object = new JSONObject(JsonString);

        ArrayList<RequestTicketTypes> arrayTypes = new ArrayList<>();

        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");
        if (success.equals("Success")) {

            try {

                JSONArray jsonArray = object.getJSONArray("CashTicketTypes");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject json_data = jsonArray.getJSONObject(i);
                    RequestTicketTypes ticketTypes = new RequestTicketTypes();

                    try {
                        ticketTypes.setDescriptionEn(json_data.getString("DescriptionEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ticketTypes.setDescriptionEn("");
                    }

                    try {
                        ticketTypes.setDescriptionAr(json_data.getString("DescriptionAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ticketTypes.setDescriptionAr("");
                    }

                    try {
                        ticketTypes.setCashTicketTypeID(json_data.getInt("CashTicketTypeID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ticketTypes.setCashTicketTypeID(0);
                    }

                    arrayTypes.add(ticketTypes);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return arrayTypes;
    }




    public static ArrayList<ResponseRequestTickets> GetRequestTickets(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        JSONObject object = new JSONObject(JsonString);

        ArrayList<ResponseRequestTickets> arrayRequests = new ArrayList<>();

        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");
        if (success.equals("Success")) {

            try {

                JSONArray jsonArray = object.getJSONArray("CashTransactionRequestList");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject json_data = jsonArray.getJSONObject(i);
                    ResponseRequestTickets request = new ResponseRequestTickets();

                    try {
                        request.setAmount(json_data.getString("Amount"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        request.setAmount("");
                    }

                    try {
                        request.setTicketDate(json_data.getString("TicketDate"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        request.setTicketDate("");
                    }

                    try {
                        request.setStatusID(json_data.getInt("StatusID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        request.setStatusID(0);
                    }

                    try {
                        request.setTypeID(json_data.getInt("TypeID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        request.setTypeID(0);
                    }

                    try {
                        request.setStatusDescription(json_data.getString("StatusDescription"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        request.setStatusDescription("");
                    }

                    try {
                        request.setTypeDescription(json_data.getString("TypeDescription"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        request.setTypeDescription("");
                    }

                    try {
                        request.setCanCancel(json_data.getBoolean("CanCancel"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        request.setCanCancel(false);
                    }

                    arrayRequests.add(request);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return arrayRequests;
    }






    public static ArrayList<NotificationItem> GetNotifications(String JsonString)
            throws JSONException, UnsupportedEncodingException {
        ArrayList<NotificationItem> notificationItems = new ArrayList<NotificationItem>();

        JSONObject object = new JSONObject(JsonString);


        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONArray("NotificationList");
                Log.wtf("NotificationList", "Count = " + jarray.length());
                for (int i = 0; i < jarray.length(); i++) {
                    NotificationItem notItem = new NotificationItem();
                    JSONObject json_data = jarray.getJSONObject(i);

                    try {
                        notItem.setDateSent(json_data.getString("DateSent"));
                    } catch (JSONException e) {
                        notItem.setDateSent("");
                        e.printStackTrace();
                    }

                    try {
                        notItem.setDeviceId(json_data.getInt("DeviceId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        notItem.setSent(json_data.getBoolean("IsSent"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    try {
                        notItem.setMessage(json_data.getString("Message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        notItem.setNotes(json_data.getString("Notes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




                    try {
                        notItem.setNotificationId(json_data.getInt("NotificationId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    notificationItems.add(notItem);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return notificationItems;
    }




    public static ArrayList<Lookups> GetLookups(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        JSONObject object = new JSONObject(JsonString);

        ArrayList<Lookups> lookups = new ArrayList<>();

        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");
        if (success.equals("Success")) {

            try {

                JSONArray jsonArray = object.getJSONArray("Lookups");

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject json_data = jsonArray.getJSONObject(i);
                    Lookups lookup = new Lookups();

                    try {
                        lookup.setDescriptionAr(json_data.getString("DescriptionAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        lookup.setDescriptionAr("");
                    }


                    try {
                        lookup.setDescriptionEn(json_data.getString("DescriptionEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        lookup.setDescriptionEn("");
                    }



                    try {
                        lookup.setNameAr(json_data.getString("NameAr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        lookup.setNameAr("");
                    }



                    try {
                        lookup.setNameEn(json_data.getString("NameEn"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        lookup.setNameEn("");
                    }



                    try {
                        lookup.setID(json_data.getInt("ID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        lookup.setID(0);
                    }

                    try {
                        lookup.setValue(json_data.getInt("Value"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        lookup.setValue(0);
                    }



                    lookups.add(lookup);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lookups;
    }



}
