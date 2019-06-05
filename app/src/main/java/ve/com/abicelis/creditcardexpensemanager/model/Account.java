package ve.com.abicelis.creditcardexpensemanager.model;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.enums.AccountType;
import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardBackground;
import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardType;
import ve.com.abicelis.creditcardexpensemanager.enums.Currency;

/**
 * Created by Alex on 6/8/2016.
 */
public class Account implements Serializable {

    private int id;
    private String nickName;
    private String bankName;
    private String accNumber;
    private float balance;
    private Currency currency;
    private AccountType accountType;
    private Calendar balanceUpdated;
    private boolean isActive;
    private List<Account> linkedCards;
    //private int closingDay;         // Fecha de corte: Fecha de cierre de operaciones del mes, para efectos de registros y cobros.
    //private int dueDay;             // Fecha Limite de Pago: Fecha máxima para el próximo pago sin generar incumplimiento e intereses de mora.
    //private CreditCardBackground creditCardBackground;


   // private Map<Integer, CreditPeriod> creditPeriods;

    public Account(String nickName, String bankName, String accNumber, @NonNull Currency currency, @NonNull AccountType accountType, @NonNull Calendar balanceUpdated) {
        this.nickName = nickName;
        this.bankName = bankName;
        this.accNumber = accNumber;
        this.currency = currency;
        this.accountType = accountType;

        this.balanceUpdated = Calendar.getInstance();
        this.balanceUpdated.setTimeZone(balanceUpdated.getTimeZone());
        this.balanceUpdated.setTimeInMillis(balanceUpdated.getTimeInMillis());

     //   this.dueDay = dueDay;
       // this.closingDay = closingDay;
        //this.creditCardBackground = creditCardBackground;
    }

    public Account(int id, String nickName, String bankName, String accNumber, @NonNull Currency currency, @NonNull AccountType accountType, @NonNull Calendar balanceUpdated) {
        this(nickName, bankName, accNumber, currency, accountType, balanceUpdated);
        this.id = id;
    }

    public Account(int id, String nickName, String bankName, String accNumber, @NonNull Currency currency, @NonNull AccountType accountType, @NonNull Calendar balanceUpdated, List<Account> linkedAccounts) {
        this(id, nickName, bankName, accNumber, currency, accountType, balanceUpdated);
        this.linkedCards = linkedAccounts;
    }



    public int getId() {
        return id;
    }


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Calendar getBalanceUpdated() {
        return balanceUpdated;
    }

    public void setBalanceUpdated(Calendar balanceUpdated) {
        this.balanceUpdated = Calendar.getInstance();
        this.balanceUpdated.setTimeZone(balanceUpdated.getTimeZone());
        this.balanceUpdated.setTimeInMillis(balanceUpdated.getTimeInMillis());
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<Account> getLinkedCards() {
        return linkedCards;
    }

    public void setLinkedCards(List<Account> linkedCards) {
        this.linkedCards = linkedCards;
    }

    @Override
    public String toString() {
        return  "ID=" + id + "\r\n" +
                " nickName=" + nickName + "\r\n" +
                " accNumber=" + accNumber + "\r\n" +
                " currency=" + currency + "\r\n" +
                " accountType=" + accountType + "\r\n" +
                " balance="+balance+"\r\n"+
                " balanceUpdated=" + balanceUpdated + "\r\n" +
                " isAcitve="+isActive+"\r\n"+
                " creditPeriods=" + linkedCards;
    }
}
