package com.example.vozoperadora;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Calculadoraparalosqueven extends AppCompatActivity {

    // Variables para almacenar la entrada actual, la entrada previa y el operador
    private TextView pantalla;
    private String entradaActual = "";
    private String entradaPrevia = "";
    private String operador = "";
    private ImageButton btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculadoraparalosqueven);

        // Inicializar la pantalla (TextView) de la calculadora y el botón de volver
        pantalla = findViewById(R.id.tvDisplay);
        btnVolver = findViewById(R.id.btnVolver);

        // Asignar listeners a los botones de números
        int[] botonesNumeros = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        for (int id : botonesNumeros) {
            findViewById(id).setOnClickListener(this::onClickNumero);
        }

        // Asignar listeners a los botones de operadores
        findViewById(R.id.btnAdd).setOnClickListener(v -> onClickOperador("+"));
        findViewById(R.id.btnSubtract).setOnClickListener(v -> onClickOperador("-"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> onClickOperador("×"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> onClickOperador("÷"));

        // Asignar listeners a botones especiales
        findViewById(R.id.btnEquals).setOnClickListener(v -> onClickIgual());
        findViewById(R.id.btnDot).setOnClickListener(v -> onClickPunto());
        findViewById(R.id.btnCE).setOnClickListener(v -> onClickBorrarEntrada());
        findViewById(R.id.btnC).setOnClickListener(v -> onClickBorrarTodo());
        findViewById(R.id.btnDel).setOnClickListener(v -> onClickBorrarUltimo());

        //Listener del boton volver
        btnVolver.setOnClickListener(v -> onClickVolver());
    }

    // Método llamado al presionar un botón numérico
    private void onClickNumero(View vista) {
        Button boton = (Button) vista;
        entradaActual += boton.getText().toString(); // Agregar número al texto actual
        actualizarPantalla(entradaActual); // Mostrarlo en pantalla
    }

    // Método llamado al seleccionar un operador
    private void onClickOperador(String operadorSeleccionado) {
        if (!entradaActual.isEmpty()) {
            entradaPrevia = entradaActual; // Guardar número actual
            entradaActual = ""; // Limpiar la entrada actual
            operador = operadorSeleccionado; // Guardar el operador
            actualizarPantalla(operador); // Mostrar el operador en pantalla
        }
    }

    // Método llamado al presionar el botón "="
    private void onClickIgual() {
        if (!entradaActual.isEmpty() && !entradaPrevia.isEmpty() && !operador.isEmpty()) {
            double resultado = 0;
            double numero1 = Double.parseDouble(entradaPrevia);
            double numero2 = Double.parseDouble(entradaActual);

            // Realizar la operación según el operador
            switch (operador) {
                case "+":
                    resultado = numero1 + numero2;
                    break;
                case "-":
                    resultado = numero1 - numero2;
                    break;
                case "×":
                    resultado = numero1 * numero2;
                    break;
                case "÷":
                    if (numero2 != 0) {
                        resultado = numero1 / numero2;
                    } else {
                        actualizarPantalla("Error"); // Mostrar error si se divide entre 0
                        return;
                    }
                    break;
            }

            // Actualizar la pantalla con el resultado
            entradaActual = String.valueOf(resultado);
            entradaPrevia = "";
            operador = "";
            actualizarPantalla(entradaActual);
        }
    }

    // Método llamado al presionar el botón "."
    private void onClickPunto() {
        if (!entradaActual.contains(".")) {
            if (entradaActual.isEmpty()) {
                entradaActual = "0."; // Si no hay entrada, comienza con "0."
            } else {
                entradaActual += ".";
            }
            actualizarPantalla(entradaActual);
        }
    }

    // Método llamado al presionar "CE" (Borrar entrada actual)
    private void onClickBorrarEntrada() {
        entradaActual = "";
        actualizarPantalla("0");
    }

    // Método llamado al presionar "C" (Reiniciar la calculadora)
    private void onClickBorrarTodo() {
        entradaActual = "";
        entradaPrevia = "";
        operador = "";
        actualizarPantalla("0");
    }

    // Método llamado al presionar "DEL" (Borrar último dígito)
    private void onClickBorrarUltimo() {
        if (!entradaActual.isEmpty()) {
            entradaActual = entradaActual.substring(0, entradaActual.length() - 1); // Eliminar último carácter
            actualizarPantalla(entradaActual.isEmpty() ? "0" : entradaActual); // Mostrar "0" si queda vacío
        }
    }
    
    private void onClickVolver(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    // Método para actualizar el texto de la pantalla
    private void actualizarPantalla(String texto) {
        pantalla.setText(texto);
    }
}
