package rs.pstech.bozovic.nemanja.contacts;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class ContactActivityFragment extends Fragment {

    static final String ARG_CONTACT_ID = "contact_id";
    static final String ARG_CONTACT_POSITION = "position";



    String mContactId = null;

    FullContact mContact;

    private final Uri PLACEHOLDER_IMAGE = Uri.parse("android.resource://rs.pstech.bozovic.nemanja.contacts/drawable/placeholder_contact_photo");

    TextView nameTextView;
    TextView phoneNumbersTextView;
    TextView emailsTextView;
    ImageView photoImageView;

    public ContactActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //restore previous contact selection
        if (savedInstanceState != null) {
            mContactId = savedInstanceState.getString(ARG_CONTACT_ID);
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        nameTextView = (TextView) getActivity().findViewById(R.id.name);
        phoneNumbersTextView = (TextView) getActivity().findViewById(R.id.phoneNumber);
        emailsTextView = (TextView) getActivity().findViewById(R.id.emails);
        photoImageView = (ImageView) getActivity().findViewById(R.id.contactPhoto);
        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.

        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return;
        }

        Bundle args = intent.getExtras();
        if (args != null) {
            mContactId = args.getString(ARG_CONTACT_ID);
            mContact = FullContact.getFullContactFromId(getActivity(), mContactId);

            updateContactView(mContactId);
        } else if (mContactId != null) {
            updateContactView(mContactId);
        }
    }

    public void updateContactView(String contact_id) {
        if (mContact == null || !contact_id.equals(mContactId)) {
            mContact = FullContact.getFullContactFromId(getActivity(), contact_id);
        }
        mContactId = contact_id;

        nameTextView.setText(mContact.getName());
        phoneNumbersTextView.setText(mContact.getPhoneNumbers().toString());
        emailsTextView.setText(mContact.getEmails().toString());


        Uri photo = mContact.getPhotoUri();
        if (photo != null ){
//            Log.i("PHOTO_URI", photo.getPath());
            photoImageView.setImageURI(photo);
        }
        else
            photoImageView.setImageURI(PLACEHOLDER_IMAGE);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_CONTACT_ID, mContactId);
    }

}
