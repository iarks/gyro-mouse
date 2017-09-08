package iarks.org.bitbucket.gyromouse;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

class DatabaseHandler extends SQLiteOpenHelper
{

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "serverManager";

    // Contacts table name
    private static final String TABLE_SERVERS = "servers";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IP = "ip";

    DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_SERVERS_TABLE = "CREATE TABLE " + TABLE_SERVERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_IP + " TEXT" + ")";
        db.execSQL(CREATE_SERVERS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVERS);

        // Create tables again
        onCreate(db);
    }

    public void addServerToDB(Server server)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, server.getServerName()); // Contact Name
        values.put(KEY_IP, server.getServerIP()); // Contact Phone Number

        // Inserting Row
        db.insert(TABLE_SERVERS, null, values);
        db.close(); // Closing database connection
    }

    public Server getServerFromDB(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SERVERS, new String[] { KEY_ID, KEY_NAME, KEY_IP }, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // TODO: 9/8/2017 check this part
        Server server = new Server(Integer.parseInt(cursor.getString(0)),  cursor.getString(1), cursor.getString(2));
        // return contact
        return server;
    }

    public List<Server> getAllDBServers()
    {
        List<Server> serverList = new ArrayList<Server>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SERVERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                Server server = new Server();
                server.setServerID(Integer.parseInt(cursor.getString(0)));
                server.setServerName(cursor.getString(1));
                server.setServerIP(cursor.getString(2));
                // Adding contact to list
                serverList.add(server);
            }
            while (cursor.moveToNext());
        }

        db.close();
        // return contact list
        return serverList;
    }

    public int getServerCount()
    {
        String countQuery = "SELECT  * FROM " + TABLE_SERVERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    public int updateServer(Server server)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, server.getServerName());
        values.put(KEY_IP, server.getServerIP());

        // updating row
        return db.update(TABLE_SERVERS, values, KEY_ID + " = ?", new String[] { String.valueOf(server.getServerID()) });
    }

    public void deleteServer(Server server)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SERVERS, KEY_ID + " = ?",
                new String[] { String.valueOf(server.getServerID()) });
        db.close();
    }

//    public int checkPresent(String k)
//    {
//        String selectQuery = "SELECT  * FROM " + TABLE_SAVED;
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//        // looping through all rows and adding to list
//        if (cursor.moveToFirst())
//        {
//            do
//            {
//                String x =cursor.getString(0);
//                if (x.equals(k))
//                {
//                    db.close();
//                    return 1;
//                }
//            } while (cursor.moveToNext());
//        }
//        db.close();
//        return 0;
//    }

}