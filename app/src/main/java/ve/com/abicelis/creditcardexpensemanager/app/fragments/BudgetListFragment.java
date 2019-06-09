package ve.com.abicelis.creditcardexpensemanager.app.fragments;

import android.content.DialogInterface;
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
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.BudgetAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.CategoryAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.dialogs.CreateOrEditCategoryDialogFragment;
import ve.com.abicelis.creditcardexpensemanager.app.dialogs.EditOrDeleteAccountDialogFragment;
import ve.com.abicelis.creditcardexpensemanager.app.holders.BudgetViewHolder;
import ve.com.abicelis.creditcardexpensemanager.app.holders.CategoryViewHolder;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotDeleteDataException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditCardNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditPeriodNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.model.Account;
import ve.com.abicelis.creditcardexpensemanager.model.TransactionCategory;

/**
 * Created by Alex on 3/9/2016.
 */
public class BudgetListFragment extends Fragment {

    //Data
    List<TransactionCategory> categories = new ArrayList<>();
    List<TransactionCategory> budgets = new ArrayList<>();
    ExpenseManagerDAO dao;

    //UI
    RecyclerView recycler_budget;
    RecyclerView recycler_category;
    LinearLayoutManager layoutManager;
    BudgetAdapter budget_adapter;
    CategoryAdapter category_adapter;
    FloatingActionButton fabNewCreditCard;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_name_budget));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadDao();
        for(TransactionCategory tc:dao.getTransactionCategoryList()) {
            if(tc.getBudget()>0||tc.getId()==0)
                budgets.add(tc);
            else
                categories.add(tc);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories_list, container, false);

        setUpRecyclerBudgetView(rootView);
        setUpRecyclerCatoegyView(rootView);
        setUpSwipeRefresh(rootView);
        setUpFab(rootView);

        return rootView;
    }


    private void loadDao() {
        if(dao == null)
            dao = new ExpenseManagerDAO(getActivity().getApplicationContext());
    }


    private void setUpRecyclerBudgetView(View rootView) {

        recycler_budget = (RecyclerView) rootView.findViewById(R.id.ccl_recycler_budget);
        BudgetViewHolder.CategoryDeletedListener listener = new BudgetViewHolder.CategoryDeletedListener() {
            @Override
            public void OnCategoryDeleted(int position) {
                try {

                    dao.deleteTransactionCategory(budgets.get(position).getId());
                    budgets.remove(position);
                    budget_adapter.notifyItemRemoved(position);
                    budget_adapter.notifyItemRangeChanged(position, budget_adapter.getItemCount());
                    //refreshChart();
                }catch (CouldNotDeleteDataException e) {
                    Toast.makeText(getActivity(), "There was an error deleting the expense!", Toast.LENGTH_SHORT).show();
                }

            }
        };
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        budget_adapter = new BudgetAdapter(this, budgets, listener);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recycler_budget.getContext(), layoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.item_decoration_half_line));


        recycler_budget.addItemDecoration(itemDecoration);
        recycler_budget.setLayoutManager(layoutManager);

        recycler_budget.setAdapter(budget_adapter);


    }
    private void setUpRecyclerCatoegyView(View rootView) {

        recycler_category = (RecyclerView) rootView.findViewById(R.id.ccl_recycler_categories);
        CategoryViewHolder.CategoryDeletedListener listener = new CategoryViewHolder.CategoryDeletedListener() {
            @Override
            public void OnCategoryDeleted(int position) {
                try {

                    dao.deleteTransactionCategory(categories.get(position).getId());
                    categories.remove(position);
                    category_adapter.notifyItemRemoved(position);
                    category_adapter.notifyItemRangeChanged(position, category_adapter.getItemCount());
                    //refreshChart();
                }catch (CouldNotDeleteDataException e) {
                    Toast.makeText(getActivity(), "There was an error deleting the expense!", Toast.LENGTH_SHORT).show();
                }

            }
        };
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        category_adapter = new CategoryAdapter(this, categories, listener);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(recycler_category.getContext(), layoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.item_decoration_half_line));


        recycler_category.addItemDecoration(itemDecoration);
        recycler_category.setLayoutManager(layoutManager);

        recycler_category.setAdapter(category_adapter);


    }
    private void setUpSwipeRefresh(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.ccl_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe_refresh_green, R.color.swipe_refresh_red, R.color.swipe_refresh_yellow);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                                                    @Override
                                                    public void onRefresh() {
                                                        refreshRecyclerView();
                                                        swipeRefreshLayout.setRefreshing(false);
                                                    }
                                                }
        );
    }


    private void setUpFab(View rootView) {
        fabNewCreditCard = (FloatingActionButton) rootView.findViewById(R.id.ccl_fab_add_category);

        fabNewCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateExpenseDialog();
            }
        });
    }
    private void showCreateExpenseDialog() {
        FragmentManager fm = getFragmentManager();
        CreateOrEditCategoryDialogFragment dialog = CreateOrEditCategoryDialogFragment.newInstance(
                dao,
                null);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                refreshRecyclerView();
                //refreshChart();
            }
        });
        dialog.show(fm, "fragment_dialog_create_category");
    }


    public void refreshData() throws CreditCardNotFoundException, CreditPeriodNotFoundException {
        //Clear the list and refresh it with new data, this must be done so the mAdapter
        // doesn't lose track of categories list
        categories.clear();
        budgets.clear();;
        for(TransactionCategory tc:dao.getTransactionCategoryList()) {
            if(tc.getBudget()>0||tc.getId()==0)
                budgets.add(tc);
            else
                categories.add(tc);
        }
    }


    public void refreshRecyclerView() {
        loadDao();

        int oldCount = categories.size();
        int oldCount_b = budgets.size();
        try {
            refreshData();
        }catch (CreditCardNotFoundException e ) {
            Toast.makeText(getActivity(), getResources().getString(R.string.err_problem_loading_card_or_no_card_exists), Toast.LENGTH_SHORT).show();
            return;
        }catch (CreditPeriodNotFoundException e) {
            Toast.makeText(getActivity(), getResources().getString(R.string.err_problem_loading_credit_period), Toast.LENGTH_SHORT).show();
            return;
        }

        int newCount = categories.size();
        int newCount_b = budgets.size();


        //If a new credit card was added
        if(newCount == oldCount+1) {
            category_adapter.notifyItemInserted(0);
            category_adapter.notifyItemRangeChanged(1, categories.size()-1);
            layoutManager.scrollToPosition(0);
        } else {
            category_adapter.notifyDataSetChanged();
        }
        //If a new credit card was added
        if(newCount_b == oldCount_b+1) {
            budget_adapter.notifyItemInserted(0);
            budget_adapter.notifyItemRangeChanged(1, budgets.size()-1);
            layoutManager.scrollToPosition(0);
        } else {
            budget_adapter.notifyDataSetChanged();
        }

    }


}
