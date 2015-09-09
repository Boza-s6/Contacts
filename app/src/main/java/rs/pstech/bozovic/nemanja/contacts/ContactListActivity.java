package rs.pstech.bozovic.nemanja.contacts;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

public class ContactListActivity extends Activity implements ContactListActivityFragment.OnContactSelectedListener {

    public static final String CLASS_NAME = ContactListActivity.class.getSimpleName();
    public static final String CALLER = "caller";
    public static final int REQUEST_CODE = 1;

    private static final String ARG_ID = "id";
    private static final String ARG_POS = "pos";
    private static final String ARG_PREVIOUS_ORIENTATION = "orientation";

    private static final String EXTRA_LANDSCAPE = "landscape";
    private static final String EXTRA_PORTRAIT = "portrait";


    private String mContactId = null;
    private int mPosition = -1;
    private String mPreviousOrientation = null;

    Bundle savedState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(CLASS_NAME, "onCreate");
        super.onCreate(savedInstanceState);
        //use different layout
        //depanding on orientation
        setContentView(R.layout.activity_contact_list);
    }

    @Override
    protected void onStart() {
        Log.d(CLASS_NAME, "onStart");
        super.onStart();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        if(savedInstanceState != null){

        //savedInstanceState is NEVER NULL
        mContactId = savedInstanceState.getString(ARG_ID);
        mPosition = savedInstanceState.getInt(ARG_POS);
        mPreviousOrientation = savedInstanceState.getString(ARG_PREVIOUS_ORIENTATION);

        onContactSelected(mPosition, mContactId);
//        }
    }

    @Override
    public void onContactSelected(int position, String contact_id) {
        Log.d(CLASS_NAME, "onContactSelected");
        //start activity if portrait
        //or just change data in ContactAcitvityFragment if landscape

        if (position == -1 || contact_id == null)
            return;

        mContactId = contact_id;
        mPosition = position;


        //arguments to send to fragment
        Bundle args = new Bundle();
        args.putString(ContactActivityFragment.ARG_CONTACT_ID, contact_id);
        args.putInt(ContactActivityFragment.ARG_CONTACT_POSITION, position);


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //PORTRAIT mode
            //start new ContactActivity
            //and sand data


            Intent startNewActivity = new Intent(this, ContactActivity.class);
            startNewActivity.putExtras(args);

            startActivityForResult(startNewActivity, REQUEST_CODE);


        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //LANDSCAPE mode
            //find fragment (from xml, already inflated)
            //call method to update view

            ContactActivityFragment frag = (ContactActivityFragment) getFragmentManager().findFragmentById(R.id.fragment_contact);
            frag.updateContactView(contact_id);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(CLASS_NAME, "onActivityResult");
        //
        if (requestCode == REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                String result = data.getStringExtra(ContactActivity.ARG_RESULT);

                if (result.equals(ContactActivity.ARG_LANDSCAPE)) {

                    String id = data.getStringExtra(ContactActivity.ARG_ID);
                    int pos = data.getIntExtra(ContactActivity.ARG_POSITION, 0);

                    onContactSelected(pos, id);

                }
            } else {
                return;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_PREVIOUS_ORIENTATION, getOrientation());
        outState.putString(ARG_ID, mContactId);
        outState.putInt(ARG_POS, mPosition);

    }

    private String getOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            return EXTRA_PORTRAIT;
        else
            return EXTRA_LANDSCAPE;
    }
}
