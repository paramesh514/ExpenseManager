package ve.com.abicelis.creditcardexpensemanager.app.holders;

import android.content.Intent;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ve.com.abicelis.creditcardexpensemanager.R;
import ve.com.abicelis.creditcardexpensemanager.app.activities.AppPreferenceActivity;
import ve.com.abicelis.creditcardexpensemanager.app.activities.OcrCreateExpenseActivity;
import ve.com.abicelis.creditcardexpensemanager.app.fragments.AboutFragment;
import ve.com.abicelis.creditcardexpensemanager.app.fragments.AppPreferenceFragment;
import ve.com.abicelis.creditcardexpensemanager.app.fragments.BudgetListFragment;
import ve.com.abicelis.creditcardexpensemanager.app.fragments.CreditCardListFragment;
import ve.com.abicelis.creditcardexpensemanager.app.fragments.ChartsFragment;
import ve.com.abicelis.creditcardexpensemanager.app.fragments.NavigationDrawerFragment;
import ve.com.abicelis.creditcardexpensemanager.app.fragments.ExpenseListFragment;
import ve.com.abicelis.creditcardexpensemanager.app.fragments.OverviewFragment;
import ve.com.abicelis.creditcardexpensemanager.model.NavigationDrawerItem;


/**
 * Created by Alex on 5/8/2016.
 */
public class NavigationDrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView title;
    private ImageView icon;
    private LinearLayout container;

    private NavigationDrawerItem current;
    private Fragment fragment;
    private int position;

    public NavigationDrawerViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.txt_title_nav_drawer);
        icon = (ImageView) itemView.findViewById(R.id.img_icon_nav_drawer);
        container = (LinearLayout) itemView.findViewById(R.id.container_item_nav_drawer);
    }

    public void setData(Fragment fragment, NavigationDrawerItem current, int position) {
        this.fragment = fragment;
        this.current = current;
        this.position = position;

        this.title.setText(current.getTitle());
        this.icon.setImageResource(current.getIconID());
    }

    public void setListeners() {
        container.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id) {
            case R.id.container_item_nav_drawer:
                FragmentManager fm = fragment.getFragmentManager();
                Fragment f = null;
                switch (position) {
                    case 0:
                        f = new OverviewFragment();
                        break;
                    case 1:
                        f = new ExpenseListFragment();
                        break;
                    case 2:
                        f = new ChartsFragment();
                        break;
                    case 3:
                        f = new BudgetListFragment();
                        break;
                    case 4:
                        f = new CreditCardListFragment();
                        break;
                    case 5:
                        Intent intent = new Intent(fragment.getActivity(), AppPreferenceActivity.class);
                        fragment.getActivity().startActivity(intent);
                        break;
                    case 6:
                        f = new AboutFragment();
                        break;
                    default:
                        Toast.makeText(fragment.getActivity(), title.getText().toString() + " is under construction :)", Toast.LENGTH_SHORT).show();
                        break;
                }

                if(f != null) {
                    fm.beginTransaction().replace(R.id.home_content_frame, f).commit();

                    //TODO: kind of a hack..
                    ((NavigationDrawerFragment)fragment).closeDrawer();
                }
                break;
        }
    }

}
