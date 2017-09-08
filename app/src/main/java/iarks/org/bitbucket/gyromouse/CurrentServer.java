package iarks.org.bitbucket.gyromouse;

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Arkadeep on 9/7/2017.
 */

class CurrentServer
{
    static String sessionKey, serverIP, serverName, tcpPort,udpPort;
    static Socket tcpSocket;

    static InetAddress inetAddress;


    private CurrentServer()
    {

    }
}
