package com.prashant.adesara.socket.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import android.util.Log;

/**
 * @author Prashant Adesara
 * Handle the TCPClient with Socket Server. 
 * */

public class TCPClient {
 
    private String serverMessage;
    /**
	 * Specify the Server Ip Address here. Whereas our Socket Server is started.
	 * */
	public static final String SERVERIP = "192.168.1.2"; // your computer IP address
    public static final int SERVERPORT = 5657;
    private OnMessageReceived mMessageListener = null;
    private boolean mRun = false;
 
    private PrintWriter out = null;
    private BufferedReader in = null;
    
    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(final OnMessageReceived listener) 
    {
        mMessageListener = listener;
    }
 
    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
        	System.out.println("message: "+ message);
            out.println(message);
            out.flush();
        }
    }
 
    public void stopClient(){
        mRun = false;
    }
    
    public void run() {
 
        mRun = true;
 
        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);
 
            Log.e("TCP SI Client", "SI: Connecting...");
 
            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVERPORT);
            try {
          
                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
 
                Log.e("TCP SI Client", "SI: Sent.");
 
                Log.e("TCP SI Client", "SI: Done.");
                
                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
 
                //in this while the client listens for the messages sent by the server
                while (mRun) {
                	serverMessage = in.readLine();
 
                    if (serverMessage != null && mMessageListener != null) {
                        //call the method messageReceived from MyActivity class
                        mMessageListener.messageReceived(serverMessage);
                        Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");
                    }
                    serverMessage = null;
                }
            }
            catch (Exception e) 
            {
                Log.e("TCP SI Error", "SI: Error", e);
                e.printStackTrace();
            }
            finally 
            {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }
 
        } catch (Exception e) {
 
            Log.e("TCP SI Error", "SI: Error", e);
 
        }
 
    }
 
    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}