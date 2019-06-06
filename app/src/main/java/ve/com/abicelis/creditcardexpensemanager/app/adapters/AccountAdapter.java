package ve.com.abicelis.creditcardexpensemanager.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.holders.AccountViewHolder;
import ve.com.abicelis.creditcardexpensemanager.app.holders.CreditCardViewHolder;
import ve.com.abicelis.creditcardexpensemanager.model.Account;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

/**
 * Created by Alex on 30/8/2016.
 */
public class AccountAdapter extends RecyclerView.Adapter<AccountViewHolder>  {

    private List<Account> mCreditCards;
    private LayoutInflater mInflater;
    private Context mContext;
    private Fragment mFragment;
    private AccountViewHolder.AccountSelectedListener mCCSelectedListener;

    public AccountAdapter(Context context, Fragment fragment, List<Account> creditCards) {
        mContext = context;
        mFragment = fragment;
        mCreditCards = creditCards;
        mInflater = LayoutInflater.from(context);
    }

    public void setCreditCardSelectedListener(AccountViewHolder.AccountSelectedListener ccSelectedListener) {
        mCCSelectedListener = ccSelectedListener;
    }


    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_credit_card, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {
        Account current = mCreditCards.get(position);
        holder.setData(mContext, mFragment, current, position);

        if(mCCSelectedListener != null) {
            holder.setListeners();
            holder.setOnCreditCardSelectedListener(mCCSelectedListener);
        }
        else
            Toast.makeText(mContext, "Warning: mCCSelectedListener == null!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return mCreditCards.size();
    }
}
