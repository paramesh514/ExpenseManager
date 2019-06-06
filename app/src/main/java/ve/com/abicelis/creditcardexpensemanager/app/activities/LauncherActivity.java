package ve.com.abicelis.creditcardexpensemanager.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.Calendar;

import ve.com.abicelis.creditcardexpensemanager.app.utils.Constants;
import ve.com.abicelis.creditcardexpensemanager.app.utils.SharedPreferencesUtils;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.enums.AccountType;
import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.exceptions.AccountNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotInsertDataException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditCardNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditPeriodNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.SharedPreferenceNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.model.Account;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;

/**
 * Created by Alex on 26/8/2016.
 */
public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ExpenseManagerDAO mDao = new ExpenseManagerDAO(getApplicationContext());

        //Check if ACTIVE_CC_ID exists and it is valid
        try {
            int activeCreditCardId = SharedPreferencesUtils.getInt(getApplicationContext(), Constants.ACTIVE_CC_ID);
            mDao.getAccount(activeCreditCardId);
        }catch(SharedPreferenceNotFoundException | AccountNotFoundException e) {

            //if there's at least one CreditCard in database, set it's id to ACTIVE_CC_ID
            if(mDao.getCreditCardList().size() > 0) {
                SharedPreferencesUtils.setInt(getApplicationContext(), Constants.ACTIVE_CC_ID,  mDao.getAccountList().get(0).getId());
            } else {
                //There are no credit cards in the system, send to welcomeActivity
                Account cashAcc = new Account("Cash","CashSpend","0", Currency.INR, AccountType.Cash, Calendar.getInstance());
                try {
                    SharedPreferencesUtils.setInt(getApplicationContext(), Constants.ACTIVE_CC_ID, (int)mDao.insertAccount(cashAcc));
                }
                catch(CouldNotInsertDataException ex)
                {

                }
                //finish();
                //return;
            }
        }

        //All is good, go home!
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

}
