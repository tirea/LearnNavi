package org.learnnavi.learnnavi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter extends SQLiteOpenHelper {

    private static final String DB_PATH = "/data/data/org.learnnavi.learnnavi/databases/";
	private static final String DB_NAME = "database.sqlite";
	private SQLiteDatabase myDataBase;
	private final Context myContext;
	private int mRefCount;
	private String mDbVersion;
	
	// Hard coded for now until the option is implemented
	private static final String LOCALE = "eng";
	
	// Column names for use by other classes
    public static final String KEY_WORD = "word";
    public static final String KEY_DEFINITION = "definition";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_IPA = "ipa";
    public static final String KEY_LETTER = "letter";
    public static final String KEY_PART = "part_of_speech";

    // Undo ì->j and ä->b substitution for the Na'vi word
    private static final String QUERY_PART_NAVI_WORD = "metaWords.navi";
    // Undo ì->j and ä->b substitution for the Na'vi letter
    private static final String QUERY_PART_NAVI_LETTER = "replace(replace(metaWords.alpha, 'B', 'Ä'), 'J', 'Ì')";
    
    // Basic query by Na'vi word
    private static final String QUERY_PART_NAVI_START = "SELECT metaWords.id AS _id, " + QUERY_PART_NAVI_WORD + " AS word, " + QUERY_PART_NAVI_LETTER + " AS letter, localizedWords.localized AS definition FROM localizedWords JOIN metaWords ON (localizedWords.id = metaWords.id) WHERE localizedWords.languageCode = ? ";
    private static final String QUERY_PART_NAVI_END = "ORDER BY metaWords.alpha COLLATE UNICODE, metaWords.navi_no_specials COLLATE UNICODE";

    // Query the first letter of Na'vi words
    private static final String QUERY_PART_NAVI_LETTER_START = "SELECT MIN(metaWords.id) AS _id, COUNT(*) AS _count, ' ' || " + QUERY_PART_NAVI_LETTER + " || ' ' AS letter FROM localizedWords JOIN metaWords ON (localizedWords.id = metaWords.id) WHERE localizedWords.languageCode = ? ";
    private static final String QUERY_PART_NAVI_LETTER_END = "GROUP BY metaWords.alpha ORDER BY metaWords.alpha COLLATE UNICODE";

    // Filter Na'vi word query
    private static final String QUERY_PART_NAVI_FILTER_WHERE = "metaWords.navi LIKE ? ";

    // Basic query by translation
    private static final String QUERY_PART_TO_NAVI_START = "SELECT metaWords.id AS _id, " + QUERY_PART_NAVI_WORD + " AS definition, localizedWords.localized AS word, localizedWords.alpha AS letter FROM localizedWords JOIN metaWords ON (localizedWords.id = metaWords.id) WHERE localizedWords.languageCode = ? ";
    private static final String QUERY_PART_TO_NAVI_END = "ORDER BY localizedWords.alpha COLLATE UNICODE, localizedWords.localized COLLATE UNICODE";
    
    // Query the first letter of translation
    private static final String QUERY_PART_TO_NAVI_LETTER_START = "SELECT MIN(localizedWords.id) AS _id, COUNT(*) AS _count, ' ' || localizedWords.alpha || ' ' AS letter FROM localizedWords JOIN metaWords ON (localizedWords.id = metaWords.id) WHERE localizedWords.languageCode = ? ";
    private static final String QUERY_PART_TO_NAVI_LETTER_END = "GROUP BY localizedWords.alpha ORDER BY localizedWords.alpha COLLATE UNICODE";
    
    // Filter translated word query
    private static final String QUERY_PART_TO_NAVI_FILTER_WHERE = "localizedWords.localized LIKE ? ";
    
    // Query a single entry by ID
    private static final String QUERY_ENTRY = "SELECT metaWords.id AS _id, " + QUERY_PART_NAVI_WORD + " AS word, localizedWords.localized AS definition, metaWords.ipa, ps.description as part_of_speech FROM localizedWords JOIN metaWords ON (localizedWords.id = metaWords.id) LEFT JOIN partsOfSpeech ps USING (partOfSpeech) WHERE metaWords.id = ? AND localizedWords.languageCode = ?";

    // Query used by the search suggest when neither to or from Na'vi is requested
    private static final String QUERY_FOR_SUGGEST = "SELECT metaWords.id AS _id, metaWords.id AS suggest_intent_data, " + QUERY_PART_NAVI_WORD + " AS suggest_text_1, localizedWords.localized AS suggest_text_2 FROM localizedWords JOIN metaWords ON (localizedWords.id = metaWords.id) WHERE (metaWords.navi LIKE ? OR localizedWords.localized LIKE ?) AND localizedWords.languageCode = ? ORDER BY LENGTH(metaWords.navi), localizedWords.alpha COLLATE UNICODE, localizedWords.localized COLLATE UNICODE LIMIT 25";
    // Query used by the search suggest when Na'vi words are being searched
    private static final String QUERY_FOR_SUGGEST_NAVI = "SELECT metaWords.id AS _id, metaWords.id AS suggest_intent_data, " + QUERY_PART_NAVI_WORD + " AS suggest_text_1, localizedWords.localized AS suggest_text_2 FROM localizedWords JOIN metaWords ON (localizedWords.id = metaWords.id) WHERE metaWords.navi LIKE ? AND localizedWords.languageCode = ? ORDER BY LENGTH(metaWords.navi), metaWords.alpha COLLATE UNICODE, metaWords.navi_no_specials COLLATE UNICODE LIMIT 25";
    // Query used by the search suggest when English words are being searched
    private static final String QUERY_FOR_SUGGEST_NATIVE = "SELECT metaWords.id AS _id, metaWords.id AS suggest_intent_data, " + QUERY_PART_NAVI_WORD + " AS suggest_text_2, localizedWords.localized AS suggest_text_1 FROM localizedWords JOIN metaWords ON (localizedWords.id = metaWords.id) WHERE localizedWords.localized LIKE ? AND localizedWords.languageCode = ? ORDER BY LENGTH(localizedWords.localized), localizedWords.alpha COLLATE UNICODE, localizedWords.localized COLLATE UNICODE LIMIT 25";
    
    // Part of speech filter clauses
    public static final String FILTER_ALL = null;
    public static final String FILTER_NOUN = "(metaWords.partOfSpeech LIKE '%^prop.n.^%' OR metaWords.partOfSpeech LIKE '%^n.^%') ";
    public static final String FILTER_PNOUN = "(metaWords.partOfSpeech LIKE '%^pn.^%') ";
    public static final String FILTER_VERB = "(metaWords.partOfSpeech LIKE '%^v.^%') ";
    public static final String FILTER_VIN = "(metaWords.partOfSpeech LIKE '%^vin.^%') ";
    public static final String FILTER_VTR = "(metaWords.partOfSpeech LIKE '%^vtr.^%') ";
    public static final String FILTER_MODAL = "(metaWords.partOfSpeech LIKE '%^vtrm.^%' OR metaWords.partOfSpeech LIKE '%^vim.^%') ";
    public static final String FILTER_SVIN = "(metaWords.partOfSpeech LIKE '%^svin.^%') ";
    public static final String FILTER_VERB_SI = "(metaWords.navi LIKE '%si' AND metaWords.partOfSpeech LIKE '%^vin.^%') ";
    public static final String FILTER_ADJ = "(metaWords.partOfSpeech LIKE '%^adj.^%') ";
    public static final String FILTER_ADV = "(metaWords.partOfSpeech LIKE '%^adv.^%') ";
	public static final String FILTER_ADP = "(metaWords.partOfSpeech LIKE '%^adp.^%') ";
    public static final String FILTER_CONJ = "(metaWords.partOfSpeech LIKE '%^conj.^%') ";
	public static final String FILTER_PART = "(metaWords.partOfSpeech LIKE '%^part.^%') ";
	public static final String FILTER_INTJ = "(metaWords.partOfSpeech LIKE '%^intj.^%')";
	public static final String FILTER_INTER = "(metaWords.partOfSpeech LIKE '%^inter.^%')";
	
	/**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DBAdapter(Context context) {
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }
    
    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		//do nothing - database already exist
    	}else{
 
    		//By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
 
        	try {
    			copyDataBase();
    		} catch (IOException e) {
        		throw new Error("Error copying database");
        	}
    	}
    }
    
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
    	SQLiteDatabase checkDB = null;
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	}catch(SQLiteException e){
    		//database does't exist yet.
    	}
    	if(checkDB != null){
    		checkDB.close();
    	}
    	return checkDB != null ? true : false;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
    }
 
    public void openDataBase() throws SQLException{
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }
 
    @Override
	public synchronized void close() {
    	    if(myDataBase != null)
    		    myDataBase.close();
    	    super.close();
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
	
}