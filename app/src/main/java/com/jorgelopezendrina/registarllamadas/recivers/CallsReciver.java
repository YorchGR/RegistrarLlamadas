package com.jorgelopezendrina.registarllamadas.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import com.jorgelopezendrina.registarllamadas.archivos.Archivos;
import com.jorgelopezendrina.registarllamadas.datos_llamada.Llamada;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.TimeZone;

public class CallsReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Hilo h1 = new Hilo(context, intent);
        h1.start();
    }

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

    private String consigueFecha() {
        String fecha;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy; MM; dd; HH; mm; ss; ");
        formato.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        fecha = formato.format(cal.getTime());
        return fecha;
    }

    /*-------------------------------------------------------------------------------------Hilos*/

    private class Hilo extends Thread {
        private Context cont;
        private Intent intent;
        String nombre, numero, fecha;
        Archivos ar = new Archivos();
        ArrayList<Llamada> listaLlamadas;
        public Hilo(Context context, Intent intent) {
            cont = context;
            this.intent = intent;
        }

        @Override
        public void run() {
            numero = consigueNum(intent);
            nombre = consigueNombre(numero,cont);
            fecha = consigueFecha();
            if (numero != null && nombre != null) {
                Llamada llamadaSuelta = new Llamada(numero, nombre, fecha);
                ArrayList<Llamada> listaLlamadas = ar.leerListadoLlamadasSerializado(cont);
                listaLlamadas.add(llamadaSuelta);
                Collections.sort(listaLlamadas);
                ar.guardaLlamadasDesordenado(llamadaSuelta, cont);
                ar.guardaLlamadasOrdenado(listaLlamadas, cont);
            }
        }
    }
}

