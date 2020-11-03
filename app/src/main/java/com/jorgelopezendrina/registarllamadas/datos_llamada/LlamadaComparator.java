package com.jorgelopezendrina.registarllamadas.datos_llamada;

import android.util.Log;

import java.util.Comparator;

public class LlamadaComparator implements Comparator<Llamada> {
    @Override
    public int compare(Llamada ll1, Llamada ll2) {
        int sort = ll1.getFecha().compareTo(ll2.getFecha());
        if(sort == 0) {
            sort = ll1.getNumero().compareTo(ll2.getNumero());
            if(sort == 0) {
                sort = ll1.getNombre().compareTo(ll2.getNombre());;
            }
        }
        return sort;
    }
}
