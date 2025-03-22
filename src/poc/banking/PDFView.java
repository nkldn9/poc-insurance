package poc.banking;

import java.io.File;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class PDFView extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
        Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File("/mnt/sdcard/DCIM/flower.png");
		intent.setData(Uri.fromFile(file.getAbsoluteFile())); //Uri.fromFile(file)
		//intent.setType("application/pdf");
		//startActivity(intent); 
		try {
            startActivity(intent);
        } 
         catch (ActivityNotFoundException e)
          {
            Log.e("","Activity not found: " , e);
    	}
    	
    }
}