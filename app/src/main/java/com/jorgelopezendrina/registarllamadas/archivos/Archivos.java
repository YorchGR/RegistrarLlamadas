package com.jorgelopezendrina.registarllamadas.archivos;

import android.content.Context;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.jorgelopezendrina.registarllamadas.datos_llamada.Llamada;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Archivos extends AppCompatActivity implements Serializable {

    //Historial.csv SE GUARDA DESORDENADO
    public boolean guardaLlamadasDesordenado(Llamada llamada, Context cont) {
        boolean result = true;
        File f = new File(cont.getFilesDir(), "Historial.csv");
        FileWriter fw = null;
        try {
            fw = new FileWriter(f, true);
            fw.write(llamada.toString() + "\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    public void eliminaArchivo(Context cont){
        File f = new File(cont.getExternalFilesDir(null), "Llamadas.csv");
        if (f.exists()){
            f.delete();
        }
    }

    //Llamadas.csv SE GUARDA ORDENADO
    public boolean guardaLlamadasOrdenado(ArrayList<Llamada> listaLlamadas , Context cont) {
        boolean result = true;
        File f = new File(cont.getExternalFilesDir(null), "Llamadas.csv");

        Log.v("HOLA", "GUARDA ORDENADO");
        for (int i = 0; i < listaLlamadas.size(); i++) {
            Log.v("HOLA", listaLlamadas.get(i).toString());
        }

        try {
            FileWriter fw = new FileWriter(f,true);
            for (int i = 0; i < listaLlamadas.size(); i++) {
                fw.write(listaLlamadas.get(i).toString() + "\n");
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    public ArrayList<String> leerArchivo(boolean orden, Context cont) {
        ArrayList<String> listaLlamadas = new ArrayList();
        File f;
        String linea;

        try {
            if (orden) {
                f = new File(cont.getExternalFilesDir(null), "Llamadas.csv");
            } else {
                f = new File(cont.getFilesDir(), "Historial.csv");
            }

            BufferedReader br = new BufferedReader(new FileReader(f));
            if (orden) {
                listaLlamadas.add("LLAMADAS.CSV\n");
            } else {
                listaLlamadas.add("HISTORIAL.CSV\n");
            }
            while ((linea = br.readLine()) != null) {
                listaLlamadas.add(linea);
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return listaLlamadas;
    }


    public void guardarListadoLlamadasSerializado(ArrayList<Llamada> aux, Context cont) {
        File  f = new File(cont.getFilesDir(), "listaLlamadasObj.obj");
        try {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(aux);
            oos.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public ArrayList<Llamada> leerListadoLlamadasSerializado(Context cont) {
        ArrayList listaLlamadas = new ArrayList();
        try {
            File  f = new File(cont.getFilesDir(), "listaLlamadasObj.obj");
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

            listaLlamadas = (ArrayList) ois.readObject();
            ois.close();
            fis.close();

        } catch (IOException | ClassNotFoundException ioe) {
            ioe.printStackTrace();
        }
        Log.v("HOLA", "LEE SERIALIZABLE");
        for (int i = 0; i < listaLlamadas.size(); i++) {
            Log.v("HOLA", listaLlamadas.get(i).toString());
        }
        return listaLlamadas;

    }
}

