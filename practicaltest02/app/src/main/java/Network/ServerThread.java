package Network;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.SocketHandler;

import General.Constants;
import Model.WeatherForecastInformation;
import cz.msebera.android.httpclient.client.ClientProtocolException;

/**
 * Created by laura on 5/17/17.
 */

public class ServerThread extends  Thread{
    private int port = 0;
    private ServerSocket serverSocket = null;

    private HashMap<String, String> data = null;

    public ServerThread(int port){
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
        }
        this.data = new HashMap<>();
    }

    public synchronized void setData(String city, String weatherForecastInformation){
        //System.out.print(weatherForecastInformation);
        data.put(city, weatherForecastInformation);
    }

    public synchronized HashMap<String, String> getData() {
        return data;
    }


    public ServerSocket getServerSocket(){
        return serverSocket;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER THREAD] Waiting for client invocation...");
                Socket socket = serverSocket.accept();
                Log.i(Constants.TAG, "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (ClientProtocolException clientProtocolException){
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + clientProtocolException.getMessage());
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioExecption) {
                Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred " + ioExecption.getMessage());
            }
        }
    }
}
