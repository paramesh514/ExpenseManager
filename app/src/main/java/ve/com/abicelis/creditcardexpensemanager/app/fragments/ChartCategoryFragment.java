package ve.com.abicelis.creditcardexpensemanager.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.enums.ExpenseCategory;
import ve.com.abicelis.creditcardexpensemanager.enums.TransactionType;
import ve.com.abicelis.creditcardexpensemanager.model.TransactionCategory;

/**
 * Created by abice on 30/1/2017.
 */

public class ChartCategoryFragment extends Fragment {


    //UI
    private PieChartView chart;
    private RelativeLayout mNoExpensesContainer;

    //DATA
    private boolean chartIsVisible = false;
    //private int activeCreditCardId;
    private ExpenseManagerDAO dao;
    private PieChartData data;
    private List<TransactionCategory> categories;
    //CreditPeriod creditPeriod;
    //CreditCard creditCard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_chart_categories, container, false);
        chart = (PieChartView) rootView.findViewById(R.id.chart_categories_piechart);
        mNoExpensesContainer = (RelativeLayout) rootView.findViewById(R.id.chart_categories_no_expenses_container);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !chartIsVisible) {
            refreshData();
            chartIsVisible = true;
        }
        else {
        }
    }



    public void refreshData() {
        if(dao == null)
            dao = new ExpenseManagerDAO(getActivity());

        categories = dao.getTransactionCategoryList(TransactionType.EXPENSE);

        //Check if there are no expenses in this period or there is no active credit card
        if(dao.getTransactionList().size() ==0) {
            chart.setVisibility(View.GONE);
            mNoExpensesContainer.setVisibility(View.VISIBLE);
            return;
        }

        //int numCategories = categories.size();
        //List<BigDecimal> expenseByCategory = creditPeriod.getExpensesByCategory();
        List<SliceValue> sliceValues = new ArrayList<>();

        int index=0;
        for (TransactionCategory tc:categories) {
            //SliceValue sliceValue = new SliceValue(expenseByCategory[i].floatValue(), ContextCompat.getColor(getContext(), ExpenseCategory.values()[i].getColor() ));
            SliceValue sliceValue = new SliceValue(20, ContextCompat.getColor(getContext(), ExpenseCategory.values()[index%5].getColor() ));
            sliceValue.setTarget((float)tc.getSpent());
            sliceValue.setLabel(getExpenseLabel(tc.getSpent(),dao.getBudgetSpendForCurrentMonth(TransactionType.EXPENSE,0),tc.getmName()));
            sliceValues.add(sliceValue);
            index++;
        }


        //Setup chart
        data = new PieChartData(sliceValues);
        data.setHasLabels(true);
        //data.setHasLabelsOutside(true);
        chart.setPieChartData(data);

        chart.startDataAnimation(10000);

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

}

