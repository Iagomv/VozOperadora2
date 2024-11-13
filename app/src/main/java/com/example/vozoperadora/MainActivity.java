package com.example.vozoperadora;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private String recognizedText = "";  // Variable para almacenar el texto reconocido

    private ImageView inicio;
    private TextToSpeech textToSpeech;  // Instancia de TextToSpeech

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configurar el SpeechRecognizer y el TextToSpeech
        configurarSpeechRecognizer();
        configurarTextToSpeech();  // Inicializamos TextToSpeech

        // Referencia al ImageView para iniciar la grabación
        inicio = findViewById(R.id.inicio);  // Asegúrate de tener este ImageView en tu XML
        inicio.setOnClickListener(v -> startSpeechRecognition());  // Inicia el reconocimiento al hacer clic
    }

    // Configurar el SpeechRecognizer
    public void configurarSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Toast.makeText(MainActivity.this, "Listo para escuchar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {
                Toast.makeText(MainActivity.this, "Iniciando grabación...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // Puedes usar esto para obtener el volumen si lo necesitas
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // Se recibe el audio, pero no es necesario procesarlo aquí
            }

            @Override
            public void onEndOfSpeech() {
                Toast.makeText(MainActivity.this, "Grabación terminada", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int error) {
                String errorMessage = "Error desconocido";
                switch (error) {
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        errorMessage = "Tiempo de espera de red agotado";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        errorMessage = "No se encontró coincidencia de voz";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        errorMessage = "Se agotó el tiempo de espera de la voz";
                        break;
                }
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                // Aquí obtenemos el texto del reconocimiento
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    recognizedText = matches.get(0);  // Almacenamos el primer resultado
                    Toast.makeText(MainActivity.this, "Texto reconocido: " + recognizedText, Toast.LENGTH_LONG).show();
                    // Reproducir el texto usando TextToSpeech
                    speakText(recognizedText);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // Si necesitas resultados parciales, puedes manejar esto aquí
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // Aquí puedes manejar otros eventos si es necesario
            }
        });

        // Configurar el Intent para empezar la grabación
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES"); // Configura el idioma de la voz
    }

    // Método para iniciar el reconocimiento de voz
    private void startSpeechRecognition() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer.startListening(recognizerIntent);
        } else {
            Toast.makeText(this, "Reconocimiento de voz no disponible", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para configurar TextToSpeech
    private void configurarTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                // Configurar idioma a español
                int langResult = textToSpeech.setLanguage(Locale.forLanguageTag("es-ES"));
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(MainActivity.this, "El idioma no es soportado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "TextToSpeech listo", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Error en la inicialización de TTS", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para reproducir el texto
    private void speakText(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar recursos
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (textToSpeech != null) {
            textToSpeech.shutdown(); // Apagar TTS para liberar recursos
        }
    }
}
