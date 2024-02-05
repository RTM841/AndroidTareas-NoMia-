package iestrassierra.jlcamunas.trasstarea.modelo.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

import iestrassierra.jlcamunas.trasstarea.modelo.entidades.Tarea;
@Dao
public interface TareaDAO {

    // Consulta para la inserción de una tarea
    @Insert
    void anadirTarea(Tarea tarea);

    // Consulta para la actualización de una tarea por ID
    @Update
    void reemplazarTarea(Tarea tarea);

    // Consulta para el borrado de una tarea por ID
    @Delete
    void eliminarTarea(Tarea tarea);

    // Consulta de una tarea por ID
    @Query("SELECT * FROM Tarea WHERE id=:id")
    Tarea findById(int id);

    // Consulta de todas las tareas
    @Query("SELECT * FROM Tarea")
    List<Tarea> getTareas();

    // Consulta para obtener el número total de tareas
    @Query("SELECT COUNT(*) FROM Tarea")
    int getCuentaTareas();

    // Consulta para obtener el promedio de progreso de todas las tareas
    @Query("SELECT AVG(progreso) FROM Tarea")
    float getProgresoMedio();

    // Consulta para obtener el número de tareas prioritarias
    @Query("SELECT COUNT(*) FROM Tarea WHERE prioritaria = 1")
    int getCuentaPrioritarias();

    // Consulta para obtener el número de tareas con fecha objetivo entre hoy y una semana después
    @Query("SELECT COUNT(*) FROM Tarea WHERE fecha_objetivo BETWEEN :today AND :nextWeek")
    int getTareasSemana(Date today, Date nextWeek);

}
