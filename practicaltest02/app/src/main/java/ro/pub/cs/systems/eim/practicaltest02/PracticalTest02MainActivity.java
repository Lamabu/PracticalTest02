package ro.pub.cs.systems.eim.practicaltest02;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import General.Constants;
import Network.ClientThread;
import Network.ServerThread;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText serverPortEditText = null;
    private Button connectButton  = null;

    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private EditText cityEditText = null;

    private Spinner informationTypeSpinner = null;
    private Button getWeatherForecastButton = null;
    private TextView weatherForecastTextView = null;

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    private  class ConnectButtonClickListener implements Button.OnClickListener{

        @Override
        public void onClick(View view){
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread");
                return;
            }
            serverThread.start();
        }
    }

    private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();


    private class  GetWeatherForecastButtonClickListener implements Button.OnClickListener{

        @Override
        public  void onClick(View view) {
            String clientAddress = "localhost";
            //String clientPort = clientPortEditText.getText().toString();
            String clientPort = serverPortEditText.getText().toString();

            String city = cityEditText.getText().toString();
     //       String informationType = informationTypeSpinner.getSelectedItem().toString();

          /*  if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty())
            {
                Toast.makeText(getApplicationContext(), "[MAIN ACIVITY] Client connection parameter should be filled!",Toast.LENGTH_SHORT).show();
                return;
            }*/

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
/*
            if (city == null || city.isEmpty()
                    || informationType == null || informationType.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters for client should be filled", Toast.LENGTH_SHORT).show();
                return;
            }
*/
            if (city == null || city.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters for client should be filled", Toast.LENGTH_SHORT).show();
                return;
            }
            weatherForecastTextView.setText(Constants.EMPTY_STRING);
            //clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), city, informationType, weatherForecastTextView);
            clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), city, weatherForecastTextView);

            clientThread.start();
        }
    }

    private  GetWeatherForecastButtonClickListener getWeatherForecastButtonClickListener = new GetWeatherForecastButtonClickListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        connectButton = (Button)findViewById((R.id.connect_button));
        connectButton.setOnClickListener(connectButtonClickListener);

      //  clientAddressEditText = (EditText)findViewById(R.id.client_address_edit_text);
       // clientPortEditText = (EditText)findViewById(R.id.client_port_edit_text);
        cityEditText = (EditText)findViewById(R.id.city_edit_text);
      //  informationTypeSpinner = (Spinner)findViewById(R.id.information_type_spinner);
        weatherForecastTextView = (TextView)findViewById(R.id.weather_forecast_text_view);
        getWeatherForecastButton = (Button)findViewById(R.id.get_weather_forecast_button);
        getWeatherForecastButton.setOnClickListener(getWeatherForecastButtonClickListener);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy callback method was invoked");
        if (serverThread != null)
            serverThread.stopThread();
        super.onDestroy();
    }
}
