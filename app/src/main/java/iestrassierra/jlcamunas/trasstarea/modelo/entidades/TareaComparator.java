package iestrassierra.jlcamunas.trasstarea.modelo.entidades;

import java.util.Comparator;
import java.util.Date;

public class TareaComparator implements Comparator<Tarea> {
    private final String criterio; // Puede ser "a", "b", "c" o "d"
    private final boolean ordenAscendente; // true para ascendente, false para descendente

    public TareaComparator(String criterio, boolean ordenAscendente) {
        this.criterio = criterio;
        this.ordenAscendente = ordenAscendente;
    }

    @Override
    public int compare(Tarea tarea1, Tarea tarea2) {
        switch (criterio) {
            //Criterio alfabético
            case "a":
                return ordenAscendente ? tarea1.getTitulo().compareTo(tarea2.getTitulo())
                        : tarea2.getTitulo().compareTo(tarea1.getTitulo());
            //Criterio por fecha de creación
            case "b":
                Date fechaCreacion1 = Tarea.stringADate(tarea1.getStringFechaCreacion());
                Date fechaCreacion2 = Tarea.stringADate(tarea2.getStringFechaCreacion());
                return ordenAscendente ? fechaCreacion1.compareTo(fechaCreacion2)
                        : fechaCreacion2.compareTo(fechaCreacion1);
            //Criterio por días restantes
            case "c":
                int dias1 = tarea1.getProgreso() < 100 ? Tarea.diasHastaHoy(tarea1.getFechaObjetivo()) : 0;
                int dias2 = tarea2.getProgreso() < 100 ? Tarea.diasHastaHoy(tarea2.getFechaObjetivo()) : 0;
                return ordenAscendente ? Integer.compare(dias1, dias2):Integer.compare(dias2, dias1);
            //Criterio por progreso
            case "d":
                return ordenAscendente ? Integer.compare(tarea1.getProgreso(), tarea2.getProgreso())
                        : Integer.compare(tarea2.getProgreso(), tarea1.getProgreso());
            default:
                throw new IllegalArgumentException("Criterio no válido");
        }
    }
}
