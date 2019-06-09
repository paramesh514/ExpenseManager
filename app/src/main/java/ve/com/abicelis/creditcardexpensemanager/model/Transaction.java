package ve.com.abicelis.creditcardexpensemanager.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;

import ve.com.abicelis.creditcardexpensemanager.enums.Currency;
import ve.com.abicelis.creditcardexpensemanager.enums.TransactionType;

/**
 * Created by Alex on 6/8/2016.
 */
public class Transaction implements Serializable {

    private int id;
    private int giver;
    private int reciver;
    private String description;
    byte[] thumbnail;
    String fullImagePath;
    BigDecimal amount;
    Currency currency;
    Calendar date;
    TransactionCategory transactionCategory;
    TransactionType transactionType;


    public Transaction(int giver,int reciver,@NonNull String description, @Nullable byte[] thumbnail, @Nullable String fullImagePath, @NonNull BigDecimal amount, @NonNull Currency currency, @NonNull Calendar date, @NonNull TransactionCategory transactionCategory, @NonNull TransactionType transactionType) {
        this.description = description;
        this.fullImagePath = fullImagePath;
        this.amount = amount;
        this.currency = currency;
        this.transactionCategory = transactionCategory;
        this.transactionType = transactionType;
        this.giver = giver;
        this.reciver = reciver;
        this.thumbnail = thumbnail;
        if(this.thumbnail != null && this.thumbnail.equals(new byte[0]) )   //if thumbnail is empty byte array -> null
            this.thumbnail = null;

        this.date = Calendar.getInstance();
        this.date.setTimeZone(date.getTimeZone());
        this.date.setTimeInMillis(date.getTimeInMillis());
    }

    public Transaction(int id, int giver,int reciver,String description, byte[] thumbnail, String fullImagePath, BigDecimal amount, Currency currency, Calendar date, TransactionCategory transactionCategory, TransactionType transactionType) {
        this(giver,reciver,description, thumbnail, fullImagePath, amount, currency, date, transactionCategory, transactionType);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public String getFullImagePath() {
        return fullImagePath;
    }

    public Bitmap getFullImage() throws FileNotFoundException {
        Bitmap fullImage = BitmapFactory.decodeFile(fullImagePath);

        if(fullImage == null)
            throw new FileNotFoundException("This image does not exist, has probably been deleted");

        return fullImage;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Calendar getDate() {
        return date;
    }


    public TransactionCategory getTransactionCategory() {
        return transactionCategory;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public int getGiver() {
        return giver;
    }
    public void setGiver(int giver) {
        this.giver = giver;
    }

    public int getTaker() {
        return  reciver;
    }

    public void setTaker(int taker) {
        this.reciver = taker;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setFullImagePath(String fullImagePath) {
        this.fullImagePath = fullImagePath;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setTransactionCategory(TransactionCategory transactionCategory) {
        this.transactionCategory = transactionCategory;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public String toString() {
        return  "ID=" + id + "\r\n" +
                " description=" + description + "\r\n" +
                " amount=" + amount + "\r\n" +
                " currency=" + currency + "\r\n" +
                " date=" + date + "\r\n" +
                " transactionCategory=" + transactionCategory + "\r\n" +
                " transactionType=" + transactionType;
    }
}
