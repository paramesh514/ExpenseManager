package ve.com.abicelis.creditcardexpensemanager.app.dialogs;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.utils.NumberInputFilter;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.enums.AccountType;
import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotDeleteDataException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotUpdateDataException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditCardNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditPeriodNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.model.Account;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

/**
 * Created by Alex on 9/8/2016.
 */
public class EditOrDeleteAccountDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    //Constants
    private static final String TAG_ARGS_CREDIT_CARD = "TAG_ARGS_CREDIT_CARD";
    private static final String TAG_ARGS_CREDIT_LIMIT = "TAG_ARGS_CREDIT_LIMIT";

    //DB
    private ExpenseManagerDAO mDao;

    //DATA
    Account mAccount = null;
    //BigDecimal mOldCreditLimit = null;
    List<Currency> currencies;
    List<AccountType> accTypes;
    //Calendar cardExpirationCal =  Calendar.getInstance();


    //UI
    private DialogInterface.OnDismissListener mOnDismissListener;
    private EditText mNickName;
    private EditText mBankName;
    private EditText mAccNumber;
    private TextView mCreditLimitLabel;
    private EditText mAccBalance;
    //private EditText mCardExpiration;
    private Spinner mAccCurrency;
    private Spinner mAccType;

    private Button mDeleteButton;
    private Button mCancelButton;
    private Button mEditButton;

    //private RecyclerView mRecycler;

    public EditOrDeleteAccountDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static EditOrDeleteAccountDialogFragment newInstance(ExpenseManagerDAO dao, @NonNull Account creditCard) {
        EditOrDeleteAccountDialogFragment frag = new EditOrDeleteAccountDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(TAG_ARGS_CREDIT_CARD, creditCard);
        frag.setArguments(args);
        frag.setDao(dao);
        return frag;
    }

    public void setDao(ExpenseManagerDAO dao) {
        mDao = dao;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_or_delete_credit_card, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(R.string.dialog_edit_delete_cc_title);

        // Fetch arguments from bundle and set title
        mAccount = (Account) getArguments().getSerializable(TAG_ARGS_CREDIT_CARD);

        if(mAccount == null) {
            Toast.makeText(getActivity(), "Error, wrong arguments passed. Dismissing dialog.", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        // Get fields from view
        mBankName = (EditText) view.findViewById(R.id.dialog_edit_delete_acc_bank_name);
        mNickName = (EditText) view.findViewById(R.id.dialog_edit_delete_acc_nick_name);
        mAccNumber = (EditText) view.findViewById(R.id.dialog_edit_delete_acc_number);
        mCreditLimitLabel = (TextView) view.findViewById(R.id.dialog_edit_delete_cc_label_credit_limit);
        mAccBalance = (EditText) view.findViewById(R.id.dialog_edit_delete_acc_balance);
///        mCardExpiration = (EditText) view.findViewById(R.id.dialog_edit_delete_cc_card_expiration);
        mAccCurrency = (Spinner) view.findViewById(R.id.dialog_edit_delete_cc_currency);
        mAccType = (Spinner) view.findViewById(R.id.dialog_edit_delete_cc_card_type);
        //mRecycler = (RecyclerView) view.findViewById(R.id.dialog_edit_delete_cc_recycler);

        mDeleteButton = (Button) view.findViewById(R.id.dialog_edit_delete_cc_button_delete);
        mCancelButton = (Button) view.findViewById(R.id.dialog_edit_delete_cc_button_cancel);
        mEditButton = (Button) view.findViewById(R.id.dialog_edit_delete_cc_button_edit);

        //Limit mAccBalance
        //mAccBalance.setFilters(new InputFilter[]{new NumberInputFilter(9, 2)});

        setupListeners();
        setupSpinners();
        setupPickers();


        //Set original credit card values
        mBankName.setText(mAccount.getBankName());
        mNickName.setText(mAccount.getNickName());
        mAccNumber.setText(mAccount.getAccNumber());
        mAccCurrency.setSelection(currencies.indexOf(mAccount.getCurrency()));
        mAccType.setSelection(accTypes.indexOf(mAccount.getAccountType()));
        mAccBalance.setText(String.valueOf(mAccount.getBalance()));


        //SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd", Locale.getDefault());
        //cardExpirationCal.setTimeInMillis(mAccount.getBalanceUpdated().getTimeInMillis());
        //mCardExpiration.setText(formatter.format(cardExpirationCal.getTime()));
        //mCardExpiration.setText(mAccount.getLongCardExpirationString());

        // Show soft keyboard automatically and request focus to field
        //mAmountText.requestFocus();
        //getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }


    private void setupListeners() {
        mDeleteButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);
        mEditButton.setOnClickListener(this);
    }

    private void setupSpinners() {
        currencies = new ArrayList<>(Arrays.asList(Currency.values()));
        ArrayAdapter currencyAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mAccCurrency.setAdapter(currencyAdapter);

        accTypes = new ArrayList<>(Arrays.asList(AccountType.values()));
        ArrayAdapter cardTypeAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, accTypes);
        cardTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mAccType.setAdapter(cardTypeAdapter);

    }

    private void setupPickers() {
        /*mCardExpiration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {
                            @Override
                            public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                cardExpirationCal.set(year, monthOfYear, dayOfMonth);
                                mAccount.setBalanceUpdated(cardExpirationCal);
                                mCardExpiration.setText(mAccount.getLongCardExpirationString());
                            }
                        })
                        .setFirstDayOfWeek(Calendar.SUNDAY)
                        .setDoneText(getResources().getString(R.string.activity_add_new_cc_expiration_datepicker_button_select))
                        .setCancelText(getResources().getString(R.string.activity_add_new_cc_expiration_datepicker_button_cancel));
                cdp.show(getFragmentManager(), "CAL_TAG");
            }
        });*/
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id) {
            case R.id.dialog_edit_delete_cc_button_delete:
                handleCardDeletion();
                break;

            case R.id.dialog_edit_delete_cc_button_cancel:
                dismiss();
                break;
            case R.id.dialog_edit_delete_cc_button_edit:
                handleCardEdition();
                break;

        }

    }


    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }

    private void handleCardDeletion() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.dialog_edit_delete_cc_delete_title))
                .setMessage(getResources().getString(R.string.dialog_edit_delete_cc_delete_message))
                .setPositiveButton(getResources().getString(R.string.dialog_edit_delete_cc_button_delete),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            mDao.deleteCreditCard(mAccount.getId());
                            dismiss();
                        }catch (CouldNotDeleteDataException e) {
                            Toast.makeText(getActivity(), "Error deleting Credit Card", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialog_edit_delete_cc_button_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void handleCardEdition() {
        String alias = mNickName.getText().toString();
        if(alias.isEmpty()) {
            Toast.makeText(getActivity(), getResources().getString(R.string.activity_add_new_cc_error_bad_alias), Toast.LENGTH_SHORT).show();
            return;
        }

        String amount = mAccBalance.getText().toString();
        try{
            float fAmount = Float.parseFloat(amount);
            mAccount.setBalance(fAmount);
        }catch (NumberFormatException e) {
            amount = "";    //Force if below
        }
        if(amount.equals("") || new BigDecimal(amount).compareTo(new BigDecimal(0)) == 0) {
            Toast.makeText(getActivity(), getResources().getString(R.string.activity_add_new_cc_error_bad_credit_limit), Toast.LENGTH_SHORT).show();
            return;
        }

        /*if(cardExpirationCal == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.activity_add_new_cc_error_bad_expiration), Toast.LENGTH_SHORT).show();
            return;
        }*/

//        int closing = days.get(cardClosingDay.getSelectedItemPosition());
//        int due = days.get(cardDueDay.getSelectedItemPosition());
//        if(closing == due) {
//            Toast.makeText(this, getResources().getString(R.string.activity_add_new_cc_error_bad_closing_due_days), Toast.LENGTH_SHORT).show();
//            return;
//        }

//        if(selectedCreditCardBackground == null) {
//            Toast.makeText(this, getResources().getString(R.string.activity_add_new_cc_error_cc_background_not_selected), Toast.LENGTH_SHORT).show();
//            return;
//        }


        mAccount.setBankName(mBankName.getText().toString());
        mAccount.setNickName(alias);
        mAccount.setAccNumber(mAccNumber.getText().toString());
        mAccount.setBalanceUpdated(Calendar.getInstance());
        mAccount.setCurrency(currencies.get(mAccCurrency.getSelectedItemPosition()));
        mAccount.setAccountType(accTypes.get(mAccType.getSelectedItemPosition()));


        try {
            mDao.updateAccount(mAccount);

            /*//Check if CreditCard has a current creditPeriod
            try {
                CreditCard c = mDao.getCreditCardWithCreditPeriod(mAccount.getId(), 0);
                CreditPeriod newCreditPeriod = c.getCreditPeriods().get(0);

                //Get new creditLimit from dialog
                BigDecimal newCreditLimit = new BigDecimal(amount);
                newCreditLimit = newCreditLimit.setScale(2, BigDecimal.ROUND_DOWN);

                //Set creditLimit to creditPeriod
                newCreditPeriod.setCreditLimit(newCreditLimit);
                mDao.updateCreditPeriod(mAccount.getId(), newCreditPeriod);

            } catch (CreditCardNotFoundException | CreditPeriodNotFoundException e) {

                //Credit card has no current credit period, create one.
                try {
                    mDao.insertCurrentCreditPeriod(mAccount.getId(), mAccount.getClosingDay(), new BigDecimal(amount));
                }catch (CouldNotInsertDataException e1) {
                    Toast.makeText(getActivity(), "Could not create Credit Period", Toast.LENGTH_SHORT).show();
                }
            }*/
        }catch (CouldNotUpdateDataException e) {
            Toast.makeText(getActivity(), "Error updating Account", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(getActivity(), "Account successfully updated", Toast.LENGTH_SHORT).show();
        dismiss();
    }


}
