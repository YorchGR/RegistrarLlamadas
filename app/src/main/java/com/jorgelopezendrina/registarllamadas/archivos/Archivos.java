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
 *
 * @author Jorge López Endrina.
 */
public class Archivos extends AppCompatActivity implements Serializable {

    /**
     * Método encargado de convertir un array de cadenas, en uno de tipo Llamada;
     * */
    public ArrayList<Llamada> convierteCadenas(ArrayList<String> listaLlamadasString) {
        Llamada llamada;
        String cadena;
        ArrayList<Llamada> listaLlamadasObj = new ArrayList();
        for (int i = 0; i < listaLlamadasString.size(); i++) {
            cadena = listaLlamadasString.get(i);
            String[] partes = cadena.split(" ");
            if(partes.length ==8){
                String num = partes[6].trim()+" ";
                String nom = " "+partes[7].trim();
                String fecha = partes[0].trim() +" "+ partes[1].trim() +" "+ partes[2].trim() +" "+ partes[3].trim() +" "+ partes[4].trim() +" "+ partes[5].trim()+" ";
                llamada = new Llamada (num, nom, fecha);
                listaLlamadasObj.add(llamada);
            }
        }
        return listaLlamadasObj;
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
            FileWriter fw = new FileWriter(f);
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
            while ((linea = br.readLine()) != null) {
                listaLlamadas.add(linea);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listaLlamadas;
    }
}

