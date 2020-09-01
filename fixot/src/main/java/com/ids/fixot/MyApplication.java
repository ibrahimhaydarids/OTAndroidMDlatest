package com.ids.fixot;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import androidx.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.ids.fixot.classes.ReportPageSetup;
import com.ids.fixot.enums.enums;
import com.ids.fixot.model.ApplicationList;
import com.ids.fixot.model.BrokerageFee;
import com.ids.fixot.model.Instrument;
import com.ids.fixot.model.Lookups;
import com.ids.fixot.model.MarketStatus;
import com.ids.fixot.model.NotificationSettings;
import com.ids.fixot.model.OnlineOrder;
import com.ids.fixot.model.OrderDurationType;
import com.ids.fixot.model.Parameter;
import com.ids.fixot.model.Portfolio;
import com.ids.fixot.model.Stock;
import com.ids.fixot.model.StockAlerts;
import com.ids.fixot.model.StockQuotation;
import com.ids.fixot.model.SubAccount;
import com.ids.fixot.model.SubscriberUser;
import com.ids.fixot.model.TimeSale;
import com.ids.fixot.model.Unit;
import com.ids.fixot.model.User;
import com.ids.fixot.model.WebItem;
import com.ids.fixot.model.item;
import com.ids.fixot.model.mowazi.MoaziCompany;
import com.ids.fixot.model.mowazi.MowaziCompany;
import com.ids.fixot.model.mowazi.MowaziMobileConfiguration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//import com.crashlytics.android.Crashlytics;

public class MyApplication extends Application {

    public static final int VIBRATION_PERIOD = 250;
    public static final int Session_Out_Period = BuildConfig.Enable_Markets? 14 : 360;
    public static boolean IsTimeSaleLoginRetreived = false;
    //<editor-fold desc="Ot section">
    public final static int MARKET_OPEN = 1;
    public static boolean Pahse2=true;
    public static boolean isNotification = false;
    public static boolean isLoggedIn = true;
    public static boolean enablePlaceOrder = false;
    public static ArrayList<String> allYears = new ArrayList<String>();
    public static ArrayList<MoaziCompany> allCompanies = new ArrayList<MoaziCompany>();
    public final static int Enquiry = -1;
    public static int UNIQUE_REQUEST_CODE = 0;
    public final static int MARKET_CLOSED = -1;
    public final static int VALUES_SPAN_COUNT = 1;
    public static   int VALUES_SPAN_COUNT_SECTOR = 1;
    public final static int GRID_VALUES_SPAN_COUNT = 2;
    public final static int ORDER_BUY = 1, ORDER_SELL = 2;
    public final static int STATUS_EXECUTED = 6, STATUS_REJECTED = 10, STATUS_PRIVATE = 16;
    public static final String CIRCUIT_BREAKER = "10";
    public static final int TOP_GAINERS = 1;
    public static final int TOP_LOSERS = 2;
    public static final int TOP_TRADED = 3;
    public static final int TOP_TRADES = 4;
    public static final int TOP_VALUES = 5;
    public static final int MARKET_PRICE = 1;
    public static final int LIMIT = 2;
    public static ArrayList<StockAlerts> userStockAlerts = new ArrayList<>();
    public static ArrayList<Lookups> allAlertTypes = new ArrayList<>();
    public static ArrayList<Lookups> allAlertOperators = new ArrayList<>();

    public static ArrayList<Integer> expandedStock = new ArrayList<>();

    public static final int MARKET_IF_TOUCHED = 8;
    public static final int LIMIT_IF_TOUCHED = 9;
    public static final int SI = 10;
    public static final int MO = 11;
    public static final int OCA = 12;
    public static final int ICEBERG = 13;

    public static final int TYPE_NORMAL_REVIEW = 1;
    public static final int TYPE_OCA_ADD = 2;
    public static final int TYPE_OCA_REVIEW = 3;

    public static int selectedStockId = 0;


    public static  int STOCK_TYPE_STOCK_NAME = 1;
    public static  int STOCK_TYPE_STOCK_SYMBOL = 2;

    public static  int STOCK_TYPE_OFFER_PRICE = 1;
    public static  int STOCK_TYPE_OFFER_VOLUME = 2;

    public static  int STOCK_TYPE_CURRENT_PRICE = 1;
    public static  int STOCK_TYPE_LOW_PRICE = 2;
    public static  int STOCK_TYPE_HIGH_PRICE = 3;
    public static  int STOCK_TYPE_IMP = 4;

    public static  int STOCK_TYPE_BID_PRICE = 1;
    public static  int STOCK_TYPE_BID_VOLUME = 2;

    public static  int STOCK_TYPE_CHANGE_PER = 1;
    public static  int STOCK_TYPE_CHANGE = 2;





    public static  int PORTFOLIO_SHARES = 1;
    public static  int PORTFOLIO_AVAILABLE = 2;

    public static  int PORTFOLIO_MARKET = 1;
    public static  int PORTFOLIO_AVG_COST = 2;


    public static  int PORTFOLIO_MARKET_VALUE = 1;
    public static  int PORTFOLIO_COST_VALUE = 2;

    public static  int PORTFOLIO_BALANCE_PRICE = 1;
    public static  int PORTFOLIO_GAIN_LOSS = 2;

    public static  int STOCK_COLUMN_1 = 1;
    public static  int STOCK_COLUMN_2 = 1;
    public static  int STOCK_COLUMN_3 = 1;
    public static  int STOCK_COLUMN_4 = 1;
    public static  int STOCK_COLUMN_5 = 1;


    public static boolean isMultiAccountStatements = true;
    //<editor-fold desc="Initial Configuration section">
    public static boolean showInternetLossDialog = true;
    public static boolean showBackgroundRequestToastError = true;


    public static String link = "", baseLink = "", appLabel = "";
    public static String statementLink = "http://10.2.2.103/";
    //</editor-fold>
    public static SharedPreferences mshared;
    public static SharedPreferences.Editor editor;
    public static int ENGLISH = 1, ARABIC = 2;
    public static int lang = ENGLISH;
    public static int screenWidth, screenHeight;
    public static Typeface opensansregular, opensansbold, droidregular, giloryBold, giloryItaly, droidbold;
    public static Boolean trustEveryone = false;

    public static Boolean isOTC = false;
    public static boolean showMowazi = false;
    public static boolean isDebug = false;

    public static Boolean contactKicMarketIndexShown = false;
    public static Boolean contactKicPortfolioShown = false;
    public static Boolean contactKicOrdersShown = false;

    public static NotificationSettings notificationSettings;

    public static String afterKey = "";
    public static Context context;
    public static ProgressDialog progress;
    public static MyApplication instance;
    public static int count = 20;
    public static User currentUser = new User();
    public static SubAccount selectedSubAccount = new SubAccount();
    public static Portfolio portfolio = new Portfolio();
    public static MarketStatus marketStatus = new MarketStatus();
    public static MarketStatus marketServerTime = new MarketStatus();
    public static Parameter parameter = new Parameter();
    public static String instrumentId = "0";
    public static String Auction_Instrument_id = "AUCTION_MKT";
    public static String CB_Auction_id = "10";
    public static ArrayList<BrokerageFee> allBrokerageFees = new ArrayList<>();
    public static ArrayList<StockQuotation> stockQuotations = new ArrayList<>();
    public static ArrayList<StockQuotation> stockQuotationsBonds = new ArrayList<>();
    public static ArrayList<Stock> stockTops = new ArrayList<>();
    public static ArrayList<TimeSale> timeSales = new ArrayList<>();
    public static ArrayList<WebItem> webItems = new ArrayList<>();
    public static ArrayList<Instrument> instruments = new ArrayList<>();
    public static ArrayList<Integer> availableMarkets = new ArrayList<>();
    public static ArrayList<Unit> units = new ArrayList<>();
    public static ArrayList<ApplicationList> applicationLists = new ArrayList<>();
    public static ArrayList<ReportPageSetup> reportPageList = new ArrayList<>();
    public static OnlineOrder onlineParentOrder;

    public static ArrayList<OrderDurationType> allOrderDurationType = new ArrayList<>();
    public static HashMap<String, Instrument> instrumentsHashmap = new HashMap<>();

    public static HashMap<String, String> timeSalesTimesTampMap = new HashMap<>();
    public static HashMap<String, String> userOrderTimesTamMap = new HashMap<>();

    public static String brokerID = "0";
    public static String stockTimesTamp = "0";
    public static String bondsTimesTamp = "0";
    public static HashMap<String, String> stockTimesTampMap = new HashMap<>();
    public static HashMap<String, String> bondsTimesTampMap = new HashMap<>();

    public static String sectorsTimesTamp = "0";
    public static String timeSalesTimesTamp = "0";
    //public static String marketID = (isOTC ? Integer.toString(enums.MarketType.KWOTC.getValue()) : Integer.toString(enums.MarketType.XKUW.getValue()));
    public static String marketID = Integer.toString(enums.MarketType.XKUW.getValue());
    public static SubscriberUser userSubscriber;
    public static Boolean isSubscriber = false;
    public static Boolean isAutoLogin = false;
    //</editor-fold>

    public static String lastPasswordUsed = "";
    public static boolean appServiceFirstTime = true;

    //<editor-fold desc="async tasks pool section">
    public static int corePoolSize = 60;
    public static int maximumPoolSize = 80;
    public static int keepAliveTime = 10;
    public static BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    //</editor-fold>

    //<editor-fold desc="mowazi section">
    public static double dinar = 0.0;
    public static double brokerageQuantityCoif = 0.0;
    public static double brokerageSumCoif = 0.0;
    public static int defaultPriceType = 1;
    public static int mowaziClientID = 0;
    public static String mowaziUrl = "https://www.almowazi.com/md3iphoneservice/services/dataservice.svc";
    public static String mowaziNewsLink = "";
    public static String mowaziImagePath = "";
    public static String mowaziGeneralNewsLink = "";
    public static String mowaziBrokerId = "1";
    public static ArrayList<MowaziCompany> allMowaziCompanies = new ArrayList<>();
    public static ArrayList<MowaziMobileConfiguration> allMowaziMobileConfigurations = new ArrayList<>();
    //</editor-fold>

    public static item GetStockQuotation = new item();
    public static item GetBonds = new item();
    public static item GetRealTimeData = new item();
    public static item AddDevice2 = new item();
    public static item GetParameters = new item();
    public static item GetInstruments = new item();
    public static item GetApplication = new item();
    public static item GetBrokerageFees = new item();
    public static item GetSiteMapData = new item();
    public static item UpdateBadgeNotification = new item();
    public static item GetOrderDurationTypes = new item();
    public static item Login = new item();
    public static item GetTradeTickerData = new item();
    public static item GetPriceTickerData = new item();
    public static item GetSectorChartData = new item();
    public static item LoadSectorDetails = new item();
    public static item GetPortfolio = new item();
    public static item GetUserOrders = new item();
    public static item LoadStockDetails = new item();
    public static item GetStockChartData = new item();
    public static item GetTrades = new item();
    public static item GetOffMarketQuotes = new item();
    public static item GetStockOrderBook = new item();
    public static item GetStockOrderBookByOrder = new item();
    public static item GetStockOrderBookByPrice = new item();

    public static item GetSectorIndex = new item();
    public static item GetStockTops = new item();
    public static item GetNews = new item();
    public static item GetMobileSiteMap = new item();
    public static item AddFavoriteStocks = new item();
    public static item RemoveFavoriteStocks = new item();
    public static item GetFavoriteStocks = new item();
    public static item GetQuickLinks = new item();
    public static item GetTradeInfo = new item();
    public static item AddNewOrder = new item();
    public static item AddNewOrderList = new item();
    public static item CancelOrder = new item();
    public static item UpdateOrder = new item();
    public static item ActivateOrder = new item();
    public static item GetUnits = new item();
    public static item ChangePassword = new item();
    public static item CheckSessionWithServerTime = new item();
    public static item GetMarketStatusMobile = new item();
    public static item GetOrderTypeByStock = new item();
    public static item GetMobileNotification = new item();
    public static item GetUserNotificationSettings = new item();
    public static item SaveUserNotificationSettings = new item();

    public static item GetLookups = new item();
    public static item ActivateStockAlert = new item();
    public static item DeleteStockAlert = new item();
    public static item SaveStockAlert= new item();
    public static item GetStockAlerts= new item();

    public static item ValidateOrder= new item();

    public static item SaveSubscriber = new item();
    public static item VerifySubscriber = new item();
    public static item LoadSubscriber = new item();
    public static item LoadUserOrder = new item();
    public static item GetMobileReportPageSetup = new item();


    public static item GetAvailableForWithdrawal= new item();
    public static item SaveChequeRequest= new item();

    public static item GetBalanceDetails= new item();


    public static Double lastId;

    public static String AlternativeWebserviceLink = "http://www.ids-support.com/iphone/fixbrokers.json";

    public static String CmplxPassStringAr, CmplxPassStringEn, PassStringAr, PassStringEn;

    public static Calendar sessionOut = null;
    public static boolean isFromLogin = false;

    public static MyApplication getInstance() {
        return instance;
    }

    public static void showDialog(Activity activity) {
       try {
           progress = new ProgressDialog(activity, R.style.MyTheme);
           progress.setCancelable(false);
           progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
           progress.show();
       }catch (Exception e){}
    }

    public static void dismiss() {
        if (progress != null)
            progress.dismiss();
    }

    @Override
    public void onCreate() {
        super.onCreate();


        link = BuildConfig.MD3Link;
        baseLink = BuildConfig.BaseMD3Link;

//        link = "http://10.2.2.103/Live_Webservice/Services/DataService.svc";
//        baseLink = "http://10.2.2.103/Live_Webservice/Mobile/";

        appLabel = BuildConfig.label;

        brokerID = BuildConfig.BrokerId;
        MyApplication.showInternetLossDialog = false;
        MyApplication.timeSalesTimesTamp = "0";
        MyApplication.timeSalesTimesTampMap.put(Integer.toString(enums.MarketType.KWOTC.getValue()),"0");
        MyApplication.timeSalesTimesTampMap.put(Integer.toString(enums.MarketType.XKUW.getValue()),"0");
        MyApplication.timeSalesTimesTampMap.put(Integer.toString(enums.MarketType.DSMD.getValue()),"0");

        MyApplication.stockTimesTampMap.put(Integer.toString(enums.MarketType.KWOTC.getValue()),"0");
        MyApplication.stockTimesTampMap.put(Integer.toString(enums.MarketType.XKUW.getValue()),"0");
        MyApplication.stockTimesTampMap.put(Integer.toString(enums.MarketType.DSMD.getValue()),"0");

        context = getApplicationContext();
        setWebserviceItem();

        GetScreenDimensions();
        try {
            droidregular = Typeface.createFromAsset(getAssets(),
                    "DroidKufiRegular.ttf");
            droidbold = Typeface.createFromAsset(getAssets(),
                    "DroidKufiBold.ttf");
            giloryBold = Typeface.createFromAsset(getAssets(),
                    "GilroyBold.otf");
            giloryItaly = Typeface.createFromAsset(getAssets(),
                    "GilroyLight.otf");

            if (MyApplication.showMowazi) {
                opensansregular = Typeface.createFromAsset(getAssets(),
                        "opensansregular.ttf");
                opensansbold = Typeface.createFromAsset(getAssets(),
                        "opensansbold.ttf");
            }

            instance = this;

            mshared = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            editor = mshared.edit();
            MyApplication.lang = MyApplication.mshared.getInt("lang", 0);


            if (MyApplication.lang == MyApplication.ARABIC)
                LocalUtils.setLocale(new Locale("ar"));
            else
                LocalUtils.setLocale(new Locale("en"));
            LocalUtils.updateConfig(this, getBaseContext().getResources().getConfiguration());

            if (Actions.isMyServiceRunning(context, AppService.class)) {
                Log.wtf("will stop it", "...");
                Actions.stopAppService(context);
            }

       /*     Intent intent = new Intent(getApplicationContext(), AppService.class);
            startService(intent);*/

        } catch (Exception e) {
            e.printStackTrace();
        }


        CmplxPassStringAr = "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "</head>\n" +
                "<div style=\"direction: rtl;\">\n" +
                "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"3\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"width: 100%;\">بهدف توفير الحماية والأمن, قمنا بوضع بعض النصائح لإختيار كلمة السر. يرجى إتباع التعليمات التالية عند اختيار كلمة السر</td>\n" +
                "<td valign=\"top\"></td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td colspan=\"2\">\n" +
                "<ul>\n" +
                "<li>إن كلمة السر حساسة للغاية (فحرف A الكبير يختلف عن a الصغير) عند إختيار كلمة السر</li>\n" +
                "<li>يجب أن تكون كلمة السر من 8 حروف على الاقل وبحد اقصى 16 حرف</li>\n" +
                "<li>يجب أن تحتوي كلمة السر على اي حرف من A الى Z وعلى رقم من 0 الى 9</li>\n" +
                "<li>ويتعين أن لا تحتوي كلمة السر على:</li>\n" +
                "<ul>\n" +
                "<li>اكثر من حرفين متشابهين ومكررين (rrr او ppp لا يسمح بكتابة)</li>\n" +
                "<li>اكثر من حرفين متتاليين أو متعاقبين (لا يسمح بإستخدام abc على سبيل المثال)</li>\n" +
                "<li>اكثر من ثلاثة إرقام متتالية او متشابهة (لا يسمح بإستخدام 2345 على سبيل المثال)</li>\n" +
                "<li>لا يسمح باستخدام الحروف او الرموز الخاصة مثل ?!()&lt;&gt;&amp;%$#@*</li>\n" +
                "</ul>\n" +
                "<li>يتعين أن لا تكون كلمة السر الجديدة مطابقة لإسم المستخدم او لإسم الدخول</li>\n" +
                "<li>يتعين ان تكون كلمة السر الجديدة مختلفة عن كلمة السر السابقة</li>\n" +
                "</ul>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</div>\n";

        CmplxPassStringEn = "\n" +
                "<table cellpadding=\"3\" cellspacing=\"0\" width=\"100%\">\n" +
                "<tbody><tr>\n" +
                "<td style=\"width: 100%;\">\n" +
                "In order to protect your security, we have set certain rules for selecting passwords. Please follow these guidlines while selecting a password:\n" +
                "</td>\n" +
                "<td valign=\"top\"> </td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td colspan=\"2\">\n" +
                "<ul>\n" +
                "<li>\n" +
                "The password is case sensitive ('A' is different from 'a')\n" +
                "</li>\n" +
                "<li>\n" +
                "The password should have at least 8 characters and a maximum of 16 characters\n" +
                "</li>\n" +
                "<li>\n" +
                "The password should have at least one letter (a-z) and one numeric character (0-9)\n" +
                "</li>\n" +
                "<li>\n" +
                "The password should not have:\n" +
                "</li>\n" +
                "<ul>\n" +
                "<li>\n" +
                "more than two repeated letters (rrr, ppp are not allowed)\n" +
                "</li>\n" +
                "<li>\n" +
                "more than two consecutive letters (abc is not allowed)\n" +
                "</li>\n" +
                "<li>\n" +
                "more than three consecutive numbers (2345 is not allowed)\n" +
                "</li>\n" +
                "<li>\n" +
                "special characters such as spaces or \\\"()/|?,;:'~&lt;&gt;\\\\+=.[]{}\\\"\n" +
                "</li>\n" +
                "</ul>\n" +
                "<li>\n" +
                "The password should not be identical to the User Name or name\n" +
                "</li>\n" +
                "<li>\n" +
                "The password must be different from your earlier password\n" +
                "</li>\n" +
                "</ul>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody></table>";

        PassStringAr = "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "</head>\n" +
                "<div style=\"direction: rtl;\">\n" +
                "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"3\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"width: 100%;\">بهدف توفير الحماية والأمن, قمنا بوضع بعض النصائح لإختيار كلمة السر. يرجى إتباع التعليمات التالية عند اختيار كلمة السر</td>\n" +
                "<td valign=\"top\"></td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td colspan=\"2\">\n" +
                "<ul>\n" +
                "<li>إن كلمة السر حساسة للغاية (فحرف A الكبير يختلف عن a الصغير) عند إختيار كلمة السر</li>\n" +
                "<li>يجب أن تكون كلمة السر من 8 حروف على الاقل وبحد اقصى 16 حرف</li>\n" +
                "<li>يجب أن تحتوي كلمة السر على اي حرف من A الى Z وعلى رقم من 0 الى 9</li>\n" +
                "<li>ويتعين أن لا تحتوي كلمة السر على:</li>\n" +
                "<ul>\n" +
                "<li>لا يسمح باستخدام الحروف او الرموز الخاصة مثل ?!()&lt;&gt;&amp;%$#@*</li>\n" +
                "</ul>\n" +
                "<li>يتعين أن لا تكون كلمة السر الجديدة مطابقة لإسم المستخدم او لإسم الدخول</li>\n" +
                "<li>يتعين ان تكون كلمة السر الجديدة مختلفة عن كلمة السر السابقة</li>\n" +
                "</ul>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</div>\n";

        PassStringEn = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"3\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"width: 100%;\">In order to protect your security, we have set certain rules for selecting passwords. Please follow these guidlines while selecting a password:</td>\n" +
                "<td valign=\"top\">&nbsp;</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td colspan=\"2\">\n" +
                "<ul>\n" +
                "<li>The password is case sensitive ('A' is different from 'a')</li>\n" +
                "<li>The password should have at least 8 characters and a maximum of 16 characters</li>\n" +
                "<li>The password should have at least one letter (a-z) and one numeric character (0-9)</li>\n" +
                "<li>The password should not have:&nbsp;</li>\n" +
                "<ul>\n" +
                "<li>special characters such as spaces or \\\"()/|?,;:'~&lt;&gt;\\\\+=.[]{}\\\"</li>\n" +
                "</ul>\n" +
                "<li>The password should not be identical to the User Name or name</li>\n" +
                "<li>The password must be different from your earlier password</li>\n" +
                "</ul>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n";


    }

    @Override
    protected void attachBaseContext(Context newBase) {

        super.attachBaseContext(newBase);

        MultiDex.install(this);
    }

    private void GetScreenDimensions() {
        WindowManager wm = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point point = new Point();
        display.getSize(point);
        screenWidth = point.x;
        screenHeight = point.y;
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocalUtils.updateConfig(this, newConfig);
    }

/*    public void setWebserviceItem() {

        GetStockQuotation = new item("100", "/GetStockQuotation?");
        GetRealTimeData = new item("101", "/GetRealTimeData?");
        AddDevice2 = new item("102", "/AddDevice?");
        GetParameters = new item("103", "/GetParameters?");
        GetInstruments = new item("104", "/GetInstruments?");
        GetBrokerageFees = new item("105", "/GetBrokerageFees?");
        GetSiteMapData = new item("106", "/GetSiteMapData?");
        UpdateBadgeNotification = new item("107", "/UpdateBadgeNotification?");
        GetOrderDurationTypes = new item("108", "/GetOrderDurationTypes?");
        Login = new item("109", "/Login?");
        GetTradeTickerData = new item("110", "/GetTradeTickerData?");
        GetPriceTickerData = new item("111", "/GetPriceTickerData?");
        GetSectorChartData = new item("112", "/GetSectorChartData?");
        LoadSectorDetails = new item("113", "/LoadSectorDetails?");
        GetPortfolio = new item("114", "/GetPortfolio?");
        GetUserOrders = new item("115", "/GetUserOrders?");
        LoadStockDetails = new item("116", "/LoadStockDetails?");
        GetStockChartData = new item("117", "/GetStockChartData?");
        GetTrades = new item("118", "/GetTrades?");
        GetStockOrderBook = new item("119", "/GetStockOrderBook?");
        GetSectorIndex = new item("122", "/GetSectorIndex?");
        GetStockTops = new item("123", "/GetStockTops?");
        GetNews = new item("124", "/GetNews?");
        GetMobileSiteMap = new item("125", "/GetMobileSiteMap?");
        AddFavoriteStocks = new item("126", "/AddFavoriteStocks?");
        RemoveFavoriteStocks = new item("127", "/RemoveFavoriteStocks?");
        GetFavoriteStocks = new item("128", "/GetFavoriteStocks?");
        GetQuickLinks = new item("129", "/GetQuickLinks?");
        GetTradeInfo = new item("130", "/GetTradeInfo?");
        AddNewOrder = new item("131", "/AddNewOrder?");
        CancelOrder = new item("132", "/CancelOrder?");
        UpdateOrder = new item("133", "/UpdateOrder?");
        ActivateOrder = new item("134", "/ActivateOrder?");
        GetUnits = new item("135", "/GetUnits?");
        ChangePassword = new item("136", "/ChangePassword?");
        GetOffMarketQuotes = new item("137", "/GetOffMarketQuotes?");
        GetStockOrderBookByOrder = new item("138", "/GetOrderBookByOrder?");
    }*/

    public static void saveNotificationSettings(NotificationSettings notificationSettings) {
        Gson gson = new Gson();
        String json = gson.toJson(notificationSettings);
        try {
            MyApplication.editor.putString("my_notification_settings", json).apply();
        } catch (Exception e) {
        }
    }

    public static NotificationSettings getNotificationSettings() {
        Gson gson = new Gson();
        String json = MyApplication.mshared.getString("my_notification_settings", "");
        NotificationSettings obj = gson.fromJson(json, NotificationSettings.class);
        return obj;
    }




    public static void setWebserviceItem() {

        GetStockQuotation = new item("100", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/StockQuotation/GetStockQuotationLite?");
        GetBonds = new item("160", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/Stock/GetBonds?");

        GetRealTimeData = new item("101", "/api/oms/User/GetRealTimeData?");
        AddDevice2 = new item("102", "/api/oms/Device/AddDevice?");
        GetParameters = new item("103", "/api/oms/Parameter/GetParameters?");
        GetInstruments = new item("104", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/Instrument/GetInstruments?");
        GetApplication = new item("141", "/api/oms/Application/GetApplications?");


        GetBrokerageFees = new item("105", "/api/oms/BrokerageFees/GetBrokerageFees?");
        GetSiteMapData = new item("106", "/api/oms/Content/GetSiteMapData?");
        UpdateBadgeNotification = new item("107", "/UpdateBadgeNotification?");
        GetOrderDurationTypes = new item("108", "/api/oms/OrderPlacement/GetOrderDurationTypes?");
        Login = new item("109", "/api/oms/Login/Login?");
        GetTradeTickerData = new item("110", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/Ticker/GetTradeTickerData?");
        GetPriceTickerData = new item("111", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/Ticker/GetPriceTickerData?");
        GetSectorChartData = new item("112", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/Sector/GetSectorChartData?");
        LoadSectorDetails = new item("113", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/Sector/LoadSectorDetails?");
        GetPortfolio = new item("114", "/api/oms/Portfolio/GetPortfolio?");
        GetUserOrders = new item("115", "/api/oms/Order/GetUserOrders?");
        LoadStockDetails = new item("116", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/StockQuotation/LoadStockDetails?");
        GetStockChartData = new item("117", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/Stock/GetStockChartData?");
        GetTrades = new item("118", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/Trade/GetTrades?");
        GetStockOrderBook = new item("119", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/Orderbook/GetStockOrderBook?");
        GetSectorIndex = new item("122", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/Sector/GetSectorIndex?");
        GetStockTops = new item("123", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/StockTops/GetStockTops?");
        GetNews = new item("124", "/api/oms/News/GetNews?");
        GetMobileSiteMap = new item("125", "/api/oms/Content/GetMobileSiteMap?");
        AddFavoriteStocks = new item("126", "/api/oms/FavoriteStock/AddFavoriteStocks?");
        RemoveFavoriteStocks = new item("127", "/api/oms/FavoriteStock/RemoveFavoriteStocks?");
        GetFavoriteStocks = new item("128", "/api/oms/FavoriteStock/GetFavoriteStocks?");
        GetQuickLinks = new item("129", "/api/oms/Links/GetQuickLinks?");
        GetTradeInfo = new item("130", "/api/oms/Portfolio/GetTradeInfo?");
        AddNewOrder = new item("131", "/api/oms/OrderPlacement/AddNewOrder?");
        AddNewOrderList = new item("153", "/api/oms/OrderPlacement/AddNewOrderList?");
        ValidateOrder = new item("152", "/api/oms/OrderPlacement/ValidateOrder?");
        CancelOrder = new item("132", "/api/oms/OrderPlacement/CancelOrder?");
        UpdateOrder = new item("133", "/api/oms/OrderPlacement/UpdateOrder?");
        ActivateOrder = new item("134", "/api/oms/OrderPlacement/ActivateOrder?");
        GetUnits = new item("135", "/api/oms/Units/GetUnits?");
        ChangePassword = new item("136", "/api/oms/User/ChangePassword?");
        GetOffMarketQuotes = new item("137", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/SpecialTrade/GetOffMarketQuotes?");
        GetStockOrderBookByOrder = new item("138", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/Orderbook/GetOrderBookByOrder?");
        GetStockOrderBookByPrice = new item("161", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/Orderbook/GetStockOrderBookByPrice?");



        GetOrderTypeByStock = new item("142", "/api/oms/OrderPlacement/GetOrderTypeByUserStock?");
        CheckSessionWithServerTime = new item("139", "/api/oms/User/CheckSessionWithServerTime?");
        GetMarketStatusMobile = new item("140", "/api/mdf/"+Actions.getLinkMarketType(Integer.parseInt(marketID))+"/MarketStatus/GetMarketStatusMobile?");


        LoadUserOrder=new item("159", "/api/oms/Order/LoadUserOrder?");

        GetMobileNotification = new item("143", "/api/oms/Notification/GetMobileNotifications?");
        GetUserNotificationSettings = new item("144", "/api/oms/UserNotificationSettings/GetUserNotificationSettings?");
        SaveUserNotificationSettings = new item("145", "/api/oms/UserNotificationSettings/SaveUserNotificationSettings?");
        GetLookups = new item("150", "/api/oms/Lookups/GetLookups?");
        ActivateStockAlert = new item("151", "/api/oms/StockAlert/ActivateStockAlert?");
        DeleteStockAlert = new item("147", "/api/oms/StockAlert/DeleteStockAlert?");
        SaveStockAlert = new item("148", "/api/oms/StockAlert/SaveStockAlert?");
        GetStockAlerts = new item("149", "/api/oms/StockAlert/GetStockAlerts?");

        SaveSubscriber = new item("154", "/api/oms/Subscriber/SaveSubscriber?");
        LoadSubscriber = new item("155", "/api/oms/Subscriber/LoadSubscriber?");
        VerifySubscriber = new item("156", "/api/oms/Subscriber/VerifySubscriber?");

        GetAvailableForWithdrawal = new item("157", "/api/oms/ChequeRequest/GetAvailableForWithdrawal?");
        SaveChequeRequest = new item("158", "/api/oms/ChequeRequest/SaveChequeRequest?");

        GetMobileReportPageSetup=new item("162", "/api/oms/Report/GetMobileReportPageSetup?");
        GetBalanceDetails=new item("163", "/api/oms/BalanceDetails/GetBalanceDetails?");

    }
}