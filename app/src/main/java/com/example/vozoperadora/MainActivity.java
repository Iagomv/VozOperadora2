    package com.example.vozoperadora;


    import android.Manifest;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.os.Build;
    import android.os.Bundle;
    import android.speech.RecognitionListener;
    import android.speech.RecognizerIntent;
    import android.speech.SpeechRecognizer;
    import android.speech.tts.TextToSpeech;
    import android.util.Log;
    import android.widget.ImageView;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;

    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.Locale;

    public class MainActivity extends AppCompatActivity {

        private SpeechRecognizer speechRecognizer;
        private TextToSpeech textToSpeech;  // Instancia de TextToSpeech
        private Intent recognizerIntent;
        private String recognizedText = "";  // Variable para almacenar el texto reconocido
        private static final int REQUEST_RECORD_AUDIO_PERMISSION = 101;
        private ImageView inicio;
        private TextView tvDisplay;
        private static int resultado;
        MapeoNumeros mapeoNumeros;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            // Configurar el SpeechRecognizer y el TextToSpeech
            configurarSpeechRecognizer();
            configurarTextToSpeech();  // Inicializamos TextToSpeech

            // Pedir permisos de grabación de audio
            requestRecordAudioPermission();

            // Referencia al ImageView para iniciar la grabación
            tvDisplay = findViewById(R.id.tvDisplay);
            inicio = findViewById(R.id.inicio);
            inicio.setOnClickListener(v -> startSpeechRecognition());  // Inicia el reconocimiento al hacer clic
            String textoPrueba = "Hola";
            speakText(textoPrueba);
        }

        // Método para pedir permiso de grabación de audio
        private void requestRecordAudioPermission() {
            // Verifica si el dispositivo está ejecutando una versión de Android >= Marshmallow (API 23)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Si el permiso ya ha sido concedido, no es necesario pedirlo
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    // Si el permiso no está concedido, pide permiso
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
                }
            }
        }

        // Manejar la respuesta de la solicitud de permisos
        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
                // Si el permiso fue concedido, iniciar el reconocimiento de voz
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                } else {
                    // Si el permiso fue denegado, mostrar un mensaje
                    Toast.makeText(this, "Permiso de grabación de audio denegado", Toast.LENGTH_SHORT).show();
                }
            }
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
                    System.out.println(error);
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
                        case SpeechRecognizer.ERROR_AUDIO:
                            errorMessage = "Error en el audio";
                            break;
                        case SpeechRecognizer.ERROR_SERVER:
                            errorMessage = "Error en el servidor";
                            break;
                        case SpeechRecognizer.ERROR_CLIENT:
                            errorMessage = "Error en el cliente";
                            break;
                        // Agrega más casos si es necesario
                    }
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResults(Bundle results) {
                    // Aquí obtenemos el texto del reconocimiento
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                    if (matches != null && !matches.isEmpty()) {
                        System.out.println(matches.get(0));
                        recognizedText = matches.get(0);  // Almacenamos el primer resultado
                        System.out.println(matches);
                        Toast.makeText(MainActivity.this, "Texto reconocido: " + recognizedText, Toast.LENGTH_LONG).show();
                        String[] testFrase = {"SIETE", "SUMA", "DOS"};
                        mapeoNumeros = new MapeoNumeros(testFrase);
                        Log.d("Resultado  SUMA UNO", mapeoNumeros.getResultado().toString());
                        mapeoNumeros.resetValores();
                        //speakText(recognizedText); //Método para dar feedback del resultado por voz
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
                        speakText("Instrucciones de uso de la aplicación, para comenzar a grabar presione la pantalla una vez");

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

        public void setResultado(int resultado) {
            this.resultado = resultado;
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

        private void mostrarResultado(int resultado){
            String texto = String.valueOf(resultado);
            tvDisplay.setText(texto);
        }
    }
