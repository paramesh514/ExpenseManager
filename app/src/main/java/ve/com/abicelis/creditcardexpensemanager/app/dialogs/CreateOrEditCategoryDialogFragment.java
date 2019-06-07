package ve.com.abicelis.creditcardexpensemanager.app.dialogs;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.activities.ImageCropperActivity;
import ve.com.abicelis.creditcardexpensemanager.app.utils.PermissionUtils;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.enums.TransactionType;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotInsertDataException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CouldNotUpdateDataException;
import ve.com.abicelis.creditcardexpensemanager.model.TransactionCategory;

/**
 * Created by Alex on 9/8/2016.
 */
public class CreateOrEditCategoryDialogFragment extends AppCompatDialogFragment implements View.OnClickListener {

    //Constants
    private static final int RC_HANDLE_CAMERA_PERM = 2;                 // Permission request codes need to be < 256
    private static final String TAG_ARGS_TITLE = "tagArgsTitle";
    private static final String TAG_ARGS_PERIOD_ID = "tagArgsPeriodId";
    private static final String TAG_ARGS_CURRENCY = "tagArgsCurrency";
    private static final String TAG_ARGS_ORIGINAL_CATEGORY = "tagArgsOriginalCategory";
    private static final String TAG = "CreateExpenseDialogFrag";
    private static final int REQUEST_IMAGE_CAPTURE = 123;
    private static final int REQUEST_IMAGE_CROP = 124;

    //DB
    private ExpenseManagerDAO mDao;

    //UI
    private DialogInterface.OnDismissListener mOnDismissListener;
    private EditText mAmountText;
    private EditText mDescriptionText;
    //private Spinner mExpenseCategory;
    private Spinner mExpenseType;
    private Button mCancelButton;
    private Button mCreateButton;
    //private ImageView mImage;

    //DATA
    private TransactionCategory mOriginalExpense = null;
   //
   // int mCreditPeriodId = -1;
    //Currency mCurrency = null;
    //byte[] expenseImageThumbnailBytes;
    List<TransactionCategory> expenseCategories;
    List<TransactionType> expenseTypes;

    //private Uri imageUri;
    //private String imagePath = null;
    //private String tempImagePath = null;

    public CreateOrEditCategoryDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static CreateOrEditCategoryDialogFragment newInstance(ExpenseManagerDAO dao, @Nullable TransactionCategory originalExpense) {
        CreateOrEditCategoryDialogFragment frag = new CreateOrEditCategoryDialogFragment();
        Bundle args = new Bundle();
        //args.putInt(TAG_ARGS_PERIOD_ID, creditPeriodId);
        //args.putSerializable(TAG_ARGS_CURRENCY, currency);
        if(originalExpense != null)
            args.putSerializable(TAG_ARGS_ORIGINAL_CATEGORY, originalExpense);
        frag.setArguments(args);
        frag.setDao(dao);
        return frag;
    }

    public void setDao(ExpenseManagerDAO dao) {
        mDao = dao;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setCancelable(false);
        return inflater.inflate(R.layout.dialog_create_or_edit_category, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().setTitle(R.string.dialog_create_expense_title);

        // Fetch arguments from bundle and set title
        //mCurrency = (Currency) getArguments().getSerializable(TAG_ARGS_CURRENCY);
        mOriginalExpense = (TransactionCategory) getArguments().getSerializable(TAG_ARGS_ORIGINAL_CATEGORY);
        //mCreditPeriodId =  getArguments().getInt(TAG_ARGS_PERIOD_ID, -1);

       // if(mCreditPeriodId == -1 || mCurrency == null) {
         //   Toast.makeText(getActivity(), "Error, wrong arguments passed. Dismissing dialog.", Toast.LENGTH_SHORT).show();
         //   dismiss();
        //}

        // Get fields from view
        mAmountText = (EditText) view.findViewById(R.id.dialog_create_category_budget);
        mDescriptionText = (EditText) view.findViewById(R.id.dialog_create_category_name);
        //mExpenseCategory = (Spinner) view.findViewById(R.id.dialog_create_expense_category);
        mExpenseType = (Spinner) view.findViewById(R.id.dialog_create_category_type);
        mCancelButton = (Button) view.findViewById(R.id.dialog_create_expense_button_cancel);
        mCreateButton = (Button) view.findViewById(R.id.dialog_create_expense_button_create);
        //mImage = (ImageView) view.findViewById(R.id.dialog_create_expense_image);


        //Limit mAmount
       // mAmountText.setFilters(new InputFilter[]{new NumberInputFilter(9, 2)});


        // Set onClick listeners
        mCancelButton.setOnClickListener(this);
        mCreateButton.setOnClickListener(this);
        //mImage.setOnClickListener(this);

        //Set spinners
        //expenseCategories = mDao.getTransactionCategoryList();
        //ArrayAdapter expenseCategoryAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, expenseCategories);
        //expenseCategoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        //mExpenseCategory.setAdapter(expenseCategoryAdapter);

        expenseTypes = new ArrayList<>(Arrays.asList(TransactionType.values()));
        ArrayAdapter expenseTypeAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, expenseTypes);
        expenseTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mExpenseType.setAdapter(expenseTypeAdapter);

        //If editing an existing expense, set its values
        if(mOriginalExpense != null)
            setOriginalExpenseValues();

        // Show soft keyboard automatically and request focus to field
       // mAmountText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void setOriginalExpenseValues() {
        mAmountText.setText(String.valueOf(mOriginalExpense.getBudget()));
        mDescriptionText.setText(mOriginalExpense.getName());
    //    mExpenseCategory.setSelection(expenseCategories.indexOf(mOriginalExpense.getExpenseCategory()));
        mExpenseType.setSelection(expenseTypes.indexOf(mOriginalExpense.getType()));
        mCreateButton.setText(R.string.dialog_create_expense_button_edit);
        getDialog().setTitle("Edit Category");

    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id) {
            case R.id.dialog_create_expense_image:
                handleExpenseImageCapture();
                break;

            case R.id.dialog_create_expense_button_create:
                handleNewExpenseCreation();
                break;

            case R.id.dialog_create_expense_button_cancel:
                this.dismiss();
                break;

        }
    }

    private void handleExpenseImageCapture() {
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        String[] nonGrantedPermissions;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            nonGrantedPermissions = PermissionUtils.checkIfPermissionsAreGranted(getContext(), Manifest.permission.CAMERA);
        else
            nonGrantedPermissions = PermissionUtils.checkIfPermissionsAreGranted(getContext(), Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }

    @NonNull
    private File getExpensesDir() {
        return new File(getActivity().getExternalFilesDir(null), "expenses/");
    }

    private void createDirIfNotExists(File directory) throws IOException, SecurityException  {
        if (directory.mkdir()){
            File nomedia = new File(directory, ".nomedia");
            nomedia.createNewFile();
        }
    }

    private File createImageFileInDir(File directory) throws IOException, SecurityException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "EXP_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName, ".jpg", directory);

        return image;
    }

    private File createTempImageFileInDir(File directory) throws IOException, SecurityException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEMP_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName, ".jpg", directory);

        return image;
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        //Check if all permissions have been granted
        if (grantResults.length != 0) {
            boolean permissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted = false;
                    break;
                }
            }

            if(permissionsGranted) {
                Log.d(TAG, "Camera permission granted - initialize the camera source");
                // We have permission, so launch camera
    //            dispatchTakePictureIntent();
                return;
            }
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        };


        //TODO: fix this message
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {

            try {
                //Go to the imageCropper activity
                Intent cropImageIntent = new Intent(getActivity(), ImageCropperActivity.class);
      //          cropImageIntent.putExtra(ImageCropperActivity.IMAGE_PATH, tempImagePath);
                startActivityForResult(cropImageIntent, REQUEST_IMAGE_CROP);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestCode == REQUEST_IMAGE_CROP && resultCode == Activity.RESULT_OK) {

            try {
        //        Bitmap expenseImageThumbnail = BitmapFactory.decodeFile(tempImagePath);
          //      expenseImageThumbnail = ImageUtils.scaleBitmap(expenseImageThumbnail, 120);
            //    expenseImageThumbnailBytes = ImageUtils.toByteArray(expenseImageThumbnail);

            } catch (Exception e) {
                Toast.makeText(getActivity(), "Peos!", Toast.LENGTH_SHORT).show();
            }
            ///mImage.setImageBitmap(ImageUtils.getBitmap(expenseImageThumbnailBytes));

        } else {
            Toast.makeText(getContext(), "OnActivityResult but reqCode =" +requestCode + " and resCode=" + resultCode, Toast.LENGTH_SHORT).show();

        }
    }


    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(dialog);
        }
    }


    private void handleNewExpenseCreation() {

        String amount = mAmountText.getText().toString();
        double amt = 0.0;
        try
        {
            amt = Double.parseDouble(amount);
        }
        catch (Exception e)
        {

        }
        String description = mDescriptionText.getText().toString();
        if(amount.equals("") || new BigDecimal(amount).compareTo(new BigDecimal(0)) == 0) {
            Toast.makeText(getContext(), getResources().getString(R.string.dialog_create_expense_error_bad_amount), Toast.LENGTH_SHORT).show();
            return;
        }

        //TransactionCategory expenseCategory = expenseCategories.get(mExpenseCategory.getSelectedItemPosition());
        TransactionType expenseType = expenseTypes.get(mExpenseType.getSelectedItemPosition());


        //TODO: Allow adding expenses on a specific date inside the current creditPeriod, not just time=now
        //If editing an expense, update it
        if(mOriginalExpense != null) {
            try {
                TransactionCategory expense = new TransactionCategory(mOriginalExpense.getId(), description, expenseType,amt,mOriginalExpense.getSpent());
                mDao.updateTransactionCategory(expense);
            } catch (CouldNotUpdateDataException e) {
                Toast.makeText(getActivity(), "There was a problem updating the Expense", Toast.LENGTH_SHORT).show();
            }
        } else {
            //Otherwise, insert a new expense
            try {
                TransactionCategory expense = new TransactionCategory(description, expenseType,amt,0.0);
                mDao.insertTransactionCategory(expense);
            } catch (CouldNotInsertDataException e) {
                Toast.makeText(getActivity(), "There was a problem inserting the Expense", Toast.LENGTH_SHORT).show();
            }
        }

        dismiss();
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }


}
