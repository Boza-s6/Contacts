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


public class ContactFragment extends Fragment {

    static final String ARG_CONTACT_ID = "contact_id";
    static final String ARG_CONTACT_POSITION = "position";

    private static final String EXTRA_CONTACT_ID = "contact id";
//    private static final String EXTRA_CONTACT_POS = "pos id";

    String mContactId = null;

    FullContact mContact;

//    private final Uri PLACEHOLDER_IMAGE = Uri.parse("android.resource://rs.pstech.bozovic.nemanja.contacts/drawable/placeholder_contact_photo");

    private TextView nameTextView;
    private TextView phoneNumbersTextView;
    private TextView emailsTextView;
    private ImageView photoImageView;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(getClass().getSimpleName(), "onCreateView");

        //restore previous contact selection
        if (savedInstanceState != null) {
            mContactId = savedInstanceState.getString(EXTRA_CONTACT_ID);
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        nameTextView = (TextView) view.findViewById(R.id.name);
        phoneNumbersTextView = (TextView) view.findViewById(R.id.phoneNumber);
        emailsTextView = (TextView) view.findViewById(R.id.emails);
        photoImageView = (ImageView) view.findViewById(R.id.contactPhoto);

        return view;
    }

    @Override
    public void onStart() {
        Log.d(getClass().getSimpleName(), "onStart");
        super.onStart();



        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.

        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return;
        }

        Bundle args = getArguments();
        if (args != null) {
            Log.d(getClass().getSimpleName(), "onCreate; getArguments() != null");
            mContactId = args.getString(ARG_CONTACT_ID);
            mContact = FullContact.getFullContactFromId(getActivity(), mContactId);

            updateContactView(mContactId);
        } else if (mContactId != null) {
            updateContactView(mContactId);
        }
    }

    public void updateContactView(String contact_id) {
        Log.d(getClass().getSimpleName(), "updateContactView");

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
            photoImageView.setImageResource(R.drawable.placeholder_contact_photo);


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(getClass().getSimpleName(), "onSaveInstanceState");

        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_CONTACT_ID, mContactId);
    }

}
