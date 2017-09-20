package iarks.org.bitbucket.gyromouse;

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

class NetworkScannerUtil
{
    static ArrayList<Server> searchServer()
    {
        ArrayList<Server> list = new ArrayList<>();
        list.clear();


        byte[] sendData = "CANHAVEIP?;x;GMO".getBytes();


        DatagramSocket datagramSocket = null;

        try
        {
            datagramSocket = new DatagramSocket();
            datagramSocket.setSoTimeout(3000);
            datagramSocket.setBroadcast(true);
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }

        try
        {
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), Integer.parseInt(ConnectedServer.udpPort));
            datagramSocket.send(sendPacket);
            Log.i("NetworkUtil" , ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }


        try
        {
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements())
            {
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
                    try
                    {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                        datagramSocket.send(sendPacket);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    Log.i("NetworkUtil" ,">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }
            Log.i("NetworkUtil" , ">>> Done looping over all network interfaces. Now waiting for a reply!");

            while (true)
            {
                // Wait for a response
                byte[] recvBuf = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                datagramSocket.receive(receivePacket);

                // We have a response
                Log.i("NetworkUtil" , ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());
                // check if message is correct - no need

                String name = new String(receivePacket.getData()).trim();
                String id = "_"+name+"_"+receivePacket.getAddress().getHostAddress();

                list.add(new Server(id, name, receivePacket.getAddress().getHostAddress()));

            }
        }
        catch (SocketTimeoutException e)
        {
            Log.i("NetworkUtil" , ">>> SOCKET TIMED OUT");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (datagramSocket != null)
                datagramSocket.close();
            return list;
        }
    }
}