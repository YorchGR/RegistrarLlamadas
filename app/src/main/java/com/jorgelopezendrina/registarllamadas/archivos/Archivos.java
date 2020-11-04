package com.jorgelopezendrina.registarllamadas.archivos;

import android.content.Context;

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
import java.util.Collections;

/**
 * Clase que se ocupa de la gestión de los datos para su guardado o lectura.
 * @author Jorge López Endrina.
 */
public class Archivos extends AppCompatActivity implements Serializable {

    /**
     * Método encargado de eliminar el archivo, Llamadas.csv
     */
    public void eliminaArchivo(Context cont) {
        File f = new File(cont.getExternalFilesDir(null), "Llamadas.csv");
        if (f.exists()) {
            f.delete();
        }
    }

    /**
     *Método encargado de guardar el array de llamadas en el archivo, listaLlamadasObj.obj
     */
    public void guardarListadoLlamadasSerializado(ArrayList<Llamada> listaLlamadas, Context cont) {
        File f = new File(cont.getFilesDir(), "listaLlamadasObj.obj");
        try {
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            Collections.sort(listaLlamadas);
            oos.writeObject(listaLlamadas);
            oos.close();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Método encargado de guardar la última llamada entrante en el archivo desordenado, Historial.csv
     */
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

    /**
     * Método encargado de guardar la última llamada entrante en el archivo ordenado, Llamadas.csv
     */
    public boolean guardaLlamadasOrdenado(ArrayList<Llamada> listaLlamadas, Context cont) {
        boolean result = true;
        File f = new File(cont.getExternalFilesDir(null), "Llamadas.csv");
        try {
            FileWriter fw = new FileWriter(f, true);
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

    /**
     * Método encargado de leer uno de los dos archivos csv que almacena la aplicación
     */
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

    /**
     * Método encargado de leer el archivo que contiene el array de llamadas, listadoLlamadasObj.obj
     */
    public ArrayList<Llamada> leerListadoLlamadasSerializado(Context cont) {
        ArrayList listaLlamadas = new ArrayList();
        try {
            File f = new File(cont.getFilesDir(), "listaLlamadasObj.obj");
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            listaLlamadas = (ArrayList) ois.readObject();
            ois.close();
            fis.close();

        } catch (IOException | ClassNotFoundException ioe) {
            ioe.printStackTrace();
        }
        return listaLlamadas;
    }
}

