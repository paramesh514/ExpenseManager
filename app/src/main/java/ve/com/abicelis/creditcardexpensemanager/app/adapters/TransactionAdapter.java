package ve.com.abicelis.creditcardexpensemanager.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.holders.ExpensesViewHolder;
import ve.com.abicelis.creditcardexpensemanager.app.holders.TransactionViewHolder;
import ve.com.abicelis.creditcardexpensemanager.model.Expense;
import ve.com.abicelis.creditcardexpensemanager.model.Transaction;

/**
 * Created by Alex on 7/8/2016.
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionViewHolder> {

    private List<Transaction> mExpenses;
    private int mCreditPeriodId;
    private LayoutInflater mInflater;
    private Fragment mFragment;
    private TransactionViewHolder.TransactionDeletedListener mListener;

    public TransactionAdapter(Fragment fragment, List<Transaction> expenses, int creditPeriodId, TransactionViewHolder.TransactionDeletedListener listener) {
        mFragment = fragment;
        mExpenses = expenses;
        mCreditPeriodId = creditPeriodId;
        mInflater = LayoutInflater.from(mFragment.getContext());
        mListener = listener;
    }

    @Override
    public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = mInflater.inflate(R.layout.list_item_expenses, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        Transaction current = mExpenses.get(position);
        holder.setData(this, mFragment, current, mCreditPeriodId, position);
        holder.setListeners();
        holder.setOnExpenseDeletedListener(mListener);
    }

    @Override
    public int getItemCount() {
        return mExpenses.size();
    }

    /*public void removeExpense(int position) {
        mExpenses.remove(position);
    }*/
}
