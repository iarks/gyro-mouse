package iarks.org.bitbucket.gyromouse;

import java.net.InetAddress;
import java.net.Socket;

import static iarks.org.bitbucket.gyromouse.MainActivity.getTCPPort;
import static iarks.org.bitbucket.gyromouse.MainActivity.getUdpPort;

class ConnectedServer
{
    static String sessionKey, serverIP = "unavailable", serverName = "unavailable", tcpPort = getTCPPort(),udpPort = getUdpPort();
    static Socket clientTcpSocket;
    static InetAddress serverInetAddress;

    private ConnectedServer(){}

    static void reset()
    {
        serverName = "unavailable";
        serverIP = "unavailable";
        sessionKey="";
        clientTcpSocket =null;
    }
}
