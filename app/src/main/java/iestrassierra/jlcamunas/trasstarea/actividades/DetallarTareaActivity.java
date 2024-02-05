package iestrassierra.jlcamunas.trasstarea.actividades;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import iestrassierra.jlcamunas.trasstarea.R;
import iestrassierra.jlcamunas.trasstarea.adaptadores.TareaViewModel;
import iestrassierra.jlcamunas.trasstarea.modelo.entidades.Tarea;

public class DetallarTareaActivity extends AppCompatActivity {

    private TareaViewModel tareaViewModel;
    private MediaPlayer mp;

    private ImageButton play, pause, reset;

    private int posicion = 0;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detallar_tarea);

        tareaViewModel = new ViewModelProvider(this).get(TareaViewModel.class);
        //Recibimos la tarea que va a ser editada
        Tarea tareaDetalle = getIntent().getParcelableExtra("tareaDetallar");

// Obtener la URI del LiveData
        Uri audioUri = Uri.parse(tareaDetalle.getRutaAudio());
        //tareaViewModel.getAudio().getValue();

// Verificar si la URI es nula antes de llamar a toString()
        if (audioUri != null) {
            // Imprimir la URI como cadena
            Toast.makeText(this, audioUri.toString(), Toast.LENGTH_SHORT).show();

            // Crear el MediaPlayer con la URI
            mp = MediaPlayer.create(this, audioUri);
        } else {
            Toast.makeText(this, rutaANombre(tareaDetalle.getRutaAudio()), Toast.LENGTH_SHORT).show();
            // Manejar la situación en la que la URI es nula
            Toast.makeText(this, "La URI de audio es nula", Toast.LENGTH_SHORT).show();
        }


        // Oculta la barra de acción
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //Executor para ejecutar en hilos secundarios
        Executor executor = Executors.newSingleThreadExecutor();



        //Seteamos las informaciones en las distintas Vistas
        TextView tvDetallarTitulo = findViewById(R.id.tv_detallar_titulo_contenido);
        tvDetallarTitulo.setText(tareaDetalle.getTitulo());

        TextView tvDetallarDias = findViewById(R.id.tv_detallar_dias_contenido);
        int dias = Tarea.diasHastaHoy(tareaDetalle.getFechaObjetivo());
        tvDetallarDias.setText(String.valueOf(dias));

        ProgressBar pgDetallarProgreso = findViewById(R.id.pg_detallar_progreso);
        pgDetallarProgreso.setProgress(tareaDetalle.getProgreso());

        TextView tvDetallarDescripcion = findViewById(R.id.tv_detallar_descripcion_contenido);
        tvDetallarDescripcion.setText(tareaDetalle.getDescripcion());

        //Mostramos los nombres de los documentos a partir de su ruta completa
        TextView tv_detallar_documento = findViewById(R.id.tv_detallar_documento);
        tv_detallar_documento.setText(rutaANombre(tareaDetalle.getRutaDocumento()));

        TextView tv_detallar_imagen = findViewById(R.id.tv_detallar_imagen);
        tv_detallar_imagen.setText(rutaANombre(tareaDetalle.getRutaImagen()));

        TextView tv_detallar_audio = findViewById(R.id.tv_detallar_audio);
        tv_detallar_audio.setText(rutaANombre(tareaDetalle.getRutaAudio()));

        TextView tv_detallar_video = findViewById(R.id.tv_detallar_video);
        tv_detallar_video.setText(rutaANombre(tareaDetalle.getRutaVideo()));

        //Control del botón volver
        Button btDetallarVolver = findViewById(R.id.bt_detallar_cerrar);
        btDetallarVolver.setOnClickListener(this::onBotonVolverClicked);

        play =findViewById(R.id.ibReproducir);
        play.setOnClickListener(v -> {
            if (mp != null){
                //En un hilo secundario reproducimos el audio
                executor.execute(()->{
                    if(!mp.isPlaying()) {
                        mp.setLooping(false);
                        mp.start();
                    }
                });
            }else{
                Toast.makeText(this, "No hay grabación para reproducir", Toast.LENGTH_SHORT).show();
            }
        });


        pause = findViewById(R.id.ibPausar);
        pause.setOnClickListener( v -> {
            if(mp!=null){
                if(mp.isPlaying()){
                    posicion = mp.getCurrentPosition();
                    mp.pause();
                }
                else {
                    mp.seekTo(posicion);
                    mp.start();
                }

            }
        });

        reset = findViewById(R.id.ibStop);
        reset.setOnClickListener(v -> {
            if(mp!=null){
                if(mp.isPlaying()){
                    mp.stop();
                }
                mp.release();
            }
        });
    }



    public void onBotonVolverClicked(View view) {
        finish();
    }

    private String rutaANombre(@Nullable String ruta) {
        if (ruta != null){
            File file = new File(ruta);
            return file.getName();
        }else
            return getString(R.string.tv_f2_not_present);
    }

    public void reproducirAudio(){




    }
}