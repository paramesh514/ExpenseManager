package ve.com.abicelis.creditcardexpensemanager.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.holders.CategoryViewHolder;
import ve.com.abicelis.creditcardexpensemanager.model.TransactionCategory;

/**
 * Created by Alex on 7/8/2016.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

    private List<TransactionCategory> mExpenses;
    //private int mCreditPeriodId;
    private LayoutInflater mInflater;
    private Fragment mFragment;
    private CategoryViewHolder.CategoryDeletedListener mListener;

    public CategoryAdapter(Fragment fragment, List<TransactionCategory> expenses,  CategoryViewHolder.CategoryDeletedListener listener) {
        mFragment = fragment;
        mExpenses = expenses;
       // mCreditPeriodId = creditPeriodId;
        mInflater = LayoutInflater.from(mFragment.getContext());
        mListener = listener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = mInflater.inflate(R.layout.list_item_categories, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        TransactionCategory current = mExpenses.get(position);
        holder.setData(this, mFragment, current, position);
        holder.setListeners();
        holder.setOnCategoryDeletedListener(mListener);
    }

    @Override
    public int getItemCount() {
        return mExpenses.size();
    }

    /*public void removeExpense(int position) {
        mExpenses.remove(position);
    }*/
}
