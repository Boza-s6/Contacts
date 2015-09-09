package rs.pstech.bozovic.nemanja.contacts;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nemanja on 7.9.15..
 */
public class FullContact {
    String mId;
    String mName;
    Uri mPhotoUri;
    List<String> mPhoneNumbers;
    List<String> mEmails;

    public FullContact(String id, String name, Uri photoUri, List<String> phoneNumbers, List<String> emails) {
        mId = id;
        mPhotoUri = photoUri;
        mName = name;
        mPhoneNumbers = phoneNumbers;
        mEmails = emails;
    }

    public FullContact() {
        mId = "None";
        mName = "None";

        mPhoneNumbers = new ArrayList<>();
        mPhoneNumbers.add("None");

        mEmails = new ArrayList<>();
        mEmails.add("None");
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public Uri getPhotoUri() {
        return mPhotoUri;
    }

    public List<String> getPhoneNumbers() {
        return mPhoneNumbers;
    }

    public List<String> getEmails() {
        return mEmails;
    }

    @Override
    public String toString() {
        return "FullContact{" +
                "mId='" + mId + '\'' +
                ", mName='" + mName + '\'' +
                ", mPhoneNumbers=" + mPhoneNumbers +
                ", mEmails=" + mEmails +
                '}';
    }

    public static FullContact getFullContactFromId(Context context, String contactId) {

        ContentResolver contentResolver = context.getContentResolver();

        String name = getNameById(contactId, contentResolver);
        List<String> phoneList = getPhoneNumberListById(contactId, contentResolver);
        List<String> emailList = getEmailListFromId(contactId, contentResolver);
        Uri photoUri = getPhotoUriById(contactId, contentResolver);


        return new FullContact(contactId, name, photoUri, phoneList, emailList);
    }

    @Nullable
    private static String getNameById(String contactId, ContentResolver contentResolver) {

        String name = null;
        Cursor cursorName = null;
        try {

            cursorName = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                    new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME},
                    ContactsContract.Contacts._ID + " = " + contactId, null, null);

            //query can return null
            if (cursorName == null)
                return null;
            final int DISPLAY_NAME = cursorName.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

            if (cursorName.getCount() > 0) {
                if (cursorName.moveToNext()) {

                    name = cursorName.getString(DISPLAY_NAME);

                }
            }
        } finally {
            if (cursorName != null)
                cursorName.close();
        }
        return name;
    }

    @Nullable
    private static List<String> getEmailListFromId(String contactId, ContentResolver contentResolver) {
        ArrayList<String> emails = new ArrayList<>();

        //this block here query email for current conntact
        Cursor cursorEmails = null;
        try {
            cursorEmails = contentResolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId,
                    null,
                    null);

            if (cursorEmails == null)
                return emails;

            final int EMAIL = cursorEmails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

            if (cursorEmails.getCount() > 0) {
                while (cursorEmails.moveToNext()) {
                    emails.add(cursorEmails.getString(EMAIL));
                }
            }
        } finally {
            if (cursorEmails != null)
                cursorEmails.close();
        }
        return emails;
    }

    @Nullable
    private static List<String> getPhoneNumberListById(String contactId, ContentResolver contentResolver) {
        ArrayList<String> phoneNumbers = new ArrayList<>();

        Cursor cursorNumbers = null;
        try {

            cursorNumbers = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null);

            if (cursorNumbers == null)
                return phoneNumbers;

            final int PHONE = cursorNumbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);

            if (cursorNumbers.getCount() > 0) {
                while (cursorNumbers.moveToNext()) {
                    phoneNumbers.add(cursorNumbers.getString(PHONE));
                }
            }
        } finally {
            if (cursorNumbers != null)
                cursorNumbers.close();
        }
        return phoneNumbers;
    }

    @Nullable
    private static Uri getPhotoUriById(String contactId, ContentResolver contentResolver) {


        Cursor cursorPhoto = null;
        Uri photoUri = null;
//
//        {
//            cursorPhoto = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//            System.out.println("Pocetak");
//            for(int i=0; i < cursorPhoto.getColumnCount(); ++i){
//                System.out.println(cursorPhoto.getColumnName(i));
//            }
//            System.out.println("KRAJ");
//        }

        String[] projection = new String[]{ContactsContract.Contacts.PHOTO_URI};
        String where = ContactsContract.Contacts._ID + "= ?";
        String[] whereArgs = new String[]{contactId};


        try {
            cursorPhoto = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI
                    , projection
                    , where
                    , whereArgs
                    , null
            );

            if (cursorPhoto == null)
                return null;

            if (!cursorPhoto.moveToNext())
                return null;

            final int PHOTO_URI_COLUMN_INDEX = cursorPhoto.getColumnIndex(ContactsContract.Contacts.PHOTO_URI);

            String photoUriStr;

            photoUriStr = cursorPhoto.getString(PHOTO_URI_COLUMN_INDEX);

            if (photoUriStr != null)
                photoUri = Uri.parse(photoUriStr);
            else
                photoUri = null;


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursorPhoto != null)
                cursorPhoto.close();
        }

        return photoUri;
    }
}
