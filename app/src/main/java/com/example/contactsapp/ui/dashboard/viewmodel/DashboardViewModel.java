package com.example.contactsapp.ui.dashboard.viewmodel;

import android.app.Application;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.contactsapp.db.Image;
import com.example.contactsapp.db.ImageDatabase;
import com.example.contactsapp.ui.dashboard.models.ContactModel;

import java.util.ArrayList;

public class DashboardViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<ContactModel>> _contacts = new MutableLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ArrayList<ContactModel>> getContacts() {
        return _contacts;
    }

    public String getId(ContentResolver cr, String phoneNumber) {
        Cursor cursor = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID},
                String.format(
                        "%s = '%s'",
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        phoneNumber
                ),
                null,
                null
        );

        if (cursor != null) {
            cursor.moveToNext();
            return cursor.getString(0);
        }

        return null;
    }

    public void addContact(ContentResolver cr, ContactModel contactModel, int position) {
        ArrayList<ContactModel> temp = _contacts.getValue();
        temp.add(position, contactModel);
        _contacts.postValue(temp);

        if (contactModel.getLastName().length() != 0)
            addContact(cr, contactModel.getFirstName().concat(" ").concat(contactModel.getLastName()), contactModel.getPhoneNumber());
        else
            addContact(cr, contactModel.getFirstName(), contactModel.getPhoneNumber());
    }

    private String addContact(ContentResolver cr, String name, String phoneNumber) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // insert name
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, name)
                .build());

        // insert phone number
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // return phoneNumber to later re-position adapter to its corresponding index
        return phoneNumber;

    }

    public void deleteContact(ContentResolver cr, ContactModel contactModel) {
        ArrayList<ContactModel> temp = _contacts.getValue();
        temp.remove(contactModel);
        _contacts.postValue(temp);
        deleteContact(cr, contactModel.getId(), contactModel.getPhoneNumber());
    }


    private boolean deleteContact(ContentResolver cr, String id, String phoneNumber) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cur = cr.query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.NUMBER)).equalsIgnoreCase(phoneNumber)) {
                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        cr.delete(uri, null, null);

                        new Thread(() -> {
                            try {
                                Image image = ImageDatabase.newInstance(getApplication().getApplicationContext()).getImageDao().getImage(id);
                                ImageDatabase.newInstance(getApplication().getApplicationContext()).getImageDao().delete(image);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();

                        return true;
                    }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            cur.close();
        }
        return false;
    }

    public void fetchContacts() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor cursor = getApplication().getContentResolver().query(
                uri,
                projection,
                null,
                null,
                null
        );

        if (cursor != null) {
            ArrayList<ContactModel> temp = new ArrayList<>();
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String[] names = cursor.getString(1).split(" ");
                String phoneNumber = cursor.getString(2);

                ContactModel contact = new ContactModel();
                contact.setId(id);
                contact.setPhoneNumber(phoneNumber);
                contact.setFirstName(names[0]);

                if (names.length == 1) {
                    contact.setLastName("");
                } else {
                    contact.setLastName(names[1]);
                }

                temp.add(contact);
            }

            cursor.close();

            _contacts.postValue(temp);
        }


    }
}
