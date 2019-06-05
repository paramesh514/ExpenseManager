package ve.com.abicelis.creditcardexpensemanager.model;

import ve.com.abicelis.creditcardexpensemanager.R;

/**
 * Created by Alex on 6/8/2016.
 */
public enum AccountCategory {

    FOOD(0, "Food", R.color.expense_category_1),
    LEISURE(1, "Leisure", R.color.expense_category_2),
    ENTERTAINMENT(2, "Entertainment", R.color.expense_category_3),
    CLOTHING(3, "Clothing", R.color.expense_category_4),
    EDUCATION(4, "Education", R.color.expense_category_5);



    private int mIndex;
    private String mFriendlyName;
    private int mColor;

    AccountCategory(int index, String friendlyName, int color) {

        mIndex = index;
        mFriendlyName = friendlyName;
        mColor = color;
    }


    public String getCode(){
        return this.name();
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

    public static AccountCategory getByExpenseCategoryId(int expenseCategoryId) {

        for(AccountCategory expenseCategory : AccountCategory.values()) {
            if(expenseCategory.getIndex() == expenseCategoryId)
                return expenseCategory;
        }
        throw new IllegalArgumentException(String.valueOf(expenseCategoryId));
    }

}
