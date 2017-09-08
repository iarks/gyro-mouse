package iarks.org.bitbucket.gyromouse;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import xdroid.toaster.Toaster;

class ServerHandler implements Runnable
{
    private DatabaseHandler dbHandler;
    private int connected=0;
    private static List<String> list = new ArrayList<>();
    private UDPClient udpClient;

    String discoveredServer=null;

    ServerHandler(DatabaseHandler dbHandler, UDPClient udpClient)
    {
        this.dbHandler = dbHandler;
        this.udpClient=udpClient;
    }

    @Override
    public void run()
    {
//        int x = dbHandler.getServerCount();
        int x=0;
        if(x>0)
        {
            List<Server> serverList = dbHandler.getAllServers();

            for (Server object: serverList)
            {
                boolean check = connectTCP(object.getServerIP());
                if(check)
                {
                    connected=1;
                    break;
                }
            }
        }
        else if(x==0 || connected == 0)
        {
            Toaster.toast("No Predefined Server available");
            Toaster.toast("Searching for servers on local network");
            searchServer();
            if (list.size() > 0)
            {
                Toaster.toast("We have responses");
                connectTCP(list.remove(0));
            }
            else
            {
                Toaster.toast("NO SERVERS AVAILABLE. CONNECT MANUALLY");
                return;
            }
        }


        while (true)
        {
            try {
                DataOutputStream outToServer = new DataOutputStream(CurrentServer.tcpSocket.getOutputStream());
                DataInputStream inFromServer = new DataInputStream(CurrentServer.tcpSocket.getInputStream());

                String receivedString = null;
                byte[] receivedBytes = new byte[256];
                int i;


                while ((i = inFromServer.read(receivedBytes, 0, receivedBytes.length)) != 0) {
                    // Translate data bytes to a ASCII string.
                    receivedString = new String(receivedBytes);
                    receivedString = receivedString.trim();
                    Log.e("Received from client>> ", receivedString.trim());
                    break;
                }

                if (receivedString.equals("UDERE?"))
                {
                    Log.e(getClass().getName(), "SERVER IS PINGING");
                    outToServer.write("YES".getBytes(), 0, "YES".getBytes().length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean connectTCP(String ip)
    {
        try
        {
            // this connects client to server
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(ip, 13000), 2000);

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.write("CANCONNECT?".getBytes(), 0, "CANCONNECT?".getBytes().length);

            DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());

            String receivedString = null;
            byte[] receivedBytes = new byte[256];
            int i;

            while ((i = inFromServer.read(receivedBytes, 0, receivedBytes.length)) != 0)
            {
                // Translate data bytes to a ASCII string.
                receivedString = new String(receivedBytes);
                receivedString = receivedString.trim();
                Log.e("Received from client>> ", receivedString.trim());
                break;
            }

            if (receivedString.equals("BUSY"))
            {
                Log.e(getClass().getName(), "Server busy at the moment cannot connect now");
                return false;
            }
            CurrentServer.serverIP = ip;

            CurrentServer.sessionKey = receivedString;

            CurrentServer.tcpSocket = clientSocket;

            CurrentServer.inetAddress = InetAddress.getByName(ip);

            udpClient.udpSetup();

            // TODO: 9/8/2017 solve this thing below
            //CurrentServer.serverName = getServerName();


            //udp and tcp port numbers are defined in shared preferences

            // TODO: 9/7/2017 Server.name = get the server name
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e("", "Whoops! It didn't work!\n");
        }
        return false;
    }
    // TODO: 9/8/2017 solve this

//    String getServerName()
//    {
//        try
//        {
//            DatagramSocket dskt = new DatagramSocket();
//            byte[] data =  "GIMMENAME?".getBytes();
//            DatagramPacket sendPacket = new DatagramPacket(data , data.length, CurrentServer.inetAddress, //get port from shared prefs);
//            ;
//        }catch (SocketException e)
//        {
//            e.printStackTrace();
//        }
//
//    }

    private void searchServer()
    {
        list.clear();
        String toConnect = null;
        Toaster.toast("In search server");
        byte[] sendData = "CANHAVEIP?;x;GMO".getBytes();
        DatagramSocket datagramSocket = null;

        try
        {
            datagramSocket = new DatagramSocket();
            datagramSocket.setSoTimeout(10000);
            datagramSocket.setBroadcast(true);
        }
        catch (SocketException e)
        {
            Toaster.toast("SOCKET EXCEPTION");
            e.printStackTrace();
        }

        try
        {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), Integer.parseInt(CurrentServer.udpPort));
            datagramSocket.send(sendPacket);
            Log.e(getClass().getName() , ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
        } catch (Exception e) {

        }

        try {
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback // interface
                }
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }
                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                        datagramSocket.send(sendPacket);
                    } catch (Exception e) {

                    }
                    Log.e(getClass().getName() ,">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }
            Log.e(getClass().getName() , ">>> Done looping over all network interfaces. Now waiting for a reply!");

            while (true) {
                // Wait for a response
                byte[] recvBuf = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                datagramSocket.receive(receivePacket);

                // We have a response
                Log.e(getClass().getName() , ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

                // check if message is correct - no need
                Toaster.toast(receivePacket.getAddress().getHostAddress());
                list.add(receivePacket.getAddress().getHostAddress());
            }
        } catch (SocketTimeoutException e)
        {
            Log.e(getClass().getName() , ">>> SOCKET TIMED OUT");
            Toaster.toast("SOCKET TIMED OUT");
        } catch (IOException e) {
            //no hosts discovered
        } finally {
            if (datagramSocket != null)
                datagramSocket.close();
        }
    }
}

