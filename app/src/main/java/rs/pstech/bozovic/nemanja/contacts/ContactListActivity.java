package rs.pstech.bozovic.nemanja.contacts;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

public class ContactListActivity extends Activity implements ContactListFragment.OnContactSelectedListener {

    private enum FragmentTypeShowing {
        CONTACT_LIST_FRAGMENT,
        CONTACT_FRAGMENT,
        BOTH
    }

    public static final String CLASS_NAME = ContactListActivity.class.getSimpleName();
    public static final String CALLER = "caller";

    public static final int REQUEST_CODE = 1;

    private static final String EXTRA_ID = "id";
    private static final String EXTRA_POS = "pos";
    private static final String EXTRA_PREVIOUS_FRAGMENT_SHOWED = "which fragment was last showed";

    private static final String TAG_LIST_FRAGMENT = "list fragment";
    private static final String TAG_CONTACT_FRAGMENT = "contact fragment";

    private String mCurrentShowingContactId = null;
    private int mCurrentShowingContactPositionInList = -1;

    private ContactListFragment mContactListFragment; //instance of ListFragment
    private ContactFragment mContactFragment;         //instance of ContactFragment

    private FragmentTypeShowing mCurrentFragmentTypeShowing; //used to track currently showing fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(CLASS_NAME, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        FragmentManager fm = getFragmentManager();

        mContactListFragment = new ContactListFragment();
        mContactFragment = new ContactFragment();

        FragmentTransaction transaction = fm.beginTransaction();

        switch (getOrientation()) {
            case Configuration.ORIENTATION_PORTRAIT:

                transaction.add(R.id.fragment_container_portrait, mContactListFragment, TAG_LIST_FRAGMENT);
                transaction.commit();
                //now the ListFragment is assigned, we should check if we need to show ContactFragment
                //on top of ListFragment
                //we'll do that in onRestoreInstantState

                //we assign this, but it may be reassigned in onRestoreIntance
                mCurrentFragmentTypeShowing = FragmentTypeShowing.CONTACT_LIST_FRAGMENT;

                break;
            case Configuration.ORIENTATION_LANDSCAPE:

                transaction.add(R.id.fragment_list_container_landscape, mContactListFragment, TAG_LIST_FRAGMENT);
                //ListFragment exist now
                //We should just start ContactFragment
                //MAYBE we could pass some parameters so we show FIRST contact when app first time started

                transaction.add(R.id.fragment_contact_container_landscape, mContactFragment, TAG_CONTACT_FRAGMENT);

                transaction.commit();

                mCurrentFragmentTypeShowing = FragmentTypeShowing.BOTH;
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
        FragmentTypeShowing previuousFragmentTypeShowing = FragmentTypeShowing.values()[i];

        if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
            if (previuousFragmentTypeShowing == FragmentTypeShowing.CONTACT_FRAGMENT
                    || previuousFragmentTypeShowing == FragmentTypeShowing.BOTH) {
                //if current state is PORTRAIT and previous FragmentType showing was CONTACT_FRAGMENT
                // or BOTH
                //we need to replace current fragment(ListFragment)
                //and change mCurrentFragmentTypeShowing to CONTACT_FRAGMENT

                Bundle args = new Bundle();
                args.putString(ContactFragment.ARG_CONTACT_ID, mCurrentShowingContactId);
                args.putInt(ContactFragment.ARG_CONTACT_POSITION, mCurrentShowingContactPositionInList);
                mContactFragment.setArguments(args);

                getFragmentManager().beginTransaction().replace(R.id.fragment_container_portrait, mContactFragment, TAG_CONTACT_FRAGMENT).addToBackStack(null).commit();

                mCurrentFragmentTypeShowing = FragmentTypeShowing.CONTACT_FRAGMENT;
            }
        }
        if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE && previuousFragmentTypeShowing == FragmentTypeShowing.CONTACT_FRAGMENT) {
            //if we are now in landscape and previous fragment was ContactFragment
            //we need to select some contact in list and `tell` ContactFragment to show that contact
            //mCurrentFragmentTypeShowing is already set

            //TODO Select current contact in list
            mContactListFragment.selectItem(mCurrentShowingContactPositionInList);

            onContactSelected(mCurrentShowingContactPositionInList, mCurrentShowingContactId);

        }
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
        Log.d(CLASS_NAME, "onContactSelected" + "(" + position + ", " + contact_id + ")");
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


        if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
            //make ContactFragment and replace current one
            //dont forget to change mCurrentFragmentTypeShowing

            mContactFragment = new ContactFragment();
            mContactFragment.setArguments(args);

            getFragmentManager().beginTransaction().replace(R.id.fragment_container_portrait, mContactFragment, TAG_CONTACT_FRAGMENT).addToBackStack(null).commit();

        } else if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            //LANDSCAPE mode
            //find fragment (from xml, already inflated)
            //just call method to update view

            mContactFragment.updateContactView(contact_id);
        }

    }


    private int getOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            return Configuration.ORIENTATION_PORTRAIT;
        else
            return Configuration.ORIENTATION_LANDSCAPE;
    }
}
