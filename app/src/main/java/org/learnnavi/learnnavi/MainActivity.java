package org.learnnavi.learnnavi;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    //DBAdapter myDB = new DBAdapter(this);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		String[] words = {
			"Word 1",  "Word 2", "Word 3", "Word 4",
			"Word 5", "Word 6", "Word 7", "Word 8",
			"Word 9", "Word 10", "Word 11", "Word 12",
			"Word 13", "Word 14", "Word 15", "Word 16",
			"Word 17", "Word 18", "Word 19", "Word 20",
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
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

    private void populateListView() {
        /*
        Cursor cursor = myDB.getAllRows();
        String[] fromFieldNames = new String[] {DBAdapter.KEY_ROWID,DBAdapter.KEY_TASK};
        int[] toViewIDSNewIDs = new int[] {R.id.textViewItemNumber,R.id.textViewItemTask};
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(),R.layout.item_layout,cursor,fromFieldNames,toViewIDS,0);
        ListView mylist = (ListView) findViewById(R.id.listViewTasks);
        mylist.setAdapter(myCursorAdapter);
        */
    }
}