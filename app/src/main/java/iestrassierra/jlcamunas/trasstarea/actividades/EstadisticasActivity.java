package iestrassierra.jlcamunas.trasstarea.actividades;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import iestrassierra.jlcamunas.trasstarea.R;
import iestrassierra.jlcamunas.trasstarea.modelo.basedatos.BaseDatos;
import iestrassierra.jlcamunas.trasstarea.modelo.basedatos.repositorios.Repositorio;

public class EstadisticasActivity extends AppCompatActivity {

    private Repositorio repositorio;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        //Handler será un comunicador con el hilo principal para poder acceder a las vistas de la UI
        handler = new Handler(Looper.getMainLooper());

        //Creamos un hilo independiente
        Executor executor = Executors.newSingleThreadExecutor();

        findViewById(R.id.bt_estadisticas_cerrar)
                .setOnClickListener(v -> finish());

        TextView tvVal1 = findViewById(R.id.tv_estadisticas_val1);
        TextView tvVal2 = findViewById(R.id.tv_estadisticas_val2);
        TextView tvVal3 = findViewById(R.id.tv_estadisticas_val3);
        TextView tvVal4 = findViewById(R.id.tv_estadisticas_val4);

        //Consultamos las preferencias de usuario en cuanto a almacén.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean esExterna = sharedPreferences.getBoolean("bd", false);

        //Obtenemos una instancia del almacén en un hilo aparte
        executor.execute(()->{
            repositorio = BaseDatos.getInstance().getRepositorioBD(esExterna);

            //Representación del número de tareas
            int nTareas = repositorio.getCuentaTareas();
            //Representación del número de tareas prioritarias
            int nPrioritarias = repositorio.getCuentaPrioritarias();
            //Promedio de progreso
            float avgProgreso = repositorio.getProgresoMedio();
            //Fecha de hoy
            Date hoy = new Date();
            //Fecha de dentro de una semana
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(hoy);
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
            Date proximaSemana = calendar.getTime();
            //Tareas que expiran en esta semana
            int tareas7dias = repositorio.getTareasSemana(hoy, proximaSemana);

            //Utilizamos el handler para escribir en las vistas en el hilo principal
            //También se puede utilizar runOnUiThread()
            handler.post(() -> {
                tvVal1.setText(String.valueOf(nTareas));
                tvVal2.setText(String.valueOf(nPrioritarias));
                tvVal3.setText(String.format("%s%%", avgProgreso));
                tvVal4.setText(String.valueOf(tareas7dias));
            });
        });

    }
}