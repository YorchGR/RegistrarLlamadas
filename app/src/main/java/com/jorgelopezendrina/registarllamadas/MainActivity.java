package com.jorgelopezendrina.registarllamadas;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.jorgelopezendrina.registarllamadas.ajustes.SettingsActivity;
import com.jorgelopezendrina.registarllamadas.archivos.Archivos;
import com.jorgelopezendrina.registarllamadas.datos_llamada.Llamada;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private static final int CODIGO_PERMISOS = 888;
    private static final String[] PERMISOS_REQUERIDOS = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private TextView tv_lista;
    private Button bt;
    private Archivos ar = new Archivos();
    private String valor = "";
    private boolean eleccion=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        tv_lista = findViewById(R.id.tv_lista);
        bt = findViewById(R.id.bt_carga);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (chequearPermisos()) {
                cargarArchivo(true);
                botonCargar();
                inicializarArchivos();
            } else {
                if (shouldShowRequestPermissionRationale(String.valueOf(PERMISOS_REQUERIDOS))) {
                    explicacionDetallada();
                } else {
                    requestPermission();
                }
            }
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        saveSharedPreferences();
        setOnTextView();
    }

    private void setOnTextView() {
        valor = sharedPreferences.getString("listPref", null);

        if (valor.equalsIgnoreCase("Cargar archivo Llamadas.csv")) {
            // choice 1
            cargarArchivo(true);
            eleccion=false;
        } else if (valor.equalsIgnoreCase("Cargar archivo Historial.csv")) {
            // choice 2
            cargarArchivo(false);
            eleccion=true;
        }
    }


    private void inicializarArchivos() {
        ArrayList<Llamada> aux = new ArrayList();
        File f = new File(getApplicationContext().getFilesDir(), "listaLlamadasObj.dat");
        if (!f.exists()) {
            ar.guardarListadoLlamadasSerializado(aux, getApplicationContext());
        }
    }

    public void botonCargar() {
        bt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                ArrayList<String> listado = ar.leerArchivo(eleccion, getApplicationContext());
                StringBuilder aux = new StringBuilder();
                for (int i = 0; i < listado.size(); i++) {
                    aux.append(listado.get(i)).append("\n");
                }
                tv_lista.setText(aux.toString());
            }
        });
    }

    private void cargarArchivo(boolean eleccion) {
        ArrayList<String> listado = ar.leerArchivo(eleccion, getApplicationContext());
        if (listado.isEmpty()) {
            tv_lista.setText("No hay llamadas registradas.");
        } else {
            StringBuilder aux = new StringBuilder();
            for (int i = 0; i < listado.size(); i++) {
                aux.append(listado.get(i)).append("\n");
            }
            tv_lista.setText(aux.toString());
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        valor = sharedPreferences.getString(key, "na");
    }

    private void saveSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("listPref", valor);

        editor.commit();
    }

    /*-------------------------------------------------------------------Menu de Ajustes*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSettings:
                return viewSettingsActivity();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private boolean viewSettingsActivity() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        return true;

    }

    /*--------------------------------------------------------------------------Permisos*/

    @SuppressLint("NewApi")
    public boolean chequearPermisos() {
        int perUno = checkSelfPermission(PERMISOS_REQUERIDOS[0]);
        int perDos = checkSelfPermission(PERMISOS_REQUERIDOS[1]);
        int perTres = checkSelfPermission(PERMISOS_REQUERIDOS[2]);
        return perUno == PackageManager.PERMISSION_GRANTED
                && perDos == PackageManager.PERMISSION_GRANTED
                && perTres == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("NewApi")
    private void requestPermission() {
        requestPermissions(PERMISOS_REQUERIDOS, CODIGO_PERMISOS);
    }


    private void explicacionDetallada() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle(R.string.titulo_permiso);
        builder.setMessage(R.string.mensaje_permiso_requerido);
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermission();
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CODIGO_PERMISOS:
                if (grantResults.length > 0) {
                    boolean perUno = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean perDos = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean perTres = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (perUno && perDos && perTres) {
                        Toast to = (Toast.makeText(getApplicationContext(), "Dispones de todos los permisos necesatios", Toast.LENGTH_SHORT));
                        to.show();
                    } else {
                        explicacionDetallada();
                    }
                }
                break;
        }
    }
}