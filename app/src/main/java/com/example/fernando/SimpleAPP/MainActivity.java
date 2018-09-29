package com.example.fernando.SimpleAPP;

import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //TODO: Refactorizar y añadir strings como recursos.
    //TODO: Ordenar y Comentar el codigo.
    //TODO: Dejar todo en ingles.
    //TODO: Hacer pruebas unitarias.

    private final static String POST_CREATED_AT = "created_at_i";
    private final static String POST_AUTHOR = "author";
    private final static String POST_STORY_TITLE = "story_title";
    private final static String POST_TITLE = "title";
    private final static String POST_STORY_URL = "story_url";
    private final static String POST_URL = "url";
    private final static String POST_ID = "objectID";
    private final static String API_url = "https://hn.algolia.com/api/v1/search_by_date?query=android";

    private ArrayList<Post> PostList = new ArrayList<>();
    private PostDataBase db = new PostDataBase(this);

    private RecyclerView.Adapter<CustomAdapter.MyViewHolder> adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        GetPosts(); //Llamada al metodo principal encargado de recuperar los Post

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new CustomAdapter(PostList);
        recyclerView.setAdapter(adapter);

        //region SWIPE MANAGE
        ItemTouchHelper.SimpleCallback itemTouchHelperSimpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView,@NonNull RecyclerView.ViewHolder viewHolder) {
                final View foregroundView = ((CustomAdapter.MyViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,@NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                        int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((CustomAdapter.MyViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                        actionState, isCurrentlyActive);
            }

            @Override
            public void onChildDrawOver(@NonNull Canvas c,@NonNull RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((CustomAdapter.MyViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                        actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
                //Se marca el flag de inactivo en la base de datos y se elimina el elemento del CustomAdapter.
                if (viewHolder instanceof CustomAdapter.MyViewHolder) {
                    int pos = viewHolder.getLayoutPosition();
                    Post post = ((CustomAdapter) adapter).mDataset.get(pos);
                    db.deletePost(post);
                    ((CustomAdapter) adapter).removePost(pos);
                    //()viewHolder
                }
            }
        };

        new ItemTouchHelper(itemTouchHelperSimpleCallback).attachToRecyclerView(recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetPosts();
            }
        });
        //endregion
    }

    /**
     * Metodo encargado de recuperar los datos de la URL,
     */
    private void GetPosts() {

        //Se comprueba si es que existe conexion
        if(checkNetwork()){
            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() { //Se ejecutan los procesos en una tarea asyncrona para no iterrumpir la UI
                @Override
                protected Void doInBackground(Void... voids) {
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());//Mediante el uso de la libreria Volley se instancia una nueva solicitud
                    //Se carga el Objeto Json desde la url y se construye el objeto Post segun los parametros necesarios extraidos (Titulo, Autor, TimeStamp, url, ObjectID)
                    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, API_url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONArray PostsArray = response.getJSONArray("hits");//El primer elemento del Json es un array que contiene los Post como objetos, llamado "hits"
                                        PostList.clear();//Se limpia la lista de Posts en caso de que se este haciendo un refresh
                                        for (int i = 0; i < PostsArray.length(); i++) {
                                            JSONObject postElements = PostsArray.getJSONObject(i);

                                            //Si "title" viene null, usar "story_title", si no usar "title".
                                            String title = postElements.getString(POST_TITLE).equals("null") ? postElements.getString(POST_STORY_TITLE) : postElements.getString(POST_TITLE);
                                            //Si "url" viene null, usar "story_url", si no usar "url".
                                            String url = postElements.getString(POST_URL).equals("null") ? postElements.getString(POST_STORY_URL) : postElements.getString(POST_URL);

                                            PostList.add(new Post(postElements.getInt(POST_CREATED_AT), //Se crea el objeto y se añade a la lista
                                                    postElements.getString(POST_AUTHOR),
                                                    title,
                                                    url,
                                                    postElements.getString(POST_ID),
                                                    0));

                                        }
                                        db.storePosts(PostList); //Se llama al metodo guardar de la base de datos.
                                        PostList.clear();
                                        PostList = db.getPosts();//Se cargan solo los Post que ya estan el base de datos en la lista, de esta forma no se cargan Post que hayan sido eliminados por el usuario
                                        if (PostList.isEmpty()) { //Si en este punto la lista esta vacia, significa que el usuario eliminó todos los post y no hay ninguno nuevo
                                            Toast.makeText(getApplicationContext(),"No se encontraron nuevos post", Toast.LENGTH_LONG).show();
                                        } else {
                                            ((CustomAdapter) adapter).mDataset = PostList; //Se entrega la lista como parametro al CustomAdapter para que la despliegue
                                            adapter.notifyDataSetChanged();
                                        }
                                        if(swipeRefreshLayout!=null) swipeRefreshLayout.setRefreshing(false); //Si se estaba haciendo un refresh, este se cancela
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        if(swipeRefreshLayout!=null) swipeRefreshLayout.setRefreshing(false);
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("DEBUG", "error: " + error.toString());
                                    if(swipeRefreshLayout!=null) swipeRefreshLayout.setRefreshing(false);
                                }
                            });

                    requestQueue.add(objectRequest);
                    return null;
                }
            };
            task.execute();
        }else{ //Si no hay conexion disponible
            Toast.makeText(getApplicationContext(),"No hay conexion", Toast.LENGTH_SHORT).show();
            PostList = db.getPosts(); //Se cargan los ultimos Post guardados en la base de datos
            if(swipeRefreshLayout!=null) swipeRefreshLayout.setRefreshing(false);
        }

    }

    /**
     * Metodo encargado de determinar si existe una conexion disponible y el estado de esta.
     * @return Boolean
     */
    private boolean checkNetwork(){

        boolean isConnected;

        ConnectivityManager connectivityManager =(ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        isConnected = activeNetwork!= null && activeNetwork.isConnected();
        return isConnected;
    }

}
