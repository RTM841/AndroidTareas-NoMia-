package iestrassierra.jlcamunas.trasstarea.adaptadores;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import iestrassierra.jlcamunas.trasstarea.R;
import iestrassierra.jlcamunas.trasstarea.modelo.entidades.Tarea;


public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TaskViewHolder>{

    private final Context contexto;
    private final ArrayList<Tarea> tasks;
    private Tarea tareaSeleccionada;
    private boolean boolPrior;
    public void setBoolPrior(boolean boolPrior){
        this.boolPrior = boolPrior;
    }

    //Constructor
    public TareaAdapter(Context contexto, ArrayList<Tarea> tasks, boolean boolPrior) {
        this.contexto = contexto;
        this.tasks = tasks;
        this.boolPrior = boolPrior;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elemento = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista,parent,false);
        return new TaskViewHolder(elemento);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder taskViewHolder, int position) {

        Tarea tarea = tasks.get(position);
        taskViewHolder.bind(tarea);
        taskViewHolder.itemView.setTag(tarea); //Adjuntamos la tarea en la vista del ViewHolder
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Nullable
    public Tarea getTareaSeleccionada() {
        return tareaSeleccionada;
    } //Pasar la tarea seleccionada a la actividad

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitulo;
        private final ProgressBar pgProgreso;
        private final TextView tvFechaCreacion;
        private final TextView tvDias;

        //Método constructor
        private TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_elemento_titulo);
            pgProgreso = itemView.findViewById(R.id.pg_elemento);
            tvFechaCreacion = itemView.findViewById(R.id.tv_elemento_fecha);
            tvDias = itemView.findViewById(R.id.tv_elemento_ndias);

            //Mostrar el menú contextual
            itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
                //Hacemos la instantánea de cuál es la tarea seleccionada en el momento que se crea el viewHolder
                tareaSeleccionada = tasks.get(getAdapterPosition());
                //Mostramos el menú contextual
                MenuInflater inflater = new MenuInflater(contexto);
                inflater.inflate(R.menu.menu_contextual, menu);
            });
        }

        //Método que nos permitirá configurar cada elemento del Recycler con las informaciones
        //de la tarea
        private void bind(Tarea t) {
            tvTitulo.setText(t.getTitulo());
            pgProgreso.setProgress(t.getProgreso());
            tvFechaCreacion.setText(t.getStringFechaCreacion());

            int dias = 0;
            TextPaint paint = tvTitulo.getPaint();
            //Comprobación de tarea completada
            if(t.getProgreso()<100) { //Si la tarea no está completada
                dias = Tarea.diasHastaHoy(t.getFechaObjetivo());
                //Comprobación de días negativos para color rojo
                if (dias >= 0) {
                    tvDias.setTextColor(contexto.getColor(R.color.mi_color_primary)); // Establece el color primario
                } else {
                    tvDias.setTextColor(contexto.getColor(R.color.mi_color_accent)); // Establece el color acentuado
                }
                //Si la tarea no está completa bajamos la bandera de tachado
                paint.setFlags(paint.getFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }else{
                tvDias.setTextColor(contexto.getColor(R.color.mi_color_primary)); // Establece el color a primario
                // Si la tarea está completa ponemos la bandera de tachado
                paint.setFlags(paint.getFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            }
            //Si está completada el valor será 0
            tvDias.setText(String.format(Integer.toString(dias)));

            tvTitulo.setPaintFlags(paint.getFlags());
            tvTitulo.setText(t.getTitulo());

            //Comprobamos si la tarea es prioritaria o no para cambiar la imagen
            if(t.isPrioritaria())
                tvTitulo.setCompoundDrawablesWithIntrinsicBounds(
                        AppCompatResources.getDrawable(contexto,R.drawable.baseline_stars_24_gold),
                        null, null, null);
            else
                tvTitulo.setCompoundDrawablesWithIntrinsicBounds(
                        AppCompatResources.getDrawable(contexto,R.drawable.baseline_stars_24_black),
                        null, null, null);

            //Comprobamos si se tiene que mostrar el ítem de la lista según esté
            //activado el filtro SOLO prioritarias o no
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();

            if (t.isPrioritaria() || !boolPrior) {
                //Si la tarea es prioritaria o se muestran todas, mostramos el ítem
                layoutParams.height = LayoutParams.WRAP_CONTENT;
                layoutParams.width = LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);

            } else { //En caso contrario ocultamos el ítem
                layoutParams.height = 0;
                layoutParams.width = 0;
                itemView.setVisibility(View.GONE);
            }
        }

    }
}
