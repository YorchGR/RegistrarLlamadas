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

/**
 * Clase Main de la app. Esta clase es la principal y es la encargada de comprobar los permisos,
 * atender a la gestión del botón de carga, comprobar las preferencias compartidas, etc.
 * Variables de la clase:
 *     SharedPreferences preferencias. Variable de la clase preferencias, encargada de guardar
 *     valores de preferencias compartidas
 *     static final int CODIGO_PERMISOS. Variable que contiene el request code de los permisos
 *     static final String[] PERMISOS_REQUERIDOS. Array con los permisos que se van a solicitar
 *     TextView tv_lista. Text view que mostrará el archivo con la lista de llamadas que se solicite
 *     Button bt. Botón encargado de llamar al método de carga de la lista de llamadas.
 *     Archivos ar. Objeto de la clase Archivos. Esta clase se encarga de manejar los datos, tanto
 *     para su lectura como para su escritura.
 *     boolean eleccion. Booleano usado para pasar la elección contraria a la que esté en
 *     preferencias, dandole sentido al botón, ya que carga la opción contraria a la de las opciones
 *     compartidas.
 *
 * @author Jorge López Endrina.
 */

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences preferencias;
    private static final int CODIGO_PERMISOS = 888;
    private static final String[] PERMISOS_REQUERIDOS = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private TextView tv_lista;
    private Button bt;
    private Archivos ar = new Archivos();
    private boolean eleccion=true;

    /**
    *  Método que trata el listener del botón. Llama al método cargarArchivo.
    * */
    public void botonCargar() {
        bt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                cargaArchivo(eleccion);
            }
        });
    }

    /**
     *  Método encargado de recibir la lista de llamadas y mostrarla en pantalla
     * */
    private void cargaArchivo(boolean eleccion) {
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

    /**
     *  Método que trata carga los ajustes compartidos y le da el valor contrario a la variable
     * booleana eleccion, para darle así, sentido al botón.
     * */
    private void cargaEleccion() {
        String valor = "";
        valor = preferencias.getString("listPref", "listPref");

        if (valor.equalsIgnoreCase("Cargar archivo Llamadas.csv")) {
            cargaArchivo(true);
            eleccion=false;
        } else if (valor.equalsIgnoreCase("Cargar archivo Historial.csv")) {
            cargaArchivo(false);
            eleccion=true;
        }
    }

    /**
     *  Método encargado de comprobar si se tienen los permisos requeridos
     * */
    @SuppressLint("NewApi")
    public boolean chequearPermisos() {
        int perUno = checkSelfPermission(PERMISOS_REQUERIDOS[0]);
        int perDos = checkSelfPermission(PERMISOS_REQUERIDOS[1]);
        int perTres = checkSelfPermission(PERMISOS_REQUERIDOS[2]);
        return perUno == PackageManager.PERMISSION_GRANTED
                && perDos == PackageManager.PERMISSION_GRANTED
                && perTres == PackageManager.PERMISSION_GRANTED;
    }

    /**
     *  Método encargado de mostrar de una u otra manera, el mensaje de petición de permisos.
     * */
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

    /**
     *  Método encargado de guardar los valores de las preferencias compartidas.
     * */
    private void guardaLaPreferencia() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("listPref", String.valueOf(eleccion));
        editor.commit();
    }

    /**
     *  Este método comprueba si existe el archivo con el array de datos serializados. De no ser así
     * (normalmente ocurre al iniciarse por primera vez la aplicación), crea el archivo y le incluye
     * un array vacío de datos.
     * */
    private void inicializarArchivos() {
        ArrayList<Llamada> aux = new ArrayList();
        File f = new File(getApplicationContext().getFilesDir(), "listaLlamadasObj.dat");
        if (!f.exists()) {
            ar.guardarListadoLlamadasSerializado(aux, getApplicationContext());
        }
    }

    /**
     * Método encargado de inicializar diferentes variables de la clase. También llama al método
     * encargado de comprobar los permisos y, de tenerlos, llama a los métodos de carga, de creación
     * de listener, preferencias e inicialización de archivos.
     * */
    private void init() {
        tv_lista = findViewById(R.id.tv_lista);
        bt = findViewById(R.id.bt_carga);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (chequearPermisos()) {
                cargaArchivo(true);
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
        preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        preferencias.registerOnSharedPreferenceChangeListener(this);
        guardaLaPreferencia();
        cargaEleccion();
    }

    /**
     * Método de llamada al activity del menú
     * */
    private boolean lanzaActivitySettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
        return true;

    }

    /**
     * Método onCreate del ciclo de vida de la aplicación
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    /**
     * Método onCreate del menú de opciones
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Método de selección de opciones del menú
     * */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSettings:
                return lanzaActivitySettings();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Método de comprobación de los permisos requeridos. De no tener todos los permisos, reitera en
    * su petición, saliendo de la aplicación de no tenerlos todos
     * */
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

    /**
     * Método onSharedPreferencChange
     * */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String valor;
        valor = sharedPreferences.getString(key, "na");
    }

    /**
     * Método encargado de pedir los permisos
     * */
    @SuppressLint("NewApi")
    private void requestPermission() {
        requestPermissions(PERMISOS_REQUERIDOS, CODIGO_PERMISOS);
    }
}