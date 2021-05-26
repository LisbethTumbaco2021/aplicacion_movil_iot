package com.example.iot;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonElement;

import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.fabric.sdk.android.Fabric;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static final String url = "http://192.168.1.3/iot/public";
    private Button boton, botonConsumo;
    private TextView texto;
    private ImageView imagen;
    private TextView temp;
    private TextView amperaje;
    private TextView tempInfo;
    private static String acciones = "encender";
    private ProgressBar barra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //peticion("1");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        boton =  findViewById(R.id.boton);
        botonConsumo = findViewById(R.id.btnConsumo);
        botonConsumo.setText("Datos Consumo");
        imagen = findViewById(R.id.imageView);
        texto = findViewById(R.id.texto);
        temp = findViewById(R.id.temp);
        amperaje = findViewById(R.id.amperaje);
        tempInfo = findViewById(R.id.tempInfo);
        barra = (ProgressBar) findViewById(R.id.barra);
        peticionTodo("estado");
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Otra vez", "ESTADOS XXXX "+MainActivity.acciones);
                peticion(MainActivity.acciones);
            }
        });
        botonConsumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActividadConsumo.class);
                startActivity(intent);
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void peticion(final String accion) {
        if(verificarConexion()) {

            new Thread(new Runnable(){
                @Override
                public void run() {
                    Log.e("MainActivity", "ENTER AQUI "+MainActivity.url+ "/"+accion);
                    try {
                        URL url = new URL(MainActivity.url+ "/"+accion);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        try {

                            JsonElement elemento = estadoJson(urlConnection);
                            String estado = elemento.getAsJsonObject().get("foco").getAsString();
                            String estadoT = elemento.getAsJsonObject().get("potencia").getAsString();
                            String tempe = elemento.getAsJsonObject().get("amperage").getAsString();
                           // String estado = estado(urlConnection);

                            Log.e("MainActivity", "ENTER AQUI estados **"+estado+"**");
                            if(estado.equalsIgnoreCase("0")) {
                                MainActivity.acciones = "encender";
                                boton.setText("Prender");
                                imagen.setImageResource(R.drawable.apagado);
                                Log.e("MainActivity", "ESTA APAGADO ***********");
                                texto.setText("FOCO APAGADO");
                            }else if(estado.equalsIgnoreCase("1")){
                                MainActivity.acciones = "apagar";
                                imagen.setImageResource(R.drawable.encendido);

                                boton.setText("Apagar");
                                Log.e("MainActivity", "ESTA PRENDIDO ***********");
                                texto.setText("FOCO PRENDIDO");
                            } else if(estado.equalsIgnoreCase("404")) {
                                boton.setEnabled(false);
                                //imagen.setImageResource(R.drawable.sin_senal);
                                texto.setText("No hay conexion");
                            }

                            if(tempe != null) {
                                tempInfo.setText("Voltage: "+estadoT);
                                amperaje.setText("Amperaje: "+tempe);

                            }
                            // Acciones a realizar con el flujo de datos
                            Log.e("MainActivity", "Hubo respuesta "+MainActivity.acciones);
                        }catch (Exception ex){
                            Log.e("MainActivity", "Hubro un error "+ex);
                            texto.setText("No hay conexion");
                            boton.setText("No hay conexion");
                        }
                        finally {
                            urlConnection.disconnect();
                        }
                    } catch (Exception e) {
                        Log.e("MainActivity", "Hubro un errors "+e);
                    }
                }
            }).start();
        } else {
            texto.setText("No hay conexion a internet");
        }


    }

    private void peticionTodo(final String accion) {
        if(verificarConexion()) {
            //barra.setVisibility(View.VISIBLE);
            new Thread(new Runnable(){
                @Override
                public void run() {
                    Log.e("MainActivity", "ENTER AQUI "+MainActivity.url+ "/"+accion);
                    try {

                        URL url = new URL(MainActivity.url+ "/"+accion);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        try {
                            JsonElement elemento = estadoJson(urlConnection);
                            String estado = elemento.getAsJsonObject().get("foco").getAsString();
                            String estadoT = elemento.getAsJsonObject().get("potencia").getAsString();
                            String tempe = elemento.getAsJsonObject().get("amperage").getAsString();
                            Log.e("haber", "*****************" + elemento);
                            if(estado == null) {
                                estado = elemento.getAsJsonObject().get("status").getAsString();
                            }
                            Log.e("MainActivity", "ENTER AQUI estados **"+estado+"**");
                            if(estado.equalsIgnoreCase("0")) {
                                MainActivity.acciones = "encender";
                                boton.setText("Prender");
                                Log.e("MainActivity", "ESTA APAGADO ***********");
                                texto.setText("FOCO APAGADO");
                            }else if(estado.equalsIgnoreCase("1")){
                                MainActivity.acciones = "apagar";
                                boton.setText("Apagar");
                                Log.e("MainActivity", "ESTA PRENDIDO ***********");
                                texto.setText("FOCO PRENDIDO");
                            } else if(estado.equalsIgnoreCase("404")) {
                                boton.setEnabled(false);
                                texto.setText("No hay conexion");
                            }
                            if(tempe != null) {
                                tempInfo.setText("Voltage: "+estadoT);
                                amperaje.setText("Amperaje: "+tempe);

                            }

                            // Acciones a realizar con el flujo de datos
                            Log.e("MainActivity", "Hubo respuesta "+MainActivity.acciones);
                        }catch (Exception ex){
                            Log.e("MainActivity", "Hubro un error "+ex);
                            texto.setText("No hay conexion");
                            boton.setText("No hay conexion");
                        }
                        finally {
                            urlConnection.disconnect();
              //              barra.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        Log.e("MainActivity", "Hubro un errors "+e);
                //        barra.setVisibility(View.GONE);
                    }
                }
            }).start();
        } else {
            texto.setText("No hay conexion a internet");
        }


    }

    private boolean verificarConexion() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

    }
    private String estado(HttpURLConnection urlConnection) throws Exception {
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        JsonReader jsonReader = new JsonReader(new InputStreamReader(in));
        JsonParser jsonParser = new JsonParser();
        JsonElement data =  jsonParser.parse(jsonReader);
        String estado = data.getAsJsonObject().get("foco").getAsString();
        Log.e("haber", "*****************" + data);
        if(estado == null) {
            estado = data.getAsJsonObject().get("status").getAsString();
        }
        return estado;
    }

    private JsonElement estadoJson(HttpURLConnection urlConnection) throws Exception {
        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
        JsonReader jsonReader = new JsonReader(new InputStreamReader(in));
        JsonParser jsonParser = new JsonParser();
        JsonElement data =  jsonParser.parse(jsonReader);

        return data;
    }

    @Override protected void onResume() { super.onResume(); Log.e("onResume","Segundo plano onResume");
        peticionTodo("estado");
    }
    //@Override protected void onPause() { super.onPause();  Log.e("onPause","Segundo plano onPause");}

}
