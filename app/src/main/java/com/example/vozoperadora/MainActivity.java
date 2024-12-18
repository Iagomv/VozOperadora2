    package com.example.vozoperadora;


    import android.Manifest;
    import android.animation.ObjectAnimator;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.os.Build;
    import android.os.Bundle;
    import android.speech.RecognitionListener;
    import android.speech.RecognizerIntent;
    import android.speech.SpeechRecognizer;
    import android.speech.tts.TextToSpeech;
    import android.util.Log;
    import android.view.View;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.Spinner;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;

    import java.util.ArrayList;
    import java.util.Locale;

    public class MainActivity extends AppCompatActivity {

        private SpeechRecognizer speechRecognizer;
        private TextToSpeech textToSpeech;  // Instancia de TextToSpeech
        private Intent recognizerIntent;
        private String recognizedText = "";  // Variable para almacenar el texto reconocido
        private static final int REQUEST_RECORD_AUDIO_PERMISSION = 101;
        private ImageView ivBienvenida;
        private ImageView inicio;
        private TextView tvDisplay;
        private ImageView ivEstadoGrabacion;
        private Spinner spinnerIdiomas;
        private String idioma = "es-ES";
        private boolean bienvenida = false;
        private static int resultado;
        String[] idiomasDisponibles = {"es-ES", "en-US", "fr-FR", "ru-RU"};
        private EditText etAudioReconocido;

        MapeoNumeros mapeoNumeros;
        Interfaz interfaz = new Interfaz();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            iniciarSpinner();
            // Configurar el SpeechRecognizer y el TextToSpeech
            configurarSpeechRecognizer();
            configurarTextToSpeech(idioma);  // Inicializamos TextToSpeech
            // Pedir permisos de grabación de audio
            requestRecordAudioPermission();
            Referencias();
            bienvenida();
            spinnerIdiomas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    idioma = idiomasDisponibles[position];
                    configurarTextToSpeech(idioma);  // Actualiza TTS
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Manejo si no se selecciona nada
                }
            });

            inicio.setOnClickListener(v -> startSpeechRecognition());  // Inicia el reconocimiento al hacer clic

        }

        // Referencia a los elementos de la interfaz
        private void Referencias() {
            tvDisplay = findViewById(R.id.tvResultado);
            inicio = findViewById(R.id.inicio);
            etAudioReconocido = findViewById(R.id.etAudioReconocido);
            ivEstadoGrabacion = findViewById(R.id.ivEstadoGrabacion);
            ivBienvenida = findViewById(R.id.ivBienvenida);
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
                    ivEstadoGrabacion.setImageResource(R.drawable.grabando);
                }

                @Override
                public void onBeginningOfSpeech() {
                    //Toast.makeText(MainActivity.this, "Iniciando grabación...", Toast.LENGTH_SHORT).show();
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
                    ivEstadoGrabacion.setImageResource(R.drawable.startrecording);
                }

                @Override
                public void onError(int error) {
                    System.out.println(error);
                    String errorMessage = "Error desconocido";
                    switch (error) {
                        case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                            errorMessage = "Tiempo de espera de red agotado";
                            speakText(errorMessage);
                            break;
                        case SpeechRecognizer.ERROR_NO_MATCH:
                            errorMessage = "No se encontró coincidencia de voz";
                            speakText(errorMessage);
                            break;
                        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                            errorMessage = "Se agotó el tiempo de espera de la voz";
                            speakText(errorMessage);
                            break;
                        case SpeechRecognizer.ERROR_AUDIO:
                            errorMessage = "Error en el audio";
                            speakText(errorMessage);
                            break;
                        case SpeechRecognizer.ERROR_SERVER:
                            errorMessage = "Error en el servidor";
                            speakText(errorMessage);
                            break;
                        case SpeechRecognizer.ERROR_CLIENT:
                            errorMessage = "Error en el cliente";
                            speakText(errorMessage);
                            break;
                        // Agrega más casos si es necesario
                    }
                    Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResults(Bundle results) {
                    // Aquí obtenemos el texto del reconocimiento
                    ArrayList<String> textoObtenido = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                    if (textoObtenido != null && !textoObtenido.isEmpty()) {
                        recognizedText = textoObtenido.get(0);  // Almacenamos el primer resultado

                        Cliente.enviarAlServidor(recognizedText);


                        etAudioReconocido.setText(getString(R.string.texto_reconocido) + recognizedText);
                        String conversacionText = conversacion(recognizedText);
                        Log.d("conversacionText", conversacionText);
                        if(conversacionText!=null && !conversacionText.equals("0")){
                            speakText(conversacionText);
                        }else {
                            System.out.println("-------------------");
                            mapeoNumeros = new MapeoNumeros(recognizedText, MainActivity.this);
                            if (!erroresPersonalizados(mapeoNumeros.getResultado())){
                                interfaz.mostrarResultado(mapeoNumeros.getResultado(),tvDisplay);
                                speakText((String.valueOf(mapeoNumeros.getResultado()))); //Método para dar feedback del resultado por voz
                            }
                            reproducirErrores(mapeoNumeros.getResultado());
                            mapeoNumeros.resetValores();
                        }

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
        private void configurarTextToSpeech(String idioma) {
            textToSpeech = new TextToSpeech(this, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    // Configurar idioma a español
                    int langResult = textToSpeech.setLanguage(Locale.forLanguageTag(idioma));
                    if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, "El idioma no es soportado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "TextToSpeech listo", Toast.LENGTH_SHORT).show();
                        if (!bienvenida){
                            speakText("Hola, soy vozoperadora, para hablar conmigo, presione la pantalla una vez, puedes pedirme que " +
                                    "realice operaciones sencillas con un solo click en la pantalla, encantada de conocerte");
                            bienvenida = true;
                        }

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

        // Deteccion de errores
        private boolean erroresPersonalizados(double x){
            if(x==-6969 || x==-2.69){
                return true;
            }
            return false;
        }

        //Error handler
        private void reproducirErrores(double x){
            if(x==-6969){
                speakText("¿Dividiendo entre cero, listillo?");
                interfaz.mostrarError("Listill@", tvDisplay);
            }
            if(x==-2.0069){
                speakText("Perdona, creo que no te he entendido bien");
                interfaz.mostrarError("Error", tvDisplay);
            }
        }


        //Solucion para hablar con la calculadora
        public String conversacion(String text) {
            if (text == null || text.isEmpty()) {
                return "No entendí lo que dijiste, por favor intenta de nuevo.";
            }

            // Convertir texto a minúsculas y limpiar espacios
            String comando = text.toLowerCase();

            switch (comando) {
                case "¿cómo estás?":
                case "cómo estás":
                case "como estas":
                    return "Todo bien, gracias por preguntar. ¿Y tú?";

                case "hola":
                    return "¡Hola! ¿En qué puedo ayudarte?";

                case "adiós":
                case "adios":
                    return "Adiós, que tengas un buen día.";

                case "qué haces":
                case "cuál es tu función":
                case "cuál es tu función principal":
                case "qué eres":
                    return "Hola, soy vozoperadora, mi función principal es proporcionar apoyo para calcular a personas con visibilidad reducida";

                case "cómo te llamas":
                case "quién eres":
                    return "¡Mi nombre es vozoperadora!, encantada de conocerte";

                case "cómo abro la calculadora normal":
                case "cómo veo la calculadora":
                case "quiero ver la calculadora":
                case "por qué no se abre la calculadora":
                    return "Si quieres abrir la calculadora gráfica, prueba a decir el comando secreto, yaveo, o, abresupercalculadora";

                case "creador":
                case "creador de vozoperadora":
                case "quien es el creador de vozoperadora":
                case "cómo se llama el creador de vozoperadora":
                case "quién te ha creado":
                case "quién es tu creador":

                    return "Mi dios y creador es Iago, si te caigo bien," +
                            "puedes apoyarlo en las plataformas de Patreon y kickstarter,  para que pueda seguir trabajando en mi desarrollo, !Gracias¡";

                default:
                    // Respuesta para comandos no reconocidos
                    return "0";
            }
        }

        //Pantalla de inicio
        public void bienvenida(){
            // Crear animación de alpha (0: completamente invisible, 1: completamente visible)
            ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(ivBienvenida, "alpha", 1f, 0f);
            alphaAnimator.setDuration(4000); // 2 segundos
            // Iniciar la animación
            alphaAnimator.start();
        }


        // Iniciación del spinner
        public void iniciarSpinner(){

            // 1. Obtén el Spinner desde el layout
            spinnerIdiomas = findViewById(R.id.spinnerIdiomas);

            // 2. Crea un adaptador con el contexto, el diseño y los datos
            ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    idiomasDisponibles
            );

            // 3. Configura el diseño para las opciones desplegables (opcional)
            adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // 4. Asocia el adaptador al Spinner
            spinnerIdiomas.setAdapter(adaptador);
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
