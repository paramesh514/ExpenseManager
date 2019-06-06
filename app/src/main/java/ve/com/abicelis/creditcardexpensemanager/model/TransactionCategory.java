package ve.com.abicelis.creditcardexpensemanager.model;

import java.io.Serializable;

import ve.com.abicelis.creditcardexpensemanager.enums.TransactionType;

/**
 * Created by Alex on 6/8/2016.
 */
public class TransactionCategory implements Serializable {

    private int mId;
    private String mName;
    private TransactionType type;
    private int mColor;
    private double budget;

    public TransactionCategory(String friendlyName,TransactionType type,double budget) {

        mId = 0;
        mName = friendlyName;
        this.type = type;
        mColor = 0;
        this.budget = budget;
    }
    public TransactionCategory(int index, String friendlyName,TransactionType type,double budget) {

        mId = index;
        mName = friendlyName;
        this.type = type;
        mColor = 0;
        this.budget = budget;
    }

    public int getColor() {
        return mColor;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }

    public int getId() {
       return mId;
    }

    public static TransactionCategory getDefault()
    {
        return new TransactionCategory(0,"Uncatgorized",TransactionType.CORRECTION,0.0);
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
}
