package iarks.org.bitbucket.gyromouse;

import android.content.SharedPreferences;

import java.util.concurrent.CyclicBarrier;

class Globals
{
    static DatabaseHandler databaseHandler;
    static UDPClientUtil udpClientUtil;
    static CyclicBarrier cdLatch;
    static int advanceChanged=0;
    static SharedPreferences sharedPreferences;
}
