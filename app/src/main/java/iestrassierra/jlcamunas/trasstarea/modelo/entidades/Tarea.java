package iestrassierra.jlcamunas.trasstarea.modelo.entidades;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
public class Tarea implements Parcelable {

    @PrimaryKey (autoGenerate = true)
    private long id;
    @NonNull
    private String titulo;
    @NonNull
    @ColumnInfo(name = "fecha_creacion")
    private Date fechaCreacion;
    @NonNull
    @ColumnInfo(name = "fecha_objetivo")
    private Date fechaObjetivo;
    private Integer progreso;
    private Boolean prioritaria;
    private String descripcion;
    @ColumnInfo(name = "ruta_doc")
    private String rutaDocumento;
    @ColumnInfo(name = "ruta_img")
    private String rutaImagen;
    @ColumnInfo(name = "ruta_aud")
    private String rutaAudio;
    @ColumnInfo(name = "ruta_vid")
    private String rutaVideo;


    // Constructores

    public Tarea(){}

    public Tarea(@NonNull String titulo,@NonNull String fechaCreacion,@NonNull String fechaObjetivo,
                 int progreso, boolean prioritaria, @Nullable  String descripcion, @Nullable String rutaDocumento,
                 @Nullable String rutaImagen, @Nullable String rutaAudio, @Nullable String rutaVideo) {
        this.titulo = titulo;
        this.fechaCreacion = Tarea.stringADate(fechaCreacion);
        this.fechaObjetivo = Tarea.stringADate(fechaObjetivo);
        this.progreso = progreso;
        this.prioritaria = prioritaria;
        this.descripcion = descripcion;
        this.rutaDocumento = rutaDocumento;
        this.rutaImagen = rutaImagen;
        this.rutaAudio = rutaAudio;
        this.rutaVideo = rutaVideo;
    }

    // Getters y setters para acceder y modificar los atributos
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(@NonNull String titulo) {
        this.titulo = titulo;
    }

    public String getStringFechaCreacion() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(fechaCreacion);
    }

    public Date getFechaCreacion(){
        return this.fechaCreacion;
    }

    public void setFechaCreacion(String fecha){
        this.fechaCreacion = stringADate(fecha);
    }

    public String getStringFechaObjetivo() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(fechaObjetivo);
    }

    public void setFechaCreacion(@NonNull Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setFechaObjetivo(@NonNull Date fechaObjetivo) {
        this.fechaObjetivo = fechaObjetivo;
    }

    public Date getFechaObjetivo(){
        return this.fechaObjetivo;
    }

    public void setFechaObjetivo(String fecha){
        this.fechaObjetivo = stringADate(fecha);
    }

    public Integer getProgreso() {
        return progreso;
    }

    public void setProgreso(Integer progreso) {
        this.progreso = progreso;
    }

    public Boolean isPrioritaria() {
        return prioritaria;
    }

    public void setPrioritaria(Boolean prioritaria) {
        this.prioritaria = prioritaria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRutaDocumento() {
        return rutaDocumento;
    }

    public void setRutaDocumento(String RutaDocumento) {
        this.rutaDocumento = RutaDocumento;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String RutaImagen) {
        this.rutaImagen = RutaImagen;
    }

    public String getRutaAudio() {
        return rutaAudio;
    }

    public void setRutaAudio(String rutaAudio) {
        this.rutaAudio = rutaAudio;
    }

    public String getRutaVideo() {
        return rutaVideo;
    }

    public void setRutaVideo(String rutaVideo) {
        this.rutaVideo = rutaVideo;
    }

    //Métodos estáticos
    public static Date stringADate(@NonNull String fechaCreacion){
        Date fecha = new Date(); //Para evitar devolver null
        if (Tarea.validarFormatoFecha(fechaCreacion)) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                fecha = sdf.parse(fechaCreacion);
            } catch (Exception e) {
                Log.e("Error fecha","Parseo de fecha no válido");
            }
        } else {
            Log.e("Error fecha","Formato de fecha no válido");
        }
        return fecha;
    }
    private static boolean validarFormatoFecha(@NonNull String fecha) {
        String regex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/(19|20)\\d\\d$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fecha);
        return matcher.matches();
    }

    public static int diasHastaHoy(Date date) {
        Date hoy = new Date(); // Obtener la fecha actual
        long diferenciaMillis = date.getTime() - hoy.getTime();
        long diasDiferencia = TimeUnit.DAYS.convert(diferenciaMillis, TimeUnit.MILLISECONDS);
        return (int) diasDiferencia;
    }

    //Otros métodos

    @NonNull
    @Override
    public String toString() {
        return titulo;
    }

    //Métodos equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tarea)) return false;
        Tarea tarea = (Tarea) o;
        return id == tarea.id && Objects.equals(titulo, tarea.titulo) && Objects.equals(fechaCreacion, tarea.fechaCreacion)
                && Objects.equals(fechaObjetivo, tarea.fechaObjetivo) && Objects.equals(progreso, tarea.progreso)
                && Objects.equals(prioritaria, tarea.prioritaria) && Objects.equals(descripcion, tarea.descripcion)
                && Objects.equals(rutaDocumento, tarea.rutaDocumento) && Objects.equals(rutaImagen, tarea.rutaImagen)
                && Objects.equals(rutaAudio, tarea.rutaAudio) && Objects.equals(rutaVideo, tarea.rutaVideo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titulo, fechaCreacion, fechaObjetivo, progreso, prioritaria,
                descripcion, rutaDocumento, rutaImagen, rutaAudio, rutaVideo);
    }

    //Métodos para la interfaz Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.titulo);
        dest.writeLong(this.fechaCreacion.getTime());
        dest.writeLong(this.fechaObjetivo.getTime());
        dest.writeValue(this.progreso);
        dest.writeValue(this.prioritaria);
        dest.writeString(this.descripcion);
        dest.writeString(this.rutaDocumento);
        dest.writeString(this.rutaImagen);
        dest.writeString(this.rutaAudio);
        dest.writeString(this.rutaVideo);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readLong();
        this.titulo = source.readString();
        long tmpFechaCreacion = source.readLong();
        this.fechaCreacion = new Date(tmpFechaCreacion);
        long tmpFechaObjetivo = source.readLong();
        this.fechaObjetivo = new Date(tmpFechaObjetivo);
        this.progreso = (Integer) source.readValue(Integer.class.getClassLoader());
        this.prioritaria = (Boolean) source.readValue(Boolean.class.getClassLoader());
        this.descripcion = source.readString();
        this.rutaDocumento = source.readString();
        this.rutaImagen = source.readString();
        this.rutaAudio = source.readString();
        this.rutaVideo = source.readString();
    }

    protected Tarea(Parcel in) {
        this.id = in.readLong();
        this.titulo = in.readString();
        long tmpFechaCreacion = in.readLong();
        this.fechaCreacion = new Date(tmpFechaCreacion);
        long tmpFechaObjetivo = in.readLong();
        this.fechaObjetivo = new Date(tmpFechaObjetivo);
        this.progreso = (Integer) in.readValue(Integer.class.getClassLoader());
        this.prioritaria = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.descripcion = in.readString();
        this.rutaDocumento = in.readString();
        this.rutaImagen = in.readString();
        this.rutaAudio = in.readString();
        this.rutaVideo = in.readString();
    }

    public static final Creator<Tarea> CREATOR = new Creator<Tarea>() {
        @Override
        public Tarea createFromParcel(Parcel source) {
            return new Tarea(source);
        }

        @Override
        public Tarea[] newArray(int size) {
            return new Tarea[size];
        }
    };
}


