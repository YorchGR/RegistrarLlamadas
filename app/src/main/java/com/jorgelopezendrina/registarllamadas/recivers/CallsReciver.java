package com.jorgelopezendrina.registarllamadas.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.jorgelopezendrina.registarllamadas.MainActivity;
import com.jorgelopezendrina.registarllamadas.archivos.Archivos;
import com.jorgelopezendrina.registarllamadas.datos_llamada.Llamada;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.TimeZone;

/**
 * Clase que extiende de BroadcastReciver y que se encarga de escuchar las llamadas entrantes, y
 * mediante el uso de diferentes métodos, recoge la información necesaria para que se pueda crear un
 * objeto de la clase llamada. Contiene un hilo, que se encarga de recabar la información y de
 * mandarla para que se guarde en diferentes ficheros.
 *
 * @author Jorge López Endrina.
 */

public class CallsReciver extends BroadcastReceiver {

    /**
    * Método encargado de conseguir la fecha del sistema.
    * */
    private String consigueFecha() {
        String fecha;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy; MM; dd; HH; mm; ss; ");
        formato.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        fecha = formato.format(cal.getTime());
        return fecha;
    }

    /**
    * Método encargado de buscar el número de teléfono en los contactos y devolver el
    * nombre del contacto al que pertenece el número de teléfono
    * */
    private String consigueNombre(String numTlf,Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(numTlf));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
        String nombreContacto = "Desconocido";
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                nombreContacto = cursor.getString(0);
            }
            cursor.close();
        }
        return nombreContacto;
    }

    /**
     * Método encargado de extraer de una llamada entrante, el número de teléfono.
     * */
    private String consigueNum(Intent intent) {
        String numTlf = null;
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String estadoTlf = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (estadoTlf.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                numTlf = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            }
        }
        return numTlf;
    }

    /**
     * Método onRecibe de la clase BroadcastReciver
     * */
    @Override
    public void onReceive(Context context, Intent intent) {
        Hilo h1 = new Hilo(context, intent);
        h1.start();
    }

    /**
     * Clase privada que extiende de Thread, encargada de conseguir los distintos datos que conforman
     * un objeto de la clase llamada y de llamnar a distintos métodos que ordenan y guardan el
     * objeto
     * */
    private class Hilo extends Thread {
        private Context cont;
        private Intent intent;
        String nombre, numero, fecha;
        Archivos ar = new Archivos();
        public Hilo(Context context, Intent intent) {
            cont = context;
            this.intent = intent;
        }

        /**
         * Método run de la clase Thread
         * */
        @Override
        public void run() {
            numero = consigueNum(intent);
            nombre = consigueNombre(numero,cont);
            fecha = consigueFecha();
            if (numero != null && nombre != null) {
                Llamada llamadaSuelta = new Llamada(numero, nombre, fecha);
                ArrayList<Llamada> listaLlamadas = ar.leerListadoLlamadasSerializado(cont);
                ar.eliminaArchivo(cont);
                listaLlamadas.add(llamadaSuelta);
                ar.guardarListadoLlamadasSerializado(listaLlamadas,cont);
                ar.guardaLlamadasOrdenado(listaLlamadas, cont);
                ar.guardaLlamadasDesordenado(llamadaSuelta, cont);

            }
        }
    }
}

