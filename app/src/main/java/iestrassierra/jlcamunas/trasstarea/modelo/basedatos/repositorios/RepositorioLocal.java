package iestrassierra.jlcamunas.trasstarea.modelo.basedatos.repositorios;

import java.util.Date;
import java.util.List;

import iestrassierra.jlcamunas.trasstarea.modelo.DAO.TareaDAO;
import iestrassierra.jlcamunas.trasstarea.modelo.basedatos.BaseDatos;
import iestrassierra.jlcamunas.trasstarea.modelo.entidades.Tarea;

public class RepositorioLocal implements Repositorio {

    //Vamos a mapear la interfaz RepositorioBD en la interfaz DAO
    private final TareaDAO miDao = BaseDatos.getInstance().tareaDAO();

    public RepositorioLocal() {
    }
    @Override
    public List<Tarea> getTareas() {
        return miDao.getTareas();
    }

    @Override
    public void anadirTarea(Tarea tarea) {
        miDao.anadirTarea(tarea);
    }

    @Override
    public void reemplazarTarea(Tarea tarea) {
        miDao.reemplazarTarea(tarea);
    }

    @Override
    public void eliminarTarea(Tarea tarea) {
        miDao.eliminarTarea(tarea);
    }

    @Override
    public int getCuentaTareas() {
        return miDao.getCuentaTareas();
    }

    @Override
    public float getProgresoMedio() {
        return miDao.getProgresoMedio();
    }

    @Override
    public int getCuentaPrioritarias() {
        return miDao.getCuentaPrioritarias();
    }

    @Override
    public int getTareasSemana(Date today, Date nextWeek) {
        return miDao.getTareasSemana(today, nextWeek);
    }
}
