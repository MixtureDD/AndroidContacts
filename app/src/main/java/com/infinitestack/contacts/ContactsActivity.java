package com.infinitestack.contacts;

import android.app.SearchManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class ContactsActivity extends AppCompatActivity implements BaseFragment.BackPressedListener {

    private BaseFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        //加载Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ContractsFragment remoterFragment =
                (ContractsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (remoterFragment == null) {
            // Create the fragment
            remoterFragment = ContractsFragment.newInstance();
            ActivityUtils.replaceFragmentToActivity(
                    getSupportFragmentManager(), remoterFragment, R.id.contentFrame, remoterFragment.getClass().getName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(currentFragment != null ){
            if(getSupportFragmentManager().getBackStackEntryCount() == 1){
                finishAfterTransition();
            }else{
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void setCurrentFragment(BaseFragment currentFragment) {
        this.currentFragment = currentFragment;
    }
}
