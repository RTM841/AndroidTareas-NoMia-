package iestrassierra.jlcamunas.trasstarea.fragmentos;

import static androidx.core.content.ContextCompat.getExternalFilesDirs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import iestrassierra.jlcamunas.trasstarea.R;
import iestrassierra.jlcamunas.trasstarea.adaptadores.TareaViewModel;

public class FragmentoDos extends Fragment {

    private TareaViewModel tareaViewModel;
    private EditText etDescripcion;

    public enum TipoArchivo {
        DOCUMENTO,
        IMAGEN,
        AUDIO,
        VIDEO
    }
    private TipoArchivo tipoArchivoSeleccionado;
    private TextView tvDoc, tvPic, tvAud, tvMov;
    private Uri uriDoc, uriPic, uriAud, uriMov;

    private boolean savedInstanceStateRestored = false;

    //Interfaces de comunicación con la actividad para el botón Guardar y Volver
    public interface ComunicacionSegundoFragmento {
        void onBotonGuardarClicked();
        void onBotonVolverClicked();
    }

    private ComunicacionSegundoFragmento comunicadorSegundoFragmento;

    public FragmentoDos() {}
    //Sobrescribimos onAttach para establecer la comunicación entre el fragmento y la actividad
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ComunicacionSegundoFragmento) { //Si la actividad implementa la interfaz
            comunicadorSegundoFragmento = (ComunicacionSegundoFragmento) context; //La actividad se convierte en escuchadora
        } else {
            throw new ClassCastException(context + "Error de casting en el fragmento 2");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tareaViewModel = new ViewModelProvider(requireActivity()).get(TareaViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Inflamos el fragmento
        return inflater.inflate(R.layout.fragment_dos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Binding y set EditText y TextViews
        etDescripcion = view.findViewById(R.id.et_f2_descripcion);
        tvDoc = view.findViewById(R.id.tv_f2_documento);
        tvPic = view.findViewById(R.id.tv_f2_imagen);
        tvAud = view.findViewById(R.id.tv_f2_audio);
        tvMov = view.findViewById(R.id.tv_f2_video);

        //Restauramos el estado
        if (savedInstanceState != null && !savedInstanceStateRestored) {
            etDescripcion.setText(savedInstanceState.getString("descripcion"));
            tareaViewModel.setRutaDocumento(savedInstanceState.getString("rutaDoc"));
            tvDoc.setText(rutaANombre(tareaViewModel.getRutaDocumento().getValue()));
            tareaViewModel.setRutaImagen(savedInstanceState.getString("rutaImg"));
            tvPic.setText(rutaANombre(tareaViewModel.getRutaImagen().getValue()));
            tareaViewModel.setRutaAudio(savedInstanceState.getString("rutaAud"));
            tvAud.setText(rutaANombre(tareaViewModel.getAudio().toString()));
            tareaViewModel.setRutaVideo(savedInstanceState.getString("rutaVid"));
            tvMov.setText(rutaANombre(tareaViewModel.getRutaVideo().getValue()));
            savedInstanceStateRestored = true; // Marca que se ha restaurado el estado desde savedInstanceState
        }
        //O ponemos escuchadores al ViewModel
        else {
            tareaViewModel.getDescripcion().observe(getViewLifecycleOwner(),
                    etDescripcion::setText);
            tareaViewModel.getRutaDocumento().observe(getViewLifecycleOwner(),
                    doc -> tvDoc.setText(rutaANombre(doc)));
            tareaViewModel.getRutaImagen().observe(getViewLifecycleOwner(),
                    img -> tvPic.setText(rutaANombre(img)));
            tareaViewModel.getAudio().observe(getViewLifecycleOwner(),
                    aud -> tvAud.setText(rutaANombre(String.valueOf(aud))));
            tareaViewModel.getRutaVideo().observe(getViewLifecycleOwner(),
                    mov -> tvMov.setText(rutaANombre(mov)));
        }

        //Binding y config botones de adjuntar documentos
        ImageButton ibDoc = view.findViewById(R.id.ib_f2_documento);
        ibDoc.setOnClickListener(this::escuchadorBotonesArchivos);

        ImageButton ibPic = view.findViewById(R.id.ib_f2_imagen);
        ibPic.setOnClickListener(this::escuchadorBotonesArchivos);

        ImageButton ibAud = view.findViewById(R.id.ib_f2_audio);
        ibAud.setOnClickListener(this::escuchadorBotonesArchivos);

        ImageButton ibVid = view.findViewById(R.id.ib_f2_video);
        ibVid.setOnClickListener(this::escuchadorBotonesArchivos);

        //Binding y config boton Volver
        Button btVolver = view.findViewById(R.id.bt_f2_volver);
        btVolver.setOnClickListener(v -> {
            //Escribimos en el ViewModel
            String descripcion = etDescripcion.getText().toString();
            tareaViewModel.setDescripcion(descripcion);
            //Llamamos al método onBotonVolverClicked que está implementado en la actividad
            if(comunicadorSegundoFragmento != null)
                comunicadorSegundoFragmento.onBotonVolverClicked();
        });

        //Binding y config boton Guardar
        Button btGuardar = view.findViewById(R.id.bt_f2_guardar);
        btGuardar.setOnClickListener(v -> {
            //Escribimos en el ViewModel
            String descripcion = etDescripcion.getText().toString();
            tareaViewModel.setDescripcion(descripcion);
            //Copiar los archivos en memoria
            if(uriDoc != null){
                guardarArchivo(uriDoc, tareaViewModel.getRutaDocumento().getValue());
            }
            if(uriPic != null){
                guardarArchivo(uriPic, tareaViewModel.getRutaImagen().getValue());
            }
            if(uriAud != null){
                guardarArchivo(uriAud, tareaViewModel.getAudio().toString());
            }
            if(uriMov != null){
                guardarArchivo(uriMov, tareaViewModel.getRutaVideo().getValue());
            }
            //Llamamos al método onBotonGuardarClicked que está implementado en la actividad.
            if(comunicadorSegundoFragmento != null)
                comunicadorSegundoFragmento.onBotonGuardarClicked();
        });
    }

    //Método para guardar el estado del formulario en un Bundle
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String descripcion = etDescripcion.getText().toString();
        outState.putString("descripcion", descripcion);
        tareaViewModel.setDescripcion(descripcion); // Sincroniza la información también en el ViewModel
        outState.putString("rutaDoc", tareaViewModel.getRutaDocumento().getValue());
        outState.putString("rutaImg", tareaViewModel.getRutaImagen().getValue());
        outState.putString("rutaAud", tareaViewModel.getAudio().toString());
        outState.putString("rutaVid", tareaViewModel.getRutaVideo().getValue());
        savedInstanceStateRestored = false; // Restablece el indicador para futuros giros
    }

    private void escuchadorBotonesArchivos(View view){
        Intent archivos = new Intent(Intent.ACTION_GET_CONTENT);
        int botonId = view.getId();
        if (botonId == R.id.ib_f2_documento) {
            tipoArchivoSeleccionado = TipoArchivo.DOCUMENTO;
            //archivos.setType("*/*");
        } else if (botonId == R.id.ib_f2_imagen) {
            tipoArchivoSeleccionado = TipoArchivo.IMAGEN;
            //archivos.setType("image/*");
        } else if (botonId == R.id.ib_f2_audio) {
            //tipoArchivoSeleccionado = TipoArchivo.AUDIO;
            //archivos.setType("audio/*");



            //Creamos un intent para ir a la carpeta de grabaciones
            Intent aGrabaciones = new Intent();
            aGrabaciones.setType("audio/*");
            aGrabaciones.setAction(Intent.ACTION_GET_CONTENT);
            //Creamos otro intent para ir a la aplicación grabadora
            Intent aGrabadora = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            //Creamos un tercer intent que permitirá abrir un cuadro de diálogo para que
            //el usuario elija ir a la carpeta (opción principal) o la grabadora (opción secundaria)
            Intent chooser = new Intent(Intent.ACTION_CHOOSER);
            chooser.putExtra(Intent.EXTRA_TITLE, "Grabaciones"); //Título del diálogo
            chooser.putExtra(Intent.EXTRA_INTENT, aGrabaciones); //Opción principal
            Intent[] intentarray= {aGrabadora}; //Opciones secundarias, hay 1 sola
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS,intentarray);
            lanzadorGrabadora.launch(chooser);


            //lanzadorArchivos.launch(archivos);


        } else if (botonId == R.id.ib_f2_video) {
            tipoArchivoSeleccionado = TipoArchivo.VIDEO;
            //archivos.setType("video/*");
        }
        //Utilizamos un intent para el selector de archivos

    }

    ActivityResultLauncher<Intent> lanzadorArchivos = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult activityResult) {
                    if(activityResult.getResultCode() == Activity.RESULT_OK) {
                        Uri uri = activityResult.getData().getData();
                        if (uri != null) {
                            actualizarViewModel(uri, tipoArchivoSeleccionado);
                        }
                    }
                    //Si se cierra el explorador de archivos sin selección no hacemos nada
                }
            }
    );

    //Método que sirve para guardar en el ViewModel la uri seleccionada
    private void actualizarViewModel(Uri uri, TipoArchivo tipoArchivo) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String nombreArchivo = tipoArchivo.toString() + "_" + timeStamp;
        //Leemos la preferencia de usuario sobre almacenamiento
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean sd = sharedPreferences.getBoolean("sd", false);

        //Creamos un String para la ruta de almacenamiento interno (caso por defecto)
        String directorio = requireContext().getFilesDir().getAbsolutePath();

        //Si el almacenamiento es externo sobrescribimos la variable directorio
        if (sd) {
            //Si la tarjeta SD está disponible
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                File[] directorios = getExternalFilesDirs(requireContext(), null);
                // directorios[1] apunta al directorio privado en la tarjeta SD
                File directorioExterno = directorios[1];
                directorio = directorioExterno.getAbsolutePath();
            }
            //Si la tarjeta SD no está disponible
            else {
                Toast.makeText(requireContext(), R.string.dialog_f2_sd_not_mounted, Toast.LENGTH_SHORT).show();
                //Forzamos la preferencia de usuario para almacenamiento interno
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("sd", false);
                //Aplicamos los cambios
                editor.apply();
            }
        }

        //La ruta completa será la que persistiremos en la tarea
        String rutaCompleta = directorio + File.separator + nombreArchivo;

        //Escribimos las rutas en el ViewModel y se actualizarán los tv con los nombres del archivo
        //También guardamos las Uri para el proceso de guardado
        switch (tipoArchivo) {
            case DOCUMENTO:
                tareaViewModel.setRutaDocumento(rutaCompleta);
                uriDoc = uri;
                break;
            case IMAGEN:
                tareaViewModel.setRutaImagen(rutaCompleta);
                uriPic = uri;
                break;
            case AUDIO:
                tareaViewModel.setAudio(Uri.parse(rutaCompleta));
                uriAud = uri;
                break;
            case VIDEO:
                tareaViewModel.setRutaVideo(rutaCompleta);
                uriMov = uri;
                break;
        }
    }

    //Guardamos en la memoria interna o en la SD
    private void guardarArchivo(Uri uri, String rutaCompleta){
        try{
            File destino = new File(rutaCompleta);
            copyFile(uri, destino);
            Log.d("Archivo guardado", rutaCompleta);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            Log.e("Error al guardar el archivo", rutaCompleta);
            Log.e("Error", e.getLocalizedMessage());
        }
    }


    private void copyFile(Uri sourceUri, File destination) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = requireContext().getContentResolver().openInputStream(sourceUri);
            outputStream = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            Log.d("Archivo copiado", "De " + sourceUri + " a " + destination.getAbsolutePath());
        } finally {
            // Cerrar los flujos, incluso si hay una excepción
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String rutaANombre(@Nullable String ruta) {
        if (ruta != null){
            File file = new File(ruta);
            return file.getName();
        }else
            return getString(R.string.tv_f2_not_present);
    }

    ActivityResultLauncher<Intent> lanzadorGrabadora = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    Uri audio = o.getData() != null ? o.getData().getData() : null;
                    if (audio != null)
                        //Metemos en el ViewModel la Uri del audio
                        tareaViewModel.setAudio(audio);
                }
            }
    );
}