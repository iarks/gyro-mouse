package iarks.org.bitbucket.gyromouse;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.net.InetAddress;
import java.net.Socket;

import static iarks.org.bitbucket.gyromouse.MainActivity.getTCPPort;
import static iarks.org.bitbucket.gyromouse.MainActivity.getUdpPort;

class CurrentServer
{

    static String sessionKey, serverIP = "unavailable", serverName = "unavailable", tcpPort = getTCPPort(),udpPort = getUdpPort();
    static Socket tcpSocket;




    static InetAddress inetAddress;

    private CurrentServer(){}







}
