package rs.pstech.bozovic.nemanja.contacts;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

public class ContactListActivity extends Activity implements ContactListFragment.OnContactSelectedListener {

    private enum FragmentTypeShowing {
        LIST, CONTACT, BOTH
    }

    public static final String CLASS_NAME = ContactListActivity.class.getSimpleName();
    public static final String CALLER = "caller";

    public static final int REQUEST_CODE = 1;

    private static final String EXTRA_ID = "id";
    private static final String EXTRA_POS = "pos";
    private static final String EXTRA_PREVIOUS_FRAGMENT_SHOWED = "which fragment was last showed";

    private String mCurrentShowingContactId = null;
    private int mCurrentShowingContactPositionInList = -1;

    private ContactListFragment mContactListFragment; //instance of ListFragment
    private ContactFragment mContactFragment;         //instance of ContactFragment

    private FragmentTypeShowing mCurrentFragmentTypeShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(CLASS_NAME, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        FragmentManager fm = getFragmentManager();

        //we need to get references to fragments
        switch (getOrientation()) {
            case Configuration.ORIENTATION_PORTRAIT:

                mCurrentFragmentTypeShowing = FragmentTypeShowing.LIST;
                mContactListFragment = (ContactListFragment) fm.findFragmentById(R.id.fragment_container_portrait);
                //now the ListFragment is assigned, we should check if we need to show ContactFragment
                //on top of ListFragment
                //we'll do that in onRestoreInstantState

                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                mCurrentFragmentTypeShowing = FragmentTypeShowing.BOTH;
                //fragment are inflated from XML
                mContactFragment = (ContactFragment) fm.findFragmentById(R.id.fragment_contact_container_landscape);
                mContactListFragment = (ContactListFragment) fm.findFragmentById(R.id.fragment_list_container_landscape);
                break;
        }


    }

    @Override
    protected void onStart() {
        Log.d(CLASS_NAME, "onStart");
        super.onStart();

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Log.d(getClass().getSimpleName(), "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);

        //savedInstanceState is NEVER NULL;

        mCurrentShowingContactId = savedInstanceState.getString(EXTRA_ID);
        mCurrentShowingContactPositionInList = savedInstanceState.getInt(EXTRA_POS);

        //this returns int so we have to `cast` it to enum
        int i = savedInstanceState.getInt(EXTRA_PREVIOUS_FRAGMENT_SHOWED);
        FragmentTypeShowing previousFragmentTypeShowing = FragmentTypeShowing.values()[i];


        if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
            if (previousFragmentTypeShowing == FragmentTypeShowing.CONTACT
                    || previousFragmentTypeShowing == FragmentTypeShowing.BOTH) {
                //if current state is PORTRAIT and previous FragmentType showing was CONTACT_FRAGMENT
                // or BOTH
                //we need to replace current fragment(ListFragment)
                //and change mCurrentFragmentTypeShowing to CONTACT_FRAGMENT

                mCurrentFragmentTypeShowing = FragmentTypeShowing.CONTACT;
            }
        }
        if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE && previousFragmentTypeShowing == FragmentTypeShowing.CONTACT) {
            //if we are now in landscape and previous fragment was ContactFragment
            //we need to select some contact in list and `tell` ContactFragment to show that contact
            //mCurrentFragmentTypeShowing is already set

            //TODO Select current contact in list
            mContactListFragment.selectItem(mCurrentShowingContactPositionInList);
            mCurrentFragmentTypeShowing = FragmentTypeShowing.BOTH;

        }
        onContactSelected(mCurrentShowingContactPositionInList, mCurrentShowingContactId);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_ID, mCurrentShowingContactId);
        outState.putInt(EXTRA_POS, mCurrentShowingContactPositionInList);
        outState.putInt(EXTRA_PREVIOUS_FRAGMENT_SHOWED, mCurrentFragmentTypeShowing.ordinal());

    }



    @Override
    public void onContactSelected(int position, String contact_id) {
        Log.d(CLASS_NAME, "onContactSelected" + "( " + position + ", " + contact_id + " )");
        //start activity if portrait
        //or just change data in ContactAcitvityFragment if landscape

        if (position == -1 || contact_id == null)
            return;

        mCurrentShowingContactId = contact_id; //we need this because this function can be called from ListFragment
        mCurrentShowingContactPositionInList = position; //this too

        //arguments to send to fragment
        Bundle args = new Bundle();
        args.putString(ContactFragment.ARG_CONTACT_ID, contact_id);
        args.putInt(ContactFragment.ARG_CONTACT_POSITION, position);

        Log.d(getClass().getSimpleName(), "Number of backStack entries" + getFragmentManager().getBackStackEntryCount());


        if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
            //make ContactFragment and replace current one
            //dont forget to change mCurrentFragmentTypeShowing

            mCurrentFragmentTypeShowing = FragmentTypeShowing.CONTACT;

            Intent startContactActivity = new Intent(this, ContactActivity.class);
            startContactActivity.putExtra(ContactActivity.ARG_CONTACT_ID, contact_id);
            startContactActivity.putExtra(ContactActivity.ARG_POSITION, position);

            startActivity(startContactActivity);

        } else if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            //LANDSCAPE mode
            //find fragment (from xml, already inflated)
            //just call method to update view

            mContactFragment.updateContactView(contact_id);

            mCurrentFragmentTypeShowing = FragmentTypeShowing.BOTH;
        }

    }


    private int getOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            return Configuration.ORIENTATION_PORTRAIT;
        else
            return Configuration.ORIENTATION_LANDSCAPE;
    }
}
