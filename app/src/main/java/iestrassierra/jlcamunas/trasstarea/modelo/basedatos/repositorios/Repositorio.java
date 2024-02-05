package iestrassierra.jlcamunas.trasstarea.modelo.basedatos.repositorios;

import java.util.Date;
import java.util.List;

import iestrassierra.jlcamunas.trasstarea.modelo.entidades.Tarea;

public interface Repositorio {
    String NOMBRE_TABLA = "Tarea";
    String Col1 = "id";
    String Col2 = "titulo";
    String Col3= "fecha_creacion";
    String Col4 = "fecha_objetivo";
    String Col5 = "progreso";
    String Col6 = "prioritaria";
    String Col7 = "descripcion";
    String Col8 = "url_doc";
    String Col9 = "url_img";
    String Col10 = "url_aud";
    String Col11 = "url_vid";

    List<Tarea> getTareas();
    void anadirTarea(Tarea tarea);
    void reemplazarTarea(Tarea tarea);
    void eliminarTarea(Tarea tarea);
    int getCuentaTareas();
    float getProgresoMedio();
    int getCuentaPrioritarias();
    int getTareasSemana(Date today, Date nextWeek);
}
