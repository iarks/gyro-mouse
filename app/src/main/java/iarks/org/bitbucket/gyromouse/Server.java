package iarks.org.bitbucket.gyromouse;

/**
 * Created by Arkadeep on 9/6/2017.
 */

class Server
{
    private String serverIP, serverName;
    private int serverID;

    Server()
    {

    }

    Server(int serverID, String serverName, String serverIP)
    {
        this.serverID=serverID;
        this.serverIP=serverIP;
        this.serverName=serverName;
    }

    void setServerID(int serverID)
    {
        this.serverID = serverID;
    }

    void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    String getServerIP()
    {
        return serverIP;
    }

    String getServerName()
    {
        return serverName;
    }

    int getServerID()
    {
        return serverID;
    }
}
