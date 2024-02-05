package iestrassierra.jlcamunas.trasstarea.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import iestrassierra.jlcamunas.trasstarea.R;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Oculta la barra de acción
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_main);
        Button btEmpezar = findViewById(R.id.bt_main_empezar);
        btEmpezar.setOnClickListener(this::empezar);

    }
    private void empezar(View v){
        Intent aListado = new Intent(this, ListadoTareasActivity.class);
        startActivity(aListado);
    }

    @Override
    protected void onResume() {
        super.onResume();
        establecerFuente();
    }

    private void establecerFuente() {

        //FUENTE
        String tamanoLetra = sharedPreferences.getString("fuente", "M");
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

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
        resources.updateConfiguration(configuration, displayMetrics);
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

}