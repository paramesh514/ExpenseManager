package ve.com.abicelis.creditcardexpensemanager.app.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

/**
 * Created by Alex on 30/8/2016.
 */
public class SelectableCreditCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context mContext;
    SelectableCreditCardSelectedListener mListener = null;

    //DATA
    private CreditCard mCurrent;
    private int mPosition;

    //UI
    private RelativeLayout container;
    private TextView bankName;
    private TextView nickName;
    private TextView balance;
    private TextView accNumber;
//    private TextView //creditCardLabel;
 //   private TextView cardExpirationLabel;
    private TextView cardExpiration;
    private ImageView cardType;
//    private ImageView cardChip;


    public SelectableCreditCardViewHolder(View itemView) {
        super(itemView);

        container = (RelativeLayout) itemView.findViewById(R.id.list_item_credit_card_container);
        bankName = (TextView) itemView.findViewById(R.id.list_item_account_type);
        nickName = (TextView) itemView.findViewById(R.id.list_item_acc_nick_name);
        balance = (TextView) itemView.findViewById(R.id.list_item_balance);
        accNumber = (TextView) itemView.findViewById(R.id.list_item_account_number);
        //creditCardLabel = (TextView) itemView.findViewById(R.id.list_item_credit_card_label);
        //cardExpirationLabel = (TextView) itemView.findViewById(R.id.list_item_credit_card_expiration_label);
        cardExpiration = (TextView) itemView.findViewById(R.id.list_item_balance_update_date);
        cardType = (ImageView) itemView.findViewById(R.id.list_item_credit_card_type);
        //cardChip = (ImageView) itemView.findViewById(R.id.list_item_credit_card_chip);

        //LAYOUT_DETAILED views


    }


    public void setData(Context context, CreditCard current, int position) {
        mContext = context;
        mCurrent = current;
        mPosition = position;

        bankName.setText(current.getBankName());
        nickName.setText(current.getCardAlias());
        accNumber.setText(current.getCardNumber());
        balance.setText(current.getCurrency().getCode());
        cardExpiration.setText(current.getShortCardExpirationString());

        container.setBackground(current.getCreditCardBackground().getBackgroundDrawable(context));
        bankName.setTextColor(current.getCreditCardBackground().getTextColor(context));
        nickName.setTextColor(current.getCreditCardBackground().getTextColor(context));
        balance.setTextColor(current.getCreditCardBackground().getTextColor(context));
        accNumber.setTextColor(current.getCreditCardBackground().getTextColor(context));
        cardExpiration.setTextColor(current.getCreditCardBackground().getTextColor(context));
        //creditCardLabel.setTextColor(current.getCreditCardBackground().getTextColor(context));
        //cardExpirationLabel.setTextColor(current.getCreditCardBackground().getTextColor(context));



    }

    public void setListeners() {
        container.setOnClickListener(this);
    }

    public void setOnCreditCardSelectedListener(SelectableCreditCardSelectedListener listener) {
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


    public interface SelectableCreditCardSelectedListener {
        void OnCreditCardSelected(CreditCard creditCard);
    }

}
