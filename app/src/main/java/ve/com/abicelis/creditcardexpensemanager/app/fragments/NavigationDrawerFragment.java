package ve.com.abicelis.creditcardexpensemanager.app.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.activities.AddCreditCardActivity;
import ve.com.abicelis.creditcardexpensemanager.app.activities.HomeActivity;
import ve.com.abicelis.creditcardexpensemanager.app.activities.WelcomeActivity;
import ve.com.abicelis.creditcardexpensemanager.app.adapters.NavigationDrawerAdapter;
import ve.com.abicelis.creditcardexpensemanager.app.dialogs.SelectCreditCardDialogFragment;
import ve.com.abicelis.creditcardexpensemanager.app.holders.SelectableCreditCardViewHolder;
import ve.com.abicelis.creditcardexpensemanager.app.utils.Constants;
import ve.com.abicelis.creditcardexpensemanager.app.utils.SharedPreferencesUtils;
import ve.com.abicelis.creditcardexpensemanager.database.ExpenseManagerDAO;
import ve.com.abicelis.creditcardexpensemanager.exceptions.CreditCardNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.exceptions.SharedPreferenceNotFoundException;
import ve.com.abicelis.creditcardexpensemanager.model.CreditCard;
import ve.com.abicelis.creditcardexpensemanager.model.NavigationDrawerItem;


/**
 * Created by Alex on 5/8/2016.
 */
public class NavigationDrawerFragment extends Fragment {

    //DATA
    //int mActiveCreditCardID = -1;
    //private CreditCard mActiveCreditCard = null;
    private ExpenseManagerDAO mDao;

    //UI
    //SelectableCreditCardViewHolder holder;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    //private RelativeLayout mHeaderContainer;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container);

        refreshData();
        setUpRecyclerView(rootView);
        refreshDrawerHeader(rootView);

        return rootView;
    }

    private void refreshData() {
        //Load mDao and data
        if(mDao == null)
            mDao = new ExpenseManagerDAO(getActivity().getApplicationContext());

        //try {
        //    mActiveCreditCardID = SharedPreferencesUtils.getInt(getContext(), Constants.ACTIVE_CC_ID);
        //    mActiveCreditCard = mDao.getCreditCard(mActiveCreditCardID);
        //}catch (SharedPreferenceNotFoundException | CreditCardNotFoundException e) {}
    }


    private void refreshDrawerHeader(View rootView) {

        //mHeaderContainer = (RelativeLayout) rootView.findViewById(R.id.nav_drawer_header_container);
        //View headerCreditCardContainer = rootView.findViewById(R.id.list_item_credit_card_container);
        //View errNoCC = rootView.findViewById(R.id.nav_drawer_err_no_cc);
        //View errSelectACC = rootView.findViewById(R.id.nav_drawer_err_select_a_cc);

        //Hide all
        //headerCreditCardContainer.setVisibility(View.GONE);
        //errNoCC.setVisibility(View.GONE);
        //errSelectACC.setVisibility(View.GONE);


       // if(mActiveCreditCard != null) {                                                                             //There is an active credit card
            //Setup cc holder data
          //  headerCreditCardContainer.setVisibility(View.VISIBLE);
           // holder = new SelectableCreditCardViewHolder(headerCreditCardContainer);
            //holder.setData(getContext(), mActiveCreditCard, 0);
        //} else if (mDao.getCreditCardList().size() > 0) {                                                           //There's no active cc but there are cc's in the db
          //  errSelectACC.setVisibility(View.VISIBLE);
            //Toast.makeText(getContext(), "Click to select a credit card", Toast.LENGTH_SHORT).show();
        //} else {                                                                                                    //No active and no cc's in db
            //errNoCC.setVisibility(View.VISIBLE);


            //Set header onClick
      /*      mHeaderContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent addCCIntent = new Intent(getActivity(), AddCreditCardActivity.class);
                    //addCCIntent.putExtra(AddCreditCardActivity.CAME_FROM_WELCOME_ACTIVITY_INTENT, true);
                    startActivity(addCCIntent);
                }
            });*/

            //Toast.makeText(getContext(), "No credit cards, add a credit card", Toast.LENGTH_SHORT).show();
        //}

        //if(mDao.getCreditCardList().size() > 0) {                                                   //If there's more than one cc, allow selection

            //Set header onClick
            //mHeaderContainer = (RelativeLayout) rootView.findViewById(R.id.nav_drawer_header_container);
            /*mHeaderContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SelectCreditCardDialogFragment dialog = SelectCreditCardDialogFragment.newInstance(mDao.getCreditCardList());
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            refreshData();
                            holder.setData(getContext(), mActiveCreditCard, 0);
                            closeDrawer();
                        }
                    });
                    dialog.show(getFragmentManager(), "fragment_dialog_select_credit_card");
                }
            });*/

        //}

    }

    private void setUpRecyclerView(View view) {

        List<NavigationDrawerItem> data = new ArrayList<>();
        data.add(new NavigationDrawerItem(getResources().getString(R.string.fragment_name_overview), R.drawable.icon_overview));
        data.add(new NavigationDrawerItem(getResources().getString(R.string.fragment_name_expense_list), R.drawable.icon_expenses));
        data.add(new NavigationDrawerItem(getResources().getString(R.string.fragment_name_graphs), R.drawable.icon_chart));
        data.add(new NavigationDrawerItem(getResources().getString(R.string.fragment_name_credit_cards), R.drawable.icon_credit_card));
        data.add(new NavigationDrawerItem(getResources().getString(R.string.fragment_name_preferences), R.drawable.icon_settings));
        data.add(new NavigationDrawerItem(getResources().getString(R.string.fragment_name_about), R.drawable.icon_about));


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.nad_drawer_recyclerview_list);

        NavigationDrawerAdapter adapter = new NavigationDrawerAdapter(this, data);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


    }

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
    }

    public void closeDrawer() {
        if(mDrawerLayout != null)
            mDrawerLayout.closeDrawer(GravityCompat.START);
    }

//
//    public void setUpDrawer(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
//
//        mDrawerLayout = drawerLayout;
//
//        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout,toolbar, R.string.drawer_open, R.string.drawer_closed) {
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//            }
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
//            }
//
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                super.onDrawerSlide(drawerView, slideOffset);
//            }
//        };
//
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
//
//        mDrawerLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                mDrawerToggle.syncState();
//
//            }
//        });
//    }

}
