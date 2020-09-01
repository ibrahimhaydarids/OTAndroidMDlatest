package com.ids.fixot.activities.mowazi;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.LoginFingerPrintActivity;
import com.ids.fixot.adapters.mowaziAdapters.MowaziCompanySpinnerAutoCompleteAdaper;
import com.ids.fixot.adapters.mowaziAdapters.MowaziMyOrdersRecyclerAdapter;
import com.ids.fixot.adapters.mowaziAdapters.MowaziOrderTypeSpinnerAdapter;
import com.ids.fixot.adapters.mowaziAdapters.MowaziSectorSpinnerAdapter;
import com.ids.fixot.model.mowazi.MowaziCompany;
import com.ids.fixot.model.mowazi.MowaziOnlineOrder;
import com.ids.fixot.model.mowazi.MowaziOrderType;
import com.ids.fixot.model.mowazi.MowaziSector;
import com.ids.fixot.model.mowazi.SimpleDividerItemDecoration;
import com.ids.fixot.parser.MowaziCompanyNameParser;
import com.ids.fixot.parser.MowaziOnlineOrderParser;
import com.ids.fixot.parser.MowaziOrderTypeParser;
import com.ids.fixot.parser.MowaziSectorParser;

import org.json.JSONException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by DEV on 3/29/2018.
 */

public class MowaziMyOrdersActivity extends AppCompatActivity implements MowaziMyOrdersRecyclerAdapter.RecyclerViewOnItemClickListener {

    public ArrayList<MowaziCompany> arrayofcompanies = new ArrayList<MowaziCompany>();
    int footerButton;
    LinearLayout llcompany;
    ImageView ivCompany;
    SharedPreferences mshared;
    SharedPreferences.Editor edit;
    AutoCompleteTextView spinnerCompanies;
    private LinearLayoutManager llm;
    private RelativeLayout llOrdersLayout, top, rlFromDate, rlToDate;
    private RecyclerView rvOrders;
    private TextView tvOrders, tvNoData;
    private MowaziMyOrdersRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<MowaziOnlineOrder> allOrders = new ArrayList<>();
    private boolean flagLoading = false, pulltoRefresh = false;
    private int visibleItemCount;
    private int totalItemCount;
    private int companyId = 0, sectorId = 0, orderId = 0, orderType = 0;
    private Spinner spinnerSectors, spinnerTypes;
    private ArrayList<MowaziSector> allSectors = new ArrayList<MowaziSector>();
    private MowaziCompanySpinnerAutoCompleteAdaper spinnerAdapter;
    private MowaziSectorSpinnerAdapter sectorSpinnerAdapter;
    private ArrayList<MowaziCompany> allCompanies = new ArrayList<MowaziCompany>();
    private int pastVisiblesItems;
    private GetMyOrderBookSummary getOrderBookSummary;
    private ProgressBar pbOrders;
    private GetCompanies getCompanies;
    private GetSectors getSectors;
    private GetOrderTypes getOrderTypes;
    private ArrayList<MowaziOrderType> allOrderTypes = new ArrayList<MowaziOrderType>();
    private MowaziOrderTypeSpinnerAdapter orderTypeSpinnerAdapter;
    private MowaziCompany selectedcompany = new MowaziCompany();
    private MowaziSector selectedsector = new MowaziSector();
    private MowaziOrderType selectedOrderType = new MowaziOrderType();
    private RelativeLayout searchlayoutall;
    private TextView btFilter;
    private RelativeLayout main;
    private ImageButton back;
    private int mYear, mMonth, mDay;
    private DrawerLayout drawer;
    private TextView tvFromDate, tvToDate;
    private String fromDate = "", toDate = "", returnedDatefom = "",
            returnedDateto = "";
    private ArrayList<MowaziOnlineOrder> OrdersToSort = new ArrayList<MowaziOnlineOrder>();
    private ArrayList<MowaziCompany> arrayofspinner = new ArrayList<MowaziCompany>();
    private boolean started = false;

    public MowaziMyOrdersActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Actions.setLocal(MyApplication.lang, this);

        setContentView(R.layout.activity_mowazi_my_orders);
        mshared = PreferenceManager.getDefaultSharedPreferences(MowaziMyOrdersActivity.this);
        edit = mshared.edit();

        started = true;

        findViews();

        getSectors = new GetSectors();
        getSectors.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Actions.overrideFonts(this, llOrdersLayout, true);

    }

    public void showDrawer(View v) {

    }

    private void findViews() {
        llOrdersLayout = (RelativeLayout) findViewById(R.id.llOrdersLayout);
        top = (RelativeLayout) findViewById(R.id.top);

        ImageButton menu = findViewById(R.id.menu);
        menu.setVisibility(View.GONE);

        tvOrders = (TextView) findViewById(R.id.tvOrders);
        pbOrders = (ProgressBar) findViewById(R.id.pbOrders);
        tvNoData = (TextView) findViewById(R.id.tvNoData);
        tvFromDate = (TextView) findViewById(R.id.tvFromDate);
        tvToDate = (TextView) findViewById(R.id.tvToDate);
        rlFromDate = (RelativeLayout) findViewById(R.id.rlFromDate);
        rlToDate = (RelativeLayout) findViewById(R.id.rlToDate);
        main = (RelativeLayout) findViewById(R.id.main);
        back = (ImageButton) findViewById(R.id.back);
        searchlayoutall = (RelativeLayout) findViewById(R.id.searchlayoutall);

        spinnerCompanies = (AutoCompleteTextView) findViewById(R.id.spinnerCompanies);
        spinnerTypes = (Spinner) findViewById(R.id.spinnerTypes);

        MowaziOrderType order = new MowaziOrderType();
        order.setOrderTypeId(0);
        order.setNameEn(getResources().getString(R.string.mowazi_orderType));
        order.setNameAr(getResources().getString(R.string.mowazi_orderType));
        allOrderTypes.add(order);
        orderTypeSpinnerAdapter = new MowaziOrderTypeSpinnerAdapter(MowaziMyOrdersActivity.this, allOrderTypes);
        spinnerTypes.setAdapter(orderTypeSpinnerAdapter);

        spinnerCompanies.setThreshold(1);
        arrayofspinner.addAll(MyApplication.allMowaziCompanies);
        spinnerCompanies.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View arg0) {
                if (spinnerCompanies.getAdapter().getCount() == 0) {

                    arrayofspinner.addAll(MyApplication.allMowaziCompanies);
                    spinnerAdapter.notifyDataSetChanged();
                }
                spinnerCompanies.showDropDown();
            }
        });
        spinnerCompanies
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        MowaziCompany c = spinnerAdapter.getItem(position);
                        selectedcompany = c;
                        Actions.closeKeyboard(MowaziMyOrdersActivity.this);
                    }

                });

        spinnerSectors = (Spinner) findViewById(R.id.spinnerSectors);
        MowaziSector sector = new MowaziSector();
        sector.setId(0);
        sector.setName(getResources().getString(R.string.sector));
        allSectors.add(sector);
        sectorSpinnerAdapter = new MowaziSectorSpinnerAdapter(MowaziMyOrdersActivity.this, allSectors);
        spinnerSectors.setAdapter(sectorSpinnerAdapter);
        spinnerSectors.setSelection(0);


        tvOrders.setText(getResources().getString(R.string.mowazi_my_orders));

        llm = new LinearLayoutManager(MowaziMyOrdersActivity.this);
        rvOrders = (RecyclerView) findViewById(R.id.rvOrders);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setColorSchemeColors(0, 0, 0, 0);
        swipeContainer.setProgressBackgroundColor(android.R.color.transparent);
        swipeContainer.setRefreshing(false);
        swipeContainer
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                    public void onRefresh() {

                        swipeContainer.setRefreshing(false);
                        refreshItems();
                        pulltoRefresh = true;

                        swipeContainer.setRefreshing(false);
                    }
                });

        swipeContainer.setRefreshing(false);

        rvOrders.setLayoutManager(llm);

        adapter = new MowaziMyOrdersRecyclerAdapter(this, allOrders, this);
        rvOrders.setAdapter(adapter);
        rvOrders.addItemDecoration(new SimpleDividerItemDecoration(this, 0,
                R.drawable.line_divider));

        rvOrders.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) // check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (flagLoading == false) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            flagLoading = true;
                            addOrders();
                        }
                    }
                }
            }
        });


        spinnerSectors
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parentView,
                                               View view, int position, long id) {

                        if (position != 0) {
                            MowaziSector sector = sectorSpinnerAdapter
                                    .getItem(position);
                            selectedsector = sector;
                        } else
                            selectedsector.setId(0);
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });

        spinnerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parentView,
                                       View view, int position, long id) {

                if (position != 0) {
                    MowaziOrderType s = orderTypeSpinnerAdapter
                            .getItem(position);
                    selectedOrderType = s;
                    Log.d("order", "" + s.getOrderTypeId());
                } else
                    selectedOrderType.setOrderTypeId(0);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

        rlFromDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                showDateTimePickerFrom(rlFromDate);

            }
        });

        rlToDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                showDateTimePickerFrom(rlToDate);

            }
        });

    }

    public void clear(View v) {
        sectorId = 0;
        companyId = 0;
        orderType = 0;
        spinnerSectors.setSelection(0);
        fromDate = "";
        spinnerCompanies.setText("");
        toDate = "";
        tvFromDate.setText(getString(R.string.mowazi_from));
        tvToDate.setText(getString(R.string.mowazi_to));
        spinnerCompanies.setSelection(0);
        Intent intent = getIntent();
        if (intent.hasExtra("companyId")) // try on 232 al razi company
            companyId = getIntent().getExtras().getInt("companyId");
        else
            companyId = 0;
        refreshItems();
    }

    public String showDateTimePickerFrom(final View v) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        monthOfYear = monthOfYear + 1;
                        String mm = String.valueOf(monthOfYear);
                        if (mm.length() == 1)
                            mm = "0" + mm;
                        String dd = String.valueOf(dayOfMonth);
                        if (dd.length() == 1)
                            dd = "0" + dd;

                        if (v == rlFromDate) {
                            returnedDatefom = year + "-" + mm + "-" + dd;
                            tvFromDate.setText(year + "-" + mm + "-" + dd);
                            fromDate = returnedDatefom;

                        } else {
                            returnedDateto = year + "-" + mm + "-" + dd;
                            tvToDate.setText(year + "-" + mm + "-" + dd);
                            toDate = returnedDateto;
                        }

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

        return "";
    }

    private void refreshItems() {

        swipeContainer.setRefreshing(false);
        getOrderBookSummary = new GetMyOrderBookSummary();
        getOrderBookSummary.execute("0");

        swipeContainer.setRefreshing(false);
    }

    public void search(View v) {
        allOrders.clear();
        // allOrders.add(new OnlineOrder());
        companyId = selectedcompany.getCompanyId();
        sectorId = selectedsector.getId();
        orderType = selectedOrderType.getOrderTypeId();
        getOrderBookSummary = new GetMyOrderBookSummary();
        getOrderBookSummary.execute("0");
    }

    public void closeFilter(View v) {
        if (searchlayoutall.getVisibility() == View.VISIBLE) {
            searchlayoutall.setVisibility(View.GONE);
            fromDate = "";
            toDate = "";
            sectorId = 0;
            orderType = 0;
            Intent intent = getIntent();
            if (intent.hasExtra("companyId")) // try on 232 al razi company
                companyId = getIntent().getExtras().getInt("companyId");
            else
                companyId = 0;
        } else {
            searchlayoutall.setVisibility(View.VISIBLE);
            companyId = selectedcompany.getCompanyId();
            fromDate = returnedDatefom;
            toDate = returnedDateto;
            sectorId = selectedsector.getId();
            orderType = selectedOrderType.getOrderTypeId();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);


        allOrders.clear();
        // allOrders.add(new OnlineOrder());
        adapter.notifyDataSetChanged();

        LocalBroadcastManager.getInstance(MowaziMyOrdersActivity.this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        if (started)
                            recreate();
                    }
                }, new IntentFilter(MyApplication.class.getName() + "ChangedLanguage")
        );
        getOrderBookSummary = new GetMyOrderBookSummary();
        getOrderBookSummary.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "0");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    private void onItemsLoadComplete() {
        Log.d("Recycler", "Refreshed");
        swipeContainer.setRefreshing(false);
    }

    public void addOrders() {
        if (allOrders.size() > 0) {
            getOrderBookSummary = new GetMyOrderBookSummary();
            getOrderBookSummary.execute(String.valueOf(allOrders.get(
                    allOrders.size() - 1).getCompanyId()));
            flagLoading = false;
        }
    }

    public void onItemClicked(View v, int position) {
        Log.d("Item", "Clicked");

        if (position >= 0) {
            Intent i = new Intent();
            Bundle b = new Bundle();
            b.putParcelable("order", allOrders.get(position));
            b.putInt("flag", 1);
            i.putExtras(b);
            i.setClass(MowaziMyOrdersActivity.this, MowaziPlaceOrderActivity.class);
            MowaziMyOrdersActivity.this.startActivity(i);
        }

    }

    public void back(View v) {
        finish();
    }

    public void InitializeHeader() {

        llcompany = (LinearLayout) findViewById(R.id.llcompany);

        ivCompany = (ImageView) findViewById(R.id.ivcompany);

        llcompany.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (ivCompany.getTag().equals("desc")) {
                    ivCompany.setImageDrawable(ContextCompat.getDrawable(
                            MowaziMyOrdersActivity.this, R.drawable.updown));
                    ivCompany.setRotation(180);

                    ivCompany.setTag("asce");
                    sort("asce");
                    edit.putString("myOrderSortKind", "symbol");
                    edit.putString("myOrderSortType", "asce");
                    edit.commit();
                } else {
                    ivCompany.setRotation(ivCompany.getRotation() - 180);
                    ivCompany.setImageDrawable(ContextCompat.getDrawable(
                            MowaziMyOrdersActivity.this, R.drawable.updown));
                    ivCompany.setTag("desc");
                    sort("desc");
                    edit.putString("myOrderSortKind", "symbol");
                    edit.putString("myOrdeSortType", "desc");
                    edit.commit();
                }

            }
        });
        setImageForSorting();
    }

    public void setImageForSorting() {
        if (!mshared.getString("myOrderSortKind", "").equals("")) {
            if (mshared.getString("myOrdeSortType", "").equals("asce")) {

                ivCompany.setImageDrawable(ContextCompat.getDrawable(
                        MowaziMyOrdersActivity.this, R.drawable.updown));

                ivCompany.setRotation(180);
            } else {
                ivCompany.setImageDrawable(ContextCompat.getDrawable(
                        MowaziMyOrdersActivity.this, R.drawable.updown));
            }
        }
    }

    public void sort(final String type) {
        OrdersToSort.addAll(allOrders);
        allOrders.clear();
        Collections.sort(OrdersToSort, new Comparator<MowaziOnlineOrder>() {

            public int compare(MowaziOnlineOrder item1, MowaziOnlineOrder item2) {

                if (type.equals("asce")) {
                    if (MyApplication.lang == MyApplication.ENGLISH) {
                        if (item2.getCompany().getSymbolEn() != null)
                            return item1
                                    .getCompany()
                                    .getSymbolEn()
                                    .compareTo(item2.getCompany().getSymbolEn());
                        else
                            return 1;
                    } else {
                        if (item2.getCompany().getSymbolAr() != null)
                            return item1
                                    .getCompany()
                                    .getSymbolAr()
                                    .compareTo(item2.getCompany().getSymbolAr());
                        else
                            return 1;
                    }
                } else {

                    if (MyApplication.lang == MyApplication.ENGLISH) {
                        if (item2.getCompany().getSymbolEn() != null)
                            return item2
                                    .getCompany()
                                    .getSymbolEn()
                                    .compareTo(item1.getCompany().getSymbolEn());
                        else
                            return 1;
                    } else {
                        if (item2.getCompany().getSymbolAr() != null)
                            return item2
                                    .getCompany()
                                    .getSymbolAr()
                                    .compareTo(item1.getCompany().getSymbolAr());
                        else
                            return 1;
                    }
                }
            }

        });
        allOrders.addAll(OrdersToSort);
        adapter.notifyDataSetChanged();
        allOrders.clear();

    }

    private boolean containsCompany(ArrayList<MowaziCompany> arrayofcompanies,
                                    int companyId) {

        for (int i = 0; i < arrayofcompanies.size(); i++) {
            if (arrayofcompanies.get(i).getCompanyId() == companyId)
                return true;
        }
        return false;
    }

    protected class GetMyOrderBookSummary extends
            AsyncTask<String, Void, String> {

        String paramsOf0 = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            pbOrders.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                final String METHOD_NAME = "GetOnlineOrders";
                final String NAMESPACE = "http://tempuri.org/";

                // SOAP_ACTION = NAMESPACE + METHOD_NAME
                final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

                final String URL = MyApplication.mowaziUrl;

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);


                request.addProperty("countryId", "116");

                request.addProperty("sectorId", "" + sectorId);

                request.addProperty("companyId", "" + companyId);

                request.addProperty("onlyClientOrders", "true");

                request.addProperty("orderTypeId", "" + orderType);

                request.addProperty("orderStatusId", "0");
                request.addProperty("DefaultCountryId", "116");
                request.addProperty("fromDate", "");// fromDate
                request.addProperty("toDate", "");// toDate

                request.addProperty("id", "" + orderId);
                request.addProperty("top", "20");

                Element[] header = new Element[1];
                header[0] = new Element().createElement(NAMESPACE,
                        "SoapClassService");
                Element username = new Element().createElement(NAMESPACE,
                        "ClientID");
                username.addChild(Node.TEXT, "" + MyApplication.mowaziClientID);
                header[0].addChild(Node.ELEMENT, username);
                Element pass = new Element().createElement(NAMESPACE,
                        "oldToken");
                pass.addChild(Node.TEXT, mshared.getString("oldToken", ""));
                header[0].addChild(Node.ELEMENT, pass);
                Element random = new Element().createElement(NAMESPACE,
                        "newToken");
                random.addChild(Node.TEXT, mshared.getString("newToken", ""));
                header[0].addChild(Node.ELEMENT, random);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);

                envelope.dotNet = true;
                envelope.headerOut = header;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransport = new HttpTransportSE(URL);
                httpTransport.debug = true;

                httpTransport.call(SOAP_ACTION, envelope);
                result = envelope.getResponse().toString();
                SoapObject res = (SoapObject) envelope.bodyIn;
                SoapObject t = (SoapObject) res
                        .getProperty("GetOnlineOrdersResult");

                result = t.getProperty("message").toString();

                // = bank.getProperty("success").toString();

            } catch (SoapFault sf) {
                System.out.println(sf.faultstring);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            MowaziOnlineOrderParser parser = new MowaziOnlineOrderParser(result, MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");
            try {
                allOrders.clear();
                if (pulltoRefresh) {

                    // allOrders.add(new OnlineOrder());
                    pulltoRefresh = false;
                }
                allOrders.addAll(parser.GetOrders());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.contains("Expired")) {
                RegisterOrLoginMowazi login = new RegisterOrLoginMowazi();
                login.execute();
            } else if (result.contains("Security Issue")) {

                // MyApplication.logged = false;
                Actions.logout(MowaziMyOrdersActivity.this);
                Intent i = new Intent();
                i.putExtra("activity", "myorders");

                i.setClass(MowaziMyOrdersActivity.this, LoginFingerPrintActivity.class);

                startActivity(i);

                finish();

                edit.putBoolean("loggedMowazi", false);
                edit.commit();
            } else {
                if (!mshared.getString("myOrderSortKind", "").equals("")) {
                    if (mshared.getString("myOrderSortKind", "").equals(
                            "symbol"))
                        sort(mshared.getString("myOrderSortType", "asce"));
                }
                adapter.notifyDataSetChanged();
                pbOrders.setVisibility(View.GONE);
                onItemsLoadComplete();

                if (allOrders.size() == 0) {
                    tvNoData.setVisibility(View.VISIBLE);
                    swipeContainer.setVisibility(View.GONE);
                } else {
                    tvNoData.setVisibility(View.GONE);
                    swipeContainer.setVisibility(View.VISIBLE);
                }

                for (int i = 0; i < allOrders.size(); i++) {
                    MowaziCompany c = new MowaziCompany();
                    if (MyApplication.lang == MyApplication.ARABIC)
                        c.setSymbolAr(allOrders.get(i).getCompany()
                                .getSymbolAr());
                    else
                        c.setSymbolEn(allOrders.get(i).getCompany()
                                .getSymbolEn());
                    c.setCompanyId(allOrders.get(i).getCompanyId());
                    if (!containsCompany(allCompanies, allOrders.get(i).getCompanyId()))
                        allCompanies.add(c);
                }

                // spinnerAdapter.notifyDataSetChanged();
                for (int i = 0; i < allOrders.size(); i++) {
                    MowaziCompany c = new MowaziCompany();
                    if (MyApplication.lang == MyApplication.ARABIC)
                        c.setSymbolAr(allOrders.get(i).getCompany().getSymbolAr());
                    else
                        c.setSymbolEn(allOrders.get(i).getCompany().getSymbolEn());


                    c.setCompanyId(allOrders.get(i).getCompanyId());
                    if (!containsCompany(arrayofcompanies, allOrders.get(i).getCompanyId()))
                        arrayofcompanies.add(c);
                }

                spinnerCompanies.setThreshold(1);
                spinnerAdapter = new MowaziCompanySpinnerAutoCompleteAdaper(MowaziMyOrdersActivity.this, R.layout.need_list_spinner_item, MyApplication.allMowaziCompanies);

                spinnerCompanies.setAdapter(spinnerAdapter);
                spinnerCompanies.setOnClickListener(new View.OnClickListener() {

                    public void onClick(final View arg0) {
                        spinnerCompanies.showDropDown();
                    }
                });
            }

            getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        }
    }

    public class RegisterOrLoginMowazi extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String result = "";

            try {
                final String METHOD_NAME = "RegisterOrLoginFixOT";
                final String NAMESPACE = "http://tempuri.org/";

                // SOAP_ACTION = NAMESPACE + METHOD_NAME
                final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

                final String URL = MyApplication.mowaziUrl;

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                // parameters to be passed to the method (parameter name, value)
                Element[] header = new Element[1];

                header[0] = new Element().createElement(NAMESPACE, "FixOtHeader");

                Element username = new Element().createElement(NAMESPACE, "Username");
                username.addChild(Node.TEXT, MyApplication.currentUser.getUsername());
                header[0].addChild(Node.ELEMENT, username);

                Element investorNumber = new Element().createElement(NAMESPACE, "InvestorNumber");
                investorNumber.addChild(Node.TEXT, String.valueOf(MyApplication.currentUser.getInvestorId()));
                header[0].addChild(Node.ELEMENT, investorNumber);

                Element name = new Element().createElement(NAMESPACE, "Name");
                name.addChild(Node.TEXT, MyApplication.currentUser.getUsername());
                header[0].addChild(Node.ELEMENT, name);

                Element random = new Element().createElement(NAMESPACE, "Password");
                random.addChild(Node.TEXT, Actions.MD5(Actions.getRandom()));
                header[0].addChild(Node.ELEMENT, random);

                Element brokerid = new Element().createElement(NAMESPACE, "BrokerID");
                brokerid.addChild(Node.TEXT, MyApplication.mowaziBrokerId);
                header[0].addChild(Node.ELEMENT, brokerid);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.headerOut = header;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransport = new HttpTransportSE(URL);
                httpTransport.debug = true;

                httpTransport.call(SOAP_ACTION, envelope);
                result = envelope.getResponse().toString();
                SoapObject res = (SoapObject) envelope.bodyIn;
                SoapObject t = (SoapObject) res.getProperty("RegisterOrLoginFixOTResult");

                result = t.getProperty("success").toString();

            } catch (SoapFault sf) {
                System.out.println(sf.faultstring);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                if (result.contains("true")) {

                    String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                    try {

                        MyApplication.mowaziClientID = Integer.parseInt(result.split(",")[0]);
                        MyApplication.editor.putString("lastdate", formattedDate).apply();
                        MyApplication.editor.putInt("clientId", MyApplication.mowaziClientID).apply();

                        if (MyApplication.mshared.getString("oldToken", "").equals("") && MyApplication.mshared.getString("newToken", "").equals("")) {

                            MyApplication.editor.putString("oldToken", result.split(",")[2]);
                            MyApplication.editor.putString("newToken", result.split(",")[2]);
                            MyApplication.editor.putString("expiry", result.split(",")[3]);
                            MyApplication.editor.apply();
                        } else {

                            MyApplication.editor.putString("oldToken", MyApplication.mshared.getString("newToken", ""));
                            MyApplication.editor.putString("newToken", result.split(",")[2]);
                            MyApplication.editor.putString("expiry", result.split(",")[3]);
                            MyApplication.editor.apply();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    MyApplication.editor.apply();
                } else
                    Toast.makeText(getApplicationContext(), "Error when register in mowazi", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    protected class GetCompanies extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetCompaniesName?";

            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("lang", "" + MyApplication.lang);
            result = ConnectionRequests.POST(url, parameters);

            MowaziCompanyNameParser parser = new MowaziCompanyNameParser(result, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            try {
                allCompanies.clear();
                MowaziCompany c = new MowaziCompany();
                c.setSymbolEn(getString(R.string.mowazi_companies));
                c.setSymbolAr(getString(R.string.mowazi_companies));
                allCompanies.add(c);
                allCompanies.addAll(parser.GetCompanies());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            // pbOrders.setVisibility(View.GONE);
            spinnerAdapter.notifyDataSetChanged();

            getSectors = new GetSectors();
            getSectors.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            pbOrders.setVisibility(View.GONE);

        }
    }

    protected class GetSectors extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetSectors?";

            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("lang", MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");

            result = ConnectionRequests.POST(url, parameters);

            MowaziSectorParser parser = new MowaziSectorParser(result, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            try {
                allSectors.clear();
                MowaziSector sector = new MowaziSector();
                sector.setId(0);
                sector.setName(getResources().getString(R.string.sector));
                allSectors.add(sector);
                allSectors.addAll(parser.GetSectors());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            pbOrders.setVisibility(View.GONE);
            sectorSpinnerAdapter.notifyDataSetChanged();

            getOrderTypes = new GetOrderTypes();
            getOrderTypes.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    }

    protected class GetOrderTypes extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!pulltoRefresh)
                pbOrders.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetOrderTypes?";

            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("lang", "" + MyApplication.lang);

            result = ConnectionRequests.POST(url, parameters);

            MowaziOrderTypeParser parser = new MowaziOrderTypeParser(result, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            try {
                allOrderTypes.clear();
                MowaziOrderType order = new MowaziOrderType();
                order.setOrderTypeId(0);
                order.setNameEn(getResources().getString(R.string.mowazi_orderType));
                order.setNameAr(getResources().getString(R.string.mowazi_orderType));
                allOrderTypes.add(order);
                allOrderTypes.addAll(parser.GetOrderTypes());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            pbOrders.setVisibility(View.GONE);
            orderTypeSpinnerAdapter.notifyDataSetChanged();
            onItemsLoadComplete();
            // getOrderBookSummary = new GetMyOrderBookSummary();
            // getOrderBookSummary.execute("0");
        }
    }

}
