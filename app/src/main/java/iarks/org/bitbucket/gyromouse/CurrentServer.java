package iarks.org.bitbucket.gyromouse;
import java.net.InetAddress;
import java.net.Socket;

class CurrentServer
{
    static String sessionKey, serverIP = "unavailable", serverName = "unavailable", tcpPort,udpPort;
    static Socket tcpSocket;

    static InetAddress inetAddress;

    private CurrentServer(){};

}
