package com.example.contactsapp.ui.modify_contact;

import android.app.Application;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.example.contactsapp.ui.dashboard.models.ContactModel;

import java.util.ArrayList;

public class ModifyContactViewModel extends ViewModel {

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

    public void updateName(ContentResolver cr, String id, String newName) {
        String where = String.format(
                "%s = '%s' AND %s = ?",
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                ContactsContract.Data.CONTACT_ID);

        String[] args = {id};

        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        operations.add(
                ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(where, args)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, newName)
                        .build()
        );

        try {
            cr.applyBatch(ContactsContract.AUTHORITY, operations);
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updatePhoneNumber(ContentResolver cr, String id, String newPhoneNumber) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber);

        StringBuilder where = new StringBuilder();

        where.append(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
        where.append(" = ");
        where.append(id);

        where.append(" and ");
        where.append(ContactsContract.Data.MIMETYPE);
        where.append(" = '");
        String mimeType = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
        where.append(mimeType);
        where.append("'");

        Uri uri = ContactsContract.Data.CONTENT_URI;

        cr.update(uri, contentValues, where.toString(), null);
    }

    public void addContact(ContentResolver cr, String name, String phoneNumber) {

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

    }
}
