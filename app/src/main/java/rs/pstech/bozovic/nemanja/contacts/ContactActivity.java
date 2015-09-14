package rs.pstech.bozovic.nemanja.contacts;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

public class ContactActivity extends Activity {

    public static final String ARG_CONTACT_ID = "contact id";
    public static final String ARG_POSITION = "position";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.d(getClass().getSimpleName(), "ORIENTATION_LANDSCAPE");
            finish();
        }



    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent= getIntent();

        if(intent == null){
            finish();
        }

        String contactId = intent.getExtras().getString(ARG_CONTACT_ID);
        int position  = intent.getExtras().getInt(ARG_POSITION);
        ContactFragment fragment = (ContactFragment) getFragmentManager().findFragmentById(R.id.fragment_contact_container);
        fragment.updateContactView(contactId);
    }
}
