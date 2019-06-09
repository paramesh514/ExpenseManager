package ve.com.abicelis.creditcardexpensemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import ve.com.abicelis.creditcardexpensemanager.app.utils.FileUtils;
import ve.com.abicelis.creditcardexpensemanager.enums.AccountType;
import ve.com.abicelis.creditcardexpensemanager.enums.TransactionType;
import ve.com.abicelis.creditcardexpensemanager.model.Account;


/**
 * Created by Alex on 8/8/2016.
 */
public class ExpenseManagerDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ExpenseManager.db";
    public static final int DATABASE_VERSION = 3;                               // If you change the database schema, you must increment the database version.
    private static final String COMMA_SEP = ", ";

    private String mAppDbFilepath;
    private String mDbExternalBackupFilepath;
    private Context mContext;


    public ExpenseManagerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mAppDbFilepath =  context.getDatabasePath(DATABASE_NAME).getPath();
        mDbExternalBackupFilepath = Environment.getExternalStorageDirectory().getPath() + "/" + DATABASE_NAME;
        mContext = context;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createDatabase(sqLiteDatabase);
        //insertMockData(sqLiteDatabase);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //TODO: FIGURE THIS PART OUT!
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        deleteDatabase(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }


    /**
     * Copies the database file at the specified location over the current
     * internal application database.
     * */
    public boolean exportDatabase() throws IOException {

        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.
        close();
        File appDatabase = new File(mAppDbFilepath);
        File backupDatabase = new File(mDbExternalBackupFilepath);

        if (appDatabase.exists()) {
            FileUtils.copyFile(new FileInputStream(appDatabase), new FileOutputStream(backupDatabase));
            return true;
        }
        return false;
    }

    /**
     * Copies the database file at the specified location over the current
     * internal application database.
     * */
    public boolean importDatabase() throws IOException {

        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.
        close();
        File appDatabase = new File(mAppDbFilepath);
        File backupDatabase = new File(mDbExternalBackupFilepath);

        if (backupDatabase.exists()) {
            FileUtils.copyFile(new FileInputStream(backupDatabase), new FileOutputStream(appDatabase));
            // Access the copied database so SQLiteHelper will cache it and mark
            // it as created.
            getWritableDatabase().close();
            return true;
        }
        return false;
    }

    private void insertMockData(SQLiteDatabase sqLiteDatabase) {
        String statement;

        Calendar cal = Calendar.getInstance();
        // Set cal to be at midnight (start of day) today.
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        cal.add(Calendar.DAY_OF_MONTH, -10);
        int closingDay = cal.get(Calendar.DAY_OF_MONTH);        //closingDay 10 days ago
        cal.add(Calendar.DAY_OF_MONTH, 20);
        int dueDay = cal.get(Calendar.DAY_OF_MONTH);            //dueDay in 10 days
        cal.add(Calendar.DAY_OF_MONTH, -10);


        statement = "INSERT INTO " + ExpenseManagerContract.CreditCardTable.TABLE_NAME + " (" +
                        ExpenseManagerContract.CreditCardTable._ID + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BACKGROUND.getName() +
                        ") VALUES (0, 'Shopping Card', 'Bank Of America', '1234-5678-1234-5678', 'USD', 'MASTERCARD', '0', " + closingDay + ", " + dueDay + ", 'DARK_RED'); ";
        sqLiteDatabase.execSQL(statement);

        cal.add(Calendar.DAY_OF_MONTH, -9);
        long period1StartDate = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, 1);
        long period2StartDate = cal.getTimeInMillis();
        cal.add(Calendar.MILLISECOND, -1);
        long period1EndDate = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, 1);
        long period2EndDate = cal.getTimeInMillis();

        statement = "INSERT INTO " + ExpenseManagerContract.CreditPeriodTable.TABLE_NAME + " (" +
                        ExpenseManagerContract.CreditPeriodTable._ID + COMMA_SEP +
                        ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getName() + COMMA_SEP +
                        ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getName() +
                        ") VALUES (0, 0, 0, '" + period1StartDate + "', '" + period1EndDate + "', '60000'), (1, 0, 0, '" + period2StartDate + "', '" + period2EndDate + "', '80000'); ";
        sqLiteDatabase.execSQL(statement);

        cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -6);
        long expense1period1 = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        long expense2period1 = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        long expense3period1 = cal.getTimeInMillis();
        long expense4period1 = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        long expense5period1 = cal.getTimeInMillis();



    }

    private void createDatabase(SQLiteDatabase sqLiteDatabase) {
        String statement;

        statement = "CREATE TABLE " + ExpenseManagerContract.CreditCardTable.TABLE_NAME + " (" +
                ExpenseManagerContract.CreditCardTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_ALIAS.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BANK_NAME.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_NUMBER.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CURRENCY.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_TYPE.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CARD_EXPIRATION.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_CLOSING_DAY.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_DUE_DAY.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BACKGROUND.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BACKGROUND.getDataType() +
                " ); " ;
        sqLiteDatabase.execSQL(statement);


        statement = "CREATE TABLE " + ExpenseManagerContract.CreditPeriodTable.TABLE_NAME + " (" +
                ExpenseManagerContract.CreditPeriodTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getName() + " " + ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_PERIOD_NAME_STYLE.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getName() + " " + ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_START_DATE.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getName() + " " + ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_END_DATE.getDataType() + COMMA_SEP +
                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getName() + " " + ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getDataType() + COMMA_SEP +

                ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD.getName() + " " + ExpenseManagerContract.CreditPeriodTable.COLUMN_NAME_CREDIT_LIMIT.getDataType() +
                " REFERENCES " + ExpenseManagerContract.CreditCardTable.TABLE_NAME + "(" + ExpenseManagerContract.CreditCardTable._ID + ") " +

                " ); ";
        sqLiteDatabase.execSQL(statement);



        statement = "CREATE TABLE " + ExpenseManagerContract.PaymentTable.TABLE_NAME + " (" +
                ExpenseManagerContract.PaymentTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +

                ExpenseManagerContract.PaymentTable.COLUMN_NAME_DESCRIPTION.getName() + " " + ExpenseManagerContract.PaymentTable.COLUMN_NAME_DESCRIPTION.getDataType() + COMMA_SEP +
                ExpenseManagerContract.PaymentTable.COLUMN_NAME_AMOUNT.getName() + " " + ExpenseManagerContract.PaymentTable.COLUMN_NAME_AMOUNT.getDataType() + COMMA_SEP +
                ExpenseManagerContract.PaymentTable.COLUMN_NAME_CURRENCY.getName() + " " + ExpenseManagerContract.PaymentTable.COLUMN_NAME_CURRENCY.getDataType() + COMMA_SEP +
                ExpenseManagerContract.PaymentTable.COLUMN_NAME_DATE.getName() + " " + ExpenseManagerContract.PaymentTable.COLUMN_NAME_DATE.getDataType() + COMMA_SEP +


                ExpenseManagerContract.PaymentTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getName() + " " + ExpenseManagerContract.PaymentTable.COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD.getDataType() +
                " REFERENCES " + ExpenseManagerContract.CreditPeriodTable.TABLE_NAME + "(" + ExpenseManagerContract.CreditPeriodTable._ID + ") " +

                " ); ";
        sqLiteDatabase.execSQL(statement);

        //Account Table
        statement = "CREATE TABLE " + ExpenseManagerContract.AccountTable.TABLE_NAME + " (" +
                ExpenseManagerContract.AccountTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_NICK_NAME.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_NICK_NAME.getDataType() + COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_BANK_NAME.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_BANK_NAME.getDataType() + COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_NUMBER.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_NUMBER.getDataType() + COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_CURRENCY.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_CURRENCY.getDataType() + COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_TYPE.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_TYPE.getDataType() + COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE.getDataType() + COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE_UPDATE.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE_UPDATE.getDataType() +COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_SPARE1.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_SPARE1.getDataType()+COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_SPARE2.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_SPARE2.getDataType()+COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_SPARE3.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_SPARE3.getDataType()+COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_SPARE4.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_SPARE4.getDataType()+COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_SPARE5.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_SPARE5.getDataType()+
                " ); " ;
        sqLiteDatabase.execSQL(statement);
       //Transaction Table
        statement = "CREATE TABLE " + ExpenseManagerContract.TransactionTable.TABLE_NAME + " (" +
                ExpenseManagerContract.TransactionTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_FOREIGN_KEY_GIVER.getName() + " " + ExpenseManagerContract.TransactionTable.COLUMN_NAME_FOREIGN_KEY_GIVER.getDataType() + COMMA_SEP +
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_FOREIGN_KEY_RECIEVER.getName() + " " + ExpenseManagerContract.TransactionTable.COLUMN_NAME_FOREIGN_KEY_RECIEVER.getDataType() + COMMA_SEP +
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_DESCRIPTION.getName() + " " + ExpenseManagerContract.TransactionTable.COLUMN_NAME_DESCRIPTION.getDataType() + COMMA_SEP +
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_THUMBNAIL.getName() + " " + ExpenseManagerContract.TransactionTable.COLUMN_NAME_THUMBNAIL.getDataType() + COMMA_SEP +
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_FULL_IMAGE_PATH.getName() + " " + ExpenseManagerContract.TransactionTable.COLUMN_NAME_FULL_IMAGE_PATH.getDataType() + COMMA_SEP +
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_AMOUNT.getName() + " " + ExpenseManagerContract.TransactionTable.COLUMN_NAME_AMOUNT.getDataType() + COMMA_SEP +
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_CURRENCY.getName() + " " + ExpenseManagerContract.TransactionTable.COLUMN_NAME_CURRENCY.getDataType() + COMMA_SEP +
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_DATE.getName() + " " + ExpenseManagerContract.TransactionTable.COLUMN_NAME_DATE.getDataType() + COMMA_SEP +
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_CATEGORY.getName() + " " + ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_CATEGORY.getDataType() + COMMA_SEP +
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_TYPE.getName() + " " + ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_TYPE.getDataType() +COMMA_SEP +
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_SPARE1.getName() + " " + ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_SPARE1.getDataType()+COMMA_SEP +
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_SPARE2.getName() + " " + ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_SPARE2.getDataType()+COMMA_SEP +
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_SPARE3.getName() + " " + ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_SPARE3.getDataType()+COMMA_SEP +
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_SPARE4.getName() + " " + ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_SPARE4.getDataType()+COMMA_SEP +
                ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_SPARE5.getName() + " " + ExpenseManagerContract.TransactionTable.COLUMN_NAME_TRANSACTION_SPARE5.getDataType()+

                " ); ";
        sqLiteDatabase.execSQL(statement);
        //Transaction Category Table
        statement = "CREATE TABLE " + ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME + " (" +
                ExpenseManagerContract.TransactionCategoryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_DESCRIPTION.getName() + " " + ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_DESCRIPTION.getDataType() + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRASACTION_TYPE.getName() + " " + ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRASACTION_TYPE.getDataType() + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_BUDGET.getName() + " " + ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_BUDGET.getDataType() +COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRANSACTION_CATEGORY_SPARE1.getName() + " " + ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRANSACTION_CATEGORY_SPARE1.getDataType() +COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRANSACTION_CATEGORY_SPARE2.getName() + " " + ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRANSACTION_CATEGORY_SPARE2.getDataType() +COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRANSACTION_CATEGORY_SPARE3.getName() + " " + ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRANSACTION_CATEGORY_SPARE3.getDataType() +COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRANSACTION_CATEGORY_SPARE4.getName() + " " + ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRANSACTION_CATEGORY_SPARE4.getDataType() +COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRANSACTION_CATEGORY_SPARE5.getName() + " " + ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRANSACTION_CATEGORY_SPARE5.getDataType() +
                //ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_NUMBER.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_NUMBER.getDataType() + COMMA_SEP +
                //ExpenseManagerContract.AccountTable.COLUMN_NAME_CURRENCY.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_CURRENCY.getDataType() + COMMA_SEP +
                //ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_TYPE.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_TYPE.getDataType() + COMMA_SEP +
                //ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE.getDataType() + COMMA_SEP +
                //ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE_UPDATE.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE_UPDATE.getDataType() + COMMA_SEP +
                //ExpenseManagerContract.AccountTable.COLUMN_NAME_CLOSING_DAY.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_CLOSING_DAY.getDataType() + COMMA_SEP +
                //ExpenseManagerContract.AccountTable.COLUMN_NAME_DUE_DAY.getName() + " " + ExpenseManagerContract.AccountTable.COLUMN_NAME_DUE_DAY.getDataType() + COMMA_SEP +
                //ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BACKGROUND.getName() + " " + ExpenseManagerContract.CreditCardTable.COLUMN_NAME_BACKGROUND.getDataType() +
                " ); " ;
        sqLiteDatabase.execSQL(statement);
//creating default acocunt
        statement = "INSERT INTO " + ExpenseManagerContract.AccountTable.TABLE_NAME + " (" +
                ExpenseManagerContract.AccountTable._ID + COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_NICK_NAME.getName() + COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_BANK_NAME.getName() + COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_NUMBER.getName() + COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_CURRENCY.getName() + COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_ACCOUNT_TYPE.getName() + COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE.getName()+ COMMA_SEP +
                ExpenseManagerContract.AccountTable.COLUMN_NAME_BALANCE_UPDATE.getName()  +
                ") VALUES ('"+ Account.CASH_ID+"', 'Cash', 'Cash', '1', 'INR', '"+AccountType.Cash+"',0.0,"+Calendar.getInstance().getTimeInMillis()+ "); ";
        sqLiteDatabase.execSQL(statement);
//creating all expense account
        statement = "INSERT INTO " + ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME + " (" +
                ExpenseManagerContract.TransactionCategoryTable._ID + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_DESCRIPTION.getName() + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRASACTION_TYPE.getName() + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_BUDGET.getName() +
                ") VALUES (0, 'All','"+ TransactionType.EXPENSE.getCode() +"',0.0); ";
        sqLiteDatabase.execSQL(statement);

        //careting uncategorized for each category
        statement = "INSERT INTO " + ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME + " (" +
                ExpenseManagerContract.TransactionCategoryTable._ID + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_DESCRIPTION.getName() + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRASACTION_TYPE.getName() + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_BUDGET.getName() +
                ") VALUES (1, 'UnCategorized','"+ TransactionType.EXPENSE.getCode() +"',0.0); ";
        sqLiteDatabase.execSQL(statement);

        statement = "INSERT INTO " + ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME + " (" +
                ExpenseManagerContract.TransactionCategoryTable._ID + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_DESCRIPTION.getName() + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRASACTION_TYPE.getName() + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_BUDGET.getName() +
                ") VALUES (2, 'UnCategorized','"+ TransactionType.INCOME.getCode() +"',0.0); ";
        sqLiteDatabase.execSQL(statement);

        statement = "INSERT INTO " + ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME + " (" +
                ExpenseManagerContract.TransactionCategoryTable._ID + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_DESCRIPTION.getName() + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRASACTION_TYPE.getName() + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_BUDGET.getName() +
                ") VALUES (3, 'UnCategorized','"+ TransactionType.TRANSFER.getCode() +"',0.0); ";
        sqLiteDatabase.execSQL(statement);

        statement = "INSERT INTO " + ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME + " (" +
                ExpenseManagerContract.TransactionCategoryTable._ID + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_DESCRIPTION.getName() + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRASACTION_TYPE.getName() + COMMA_SEP +
                ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_BUDGET.getName() +
                ") VALUES (4, 'Correction','"+ TransactionType.CORRECTION.getCode() +"',0.0); ";
        sqLiteDatabase.execSQL(statement);

        insertDefaultCategories(sqLiteDatabase);


    }
private void insertDefaultCategories(SQLiteDatabase sqLiteDatabase)
{
    String statement;
    statement = "INSERT INTO " + ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME + " (" +
            ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_DESCRIPTION.getName() + COMMA_SEP +
            ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRASACTION_TYPE.getName() + COMMA_SEP +
            ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_BUDGET.getName() +
            ") VALUES ( 'Food','"+ TransactionType.EXPENSE.getCode() +"',0.0); ";
    sqLiteDatabase.execSQL(statement);

    statement = "INSERT INTO " + ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME + " (" +
            ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_DESCRIPTION.getName() + COMMA_SEP +
            ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRASACTION_TYPE.getName() + COMMA_SEP +
            ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_BUDGET.getName() +
            ") VALUES ( 'Bills','"+ TransactionType.EXPENSE.getCode() +"',0.0); ";
    sqLiteDatabase.execSQL(statement);

    statement = "INSERT INTO " + ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME + " (" +
            ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_DESCRIPTION.getName() + COMMA_SEP +
            ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRASACTION_TYPE.getName() + COMMA_SEP +
            ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_BUDGET.getName() +
            ") VALUES ( 'Misc','"+ TransactionType.EXPENSE.getCode() +"',0.0); ";
    sqLiteDatabase.execSQL(statement);

    statement = "INSERT INTO " + ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME + " (" +
            ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_DESCRIPTION.getName() + COMMA_SEP +
            ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_TRASACTION_TYPE.getName() + COMMA_SEP +
            ExpenseManagerContract.TransactionCategoryTable.COLUMN_NAME_BUDGET.getName() +
            ") VALUES ( 'Travel','"+ TransactionType.EXPENSE.getCode() +"',0.0); ";
    sqLiteDatabase.execSQL(statement);

}
    private void deleteDatabase(SQLiteDatabase sqLiteDatabase) {
        String statement ;

        statement = "DROP TABLE IF EXISTS " + ExpenseManagerContract.PaymentTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + ExpenseManagerContract.CreditPeriodTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + ExpenseManagerContract.CreditCardTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + ExpenseManagerContract.AccountTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + ExpenseManagerContract.TransactionTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + ExpenseManagerContract.TransactionCategoryTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);
    }
}
