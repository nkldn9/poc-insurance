package poc.banking;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AndroidExplorer extends ListActivity {
 
	
	private List<String> item = null;
 	private List<String> path = null;
 private String root="/mnt/sdcard";
 private TextView myPath;
private String filenm="",filename;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dir);
        myPath = (TextView)findViewById(R.id.path);
        getDir(root);
    }
    
    
    private void getDir(String dirPath)
    {
     myPath.setText("Location: " + dirPath);
     
     item = new ArrayList<String>();
     path = new ArrayList<String>();
     
     File f = new File(dirPath);
     File[] files = f.listFiles();
     
     if(!dirPath.equals(root))
     {

      item.add(root);
      path.add(root);
      
      item.add("../");
      path.add(f.getParent());
            
     }
     
     for(int i=0; i < files.length; i++)
     {
       File file = files[i];
       path.add(file.getPath());
       if(file.isDirectory())
        item.add(file.getName() + "/");
       else
       {/*
    	   String strExtension[] = file.getName().toString().split(".");
    	   if(strExtension[1].toString() == "pdf"){
    		   item.add(file.getName());
    	   }*/
    	   item.add(file.getName());
       }
       
       }

     ArrayAdapter<String> fileList =
      new ArrayAdapter<String>(this, R.layout.row, item);
     setListAdapter(fileList);
    }

 @Override
 protected void onListItemClick(ListView l, View v, int position, long id) {
  
  File file = new File(path.get(position));
  
  if (file.isDirectory())
  {
   if(file.canRead())
    getDir(path.get(position));
   else
   {
	 filenm=file.getName();
	 new AlertDialog.Builder(this)
    .setIcon(R.drawable.icon)
    .setTitle(file.getName() + " folder can't be read!")
    .setPositiveButton("OK", 
      new DialogInterface.OnClickListener() {
       
       @Override
       public void onClick(DialogInterface dialog, int which) {
        // TODO Auto-generated method stub
       }
      }).show();
   }
  }
  else
  {
	 filenm=file.getAbsolutePath();
	 filename=file.getName();
   new AlertDialog.Builder(this)
    .setIcon(R.drawable.icon)
    .setTitle("You have selected file - " + file.getName())
    
    .setPositiveButton("OK", 
      new DialogInterface.OnClickListener() {
       
       @Override
       public void onClick(DialogInterface dialog, int which) {
        // TODO Auto-generated method stub
    	   
    	   SQLiteDatabase db=openOrCreateDatabase("POCICICIPRULIFE.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
    	   
           String CREATE_TABLE_filename ="CREATE TABLE IF NOT EXISTS FileName(filepath TEXT,filename TEXT);";
           db.execSQL(CREATE_TABLE_filename);
           ContentValues values = new ContentValues();
           values.put("filepath",filenm);
           values.put("filename",filename);          
			db.insert("FileName", null, values);
			db.close();
    	   finish();
       }
      }).show();
  }
 }
}
