package poc.banking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Locale;


 

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

/*
 * Activity that demonstrates calling the MediaGallery,  
 * 
 */
public class MediaStoreTest extends Activity implements OnClickListener{
	
	protected static final int PHOTO_PICKED = 0;
	private static final String TEMP_PHOTO_FILE = "tempPhoto.jpg";
	protected static final int REQ_CODE_PICK_IMAGE=1;
	private static final int SELECT_PICTURE = 2;
	String imagePath;
	Button btnRotLeft,btnRotRight;
	Bitmap pic,tmpimg;
	SQLiteDatabase imgdb;
	int column_index,angle=0;
	private String selectedImagePath;
	Spinner proofs;
	EditText etDesc;
    //ADDED
    private String filemanagerstring,Proof_Spinner[];
	private Button mBtn,btnFin;
	protected ImageView photo;
	protected int outputX = 200;
	protected int outputY = 200;
	protected int aspectX = 1;
	protected int aspectY = 1;
	protected boolean return_data = false;
	protected MediaStoreTest thiz;
	protected boolean scale = true;
	protected boolean faceDetection = true;
	protected boolean circleCrop = false;
	private final static String TAG = "MediaStoreTest";
	String fname,lname;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thiz = this;
        setContentView(R.layout.media);
        
        Bundle extras=getIntent().getExtras();
        if(extras!=null)
        {
        	fname=extras.getString("Fname");
        	lname=extras.getString("Lname");
        }
        
        imgdb = openOrCreateDatabase("POCICICIPRULIFE.db", SQLiteDatabase.CREATE_IF_NECESSARY,null);
        imgdb.setVersion(1);
        imgdb.setLocale(Locale.getDefault());
        imgdb.setLockingEnabled(true);
        String CREATE_TABLE_ImageDB ="CREATE TABLE IF NOT EXISTS ImageDB(CustomerId integer PRIMARY KEY,ImgPath TEXT,ImgTag TEXT,ImgDesc TEXT);";
        imgdb.execSQL(CREATE_TABLE_ImageDB);
        
        btnRotLeft=(Button)findViewById(R.id.btnRotLeft);
        btnRotRight=(Button)findViewById(R.id.btnRotRight);
        mBtn = (Button) findViewById(R.id.btnLaunch);
        btnFin=(Button)findViewById(R.id.btnFinish);
        photo = (ImageView) findViewById(R.id.imgPhoto);
        etDesc=(EditText)findViewById(R.id.etDesc);
        
        mBtn.setOnClickListener(this);
        btnRotLeft.setOnClickListener(this);	
        btnRotRight.setOnClickListener(this);
        btnFin.setOnClickListener(this);
        
        Proof_Spinner=new String[4];
        Proof_Spinner[0]="ID Proof";
        Proof_Spinner[1]="Address Proof";
        Proof_Spinner[2]="Bank Statement";
        Proof_Spinner[3]="Electricity Bill";
        proofs = (Spinner) findViewById(R.id.spinnerTag);
        ArrayAdapter adapterproof = new ArrayAdapter(
        	            this, android.R.layout.simple_spinner_dropdown_item, Proof_Spinner);
        proofs.setAdapter(adapterproof);
        
			
        	
        
    }
    
    public void onClick(View v) {
    	if(v==mBtn)
    	{
    		try {
            // Launch picker to choose photo for selected contact
    		Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            //startActivityForResult(Intent.createChooser(intent,
             //       "Select Picture"), SELECT_PICTURE);
            /*Our Code*/
            String path=Environment.getExternalStorageDirectory().toString();
            path = path+"/POC Data/test1.jpg";
            File testfile=new File(path+"/POC Data/test1.jpg");
            
            /*Our Code Ends Here*/
            
            intent.setDataAndType(Uri.fromFile(testfile),"image/*");
            
            
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
            intent.putExtra("outputX", outputX);	
            intent.putExtra("outputY", outputY);
            intent.putExtra("scale", scale);
            intent.putExtra("return-data", return_data);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra("noFaceDetection",!faceDetection); // lol, negative boolean noFaceDetection
            if (circleCrop) {
            	intent.putExtra("circleCrop", true);
            }
            
            startActivityForResult(intent, PHOTO_PICKED);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(thiz, R.string.photoPickerNotFoundText, Toast.LENGTH_LONG).show();
        	}
    	}//mBtn
    	if(v==btnRotLeft)
    	{
    		
    	}
    	if(v==btnRotRight)
    	{
    		if(angle==360)
				angle=90;
			else
				angle+=90;
			
			// Getting width & height of the given image.
			int w = pic.getWidth();
			int h = pic.getHeight();
			// Setting post rotate to 90
			Matrix mtx = new Matrix();
			mtx.postRotate(angle);
			// Rotating Bitmap
			Bitmap rotatedBMP = Bitmap.createBitmap(pic, 0, 0, w, h, mtx, true);
			BitmapDrawable bmd = new BitmapDrawable(rotatedBMP);

			photo.setImageDrawable(bmd);
    	}	
    	if(v==btnFin)
    	{
    		
    		AlertDialog al=new AlertDialog.Builder(this).create();
        	al.setTitle(new AlertBox().title);
			al.setMessage("Select action");
			al.setButton("Save changes", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
    			// Action for 'Save' Button
	    		String imgproof=Environment.getExternalStorageDirectory().toString();
	            //imgproof = imgproof+"/POC Data/"+fname+"_"+lname+"_"+proofs.getSelectedItem().toString()+".jpg";
	    		File dir=new File(imgproof+"/POC Data");
	            dir.mkdir();
	    		try{
	    			File sd = Environment.getExternalStorageDirectory();
	    		    File data = Environment.getDataDirectory();
	    		    if (sd.canWrite()) {
	    		    	Toast.makeText(thiz, "Processing ...", Toast.LENGTH_LONG).show();
	    		        String sourceImagePath= "/tempPhoto.jpg";
	    		        String destinationImagePath= "/POC Data/" + fname+"_"+lname+"_"+proofs.getSelectedItem().toString()+".jpg";
	    		        File source= new File(sd, sourceImagePath);
	    		        File destination= new File(sd, destinationImagePath);
	    		        if (source.exists()) {
	    		            FileChannel src = new FileInputStream(source).getChannel();
	    		            FileChannel dst = new FileOutputStream(destination).getChannel();
	    		            dst.transferFrom(src, 0, src.size());
	    		            src.close();
	    		            dst.close();
	    		        }
	    		        }
	    			
	        		/*OutputStream gpxfile= new FileOutputStream(new File(dir,fname+"_"+lname+"_"+proofs.getSelectedItem().toString()+".jpg"));
	        		InputStream read=new FileInputStream(new File(imgproof,"tempPhoto.jpg"));
	        		tmpimg.compress(Bitmap.CompressFormat.JPEG, 90, gpxfile);*/
	                
	    		}
	    		catch(Exception e)
	    		{}
	    		imgproof=Environment.getExternalStorageDirectory().toString();
	            imgproof = imgproof+"/POC Data/"+fname+"_"+lname+"_"+proofs.getSelectedItem().toString()+".jpg";
	    		imgdb.execSQL("CREATE TABLE IF NOT EXISTS tmp(ImgPath TEXT,ImgTag TEXT,ImgDesc TEXT);");
	    		ContentValues values = new ContentValues();
            	values.put("ImgPath",imgproof);
	    		values.put("ImgTag",proofs.getSelectedItem().toString());
	    		values.put("ImgDesc",etDesc.getText().toString());
	    		imgdb.insert("tmp", null, values);
            	imgdb.close();
            		        
            	finish();
            	
	    		}
    			});
			al.setButton2("Return without saving", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
    			// Action for 'Continue' Button
	    		finish();
	    		}
    			});
			al.setIcon(new AlertBox().msgicon);
			al.show();
			
			
    	}
    		
	}
    
    private Uri getTempUri() {
		return Uri.fromFile(getTempFile());
	}
	
	private File getTempFile() {
		if (isSDCARDMounted()) {
			
			File f = new File(Environment.getExternalStorageDirectory(),TEMP_PHOTO_FILE);
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Toast.makeText(thiz, R.string.fileIOIssue, Toast.LENGTH_LONG).show();
			}
			return f;
		} else {
			return null;
		}
	}
	
	private boolean isSDCARDMounted(){
        String status = Environment.getExternalStorageState();
       
        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	//pic=(Bitmap)data.getExtras().get(MediaStore.EXTRA_OUTPUT);
    	/*if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();

                //OI FILE Manager
                filemanagerstring = selectedImageUri.getPath();

                //MEDIA GALLERY
                selectedImagePath = getPath(selectedImageUri);

                //DEBUG PURPOSE - you can delete this if you want
                if(selectedImagePath!=null)
                {
                	AlertDialog al=new AlertDialog.Builder(this).create();
                	al.setTitle("Title");
        			al.setMessage(""+selectedImagePath);
        			al.setButton("OK", new DialogInterface.OnClickListener() {
        	    	public void onClick(DialogInterface dialog, int id) {
            			// Action for 'OK' Button
            			
        	    		}
            			});
        			al.setIcon(R.drawable.icon);
        			al.show();
                }
                //System.out.println(selectedImagePath);  
                else System.out.println("selectedImagePath is null");
                if(filemanagerstring!=null)
                    System.out.println(filemanagerstring);
                else System.out.println("filemanagerstring is null");

                //NOW WE HAVE OUR WANTED STRING
                if(selectedImagePath!=null)
                    System.out.println("selectedImagePath is the right one for you!");
                else
                    System.out.println("filemanagerstring is the right one for you!");
            }}
    	*/
    	
    	switch (requestCode) {
		case PHOTO_PICKED:
			if (resultCode == RESULT_OK) {
				/*
				AlertDialog al=new AlertDialog.Builder(this).create();
            	al.setTitle("Title");
    			al.setMessage(""+"gggg");
    			al.setButton("OK", new DialogInterface.OnClickListener() {
    	    	public void onClick(DialogInterface dialog, int id) {
        			// Action for 'OK' Button
        			
    	    		}
        			});
    			al.setIcon(R.drawable.icon);
    			al.show();
				
				*/
					if (data == null) {
						Log.w(TAG, "Null data, but RESULT_OK, from image picker!");
		                Toast t = Toast.makeText(this, R.string.no_photo_picked,
		                                         Toast.LENGTH_SHORT);
		                t.show();
		                return;
					}
					
					final Bundle extras = data.getExtras();
	                if (extras != null) {
	                	
	                	//tmpimg=(Bitmap)extras.get();
	                		File tempFile = getTempFile();
	                	    // new logic to get the photo from a URI
	                		String path=Environment.getExternalStorageDirectory().toString();
	    		            //path = path+"/POC Data/test1.jpg";
	    		           File testfile=new File(path);//path
	    		            
	                		if (data.getAction() != null) {
	                			processPhotoUpdate(tempFile);
	                		}                  
	                }
			}
			break;
		case REQ_CODE_PICK_IMAGE:
	        if(resultCode == RESULT_OK){  
	            Uri selectedImage = data.getData();
	            String[] filePathColumn = {MediaStore.Images.Media.DATA};
	            AlertDialog al=new AlertDialog.Builder(this).create();
            	al.setTitle("Title");
    			al.setMessage(""+filePathColumn);
    			al.setButton("OK", new DialogInterface.OnClickListener() {
    	    	public void onClick(DialogInterface dialog, int id) {
        			// Action for 'OK' Button
        			
    	    		}
        			});
    			al.setIcon(R.drawable.icon);
    			al.show();
	            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
	            cursor.moveToFirst();

	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String filePath = cursor.getString(columnIndex);
	            cursor.close();


	            Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
	        }
	        break;
		}
    }
	
    	public String getPath(Uri uri) {
    		String[] projection = { MediaColumns.DATA };
    		Cursor cursor = managedQuery(uri, projection, null, null, null);
    		column_index = cursor
    		        .getColumnIndexOrThrow(MediaColumns.DATA);
    		cursor.moveToFirst();
    		 imagePath = cursor.getString(column_index);

    		return cursor.getString(column_index);
    		}
    	
    	
	/*
	 *  processes a temp photo file from 
	 */
	private void processPhotoUpdate(File tempFile) {
		ProcessProfilePhotoTask task = new ProcessProfilePhotoTask(){

			@Override
			protected void onPostExecute(Bitmap result) {
				android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(result.getWidth(),result.getHeight());
				params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
				photo.setLayoutParams(params);
				photo.setImageBitmap(result);
			}
			
		};
		task.execute(tempFile);
		
	}
}