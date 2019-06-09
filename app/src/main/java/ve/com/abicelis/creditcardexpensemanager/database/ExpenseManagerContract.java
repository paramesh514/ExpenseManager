package ve.com.abicelis.creditcardexpensemanager.database;

import android.provider.BaseColumns;

/**
 * Created by Alex on 8/8/2016.
 */
public final class ExpenseManagerContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ExpenseManagerContract() { }

    /* CreditCard Table */
    public static abstract class CreditCardTable implements BaseColumns {
        public static final String TABLE_NAME = "creditcard";
        public static final TableColumn COLUMN_NAME_CARD_ALIAS = new TableColumn(DataType.TEXT, "cardalias");
        public static final TableColumn COLUMN_NAME_BANK_NAME = new TableColumn(DataType.TEXT, "bankname");
        public static final TableColumn COLUMN_NAME_CARD_NUMBER = new TableColumn(DataType.TEXT, "cardnumber");
        public static final TableColumn COLUMN_NAME_CURRENCY = new TableColumn(DataType.TEXT, "currency");
        public static final TableColumn COLUMN_NAME_CARD_TYPE = new TableColumn(DataType.TEXT, "cardtype");
        public static final TableColumn COLUMN_NAME_CARD_EXPIRATION = new TableColumn(DataType.TEXT, "cardexpiration");
        public static final TableColumn COLUMN_NAME_CLOSING_DAY = new TableColumn(DataType.TEXT, "closingday");
        public static final TableColumn COLUMN_NAME_DUE_DAY = new TableColumn(DataType.TEXT, "dueday");
        public static final TableColumn COLUMN_NAME_BACKGROUND = new TableColumn(DataType.TEXT, "background");
    }

    /* CreditPeriod Table */
    public static abstract class CreditPeriodTable implements BaseColumns {
        public static final String TABLE_NAME = "creditperiod";

        public static final TableColumn COLUMN_NAME_FOREIGN_KEY_CREDIT_CARD = new TableColumn(DataType.INTEGER, "fk_creditcard");
        public static final TableColumn COLUMN_NAME_PERIOD_NAME_STYLE = new TableColumn(DataType.TEXT, "periodnamestyle");
        public static final TableColumn COLUMN_NAME_START_DATE = new TableColumn(DataType.INTEGER, "startdate");
        public static final TableColumn COLUMN_NAME_END_DATE = new TableColumn(DataType.INTEGER, "enddate");
        public static final TableColumn COLUMN_NAME_CREDIT_LIMIT = new TableColumn(DataType.TEXT, "creditlimit");
    }

    /* Expense Table *
    public static abstract class ExpenseTable implements BaseColumns {
        public static final String TABLE_NAME = "expense";

        public static final TableColumn COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD = new TableColumn(DataType.INTEGER, "fk_creditperiod");

        public static final TableColumn COLUMN_NAME_DESCRIPTION = new TableColumn(DataType.TEXT, "description");
        public static final TableColumn COLUMN_NAME_THUMBNAIL = new TableColumn(DataType.BLOB, "thumbnail");
        public static final TableColumn COLUMN_NAME_FULL_IMAGE_PATH = new TableColumn(DataType.TEXT, "fullimagepath");
        public static final TableColumn COLUMN_NAME_AMOUNT = new TableColumn(DataType.TEXT, "amount");
        public static final TableColumn COLUMN_NAME_CURRENCY = new TableColumn(DataType.TEXT, "currency");
        public static final TableColumn COLUMN_NAME_DATE = new TableColumn(DataType.INTEGER, "date");
        public static final TableColumn COLUMN_NAME_EXPENSE_CATEGORY = new TableColumn(DataType.TEXT, "expensecategory");
        public static final TableColumn COLUMN_NAME_EXPENSE_TYPE = new TableColumn(DataType.TEXT, "expensetype");
    }*/

    /* Payment Table */
    public static abstract class PaymentTable implements BaseColumns {
        public static final String TABLE_NAME = "payment";

        public static final TableColumn COLUMN_NAME_FOREIGN_KEY_CREDIT_PERIOD = new TableColumn(DataType.INTEGER, "fk_creditperiod");

        public static final TableColumn COLUMN_NAME_DESCRIPTION = new TableColumn(DataType.TEXT, "description");
        public static final TableColumn COLUMN_NAME_AMOUNT = new TableColumn(DataType.TEXT, "amount");
        public static final TableColumn COLUMN_NAME_CURRENCY = new TableColumn(DataType.TEXT, "currency");
        public static final TableColumn COLUMN_NAME_DATE = new TableColumn(DataType.INTEGER, "date");

    }

    /* CreditCard Table */
    public static abstract class AccountTable implements BaseColumns {
        public static final String TABLE_NAME = "myaccounts";
        //public static final TableColumn COLUMN_NAME_NICK_NAME = new TableColumn(DataType.TEXT, "nickname");
        public static final TableColumn COLUMN_NAME_NICK_NAME = new TableColumn(DataType.TEXT, "nickname");
        public static final TableColumn COLUMN_NAME_BANK_NAME = new TableColumn(DataType.TEXT, "bankname");
        public static final TableColumn COLUMN_NAME_ACCOUNT_NUMBER = new TableColumn(DataType.TEXT, "accnumber");
        public static final TableColumn COLUMN_NAME_CURRENCY = new TableColumn(DataType.TEXT, "currency");
        public static final TableColumn COLUMN_NAME_ACCOUNT_TYPE = new TableColumn(DataType.TEXT, "accounttype");
        public static final TableColumn COLUMN_NAME_BALANCE = new TableColumn(DataType.REAL, "balance");
        public static final TableColumn COLUMN_NAME_BALANCE_UPDATE = new TableColumn(DataType.TEXT, "balanceupdate");
        public static final TableColumn COLUMN_NAME_ACCOUNT_SPARE1 = new TableColumn(DataType.TEXT, "spare1");
        public static final TableColumn COLUMN_NAME_ACCOUNT_SPARE2 = new TableColumn(DataType.TEXT, "spare2");
        public static final TableColumn COLUMN_NAME_ACCOUNT_SPARE3 = new TableColumn(DataType.TEXT, "spare3");
        public static final TableColumn COLUMN_NAME_ACCOUNT_SPARE4 = new TableColumn(DataType.TEXT, "spare4");
        public static final TableColumn COLUMN_NAME_ACCOUNT_SPARE5 = new TableColumn(DataType.TEXT, "spare5");
        //public static final TableColumn COLUMN_NAME_CLOSING_DAY = new TableColumn(DataType.TEXT, "closingday");
        //public static final TableColumn COLUMN_NAME_DUE_DAY = new TableColumn(DataType.TEXT, "dueday");
        //public static final TableColumn COLUMN_NAME_BACKGROUND = new TableColumn(DataType.TEXT, "background");
    }

    /* CreditCard Table */
    public static abstract class TransactionTable implements BaseColumns {
        public static final String TABLE_NAME = "mytransactions";

        public static final TableColumn COLUMN_NAME_FOREIGN_KEY_GIVER = new TableColumn(DataType.INTEGER, "fk_giver");
        public static final TableColumn COLUMN_NAME_FOREIGN_KEY_RECIEVER = new TableColumn(DataType.INTEGER, "fk_reciever");
        public static final TableColumn COLUMN_NAME_DESCRIPTION = new TableColumn(DataType.TEXT, "description");
        public static final TableColumn COLUMN_NAME_THUMBNAIL = new TableColumn(DataType.BLOB, "thumbnail");
        public static final TableColumn COLUMN_NAME_FULL_IMAGE_PATH = new TableColumn(DataType.TEXT, "fullimagepath");
        public static final TableColumn COLUMN_NAME_AMOUNT = new TableColumn(DataType.REAL, "amount");
        public static final TableColumn COLUMN_NAME_CURRENCY = new TableColumn(DataType.TEXT, "currency");
        public static final TableColumn COLUMN_NAME_DATE = new TableColumn(DataType.INTEGER, "date");
        public static final TableColumn COLUMN_NAME_TRANSACTION_CATEGORY = new TableColumn(DataType.TEXT, "transactioncategory");
        public static final TableColumn COLUMN_NAME_TRANSACTION_TYPE = new TableColumn(DataType.TEXT, "transactiontype");
        public static final TableColumn COLUMN_NAME_TRANSACTION_SPARE1 = new TableColumn(DataType.TEXT, "spare1");
        public static final TableColumn COLUMN_NAME_TRANSACTION_SPARE2 = new TableColumn(DataType.TEXT, "spare2");
        public static final TableColumn COLUMN_NAME_TRANSACTION_SPARE3 = new TableColumn(DataType.TEXT, "spare3");
        public static final TableColumn COLUMN_NAME_TRANSACTION_SPARE4 = new TableColumn(DataType.TEXT, "spare4");
        public static final TableColumn COLUMN_NAME_TRANSACTION_SPARE5 = new TableColumn(DataType.TEXT, "spare5");

    }

    /* CreditCard Table */
    public static abstract class TransactionCategoryTable implements BaseColumns {
        public static final String TABLE_NAME = "transactionCategory";
        public static final TableColumn COLUMN_NAME_DESCRIPTION = new TableColumn(DataType.TEXT, "description");
        public static final TableColumn COLUMN_NAME_TRASACTION_TYPE = new TableColumn(DataType.TEXT, "transactiontype");
        public static final TableColumn COLUMN_NAME_BUDGET = new TableColumn(DataType.REAL, "budget");
        public static final TableColumn COLUMN_NAME_TRANSACTION_CATEGORY_SPARE1 = new TableColumn(DataType.TEXT, "spare1");
        public static final TableColumn COLUMN_NAME_TRANSACTION_CATEGORY_SPARE2 = new TableColumn(DataType.TEXT, "spare2");
        public static final TableColumn COLUMN_NAME_TRANSACTION_CATEGORY_SPARE3 = new TableColumn(DataType.TEXT, "spare3");
        public static final TableColumn COLUMN_NAME_TRANSACTION_CATEGORY_SPARE4 = new TableColumn(DataType.TEXT, "spare4");
        public static final TableColumn COLUMN_NAME_TRANSACTION_CATEGORY_SPARE5 = new TableColumn(DataType.TEXT, "spare5");
    }


}
