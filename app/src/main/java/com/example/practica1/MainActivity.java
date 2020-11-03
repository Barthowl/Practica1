package com.example.practica1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.example.practica1.calls.calls;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // para comprobar las llamadas uso el zxcvb
    public static final String TAG = MainActivity.class.getName() + "zxcvb";
    private static final int PERMISO_CONTACTO = 1;
    public static TextView texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        obtenerPermisoPhone();
        obtenerPermisoContactos();
        obtenerPermisoLog();
        // si no aceptas alguno de los permisos , se cerrará la APP, al volver abrirlo saltará la
        // ventana del permiso rechazado adjuntando la razón, si vuelves a rechazar se volverá a cerrar
        // ya que no va a funcionar y volverá a mostrar la razón hasta aceptarlo
        // así con todos los permisos
    }

    private void obtenerPermisoContactos() {
        int result = PackageManager.PERMISSION_GRANTED;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            result = checkSelfPermission(Manifest.permission.READ_CONTACTS);
        }
        if(result == PackageManager.PERMISSION_GRANTED) {

        } else {
            pedirPermisoContactos();
        }
    }

    private void obtenerPermisoPhone() {
        int result = PackageManager.PERMISSION_GRANTED;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            result = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
        }
        if(result == PackageManager.PERMISSION_GRANTED) {
        } else {
            pedirPermisoPhone();
        }
    }

    private void pedirPermisoPhone() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISO_CONTACTO);
            }
        }
    }

    private void pedirPermisoContactos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                explicarRazon();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISO_CONTACTO);
            }
        }
    }

    private void explicarRazon() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.titulo_permiso);
            builder.setMessage(R.string.mensaje_permiso);
            builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISO_CONTACTO);
                }
            });
            builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.show();
    }

    private void obtenerPermisoLog() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG) !=
                PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CALL_LOG)){
                explicarRazon2();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CALL_LOG},1);
            }
        } else {
            texto = findViewById(R.id.texto);
        }
    }

    private void explicarRazon2() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permiso historial de llamadas");
        builder.setMessage("Necesitamos el permiso para leer el historial de llamadas");
        builder.setPositiveButton("aceptar", new DialogInterface.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, PERMISO_CONTACTO);
            }
        });
        builder.setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        Log.v(TAG,"Permiso garantizado");
                        texto = findViewById(R.id.texto);
                    }
                } else {
                    Log.v(TAG,"Permiso no garantizado");
                    finish();
                } return;
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.mnHistorial:
                return verHistorial();
            case R.id.mnLlamadas:
                return verLlamadas();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean verLlamadas() {
        obtenerLlamadas();
        leerLlamada();
        return true;
    }

    private boolean verHistorial() {
        obtenerHistorial();
        leerHistorial();
        return true;
    }

    private List<calls> obtenerLlamadas() {
        List<calls> listacalls = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        Cursor c = getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");
        int cachedNameIndex = c.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int numero = c.getColumnIndex(CallLog.Calls.NUMBER);
        int fecha = c.getColumnIndex(CallLog.Calls.DATE);
            while (c.moveToNext()) {
                String cachedName = c.getString(cachedNameIndex);
                String num = c.getString(numero);
                String fech = c.getString(fecha);
                Date tiempo = new Date(Long.valueOf(fech));
                SimpleDateFormat format = new SimpleDateFormat("yyyy");
                SimpleDateFormat format2 = new SimpleDateFormat("MM");
                SimpleDateFormat format3 = new SimpleDateFormat("dd");
                SimpleDateFormat format4 = new SimpleDateFormat("HH");
                SimpleDateFormat format5 = new SimpleDateFormat("mm");
                SimpleDateFormat format6 = new SimpleDateFormat("ss");
                String year = format.format(tiempo);
                int y = Integer.parseInt(year);
                String mes = format2.format(tiempo);
                int m = Integer.parseInt(mes);
                String dia = format3.format(tiempo);
                int d = Integer.parseInt(dia);
                String hora = format4.format(tiempo);
                int h = Integer.parseInt(hora);
                String minuto = format5.format(tiempo);
                int min = Integer.parseInt(minuto);
                String segundo = format6.format(tiempo);
                int s = Integer.parseInt(segundo);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (TextUtils.isEmpty(cachedName)) {
                            String updatedName = queryPhone(num);
                            if(updatedName.isEmpty()){
                                updatedName = "Desconocido";
                                calls llam = new calls(updatedName,y,m,d,h,min,s,num);
                                guardarLlamada(llam);
                                String cad = updatedName + "; " + year + "; " + mes + "; " + dia + "; " + hora + "; " + minuto + "; " + segundo + "; " + num + "\n";
                                listacalls.add(calls.fromCsvString(cad.toString(),";"));
                            } else {
                                calls llam = new calls(updatedName,y,m,d,h,min,s,num);
                                guardarLlamada(llam);
                                String cad = updatedName + "; " + year + "; " + mes + "; " + dia + "; " + hora + "; " + minuto + "; " + segundo + "; " + num + "\n";
                                listacalls.add(calls.fromCsvString(cad.toString(),";"));
                            }
                        }
                    }
                });
            }
            c.close();
            return  listacalls;
    }

    // para el nombre del contacto
    private String queryPhone(String number) {
        String name = "";
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        return name;
    }

    //getExternalFilesDir(null); sdcard > android > data > paquete > files > x.csv
    private boolean guardarLlamada(calls c) {
        List<calls> call= obtenerLlamadas();
        boolean result = true;
        File f = new File(getExternalFilesDir(null),"llamadas.csv");
        FileWriter fw = null;
        try {
            fw = new FileWriter(f);
            for(calls llam: call) {
                fw.write(llam.toCsvC() + "\n");
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    private void leerLlamada() {
        File f = new File(getExternalFilesDir(null), "llamadas.csv");
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linea;
            StringBuilder texto2 = new StringBuilder();
            while ((linea = br.readLine()) != null) {
                calls.fromCsvString(linea,";");
                texto2.append(linea);
                texto2.append('\n');
            }
            texto.setText(texto2);
            br.close();
        } catch(IOException e) {}

    }

    private List<calls> obtenerHistorial() {
        List<calls> listacalls2 = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        Cursor c = getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");
        int cachedNameIndex = c.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int numero = c.getColumnIndex(CallLog.Calls.NUMBER);
        int fecha = c.getColumnIndex(CallLog.Calls.DATE);
        while (c.moveToNext()) {
            String cachedName = c.getString(cachedNameIndex);
            String num = c.getString(numero);
            String fech = c.getString(fecha);
            Date tiempo = new Date(Long.valueOf(fech));
            SimpleDateFormat format = new SimpleDateFormat("yyyy");
            SimpleDateFormat format2 = new SimpleDateFormat("MM");
            SimpleDateFormat format3 = new SimpleDateFormat("dd");
            SimpleDateFormat format4 = new SimpleDateFormat("HH");
            SimpleDateFormat format5 = new SimpleDateFormat("mm");
            SimpleDateFormat format6 = new SimpleDateFormat("ss");
            String year = format.format(tiempo);
            int y = Integer.parseInt(year);
            String mes = format2.format(tiempo);
            int m = Integer.parseInt(mes);
            String dia = format3.format(tiempo);
            int d = Integer.parseInt(dia);
            String hora = format4.format(tiempo);
            int h = Integer.parseInt(hora);
            String minuto = format5.format(tiempo);
            int min = Integer.parseInt(minuto);
            String segundo = format6.format(tiempo);
            int s = Integer.parseInt(segundo);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (TextUtils.isEmpty(cachedName)) {
                        String updatedName = queryPhone(num);
                        if(updatedName.isEmpty()){
                            updatedName = "Desconocido";
                            calls llam2 = new calls(y,m,d,h,min,s,num,updatedName);
                            guardarHistorial(llam2);
                            String cad = year + "; " + mes + "; " + dia + "; " + hora + "; " + minuto + "; " + segundo + "; " + numero +  updatedName + "\n";
                            listacalls2.add(calls.fromCsvString2(cad.toString(),";"));
                        } else {
                            calls llam2 = new calls(y,m,d,h,min,s,num,updatedName);
                            guardarHistorial(llam2);
                            String cad = year + "; " + mes + "; " + dia + "; " + hora + "; " + minuto + "; " + segundo + "; " + numero +  updatedName + "\n";
                            listacalls2.add(calls.fromCsvString2(cad.toString(),";"));
                        }
                    }
                }
            });
        }
        c.close();
        return listacalls2;
    }

    //getFilesDir(); data > data > paquete > files > x.csv
    private boolean guardarHistorial(calls c) {
        List<calls> call = obtenerHistorial();
        boolean result = true;
        File f = new File(getFilesDir(),"historial.csv");
        FileWriter fw = null;
        try {
            fw = new FileWriter(f);
            for(calls llam: call) {
                fw.write(llam.toCsvH() + "\n");
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    private void leerHistorial() {
        File f = new File(getFilesDir(), "historial.csv");
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String linea;
            StringBuilder texto2 = new StringBuilder();
            while ((linea = br.readLine()) != null) {
                calls.fromCsvString2(linea,";");
                texto2.append(linea);
                texto2.append('\n');
            }
            texto.setText(texto2);
            br.close();
        } catch(IOException e) {}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menú se integra en el actionBar
        // ciclo de vida que se ejecutará cuando quiera abrir el menú (pulse los 3 puntos)
        // inflator especial , los menús se inflan de forma diferente
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menumainactivity, menu);
        return true;
    }
}