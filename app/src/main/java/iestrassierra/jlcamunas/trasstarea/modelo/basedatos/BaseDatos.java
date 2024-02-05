package iestrassierra.jlcamunas.trasstarea.modelo.basedatos;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import iestrassierra.jlcamunas.trasstarea.modelo.DAO.TareaDAO;
import iestrassierra.jlcamunas.trasstarea.modelo.basedatos.repositorios.Repositorio;
import iestrassierra.jlcamunas.trasstarea.modelo.basedatos.repositorios.RepositorioExterno;
import iestrassierra.jlcamunas.trasstarea.modelo.basedatos.repositorios.RepositorioLocal;
import iestrassierra.jlcamunas.trasstarea.modelo.entidades.Tarea;

@Database(entities = {Tarea.class}, version = 1, exportSchema = false)
@TypeConverters({ConversorFechas.class})
public abstract class BaseDatos extends RoomDatabase {

    private static SharedPreferences sharedPreferences;

    private static String databaseName;

    //Patrón Singleton
    private static volatile BaseDatos INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    //Método de clase para devolver una UNICA instancia de clase
    public static BaseDatos getInstance(){
        return INSTANCE;
    }

    public static void crearBaseDatos(final Context context) {
        //Leemos las preferencias gracias al contexto
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        databaseName = sharedPreferences.getString("nombrebd", "trasstarea_db");

        if (INSTANCE == null) {
            synchronized (BaseDatos.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BaseDatos.class, databaseName).build();
                }
            }
        }
    }

    //Método de instancia para obtener acceso a una clase de repositorio
    @Nullable
    public Repositorio getRepositorioBD(boolean useExternalDatabase) {
        if (useExternalDatabase) {
            // Implementación del repositorio para la base de datos externa
            String ip = sharedPreferences.getString("ip", "10.0.2.2");
            String port = sharedPreferences.getString("puerto", "3306");
            databaseName = sharedPreferences.getString("nombrebd", "trasstarea_db");
            String jdbcUrl = "jdbc:mysql://"+ip+":"+port+"/"+databaseName;

            String jdbcUser = sharedPreferences.getString("usuario", "usuario");
            String jdbcPassword = sharedPreferences.getString("password", "usuario");
            return new RepositorioExterno(jdbcUrl, jdbcUser, jdbcPassword);
        } else {
            // Implementación del repositorio para la base de datos interna
            return new RepositorioLocal();
        }
    }
    public abstract TareaDAO tareaDAO();

}




