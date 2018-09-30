package com.example.fernando.SimpleAPP;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    public ArrayList<Post> mDataset;

    /**
     * Clase que almacena a la vista
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView author;
        public TextView created_at;
        public RelativeLayout viewBackground;
        public CardView viewForeground;

        public MyViewHolder(View v) {
            super(v);
            this.title = v.findViewById(R.id.txt_title);
            this.author = v.findViewById(R.id.txt_author);
            this.created_at = v.findViewById(R.id.txt_createdAt);
            this.viewBackground = v.findViewById(R.id.view_background);
            this.viewForeground = v.findViewById(R.id.view_foreground);
        }
    }

    /**
     * Constructor, pide como parametro una lista de Posts
     *
     * @param myDataset
     */
    public CustomAdapter(ArrayList<Post> myDataset) {
        mDataset = myDataset;
    }

    //region METODOS AUTOGENERADOS
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // Se crea una nueva vista
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        CreatedTimeHelper timeHelper = new CreatedTimeHelper(holder.itemView.getContext());

        holder.title.setText(mDataset.get(position).getTitle());
        holder.author.setText(mDataset.get(position).getAuthor());

        //Se genera una String que hace uso del objeto timeHelper para obtener un valor de hace cuanto tiempo se creo el Post
        String created_at = timeHelper.getDate(mDataset.get(position).getCreated_at());
        holder.created_at.setText(created_at);

        //Se genera un listener para manejar los eventos de toque en cada elemento del RecicleView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "URL: " + mDataset.get(position).getUrl(), Toast.LENGTH_SHORT).show();
                AbrirURL(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
    //endregion

    /**
     * Método encargado de remover un Post de la lista y notificarlo para reagrupar los elementos
     *
     * @param position
     */
    public void removePost(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Método encargado de abrir la url asociada al Post que tocó el usuario
     *
     * @param view
     * @param position
     */
    public void AbrirURL(View view, int position) {
        Intent i = new Intent(view.getContext(), WebView.class);
        String URL = mDataset.get(position).getUrl();
        i.putExtra("url", URL);
        view.getContext().startActivity(i);
    }
}

