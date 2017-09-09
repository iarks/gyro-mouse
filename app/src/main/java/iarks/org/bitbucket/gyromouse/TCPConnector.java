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

import xdroid.toaster.Toaster;

class TCPConnector
{
    static boolean connectTCP(Server server, UDPClient udpClientRef)
    {
        UDPClient udpClient = udpClientRef;
        try
        {
            Socket clientSocket;
            try
            {
                // create a new socket for a new client
                clientSocket = new Socket();

                //connect this socket to the servers - details are provided
                clientSocket.connect(new InetSocketAddress(server.getServerIP(), 13000), 2000);
            }
            catch (Exception e)
            {
                return false;
            }

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
                Log.i("TCPConnector", "Received from server>> "+receivedString.trim());
                break;
            }

            if (receivedString.equals("BUSY"))
            {
                // if server returns busy it may be connected to another client
                Log.i("TCPConnector", "Server busy at the moment cannot connect now");
                return false;
            }
            CurrentServer.serverIP = server.getServerIP();

            CurrentServer.sessionKey = receivedString;

            CurrentServer.tcpSocket = clientSocket;

            CurrentServer.inetAddress = InetAddress.getByName(server.getServerIP());

            CurrentServer.serverName = server.getServerName();

            udpClient.udpSetup();

            return true;
        }
        catch (IOException e)
        {

            Log.e("TCPConnector", "Whoops! It didn't work!\n");
            e.printStackTrace();
            Log.e("TCPConnector","CAUSE " +e.getCause().toString());
            Log.e("TCPConnector","MESSAGE " +e.getMessage());
            return false;
        }

    }

    static ArrayList<Server> searchServer()
    {
        ArrayList<Server> list = new ArrayList<>();
        list.clear();

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
            Log.i("TCPConnector" , ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
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
                    Log.i("TCPConnector" ,">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }
            Log.i("TCPConnector" , ">>> Done looping over all network interfaces. Now waiting for a reply!");

            while (true)
            {
                // Wait for a response
                byte[] recvBuf = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                datagramSocket.receive(receivePacket);

                // We have a response
                Log.i("TCPConnector" , ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());
                Toaster.toast("These available");
                // check if message is correct - no need
                Toaster.toast(receivePacket.getAddress().getHostAddress()+"-"+new String(receivePacket.getData()));

                String name = new String(receivePacket.getData()).trim();
                String id = "_"+name+"_"+receivePacket.getAddress().getHostAddress();

                list.add(new Server(id, name, receivePacket.getAddress().getHostAddress()));

                Toaster.toast(receivePacket.getAddress().getHostAddress()+"-"+new String(receivePacket.getData()));
            }
        }
        catch (SocketTimeoutException e)
        {
            Log.i("TCPConnector" , ">>> SOCKET TIMED OUT");
            Toaster.toast("SOCKET TIMED OUT");
        }
        catch (IOException e)
        {
            //no hosts discovered
        }
        finally
        {
            if (datagramSocket != null)
                datagramSocket.close();
            return list;
        }
    }
}
