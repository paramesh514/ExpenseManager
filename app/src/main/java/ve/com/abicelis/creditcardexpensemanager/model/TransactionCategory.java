package ve.com.abicelis.creditcardexpensemanager.model;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.enums.TransactionType;

/**
 * Created by Alex on 6/8/2016.
 */
public class TransactionCategory {

    private int mIndex;
    private String mFriendlyName;
    private TransactionType type;
    private int mColor;
    private float budget;

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

}
