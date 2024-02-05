package iestrassierra.jlcamunas.trasstarea.modelo.basedatos.repositorios;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import iestrassierra.jlcamunas.trasstarea.modelo.entidades.Tarea;

public class RepositorioExterno implements Repositorio {

    // Configuración para la conexión JDBC
    private final String jdbcUrl;
    private final String jdbcUser;
    private final String jdbcPassword;

    public RepositorioExterno(String jdbcUrl, String jdbcUser, String jdbcPassword) {
        this.jdbcUrl = jdbcUrl;
        this.jdbcUser = jdbcUser;
        this.jdbcPassword = jdbcPassword;
    }

    public Connection getConexion(){
        try{
            Connection conexion;
            Class.forName("com.mysql.jdbc.Driver");
            conexion = DriverManager.getConnection(jdbcUrl,jdbcUser,jdbcPassword);
            return conexion;
        }catch (SQLException e) {
            Log.e("Excepción SQL en la conexión", e.getSQLState());
        } catch (ClassNotFoundException e) {
            Log.e("Error de Driver", e.getMessage());
        }
        return null;
    }

    public ResultSet ejecutarConsulta(String consultaSQL){
        ResultSet resulSet;
        Connection con = getConexion();
        if(con != null) {
            try {
                Statement statement = con.createStatement();
                resulSet = statement.executeQuery(consultaSQL);
                return resulSet;
            } catch (SQLException e) {
                Log.e("Excepción SQL en la sentencia", e.getSQLState());
            }
        }else {
            Log.e("Error de conexión", "El objeto Connection es nulo");
        }
        return null;
    }

    public void ejecutarDML(String dmlSQL){
        Connection con = getConexion();
        if(con != null) {
            try {
                Statement statement = con.createStatement();
                statement.executeUpdate(dmlSQL);
            } catch (SQLException e) {
                Log.e("Excepción SQL en la operación dml", e.getSQLState());
            }
        }else {
            Log.e("Error de conexión", "El objeto Connection es nulo");
        }
    }

    //Implementamos los métodos de la interfaz RepositorioBD
    @Override
    public List<Tarea> getTareas() {
        List<Tarea> tareas = new ArrayList<>();
        String consultaSQL = "SELECT * FROM " + NOMBRE_TABLA;
        ResultSet resultSet = ejecutarConsulta(consultaSQL);
        if(resultSet != null) {
            try {
                while (resultSet.next()) {
                    Tarea tarea = resultSetATarea(resultSet);
                    tareas.add(tarea);
                }
                return tareas;
            } catch (SQLException e) {
                Log.e("Excepción SQL en el mapeado del ResulSet", e.getSQLState());
            }
        }
        return null;
    }

    //Método para parsear una fila del ResulSet en un objeto Tarea
    private Tarea resultSetATarea(ResultSet resultSet) throws SQLException {
        // Mapea los campos del ResultSet a un objeto Tarea
        Tarea tarea = new Tarea();
        tarea.setId(resultSet.getLong(Col1));
        tarea.setTitulo(resultSet.getString(Col2));
        tarea.setFechaCreacion(dateSqlAUtil(resultSet.getDate(Col3)));
        tarea.setFechaObjetivo(dateSqlAUtil(resultSet.getDate(Col4)));
        tarea.setProgreso(resultSet.getInt(Col5));
        tarea.setPrioritaria((resultSet.getInt(Col6) == 1)); //Si es cierto, guarda 'true'
        tarea.setDescripcion(resultSet.getString(Col7));
        tarea.setRutaDocumento(resultSet.getString(Col8));
        tarea.setRutaImagen(resultSet.getString(Col9));
        tarea.setRutaAudio(resultSet.getString(Col10));
        tarea.setRutaVideo(resultSet.getString(Col11));
        return tarea;
    }

    @Override
    public void anadirTarea(Tarea tarea) {
        String insertSQL ="INSERT INTO " + NOMBRE_TABLA + " (`" + Col1 + "`,`" + Col2 + "`,`" + Col3
                + "`,`" + Col4 + "`,`" + Col5 + "`,`" + Col6 + "`,`" + Col7 + "`,`" + Col8 + "`,`"
                + Col9 + "`,`" + Col10 + "`,`" + Col11 + "`) VALUES (" + "\""
                + tarea.getId() + "\", " + "\""
                + tarea.getTitulo() + "\", " + "\""
                + dateUtilASql(tarea.getFechaCreacion()) + "\", " + "\""
                + dateUtilASql(tarea.getFechaObjetivo()) + "\", " + "\""
                + tarea.getProgreso() + "\", " + "\""
                + (tarea.isPrioritaria()?1:0) + "\", " + "\""
                + tarea.getDescripcion() + "\", " + "\""
                + tarea.getRutaDocumento() + "\", " + "\""
                + tarea.getRutaImagen() + "\", " + "\""
                + tarea.getRutaAudio() + "\", " + "\""
                + tarea.getRutaVideo() +"\")";
        ejecutarDML(insertSQL);
    }

    @Override
    public void reemplazarTarea(Tarea tarea) {
        String updateSQL =  "UPDATE `" + NOMBRE_TABLA + "` SET " +
                "`" + Col2 + "` = \"" + tarea.getTitulo() + "\", " +
                "`" + Col3 + "` = \"" + dateUtilASql(tarea.getFechaCreacion()) + "\", " +
                "`" + Col4 + "` = \"" + dateUtilASql(tarea.getFechaObjetivo()) + "\", " +
                "`" + Col5 + "` = \"" + tarea.getProgreso() + "\", " +
                "`" + Col6 + "` = \"" + (tarea.isPrioritaria()?1:0) + "\", " +
                "`" + Col7 + "` = \"" + tarea.getDescripcion() + "\", " +
                "`" + Col8 + "` = \"" + tarea.getRutaDocumento() + "\", " +
                "`" + Col9 + "` = \"" + tarea.getRutaImagen() + "\", " +
                "`" + Col10 + "` = \"" + tarea.getRutaAudio() + "\", " +
                "`" + Col11 + "` = \"" + tarea.getRutaVideo() + "\" " +
                " WHERE `" + Col1 + "` = \"" + tarea.getId() + "\"";
        ejecutarDML(updateSQL);
    }

    @Override
    public void eliminarTarea(Tarea tarea) {
        String deleteSQL = "DELETE FROM`" + NOMBRE_TABLA + "` WHERE `" + Col1 + "` = " + tarea.getId();
        ejecutarDML(deleteSQL);
    }

    @Override
    public int getCuentaTareas() {
        String taskCounter = "SELECT COUNT(*) FROM " + NOMBRE_TABLA;
        try {
            ResultSet resultado = ejecutarConsulta(taskCounter);
            if (resultado.next())
                return resultado.getInt(1);
        } catch (SQLException e) {
            Log.e("Error en la cuenta de tareas", e.getSQLState());
        }
        return -1;
    }

    @Override
    public float getProgresoMedio() {
        String avgProgressQuery = "SELECT AVG(`" + Col5 + "`) FROM " +  NOMBRE_TABLA;
        try {
            ResultSet resultado = ejecutarConsulta(avgProgressQuery);
            if (resultado.next()) {
                return resultado.getFloat(1);
            }
        } catch (SQLException e) {
            Log.e("Error al obtener el progreso medio", e.getSQLState());
        }
        return -1f;
    }

    @Override
    public int getCuentaPrioritarias() {
        String countPriorityQuery = "SELECT COUNT(*) FROM " + NOMBRE_TABLA + " WHERE `" + Col6 + "` = 1";
        try {
            ResultSet resultado = ejecutarConsulta(countPriorityQuery);
            if (resultado.next()) {
                return resultado.getInt(1);
            }
        } catch (SQLException e) {
            Log.e("Error al obtener la cuenta de tareas prioritarias", e.getSQLState());
        }
        return -1;
    }

    @Override
    public int getTareasSemana(Date today, Date nextWeek) {
        String countTasksWeekQuery = "SELECT COUNT(*) FROM " + NOMBRE_TABLA + " WHERE `" + Col6 + "` BETWEEN ? AND ?";
        try {
            PreparedStatement statement = getConexion().prepareStatement(countTasksWeekQuery);

            statement.setDate(1, dateUtilASql(today));
            statement.setDate(2, dateUtilASql(nextWeek));
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
        } catch (SQLException e) {
            Log.e("Error al obtener la cuenta de tareas para la semana", e.getSQLState());
        }
        return -1;
    }

    //Conversores del tipo de Date
    private java.sql.Date dateUtilASql (java.util.Date date){
        return new java.sql.Date(date.getTime());
    }

    private java.util.Date dateSqlAUtil (java.sql.Date date){
        return new java.util.Date(date.getTime());
    }
}
