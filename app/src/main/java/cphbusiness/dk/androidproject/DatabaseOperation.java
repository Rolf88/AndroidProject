package cphbusiness.dk.androidproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kalkun on 22-05-2016.
 */
public class DatabaseOperation extends SQLiteOpenHelper {
    public static final int database_version = 1;
    public String CREATE_QUERYONE = "CREATE TABLE "
            + TableData.MainTable.TABLE_NAME_USER + "("
            + TableData.MainTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TableData.MainTable.NAME + " TEXT,"
            + TableData.MainTable.EMAIL + " TEXT,"
            + TableData.MainTable.PASS_WORD + " TEXT,"
            + TableData.MainTable.lATITUDE + " REAL,"
            + TableData.MainTable.lONGITUDE + " REAL );";

    public DatabaseOperation(Context context) {
        super(context, TableData.MainTable.DATABASE_NAME, null, database_version);
        Log.d("Database Operations", "Database created");
    }

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL(CREATE_QUERYONE);
        Log.d("Database Operations", "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void putInformation(DatabaseOperation dob, User user) {
        SQLiteDatabase SQ = dob.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableData.MainTable.NAME, user.getName());
        cv.put(TableData.MainTable.EMAIL, user.getEmail());
        cv.put(TableData.MainTable.PASS_WORD, user.getPassword());
        cv.put(TableData.MainTable.lATITUDE, user.getLatitude());
        cv.put(TableData.MainTable.lONGITUDE, user.getLongitude());
        long k = SQ.insert(TableData.MainTable.TABLE_NAME_USER, null, cv);

        Log.d("Database Operations", "One row inserted");

    }

    public Cursor getInformations(DatabaseOperation dob) {
        SQLiteDatabase SQ = dob.getReadableDatabase();
        String[] columns = {TableData.MainTable.ID, TableData.MainTable.NAME, TableData.MainTable.PASS_WORD, TableData.MainTable.lATITUDE, TableData.MainTable.lONGITUDE};
        Cursor CR = SQ.query(TableData.MainTable.TABLE_NAME_USER, columns, null, null, null, null, null);
        Log.d("Database Operations", "Info retrieved");
        return CR;
    }

    public void deleteAllInfo(DatabaseOperation dob) {
        SQLiteDatabase SQ = dob.getWritableDatabase();
        SQ.delete(TableData.MainTable.TABLE_NAME_USER, null, null);
        SQ.execSQL("delete from " + TableData.MainTable.TABLE_NAME_USER);
        Log.d("Database Operations", "All info deleted");
    }

    public void updateInformation(DatabaseOperation dob, String friendName, User user) {
        SQLiteDatabase SQ = dob.getWritableDatabase();
        String selection = TableData.MainTable.NAME + " LIKE ? ";
        String args[] = {friendName};
        ContentValues values = new ContentValues();
        values.put(TableData.MainTable.NAME, user.getName());
        values.put(TableData.MainTable.EMAIL, user.getEmail());
        values.put(TableData.MainTable.PASS_WORD, user.getPassword());
        values.put(TableData.MainTable.lATITUDE , user.getLatitude());
        values.put(TableData.MainTable.lONGITUDE, user.getLongitude());
        SQ.update(TableData.MainTable.TABLE_NAME_USER, values, selection, args);
    }
}
