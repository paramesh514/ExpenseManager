package ve.com.abicelis.creditcardexpensemanager.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.holders.SelectableCreditCardViewHolder;
import ve.com.abicelis.creditcardexpensemanager.app.utils.Constants;
import ve.com.abicelis.creditcardexpensemanager.app.utils.SharedPreferencesUtils;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.enums.AccountType;
import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardBackground;
import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardType;
import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotInsertDataException;
import ve.com.abicelis.creditcardexpensemanager.model.Account;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

/**
 * Created by Alex on 27/8/2016.
 */
public class AddAccountActivity extends AppCompatActivity {

    // Constants used to pass extra data in the intent
    public static final String CAME_FROM_WELCOME_ACTIVITY_INTENT = "CAME_FROM_WELCOME_ACTIVITY_INTENT";

    //TAGS
    private static final String CALENDAR_EXPIRATION_TAG = "1";

    //DATA
    boolean mCameFromWelcomeScreen = false;
    //Calendar cardExpirationCal = null;
    List<Currency> currencies;
    List<AccountType> accountTypes;
    List<Integer> days;
    List<CreditCard> mCreditCardList;
    CreditCardBackground selectedCreditCardBackground = null;

    //UI
    Toolbar toolbar;
    EditText AccountBankName;
    EditText AccountNickName;
    EditText AccountNumber;
    EditText AccountBalance;
    //EditText cardExpiration;
    Spinner cardCurrency;
    Spinner cardType;
    //Spinner cardClosingDay;
    //Spinner cardDueDay;
    Button buttonAddCreditCard;
    //RecyclerView mRecyclerView;
    //SelectableCreditCardAdapter mAdapter;
    LinearLayoutManager mLayoutManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        toolbar = (Toolbar) findViewById(R.id.add_acc_toolbar);
        AccountBankName = (EditText) findViewById(R.id.add_acc_edit_bank);
        AccountNickName = (EditText) findViewById(R.id.add_acc_edit_alias);
        AccountNumber = (EditText) findViewById(R.id.add_acc_edit_number);
        AccountBalance = (EditText) findViewById(R.id.add_acc_edit_balance);
        //cardExpiration = (EditText) findViewById(R.id.add_cc_edit_expiration);
        cardCurrency = (Spinner) findViewById(R.id.add_acc_spinner_currency);
        cardType = (Spinner) findViewById(R.id.add_acc_spinner_type);
        //cardClosingDay = (Spinner) findViewById(R.id.add_cc_edit_closing);
        //cardDueDay = (Spinner) findViewById(R.id.add_cc_edit_due);
        //mRecyclerView = (RecyclerView) findViewById(R.id.add_cc_recycler);
        buttonAddCreditCard = (Button) findViewById(R.id.add_acc_button_add_acc);
        buttonAddCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNewCardCreation();
            }
        });

        setUpToolbar();
        setUpPickers();
        setUpSpinners();
        setUpCCRecyclerView();
        setUpRecyclerUpdater();

        //Check if intent comes with mCameFromWelcomeScreen = true;
        mCameFromWelcomeScreen = getIntent().getBooleanExtra(CAME_FROM_WELCOME_ACTIVITY_INTENT, false);
    }


    private void setUpToolbar() {
        toolbar.setTitle(getResources().getString(R.string.activity_add_new_cc_title));
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the mToolbar's back/home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mCameFromWelcomeScreen) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.activity_add_new_cc_exit_dialog_title))
                    .setMessage(getResources().getString(R.string.activity_add_new_cc_exit_dialog_message))
                    .setPositiveButton(getResources().getString(R.string.activity_add_new_cc_exit_dialog_button_exit),  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.activity_add_new_cc_exit_dialog_button_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
        }
        else
            super.onBackPressed();
    }

    private void setUpPickers() {

    }

    private void setUpSpinners() {
        currencies = new ArrayList<>(Arrays.asList(Currency.values()));
        ArrayAdapter currencyAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, currencies);
        currencyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        cardCurrency.setAdapter(currencyAdapter);

        accountTypes = new ArrayList<>(Arrays.asList(AccountType.values()));
        ArrayAdapter cardTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, accountTypes);
        cardTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        cardType.setAdapter(cardTypeAdapter);

        days = new ArrayList<>();
        for(int i=1; i<=28;i++) {
            days.add(new Integer(i));
        }

        ArrayAdapter closingDayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, days);
        closingDayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        ArrayAdapter dueDayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, days);
        dueDayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
    }

    private void setUpCCRecyclerView() {

        mCreditCardList = CreditCard.getCreditCardBackgroundTypesList(this);
        SelectableCreditCardViewHolder.SelectableCreditCardSelectedListener ccSelectedListener = new SelectableCreditCardViewHolder.SelectableCreditCardSelectedListener() {
            @Override
            public void OnCreditCardSelected(CreditCard creditCard) {
                Toast.makeText(AddAccountActivity.this, "Background selected", Toast.LENGTH_SHORT).show();
                selectedCreditCardBackground = creditCard.getCreditCardBackground();
            }
        };


        mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

    }


    private void setUpRecyclerUpdater() {

        AccountBankName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                for (CreditCard c : mCreditCardList) {
                    //c.setBankName(AccountBankName.getText().toString());
                }
            }
        });

        AccountNickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                for (CreditCard c : mCreditCardList) {
                    //c.setCardAlias(AccountNickName.getText().toString());
                }
            }
        });

        AccountNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                for (CreditCard c : mCreditCardList) {
                    //c.setCardNumber(AccountNumber.getText().toString());
                }
            }
        });



        cardCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                for (CreditCard c : mCreditCardList) {
                    //c.setCurrency(currencies.get(cardCurrency.getSelectedItemPosition()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        cardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                for (CreditCard c : mCreditCardList) {
                    //c.setCardType(accountTypes.get(cardType.getSelectedItemPosition()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

    }


    private void handleNewCardCreation() {
        String alias = AccountNickName.getText().toString();
        if(alias.isEmpty()) {
            Toast.makeText(this, getResources().getString(R.string.activity_add_new_cc_error_bad_alias), Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            Double.parseDouble(AccountBalance.getText().toString());
        }catch (NumberFormatException e) {
            Toast.makeText(this, getResources().getString(R.string.activity_add_new_cc_error_bad_credit_limit), Toast.LENGTH_SHORT).show();
            return;
        }

      //  if(cardExpirationCal == null) {
      //      Toast.makeText(this, getResources().getString(R.string.activity_add_new_cc_error_bad_expiration), Toast.LENGTH_SHORT).show();
       //     return;
       // }


        String bankName = AccountBankName.getText().toString();
        String number = AccountNumber.getText().toString();
        BigDecimal firstCreditPeriodLimit = new BigDecimal(AccountBalance.getText().toString());
        firstCreditPeriodLimit = firstCreditPeriodLimit.setScale(2, BigDecimal.ROUND_DOWN);

        Currency currency = currencies.get(cardCurrency.getSelectedItemPosition());
        AccountType type = accountTypes.get(cardType.getSelectedItemPosition());


        ExpenseManagerDAO dao = new ExpenseManagerDAO(this);
        try {
            int creditCardId = (int) dao.insertAccount(new Account(alias, bankName, number, currency, type, Calendar.getInstance()));
            SharedPreferencesUtils.setInt(getApplicationContext(), Constants.ACTIVE_CC_ID,  creditCardId);

        }catch(CouldNotInsertDataException e) {
            Toast.makeText(this, "There was a problem inserting the credit card!", Toast.LENGTH_SHORT).show();
        }
        Intent goHomeIntent = new Intent(this, HomeActivity.class);
        startActivity(goHomeIntent);
        finish();
    }
}
