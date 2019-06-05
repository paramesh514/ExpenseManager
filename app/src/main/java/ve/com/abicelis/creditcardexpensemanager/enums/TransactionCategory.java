package ve.com.abicelis.creditcardexpensemanager.enums;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;

/**
 * Created by Alex on 6/8/2016.
 */
public class TransactionCategory {

    private int mIndex;
    private String mFriendlyName;
    private TransactionType type;
    private int mColor;

    TransactionCategory(int index, String friendlyName, int color) {

        mIndex = index;
        mFriendlyName = friendlyName;
        mColor = color;
    }

    public int getColor() {
        return mColor;
    }

    public String getFriendlyName() {
        return mFriendlyName;
    }

    @Override
    public String toString() {
        return mFriendlyName;
    }

    public int getIndex() {
       return mIndex;
    }

    public static TransactionCategory getByExpenseCategoryId(int expenseCategoryId) {
        List<TransactionCategory> allCatagories= new ArrayList<TransactionCategory>();
        for(TransactionCategory expenseCategory : allCatagories) {
            if(expenseCategory.getIndex() == expenseCategoryId)
                return expenseCategory;
        }
        throw new IllegalArgumentException(String.valueOf(expenseCategoryId));
    }

}
