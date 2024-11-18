package com.example.vozoperadora;

import java.util.HashMap;

public class MapeoNumeros {
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
    int numero1 = 0;
    int numero2 = 0;
    String operador = null;
    boolean operadorEncontrado = false;
    int resultado = 0;
    String[] palabras;
    // Mapa de palabras a números para facilitar la conversión
    HashMap<String, Integer> mapaNumeros = new HashMap<>();
    HashMap<Integer, String> mapaInverso = new HashMap<>();

    // Constructor: inicializa el mapa de palabras a números
    public MapeoNumeros(String textoRecibido) {
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

            // Si aún no se ha encontrado el operador, busca si la palabra actual es un operador
            if (!operadorEncontrado && esOperador(palabra)) {
                operadorEncontrado = true;
                operador = palabra;
            }
            // Si es un número, añade el valor al número correspondiente
            else if (esNumero(palabra)) {
                int valor = obtenerNumero(palabra);
                if (!operadorEncontrado) {
                    numero1 += valor;
                } else {
                    numero2 += valor;
                }
            }
        }
    }

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
    public int calcularResultado() {
        int resultado;
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
                return 0;
        }
        return resultado;
    }

    // Restablece los valores para permitir reutilizar el objeto con nuevos cálculos
    public void resetValores() {
        numero1 = 0;
        numero2 = 0;
        operador = null;
        operadorEncontrado = false;
        resultado = 0;
    }
    public int getResultado() {
        return resultado;
    }

}
