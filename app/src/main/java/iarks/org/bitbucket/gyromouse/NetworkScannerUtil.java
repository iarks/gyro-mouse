package iarks.org.bitbucket.gyromouse;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class NetworkScannerUtil
{
    private final static String IP_REQUEST_STRING = "CANHAVEIP?;x;GMO";

    // this function scans the network for available servers and returns a list of all identified servers
    static ArrayList<Server> searchServer(Context context)
    {

        HashMap<String,Server> list = new HashMap<>();
        list.clear();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        byte[] sendData = IP_REQUEST_STRING.getBytes();
        DatagramSocket datagramSocket = null;

        try
        {
            Log.i(context.getClass().getName(),"initialising random port on client");
            datagramSocket = new DatagramSocket();
            datagramSocket.setSoTimeout(3000);
            datagramSocket.setBroadcast(true);
            Log.i(context.getClass().getName(),"initialising random port on client complete");
        }
        catch (SocketException e)
        {
            Log.e(context.getClass().getName(),"unable to initialise any port");
            e.printStackTrace();
            Log.e(context.getClass().getName() , "closing socket if opened");
            if (datagramSocket != null)
                datagramSocket.close();
            return new ArrayList<>(list.values());
        }


        try
        {
            Log.e(context.getClass().getName() , "sending request packet to: 255.255.255.255 (DEFAULT)");
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), Integer.parseInt(prefs.getString("udpPort","9080")));
            datagramSocket.send(sendPacket);
            Log.e(context.getClass().getName() , "sending request packet to: 255.255.255.255 (DEFAULT) complete");
        }

        catch(IOException e)
        {
            Log.e(context.getClass().getName() , "unable to send broadcast packet - IOException");
            e.printStackTrace();
        }
        catch(NullPointerException e)
        {
            Log.e(context.getClass().getName() , "unable to send broadcast packet - NullPointerException");
            e.printStackTrace();
        }

        try
        {
            Log.e(context.getClass().getName() , "broadcasting over all network interfaces individually");

            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp())
                {
                    continue; // Don't want to broadcast to the loopback // interface
                }
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
                {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null)
                    {
                        continue;
                    }
                    // Send the broadcast package!
                    try
                    {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, Integer.parseInt(prefs.getString("udpPort","9080")));
                        datagramSocket.send(sendPacket);
                    }
                    catch (NullPointerException e)
                    {
                        Log.e(context.getClass().getName(),"null pointer exception");
                        e.printStackTrace();
                    }
                    Log.e(context.getClass().getName() ,"Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }
            Log.e(context.getClass().getName() , "broadcasting over all network interfaces individually complete");

            while (true)
            {
                // Wait for a response
                Log.e(context.getClass().getName() , "waiting for reply");
                byte[] recvBuf = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                try
                {
                    datagramSocket.receive(receivePacket);
                }
                catch (NullPointerException e)
                {
                    Log.e(context.getClass().getName(), "error receiving response");
                    e.printStackTrace();
                }
                Log.e(context.getClass().getName() , "response from server: " + receivePacket.getAddress().getHostAddress());

                String name = new String(receivePacket.getData()).trim();
                String id = "_"+name+"_"+receivePacket.getAddress().getHostAddress();
                list.put(id, new Server(id, name, receivePacket.getAddress().getHostAddress()));
            }
        }
        catch (SocketTimeoutException e)
        {
            Log.e(context.getClass().getName() , "socket timed out");
            e.printStackTrace();

            return new ArrayList<>(list.values());
        }
        catch (IOException e)
        {
            Log.e(context.getClass().getName() , "IOExcetion occurred");
            e.printStackTrace();

            return new ArrayList<>(list.values());
        }
        finally
        {
            Log.e(context.getClass().getName() , "closing socket");
            if (datagramSocket != null)
                datagramSocket.close();
        }
    }
}
