  
package poc.banking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class ProcessProfilePhotoTask extends AsyncTask<File, ProcessProfilePhotoTask.ProcessingState, Bitmap> {


	public enum ProcessingState {
		STARTING,
		PROCESSING_LARGE,
		FINISHED
	}
	
   
    public static final String TAG = "ProcessProfilePhotoTask";
	
	public ProcessProfilePhotoTask() {
		super();
	}

	@Override
	protected Bitmap doInBackground(File... files) {
		ProcessingState[] s = new ProcessingState[1];
		//BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		if (files.length != 1) {
			throw new IllegalArgumentException("We expect to process only one file");
		}
		try {
			s[0] = ProcessingState.PROCESSING_LARGE;
			publishProgress(s);
			
			Bitmap largePhoto = BitmapFactory.decodeStream(new FileInputStream(files[0]));
			int height = largePhoto.getHeight();
            int width = largePhoto.getWidth();
            int density = largePhoto.getDensity();
            Log.d(TAG,"large image processing "+ height+"x"+width+"den="+density+"type=");
            
            return largePhoto;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}



}
