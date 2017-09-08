package iarks.org.bitbucket.gyromouse;
import android.util.Log;

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
    private boolean connected=false;
    private static List<Server> list = new ArrayList<>();
    private UDPClient udpClient;

    ServerHandler(DatabaseHandler dbHandler, UDPClient udpClient)
    {
        this.dbHandler = dbHandler;
        this.udpClient = udpClient;
    }

    @Override
    public void run()
    {
        // ist priority - connect to already present servers
        // check number of preexisting servers
        int count = dbHandler.getServerCount();

        Toaster.toast("DATABASE COUNT"+count);

        // if server count is more than 0, then preexisting servers are present - try to connect to those
        if(count>0)
        {
            // get a list of all the servers in the database
            List<Server> serverList = dbHandler.getAllDBServers();

            // iterate through list and try to connect to each server
            for (Server object: serverList)
            {
                // connect to that servers IP - connect TCP returns true if connection is established otherwise returns false
                boolean check = connectTCP(object);
                //check if connection is established
                if(check)
                {
                    //if connection established- set a flag and break - no need to iterate any more
                    connected=true;
                    break;
                }
            }
        }
        else if(count==0 || !connected)// if server count is 0 or all previous servers fail to connect come here
        {
            Toaster.toast("No Predefined Server available");
            Toaster.toast("Searching for servers on local network");

            // do a broadcasting server search - this fills the global list with any servers on the network
            searchServer();

            if (list.size() > 0)
            {
                // means we have servers on the network
                Toaster.toast("We have responses");

                //connect to the first server on this list
                // TODO: 9/8/2017 or ask the used to manually select
                // TODO: 9/8/2017 maybe add these servers to database?
                connectTCP(list.remove(0));
            }
            else// if no servers are available on the internet as well, just give up. ask user to connect manually or search again
            {
                Toaster.toast("NO SERVERS AVAILABLE. CONNECT MANUALLY");
                return;
            }
        }


        while (true)
        {
            try
            {
                DataOutputStream outToServer = new DataOutputStream(CurrentServer.tcpSocket.getOutputStream());
                DataInputStream inFromServer = new DataInputStream(CurrentServer.tcpSocket.getInputStream());

                String receivedString = null;
                byte[] receivedBytes = new byte[256];
                int i;


                while ((i = inFromServer.read(receivedBytes, 0, receivedBytes.length)) != 0)
                {
                    // Translate data bytes to a ASCII string.
                    receivedString = new String(receivedBytes);
                    receivedString = receivedString.trim();
                    Log.i(getClass().getName(),"Received from server>> "+ receivedString.trim());
                    break;
                }

                if (receivedString.equals("UDERE?"))
                {
                    Log.i(getClass().getName(), "SERVER IS PINGING");
                    outToServer.write("YES".getBytes(), 0, "YES".getBytes().length);
                    outToServer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean connectTCP(Server server)
    {
        try
        {
            // create a new socket for a new client
            Socket clientSocket = new Socket();

            //connect this socket to the servers - details are provided
            clientSocket.connect(new InetSocketAddress(server.getServerIP(), 13000), 2000);

            // initiate OP stream and ask for connection
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.write("CANCONNECT?".getBytes(), 0, "CANCONNECT?".getBytes().length);

            // initiate IP stream
            DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());

            // prepare to read
            String receivedString = null;
            byte[] receivedBytes = new byte[256];
            int i;

            // read from server
            while ((i = inFromServer.read(receivedBytes, 0, receivedBytes.length)) != 0)
            {
                // Translate data bytes to a ASCII string.
                receivedString = new String(receivedBytes);
                receivedString = receivedString.trim();
                Log.i(getClass().getName(), "Received from server>> "+receivedString.trim());
                break;
            }

            if (receivedString.equals("BUSY"))
            {
                // if server returns busy it may be connected to another client
                Log.i(getClass().getName(), "Server busy at the moment cannot connect now");
                return false;
            }
            CurrentServer.serverIP = server.getServerIP();

            CurrentServer.sessionKey = receivedString;

            CurrentServer.tcpSocket = clientSocket;

            CurrentServer.inetAddress = InetAddress.getByName(server.getServerIP());

            udpClient.udpSetup();

            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e("", "Whoops! It didn't work!\n");
        }
        return false;
    }

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
            Log.i(getClass().getName() , ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
        }
        catch(Exception e)
        {

        }

        try
        {
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
                    Log.i(getClass().getName() ,">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }
            Log.i(getClass().getName() , ">>> Done looping over all network interfaces. Now waiting for a reply!");

            while (true)
            {
                // Wait for a response
                byte[] recvBuf = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                datagramSocket.receive(receivePacket);

                // We have a response
                Log.i(getClass().getName() , ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());
                Toaster.toast("These available");
                // check if message is correct - no need
                Toaster.toast(receivePacket.getAddress().getHostAddress()+"-"+new String(receivePacket.getData()));

                String name = new String(receivePacket.getData());
                list.add(new Server(99, name,receivePacket.getAddress().getHostAddress()));

                Toaster.toast(receivePacket.getAddress().getHostAddress()+"-"+new String(receivePacket.getData()));
            }
        } catch (SocketTimeoutException e)
        {
            Log.i(getClass().getName() , ">>> SOCKET TIMED OUT");
            Toaster.toast("SOCKET TIMED OUT");
        } catch (IOException e) {
            //no hosts discovered
        } finally {
            if (datagramSocket != null)
                datagramSocket.close();
        }
    }
}

