package ve.com.abicelis.creditcardexpensemanager.model;

import ve.com.abicelis.creditcardexpensemanager.enums.TransactionType;

/**
 * Created by Alex on 6/8/2016.
 */
public class TransactionCategory {

    private int mId;
    private String mFriendlyName;
    private TransactionType type;
    private int mColor;
    private float budget;

    public TransactionCategory(int index, String friendlyName,TransactionType type) {

        mId = index;
        mFriendlyName = friendlyName;
        this.type = type;
        mColor = 0;
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

    public int getId() {
       return mId;
    }

    public static TransactionCategory getDefault()
    {
        return new TransactionCategory(0,"Uncatgorized",TransactionType.CORRECTION);
    }

}
