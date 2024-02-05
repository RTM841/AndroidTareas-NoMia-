package iestrassierra.jlcamunas.trasstarea.actividades;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import iestrassierra.jlcamunas.trasstarea.R;
import iestrassierra.jlcamunas.trasstarea.adaptadores.TareaAdapter;
import iestrassierra.jlcamunas.trasstarea.modelo.basedatos.BaseDatos;
import iestrassierra.jlcamunas.trasstarea.modelo.basedatos.repositorios.Repositorio;
import iestrassierra.jlcamunas.trasstarea.modelo.entidades.Tarea;
import iestrassierra.jlcamunas.trasstarea.modelo.entidades.TareaComparator;

public class ListadoTareasActivity extends AppCompatActivity {

    private RecyclerView rv;
    private TextView listadoVacio;
    private MenuItem menuItemPrior;
    private List<Tarea> tareas;
    private Executor executor;
    private Repositorio repositorio;
    private TareaAdapter tareaAdapter;
    private boolean boolPrior = false;
    private Tarea tareaSeleccionada;
    private SharedPreferences sharedPreferences;
    private boolean bdExterna;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_tareas);

        //handler será un comunicador con el hilo principal para poder acceder a las vistas de la UI
        handler = new Handler(Looper.getMainLooper());

        //Binding del TextView
        listadoVacio = findViewById(R.id.tv_listado_vacio);

        //Binding del RecyclerView
        rv = findViewById(R.id.recycler_listado);
        //Registramos el rv para el menú contextual
        registerForContextMenu(rv);

        //Creamos una instancia del controlador de Base de Datos ROOM
        BaseDatos.crearBaseDatos(this);

        //Obtenemos las preferencias compartartidas
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Leemos la preferencia de usuario sobre base de datos
        bdExterna = sharedPreferences.getBoolean("bd", false);

        //Creamos un hilo secundario
        executor = Executors.newSingleThreadExecutor();
        //En este hilo secundario accedemos a los repositorios
        executor.execute(() -> {
            //Obtenemos un repositorio (interno o externo) de BD del controlador ROOM
            repositorio = BaseDatos.getInstance().getRepositorioBD(bdExterna);
            //Añadimos a la lista las tareas del repositorio
            tareas = repositorio.getTareas();
            //Esperamos a que el hilo secundario haya obtenido la lista de tareas para proseguir en el hilo principal
            handler.post(() -> {
                if (tareas == null) {
                    tareas = new ArrayList<>();
                    Toast.makeText(this, R.string.toast_list_no_repository, Toast.LENGTH_SHORT).show();
                } else {
                    //Ordenamos la lista según las preferencia de usuario
                    if (tareas.size() > 0) {
                        ordenarLista();
                    }
                    //Seteamos el adaptador del RecyclerView
                    tareaAdapter = new TareaAdapter(ListadoTareasActivity.this, (ArrayList<Tarea>) tareas, boolPrior);
                    rv.setAdapter(tareaAdapter);
                    rv.setLayoutManager(new LinearLayoutManager(ListadoTareasActivity.this, LinearLayoutManager.VERTICAL, false));
                    //Comprobamos si el listado está vacío
                    comprobarListadoVacio();
                }
            });
            //En el hilo secundario comprobamos la limpieza automática de archivos según las preferencia de usuario
            int diasLimpieza = Integer.parseInt(sharedPreferences.getString("limpieza", "0"));
            autoLimpiarArchivos(diasLimpieza);
        });

    }

    //SALVADO DEL ESTADO GLOBAL DE LA ACTIVIDAD
    //Salva la lista de tareas y el valor booleano de prioritarias para el caso en que la actividad
    // sea destruida por ejemplo al cambiar la orientación del dispositivo
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("listaTareas", (ArrayList<Tarea>) tareas);
        outState.putBoolean("prioritarias", boolPrior);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Recuperamos la lista de Tareas y el booleano prioritarias
        tareas = savedInstanceState.getParcelableArrayList("listaTareas");
        boolPrior = savedInstanceState.getBoolean("boolPrior");
    }

    //Sobrescribiendo getTheme podemos establecer cualquier tema de nuestros recursos.
    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("contraste", false)){
            theme.applyStyle(R.style.Theme_TrassTarea_HighContrast, true);
        }else{
            theme.applyStyle(R.style.Theme_TrassTarea, true);
        }
        return theme;
    }

    /////////////////////////////////// MENÚ de la aplicación //////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menuItemPrior = menu.findItem(R.id.item_priority);
        //Colocamos el icono adecuado
        iconoPrioritarias();
        return super.onCreateOptionsMenu(menu);
    }

    ////////////////////////////////////// Opciones del Menú ///////////////////////////////////////
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        //OPCION CREAR TAREA
        if (id == R.id.item_add) {
            //Llamada al launcher con contrato y respuesta personalizados
            lanzadorCrearTarea.launch(null);
        }

        //OPCION MOSTRAR PRIORITARIAS / TODAS
        else if (id == R.id.item_priority) {

            //Conmutamos el valor booleano
            boolPrior = !boolPrior;
            //Colocamos el icono adecuado
            iconoPrioritarias();
            tareaAdapter.setBoolPrior(boolPrior);
            tareaAdapter.notifyDataSetChanged();

            //Comprobamos si el listado ha quedado vacío
            comprobarListadoVacio();
        }

        //OPCION PREFERENCIAS
        else if(id == R.id.item_settings){
            //Lanzamos la actividad de preferencias
            Intent aPreferencias = new Intent(this, PreferenciasActivity.class);
            lanzadorPreferencias.launch(aPreferencias);
        }

        //OPCIÓN ACERCA DE...
        else if (id == R.id.item_about) {

            ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.trasstarea);
            imageView.requestLayout();

            //Creamos un AlertDialog como cuadro de diálogo
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.about_title);
            builder.setView(imageView);
            String msg = getString(R.string.about_msg);
            builder.setMessage(msg);
            // Botón "Aceptar"
            builder.setPositiveButton(R.string.about_ok, (dialog, which) -> {
                dialog.dismiss(); // Cerrar el cuadro de diálogo.
            });
            // Mostrar el cuadro de diálogo
            builder.create().show();
        }

        //OPCION ESTADÍSTICAS
        else if (id == R.id.item_statistics){
            //Lanzamos la actividad de estadísticas
            Intent aEstadisticas = new Intent(this, EstadisticasActivity.class);
            //En este caso no lanzamos la actividad para obtener una respuesta.
            startActivity(aEstadisticas);
        }

        //OPCIÓN SALIR
        else if (id == R.id.item_exit) {
            Toast.makeText(this,R.string.msg_salida, Toast.LENGTH_SHORT).show();
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }


    //////////////////////////////// Opciones del menú contextual  /////////////////////////////////
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item){

        //Leemos la tarea seleccionada en el evento de mostrar el menú contextual
        tareaSeleccionada = tareaAdapter.getTareaSeleccionada();

        int itemId = item.getItemId();
        //OPCION DETALLE
        if (itemId == R.id.item_detalle) {
            //Creamos un intent y enviamos la tarea seleccionada
            Intent aDetallar = new Intent(this, DetallarTareaActivity.class);
            aDetallar.putExtra("tareaDetallar", tareaSeleccionada);
            //Lanzamos la actividad Detallar
            startActivity(aDetallar);
            return true;
        }

        //OPCION EDITAR
        else if (itemId == R.id.item_editar) {
            //Lanzador personalizado para la actividad Editar
            lanzadorEditarTarea.launch(tareaSeleccionada);
            return true;
        }

        //OPCION BORRAR
        else if (itemId == R.id.item_borrar) {
            if(tareaSeleccionada != null){
                // Mostrar un cuadro de diálogo para confirmar el borrado
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.dialog_confirmacion_titulo);
                builder.setMessage(getString(R.string.dialog_msg) + tareaSeleccionada.toString() + "?");
                builder.setPositiveButton(R.string.dialog_yes, (dialog, which) -> {
                    deleteTarea(tareaSeleccionada);
                    //Notificamos que la tarea ha sido borrada al usuario
                    Toast.makeText(this, R.string.dialog_erased, Toast.LENGTH_SHORT).show();
                });
                builder.setNegativeButton(R.string.dialog_no, null);
                builder.show();
            }else{
                // Si no se encuentra el elemento, mostrar un mensaje de error
                Toast.makeText(this, R.string.dialog_not_found, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }


    ///////////////////////// LANZADOR ACTIVIDAD PREFERENCIAS ////////////////////////
    private final ActivityResultLauncher<Intent> lanzadorPreferencias = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            activityResult -> {
                if (activityResult.getResultCode() == RESULT_OK) {
                    //Forzamos que se recree la actividad
                    recreate();
                }
            }
    );


    ///////////////////////// LANZADOR ACTIVIDAD CREAR //////////////////////////////

    //Contrato personalizado para el lanzador hacia la actividad CrearTareaActivity
    private final ActivityResultContract<Intent, Tarea> contratoCrear = new ActivityResultContract<Intent, Tarea>() {
        //En primer lugar se define el Intent de ida
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Intent intent) {
            return new Intent(context, CrearTareaActivity.class);
        }
        //Ahora se define el método de parseo de la respuesta. En este caso se recibe un objeto Tarea
        @Override
        public Tarea parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode == Activity.RESULT_OK && intent != null) {
                try {
                    return (Tarea) Objects.requireNonNull(intent.getParcelableExtra("NuevaTarea"));
                } catch (NullPointerException e) {
                    Log.e("Error en intent devuelto", Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),R.string.action_canceled, Toast.LENGTH_SHORT).show();
            }
            return null; // Devuelve null si no se pudo obtener una Tarea válida.
        }
    };

    //Registramos el lanzador hacia la actividad CrearTareaActivity con el contrato personalizado y respuesta con implementación anónima
    private final ActivityResultLauncher<Intent> lanzadorCrearTarea = registerForActivityResult(contratoCrear, nuevaTarea -> {
        if (nuevaTarea != null) {
            addTarea(nuevaTarea);
            Toast.makeText(getApplicationContext(), R.string.tarea_add, Toast.LENGTH_SHORT).show();
        }
    });

    ///////////////////////// LANZADOR ACTIVIDAD EDITAR //////////////////////////////

    //Contrato para el lanzador hacia la actividad EditarTareaActivity
    private final ActivityResultContract<Tarea, Tarea> contratoEditar = new ActivityResultContract<Tarea, Tarea>() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Tarea tarea) {
            Intent intent = new Intent(context, EditarTareaActivity.class);
            //Adjuntamos la tarea que va a ser editada
            intent.putExtra("TareaEditable", tarea);
            return intent;
        }

        @Override
        public Tarea parseResult(int i, @Nullable Intent intent) {
            if (i == Activity.RESULT_OK && intent != null) {
                try {
                    return (Tarea) Objects.requireNonNull(intent.getParcelableExtra("TareaEditada"));
                } catch (NullPointerException e) {
                    Log.e("Error en intent devuelto", Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }else if(i == Activity.RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),R.string.action_canceled, Toast.LENGTH_SHORT).show();
            }
            return null; // Devuelve null si no se pudo obtener una Tarea válida.
        }
    };

    //Respuesta para el lanzador hacia la actividad EditarTareaActivity
    private final ActivityResultCallback<Tarea> resultadoEditar = new ActivityResultCallback<Tarea>() {
        @Override
        public void onActivityResult(Tarea tareaEditada) {
            if (tareaEditada != null) {
                //Si se ha editado algo en la tarea seleccionada
                if(!tareaEditada.equals(tareaSeleccionada)) {
                    editTarea(tareaEditada);
                    //Comunicamos que la tarea ha sido editada al usuario
                    Toast.makeText(getApplicationContext(), R.string.tarea_edit, Toast.LENGTH_SHORT).show();
                }else{
                    //Comunicamos que la tarea no ha sido editada al usuario
                    Toast.makeText(getApplicationContext(), R.string.tarea_no_edit, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    //Registramos el lanzador hacia la actividad EditarTareaActivity con el contrato y respuesta personalizados
    private final ActivityResultLauncher<Tarea> lanzadorEditarTarea = registerForActivityResult(contratoEditar, resultadoEditar);


    //////////////////////////////////////// OTROS MÉTODOS /////////////////////////////////////////

    //Método para añadir una tarea en la lista y en la BD
    private void addTarea(Tarea tarea) {
        //Añadimos al repositorio en un hilo aparte
        executor.execute(() -> {
            repositorio.anadirTarea(tarea);
            actualizarTareas();
        });
    }

    //Método para editar una tarea en la lista y en la BD
    private void editTarea(Tarea tarea){
        //Editamos en el repositorio en un hilo aparte
        executor.execute(() -> {
            repositorio.reemplazarTarea(tarea);
            actualizarTareas();
        });
    }

    //Método para borrar una tarea de la lista y de la BD
    private void deleteTarea(Tarea tarea){
        //Borramos del repositorio en un hilo aparte
        executor.execute(() -> {
            repositorio.eliminarTarea(tarea);
            actualizarTareas();
        });
        //En el hilo principal podemos borrar los archivos adjuntos de la memoria
        borrarArchivos(tarea);
    }

    private void actualizarTareas(){
        //Añadimos a la lista las tareas del repositorio
        tareas = repositorio.getTareas();
        //Esperamos a que el hilo secundario haya obtenido la lista de tareas para proseguir en el hilo principal
        handler.post(() -> {
            //Ordenamos la lista según las preferencia de usuario
            ordenarLista();
            //Comprobamos si el listado está vacío
            comprobarListadoVacio();
            tareaAdapter.notifyItemRangeChanged(0,tareas.size());
            recreate();
        });
    }

    //Método para ordenar la lista en función de las preferencias del usuario
    private void ordenarLista(){
        String criterioOrdenacion = sharedPreferences.getString("criterio", "b");
        boolean ordenAsc = sharedPreferences.getBoolean("orden", true); //Orden ascendente
        tareas.sort(new TareaComparator(criterioOrdenacion, ordenAsc));
    }

    //Método para cambiar el icono de acción para mostrar todas las tareas o solo prioritarias
    private void iconoPrioritarias(){
        if(boolPrior)
            //Ponemos en la barra de herramientas el icono PRIORITARIAS
            menuItemPrior.setIcon(R.drawable.baseline_star_outline_24);
        else
            //Ponemos en la barra de herramientas el icono NO PRIORITARIAS
            menuItemPrior.setIcon(R.drawable.baseline_star_outline_24_crossed);
    }

    //Método que comprueba si el listado de tareas está vacío.
    //Cuando está vacío oculta el RecyclerView y muestra el TextView con el texto correspondiente.
    private void comprobarListadoVacio(){

        ViewTreeObserver vto = rv.getViewTreeObserver();

        //Observamos cuando el RecyclerView esté completamente terminado
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Contamos la altura total del RecyclerView
                int alturaRV = 0;
                for (int i = 0; i < tareaAdapter.getItemCount(); i++) {
                    View itemView = rv.getChildAt(i);
                    if (itemView != null)
                        alturaRV += itemView.getHeight();
                }

                if (alturaRV == 0) {
                    listadoVacio.setText(boolPrior?R.string.listado_tv_no_prioritarias:R.string.listado_tv_vacio);
                    listadoVacio.setVisibility(View.VISIBLE);
                } else {
                    listadoVacio.setVisibility(View.GONE);
                }

                // Remueve el oyente para evitar llamadas innecesarias
                rv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void borrarArchivos(Tarea tarea){
        String[] ruta = new String[4];
        File archivoBorrar;
        ruta[0] = tarea.getRutaDocumento();
        ruta[1] = tarea.getRutaImagen();
        ruta[2] = tarea.getRutaAudio();
        ruta[3] = tarea.getRutaVideo();

        for(int i=0; i<4; i++){
            if(ruta[i] != null) {
                archivoBorrar = new File(ruta[i]);
                // Verificar si el archivo existe antes de intentar borrarlo
                if (archivoBorrar.exists()) {
                    // Intentar borrar el archivo
                    if (archivoBorrar.delete()) {
                        Log.d("Archivo borrado", ruta[i]);
                    } else {
                        Log.e("Archivo no borrado", ruta[i]);
                    }
                }
            }
        }
    }

    private void autoLimpiarArchivos(int dias) {
        if (dias > 0) {
            //Directorio interno
            File directorioInterno = getFilesDir();
            File[] archivosInternos = directorioInterno.listFiles();
            borrarArchivosDirectorio(archivosInternos, dias);

            //Si la tarjeta SD está montada, obtenemos el directorio externo
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                File[] directorios = getExternalFilesDirs(null);
                // directorios[1] apunta al directorio privado en la tarjeta SD
                File directorioExterno = directorios[1];
                File[] archivosExternos = directorioExterno.listFiles();
                borrarArchivosDirectorio(archivosExternos, dias);
            }
        }
    }

    private void borrarArchivosDirectorio(File[] archivos, int dias){
        if (archivos != null) {
            // Fecha actual
            Date fechaActual = new Date();

            // Formato para parsear la marca de tiempo
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());

            // Calcular la fecha límite para borrar archivos
            long tiempoLimite = fechaActual.getTime() - (long) dias * 24 * 60 * 60 * 1000;

            // Recorrer la lista y buscar archivos antiguos para borrar
            for (File archivo : archivos) {
                if (archivo.isFile()) {
                    String nombreArchivo = archivo.getName();

                    // Extraer la marca de tiempo del nombre del archivo
                    String timeStamp = nombreArchivo.substring(nombreArchivo.lastIndexOf("_") + 1);

                    try {
                        // Parsear la marca de tiempo a formato de fecha
                        Date fechaArchivo = sdf.parse(timeStamp);

                        // Comparar con la fecha límite y borrar si es necesario
                        if (fechaArchivo.getTime() < tiempoLimite) {
                            if (archivo.delete()) {
                                Log.d("Autolimpieza", nombreArchivo);
                            } else {
                                Log.e("Autolimpieza fallida", nombreArchivo);
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
