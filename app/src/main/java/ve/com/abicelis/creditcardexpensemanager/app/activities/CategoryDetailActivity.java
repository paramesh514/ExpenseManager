package ve.com.abicelis.creditcardexpensemanager.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.dialogs.CreateOrEditCategoryDialogFragment;
import ve.com.abicelis.creditcardexpensemanager.app.utils.Constants;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotDeleteDataException;
import ve.com.abicelis.creditcardexpensemanager.model.TransactionCategory;

/**
 * Created by Alex on 19/8/2016.
 */
public class CategoryDetailActivity extends AppCompatActivity implements  View.OnClickListener {

    public static final String INTENT_EXTRAS_CATEGORY = "INTENT_EXTRAS_CATEGORY";
    //public static final String INTENT_EXTRAS_CREDIT_PERIOD_ID = "INTENT_EXTRAS_CREDIT_PERIOD_ID";

    //UI
    private Toolbar mToolbar;
    private ImageView mImage;
    private TextView mAmount;
    private TextView mDescription;
    //private TextView mDate;
    //private TextView mCategory;
    private TextView mType;
    //private Button mEdit;
    //private Button mDelete;

    //DATA
    private TransactionCategory transactionCategory;
    //private int mCreditPeriodId;
    private boolean expenseEdited = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //Enable Lollipop Material Design transitions
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);


        //Try to get the expense and the periodId, sent from the caller activity
        try {
            transactionCategory = (TransactionCategory) getIntent().getSerializableExtra(INTENT_EXTRAS_CATEGORY);
            //mCreditPeriodId = getIntent().getIntExtra(INTENT_EXTRAS_CREDIT_PERIOD_ID, -1);
        } catch (Exception e) {
            transactionCategory = null;
        }

        mToolbar = (Toolbar) findViewById(R.id.category_detail_toolbar);
        mImage = (ImageView) findViewById(R.id.category_detail_image);
        mAmount = (TextView) findViewById(R.id.category_detail_budget);
        mDescription = (TextView) findViewById(R.id.category_detail_name);
        //mDate = (TextView) findViewById(R.id.expense_detail_date);
        //mCategory = (TextView) findViewById(R.id.expense_detail_category);
        mType = (TextView) findViewById(R.id.category_detail_type);
        //mEdit = (Button) findViewById(R.id.expense_detail_btn_edit);
        //mDelete = (Button) findViewById(R.id.expense_detail_btn_delete);
//        mDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                handleOnDelete();
//            }
//        });
//
//
//        mEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                handleOnEdit();
//            }
//        });

        setUpToolbar();
        setUpCategoryDetails();
    }


    private void setUpToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getResources().getString(R.string.activity_expense_detail_title));
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setUpCategoryDetails() {
        mAmount.setText(String.valueOf(transactionCategory.getBudget()));
        mDescription.setText(transactionCategory.getName());
        //mDate.setText(DateUtils.getShortDateString(transactionCategory.getDate()) + "\r\n" + DateUtils.getRelativeTimeSpanString(transactionCategory.getDate()));

        //this.mCategory.setText(transactionCategory.getExpenseCategory().getName());
        //((GradientDrawable)this.mCategory.getBackground()).setColor(ContextCompat.getColor(this, transactionCategory.getExpenseCategory().getColor()));

        this.mType.setText(transactionCategory.getType().getFriendlyName());
        //((GradientDrawable)this.mType.getBackground()).setColor(ContextCompat.getColor(this, transactionCategory.getExpenseType().getColor()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(expenseEdited)
            setResult(Constants.RESULT_REFRESH_DATA);

        supportFinishAfterTransition();     //When user backs out, transition back!
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_expense_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.expense_detail_menu_edit:
                handleOnEdit();
                break;
            case R.id.expense_detail_menu_delete:
                handleOnDelete();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
     ///   if(i == R.id.expense_detail_image) {

   //         ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, mImage, getResources().getString(R.string.transition_name_expense_detail_image));
   //         Intent imageViewerIntent = new Intent(this, ImageViewerActivity.class);
//            imageViewerIntent.putExtra("imagePath", transactionCategory.getFullImagePath());
 //           startActivity(imageViewerIntent, options.toBundle());
        ///}
    }


    private void handleOnDelete() {
        AlertDialog dialog = new AlertDialog.Builder(CategoryDetailActivity.this)
                .setTitle(R.string.dialog_delete_expense_title)
                .setMessage(R.string.dialog_delete_expense_message)
                .setPositiveButton(R.string.dialog_delete_expense_button_yes,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            new ExpenseManagerDAO(CategoryDetailActivity.this).deleteExpense(transactionCategory.getId());
                            setResult(Constants.RESULT_REFRESH_DATA);
                            onBackPressed();
                        } catch(CouldNotDeleteDataException e) {
                            Toast.makeText(CategoryDetailActivity.this, getResources().getString(R.string.activity_expense_detail_error_cant_delete_expense), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialog_delete_expense_button_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void handleOnEdit() {
        FragmentManager fm = getSupportFragmentManager();
        CreateOrEditCategoryDialogFragment dialog = CreateOrEditCategoryDialogFragment.newInstance(
                new ExpenseManagerDAO(CategoryDetailActivity.this),
                transactionCategory);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                try {
                    transactionCategory = new ExpenseManagerDAO(getApplicationContext()).getTransactionCategory(transactionCategory.getId());
                    expenseEdited = true;
                }catch (Exception e) {
                    Toast.makeText(CategoryDetailActivity.this, "Error when updating data", Toast.LENGTH_SHORT).show();
                }
                setUpCategoryDetails();
            }
        });
        dialog.show(fm, "fragment_dialog_edit_expense");
    }
}
