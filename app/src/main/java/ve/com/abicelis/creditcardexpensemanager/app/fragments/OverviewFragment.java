package ve.com.abicelis.creditcardexpensemanager.app.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;
import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.activities.OcrCreateExpenseActivity;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.TransactionAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.dialogs.CreateOrEditTransactionDialogFragment;
import ve.com.abicelis.creditcardexpensemanager.app.holders.TransactionViewHolder;
import ve.com.abicelis.creditcardexpensemanager.app.views.HorizontalBar;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseCategory;
import ve.com.abicelis.creditcardexpensemanager.enums.TransactionType;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotDeleteDataException;
import ve.com.abicelis.creditcardexpensemanager.model.Transaction;
import ve.com.abicelis.creditcardexpensemanager.model.TransactionCategory;

/**i
 * Created by abice on 4/10/2016.
 */

public class OverviewFragment extends Fragment {

    //CONST
    private static final String TAG = OverviewFragment.class.getSimpleName();

    //DATA
    //int activeCreditCardId = -1;
    //Account activeCreditCard = null;
    ExpenseManagerDAO dao;

    List<Transaction> creditCardExpenses = new ArrayList<>();

    //UI
    RecyclerView recyclerViewExpenses;
    LinearLayoutManager mLayoutManager;
    TransactionAdapter mAdapter;
    FloatingActionMenu fabMenu;
    FloatingActionButton fabNewExpense;
    FloatingActionButton fabNewExpenseCamera;

    //UI
    //SelectableAccountViewHolder holder;
   // View headerCreditCardContainer;
    //HorizontalBar creditDatePeriodBar;
    HorizontalBar creditBalanceBar;
    //TextView extraInfo;
//    ScrollView scrollViewContainer;

    //UI
    private PieChartView chart;
    private RelativeLayout mNoExpensesContainer;

    //DATA
    private boolean chartIsVisible = false;
    private PieChartData data;
    private List<TransactionCategory> categories;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_name_overview));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDao();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        //headerCreditCardContainer = view.findViewById(R.id.list_item_credit_card_container);
      // creditDatePeriodBar = (HorizontalBar) view.findViewById(R.id.frag_overview_credit_date_period_bar);
        creditBalanceBar = (HorizontalBar) view.findViewById(R.id.frag_overview_credit_balance_bar);
        //extraInfo = (TextView) view.findViewById(R.id.frag_overview_extra_info);
//        scrollViewContainer = (ScrollView) view.findViewById(R.id.frag_overview_body_scroll_view_container);
        chart = (PieChartView) view.findViewById(R.id.overview_chart_categories_piechart);
        mNoExpensesContainer = (RelativeLayout) view.findViewById(R.id.overview_chart_categories_no_expenses_container);
        recyclerViewExpenses = (RecyclerView) view.findViewById(R.id.overview_recycler_expenses);
        fabMenu = (FloatingActionMenu) view.findViewById(R.id.overview_fab_menu);
        fabNewExpense = (FloatingActionButton) view.findViewById(R.id.home_fab_new_cash_income);
        fabNewExpenseCamera = (FloatingActionButton) view.findViewById(R.id.home_fab_new_cash_expense);

        setUpRecyclerView(view);
        setUpFab(view);

        refreshUI();

        return view;
    }

    private void loadDao() {
        if(dao == null)
            dao = new ExpenseManagerDAO(getActivity().getApplicationContext());
    }

    private void setUpFab(View rootView) {
        fabMenu.setClosedOnTouchOutside(true);


        fabNewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                showCreateExpenseDialog();
            }
        });
        fabNewExpenseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                showCreateExpenseDialog();
            }
        });

    }
    private void showCreateExpenseDialog() {
        FragmentManager fm = getFragmentManager();
        CreateOrEditTransactionDialogFragment dialog = CreateOrEditTransactionDialogFragment.newInstance(
                dao,
                Currency.INR,
                null);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                refreshUI();
                //refreshRecyclerView();
                //refreshChart();
            }
        });
        dialog.show(fm, "fragment_dialog_create_transaction");
    }

    private void refreshUI() {
        loadDao();

        //Hide all
      ///  scrollViewContainer.setVisibility(View.GONE);
     //   headerCreditCardContainer.setVisibility(View.GONE);



         ///       scrollViewContainer.setVisibility(View.VISIBLE);
       //         headerCreditCardContainer.setVisibility(View.VISIBLE);

                /* DatePeriod bar */
            //    Calendar today = Calendar.getInstance();
        //        Calendar startDate = activeAccount.getCreditPeriods().get(0).getStartDate();
          //      Calendar endDate = activeAccount.getCreditPeriods().get(0).getEndDate();
               // int daysBetweenStartAndToday = DateUtils.getDaysBetween(startDate, today);
            //    int daysInPeriod = activeAccount.getCreditPeriods().get(0).getTotalDaysInPeriod();
           //     int datePeriodPercentage;
              //  if(daysInPeriod > 0)
                //    datePeriodPercentage = (int)(100*((float)daysBetweenStartAndToday/daysInPeriod));
                //else
                  //  datePeriodPercentage = 0;


                /* Balance bar */
                //int creditLimit = activeAccount.getCreditPeriods().get(0).getCreditLimit().toBigInteger().intValue();
                //int expensesTotal = activeAccount.getCreditPeriods().get(0).getExpensesTotal().toBigInteger().intValue();
                String currencyCode = "INR";
             //   int balancePercentage;
                //if(creditLimit > 0)
                  //  balancePercentage = (int)(100*((float)expensesTotal/creditLimit));
                //else
                  //  balancePercentage = 0;
                TransactionCategory tcAll = dao.getTransactionCategory(0);
                double creditLimit = tcAll.getBudget();
                double expensesTotal = tcAll.getSpent();
                if(tcAll.getBudget() > 0) {
                    creditBalanceBar.setProgressPercentage((int) (expensesTotal * 100 / creditLimit));
                    creditBalanceBar.setTextHi(creditLimit + " " + currencyCode);
                    if (expensesTotal > 0)
                        creditBalanceBar.setTextBar(Double.toString(expensesTotal) + " " + currencyCode);
                    creditBalanceBar.setTextLo("0 " + currencyCode);
                }


          ///      extraInfo.setText(TextUtils.fromHtml(generateExtraInfo()));

                //Setup cc data
         //       holder = new SelectableCreditCardViewHolder(headerCreditCardContainer);
         //       holder.setData(getContext(), activeCreditCard, 0);


        categories = dao.getTransactionCategoryList(TransactionType.EXPENSE);

        //Check if there are no expenses in this period or there is no active credit card
        if(dao.getTransactionList(TransactionType.EXPENSE).size() ==0) {
            chart.setVisibility(View.GONE);
            mNoExpensesContainer.setVisibility(View.VISIBLE);
            return;
        }
        else
        {
            chart.setVisibility(View.VISIBLE);
            mNoExpensesContainer.setVisibility(View.GONE);

        }

        //int numCategories = categories.size();
        //List<BigDecimal> expenseByCategory = creditPeriod.getExpensesByCategory();
        List<SliceValue> sliceValues = new ArrayList<>();


        for (TransactionCategory tc:categories) {
            //SliceValue sliceValue = new SliceValue(expenseByCategory[i].floatValue(), ContextCompat.getColor(getContext(), ExpenseCategory.values()[i].getColor() ));
            SliceValue sliceValue = new SliceValue(20, ContextCompat.getColor(getContext(), ExpenseCategory.values()[tc.getId()%5].getColor() ));
            sliceValue.setTarget((float)tc.getSpent());
            sliceValue.setLabel(getExpenseLabel(tc.getSpent(),dao.getBudgetSpendForCurrentMonth(TransactionType.EXPENSE,0),tc.getmName()));
            sliceValues.add(sliceValue);
        }


        //Setup chart
        data = new PieChartData(sliceValues);
        data.setHasLabels(true);
        //data.setHasLabelsOutside(true);
        chart.setPieChartData(data);

        chart.startDataAnimation(10000);

        refreshRecyclerView();


    }
    public void refreshRecyclerView() {

        loadDao();

        int oldExpensesCount = creditCardExpenses.size();

        creditCardExpenses.clear();
        int count = 1;
        for(Transaction tc:dao.getTransactionList()) {
            creditCardExpenses.add(tc);
            count++;
            if(count >2)
                break;
        }
        int newExpensesCount = creditCardExpenses.size();

        //TODO: in the future, expenses wont necessarily be added with date=now,
        //TODO: meaning they wont always be added on recyclerview position = 0
        //If a new expense was added
        if(newExpensesCount == oldExpensesCount+1) {
            mAdapter.notifyItemInserted(0);
            //      mAdapter.notifyItemRangeChanged(1, activeAccount.getCreditPeriods().get(0).getExpenses().size()-1);
            mLayoutManager.scrollToPosition(0);
        } else {
            mAdapter.notifyDataSetChanged();
        }

    }

    private void setUpRecyclerView(View rootView) {


        TransactionViewHolder.TransactionDeletedListener listener = new TransactionViewHolder.TransactionDeletedListener() {
            @Override
            public void OnTransactionDeleted(int position) {
                try {

                    dao.deleteTransaction(creditCardExpenses.get(position).getId());
                    creditCardExpenses.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
                    refreshUI();
                    //refreshChart();
                }catch (CouldNotDeleteDataException e) {
                    Toast.makeText(getActivity(), "There was an error deleting the expense!", Toast.LENGTH_SHORT).show();
                }

            }
        };
        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mAdapter = new TransactionAdapter(this, creditCardExpenses, 0, listener);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recyclerViewExpenses.getContext(), mLayoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.item_decoration_half_line));


        recyclerViewExpenses.addItemDecoration(itemDecoration);
        recyclerViewExpenses.setLayoutManager(mLayoutManager);

        recyclerViewExpenses.setAdapter(mAdapter);
    }
    private String getExpenseLabel(double expenseValue,double total, String expenseName) {
        BigDecimal expenseTotal;
        BigDecimal aux;
        expenseTotal = new BigDecimal(expenseValue);
        BigDecimal totalBig = new BigDecimal(total);
        if(total!=0.0) {
            if(expenseValue!=0.0) {
                aux = expenseTotal.divide(totalBig, 3, RoundingMode.CEILING);
                aux = aux.multiply(new BigDecimal(100));
                return String.format(Locale.getDefault(), "%1$s %2$s%%", expenseName, new DecimalFormat("#0.#").format(aux));
            }
        }

        return "";
    }

/*
    private String generateExtraInfo() {

//        <string name="fragment_overview_credit_extra_info_spendiest_day">The spendiest day in this period is %1$s, you spent %2$s on $3$s.</string>
//        <string name="fragment_overview_credit_extra_info_spendiest_category">The %1$s category represents %2$d%% if this month\'s expenses.</string>
//                <string name="fragment_overview_credit_extra_info_average_per_day">You spend an average of %1$s a day.</string>
//        <string name="fragment_overview_credit_extra_info_to_spend_average">You have %1$s left to spend, that is an average of %2$s a day.</string>

        List<String> extraInfos = new ArrayList<>();
        String result = "";
        String info1 = getResources().getString(R.string.fragment_overview_credit_extra_info_spendiest_day);
        String info2 = getResources().getString(R.string.fragment_overview_credit_extra_info_spendiest_category);
        String info3 = getResources().getString(R.string.fragment_overview_credit_extra_info_average_per_day);
        String info4 = getResources().getString(R.string.fragment_overview_credit_extra_info_to_spend_average);

        BigDecimal maxDailyExpense = new BigDecimal(0);
        String maxDailyExpenseDate = null;
        /*List<DailyExpense> dailyExpenses = activeAccount.getCreditPeriods().get(0).getDailyExpenses();
        for(DailyExpense de : dailyExpenses) {
            if(de.getAmount().compareTo(maxDailyExpense) == 1) {  //If current larger than max
                maxDailyExpense = de.getAmount();
                maxDailyExpenseDate = de.getFormattedDate();
            }
        }*

        if(maxDailyExpenseDate != null)
            extraInfos.add(String.format(Locale.getDefault(), info1, maxDailyExpenseDate, maxDailyExpense.toBigInteger().toString(), activeCreditCard.getCurrency().getCode()));





        /*List<BigDecimal> expensesByCategory = activeAccount.getCreditPeriods().get(0).getExpensesByCategory();
        BigDecimal expenseTotal = activeAccount.getCreditPeriods().get(0).getExpensesTotal();*
        BigDecimal maxCategory = new BigDecimal(0);
        String maxCategoryName = "";



  *      if(expenseTotal.compareTo(BigDecimal.ZERO) == 1) {

            for(int i = 0; i < ExpenseCategory.values().length; i++) {
                if(expensesByCategory.get(i).compareTo(maxCategory) == 1) {  //If current larger than max
                    maxCategory = expensesByCategory.get(i);
                    maxCategoryName = ExpenseCategory.getByExpenseCategoryId(i).getName();
                }
            }

            BigDecimal percentOfTotalMaxCategory = maxCategory.divide(expenseTotal, 2, RoundingMode.HALF_UP);
            percentOfTotalMaxCategory = percentOfTotalMaxCategory.multiply(new BigDecimal(100));
            extraInfos.add(String.format(Locale.getDefault(), info2, maxCategoryName,  percentOfTotalMaxCategory.toBigInteger().toString()));
        }

*

        Calendar today = Calendar.getInstance();
        //Calendar startDate = activeAccount.getCreditPeriods().get(0).getStartDate();
        //Calendar endDate = activeAccount.getCreditPeriods().get(0).getEndDate();
        //int daysBetweenStartAndToday = DateUtils.getDaysBetween(startDate, today);
        //int daysBetweenTodayAndEnd = DateUtils.getDaysBetween(today, endDate);
        //BigDecimal expensesTotal = activeAccount.getCreditPeriods().get(0).getExpensesTotal();
        //BigDecimal creditLimit = activeAccount.getCreditPeriods().get(0).getCreditLimit();
        //BigDecimal creditToSpend = creditLimit.subtract(expensesTotal);
 //       String currencyCode = activeCreditCard.getCurrency().getCode();
/*
        if(expensesTotal.compareTo(BigDecimal.ZERO) == 1 && daysBetweenStartAndToday > 0) {
            BigDecimal average = expensesTotal.divide(new BigDecimal(daysBetweenStartAndToday), 1, RoundingMode.HALF_UP);
            average = average.setScale(1, RoundingMode.HALF_UP);
            extraInfos.add(String.format(Locale.getDefault(), info3, average.toPlainString(), currencyCode));
        }

        if(creditToSpend.compareTo(BigDecimal.ZERO) == 1 && daysBetweenTodayAndEnd > 0) {
            BigDecimal averageToSpend = creditToSpend.divide(new BigDecimal(daysBetweenTodayAndEnd), 1, RoundingMode.HALF_UP);
            averageToSpend = averageToSpend.setScale(1, RoundingMode.HALF_UP);
            creditToSpend = creditToSpend.setScale(1, RoundingMode.HALF_UP);
            extraInfos.add(String.format(Locale.getDefault(), info4, creditToSpend.toPlainString(), averageToSpend.toPlainString(), currencyCode));
        }

*



        if(extraInfos.size() > 0) {
            result += "&#8226; " + extraInfos.get(0);
            for (int i = 1; i < extraInfos.size(); i++) {
                result += "<br/>&#8226; " + extraInfos.get(i);
            }
        }

        return result;
    }

*
    private void createACurrentCreditPeriod() {
        //If there is no current credit period, create one

        try {
            //Get the credit card's currency and the credit period limit of the previous period
            CreditCard cc = dao.getCreditCard(activeCreditCardId);
            BigDecimal previousCreditPeriodLimit = dao.getCreditPeriodListFromCard(activeCreditCardId).get(0).getCreditLimit();

            //Call dialog, ask user if previousCreditPeriodLimit is okay
            FragmentManager fm = getActivity().getSupportFragmentManager();
            CheckCreditPeriodLimitDialogFragment dialog = CheckCreditPeriodLimitDialogFragment.newInstance(
                    dao,
                    cc,
                    previousCreditPeriodLimit);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    try {
                       // activeAccount = dao.getCreditCardWithCreditPeriod(activeCreditCardId, 0);
                        refreshUI();
                    }catch (Exception e) {
                        Toast.makeText(getActivity(), "Error refreshing credit card data", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show(fm, "fragment_dialog_new_credit_period");

        } catch (CreditCardNotFoundException e) {
            Toast.makeText(getActivity(), "Error getting credit card data", Toast.LENGTH_SHORT).show();
        }

    }*/

}
