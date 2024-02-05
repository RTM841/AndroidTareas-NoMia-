package iestrassierra.jlcamunas.trasstarea.adaptadores;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TareaViewModel extends ViewModel {

    //Usamos MutableLiveData para evitar problemas de ciclo de vida de los fragmentos
    private final MutableLiveData<String> titulo = new MutableLiveData<>();
    private final MutableLiveData<String> fechaCreacion = new MutableLiveData<>();
    private final MutableLiveData<String> fechaObjetivo = new MutableLiveData<>();
    private final MutableLiveData<Integer> progreso = new MutableLiveData<>();
    private final MutableLiveData<Boolean> prioritaria = new MutableLiveData<>();
    private final MutableLiveData<String> descripcion = new MutableLiveData<>();

    private final MutableLiveData<String> rutaDocumento = new MutableLiveData<>();
    private final MutableLiveData<String> rutaImagen = new MutableLiveData<>();
    private final MutableLiveData<String> rutaAudio = new MutableLiveData<>();
    private final MutableLiveData<String> rutaVideo = new MutableLiveData<>();

    private final MutableLiveData<Uri> mAudio = new MutableLiveData<>();


    //////////////////////// SETTERS Y GETTERS /////////////////////

    public MutableLiveData<String> getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo.setValue(titulo);
    }

    public MutableLiveData<String> getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion.setValue(fechaCreacion);
    }

    public MutableLiveData<String> getFechaObjetivo() {
        return fechaObjetivo;
    }

    public void setFechaObjetivo(String fechaObjetivo) {
        this.fechaObjetivo.setValue(fechaObjetivo);
    }

    public MutableLiveData<Integer> getProgreso() {
        return progreso;
    }

    public void setProgreso(int progreso) {
        this.progreso.setValue(progreso);
    }

    public MutableLiveData<Boolean> isPrioritaria() {
        return prioritaria;
    }

    public void setPrioritaria(boolean prioritaria) {
        this.prioritaria.setValue(prioritaria);
    }

    public MutableLiveData<String> getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.setValue(descripcion);
    }

    public MutableLiveData<String> getRutaDocumento() {
        return rutaDocumento;
    }

    public void setRutaDocumento(String rutaDocumento) {
        this.rutaDocumento.setValue(rutaDocumento);
    }

    public MutableLiveData<String> getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen.setValue(rutaImagen);
    }

    public MutableLiveData<String> getRutaAudio() {
        return rutaAudio;
    }
    public void setRutaAudio(String rutaAudio) {
        this.rutaAudio.setValue(rutaAudio);
    }

    public MutableLiveData<String> getRutaVideo() {
        return rutaVideo;
    }
    public void setRutaVideo(String rutaVideo) {
        this.rutaVideo.setValue(rutaVideo);
    }


    public LiveData<Uri> getAudio() {
        return mAudio;
    }
    public void setAudio(Uri audio){
        mAudio.setValue(audio);
    }

}