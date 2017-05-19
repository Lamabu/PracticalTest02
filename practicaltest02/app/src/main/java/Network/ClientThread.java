package Network;

import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.SocketHandler;

import General.Constants;
import General.Utilities;

/**
 * Created by laura on 5/17/17.
 */

public class ClientThread extends Thread {
    private String address;
    private int port;
    private String city;
    private String informationType;
    private TextView weatherForecastTextView;

    private Socket socket;

    public ClientThread(String address,int port, String city, TextView weatherForecastTextView) {
        this.address = address;
        this.port = port;
        this.city = city;
      //  this.informationType = informationType;
        this.weatherForecastTextView = weatherForecastTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Client could not create socket!");
                return;
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffers are null");
                return;
            }

            printWriter.println(city);
            printWriter.flush();
     //       printWriter.println(informationType);
        //    printWriter.flush();

            String anagrams;

            while ((anagrams = bufferedReader.readLine()) != null) {
                System.out.print(anagrams);
                final String finalizedAnagrams = anagrams;
                weatherForecastTextView.post(new Runnable() {
                    @Override
                    public void run() {
                        weatherForecastTextView.setText(weatherForecastTextView.getText().toString() + "\n"+ finalizedAnagrams);
                    }
                });
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        }
        finally {
            if (socket != null)
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                }
        }
    }
}
