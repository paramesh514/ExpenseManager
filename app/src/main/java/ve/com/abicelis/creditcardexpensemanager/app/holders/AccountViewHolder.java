package ve.com.abicelis.creditcardexpensemanager.app.holders;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.model.Account;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

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
    private TextView bankName;
    private TextView alias;
    private TextView currency;
    private TextView cardNumber;
    private TextView cardExpiration;


    private RelativeLayout ccContainer;
    private TextView ccAlias;
    private TextView ccNumber;
    private ImageView ccCardType;
    private ImageView ccCardChip;


    public AccountViewHolder(View itemView) {
        super(itemView);

        container = (RelativeLayout) itemView.findViewById(R.id.list_item_credit_card_container);
        alias = (TextView) itemView.findViewById(R.id.list_item_credit_card_alias);
        cardNumber = (TextView) itemView.findViewById(R.id.list_item_credit_card_number);
        cardExpiration = (TextView) itemView.findViewById(R.id.list_item_credit_card_expiration);
        bankName = (TextView) itemView.findViewById(R.id.list_item_credit_card_bank_name);
        currency = (TextView) itemView.findViewById(R.id.list_item_credit_card_currency);


        //Mini CreditCard view
        ccContainer = (RelativeLayout) itemView.findViewById(R.id.list_item_credit_card_cc_container);
        ccAlias = (TextView) itemView.findViewById(R.id.list_item_credit_card_cc_alias);
        ccNumber = (TextView) itemView.findViewById(R.id.list_item_credit_card_cc_number);
        ccCardType = (ImageView) itemView.findViewById(R.id.list_item_credit_card_cc_type);
        ccCardChip = (ImageView) itemView.findViewById(R.id.list_item_credit_card_cc_chip);
    }


    public void setData(Context context, Fragment fragment, Account current, int position) {
        mContext = context;
        mFragment = fragment;
        mCurrent = current;
        mPosition = position;


        alias.setText(current.getNickName());
        cardNumber.setText(current.getAccNumber());
        cardExpiration.setText("EXP " + current.getShortCardExpirationString());
        bankName.setText(current.getBankName());
        currency.setText(current.getCurrency().getCode());

        //Mini CC View
        //ccContainer.setBackground(current.getCreditCardBackground().getBackgroundDrawable(context));
        ccAlias.setText(current.getNickName());
        //ccAlias.setTextColor(current.getCreditCardBackground().getTextColor(context));
        ccNumber.setText(current.getAccNumber());
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

