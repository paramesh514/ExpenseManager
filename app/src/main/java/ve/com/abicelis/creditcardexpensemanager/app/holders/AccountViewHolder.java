package ve.com.abicelis.creditcardexpensemanager.app.holders;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.model.Account;

/**
 * Created by Alex on 30/8/2016.
 */
public class AccountViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context mContext;
    private Fragment mFragment;
    AccountSelectedListener mListener = null;


    //DATA
    private Account mCurrent;
    private int mPosition;

    //UI
    private RelativeLayout container;
    private TextView accType;
    private TextView nickName;
    private TextView balance;
    private TextView accNumber;
    private TextView balanceUpdate;


    //private RelativeLayout ccContainer;
    //private TextView ccAlias;
    //private TextView ccNumber;
    //private ImageView ccCardType;
    //private ImageView ccCardChip;


    public AccountViewHolder(View itemView) {
        super(itemView);

        container = (RelativeLayout) itemView.findViewById(R.id.list_item_credit_card_container);
        nickName = (TextView) itemView.findViewById(R.id.list_item_acc_nick_name);
        accNumber = (TextView) itemView.findViewById(R.id.list_item_account_number);
        balanceUpdate = (TextView) itemView.findViewById(R.id.list_item_balance_update_date);
        accType = (TextView) itemView.findViewById(R.id.list_item_account_type);
        balance = (TextView) itemView.findViewById(R.id.list_item_balance);


        //Mini CreditCard view
       // ccContainer = (RelativeLayout) itemView.findViewById(R.id.list_item_credit_card_cc_container);
        //ccAlias = (TextView) itemView.findViewById(R.id.list_item_credit_card_cc_alias);
        //ccNumber = (TextView) itemView.findViewById(R.id.list_item_credit_card_cc_number);
        //ccCardType = (ImageView) itemView.findViewById(R.id.list_item_credit_card_cc_type);
        //ccCardChip = (ImageView) itemView.findViewById(R.id.list_item_credit_card_cc_chip);
    }


    public void setData(Context context, Fragment fragment, Account current, int position) {
        mContext = context;
        mFragment = fragment;
        mCurrent = current;
        mPosition = position;


        nickName.setText(current.getNickName());
        accNumber.setText(current.getAccNumber());
        balanceUpdate.setText( current.getLongCardExpirationString());
        accType.setText(current.getAccountType().getFriendlyName());
        double roundOff = Math.round(current.getBalance()*100.0)/100.0;
        balance.setText(roundOff+" "+current.getCurrency().getCode());

        //Mini CC View
        //ccContainer.setBackground(current.getCreditCardBackground().getBackgroundDrawable(context));
       // ccAlias.setText(current.getNickName());
        //ccAlias.setTextColor(current.getCreditCardBackground().getTextColor(context));
        //ccNumber.setText(current.getAccNumber());
        //ccNumber.setTextColor(current.getCreditCardBackground().getTextColor(context));
    }

    public void setListeners() {
        container.setOnClickListener(this);
    }

    public void setOnCreditCardSelectedListener(AccountSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id) {
            case R.id.list_item_credit_card_container:
                if(mListener != null)
                    mListener.OnAccountSelected(mCurrent);
                break;
        }
    }


    public interface AccountSelectedListener {
        void OnAccountSelected(Account account);
    }

}

