package ve.com.abicelis.creditcardexpensemanager.app.holders;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

/**
 * Created by Alex on 30/8/2016.
 */
public class CreditCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context mContext;
    private Fragment mFragment;
    CreditCardSelectedListener mListener = null;


    //DATA
    private CreditCard mCurrent;
    private int mPosition;

    //UI
    private RelativeLayout container;
    private TextView bankName;
    private TextView alias;
    private TextView currency;
    private TextView cardNumber;
    private TextView cardExpiration;


    //private RelativeLayout ccContainer;
    //private TextView ccAlias;
    //private TextView ccNumber;
    //private ImageView ccCardType;
    //private ImageView ccCardChip;


    public CreditCardViewHolder(View itemView) {
        super(itemView);

        container = (RelativeLayout) itemView.findViewById(R.id.list_item_credit_card_container);
        alias = (TextView) itemView.findViewById(R.id.list_item_acc_nick_name);
        cardNumber = (TextView) itemView.findViewById(R.id.list_item_account_number);
        cardExpiration = (TextView) itemView.findViewById(R.id.list_item_balance_update_date);
        bankName = (TextView) itemView.findViewById(R.id.list_item_account_type);
        currency = (TextView) itemView.findViewById(R.id.list_item_balance);


        //Mini CreditCard view
        //ccContainer = (RelativeLayout) itemView.findViewById(R.id.list_item_credit_card_cc_container);
        //ccAlias = (TextView) itemView.findViewById(R.id.list_item_credit_card_cc_alias);
        //ccNumber = (TextView) itemView.findViewById(R.id.list_item_credit_card_cc_number);
        //ccCardType = (ImageView) itemView.findViewById(R.id.list_item_credit_card_cc_type);
        //ccCardChip = (ImageView) itemView.findViewById(R.id.list_item_credit_card_cc_chip);
    }


    public void setData(Context context, Fragment fragment, CreditCard current, int position) {
        mContext = context;
        mFragment = fragment;
        mCurrent = current;
        mPosition = position;


        alias.setText(current.getCardAlias());
        cardNumber.setText(current.getCardNumber());
        cardExpiration.setText("EXP " + current.getShortCardExpirationString());
        bankName.setText(current.getBankName());
        currency.setText(current.getCurrency().getCode());

        //Mini CC View
      //  ccContainer.setBackground(current.getCreditCardBackground().getBackgroundDrawable(context));
        //ccAlias.setText(current.getCardAlias());
        //ccAlias.setTextColor(current.getCreditCardBackground().getTextColor(context));
        ///ccNumber.setText(current.getCardNumber());
        //ccNumber.setTextColor(current.getCreditCardBackground().getTextColor(context));



    }

    public void setListeners() {
        container.setOnClickListener(this);
    }

    public void setOnCreditCardSelectedListener(CreditCardSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id) {
            case R.id.list_item_credit_card_container:
                if(mListener != null)
                    mListener.OnCreditCardSelected(mCurrent);
                break;
        }
    }


    public interface CreditCardSelectedListener {
        void OnCreditCardSelected(CreditCard creditCard);
    }

}

