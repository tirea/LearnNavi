package org.learnnavi.learnnavi;

import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Gravity;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private String[] mPartsOfSpeech;
    
    //DBAdapter myDB = new DBAdapter(this);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		mDrawerList = (ListView) findViewById(R.id.navList);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mActivityTitle = getTitle().toString();
		
		addDrawerItems();
		setupDrawer();
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);        
        
		String[] words = {
			"'a'aw",  "'akra", "'aku", "'al",
			"'ali'ä", "'ampi", "'ampirikx", "'ana",
			"'ango", "'angtsìk", "'anla", "'are",
			"'aw", "'awkx", "'awlie", "'awlo",
			"'awm", "'awnìm", "'awpo", "'awsiteng"
		};
		
		ListAdapter mListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, words);
		
		ListView dictListView = (ListView) findViewById(R.id.dictList);
		dictListView.setAdapter(mListAdapter);
		dictListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
            {
                String wordSelected = "You tapped " +
                    String.valueOf(p1.getItemAtPosition(p3));
                Toast.makeText(MainActivity.this, wordSelected, Toast.LENGTH_SHORT).show();
            }
		});
		
		/*FloatingActionButton fabButton = new FloatingActionButton.Builder(this)
        .withDrawable(getResources().getDrawable(R.drawable.ic_search_white_24dp))
        .withButtonColor(getResources().getColor(R.color.accent))
        .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
        .withMargins(0, 0, 16, 16)
        .create();*/
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		//MenuItem searchItem = menu.findItem(R.id.action_search);
		//SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		
		// Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
		
		// Handle item selection
		switch (item.getItemId())
		{
			case R.id.action_search:
				// TODO: search.
				return true;
			case R.id.action_settings:
				// TODO: open settings.
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
    }

    /*private void populateListView() {
        Cursor cursor = myDB.getAllRows();
        String[] fromFieldNames = new String[] {DBAdapter.KEY_ROWID,DBAdapter.KEY_TASK};
        int[] toViewIDSNewIDs = new int[] {R.id.textViewItemNumber,R.id.textViewItemTask};
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(),R.layout.item_layout,cursor,fromFieldNames,toViewIDS,0);
        ListView mylist = (ListView) findViewById(R.id.listViewTasks);
        mylist.setAdapter(myCursorAdapter);
    }*/
    
    private void addDrawerItems() {
        mPartsOfSpeech = getResources().getStringArray(R.array.partsOfSpeech);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mPartsOfSpeech);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Filter test", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.drawer_open);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
}