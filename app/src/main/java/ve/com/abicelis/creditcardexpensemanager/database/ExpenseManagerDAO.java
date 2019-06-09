package ve.com.abicelis.creditcardexpensemanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ve.com.abicelis.creditcardexpensemanager.enums.AccountType;
import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardBackground;
import ve.com.abicelis.creditcardexpensemanager.enums.CreditCardType;
import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.enums.TransactionType;
import ve.com.abicelis.creditcardexpensemanager.exceptions.AccountNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotDeleteDataException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotUpdateDataException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditCardNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditPeriodNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotGetDataException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotInsertDataException;
import ve.com.abicelis.creditcardexpensemanager.model.Account;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;
import ve.com.abicelis.creditcardexpensemanager.model.CreditPeriod;
//import ve.com.abicelis.creditcardexpensemanager.model.Expense;
import ve.com.abicelis.creditcardexpensemanager.model.DailyExpense;
import ve.com.abicelis.creditcardexpensemanager.model.Payment;
import ve.com.abicelis.creditcardexpensemanager.model.Transaction;
import ve.com.abicelis.creditcardexpensemanager.model.TransactionCategory;

/**
 * Created by Alex on 8/8/2016.
 */
public class ExpenseManagerDAO {

    private ExpenseManagerDbHelper mDatabaseHelper;

    public ExpenseManagerDAO(Context context) {
        mDatabaseHelper = new ExpenseManagerDbHelper(context);

    }

    /**
     * Returns a List of stored Credit Cards.
     * Note: The Credit Cards will not contain CreditPeriods, Expenses or Payments.
     */
    public List<CreditCard> getCreditCardList() {

        List<CreditCard> creditCards = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(ExpenseManagerContract.CreditCardTable.TABLE_NAME, null, null, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                creditCards.add(getCreditCardFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return creditCards;
    }

    /**
     * Returns a List of stored Accounts.
     * Note: The Credit Cards will not contain CreditPeriods, Expenses or Payments.
     */
    public List<Account> getAccountList() {

        List<Account> accounts = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
String where = ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_TYPE.getName() +" <> '"+AccountType.Merchant.getCode()+"'";
        Cursor cursor = db.query(ExpenseManagerContract.AccountTable.TABLE_NAME, null, where, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                accounts.add(getAccountFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }
        for(Account acc:accounts)
        {
            double income,expense,transferIn,transferOut,corrections;
            income = getTotalForAccount(acc.getId(),TransactionType.INCOME);
            transferIn = getTransferInForAccount(acc.getId());
            expense = getTotalForAccount(acc.getId(),TransactionType.EXPENSE);
            transferOut = getTotalForAccount(acc.getId(),TransactionType.TRANSFER);
            corrections = getTotalForAccount(acc.getId(),TransactionType.CORRECTION);
            acc.setBalance(income-expense+transferIn-transferOut+corrections);
        }

        return accounts;
    }
    public List<Account> getAccountListWithMerchants() {

        List<Account> accounts = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(ExpenseManagerContract.AccountTable.TABLE_NAME, null, null, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                accounts.add(getAccountFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }
        for(Account acc:accounts)
        {
            double income,expense,transferIn,transferOut,corrections;
            income = getTotalForAccount(acc.getId(),TransactionType.INCOME);
            transferIn = getTransferInForAccount(acc.getId());
            expense = getTotalForAccount(acc.getId(),TransactionType.EXPENSE);
            transferOut = getTotalForAccount(acc.getId(),TransactionType.TRANSFER);
            corrections = getTotalForAccount(acc.getId(),TransactionType.CORRECTION);
            acc.setBalance(income-expense+transferIn-transferOut+corrections);
        }

        return accounts;
    }

    private double getTransferInForAccount(int id) {

        double totalAmount=0.0;
        String whereClause = ExpenseManagerContract.TransactionTable.COLUMN_NAME_FOREIGN_KEY_RECIEVER.getName() +
                " = " + id ;
        String[] tableColumns = new String[] {
                "sum("+ ExpenseManagerContract.TransactionTable.COLUMN_NAME_AMOUNT.getName() +") As total"
        };
        // String whereClause = " strftime('%m',"+ ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName() +
        //       ") = strftime('%m',CURRENT_DATE) AND strftime('%Y',"+ ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName() +
        //     ") = strftime('%Y',CURRENT_DATE) "+

        whereClause += " AND " +ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_TYPE.getName() +
                    " = '" + TransactionType.TRANSFER.getCode() +"'";


        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(ExpenseManagerContract.TransactionTable.TABLE_NAME, tableColumns, whereClause, null, null, null,null);

        try {
            while (cursor.moveToNext()) {
                totalAmount = new Double(cursor.getString(cursor.getColumnIndex("total")));

            }
        }
        catch (Exception e)
        {

        }
        finally {
            cursor.close();
        }

        return totalAmount;
    }

    private double getTotalForAccount(int id, TransactionType type) {

        double totalAmount=0.0;
        String whereClause = ExpenseManagerContract.TransactionTable.COLUMN_NAME_FOREIGN_KEY_GIVER.getName() +" = " + id;
        String[] tableColumns = new String[] {
                "sum("+ ExpenseManagerContract.TransactionTable.COLUMN_NAME_AMOUNT.getName() +") As total"
        };
        // String whereClause = " strftime('%m',"+ ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName() +
        //       ") = strftime('%m',CURRENT_DATE) AND strftime('%Y',"+ ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName() +
        //     ") = strftime('%Y',CURRENT_DATE) "+

        whereClause += " AND " +ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_TYPE.getName() +
                    " = '" +  type.getCode() +"'";

        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(ExpenseManagerContract.TransactionTable.TABLE_NAME, tableColumns, whereClause, null, null, null,null);

        try {
            while (cursor.moveToNext()) {
                totalAmount = new Double(cursor.getString(cursor.getColumnIndex("total")));

            }
        }
        catch (Exception e)
        {

        }
        finally {
            cursor.close();
        }

        return totalAmount;
    }
    private List<Transaction> getTransactionInPeroid(Calendar start, Calendar end) {
        List<Transaction> accounts = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        long startDateInMs = start.getTimeInMillis();
        long endDateInMs = end.getTimeInMillis();

        String where = ExpenseManagerContract.TransactionTable.COLUMN_NAME_DATE.getName()+" < "+endDateInMs+" AND "+
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_DATE.getName()+" > "+startDateInMs;

        Cursor cursor = db.query(ExpenseManagerContract.TransactionTable.TABLE_NAME, null, where, null, null, null,
                null);

        try {
            while (cursor.moveToNext()) {
                accounts.add(getTransactionFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return accounts;

    }
    public List<Transaction> getTransactionList() {

        List<Transaction> accounts = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(ExpenseManagerContract.TransactionTable.TABLE_NAME, null, null, null, null, null,
                ExpenseManagerContract.TransactionTable._ID+" DESC");

        try {
            while (cursor.moveToNext()) {
                accounts.add(getTransactionFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return accounts;
    }
    public List<Transaction> getTransactionList(TransactionType type) {

        List<Transaction> accounts = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        String where = ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_TYPE.getName() +
        " = '" +  type.getCode() +"'";
        Cursor cursor = db.query(ExpenseManagerContract.TransactionTable.TABLE_NAME, null, where, null, null, null,
                ExpenseManagerContract.TransactionTable._ID+" DESC");

        try {
            while (cursor.moveToNext()) {
                accounts.add(getTransactionFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return accounts;
    }
    public double getBudgetSpendForCurrentMonth(TransactionType ty, int tid) {

        Calendar start = Calendar.getInstance();
        start.set(Calendar.DATE,1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = Calendar.getInstance();

        return getBudgetSpendForMonth(ty,tid,start,end);

    }
        public double getBudgetSpendForMonth(TransactionType ty, int tid,Calendar start,Calendar end) {

        double totalAmount=0.0;
        long startDateInMs = start.getTimeInMillis();
        long endDateInMs = end.getTimeInMillis();

        String whereTime = ExpenseManagerContract.TransactionTable.COLUMN_NAME_DATE.getName()+" < "+endDateInMs+" AND "+
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_DATE.getName()+" > "+startDateInMs;


        String whereClause = ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_TYPE.getName() +
                " = '" + ty.getCode() + "' ";
        whereClause+=" AND "+whereTime;

        String[] tableColumns = new String[] {
                "sum("+ ExpenseManagerContract.TransactionTable.COLUMN_NAME_AMOUNT.getName() +") As total"
        };
       // String whereClause = " strftime('%m',"+ ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName() +
         //       ") = strftime('%m',CURRENT_DATE) AND strftime('%Y',"+ ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName() +
           //     ") = strftime('%Y',CURRENT_DATE) "+
        if(tid != 0) {
             whereClause += " AND " +ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_CATEGORY.getName() +
                    " = '" + tid +"'";
        }

        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor = db.query(ExpenseManagerContract.TransactionTable.TABLE_NAME, tableColumns, whereClause, null, null, null,null);

        try {
            while (cursor.moveToNext()) {
                totalAmount = new Double(cursor.getString(cursor.getColumnIndex("total")));

            }
        }
        catch (Exception e)
        {

        }
        finally {
            cursor.close();
        }

        return totalAmount;
    }

    public List<TransactionCategory> getTransactionCategoryList() {

        List<TransactionCategory> transactionCategories = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        String where = ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRASACTION_TYPE.getName()+" <> '"+
                TransactionType.CORRECTION.getCode()+"' AND "+ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_DESCRIPTION.getName()+
                " <> 'UnCategorized'";
        Cursor cursor = db.query(ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME, null, where, null, null, null,
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRASACTION_TYPE.getName()+","+
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_DESCRIPTION.getName());

        try {
            while (cursor.moveToNext()) {
                transactionCategories.add(getTransactionCategoryFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }
        double totalBudget=0.0;
        double budgetAll=0.0;
        for(TransactionCategory tc:transactionCategories)
        {
            if(tc.getId() != 0)
            {
                totalBudget+=tc.getBudget();
            }
            else
                budgetAll =tc.getBudget();

            tc.setSpent(getBudgetSpendForCurrentMonth(tc.getType(),tc.getId()));
        }
        if(budgetAll == 0.0) {
            for (TransactionCategory tc : transactionCategories) {
                if (tc.getId() == 0) {
                    if(totalBudget > 0.0)
                        tc.setBudget(totalBudget);
                    else
                        tc.setBudget(tc.getSpent());
                }
            }
        }
        return transactionCategories;
    }

    public List<TransactionCategory> getTransactionCategoryList(TransactionType type) {

        List<TransactionCategory> transactionCategories = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        String whereCondtion = ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRASACTION_TYPE.getName()+
                " = '"+type.getCode()+"' and _id!=0 ";
        Cursor cursor = db.query(ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME, null, whereCondtion, null, null, null,
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_DESCRIPTION.getName()+" DESC");

        try {
            while (cursor.moveToNext()) {
                transactionCategories.add(getTransactionCategoryFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        for(TransactionCategory tc:transactionCategories)
        {
            tc.setSpent(getBudgetSpendForCurrentMonth(tc.getType(),tc.getId()));
        }
        return transactionCategories;
    }

    /**
     * Returns a List of CreditPeriods associated with ccardId.
     * Note: The periods will not contain Expenses or Payments.
     * @param cardId The ID of the Credit Card
     */
    public List<CreditPeriod> getCreditPeriodListFromCard(int cardId) {
        List<CreditPeriod> creditPeriods = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor =  db.query(ExpenseManagerContract.CreditPeriodTable.TABLE_NAME, null,
                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD.getName() + " == " + cardId, null, null, null,
                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName() + " DESC");

        try {
            while (cursor.moveToNext()) {
                creditPeriods.add(getCreditPeriodFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return creditPeriods;
    }



    /**
     * Finds a CreditCard with the supplied cardId, will not return creditPeriods associated with
     * the card
     * @param cardId The ID of the Credit Card
     */
    public CreditCard getCreditCard(int cardId) throws CreditCardNotFoundException {
        String[] selectionArgs = new String[]{String.valueOf(cardId)};
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor =  db.query(ExpenseManagerContract.CreditCardTable.TABLE_NAME, null,
                ExpenseManagerContract.CreditCardTable._ID + " =? ", selectionArgs, null, null, null);

        if(cursor.getCount() == 0)
            throw new CreditCardNotFoundException("Specified Credit Card not found in the database. Passed value=" + cardId);
        if(cursor.getCount() > 1)
            throw new CreditCardNotFoundException("Database UNIQUE constraint failure, more than one record found. Passed value=" + cardId);

        cursor.moveToNext();
        return getCreditCardFromCursor(cursor);
    }

    /**
     * Finds a CreditCard with the supplied cardId, will not return creditPeriods associated with
     * the card
     * @param accId The ID of the Account
     */
    public Account getAccount(int accId) throws AccountNotFoundException {
        String[] selectionArgs = new String[]{String.valueOf(accId)};
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor =  db.query(ExpenseManagerContract.AccountTable.TABLE_NAME, null,
                ExpenseManagerContract.AccountTable._ID + " =? ", selectionArgs, null, null, null);

        if(cursor.getCount() == 0)
            throw new AccountNotFoundException("Specified Credit Card not found in the database. Passed value=" + accId);
        if(cursor.getCount() > 1)
            throw new AccountNotFoundException("Database UNIQUE constraint failure, more than one record found. Passed value=" + accId);

        cursor.moveToNext();
        Account acc = getAccountFromCursor(cursor);

        double income,expense,transferIn,transferOut,corrections;
        income = getTotalForAccount(acc.getId(),TransactionType.INCOME);
        transferIn = getTransferInForAccount(acc.getId());
        expense = getTotalForAccount(acc.getId(),TransactionType.EXPENSE);
        transferOut = getTotalForAccount(acc.getId(),TransactionType.TRANSFER);
        corrections = getTotalForAccount(acc.getId(),TransactionType.CORRECTION);
        acc.setBalance(income-expense+transferIn-transferOut+corrections);

        return acc;
    }

    public TransactionCategory getTransactionCategory(int catId) {
        String[] selectionArgs = new String[]{String.valueOf(catId)};
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor =  db.query(ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME, null,
                ExpenseManagerContract.TransactionCategoryTable._ID + " =? ", selectionArgs, null, null, null);

        if(cursor.getCount() == 0)
            return TransactionCategory.getDefault();
        if(cursor.getCount() > 1)
            return TransactionCategory.getDefault();

        cursor.moveToNext();
        TransactionCategory tc = getTransactionCategoryFromCursor(cursor);
        cursor.close();
        tc.setSpent(getBudgetSpendForCurrentMonth(tc.getType(),tc.getId()));
        if(tc.getBudget()==0&&tc.getId()==0)
        {
            tc.setBudget(tc.getSpent());
        }
        return  tc;
    }

    public TransactionCategory getTransactionCategory(String catId) {
        String[] selectionArgs = new String[]{String.valueOf(catId)};
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor =  db.query(ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME, null,
                ExpenseManagerContract.TransactionCategoryTable._ID + " =? ", selectionArgs, null, null, null);

        if(cursor.getCount() == 0)
            return TransactionCategory.getDefault();
        if(cursor.getCount() > 1)
            return TransactionCategory.getDefault();

        cursor.moveToNext();
        return getTransactionCategoryFromCursor(cursor);
    }


    /**
     * Returns a Credit Card with the supplied cardId and the specified periodIndex.
     * Note: PeriodIndex must be either zero (returns credit period contaning current date) or a
     * negative value (-1 returns previous credit period, and so on)
     * @param cardId The ID of the Credit Card
     * @param periodIndex An integer <= 0
     */
    public CreditCard getCreditCardWithCreditPeriod(int cardId, int periodIndex) throws CreditCardNotFoundException, CreditPeriodNotFoundException, InvalidParameterException{
        if(periodIndex > 0)
            throw new InvalidParameterException("PeriodIndex must be >=0. Passed value=" + periodIndex);

        CreditCard creditCard = getCreditCard(cardId);

        //Get ordered (DESC) list of credit periods
        List<CreditPeriod> creditPeriods = getCreditPeriodListFromCard(cardId);

        //Find the creditPeriod according to periodIndex
        CreditPeriod cp = getCreditPeriodFromPeriodIndex(creditPeriods, periodIndex, creditCard.getClosingDay());

        //Create Map and set it on the Credit Card
        Map<Integer, CreditPeriod> creditPeriodMap = new HashMap<>();
        creditPeriodMap.put(periodIndex, cp);
        creditCard.setCreditPeriods(creditPeriodMap);

        return creditCard;
    }


    /**
     * Returns a Credit Card with the supplied cardId and as many credit periods as numPeriods
     * indicates. Counting backwards starting on periodIndex.
     * Note: PeriodIndex must be either zero (current creditPriod) or a
     * negative value (-1 refers to the previous credit period, and so on)
     * Note2: Passing periodIndex=-1, numPeriods=3 will return three periods (Period -1, -2 and -3)
     * @param cardId The ID of the Credit Card
     * @param periodIndex An integer <= 0
     * @param numPeriods An integer >= 1
     */
    public CreditCard getCreditCardWithCreditPeriodRange(int cardId, int periodIndex, int numPeriods) throws CreditCardNotFoundException, CreditPeriodNotFoundException, InvalidParameterException {
        if (periodIndex > 0)
            throw new InvalidParameterException("PeriodIndex must be less or equal than 0. Passed value=" + periodIndex);
        if (numPeriods <= 0)
            throw new InvalidParameterException("NumPeriods must be more or equal than 1. Passed value=" + periodIndex);

        CreditCard creditCard = getCreditCard(cardId);

        //Get ordered (DESC) list of credit periods
        List<CreditPeriod> creditPeriods = getCreditPeriodListFromCard(cardId);

        Map<Integer, CreditPeriod> creditPeriodMap = new HashMap<>();

        //Find the creditPeriod according to periodIndex
        for (int i = periodIndex; i > (periodIndex-numPeriods); --i) {
            CreditPeriod cp = getCreditPeriodFromPeriodIndex(creditPeriods, i, creditCard.getClosingDay());
            creditPeriodMap.put(i, cp);
        }

        //Set creditPeriods in creditCard and return
        creditCard.setCreditPeriods(creditPeriodMap);
        return creditCard;
    }


    /**
     * Returns a Credit Period from a List of creditPeriods which complies with the supplied
     * periodIndex and closingDay.
     * Note: PeriodIndex must be either zero (returns credit period contaning current date) or a
     * negative value (-1 returns previous credit period, and so on)
     * Note2: closingDay must be between 1 and 28.
     * @param creditPeriods List of Credit Periods to search into
     * @param periodIndex An integer <= 0
     * @param closingDay An integer between 1 and 28
     */
    private CreditPeriod getCreditPeriodFromPeriodIndex(List<CreditPeriod> creditPeriods, int periodIndex, int closingDay) throws CreditPeriodNotFoundException, InvalidParameterException {
        if(closingDay < 1 || closingDay > 28)
            throw new InvalidParameterException("ClosingDay must be between 1 and 28. Passed value=" + closingDay);
        if(periodIndex > 0)
            throw new InvalidParameterException("PeriodIndex must be >=0. Passed value=" + periodIndex);
        if(creditPeriods == null || creditPeriods.size() == 0)
            throw new InvalidParameterException("creditPeriods is null or empty");

        //Get a day which will be between the starting and ending dates of the wanted creditPeriod
        Calendar day = Calendar.getInstance();
        if(periodIndex != 0)
            day.add(Calendar.MONTH, periodIndex);

        for (CreditPeriod cp : creditPeriods) {
            if(cp.getStartDate().compareTo(day) <=0 && cp.getEndDate().compareTo(day) >=0) {
         //       cp.setExpenses(getExpensesFromCreditPeriod(cp.getId()));
                cp.setPayments(getPaymentsFromCreditPeriod(cp.getId()));
                return cp;

            }
        }

        throw new CreditPeriodNotFoundException("Credit period (index " + periodIndex + ") not found in passed creditPeriods List");
    }

    /**
     * Returns a Credit Period with the supplied periodId, containing its Expenses and Payments
     * @param periodId The ID of the Credit Period
     */
    public CreditPeriod getCreditPeriodFromPeriodId(int periodId) throws CreditPeriodNotFoundException {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        CreditPeriod creditPeriod;

        Cursor cursor =  db.query(ExpenseManagerContract.CreditPeriodTable.TABLE_NAME, null, ExpenseManagerContract.CreditPeriodTable._ID + " == " + periodId, null, null, null, null);

        if(cursor.getCount() == 0)
            throw new CreditPeriodNotFoundException("Specified Credit Period not found in the database. Passed value=" + periodId);
        if(cursor.getCount() > 1)
            throw new CreditPeriodNotFoundException("Database UNIQUE constraint failure, more than one record found. Passed Value=" + periodId);

        try{
            cursor.moveToNext();
            creditPeriod = getCreditPeriodFromCursor(cursor);
       //     creditPeriod.setExpenses(getExpensesFromCreditPeriod(periodId));
            creditPeriod.setPayments(getPaymentsFromCreditPeriod(periodId));
        } finally {
            cursor.close();
        }

        return creditPeriod;
    }


    /**
     * Returns a List of Expenses, given a creditPeriodId
     * @param periodId the Id of the Credit period
     */
   /* public List<Expense> getExpensesFromCreditPeriod(int periodId) {
        List<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor =  db.query(ExpenseManagerContract.ExpenseTable.TABLE_NAME, null,
                ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + " == " + periodId,
                null, null, null, ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName() + " DESC");


        try {
            while (cursor.moveToNext()) {
                expenses.add(getExpenseFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return expenses;
    }*/

    /**
     * Returns a List of Payments, given a creditPeriodId
     * @param periodId the Id of the Credit period
     */
    public List<Payment> getPaymentsFromCreditPeriod(int periodId) {
        List<Payment> payments = new ArrayList<>();
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();

        Cursor cursor =  db.query(ExpenseManagerContract.PaymentTable.TABLE_NAME, null,
                ExpenseManagerContract.PaymentTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + " == " + periodId,
                null, null, null, ExpenseManagerContract.PaymentTable.COLUMN_NAME_DATE.getName() + " DESC");

        try {
            while (cursor.moveToNext()) {
                payments.add(getPaymentFromCursor(cursor));
            }
        } finally {
            cursor.close();
        }

        return payments;
    }


    /**
     * Returns an Expenses, given an Expense ID
     *  the Id of the Expense
     *
    public Expense getExpense(int expenseId) throws CouldNotGetDataException {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        String[] selectionArgs = new String[]{String.valueOf(expenseId)};


        Cursor cursor =  db.query(ExpenseManagerContract.ExpenseTable.TABLE_NAME, null,
                ExpenseManagerContract.ExpenseTable._ID + " =? ",
                selectionArgs, null, null, null);


        if(cursor.getCount() == 0)
            throw new CouldNotGetDataException("Expense not found for ID = " + expenseId);


        cursor.moveToNext();
        return getExpenseFromCursor(cursor);
    }*/

    public Transaction getTransaction(int transId) throws CouldNotGetDataException {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        String[] selectionArgs = new String[]{String.valueOf(transId)};


        Cursor cursor =  db.query(ExpenseManagerContract.TransactionTable.TABLE_NAME, null,
                ExpenseManagerContract.TransactionTable._ID + " =? ",
                selectionArgs, null, null, null);


        if(cursor.getCount() == 0)
            throw new CouldNotGetDataException("Expense not found for ID = " + transId);


        cursor.moveToNext();
        return getTransactionFromCursor(cursor);
    }


    /* Delete data from database */

    /**
     * Deletes a credit card, with its associated credit periods, expenses and payments
     */
    public boolean deleteCreditCard(int creditCardId) throws CouldNotDeleteDataException {

        //Fetch all credit periods and delete associated expenses and payments
        for( CreditPeriod cp : getCreditPeriodListFromCard(creditCardId)) {
            //deleteExpensesFromCreditPeriod(cp.getId());
            deletePaymentsFromCreditPeriod(cp.getId());
            deleteCreditPeriod(cp.getId());
        }

        //Finally, delete the credit card
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(creditCardId)};

        return db.delete(ExpenseManagerContract.CreditCardTable.TABLE_NAME,
                ExpenseManagerContract.CreditCardTable._ID + " =?",
                whereArgs) > 0;
    }

    /**
     * Deletes a credit card, with its associated credit periods, expenses and payments
     */
    public boolean deleteAccount(int accId) throws CouldNotDeleteDataException {

        //Finally, delete the credit card
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(accId)};

        return db.delete(ExpenseManagerContract.AccountTable.TABLE_NAME,
                ExpenseManagerContract.AccountTable._ID + " =?",
                whereArgs) > 0;
    }

    public boolean deleteCreditPeriod(int creditPeriodId) throws CouldNotDeleteDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(creditPeriodId)};

        return db.delete(ExpenseManagerContract.CreditPeriodTable.TABLE_NAME,
                ExpenseManagerContract.CreditPeriodTable._ID + " =?",
                whereArgs) > 0;
    }



    public boolean deleteTransaction(int transId) throws CouldNotDeleteDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(transId)};

        return db.delete(ExpenseManagerContract.TransactionTable.TABLE_NAME,
                ExpenseManagerContract.TransactionTable._ID + " =?",
                whereArgs) > 0;
    }

    public boolean deleteTransactionCategory(int transId) throws CouldNotDeleteDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(transId)};

        return db.delete(ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME,
                ExpenseManagerContract.TransactionTable._ID + " =?",
                whereArgs) > 0;
    }

    public boolean deletePaymentsFromCreditPeriod(int creditPeriodId) throws CouldNotDeleteDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(creditPeriodId)};

        return db.delete(ExpenseManagerContract.PaymentTable.TABLE_NAME,
                ExpenseManagerContract.PaymentTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + " =?",
                whereArgs) > 0;
    }

    public boolean deletePayment(int paymentId) throws CouldNotDeleteDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(paymentId)};

        return db.delete(ExpenseManagerContract.PaymentTable.TABLE_NAME,
                ExpenseManagerContract.PaymentTable._ID + " =?",
                whereArgs) > 0;
    }




    /* Update data on database */
    public long updateCreditCard(CreditCard creditCard) throws CouldNotUpdateDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        //Set values
        ContentValues values = getValuesFromCreditCard(creditCard);

        //Which row to update
        String selection = ExpenseManagerContract.CreditCardTable._ID + " =? ";
        String[] selectionArgs = { String.valueOf(creditCard.getId()) };

        int count = db.update(
                ExpenseManagerContract.CreditCardTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count;
    }
    /* Update data on database */
    public long updateAccount(Account account) throws CouldNotUpdateDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        double correctedBalance =0.0;
        //Set values
        ContentValues values = getValuesFromAccount(account);

        //Which row to update
        String selection = ExpenseManagerContract.AccountTable._ID + " =? ";
        String[] selectionArgs = { String.valueOf(account.getId()) };
        try {
            Account original = getAccount(account.getId());
            correctedBalance = account.getBalance()- original.getBalance();
        }
        catch (Exception e) {
            return -1;
        }
        if(correctedBalance !=0) {
            try {
                insertTransaction(new Transaction(account.getId() , account.getId() , "updated balance manually", null, null,
                        new BigDecimal(correctedBalance),
                        account.getCurrency(), Calendar.getInstance(), getTransactionCategory(TransactionCategory.U_CORRECTION), TransactionType.CORRECTION));
            } catch (Exception e) {
                return -1;
            }
        }

        int count = db.update(
                ExpenseManagerContract.AccountTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);


        return count;
    }
/*
    public long updateExpense(Expense expense) throws CouldNotUpdateDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        //Set values
        ContentValues values = getValuesFromExpense(expense);

        //Which row to update
        String selection = ExpenseManagerContract.ExpenseTable._ID + " =? ";
        String[] selectionArgs = { String.valueOf(expense.getId()) };

        int count = db.update(
                ExpenseManagerContract.ExpenseTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count;
    }
*/
    public long updateTransaction(Transaction transaction) throws CouldNotUpdateDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        //Set values
        ContentValues values = getValuesFromTransaction(transaction);

        //Which row to update
        String selection = ExpenseManagerContract.TransactionTable._ID + " =? ";
        String[] selectionArgs = { String.valueOf(transaction.getId()) };

        int count = db.update(
                ExpenseManagerContract.TransactionTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count;
    }
    public long updateTransactionCategory(TransactionCategory transaction) throws CouldNotUpdateDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        //Set values
        ContentValues values = getValuesFromTransactionCategory(transaction);

        //Which row to update
        String selection = ExpenseManagerContract.TransactionCategoryTable._ID + " =? ";
        String[] selectionArgs = { String.valueOf(transaction.getId()) };

        int count = db.update(
                ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count;
    }
    public long updateCreditPeriod(int creditCardId, CreditPeriod creditPeriod) throws CouldNotUpdateDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        //Set values
        ContentValues values = getValuesFromCreditPeriod(creditCardId, creditPeriod);

        //Which row to update
        String selection = ExpenseManagerContract.CreditPeriodTable._ID + " =? ";
        String[] selectionArgs = { String.valueOf(creditPeriod.getId()) };

        int count = db.update(
                ExpenseManagerContract.CreditPeriodTable.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        return count;
    }



    /* Insert data into database */

    public long insertCreditCard(CreditCard creditcard, BigDecimal firstCreditPeriodLimit) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getName(), creditcard.getCardAlias());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getName(), creditcard.getBankName());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getName(), creditcard.getCardNumber());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getName(), creditcard.getCurrency().getCode());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getName(), creditcard.getCardType().getCode());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName(), creditcard.getClosingDay());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName(), creditcard.getDueDay());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BACKGROUND.getName(), creditcard.getCreditCardBackground().getCode());

        if(creditcard.getCardExpiration() != null)
            values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName(), creditcard.getCardExpiration().getTimeInMillis());
        else
            values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName(), "");

        long newRowId;
        newRowId = db.insert(ExpenseManagerContract.CreditCardTable.TABLE_NAME, null, values);

        if(newRowId == -1)
            throw new CouldNotInsertDataException("There was a problem inserting the Credit Card: " + creditcard.toString());
        else {
            //Insert first creditPeriod
            insertCurrentCreditPeriod(newRowId, creditcard.getClosingDay(), firstCreditPeriodLimit);
        }

        return newRowId;

    }

    public long insertAccount(Account account) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_NICK_NAME.getName(), account.getNickName());
        values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_BANK_NAME.getName(), account.getBankName());
        values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_NUMBER.getName(), account.getAccNumber());
        values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_CURRENCY.getName(), account.getCurrency().getCode());
        values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE.getName(), account.getBalance());
        values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_TYPE.getName(), account.getAccountType().getCode());
        values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE_UPDATE.getName(),account.getBalanceUpdated().getTimeInMillis());
        //values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_CLOSING_DAY.getName(), account.getClosingDay());
        //values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_DUE_DAY.getName(), account.getDueDay());
        //values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_BACKGROUND.getName(), account.getCreditCardBackground().getCode());

        if(account.getBalanceUpdated() != null)
            values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE_UPDATE.getName(), account.getBalanceUpdated().getTimeInMillis());
        else
            values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE_UPDATE.getName(), "");

        long newRowId;
        newRowId = db.insert(ExpenseManagerContract.AccountTable.TABLE_NAME, null, values);
        if(newRowId != -1 && account.getBalance()!=0)
        {
            insertTransaction(new Transaction((int)newRowId,(int)newRowId,"initial Balance",null,null,
                    new BigDecimal(account.getBalance()),
                    account.getCurrency(),Calendar.getInstance(),getTransactionCategory(TransactionCategory.U_CORRECTION),TransactionType.CORRECTION ));
        }
        return newRowId;

    }

    /**
     * Inserts a creditPeriod associated to a creditCard, which engulfs the current date (today), given a closing date
     * @param creditCardId the Id of the CreditCard to associate the CreditPeriod
     * @param  closingDay the credit card's closing day
     * @param creditPeriodLimit BigDecimal value of the currency limit of the period
     */
    public long insertCurrentCreditPeriod(long creditCardId, int closingDay, BigDecimal creditPeriodLimit) throws CouldNotInsertDataException {
        //Insert first creditPeriod

        // Set dates to be at midnight (start of day) today.
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);
        startDate.set(Calendar.DAY_OF_MONTH, closingDay);

        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(startDate.getTimeInMillis());

        //Check if closingDay if before or after today
        if(closingDay <= Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
            endDate.add(Calendar.MONTH, 1);
        else
            startDate.add(Calendar.MONTH, -1);

        endDate.add(Calendar.MILLISECOND, -1);

        CreditPeriod creditPeriod = new CreditPeriod(CreditPeriod.PERIOD_NAME_COMPLETE, startDate, endDate, creditPeriodLimit);
        return insertCreditPeriod(creditCardId, creditPeriod);
    }

    public long insertCreditPeriod(long creditCardId, CreditPeriod creditPeriod) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD.getName(), creditCardId);
        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getName(), creditPeriod.getPeriodNameStyle());
        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName(), creditPeriod.getStartDate().getTimeInMillis());
        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getName(), creditPeriod.getEndDate().getTimeInMillis());
        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getName(), creditPeriod.getCreditLimit().toPlainString());


        long newRowId = -1;
        newRowId = db.insert(ExpenseManagerContract.CreditPeriodTable.TABLE_NAME, null, values);

        if(newRowId == -1)
            throw new CouldNotInsertDataException("There was a problem inserting the Credit Period: " + creditPeriod.toString());

        return newRowId;

    }

    /*public long insertExpense(int creditPeriodId, Expense expense) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        ContentValues values = getValuesFromExpense(expense);
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName(), creditPeriodId);

        long newRowId;
        newRowId = db.insert(ExpenseManagerContract.ExpenseTable.TABLE_NAME, null, values);

        if(newRowId == -1)
            throw new CouldNotInsertDataException("There was a problem inserting the Expense: " + expense.toString());

        return newRowId;
    }*/

    public long insertTransaction( Transaction expense) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        ContentValues values = getValuesFromTransaction(expense);

        long newRowId;
        newRowId = db.insert(ExpenseManagerContract.TransactionTable.TABLE_NAME, null, values);

        if(newRowId == -1)
            throw new CouldNotInsertDataException("There was a problem inserting the Expense: " + expense.toString());

        return newRowId;
    }
    public long insertTransactionCategory( TransactionCategory expense) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        ContentValues values = getValuesFromTransactionCategory(expense);

        long newRowId;
        newRowId = db.insert(ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME, null, values);

        if(newRowId == -1)
            throw new CouldNotInsertDataException("There was a problem inserting the Expense: " + expense.toString());

        return newRowId;
    }

    public long insertPayment(int creditPeriodId, Payment payment) throws CouldNotInsertDataException {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.PaymentTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName(), creditPeriodId);
        values.put(ExpenseManagerContract.PaymentTable.COLUMN_NAME_DESCRIPTION.getName(), payment.getDescription());
        values.put(ExpenseManagerContract.PaymentTable.COLUMN_NAME_AMOUNT.getName(), payment.getAmount().toPlainString());
        values.put(ExpenseManagerContract.PaymentTable.COLUMN_NAME_CURRENCY.getName(), payment.getCurrency().getCode());
        values.put(ExpenseManagerContract.PaymentTable.COLUMN_NAME_DATE.getName(), payment.getDate().getTimeInMillis());


        long newRowId=1;
        return newRowId;
    }



    /* Model to ContentValues *
    private ContentValues getValuesFromExpense(Expense expense) {
        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DESCRIPTION.getName(), expense.getDescription());
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_THUMBNAIL.getName(), expense.getThumbnail());
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FULL_IMAGE_PATH.getName(), expense.getFullImagePath());
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_AMOUNT.getName(), expense.getAmount().toPlainString());
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_CURRENCY.getName(), expense.getCurrency().getCode());
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName(), expense.getDate().getTimeInMillis());
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_CATEGORY.getName(), expense.getExpenseCategory().getCode());
        values.put(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_TYPE.getName(), expense.getExpenseType().getCode());
        return values;
    }*/

    /* Model to ContentValues */
    private ContentValues getValuesFromTransaction(Transaction transaction) {
        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.TransactionTable.COLUMN_NAME_FOREIGN_KEY_GIVER.getName(),transaction.getGiver());
        values.put(ExpenseManagerContract.TransactionTable.COLUMN_NAME_FOREIGN_KEY_RECIEVER.getName(),transaction.getTaker());
        values.put(ExpenseManagerContract.TransactionTable.COLUMN_NAME_DESCRIPTION.getName(), transaction.getDescription());
        values.put(ExpenseManagerContract.TransactionTable.COLUMN_NAME_THUMBNAIL.getName(), transaction.getThumbnail());
        values.put(ExpenseManagerContract.TransactionTable.COLUMN_NAME_FULL_IMAGE_PATH.getName(), transaction.getFullImagePath());
        values.put(ExpenseManagerContract.TransactionTable.COLUMN_NAME_AMOUNT.getName(), transaction.getAmount().toPlainString());
        values.put(ExpenseManagerContract.TransactionTable.COLUMN_NAME_CURRENCY.getName(), transaction.getCurrency().getCode());
        values.put(ExpenseManagerContract.TransactionTable.COLUMN_NAME_DATE.getName(), transaction.getDate().getTimeInMillis());
        values.put(ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_CATEGORY.getName(), transaction.getTransactionCategory().getId());
        values.put(ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_TYPE.getName(), transaction.getTransactionType().getCode());
        return values;
    }
    /* Model to ContentValues */
    private ContentValues getValuesFromTransactionCategory(TransactionCategory transaction) {
        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_DESCRIPTION.getName(), transaction.getmName());
        values.put(ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRASACTION_TYPE.getName(), transaction.getType().getCode());
        values.put(ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_BUDGET.getName(), transaction.getBudget());
          return values;
    }

    private ContentValues getValuesFromCreditCard(CreditCard creditCard) {
        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getName(), creditCard.getCardAlias());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getName(), creditCard.getBankName());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getName(), creditCard.getCardNumber());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getName(), creditCard.getCurrency().getCode());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getName(), creditCard.getCardType().getCode());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName(), creditCard.getCardExpiration().getTimeInMillis());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName(), creditCard.getClosingDay());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName(), creditCard.getDueDay());
        values.put(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BACKGROUND.getName(), creditCard.getCreditCardBackground().getCode());
        return values;
    }

    private ContentValues getValuesFromAccount(Account account) {
        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_NICK_NAME.getName(), account.getNickName());
        values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_BANK_NAME.getName(), account.getBankName());
        values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_NUMBER.getName(), account.getAccNumber());
        values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_CURRENCY.getName(), account.getCurrency().getCode());
        values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE.getName(), account.getBalance());
        values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_TYPE.getName(), account.getAccountType().getCode());
        values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE_UPDATE.getName(), account.getBalanceUpdated().getTimeInMillis());
        //values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_CLOSING_DAY.getName(), account.getClosingDay());
        //values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_DUE_DAY.getName(), account.getDueDay());
        //values.put(ExpenseManagerContract.AccountTable.COLUMN_NAME_BACKGROUND.getName(), account.getCreditCardBackground().getCode());
        return values;
    }

    private ContentValues getValuesFromCreditPeriod(int creditCardId, CreditPeriod creditPeriod) {
        ContentValues values = new ContentValues();
        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD.getName(), creditCardId);
        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getName(), creditPeriod.getPeriodNameStyle());
        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName(), creditPeriod.getStartDate().getTimeInMillis());
        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getName(), creditPeriod.getEndDate().getTimeInMillis());
        values.put(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getName(), creditPeriod.getCreditLimit().toPlainString());
        return values;
    }


    /* Cursor to Model */

    private CreditCard getCreditCardFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable._ID));
        String cardAlias = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getName()));
        String bankName = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getName()));
        String cardNumber = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getName()));
        Currency currency = Currency.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getName())));
        CreditCardType cardType = CreditCardType.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getName())));
        int closingDay = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName()));
        int dueDay = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName()));
        CreditCardBackground creditCardBackground = CreditCardBackground.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BACKGROUND.getName())));


        Calendar cardExpiration = null;
        if(!cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName())).isEmpty()) {
            cardExpiration = Calendar.getInstance();
            cardExpiration.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName())));
        }

        return new CreditCard(id, cardAlias, bankName, cardNumber, currency, cardType, cardExpiration, closingDay, dueDay, creditCardBackground);
    }

    private Account getAccountFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.AccountTable._ID));
        String cardAlias = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.AccountTable.COLUMN_NAME_NICK_NAME.getName()));
        String bankName = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.AccountTable.COLUMN_NAME_BANK_NAME.getName()));
        String cardNumber = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_NUMBER.getName()));
        String balance = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE.getName()));
        Currency currency = Currency.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.AccountTable.COLUMN_NAME_CURRENCY.getName())));
        AccountType accountType = AccountType.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_TYPE.getName())));
        //int closingDay = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName()));
        //int dueDay = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName()));
        //CreditCardBackground creditCardBackground = CreditCardBackground.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BACKGROUND.getName())));


        Calendar cardExpiration = null;
        if(!cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE_UPDATE.getName())).isEmpty()) {
            cardExpiration = Calendar.getInstance();
            cardExpiration.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE_UPDATE.getName())));
        }

        Double fbal = 0.0;
        try {
             fbal = Double.valueOf(balance);
        }
        catch(Exception e)
        {

        }
        return new Account(id, cardAlias, bankName, cardNumber,fbal, currency, accountType, cardExpiration);
    }

    private CreditPeriod getCreditPeriodFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditPeriodTable._ID));
        int periodNameStyle = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getName()));
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName())));
        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getName())));
        BigDecimal creditLimit =  new BigDecimal(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getName())));

        return new CreditPeriod(id, periodNameStyle, startDate, endDate, creditLimit);
    }

/*
    private Expense getExpenseFromCursor(Cursor cursor) {

        int id = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable._ID));
        String description = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DESCRIPTION.getName()));
        byte[] image = cursor.getBlob(Cacursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_THUMBNAIL.getName()));
        String fullImagePath = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_FULL_IMAGE_PATH.getName()));
        BigDecimal amount = new BigDecimal(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_AMOUNT.getName())));
        Currency currency = Currency.valueOf-[

        \(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_CURRENCY.getName())));
        C.alendar date = Calendar.getInstance();
        date.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_DATE.getName())));
        ExpenseCategory expenseCategory = ExpenseCategory.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_CATEGORY.getName())));
        ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.ExpenseTable.COLUMN_NAME_EXPENSE_TYPE.getName())));

        return new Expense(id, description, image, fullImagePath, amount, currency, date, expenseCategory, expenseType);
    }
*/
    private Transaction getTransactionFromCursor(Cursor cursor) {

        int id = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.TransactionTable._ID));
        int giver = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.TransactionTable.COLUMN_NAME_FOREIGN_KEY_GIVER.getName())));
        int reciver = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.TransactionTable.COLUMN_NAME_FOREIGN_KEY_RECIEVER.getName())));
        String description = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.TransactionTable.COLUMN_NAME_DESCRIPTION.getName()));
        byte[] image = cursor.getBlob(cursor.getColumnIndex(ExpenseManagerContract.TransactionTable.COLUMN_NAME_THUMBNAIL.getName()));
        String fullImagePath = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.TransactionTable.COLUMN_NAME_FULL_IMAGE_PATH.getName()));
        BigDecimal amount = new BigDecimal(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.TransactionTable.COLUMN_NAME_AMOUNT.getName())));
        Currency currency = Currency.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.TransactionTable.COLUMN_NAME_CURRENCY.getName())));
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.TransactionTable.COLUMN_NAME_DATE.getName())));
        TransactionCategory expenseCategory = getTransactionCategory(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_CATEGORY.getName())));
        TransactionType expenseType = TransactionType.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_TYPE.getName())));

        return new Transaction(id,giver,reciver, description, image, fullImagePath, amount, currency, date, expenseCategory, expenseType);
    }



    private TransactionCategory getTransactionCategoryFromCursor(Cursor cursor) {

        int id = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.TransactionCategoryTable._ID));
        String description = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_DESCRIPTION.getName()));
        TransactionType expenseType = TransactionType.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRASACTION_TYPE.getName())));
        double budget = 0.0;
        try {
            budget = Double.parseDouble(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_BUDGET.getName())));
        }
        catch(Exception e) {

        }
        return new TransactionCategory(id, description,expenseType,budget,0.0);
    }

    private Payment getPaymentFromCursor(Cursor cursor) {

        int id = cursor.getInt(cursor.getColumnIndex(ExpenseManagerContract.PaymentTable._ID));
        String description = cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.PaymentTable.COLUMN_NAME_DESCRIPTION.getName()));
        BigDecimal amount = new BigDecimal(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.PaymentTable.COLUMN_NAME_AMOUNT.getName())));
        Currency currency = Currency.valueOf(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.PaymentTable.COLUMN_NAME_CURRENCY.getName())));
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(ExpenseManagerContract.PaymentTable.COLUMN_NAME_DATE.getName())));

        return new Payment(id, description, amount, currency, date);
    }

    public List<DailyExpense> getDailyExpenses() {
        List<DailyExpense> del = new ArrayList<>();
        BigDecimal accumulated = new BigDecimal(0);
        BigDecimal total;
        Calendar start = Calendar.getInstance();
        start.set(Calendar.DATE,1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = Calendar.getInstance();

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(start.getTimeZone());
        cal.setTimeInMillis(start.getTimeInMillis());

        List<Transaction> trans = getTransactionInPeroid( start, end);
        int totalDays = getTotalDaysInPeriod(start,end);
        totalDays+=1;
        for (int i = 0; i < totalDays; i++) {
            del.add(i, new DailyExpense(cal, new BigDecimal(0),new BigDecimal(0)));

            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        for(Transaction tran:trans)
        {
            int dayIndex = getDateIndex(start,tran.getDate());
            del.get(dayIndex).addToAmount(tran.getAmount());
        }

        for(int i=0;i<totalDays;i++) {
            total = del.get(i).getAmount();
            accumulated = accumulated.add(total);
            del.get(i).setAccumulated(accumulated);
        }
        return del;
    }
    public int getTotalDaysInPeriod(Calendar startDate,Calendar endDate) {

        // Create copies so we don't update the original calendars.
        Calendar start = Calendar.getInstance();
        start.setTimeZone(startDate.getTimeZone());
        start.setTimeInMillis(startDate.getTimeInMillis());

        Calendar end = Calendar.getInstance();
        end.setTimeZone(endDate.getTimeZone());
        end.setTimeInMillis(endDate.getTimeInMillis());

        // Add one millisecond so that the subtraction below works.
        // If end = Aug 5 23:59:59 we add one millisecond
        // so end = Aug 6 00:00:00 so that Aug 5 counts
        end.add(Calendar.MILLISECOND, 1);


        // At this point, each calendar is set to midnight on
        // their respective days. Now use TimeUnit.MILLISECONDS to
        // compute the number of full days between the two of them.
        int days = (int)TimeUnit.MILLISECONDS.toDays(Math.abs(end.getTimeInMillis() - start.getTimeInMillis()));
        return days;
    }

    public int getDateIndex(Calendar startDate,Calendar dateToBeIndexed) {

        // Create copies so we don't update the original calendars.
        Calendar start = Calendar.getInstance();
        start.setTimeZone(startDate.getTimeZone());
        start.setTimeInMillis(startDate.getTimeInMillis());

        Calendar end = Calendar.getInstance();
        end.setTimeZone(dateToBeIndexed.getTimeZone());
        end.setTimeInMillis(dateToBeIndexed.getTimeInMillis());

        // Set the copies to be at midnight, but keep the day information.
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);

        // At this point, each calendar is set to midnight on
        // their respective days. Now use TimeUnit.MILLISECONDS to
        // compute the number of full days between the two of them.


        int days = (int) TimeUnit.MILLISECONDS.toDays(Math.abs(end.getTimeInMillis() - start.getTimeInMillis()));
        return days;
    }




    public int getAccountOrCreate(String accName) {
        int accId=-1;

        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
String where = ExpenseManagerContract.AccountTable.COLUMN_NAME_NICK_NAME.getName()+"='"+accName+"'";
where+= " OR "+ExpenseManagerContract.AccountTable.COLUMN_NAME_BANK_NAME.getName()+"='"+accName+"'";
        Cursor cursor = db.query(ExpenseManagerContract.AccountTable.TABLE_NAME, null, where, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                       accId = new Integer(cursor.getString(cursor.getColumnIndex(ExpenseManagerContract.AccountTable._ID)));
            }
        }catch (Exception e)
        {

        }
        finally {
            cursor.close();
        }

        if(accId == -1)
        {
            try {
                accId = (int)insertAccount(new Account(accName, accName, "1", 0, Currency.INR, AccountType.Merchant, Calendar.getInstance()));
            }
            catch (Exception ex)
            {
                accId =Account.CASH_ID;
            }
        }

        return accId;
    }
}
