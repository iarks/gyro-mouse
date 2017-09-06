package iarks.org.bitbucket.gyromouse;

public class Server
{
    private String ipAddress,name,portAddress;

    public Server(String ipAddress, String name, String portAddress)
    {
        this.ipAddress = ipAddress;
        this.name = name;
        this.portAddress = portAddress;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public String getServerName() {
        return name;
    }

    public String getPortAddress() {
        return portAddress;
    }
}
