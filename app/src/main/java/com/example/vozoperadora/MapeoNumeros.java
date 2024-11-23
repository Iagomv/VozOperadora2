package com.example.vozoperadora;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;

public class MapeoNumeros {
    Context context;
    // Arreglos de palabras para los números y sus valores equivalentes en enteros
    String[] Palabras = { "UNO", "DOS", "TRES", "CUATRO", "CINCO", "SEIS",
            "SIETE", "OCHO", "NUEVE", "DIEZ", "ONCE", "DOCE", "TRECE",
            "CATORCE", "QUINCE", "DIECISEIS", "DIECISIETE", "DIECIOCHO",
            "DIECINUEVE", "VEINTE", "TREINTA", "CUARENTA", "CINCUENTA",
            "SESENTA", "SETENTA", "OCHENTA", "NOVENTA", "CIEN", "CIENTO",
            "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS", "QUINIENTOS",
            "SEISCIENTOS", "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS" };

    Integer[] Valores = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
            17, 18, 19, 20, 30, 40, 50, 60, 70, 80, 90, 100, 100, 200, 300, 400,
            500, 600, 700, 800, 900 };

    // Arreglo de operadores soportados
    String[] Operadores = {"SUMA", "RESTA", "MULTIPLICACIÓN", "DIVISIÓN"};

    // Variables para almacenar los números y el operador encontrado
    double numero1 = 0;
    double numero2 = 0;
    int decimal = 0;
    boolean con = false;
    String operador = null;
    boolean operadorEncontrado = false;
    double resultado = 0;
    String[] palabras;
    // Mapa de palabras a números para facilitar la conversión
    HashMap<String, Integer> mapaNumeros = new HashMap<>();
    HashMap<Integer, String> mapaInverso = new HashMap<>();

    // Constructor: inicializa el mapa de palabras a números
    public MapeoNumeros(String textoRecibido, Context context) {
        this.context = context;
        comprobacionDeSuperUsuario(textoRecibido.replace(" ", ""));
        palabras = getArrayTextoCorregido(corregirTexto(textoRecibido));
        llenarMapas();
        obteniendoDatos(palabras);
        resultado = calcularResultado();
    }


    // Llena el mapa con las palabras y sus valores correspondientes
    private void llenarMapas(){
        for (int i = 0; i < Palabras.length; i++) {
            mapaNumeros.put(Palabras[i], Valores[i]);
            mapaInverso.put(Valores[i], Palabras[i]);
        }
    }

    //Parsear String a String[]
    private String corregirTexto(String textoRecibido) {
        String[] palabras = textoRecibido.split(" "); // Dividimos por espacios
        for (int i = 0; i < palabras.length; i++) {
            palabras[i] = veintiNumero(palabras[i]); // Procesamos cada palabra
            System.out.println(palabras[i]);
        }
        return String.join(" ", palabras); // Reconstruimos la sentencia

    }

    private String[] getArrayTextoCorregido(String texto){
        return texto.split(" ");
    }


    // Comprueba si una palabra dada es un número
    public boolean esNumero(String textoaComparar){
        return mapaNumeros.containsKey(textoaComparar.toUpperCase());
    }

    // Obtiene el valor numérico correspondiente a una palabra
    public Integer obtenerNumero(String textoaComparar){
        return mapaNumeros.get(textoaComparar.toUpperCase());
    }

    // Recorre un arreglo de palabras y acumula los números en numero1 y numero2 según el operador encontrado
    public void obteniendoDatos(String[] palabras) {
        for (String palabra : palabras) {
            palabra = palabra.toUpperCase();
            verificarCon(palabra);

            // Si aún no se ha encontrado el operador, busca si la palabra actual es un operador
            if (!operadorEncontrado && esOperador(palabra)) {
                operador = palabra; // Se asigna el operador
                numero1 = setNumeroConDecimal(numero1,decimal); // reseteamos decimal
                operadorEncontrado = true; con = false; // Reseteamos booleanos
                Log.d("MapeoNumeros", "Operador encontrado: " + operador);
            }
            // Si es un número, añade el valor al número correspondiente
            else if (esNumero(palabra)) {
                int valor = obtenerNumero(palabra);
                if (con){
                    decimal += valor;
                }else {
                    if (!operadorEncontrado) {
                        numero1 += valor; // Se acumula el primer número
                    } else {
                        numero2 += valor; // Se acumula el segundo número
                    }
                }
            }
        }
        // Si no se encontró ningún operador, puedes asignar un valor por defecto
        if (operador == null) {
            Log.w("MapeoNumeros", "Operador no encontrado, se asignó 'SUMA' por defecto");
        }
        if (con==true){
            numero2 = setNumeroConDecimal(numero2,decimal);
        }
    }

    // Solución para los números que deletreamos como "VEINTIX" --> VEINTE Y X
    private String veintiNumero(String palabra) {
        switch (palabra.toLowerCase()) {
            case "veintiuno": return "VEINTE Y UNO";
            case "veintidos": return "VEINTE Y DOS";
            case "veintidós": return "VEINTE Y DOS";
            case "veintitres": return "VEINTE Y TRES";
            case "veinticuatro": return "VEINTE Y CUATRO";
            case "veinticinco": return "VEINTE Y CINCO";
            case "veintiseis": return "VEINTE Y SEIS";
            case "veintisiete": return "VEINTE Y SIETE";
            case "veintiocho": return "VEINTE Y OCHO";
            case "veintinueve": return "VEINTE Y NUEVE";
            default: return palabra.toUpperCase(); // Devolver la palabra en mayúsculas si no coincide
        }
    }

    //Solución para decimales
    private double setNumeroConDecimal(double x, int y) {
        System.out.println("decimal ="+decimal);
        // Calculamos el divisor para obtener la parte decimal
        int longitudY = String.valueOf(y).length();
        System.out.println("LongitudY = " + longitudY);
        StringBuilder divisorBuilder = new StringBuilder("1");
        for (int i = 0; i < longitudY; i++) {
            divisorBuilder.append("0");
        }
        int divisor = Integer.parseInt(divisorBuilder.toString());
        System.out.println("divisor = " + divisor);
        // Pasamos el número a decimal utilizando el divisor
        double decimalY = (double) y / divisor;
        System.out.println("decimal =" + decimalY);
        // Sumamos nuestro número más el decimal para obtener el resultado
        double result = x + decimalY;
        decimal = 0;
        return result;
    }

    // Verifica si una palabra es un operador y devuelve true si lo es
    private boolean esOperador(String palabra) {
        switch (palabra) {
            case "SUMA":
            case "MAS":
            case "MÁS":
            case "RESTA":
            case "MENOS":
            case "MULTIPLICACIÓN":
            case "MULTIPLICACION":
            case "MULTIPLICADO":
            case "POR":
            case "ENTRE":
            case "DIVIDIDO":
            case "DIVISIÓN":
                return true;
            default:
                return false;
        }
    }

    // Realiza el cálculo basado en el operador y devuelve el resultado como un string
    public double calcularResultado() {
        if (operador == null) {
            return -2.0069;
        }
        double resultado;
        switch (operador) {
            case "SUMA":
            case "MAS":
            case "MÁS":
                resultado = numero1 + numero2;
                break;
            case "RESTA":
            case "MENOS":
                resultado = numero1 - numero2;
                break;
            case "POR":
            case "MULTIPLICACION":
            case "MULTIPLICADO":
            case "MULTIPLICACIÓN":
                resultado = numero1 * numero2;
                break;
            case "DIVISION":
            case "DIVIDIDO":
            case "ENTRE":
                // Manejo de división por cero
                if (numero2 == 0) {
                    return -6969;
                }
                resultado = numero1 / numero2;
                break;
            default:
                return -2.0069;
        }
        return resultado;
    }

    //Solución para decimales
    private void verificarCon(String palabra){
        if (palabra.equals("CON") || palabra.equals("COMA") || palabra.equals("PUNTO")){
            con = true;
        }
    }

    // Restablece los valores para permitir reutilizar el objeto con nuevos cálculos
    public void resetValores() {
        numero1 = 0;
        numero2 = 0;
        operador = null;
        operadorEncontrado = false;
        resultado = 0;
    }

    //Solucion para abrir calculadora gráfica mediante el uso de la voz
    public boolean yaVeo(String text) {
        return text.contains("yaveo") || text.contains("abresúpercalculadora");
    }


    public void abrirCalculadora() {
            Intent intent = new Intent(context, Calculadoraparalosqueven.class);
            context.startActivity(intent);
    }

    public void comprobacionDeSuperUsuario(String texto){
        if (yaVeo(texto)){
            abrirCalculadora();
        }
    }
    public double getResultado() {
        return resultado;
    }

}
