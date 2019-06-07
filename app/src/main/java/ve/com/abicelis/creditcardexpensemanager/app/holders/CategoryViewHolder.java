package ve.com.abicelis.creditcardexpensemanager.app.holders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.activities.CategoryDetailActivity;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.CategoryAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.utils.Constants;
import ve.com.abicelis.creditcardexpensemanager.app.views.HorizontalBar;
import ve.com.abicelis.creditcardexpensemanager.model.TransactionCategory;

/**
 * Created by Alex on 7/8/2016.
 */
public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public CategoryAdapter mAdapter;
    private Fragment mFragment;
    private CategoryDeletedListener mListener = null;

    //UI
    private RelativeLayout mContainer;
    private TextView mBudget;
    private TextView mSpent;
    private TextView mCategoryName;
    HorizontalBar creditBalanceBar;

    //private TextView mDate;
    private ImageView mImage;
    //private TextView mCategory;
    private TextView mType;
    private ImageView mDeleteIcon;

    //DATA
    private TransactionCategory mCurrent;
    //private int mCreditPeriodId;
    private int mCategoryPosition;

    public CategoryViewHolder(View itemView) {
        super(itemView);

        mContainer = (RelativeLayout) itemView.findViewById(R.id.list_item_categories_container);
        mBudget = (TextView) itemView.findViewById(R.id.list_item_category_budget);
        mSpent = (TextView) itemView.findViewById(R.id.list_item_category_spent);
        mCategoryName = (TextView) itemView.findViewById(R.id.list_item_category_name);
        //mDate = (TextView) itemView.findViewById(R.id.list_item_expenses_txt_date);
        mImage = (ImageView) itemView.findViewById(R.id.list_item_category_img_image);
        //mCategory = (TextView) itemView.findViewById(R.id.list_item_expenses_txt_category);
        mType = (TextView) itemView.findViewById(R.id.list_item_category_transaction_type);
        mDeleteIcon = (ImageView) itemView.findViewById(R.id.list_item_category_img_delete);
        creditBalanceBar = (HorizontalBar) itemView.findViewById(R.id.frag_category_buget_bar);

    }

    public void setData(CategoryAdapter adapter, Fragment fragment, TransactionCategory current, int position) {
        mAdapter = adapter;
        mFragment = fragment;
        this.mCurrent = current;
      //  mCreditPeriodId = creditPeriodId;
        this.mCategoryPosition = position;

        this.mBudget.setText(String.valueOf(current.getBudget()));
        this.mSpent.setText(String.valueOf(current.getSpent()));
        this.mCategoryName.setText(current.getName());
        //this.mDate.setText(DateUtils.getRelativeTimeSpanString(current.getDate()));

        //this.mCategory.setText(current.getExpenseCategory().getName());
        //((GradientDrawable)this.mCategory.getBackground()).setColor(ContextCompat.getColor(mFragment.getContext(), current.getExpenseCategory().getColor()));

        this.mType.setText(current.getType().getCode());
        if(current.getBudget()>0) {
            creditBalanceBar.setProgressPercentage((int) ((current.getSpent() * 100.0) / current.getBudget()));
            creditBalanceBar.setTextHi(current.getBudget() + "");
            if (current.getSpent() > 0)
                creditBalanceBar.setTextBar(current.getSpent() + "");
            creditBalanceBar.setTextLo("0 ");
        }
        else
            creditBalanceBar.setVisibility(View.INVISIBLE);

        // ((GradientDrawable)this.mType.getBackground()).setColor(ContextCompat.getColor(mFragment.getContext(), current.getExpenseType().getColor()));


//        if(current.getThumbnail() != null && current.getThumbnail().length > 0)
  //          this.mImage.setImageBitmap(ImageUtils.getBitmap(current.getThumbnail()));
    //    else
      //      this.mImage.setImageResource(R.drawable.icon_expense);

    }

    public void setListeners() {
        mDeleteIcon.setOnClickListener(this);
        //mEditIcon.setOnClickListener(this);
        mContainer.setOnClickListener(this);
    }

    public void setOnCategoryDeletedListener(CategoryDeletedListener listener) {
        mListener = listener;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.list_item_categories_container:
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(mImage, mFragment.getResources().getString(R.string.transition_name_expense_detail_image));
                //pairs[1] = new Pair<View, String>(mBudget,  mActivity.getResources().getString(R.string.transition_name_expense_detail_amount));
                //pairs[2] = new Pair<View, String>(mCategoryName,  mActivity.getResources().getString(R.string.transition_name_expense_detail_description));
                //pairs[3] = new Pair<View, String>(mDate,  mActivity.getResources().getString(R.string.transition_name_expense_detail_date));

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mFragment.getActivity(), pairs);
                Intent categoryDetailIntent = new Intent(mFragment.getActivity(), CategoryDetailActivity.class);
                categoryDetailIntent.putExtra(CategoryDetailActivity.INTENT_EXTRAS_CATEGORY, mCurrent);
                //categoryDetailIntent.putExtra(CategoryDetailActivity.INTENT_EXTRAS_CREDIT_PERIOD_ID, mCreditPeriodId);
                mFragment.startActivityForResult(categoryDetailIntent, Constants.EXPENSE_DETAIL_ACTIVITY_REQUEST_CODE, options.toBundle());

                break;

            case R.id.list_item_category_img_delete:

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                            mListener.OnCategoryDeleted(mCategoryPosition);
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mFragment.getActivity());
                builder.setTitle(R.string.dialog_delete_category_title)
                        .setMessage(R.string.dialog_delete_category_message)
                        .setPositiveButton((R.string.dialog_delete_expense_button_yes), listener)
                        .setNegativeButton((R.string.dialog_delete_expense_button_no), null)
                        .show();
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    public interface CategoryDeletedListener {
        void OnCategoryDeleted(int position);
    }
}
