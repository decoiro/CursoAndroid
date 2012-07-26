package com.mefaltaalgo.coirorssold.activities;

import java.text.DateFormat;

import com.mefaltaalgo.coirorssold.R;
import com.mefaltaalgo.coirorssold.db.FeedsDB.Posts;
import com.mefaltaalgo.coirorssold.parser.RssDownloadHelper;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class TitularesActivity extends ListActivity {
	private static final long FRECUENCIA_ACTUALIZACION = 60*60*1000; // recarga cada hora
	private static final int DIALOG_ABOUT = 0;
	private ActualizarPostAsyncTask tarea;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.feeds);
        setTitle(R.string.titulo_noticias);
        configurarAdapter();
    }
    
    @Override
    public void onResume() {
	    super.onResume();
	    cargarNoticias();
    }
    
    @Override
    protected void onStop() {
	    // Si hay una tarea corriendo en segundo plano, la paramos
	    if (tarea != null && !tarea.getStatus().equals(AsyncTask.Status.FINISHED)) {
	    	tarea.cancel(true);
	    }
	    super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void configurarAdapter(){
    	//Obtenemos todos los articulos de la BD
    	final String[] columnas = new String[] {
    			Posts._ID,
    			Posts.TITLE,
    			Posts.PUB_DATE
    	};
    	Uri uri = Uri.parse("content://es.exitae.blog/post");
    	Cursor cursor = managedQuery(uri, columnas, null, null, Posts.PUB_DATE + " DESC");
    	
    	//Queremos enterarnos si cambian los datos para recargar el cursor
    	cursor.setNotificationUri(getContentResolver(), uri);
    	startManagingCursor(cursor);    
    	
    	//Mapeamos las querys SQL a los campos de las vistas
    	String[] camposDb = new String[] {
    			Posts.TITLE,
    			Posts.PUB_DATE
    	};
    	int[] camposView = new int[]{
    			R.id.feedTitulo,
    			R.id.feedFecha
    	};
    	
    	//Creamos el adapter
    	SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.feeds_item, cursor, camposDb, camposView);
    	//La fecha se debe formatear
    	final java.text.DateFormat dateFormat = DateFormat.getDateTimeInstance();
    	adapter.setViewBinder(new ViewBinder() {
			
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				if (view.getId() == R.id.feedFecha) {
					long timestamp = cursor.getLong(columnIndex);
					((TextView)view).setText(dateFormat.format(timestamp));
					return true;
				} else {
					return false; // Que se encargue el adapter
				}
			}
		});
    	setListAdapter(adapter);
    }
    
    public void cargarNoticias() {
    	SharedPreferences prefs = getPreferences(MODE_PRIVATE);
    	long ultima = prefs.getLong("ultima_actualizacion", 0);
    	if ((System.currentTimeMillis() - ultima) > FRECUENCIA_ACTUALIZACION) {
	    	tarea = new ActualizarPostAsyncTask();
	    	tarea.execute();
    	}
    }
    
    public void setBarraProgresoVisible(boolean visible) {
    	final Window window = getWindow();
    	if (visible) {
	    	window.setFeatureInt(Window.FEATURE_PROGRESS,
	    	Window.PROGRESS_VISIBILITY_ON);
	    	window.setFeatureInt(Window.FEATURE_PROGRESS,
	    	Window.PROGRESS_INDETERMINATE_ON);
    	} else {
	    	window.setFeatureInt(Window.FEATURE_PROGRESS,
	    	Window.PROGRESS_VISIBILITY_OFF);
    	}
    }

    class ActualizarPostAsyncTask extends AsyncTask<Void, Void, Void>{
    	@Override
    	protected void onPreExecute(){
    		setBarraProgresoVisible(true);
    		super.onPreExecute();
    	}
    	@Override
    	protected Void doInBackground(Void...params){
    		MFAApplication app = (MFAApplication)getApplication();
    		RssDownloadHelper.updateRssData(app.getRssUrl(), getContentResolver());
    		
    		return null;
    	}
    
	    @Override
	    protected void onPostExecute(Void result) {
		    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		    Editor editor = prefs.edit();
		    editor.putLong("ultima_actualizacion",
		    System.currentTimeMillis());
		    editor.commit();
		    setBarraProgresoVisible(false);
	    }
	    @Override
	    protected void onCancelled() {
		    setBarraProgresoVisible(false);
		    // Se ha cancelado, la próxima vez que arranque deberá volver a
		    // cargarla
		    SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		    Editor editor = prefs.edit();
		    editor.putLong("ultima_actualizacion", 0);
		    editor.commit();
		    super.onCancelled();
	    }
    }
}
