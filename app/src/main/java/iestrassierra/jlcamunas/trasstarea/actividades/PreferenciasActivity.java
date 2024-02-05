package iestrassierra.jlcamunas.trasstarea.actividades;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Objects;

import iestrassierra.jlcamunas.trasstarea.R;

public class PreferenciasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_activity_preferencias);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_OK);
            finish();
        }
        return super.onOptionsItemSelected(item);
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

    ////////////////////////////////////////////// FRAGMENTO DE PREFERENCIAS ////////////////////////////////////////////
    public static class SettingsFragment extends PreferenceFragmentCompat
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public void onResume() {
            super.onResume();
            Objects.requireNonNull(getPreferenceManager().getSharedPreferences())
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            Objects.requireNonNull(getPreferenceManager().getSharedPreferences())
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            //Nota1: El cambio de preferencia sobre bd está siendo escuchado en la clase BaseDatos
            //Nota2: El cambio de preferencia sobre ficheros se utilizará en el fragmento 2, no hay que escucharlo aquí

            // Verifica si la clave de la preferencia cambiada es null
            if (key == null) {
                Log.e("Clave de preferencia nula", "La clave de preferencia es nula");
                return;
            }

            // Cambia el tema según el estado de las preferencias tema y contraste
            if (key.equals("contraste")) {
                boolean contraste = sharedPreferences.getBoolean("contraste", false);
                boolean tema = sharedPreferences.getBoolean("tema", false);
                if (contraste) {
                    //Si hay que utilizar el tema de alto contraste lo aplicaremos desde la actividad listado.
                    Toast.makeText(getActivity(), R.string.high_contrast, Toast.LENGTH_SHORT).show();

                    // Abrir el editor para modificar la preferencia de tema a claro
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("tema", false);
                    editor.apply();

                }
                //Si se ha modificado la preferencia de alto-contraste revisamos el tema claro/oscuro.
                AppCompatDelegate.setDefaultNightMode(tema ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                requireActivity().recreate();
            }

            if (key.equals("tema")){
                boolean contraste = sharedPreferences.getBoolean("contraste", false);
                boolean tema = sharedPreferences.getBoolean("tema", false);
                if (!contraste) {
                    // Si el tema de alto contraste está desactivado se cambia el tema
                    AppCompatDelegate.setDefaultNightMode(tema ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                }
            }

            if (key.equals("fuente")) {
                // Ajusta el tamaño de la fuente
                ajustarTamanoFuente(sharedPreferences.getString("fuente", "M"));
            }

            // Recrea la actividad para aplicar el cambio de preferencias inmediatamente
            requireActivity().recreate();
        }


        private void ajustarTamanoFuente(String tamanoLetra) {
            Resources resources = getResources();
            Configuration configuration = resources.getConfiguration();

            switch (tamanoLetra) {
                case "S":
                    configuration.fontScale = 0.8f;
                    break;
                case "L":
                    configuration.fontScale = 1.2f;
                    break;
                default:
                    configuration.fontScale = 1.0f;
            }
            //resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            resources.getConfiguration().updateFrom(configuration);

        }
    }

    //Método para el botón Restablecer
    public void restablecerPreferencias(View view) {
        //Creamos un AlertDialog como cuadro de diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_pref_title));
        builder.setMessage(getString(R.string.dialog_pref_msg));
        // Botón "Aceptar"
        builder.setPositiveButton(getString(R.string.dialog_pref_ok), (dialog, which) -> {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //Cambiamos todas las preferencias a sus valores por defecto
            editor.putBoolean("tema", false);
            editor.putBoolean("contraste", false);
            editor.putString("fuente", "M");
            editor.putString("criterio", "a");
            editor.putBoolean("orden", true);
            editor.putBoolean("sd", false);
            editor.putString("limpieza", "0");
            editor.putBoolean("bd", false);
            editor.putString("nombrebd", "trasstarea_db");
            editor.putString("ip", "10.0.2.2");
            editor.putString("puerto", "3306");
            editor.putString("usuario", "usuario");
            editor.putString("password", "usuario");
            //Aplicamos los cambios
            editor.apply();
            Toast.makeText(this,R.string.dialog_pref_confirm, Toast.LENGTH_SHORT).show();
        });
        // Botón "Cancelar"
        builder.setNegativeButton(getString(R.string.dialog_pref_cancel), (dialog, which) -> {
            dialog.dismiss(); // Cerrar el cuadro de diálogo.
        });
        // Mostrar el cuadro de diálogo
        builder.create().show();
    }
}
