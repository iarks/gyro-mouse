package iarks.org.bitbucket.gyromouse;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHandler extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "gMouseServers";
    private static final String TABLE_SERVERS = "servers";
    private static final String SERVER_ID = "serverID";
    private static final String SERVER_NAME = "serverName";
    private static final String SERVER_IP = "serverip";

    DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SERVERS + "("
                + SERVER_ID + " INTEGER PRIMARY KEY," + SERVER_NAME + " TEXT,"
                + SERVER_IP + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
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

    // code to add the new server
    void addServer(Server server)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SERVER_NAME, server.getServerName()); // Contact Name
        values.put(SERVER_IP, server.getServerIP()); // Contact Phone

        // Inserting Row
        db.insert(TABLE_SERVERS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    Server getServer(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SERVERS, new String[] {SERVER_ID, SERVER_NAME, SERVER_IP}, SERVER_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Server server = new Server(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));

        // return contact
        return server;
    }

    // code to get all contacts in a list view
    public List<Server> getAllServers()
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
                // Adding server to list
                serverList.add(server);
            } while (cursor.moveToNext());
        }

        // return contact list
        return serverList;
    }

    // code to update the single contact
    public int updateServer(Server server)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SERVER_NAME, server.getServerName());
        values.put(SERVER_IP, server.getServerIP());

        // updating row
        return db.update(TABLE_SERVERS, values, SERVER_ID + " = ?", new String[] { String.valueOf(server.getServerID()) });
    }

    // Deleting single server
    public void deleteServer(Server server)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SERVERS, SERVER_ID + " = ?", new String[] { String.valueOf(server.getServerID()) });
        db.close();
    }

    // Getting contacts Count
    public int getServerCount()
    {
        String countQuery = "SELECT  * FROM " + TABLE_SERVERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}