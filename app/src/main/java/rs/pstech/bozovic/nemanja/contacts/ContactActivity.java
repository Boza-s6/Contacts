package rs.pstech.bozovic.nemanja.contacts;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

public class ContactActivity extends Activity {

    public static final String ARG_LANDSCAPE = "landscape";
    public static final String ARG_RESULT = "result";
    public static final String ARG_ID = "id";
    public static final String ARG_POSITION = "position";




    Intent mReturnIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mReturnIntent = new Intent();


        if(getIntent() == null){
            setResult(RESULT_CANCELED, mReturnIntent);
            finish();
        }

        String contactId = getIntent().getExtras().getString(ContactActivityFragment.ARG_CONTACT_ID);
        int position =  getIntent().getExtras().getInt(ContactActivityFragment.ARG_CONTACT_POSITION);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.d(getClass().getSimpleName(), "ORIENTATION_LANDSCAPE");

            setResult(RESULT_OK, mReturnIntent);

            mReturnIntent.putExtra(ARG_RESULT, ARG_LANDSCAPE);
            mReturnIntent.putExtra(ARG_ID, contactId);
            mReturnIntent.putExtra(ARG_POSITION, position);

            finish();
        }

    }
}
