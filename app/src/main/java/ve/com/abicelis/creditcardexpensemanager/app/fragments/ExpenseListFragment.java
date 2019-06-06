package ve.com.abicelis.creditcardexpensemanager.app.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.activities.OcrCreateExpenseActivity;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.TransactionAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.dialogs.CreateOrEditTransactionDialogFragment;
import ve.com.abicelis.creditcardexpensemanager.app.holders.ExpensesViewHolder;
import ve.com.abicelis.creditcardexpensemanager.app.holders.TransactionViewHolder;
import ve.com.abicelis.creditcardexpensemanager.app.utils.Constants;
import ve.com.abicelis.creditcardexpensemanager.app.utils.SharedPreferencesUtils;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotDeleteDataException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditCardNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditPeriodNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.SharedPreferenceNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.model.Account;
import ve.com.abicelis.creditcardexpensemanager.model.Transaction;

/**
 * Created by Alex on 3/9/2016.
 */
//Transaction List Fragment
public class ExpenseListFragment extends Fragment {

    //Data
    int activeCreditCardId = -1;
    Account activeAccount = null;
    List<Transaction> creditCardExpenses = new ArrayList<>();
    ExpenseManagerDAO mDao;

    //UI
    RecyclerView recyclerViewExpenses;
    LinearLayoutManager mLayoutManager;
    TransactionAdapter mAdapter;
    FloatingActionMenu fabMenu;
    FloatingActionButton fabNewExpense;
    FloatingActionButton fabNewExpenseCamera;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout noCCContainer;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_name_expense_list));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDao();

        try {
            activeCreditCardId = SharedPreferencesUtils.getInt(getContext(), Constants.ACTIVE_CC_ID);
            try {
                refreshData();
            }catch (CreditCardNotFoundException e ) {
                Toast.makeText(getActivity(), getResources().getString(R.string.err_problem_loading_card_or_no_card_exists), Toast.LENGTH_SHORT).show();
            }catch (CreditPeriodNotFoundException e) {
                Toast.makeText(getActivity(), getResources().getString(R.string.err_problem_loading_credit_period), Toast.LENGTH_SHORT).show();
            }
        }catch(SharedPreferenceNotFoundException e) {
            //This shouldn't happen
        }
    }

    @Override
    public void onResume() {
        refreshRecyclerView();
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expense_list, container, false);

        recyclerViewExpenses = (RecyclerView) rootView.findViewById(R.id.home_recycler_expenses);
        noCCContainer = (RelativeLayout) rootView.findViewById(R.id.home_err_no_cc_container);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.home_swipe_refresh);
        fabMenu = (FloatingActionMenu) rootView.findViewById(R.id.home_fab_menu);
        fabNewExpense = (FloatingActionButton) rootView.findViewById(R.id.home_fab_new_expense);
        fabNewExpenseCamera = (FloatingActionButton) rootView.findViewById(R.id.home_fab_new_expense_camera);
//        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) rootView.findViewById(R.id.home_collapsing);
//        AppBarLayout appBarLayout = (AppBarLayout) rootView.findViewById(R.id.home_appbar);
//        toolbar = (Toolbar) rootView.findViewById(R.id.home_toolbar);

        if(activeAccount != null) {
            setUpRecyclerView(rootView);
            setUpSwipeRefresh(rootView);
            //setUpToolbar(rootView);
            setUpFab(rootView);
        }
        else {
            noCCContainer.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            fabMenu.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        view.post(new Runnable() {
//            @Override
//            public void run() {
//                chartFragment = (ChartExpenseFragment) getFragmentManager().findFragmentById(R.id.home_chart_container);
//            }
//        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //If coming back from expenseDetailActivity and data was edited/deleted, refresh.
        if (requestCode == Constants.EXPENSE_DETAIL_ACTIVITY_REQUEST_CODE && resultCode == Constants.RESULT_REFRESH_DATA) {
            //Toast.makeText(getActivity(), "Refreshing Expenses!", Toast.LENGTH_SHORT).show();
            //refreshChart();
            refreshRecyclerView();
        }

    }


//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_overview, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        switch(id) {
//            case R.id.overview_menu_add_card:
//                Toast.makeText(getActivity(), "oaiwjdoiwd", Toast.LENGTH_SHORT).show();
//                break;
//        }
//
//        return true;
//    }

    private void loadDao() {
        if(mDao == null)
            mDao = new ExpenseManagerDAO(getActivity().getApplicationContext());
    }



    private void setUpRecyclerView(View rootView) {


        TransactionViewHolder.TransactionDeletedListener listener = new TransactionViewHolder.TransactionDeletedListener() {
            @Override
            public void OnTransactionDeleted(int position) {
                try {

                    mDao.deleteTransaction(creditCardExpenses.get(position).getId());
                    creditCardExpenses.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
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

    private void setUpSwipeRefresh(View rootView) {
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_green, R.color.swipe_refresh_red, R.color.swipe_refresh_yellow);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                    @Override
                                                    public void onRefresh() {
                                                        refreshRecyclerView();
                                                        //refreshChart();
                                                        swipeRefreshLayout.setRefreshing(false);
                                                    }
                                                }
        );
    }

//    private void setUpToolbar(View rootView) {
//

//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            boolean isShow = false;
//            int scrollRange = -1;
//
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (scrollRange == -1) {
//                    scrollRange = appBarLayout.getTotalScrollRange();
//                }
//                if (scrollRange + verticalOffset == 0) {
//                    collapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));
//                    isShow = true;
//                } else if(isShow) {
//                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
//
//                    isShow = false;
//                }
//            }
//        });
//    }

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
                Intent intent = new Intent(getActivity(), OcrCreateExpenseActivity.class);
      //          intent.putExtra(OcrCreateExpenseActivity.TAG_EXTRA_PERIOD_ID, activeAccount.getCreditPeriods().get(0).getId());
                intent.putExtra(OcrCreateExpenseActivity.TAG_EXTRA_CURRENCY, activeAccount.getCurrency());
                startActivity(intent);
            }
        });

    }


    public void refreshData() throws CreditCardNotFoundException, CreditPeriodNotFoundException {
        try {
            activeAccount = mDao.getAccount(activeCreditCardId);
        }
        catch(Exception e)
        {
            //shouldnt happen
        }

        //Clear the list and refresh it with new data, this must be done so the mAdapter
        // doesn't lose track of creditCardExpenses object when overwriting
        // activeAccount.getCreditPeriods().get(0).getExpenses();
        creditCardExpenses.clear();
        creditCardExpenses.addAll(mDao.getTransactionList());

    }


    public void refreshRecyclerView() {

        if(activeAccount != null) {
            loadDao();

            int oldExpensesCount = creditCardExpenses.size();
            try {
                refreshData();
            }catch (CreditCardNotFoundException e ) {
                Toast.makeText(getActivity(), getResources().getString(R.string.err_problem_loading_card_or_no_card_exists), Toast.LENGTH_SHORT).show();
                return;
            }catch (CreditPeriodNotFoundException e) {
                Toast.makeText(getActivity(), getResources().getString(R.string.err_problem_loading_credit_period), Toast.LENGTH_SHORT).show();
                return;
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

    }

//    private void refreshChart(){
//        //TODO: this is probably a hack.. maybe a listener is needed here?
//        //Refresh chartFragment
//        if(chartFragment != null)
//            chartFragment.refreshData();
//        else
//            Toast.makeText(getActivity(), "Error on onDismiss, chartFragment == null!", Toast.LENGTH_SHORT).show();
//    }

    private void showCreateExpenseDialog() {
        FragmentManager fm = getFragmentManager();
        CreateOrEditTransactionDialogFragment dialog = CreateOrEditTransactionDialogFragment.newInstance(
                mDao,
                activeAccount.getCurrency(),
                null);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                refreshRecyclerView();
                //refreshChart();
            }
        });
        dialog.show(fm, "fragment_dialog_create_transaction");
    }


}
