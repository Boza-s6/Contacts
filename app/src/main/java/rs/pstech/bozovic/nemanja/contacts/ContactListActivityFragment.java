package rs.pstech.bozovic.nemanja.contacts;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ContactListActivityFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * A placeholder fragment containing a simple view.
     */
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        Log.d(CLASS_NAME, "onActivityCreated");
//        super.onActivityCreated(savedInstanceState);
//
//
    public interface OnContactSelectedListener {

        public void onContactSelected(int position, String contact_id);
    }

    private static final String CLASS_NAME = ContactListActivityFragment.class.getSimpleName();

    private static final int CONTACT_LOADER = 0;

    private ContactListActivity mListener; //Activity, callback, for communication with other fragment
    private SimpleContactsArrayAdapter mAdapter; //Adapter for my list


    private List<SimpleContact> mContactList = new ArrayList<>(); //contact list



    public ContactListActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(CLASS_NAME, "onAttach");
        try {
            mListener = (ContactListActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(CLASS_NAME, "onCreate");
        super.onCreate(savedInstanceState);

        mAdapter = new SimpleContactsArrayAdapter(mListener, android.R.layout.simple_list_item_activated_1, mContactList);
        setListAdapter(mAdapter);

        getLoaderManager().initLoader(CONTACT_LOADER, null, this);
    }

//    }


    @Override
    public void onStart() {
        Log.d(CLASS_NAME, "onStart");
        super.onStart();

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
        else
            getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);


    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(CLASS_NAME, "onListItemClick");

        if (mListener == null) {
            Log.d(CLASS_NAME, "mListener is NULL");
            return;
        }

        mListener.onContactSelected(position, mContactList.get(position).getId());
        getListView().setItemChecked(position, true);
    }


    @Override
    public void onDetach() {
        Log.d(CLASS_NAME, "onDetach");
        super.onDetach();
        mListener = null;
    }


    private List<SimpleContact> getContacts(){
        List<SimpleContact> contacts = new ArrayList<>();
        //what to query
        Uri contactUri = ContactsContract.Contacts.CONTENT_URI;

        //which columns we want to query
        String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};

        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursorContacts = null;

        try {
            cursorContacts = contentResolver.query(contactUri, projection, null, null, null);

            final int ID           = cursorContacts.getColumnIndex(ContactsContract.Contacts._ID);
            final int DISPLAY_NAME = cursorContacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

            if (cursorContacts.getCount() > 0) {
                while (cursorContacts.moveToNext()) {
                    String contactId = cursorContacts.getString(ID);
                    String displayName = cursorContacts.getString(DISPLAY_NAME);

                    contacts.add(new SimpleContact(contactId, displayName));
                }
            }

        } finally {
            if (cursorContacts != null) {
                cursorContacts.close();
            }
        }
        return contacts;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {

        Log.d(CLASS_NAME, "onCreateLoader");

        if(loaderID != CONTACT_LOADER)
            return null;

        //which table to query
        Uri contactUri = ContactsContract.Contacts.CONTENT_URI;

        //What columns to get
        String projection[] = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};

        String where = null;
        String []whereArgs = null;
        String sortOrder = null;

        return new CursorLoader(getActivity(), contactUri, projection, where, whereArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.d(CLASS_NAME, "onLoadFinished");

        if (data == null || data.getCount() <= 0)
            return;

        mContactList.clear();

        final int ID           = data.getColumnIndex(ContactsContract.Contacts._ID);
        final int DISPLAY_NAME = data.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

        data.moveToFirst();
        while (data.moveToNext()){
            String contacId = data.getString(ID);
            String name = data.getString(DISPLAY_NAME);

            mContactList.add(new SimpleContact(contacId, name));
        }
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(CLASS_NAME, "onLoaderReset");


    }


    private class SimpleContactsArrayAdapter extends ArrayAdapter<SimpleContact> {

        public SimpleContactsArrayAdapter(Context context, int resource, List<SimpleContact> contacts){
            super(context, resource, contacts);
            Log.d(CLASS_NAME, "SimpleContactsArrayAdapter Constructor");
        }
    }
}
