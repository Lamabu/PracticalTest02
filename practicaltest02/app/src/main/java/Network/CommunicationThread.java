package Network;

import android.provider.DocumentsContract;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Documented;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import General.Constants;
import General.Utilities;
import Model.WeatherForecastInformation;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by laura on 5/17/17.
 */

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread (ServerThread serverThread, Socket socket){
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run(){
        if (socket == null) {
            Log.e(Constants.TAG, "[COMM THREAD] Socket is null!");
            return;
        }

        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMM THREAD] Buffers are null");
                return;
            }

            Log.i(Constants.TAG, "[COMM THREAD] Wainting for word  from client");

            String city = bufferedReader.readLine();
       //     String informationType = bufferedReader.readLine();
            //System.out.println(city);
            if (city == null || city.isEmpty() ) {
                Log.e(Constants.TAG, "[COMM THREAD] Error on receiving info");
                return;
            }

            HashMap<String,String> data = serverThread.getData();

         //   HashMap<String, WeatherForecastInformation> data= serverThread.getData();
            String weatherForecastInformation = null;
            if (data.containsKey(city)) {
                Log.i(Constants.TAG, "[COMM THREAD] Getting info from cache");
                weatherForecastInformation = data.get(city);
            }
            else {
                Log.i(Constants.TAG, "COMM THREAD] Getting info from web service");
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS
                        + "?" + Constants.ANA + "=" + city);

                ResponseHandler<String> responseHandlerGet = new BasicResponseHandler();
                weatherForecastInformation = httpClient.execute(httpGet, responseHandlerGet);
            //    System.out.print(weatherForecastInformation);

                serverThread.setData(city, weatherForecastInformation);

            }

            if (weatherForecastInformation == null) {
                Log.e(Constants.TAG, "[COMM THREAD] WeatherForecastInformation is null");
                return;
            }


            String result = null;
            result = weatherForecastInformation;
         //   System.out.print(result);
            printWriter.println(result);
            printWriter.flush();

        } catch (IOException ioException){
            Log.e(Constants.TAG, "[COMM THREAD] An exception has occurred " + ioException.getMessage());
            }
         /* catch (JSONException jsonException) {
              Log.e(Constants.TAG, "[COMM THREAD] An exception has occurred " + jsonException.getMessage());
          }*/
        finally {
             if (socket != null)
                 try {
                     socket.close();
                 } catch (IOException ioExeption) {
                     Log.e(Constants.TAG, "[COMM THREAD] An exception has occurred " + ioExeption.getMessage());
                 }
        }

    }
}
