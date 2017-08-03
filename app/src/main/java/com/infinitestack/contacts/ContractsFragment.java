package com.infinitestack.contacts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class ContractsFragment extends BaseFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ContractsFragment";
    static final String[] CONTACTS_SUMMARY_PROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.CONTACT_STATUS,
            ContactsContract.Contacts.CONTACT_PRESENCE,
            ContactsContract.Contacts.PHOTO_ID,
    };
    private String mFilter;
    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int CONTACT_KEY_INDEX = 1;
    // Defines the text expression
    @SuppressLint("InlinedApi")
    private static final String SELECTION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
    /*
     * Defines an array that contains column names to move from
     * the Cursor to the ListView.
     */
    private final static String[] FROM_COLUMNS = {
            ContactsContract.Contacts.DISPLAY_NAME,
//            ContactsContract.Contacts.CONTACT_STATUS
    };
    /*
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private final static int[] TO_IDS = {
            R.id.tv_contracts
    };
    // Define global mutable variables
    // Define a ListView object
    private ListView mContactsList;
    // Define variables for the contact the user selects
    // The contact's _ID value
    private long mContactId;
    // The contact's LOOKUP_KEY
    private String mContactKey;
    // A content URI for the selected contact
    private Uri mContactUri;
    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;

    public static ContractsFragment newInstance() {
        ContractsFragment fragment = new ContractsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        getLoaderManager().initLoader(0, null, this);
        super.onStart();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_contracts;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        // Gets the ListView from the View list of the parent activity
        mContactsList = (ListView) view.findViewById(R.id.lv_contacts);
        // Gets a CursorAdapter
        mCursorAdapter = new SimpleCursorAdapter(
                mActivity,
                R.layout.contact_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        // Sets the adapter for the ListView
        mContactsList.setAdapter(mCursorAdapter);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mContactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the Cursor
                CursorAdapter adapter = (CursorAdapter) parent.getAdapter();
                Cursor cursor = adapter.getCursor();
                // Move to the selected contact
                cursor.moveToPosition(position);
                // Get the _ID value
                mContactId = cursor.getLong(CONTACT_ID_INDEX);
                // Get the selected LOOKUP KEY
                mContactKey = cursor.getString(CONTACT_KEY_INDEX);
                // Create the contact's content Uri
                mContactUri = ContactsContract.Contacts.getLookupUri(mContactId, mContactKey);
                Log.d(TAG, mContactUri.toString());
//                getFragmentManager().findFragmentByTag(ContractsFragment.class.toString())
                DetailsFragment fragment = DetailsFragment.newInstance(mContactUri, mContactKey);
                ActivityUtils.replaceFragmentToActivity(getFragmentManager(), fragment, R.id.contentFrame, fragment.getClass().toString());
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri;
        if (mFilter != null) {
            baseUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI,
                    Uri.encode(mFilter));
        } else {
            baseUri = ContactsContract.Contacts.CONTENT_URI;
        }
        Log.d(TAG, baseUri.toString());
        String selection = "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1) AND ("
                + ContactsContract.Contacts.DISPLAY_NAME + " != '' ))";

        return new CursorLoader(
                mActivity,
                baseUri,
                CONTACTS_SUMMARY_PROJECTION,
                selection,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
