package ve.com.abicelis.creditcardexpensemanager.enums;

/**
 * Created by Alex on 6/8/2016.
 */
public enum AccountType {
    Cash("Cash"),
    Bank("Bank"),
    Wallet("Wallet"),
    CreditCard("CreditCard"),
    Loan("Loan"),
    Investment("Investment"),
    Merchant("Merchant");

    private String mFriendlyName;

    AccountType(String friendlyName) {
        mFriendlyName = friendlyName;
    }

    public String getCode(){
        return this.name();
    }

    public String getFriendlyName() {
        return mFriendlyName;
    }

    @Override
    public String toString() {
        return mFriendlyName;
    }
}
