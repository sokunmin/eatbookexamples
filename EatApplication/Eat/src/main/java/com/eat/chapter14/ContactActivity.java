package com.eat.chapter14;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.SimpleCursorAdapter;

import com.eat.L;

public class ContactActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CONTACT_NAME_LOADER_ID = 0;

    // Projection that defines contact display name only
    static final String[] CONTACTS_SUMMARY_PROJECTION = new String[] {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
    };

    SimpleCursorAdapter mAdapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        initAdapter();
        getLoaderManager().initLoader(CONTACT_NAME_LOADER_ID, null, this);
    }

    private void initAdapter() {
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null,
                new String[] { ContactsContract.Contacts.DISPLAY_NAME },
                new int[] { android.R.id.text1}, 0);
        setListAdapter(mAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ContactsContract.Contacts.CONTENT_URI,
                CONTACTS_SUMMARY_PROJECTION, null, null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        mAdapter.swapCursor(c);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        L.d(getClass(), "ThreadId: %d", Thread.currentThread().getId());
        mAdapter.swapCursor(null);
    }
}