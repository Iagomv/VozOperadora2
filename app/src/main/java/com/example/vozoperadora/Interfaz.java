package com.example.vozoperadora;

import android.widget.TextView;

public class Interfaz {


    public void mostrarResultado(int resultado, TextView tv){
        String texto = String.valueOf(resultado);
        tv.setText(texto);
    }

}