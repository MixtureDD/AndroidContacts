package com.infinitestack.contacts;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * DetailsFragment.java
 * Description :
 * <p>
 * Created by InfiniteStack on 2017/4/30 15:32.
 * Copyright © 2017 InfiniteStack. All rights reserved.
 */

public class DetailsFragment extends BaseFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARGS_CONTACT_URI = "ARGS_CONTACT_URI";
    private static final String ARGS_LOOKUP_KEY = "ARGS_LOOKUP_KEY";
    private static final String PROJECTION[] =
            {
                    ContactsContract.Contacts.Data._ID,
                    ContactsContract.Contacts.Data.MIMETYPE,
                    ContactsContract.Contacts.Data.DATA1,
            };
    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the MIMETYPE column
    private static final int CONTACT_MIMETYPE_INDEX = 1;
    // The column index for the DATA1 column
    private static final int CONTACT_DATA1_INDEX = 2;
    // Defines the selection clause
    private static final String SELECTION = ContactsContract.Data.LOOKUP_KEY + " = ?";
    // Defines the array to hold the search criteria
    private String[] mSelectionArgs = {""};
    private final static int DETAILS_QUERY_ID = 0;
    /*
     * Defines a variable to contain the selection value. Once you
     * have the Cursor from the Contacts table, and you've selected
     * the desired row, move the row's LOOKUP_KEY value into this
     * variable.
     */
    private String mLookupKey;
    private static final String SORT_ORDER = ContactsContract.Contacts.Data.MIMETYPE;

    private Uri mContactUri;

    private TextView mNameTextView;
    private TextView mPhoneTextView;
    private TextView mEmailTextView;

    public static DetailsFragment newInstance(Uri contactUri, String key) {

        Bundle args = new Bundle();
        args.putParcelable(ARGS_CONTACT_URI, contactUri);
        args.putString(ARGS_LOOKUP_KEY, key);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_details;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mNameTextView = (TextView) view.findViewById(R.id.tv_details_name_show);
        mPhoneTextView = (TextView) view.findViewById(R.id.tv_details_phone_show);
        mEmailTextView = (TextView) view.findViewById(R.id.tv_details_email_show);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        getLoaderManager().initLoader(DETAILS_QUERY_ID, null, this);
        super.onStart();
    }

    @Override
    protected void parseArgumentsFromBundle(Bundle argBundle) {
        super.parseArgumentsFromBundle(argBundle);
        mContactUri = argBundle.getParcelable(ARGS_CONTACT_URI);
        mLookupKey = argBundle.getString(ARGS_LOOKUP_KEY);;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.edit) {
            // Creates a new Intent to insert a contact
            Intent intent = new Intent(Intent.ACTION_EDIT);
            // Sets the ContactUri and MIME type to match the Contacts Provider
            intent.setDataAndType(mContactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);

            intent.putExtra("finishActivityOnSaveCompleted", true);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = new CursorLoader(mActivity);
        switch (id) {
            case DETAILS_QUERY_ID:
                // Assigns the selection parameter
                mSelectionArgs[0] = mLookupKey;
                // Starts the query
                loader = new CursorLoader(
                        mActivity,
                        ContactsContract.Data.CONTENT_URI,
                        PROJECTION,
                        SELECTION,
                        mSelectionArgs,
                        SORT_ORDER
                );
                break;
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case DETAILS_QUERY_ID:
                while (data.moveToNext()) {
                    String data1 = data.getString(CONTACT_DATA1_INDEX);
                    String mimetype = data.getString(CONTACT_MIMETYPE_INDEX);
                    switch (mimetype){
                        case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                            mNameTextView.setText(data1);
                            break;
                        case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                            mPhoneTextView.setText(data1);
                            break;
                        case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                            mEmailTextView.setText(data1);
                            break;
                    }
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case DETAILS_QUERY_ID:
                break;
        }
    }

}
