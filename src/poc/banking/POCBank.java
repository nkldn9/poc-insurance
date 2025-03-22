package poc.banking;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Jpeg;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Section;





import poc.banking.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.ViewFlipper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

class AlertBox{
	public String title="POC | ICICI PRUDENTIAL LIFE INSURANCE";
	public int msgicon=R.drawable.icici;
	
}

class UserInformation{
	public String fname,lname,gender,sum,marstat,multiplier,anprem,top,photoid,dob,alstrat;;
	public int CurAlloc=0,BluFund,MCGF,DynPE,OppFund,MCBF,IFund,MMF; 
	public Bitmap PhotoID;
}

public class POCBank extends Activity implements OnClickListener{
    /** Called when the activity is first created. */
	Button next,previous,save,btnRestart,btnShow,btnNew,btnEx,btnExit,btnMenu,btnAttachPDF;
	Button btnRotate,btnAttach,btnAttachDoc,btnGenPDF,btnGoBack,btnBackMenu,btnGoMenu,btnClickMenu,btnBrowse,btnSaveDoc,btnDone,btnSync;
	RadioButton rb1,rb2;
	AlertDialog al;
	int PICK_REQUEST_CODE = 0;
	double myNum,myno;
	ViewFlipper vf;
	UserInformation userinfo;
	SQLiteDatabase db;
	RadioGroup rdogender;
	EditText etfname,etlname,etanprem,etsum,etIpFname,etIpLname,etBluFund,etMCGF,etDynPE,etOppFund,etMCBF,etIFund,etMMF,txtDocDesc;
	public String fileName,filePath;
	String lvfname,lvlname;
	Spinner smarital,smultiplier,stop,sfund,sproof;
	ListView lvAttach;
	private static final int SELECT_PICTURE = 1,CAMERA_PIC_REQUEST = 1337,BROWSE=5,FILE_NAME=7;
	private String selectedImagePath;
    private String filemanagerstring;
    TextView tvpickdob,tvShowCurAlloc,tvShowTotalAlloc,tvStar;
    TextView tvShowBluFund,tvShowMCGF,tvShowDynPE,tvShowOppFund,tvShowMCBF,tvShowIFund,tvShowMMF;
    TextView tvInfoBluFund,tvInfoMCGF,tvInfoDynPE,tvInfoOppFund,tvInfoMCBF,tvInfoIFund,tvInfoMMF;
	TextView tvShowOpName,tvShowOpDOB,tvShowOpGender,tvShowOpMarStat,tvShowOpMultiplier,tvShowOpAnPrem,tvShowOpSum,tvShowOpTOP,tvShowOpAlloc;
	TextView tvname,tvdob,tvgender,tvmarstat,tvmultiplier,tvanprem,tvsum,tvtop,tvfund,tvShowDocPath;
	TextView tvInfoName,tvInfoDob,tvInfoGender,tvInfoMarstat,tvInfoMultiplier,tvInfoAnprem,tvInfoSum,tvInfoTop,tvInfoFund;
	private String Marital_Spinner[],Multiplier_Spinner[],TOP_Spinner[],Fund_Spinner[];
	static final int DATE_DIALOG_ID = 0;
	ImageView image,imgOverview;
	Bitmap thumbnail;
	int angle=0,rowcount=0,custId,attachflag=0;
	Image tempimg;
	Bundle POCState;
	String[] Proof_Spinner;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //int or=getResources().getConfiguration().orientation;
       // if(or==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
       // else
        //	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        userinfo=new UserInformation();
        
        //Database
        
        db = openOrCreateDatabase("POCICICIPRULIFE.db", SQLiteDatabase.CREATE_IF_NECESSARY,null);
        db.setVersion(1);
        db.setLocale(Locale.getDefault());
        db.setLockingEnabled(true);
        String CREATE_TABLE_CustomerMaster ="CREATE TABLE IF NOT EXISTS CustomerMasterDetails(CustomerId integer PRIMARY KEY,FirstName TEXT,LastName TEXT,DOB TEXT,Gender TEXT,MaritalStatus TEXT,Multiplier TEXT,AnnualPremium TEXT,SumAssured TEXT,TermOfPolicy TEXT,AllocationStrategy varchar(30),BluechipFund integer,MultiCapGrowthFund integer,DynamicPEFund integer,OpportunitiesFund integer,MultiCapBalancedFund integer,IncomeFund integer,MoneyMarketFund integer);";
        db.execSQL(CREATE_TABLE_CustomerMaster);
        //Attached Docs Table
        db.execSQL("CREATE TABLE IF NOT EXISTS AttachedDocs(CustomerId integer,Path TEXT,Tag TEXT,Desc TEXT);");
        db.close();
        //Spinners
        Marital_Spinner=new String[4];
        Marital_Spinner[0]="Single";
        Marital_Spinner[1]="Married";
        Marital_Spinner[2]="Divorced";
        Marital_Spinner[3]="Widowed";
        smarital = (Spinner) findViewById(R.id.spinnerMaritalStatus);
        ArrayAdapter adapter = new ArrayAdapter(
        	            this, android.R.layout.simple_spinner_dropdown_item, Marital_Spinner);
        smarital.setAdapter(adapter);
        
        Multiplier_Spinner=new String[2];
        Multiplier_Spinner[0]="125";
        Multiplier_Spinner[1]="250";
        smultiplier = (Spinner) findViewById(R.id.spinnerMultiplier);
        ArrayAdapter adaptersm = new ArrayAdapter(
        	            this, android.R.layout.simple_spinner_dropdown_item, Multiplier_Spinner);
        smultiplier.setAdapter(adaptersm);
        
        TOP_Spinner=new String[3];
        TOP_Spinner[0]="10";
        TOP_Spinner[1]="20";
        TOP_Spinner[2]="30";
        stop = (Spinner) findViewById(R.id.spinnerTOP);
        ArrayAdapter adaptertop = new ArrayAdapter(
        	            this, android.R.layout.simple_spinner_dropdown_item, TOP_Spinner);
        stop.setAdapter(adaptertop);
        
        Fund_Spinner=new String[2];
        Fund_Spinner[0]="Trigger based Portfolio";
        Fund_Spinner[1]="Fixed Portfolio";
        sfund = (Spinner) findViewById(R.id.spinnerFund);
        ArrayAdapter adaptersf = new ArrayAdapter(
        	            this, android.R.layout.simple_spinner_dropdown_item, Fund_Spinner);
        sfund.setAdapter(adaptersf);
        
        Proof_Spinner=new String[4];
        Proof_Spinner[0]="ID Proof";
        Proof_Spinner[1]="Address Proof";
        Proof_Spinner[2]="Bank Statement";
        Proof_Spinner[3]="Electricity Bill";
        sproof = (Spinner) findViewById(R.id.spinnerTag);
        ArrayAdapter sadapter = new ArrayAdapter(
        	            this, android.R.layout.simple_spinner_dropdown_item, Proof_Spinner);
        sproof.setAdapter(sadapter);
        
        
        //EditText
        etfname=(EditText)findViewById(R.id.txtFname);
        etlname=(EditText)findViewById(R.id.txtLname);
        tvpickdob=(TextView)findViewById(R.id.lblPickDate);
        etanprem=(EditText)findViewById(R.id.txtAnnualPremium);
        etsum=(EditText)findViewById(R.id.txtSum);
        
        etBluFund=(EditText)findViewById(R.id.txtBluFund);
        etMCGF=(EditText)findViewById(R.id.txtMCGF);
        etDynPE=(EditText)findViewById(R.id.txtDynPE);
        etOppFund=(EditText)findViewById(R.id.txtOppFund);
        etMCBF=(EditText)findViewById(R.id.txtMCBF);
        etIFund=(EditText)findViewById(R.id.txtIFund);
        etMMF=(EditText)findViewById(R.id.txtMMF);
        txtDocDesc=(EditText)findViewById(R.id.txtDocDesc);
        
        //on focus changed 
        etBluFund.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                	try
                	{
                    	userinfo.BluFund=Integer.parseInt(etBluFund.getText().toString().trim());
                    }
                    catch(NumberFormatException nfe)
                    {
                    	userinfo.BluFund=0;
                    }
                    userinfo.CurAlloc=userinfo.BluFund+userinfo.MCGF+userinfo.DynPE+userinfo.OppFund+userinfo.MCBF+userinfo.IFund+userinfo.MMF;
                    tvShowCurAlloc.setText(""+userinfo.CurAlloc);
                	tvShowTotalAlloc.setText(""+userinfo.CurAlloc);
                }
            }
        });
        
        etMCGF.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                	try{
                			userinfo.MCGF=Integer.parseInt(etMCGF.getText().toString().trim());
                    	}
                    	catch(NumberFormatException nfe)
                    	{
                    		userinfo.MCGF=0;
                    	}
                    	userinfo.CurAlloc=userinfo.BluFund+userinfo.MCGF+userinfo.DynPE+userinfo.OppFund+userinfo.MCBF+userinfo.IFund+userinfo.MMF;
                        tvShowCurAlloc.setText(""+userinfo.CurAlloc);
                    	tvShowTotalAlloc.setText(""+userinfo.CurAlloc);
                }
            }
        });
        
        etDynPE.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                	try{
                		
                		userinfo.DynPE=Integer.parseInt(etDynPE.getText().toString().trim());
                    	}
                    	catch(NumberFormatException nfe)
                    	{
                    		userinfo.DynPE=0;
                    	}
                    	userinfo.CurAlloc=userinfo.BluFund+userinfo.MCGF+userinfo.DynPE+userinfo.OppFund+userinfo.MCBF+userinfo.IFund+userinfo.MMF;
                        tvShowCurAlloc.setText(""+userinfo.CurAlloc);
                    	tvShowTotalAlloc.setText(""+userinfo.CurAlloc);
                }
            }
        });
        
        etOppFund.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                	try{
                		
                		userinfo.OppFund=Integer.parseInt(etOppFund.getText().toString().trim());
                    	}
                    	catch(NumberFormatException nfe)
                    	{
                    		userinfo.OppFund=0;
                    	}
                    	userinfo.CurAlloc=userinfo.BluFund+userinfo.MCGF+userinfo.DynPE+userinfo.OppFund+userinfo.MCBF+userinfo.IFund+userinfo.MMF;
                        tvShowCurAlloc.setText(""+userinfo.CurAlloc);
                    	tvShowTotalAlloc.setText(""+userinfo.CurAlloc);
                }
            }
        });
        
        etMCBF.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                	try{
                    	userinfo.MCBF=Integer.parseInt(etMCBF.getText().toString().trim());
                    	}
                    	catch(NumberFormatException nfe)
                    	{
                    		userinfo.MCBF=0;
                    	}
                    	userinfo.CurAlloc=userinfo.BluFund+userinfo.MCGF+userinfo.DynPE+userinfo.OppFund+userinfo.MCBF+userinfo.IFund+userinfo.MMF;
                        tvShowCurAlloc.setText(""+userinfo.CurAlloc);
                    	tvShowTotalAlloc.setText(""+userinfo.CurAlloc);
                }
            }
        });
        
        etIFund.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                	try{
                    	userinfo.IFund=Integer.parseInt(etIFund.getText().toString().trim());
                    	}
                    	catch(NumberFormatException nfe)
                    	{
                    		userinfo.IFund=0;
                    	}
                    	userinfo.CurAlloc=userinfo.BluFund+userinfo.MCGF+userinfo.DynPE+userinfo.OppFund+userinfo.MCBF+userinfo.IFund+userinfo.MMF;
                        tvShowCurAlloc.setText(""+userinfo.CurAlloc);
                    	tvShowTotalAlloc.setText(""+userinfo.CurAlloc);
                }
            }
        });
        
        etMMF.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                	try{
                    	userinfo.MMF=Integer.parseInt(etMMF.getText().toString().trim());
                    	}
                    	catch(NumberFormatException nfe)
                    	{
                    		userinfo.MMF=0;
                    	}
                    	userinfo.CurAlloc=userinfo.BluFund+userinfo.MCGF+userinfo.DynPE+userinfo.OppFund+userinfo.MCBF+userinfo.IFund+userinfo.MMF;
                        tvShowCurAlloc.setText(""+userinfo.CurAlloc);
                    	tvShowTotalAlloc.setText(""+userinfo.CurAlloc);
                }
            }
        });
        
        
        //ListView
        lvAttach=(ListView)findViewById(R.id.listViewAttachC11);
        
        
        //Buttons,ViewFlipper,RadioGroup
        vf = (ViewFlipper) findViewById(R.id.ViewFlipper01);
        next = (Button) findViewById(R.id.ButtonNext);
        previous = (Button) findViewById(R.id.ButtonPrev);
        //save=(Button)findViewById(R.id.btnSave);
        rdogender=(RadioGroup)findViewById(R.id.radioGroupGender);
        
        //btnRestart=(Button)findViewById(R.id.btnRestart);
        btnEx=(Button)findViewById(R.id.btnEx);
        
        btnNew=(Button)findViewById(R.id.btnNew);
        btnSync=(Button)findViewById(R.id.btnSync);
        
        
        //btnExit=(Button)findViewById(R.id.btnExit);
        btnAttach=(Button)findViewById(R.id.btnAttach);
        
        btnAttachDoc=(Button)findViewById(R.id.btnAttachDoc);
        btnGenPDF=(Button)findViewById(R.id.btnGenPDF);
        btnGoBack=(Button)findViewById(R.id.btnGoBack);
        btnGoMenu=(Button)findViewById(R.id.btnGoMenu);
        btnBackMenu=(Button)findViewById(R.id.btnBackMenu);
        btnClickMenu=(Button)findViewById(R.id.btnClickMenu);
        btnBrowse=(Button)findViewById(R.id.btnBrowse);
        btnAttachPDF=(Button)findViewById(R.id.btnAttachPDF);
        btnDone=(Button)findViewById(R.id.btnDone);
        
        
        
        next.setOnClickListener(this); 
        previous.setOnClickListener(this);
       // save.setOnClickListener(this);
        tvpickdob.setOnClickListener(this);
        
      //  btnRestart.setOnClickListener(this);
        btnEx.setOnClickListener(this);
      //  btnExit.setOnClickListener(this);
        
        btnNew.setOnClickListener(this);
        
      //  btnMenu.setOnClickListener(this);
        
        btnAttach.setOnClickListener(this);
        
        btnAttachDoc.setOnClickListener(this);
        btnGenPDF.setOnClickListener(this);
        btnGoBack.setOnClickListener(this);
        btnGoMenu.setOnClickListener(this);
        btnBackMenu.setOnClickListener(this);
        btnClickMenu.setOnClickListener(this);
        btnBrowse.setOnClickListener(this);
        btnAttachPDF.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        btnSync.setOnClickListener(this);
        
        
        //TextView
        tvStar=(TextView)findViewById(R.id.lblStar);
        tvname=(TextView)findViewById(R.id.lblShowName);
        tvdob=(TextView)findViewById(R.id.lblShowDOB);
        tvgender=(TextView)findViewById(R.id.lblShowGender);
        tvmarstat=(TextView)findViewById(R.id.lblShowMarStat);
        tvmultiplier=(TextView)findViewById(R.id.lblShowMultiplier);
        tvanprem=(TextView)findViewById(R.id.lblShowAnPrem);
        tvsum=(TextView)findViewById(R.id.lblShowSum);
        tvtop=(TextView)findViewById(R.id.lblShowTop);
        tvfund=(TextView)findViewById(R.id.lblShowFund);
        
        tvShowCurAlloc=(TextView)findViewById(R.id.lblShowCurAlloc);
        tvShowTotalAlloc=(TextView)findViewById(R.id.lblShowTotalAlloc);
        tvShowBluFund=(TextView)findViewById(R.id.lblShowBluFund);
        tvShowMCGF=(TextView)findViewById(R.id.lblShowMCGF);
        tvShowDynPE=(TextView)findViewById(R.id.lblShowDynPE);
        tvShowOppFund=(TextView)findViewById(R.id.lblShowOppFund);
        tvShowMCBF=(TextView)findViewById(R.id.lblShowMCBF);
        tvShowIFund=(TextView)findViewById(R.id.lblShowIFund);
        tvShowMMF=(TextView)findViewById(R.id.lblShowMMF);
        tvInfoName=(TextView)findViewById(R.id.lblShowInfoName);
        tvInfoDob=(TextView)findViewById(R.id.lblShowInfoDOB);
        tvInfoGender=(TextView)findViewById(R.id.lblShowInfoGender);
        tvInfoMarstat=(TextView)findViewById(R.id.lblShowInfoMarStat);
        tvInfoMultiplier=(TextView)findViewById(R.id.lblShowInfoMultiplier);
        tvInfoAnprem=(TextView)findViewById(R.id.lblShowInfoAnPrem);
        tvInfoSum=(TextView)findViewById(R.id.lblShowInfoSum);
        tvInfoTop=(TextView)findViewById(R.id.lblShowInfoTop);
        tvInfoFund=(TextView)findViewById(R.id.lblShowInfoFund);
        tvInfoBluFund=(TextView)findViewById(R.id.lblShowInfoBluFund);
        tvInfoMCGF=(TextView)findViewById(R.id.lblShowInfoMCGF);
        tvInfoDynPE=(TextView)findViewById(R.id.lblShowInfoDynPE);
        tvInfoOppFund=(TextView)findViewById(R.id.lblShowInfoOppFund);
        tvInfoMCBF=(TextView)findViewById(R.id.lblShowInfoMCBF);
        tvInfoIFund=(TextView)findViewById(R.id.lblShowInfoIFund);
        tvInfoMMF=(TextView)findViewById(R.id.lblShowInfoMMF);
        tvShowDocPath=(TextView)findViewById(R.id.lblShowDocPath);
        
        //ImageView
        
        
        
        if(vf.getDisplayedChild()==0)
        {
        	next.setVisibility(View.INVISIBLE);
        	previous.setVisibility(View.INVISIBLE);
        	tvStar.setVisibility(View.INVISIBLE);
        	
        }
               
        al=new AlertDialog.Builder(this).create();
        smultiplier.setOnItemSelectedListener(
                new  AdapterView.OnItemSelectedListener() {           
           @Override
           public void onItemSelected(AdapterView<?> parent, 
             View view, int position, long id) {
        	   
        	   int pos=smultiplier.getSelectedItemPosition();
        	   myNum = Double.parseDouble(etanprem.getText().toString().trim());
   			   myno=Double.parseDouble(Multiplier_Spinner[pos]);
   		       etsum.setText(""+(long)(myNum*myno)/100);
   		   }

           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
           });
     }
    
    
       
  //Calendar
    // Creating dialog
    	@Override
    	protected Dialog onCreateDialog(int id) {
    	Calendar c = Calendar.getInstance();
    	
    	int cyear = c.get(Calendar.YEAR);
    	int cmonth = c.get(Calendar.MONTH);
    	int cday = c.get(Calendar.DAY_OF_MONTH);
    	al=new AlertDialog.Builder(this).create();
    	switch (id) {
    	case DATE_DIALOG_ID:
    	return new DatePickerDialog(this,  mDateSetListener,  cyear, cmonth, cday);
    	}
    	return null;
    	}
    	
    	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
    	// onDateSet method
    	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
    	String date_selected = String.valueOf(dayOfMonth)+"/"+String.valueOf(monthOfYear+1)+"/"+String.valueOf(year);
    	Calendar c = Calendar.getInstance();
    	int cyear = c.get(Calendar.YEAR);
    	int cmonth = c.get(Calendar.MONTH);
    	int cday = c.get(Calendar.DAY_OF_MONTH);
    	if(year>cyear)
    	{
    		//alertbox
    		al.setTitle(new AlertBox().title);
			al.setMessage("Please select valid Year.");
			al.setButton("OK", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int id) {
    			// Action for 'OK' Button
    			
	    		}
    			});
			al.setIcon(new AlertBox().msgicon);
			al.show();
    	}
    	else if(year==cyear)
    	{
    		if(monthOfYear>cmonth)
    		{	//alertbox
    			al.setTitle(new AlertBox().title);
    			al.setMessage("Please select valid Month.");
    			al.setButton("OK", new DialogInterface.OnClickListener() {
    	    	public void onClick(DialogInterface dialog, int id) {
        			// Action for 'OK' Button
        			
    	    		}
        			});
    			al.setIcon(new AlertBox().msgicon);
    			al.show();
    		}
    		else if(monthOfYear<=cmonth)
    		{
    			if(dayOfMonth>cday)
    			{
    				//alertbox
    				al.setTitle(new AlertBox().title);
    				al.setMessage("Please select valid Date.");
    				al.setButton("OK", new DialogInterface.OnClickListener() {
    		    	public void onClick(DialogInterface dialog, int id) {
    	    			// Action for 'OK' Button
    	    			
    		    		}
    	    			});
    				al.setIcon(new AlertBox().msgicon);
    				al.show();
    			}
    			else
    				tvpickdob.setText(date_selected);
    		}//month compare
    		
    	}//year compare
    	else
			tvpickdob.setText(date_selected);
    	
    	}
    	};
    
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
    	
    	if(v==btnNew)
    	{
    		vf.setDisplayedChild(1);
    		next.setVisibility(View.VISIBLE);
        	previous.setVisibility(View.VISIBLE);
        	tvStar.setVisibility(View.VISIBLE);
        	
    	}
    	/*if(v==btnSearch)
    	{
    		vf.setDisplayedChild(10);
    		previous.setVisibility(View.INVISIBLE);
    		next.setVisibility(View.INVISIBLE);
    		tvStar.setVisibility(View.VISIBLE);
    	}*/
    	if(v==btnBackMenu)
    	{
    		Intent intent = getIntent();
        	finish();
        	startActivity(intent);
    	}
    	if(v==btnGoMenu)
    	{
    		Intent intent = getIntent();
        	finish();
        	startActivity(intent);
    	}
    	if(v==btnGenPDF)
    	{
    		
    		generatePDFFile(); //	--working code
    		/*	not working-->
    		try{
    		PdfReader reader1 = new PdfReader("/mnt/sdcard/POC Data/I_N.pdf");
    		PdfReader reader2 = new PdfReader("/mnt/sdcard/POC Data/N_D.pdf");
    		PdfCopyFields copy = new PdfCopyFields(new FileOutputStream("/mnt/sdcard/POC Data/concatenatedPDF.pdf"));
    		copy.addDocument(reader1);
    		copy.addDocument(reader2);
    		copy.close();
    		}
    		catch(Exception e){
    		
    		}
    		<----not working code
    		*/
    		//update("/mnt/sdcard/POC Data/N_D.pdf");
    		/* working code---> */
    		
    		Toast.makeText(POCBank.this, "Generating PDF....", Toast.LENGTH_LONG).show();
    		String pdf1="/mnt/sdcard/POC Data/"+lvfname+"_"+lvlname+".pdf";
    		String pdf2="";
    		db.close();
    		try
			{	
    		db = openOrCreateDatabase("POCICICIPRULIFE.db", SQLiteDatabase.CREATE_IF_NECESSARY,null);
			Cursor crsrPDF=db.query("AttachedDocs",new String[]{"CustomerId","Path","Tag","Desc"},"CustomerId="+custId,null,null,null,null,null);
			int count=crsrPDF.getCount();
			crsrPDF.moveToFirst();
			PdfCopyFields copy = new PdfCopyFields(new FileOutputStream("/mnt/sdcard/POC Data/C_"+lvfname+"_"+lvlname+".pdf"));
			PdfReader reader1 = new PdfReader(pdf1);//"1PDF.pdf"
			copy.addDocument(reader1);
			for(int i=0;i<count;i++)
			{
								
					pdf2="/mnt/sdcard"+crsrPDF.getString(1);
					String tag=crsrPDF.getString(2);
					String desc=crsrPDF.getString(3);
					
					PdfReader reader2 = new PdfReader(pdf2);//"2PDF.pdf"
					//copy.open();
										
					
					copy.addDocument(reader2);
					
					
				
				
				crsrPDF.moveToNext();
			}
			copy.close();
			crsrPDF.close();
			db.close();
			}
			catch(Exception e){}
			/*<-----working code */
				
			// View PDF
    		/* Not working code----> 
    		Toast.makeText(POCBank.this, "Opening PDF....", Toast.LENGTH_LONG).show();
			Intent intent = new Intent();//this,PDFView.class
			intent.setAction(android.content.Intent.ACTION_RUN);
			File file = new File("/mnt/sdcard/tempPhoto.jpg");
			intent.setData(Uri.fromFile(file.getAbsoluteFile())); //Uri.fromFile(file)
			intent.setType("application/pdf");
			//startActivity(intent); 
			try {
                startActivity(intent);
            } 
             catch (ActivityNotFoundException e)
              {
                Log.e("","Activity not found: " , e);
        	}
			/*<---Not working code*/
			
    	}
    	
    	if(v==btnAttach)
    	{
    		vf.setDisplayedChild(7);
    		previous.setVisibility(View.INVISIBLE);
    		next.setVisibility(View.INVISIBLE);
    		tvStar.setVisibility(View.INVISIBLE);
    		
    		//Retrieve records from db
    		db = openOrCreateDatabase("POCICICIPRULIFE.db", SQLiteDatabase.CREATE_IF_NECESSARY,null);
			Cursor crsrlv=db.query("CustomerMasterDetails",new String[]{"CustomerId","FirstName","LastName","DOB","Gender","MaritalStatus","Multiplier","AnnualPremium","SumAssured","TermOfPolicy","AllocationStrategy"},null,null,null,null,null,null);
			int count=crsrlv.getCount();
			String records[]=new String[count];
			crsrlv.moveToFirst();
					
			for(int i=0;i<count;i++)
			{
				records[i]=(i+1)+" | "+crsrlv.getString(1)+" "+crsrlv.getString(2)+" | "+crsrlv.getString(8);
				crsrlv.moveToNext();
			}
			//String lvAttachRecord_arr[]={records};
	    	lvAttach.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1 , records));						
	    	
			crsrlv.close();
	        db.close();
    		
    		
    		// By using setTextFilterEnabled method in listview we can filter the listview items.
    		lvAttach.setTextFilterEnabled(true);
    		lvAttach.setOnItemClickListener(new OnItemClickListener() {
    			@Override
    			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
    			/*
    			AlertDialog.Builder adb=new AlertDialog.Builder(POCBank.this);
    			adb.setTitle("LVSelectedItemExample");
    			adb.setMessage("Selected Item is = "+lvAttach.getItemAtPosition(position));
    			adb.setPositiveButton("Ok", null);
    			adb.show();
    			*/
    				Toast.makeText(POCBank.this, "Processing ...", Toast.LENGTH_LONG).show();
    				String name=lvAttach.getItemAtPosition(position).toString();
    				String[] strnames=name.split(" | ");
    				//strnames[0] will be sr no, strnames[1] will be  |, strnames[2] will be fname, strnames[3] will be lname, strnames[4] will be |, strnames[5] will be sum,
    				
    				lvfname=strnames[2];
    				lvlname=strnames[3];
    				
    				//Retrieve record
    	    		db = openOrCreateDatabase("POCICICIPRULIFE.db", SQLiteDatabase.CREATE_IF_NECESSARY,null);

    				Cursor crsrrec=db.query("CustomerMasterDetails",new String[]{"CustomerId","FirstName","LastName","DOB","Gender","MaritalStatus","Multiplier","AnnualPremium","SumAssured","TermOfPolicy","AllocationStrategy","BluechipFund","MultiCapGrowthFund","DynamicPEFund","OpportunitiesFund","MultiCapBalancedFund","IncomeFund","MoneyMarketFund"},"FirstName='"+lvfname+"' AND LastName='"+lvlname+"'",null,null,null,null,null);
    				crsrrec.moveToFirst();
    				//set text to overview labels
		        	custId=crsrrec.getInt(0);
    				tvInfoName.setText(": "+crsrrec.getString(1)+" "+crsrrec.getString(2));
		        	tvInfoDob.setText(": "+crsrrec.getString(3));
		        	tvInfoGender.setText(": "+crsrrec.getString(4));
		        	tvInfoMarstat.setText(": "+crsrrec.getString(5));
		        	tvInfoMultiplier.setText(": "+crsrrec.getString(6));
		        	tvInfoAnprem.setText(": "+crsrrec.getString(7));
		        	tvInfoSum.setText(": "+crsrrec.getString(8));
		        	tvInfoTop.setText(": "+crsrrec.getString(9));
		        	tvInfoFund.setText(": "+crsrrec.getString(10));
		        	tvInfoBluFund.setText(": "+crsrrec.getInt(11));
					tvInfoMCGF.setText(": "+crsrrec.getInt(12));
					tvInfoDynPE.setText(": "+crsrrec.getInt(13));
					tvInfoOppFund.setText(": "+crsrrec.getInt(14));
					tvInfoMCBF.setText(": "+crsrrec.getInt(15));
					tvInfoIFund.setText(": "+crsrrec.getInt(16));
					tvInfoMMF.setText(": "+crsrrec.getInt(17));
    				
					//set userinfo variables for PDF
					userinfo.fname=crsrrec.getString(1);
    				userinfo.lname=crsrrec.getString(2);
    				userinfo.dob=crsrrec.getString(3);
    				userinfo.gender=crsrrec.getString(4);
    				userinfo.marstat=crsrrec.getString(5);
    				userinfo.multiplier=crsrrec.getString(6);
    				userinfo.anprem=crsrrec.getString(7);
    				userinfo.sum=crsrrec.getString(8);
    				userinfo.top=crsrrec.getString(9);
    				userinfo.alstrat=crsrrec.getString(10);
    				userinfo.BluFund=crsrrec.getInt(11);
    				userinfo.MCGF=crsrrec.getInt(12);
    				userinfo.DynPE=crsrrec.getInt(13);
    				userinfo.OppFund=crsrrec.getInt(14);
    				userinfo.MCBF=crsrrec.getInt(15);
    				userinfo.IFund=crsrrec.getInt(16);
    				userinfo.MMF=crsrrec.getInt(17);
    				
		        	crsrrec.close();
		        	db.close();
    				vf.setDisplayedChild(8);
    			}
    			});
    		
    	}
    	if(v==btnGoBack)
    	{
    		vf.setDisplayedChild(7);
    	}
    	if(v==btnAttachDoc)
    	{
    		attachflag=1;
    		vf.setDisplayedChild(9);
    		
    		/*
    		 Intent intentBrowse = new Intent();
    		 
	        intent.putExtra("Fname", lvfname);
	        intent.putExtra("Lname", lvlname);
	        startActivityForForResult(intentBrowse,BROWSE);
	        */
    	}
    	if(v==btnBrowse)
    	{
    		//Browse window open
    		Intent intent=new Intent(this,AndroidExplorer.class);
    		//intent.putExtra(fileName,"");
    		
    		startActivityForResult(intent,FILE_NAME);
    		/*Intent browseIntent=new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    		browseIntent.setAction("android.intent.action.VIEW");
    		browseIntent.putExtra("Fname", lvfname);
	        browseIntent.putExtra("Lname", lvlname);
	        startActivityForResult(browseIntent,BROWSE);
	        */
    		
    		/*Intent intent = new Intent();
    		intent.setAction(Intent.ACTION_PICK);
    		Uri startDir = Uri.fromFile(new File("/mnt/sdcard"));
    		
    		// Files and directories !
    		intent.setDataAndType(startDir, "vnd.android.cursor.dir/lysesoft.andexplorer.file");
    		// Title
    	//	String dirpath=startDir.getPath();
    		intent.putExtra("explorer_title", "Select a file");
    		// Optional colors
    		intent.putExtra("browser_title_background_color", "440000AA");
    		intent.putExtra("browser_title_foreground_color", "FFFFFFFF");
    		intent.putExtra("browser_list_background_color", "00000066");
    		// Optional font scale
    		intent.putExtra("browser_list_fontscale", "120%");
    		// Optional 0=simple list, 1 = list with filename and size, 2 = list with filename, size and date.
    		intent.putExtra("browser_list_layout", "2");
    		startActivityForResult(intent, 0);
    		String temp="Hellooo";
    		temp="Balkrishna";
    		*/
    		
    	}
    	/*if(v==btnGenPDF)
    	{
    		if(attachflag==0)
    		{
    			AlertDialog.Builder adb=new AlertDialog.Builder(POCBank.this);
    			adb.setTitle(new AlertBox().title);
    			adb.setMessage("No document is attached. Can't save");
    			adb.setPositiveButton("Ok", null);
    			adb.setIcon(new AlertBox().msgicon);
    			adb.show();
    			
    		}
    		else
    		{
    		db = openOrCreateDatabase("POCICICIPRULIFE.db", SQLiteDatabase.CREATE_IF_NECESSARY,null);
    		Cursor crsimg=null;
    		try{
    		crsimg=db.query("tmp", new String[]{"ImgPath","ImgTag","ImgDesc"}, null, null, null, null, null);
    		}
    		catch(Exception e)
    		{
    			
    		}
    		int tmpcount=crsimg.getCount();
    		ContentValues values = new ContentValues();
    		crsimg.moveToFirst();
    		
    		for(int i=1;i<=tmpcount;i++)
    		{
    			values.put("CustomerId",custId);
    			values.put("ImgPath",crsimg.getString(0));
    			values.put("ImgTag",crsimg.getString(1));
    			values.put("ImgDesc",crsimg.getString(2));
    			db.insert("ImageDB", null, values);
    			
    			crsimg.moveToNext();
    		}
        	crsimg.close();
        	db.delete("tmp", null, null);
        	db.close();
        	
        	generatePDFFile();
        	Toast.makeText(this, "Save successful ...", Toast.LENGTH_LONG).show();
        	
        	Intent intent = getIntent();
        	finish();
        	startActivity(intent);
    		}
    	}*/
    	if(v==btnAttachPDF)
    	{
    		String destinationImagePath="";
    		//if(fileName!="" && filePath!="")
    		{
    		try{
    			File sd = Environment.getExternalStorageDirectory();
    		    File data = Environment.getDataDirectory();
    		    if (sd.canWrite()) {
    		    	Toast.makeText(this, "Processing ...", Toast.LENGTH_LONG).show();
    		        String sourceImagePath= filePath;
    		        destinationImagePath= lvfname+"_"+lvlname+"_"+sproof.getSelectedItem().toString()+".pdf";
    		        File source= new File(filePath);//sd, sourceImagePath
    		        File dir=new File(sd+"/POC Data/Attachments/");
    	            dir.mkdir();
    		        File destination= new File(dir, destinationImagePath);
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
    		/*
//    		db.close();
            db = openOrCreateDatabase("POCICICIPRULIFE.db", SQLiteDatabase.CREATE_IF_NECESSARY,null);
            //db.execSQL("CREATE TABLE IF NOT EXISTS AttachedDocs(CustomerId integer,Path TEXT,Tag TEXT,Desc TEXT);");
    		ContentValues values = new ContentValues();
        	values.put("CustomerId",custId );
    		values.put("Path","/POC Data/Attachments/"+destinationImagePath);
    		values.put("Tag",sproof.getSelectedItem().toString());
    		values.put("Desc",txtDocDesc.getText().toString());
    		String str=sproof.getSelectedItem().toString();
    		db.insert("AttachedDocs",null,values);
        	db.close();*/
    		db = openOrCreateDatabase("POCICICIPRULIFE.db", SQLiteDatabase.CREATE_IF_NECESSARY,null);
			//db.query("AttachedDocs",new String[]{"CustomerId"},null,null,null,null,null,null);
	        
    		ContentValues values = new ContentValues();
        	
    		values.put("CustomerId", custId);
    		values.put("Path","/POC Data/Attachments/"+destinationImagePath);
        	values.put("Tag",sproof.getSelectedItem().toString() );
        	values.put("Desc",txtDocDesc.getText().toString() );
        	
        	db.insert("AttachedDocs", null, values);
    		
    		db.close();
    		//fileName=filePath="";
    		txtDocDesc.setText("");
    		tvShowDocPath.setText("");
    		}
    		//else
    			//Toast.makeText(this, "Please select PDF file...", Toast.LENGTH_LONG).show();
            
    	}
    	if(v==btnSync)
    	{
    		Toast.makeText(this, "Processing ...", Toast.LENGTH_LONG).show();
    		String imgpath,tag,desc;
    		db = openOrCreateDatabase("POCICICIPRULIFE.db", SQLiteDatabase.CREATE_IF_NECESSARY,null);
			final Cursor crsr=db.query("CustomerMasterDetails",new String[]{"FirstName","LastName","DOB","Gender","MaritalStatus","Multiplier","AnnualPremium","SumAssured","TermOfPolicy","AllocationStrategy","BluechipFund","MultiCapGrowthFund","DynamicPEFund","OpportunitiesFund","MultiCapBalancedFund","IncomeFund","MoneyMarketFund"},null,null,null,null,null,null);
			final int count=crsr.getCount();
			crsr.moveToFirst();
					
			for(int i=1;i<=count;i++)
			{
				//int pk=crsr.getInt(0);
				try{
					new Thread().sleep(1000);
				Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://192.168.0.63/POC-ICICIPRULife/sync.aspx?FName="+crsr.getString(0)+"&LName="+crsr.getString(1)+"&DOB="+crsr.getString(2)+"&Gender="+crsr.getString(3)+"&MStatus="+crsr.getString(4)+"&Mul="+crsr.getString(5)+"&APRM="+crsr.getString(6)+"&SumA="+crsr.getString(7)+"&TOP="+crsr.getString(8)+"&AlStr="+crsr.getString(9)+"&BlFund="+crsr.getInt(10)+"&MCGF="+crsr.getInt(11)+"&DynPE="+crsr.getInt(12)+"&OpFund="+crsr.getInt(13)+"&MCBF="+crsr.getInt(14)+"&IFund="+crsr.getInt(15)+"&MMF="+crsr.getInt(16)));
				startActivityForResult(browserIntent, count);
				//for image dump on server
				/*
				Cursor imgcrsr=db.query("ImageDB",new String[]{"CustomerId","ImgPath","ImgTag","ImgDesc"},"CustomerId="+pk,null,null,null,null,null);
				int imgcnt=imgcrsr.getCount();
				imgcrsr.moveToFirst();
				for(int j=1;j<=imgcnt;j++)
				{
					imgpath=imgcrsr.getString(1);
					tag=imgcrsr.getString(2);
					desc=imgcrsr.getString(3);
					imgcrsr.moveToNext();
				}
				
				crsr.moveToNext();
				*/
				}
				catch(Exception e){}
				finally{			
					try{
					new Thread().sleep(2000);
					}
					catch(Exception e){}
				}
						
			}
			crsr.close();
	        db.delete("CustomerMasterDetails", null, null);
	        db.delete("AttachedDocs", null, null);
	        db.close();
    	}
    	if(v==btnMenu)
    	{
    		Intent intent = getIntent();
        	finish();
        	startActivity(intent);
    	}
    	if(v == tvpickdob)
    			showDialog(DATE_DIALOG_ID);
    	
		/*if (v == save) 
		{
			al=new AlertDialog.Builder(this).create();
	        al.setTitle(new AlertBox().title);
		    al.setMessage("Do you want to save? ");
		    al.setButton("Yes", new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int id) {
	    			// Action for 'Yes' Button
		    		
                	}
	    			});
		    al.setButton2("No", new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int id) {
	    			// Action for 'No' Button
	    			}
	    			});
		    al.setIcon(new AlertBox().msgicon);
		    al.show();
			
		}*/
		
		/*if(v==btnRestart)
		{
			Intent intent = getIntent();
        	finish();
        	startActivity(intent);
        }*/
		if(v==btnClickMenu)
		{
			Intent intent = getIntent();
        	finish();
        	startActivity(intent);
        }
		
		if(v==btnEx)
		{
			finish();
		}
		if(v==btnDone)
		{
			Toast.makeText(POCBank.this, "Processing ...", Toast.LENGTH_LONG).show();                     
    		//Values insertion in DB
    		db = openOrCreateDatabase("POCICICIPRULIFE.db", SQLiteDatabase.CREATE_IF_NECESSARY,null);
			Cursor crsr=db.query("CustomerMasterDetails",new String[]{"CustomerId"},null,null,null,null,null,null);
	        rowcount=crsr.getCount()+1;
    		ContentValues values = new ContentValues();
        	
    		values.put("CustomerId", rowcount);
    		values.put("FirstName",userinfo.fname);
        	values.put("LastName",userinfo.lname );
        	values.put("DOB",userinfo.dob );
        	values.put("Gender",userinfo.gender );
        	values.put("MaritalStatus",userinfo.marstat );            	
        	values.put("Multiplier",userinfo.multiplier );
        	values.put("AnnualPremium",userinfo.anprem );
        	values.put("SumAssured",userinfo.sum );
        	values.put("TermOfPolicy",userinfo.top );
        	values.put("AllocationStrategy",userinfo.alstrat);
        	
        	values.put("BluechipFund",userinfo.BluFund);
    		values.put("MultiCapGrowthFund",userinfo.MCGF);
    		values.put("DynamicPEFund",userinfo.DynPE);
    		values.put("OpportunitiesFund",userinfo.OppFund);
    		values.put("MultiCapBalancedFund",userinfo.MCBF );            	
    		values.put("IncomeFund",userinfo.IFund);
    		values.put("MoneyMarketFund",userinfo.MMF);
        	
    		db.insert("CustomerMasterDetails", null, values);
    		crsr.close();
    		db.close();
    	/*	db = openOrCreateDatabase("POCICICIPRULIFE.db", SQLiteDatabase.CREATE_IF_NECESSARY,null);
    		Cursor crsimg=db.query("tmp", new String[]{"ImgPath","ImgTag","ImgDesc"}, null, null, null, null, null);
    		int tmpcount=crsimg.getCount();
    		crsimg.moveToFirst();
    		
    		for(int i=1;i<=tmpcount;i++)
    		{
    			values.put("CustomerId",rowcount);
    			values.put("ImgPath",crsimg.getString(0));
    			values.put("ImgTag",crsimg.getString(1));
    			values.put("ImgDesc",crsimg.getString(2));
    			db.insert("ImageDB", null, values);
    			crsimg.moveToNext();
    		}
        	crsimg.close();
        	db.delete("tmp", null, null);
        	db.close();
        	*/
        	//generatePDFFile();
        	/*
        	String path=Environment.getExternalStorageDirectory().toString();
            File dir=new File(path+"/DCIM/Camera");
        	dir.deleteOnExit();
        	*/
      	
        	
        	Intent intent = getIntent();
        	finish();
        	startActivity(intent);
		}
		/*if(v==btnExit)
		{
			finish();
		}*/
		/*if(v==btnShow)
		{
			if(!validIpFname())
			{
				al=new AlertDialog.Builder(this).create();
				al.setTitle(new AlertBox().title);
				al.setMessage("Please enter only alphabets in First Name! ");
				al.setButton("OK", new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int id) {
	    			// Action for 'OK' Button
	    			etfname.findFocus();
		    		}
	    			});
				al.setIcon(new AlertBox().msgicon);
				al.show();
			}
			else if(!validIpLname())
			{
				al=new AlertDialog.Builder(this).create();
				al.setTitle(new AlertBox().title);
				al.setMessage("Please enter only alphabets in Last Name! ");
				al.setButton("OK", new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int id) {
	    			// Action for 'OK' Button
	    			etlname.findFocus();
		    		}
	    			});
				al.setIcon(new AlertBox().msgicon);
				al.show();
			}
			else
			{
				//fetch record and display
				String ipfname=etIpFname.getText().toString().trim();
				String iplname=etIpLname.getText().toString().trim();
				db = openOrCreateDatabase("POCICICIPRULIFE.db", SQLiteDatabase.CREATE_IF_NECESSARY,null);
				Cursor crsr=db.query("CustomerMasterDetails",new String[]{"CustomerId","FirstName","LastName","DOB","Gender","MaritalStatus","Multiplier","AnnualPremium","SumAssured","TermOfPolicy","AllocationStrategy"},"FirstName='"+ipfname+"' AND LastName='"+iplname+"'",null,null,null,null,null);
		        if(crsr.isAfterLast() == true)
		        {
		        	al=new AlertDialog.Builder(this).create();
					al.setTitle(new AlertBox().title);
					al.setMessage("No such record..");
					al.setButton("OK", new DialogInterface.OnClickListener() {
			    	public void onClick(DialogInterface dialog, int id) {
		    			// Action for 'OK' Button
		    			etIpFname.setText("");
		    			etIpLname.setText("");
			    		}
		    			});
					al.setIcon(new AlertBox().msgicon);
					al.show();
		        }
		        else
		        {
		        	crsr.moveToFirst();
		        	
		        	tvShowOpName.setText(": "+crsr.getString(1)+" "+crsr.getString(2));
		        	tvShowOpDOB.setText(": "+crsr.getString(3));
		        	tvShowOpGender.setText(": "+crsr.getString(4));
		        	tvShowOpMarStat.setText(": "+crsr.getString(5));
		        	tvShowOpMultiplier.setText(": "+crsr.getString(6));
		        	tvShowOpAnPrem.setText(": "+crsr.getString(7));
		        	tvShowOpSum.setText(": "+crsr.getString(8));
		        	tvShowOpTOP.setText(": "+crsr.getString(9));
		        	tvShowOpAlloc.setText(": "+crsr.getString(10));
		        	/*
		        	al=new AlertDialog.Builder(this).create();
					al.setTitle(new AlertBox().title);
					al.setMessage(""+crsr.getInt(0));
					al.setButton("OK", new DialogInterface.OnClickListener() {
			    	public void onClick(DialogInterface dialog, int id) {
		    			// Action for 'OK' Button
		    			
			    		}
		    			});
					al.setIcon(new AlertBox().msgicon);
					al.show();
		        	
		        	
		        }
		        crsr.close();
		        db.close();
			}
		}
		*/
		/*if(v==btnCapture)
		{
			
			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
	        
		}*/
		/*
		if(v==btnRotate)
		{
			if(angle==360)
				angle=90;
			else
				angle+=90;
			
			// Getting width & height of the given image.
			int w = thumbnail.getWidth();
			int h = thumbnail.getHeight();
			// Setting post rotate to 90
			Matrix mtx = new Matrix();
			mtx.postRotate(angle);
			// Rotating Bitmap
			Bitmap rotatedBMP = Bitmap.createBitmap(thumbnail, 0, 0, w, h, mtx, true);
			BitmapDrawable bmd = new BitmapDrawable(rotatedBMP);

			image.setImageDrawable(bmd);
		}
		*/		
		if(v==next)
		{
			int pos=smultiplier.getSelectedItemPosition();
     	   	myNum = Double.parseDouble(etanprem.getText().toString().trim());
			   myno=Double.parseDouble(Multiplier_Spinner[pos]);
		       etsum.setText(""+(long)(myNum*myno)/100);
			if(vf.getDisplayedChild()==6)
			{
				al=new AlertDialog.Builder(this).create();
				al.setTitle(new AlertBox().title);
				al.setMessage("End of procedure. Please select any one action from above.");
				al.setButton("OK", new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int id) {
	    			// Action for 'OK' Button
	    			
		    		}
	    			});
				al.setIcon(new AlertBox().msgicon);
				al.show();
			}
			
			else
			{
				if(!validatefname())
				{
					al=new AlertDialog.Builder(this).create();
					al.setTitle(new AlertBox().title);
					al.setMessage("Please enter only alphabets in First Name! ");
					al.setButton("OK", new DialogInterface.OnClickListener() {
			    	public void onClick(DialogInterface dialog, int id) {
		    			// Action for 'OK' Button
		    			etfname.findFocus();
			    		}
		    			});
					al.setIcon(new AlertBox().msgicon);
					al.show();
				}
				else if(!validatelname())
				{
					al=new AlertDialog.Builder(this).create();
					al.setTitle(new AlertBox().title);
					al.setMessage("Please enter only alphabets in Last Name! ");
					al.setButton("OK", new DialogInterface.OnClickListener() {
			    	public void onClick(DialogInterface dialog, int id) {
		    			// Action for 'OK' Button
		    			etlname.findFocus();
			    		}
		    			});
					al.setIcon(new AlertBox().msgicon);
					al.show();
				}
				else if(!validatedob())
				{
					al=new AlertDialog.Builder(this).create();
					al.setTitle(new AlertBox().title);
					al.setMessage("Please select date of birth! ");
					al.setButton("OK", new DialogInterface.OnClickListener() {
			    	public void onClick(DialogInterface dialog, int id) {
		    			// Action for 'OK' Button
		    			
			    		}
		    			});
					al.setIcon(new AlertBox().msgicon);
					al.show();
				}
				else if(!validateanprem())
				{
					al=new AlertDialog.Builder(this).create();
					al.setTitle(new AlertBox().title);
					al.setMessage("Please enter Annual Premium amount >=40000! ");
					al.setButton("OK", new DialogInterface.OnClickListener() {
			    	public void onClick(DialogInterface dialog, int id) {
		    			// Action for 'OK' Button
		    			
			    		}
		    			});
					al.setIcon(new AlertBox().msgicon);
					al.show();
				}
				else if(vf.getDisplayedChild()==5)
				{
					userinfo.CurAlloc=0;
					userinfo.BluFund=Integer.parseInt(etBluFund.getText().toString().trim());
					userinfo.MCGF=Integer.parseInt(etMCGF.getText().toString().trim());
					userinfo.DynPE=Integer.parseInt(etDynPE.getText().toString().trim());
					userinfo.OppFund=Integer.parseInt(etOppFund.getText().toString().trim());
					userinfo.MCBF=Integer.parseInt(etMCBF.getText().toString().trim());
					userinfo.IFund=Integer.parseInt(etIFund.getText().toString().trim());
					userinfo.MMF=Integer.parseInt(etMMF.getText().toString());
					userinfo.CurAlloc=userinfo.BluFund+userinfo.MCGF+userinfo.DynPE+userinfo.OppFund+userinfo.MCBF+userinfo.IFund+userinfo.MMF;
					if(userinfo.CurAlloc!=100)
					{
						al=new AlertDialog.Builder(this).create();
						al.setTitle(new AlertBox().title);
						al.setMessage("Your fund allocation is adding upto "+userinfo.CurAlloc+"% instead of 100%. Please re-enter values.");
						al.setButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Action for 'OK' Button
		    			
							}
		    			});
						al.setIcon(new AlertBox().msgicon);
						al.show();
					}
					else if(vf.getDisplayedChild()==6)
					{
						
						previous.setVisibility(View.INVISIBLE);
			    		next.setVisibility(View.INVISIBLE);
			    		tvStar.setVisibility(View.INVISIBLE);
					}
					else
						vf.showNext();
					
				}
				
				/*else if(sfund.getSelectedItem().toString()=="Trigger based Portfolio" && vf.getDisplayedChild()==6)
				{
					vf.setDisplayedChild(7);
				}*/
				else
				{
					if(vf.getDisplayedChild()==3 && sfund.getSelectedItem().toString()=="Trigger based Portfolio")
					{
						vf.setDisplayedChild(6);
					}
					else
					{
					// Storing into class variables
					userinfo.fname=etfname.getText().toString().trim();
					userinfo.lname=etlname.getText().toString().trim();
					userinfo.dob=tvpickdob.getText().toString();
					rb1=(RadioButton)findViewById(rdogender.getCheckedRadioButtonId());
					userinfo.gender=rb1.getText().toString();
					userinfo.marstat=smarital.getSelectedItem().toString();
					userinfo.multiplier=smultiplier.getSelectedItem().toString();
					userinfo.anprem=etanprem.getText().toString().trim();
					userinfo.sum=etsum.getText().toString().trim();
					userinfo.top=stop.getSelectedItem().toString();
					userinfo.alstrat=sfund.getSelectedItem().toString().trim();
					
					//Overview text set
					tvname.setText(": "+userinfo.fname+" "+userinfo.lname);
					tvgender.setText(": "+userinfo.gender);
					tvdob.setText(": "+userinfo.dob);
					tvmarstat.setText(": "+userinfo.marstat);
					tvmultiplier.setText(": "+userinfo.multiplier);
					tvanprem.setText(": "+userinfo.anprem);
					tvsum.setText(": "+userinfo.sum);
					tvtop.setText(": "+userinfo.top);
					tvfund.setText(": "+userinfo.alstrat);
					tvShowBluFund.setText(": "+userinfo.BluFund);
					tvShowMCGF.setText(": "+userinfo.MCGF);
					tvShowDynPE.setText(": "+userinfo.DynPE);
					tvShowOppFund.setText(": "+userinfo.OppFund);
					tvShowMCBF.setText(": "+userinfo.MCBF);
					tvShowIFund.setText(": "+userinfo.IFund);
					tvShowMMF.setText(": "+userinfo.MMF);
					
					//imgOverview.setImageBitmap(userinfo.PhotoID);
					vf.showNext();
					}
				}//else
			}//else
		}//if
		if (v == previous) 
		{
			
			if(vf.getDisplayedChild()==1){
				al=new AlertDialog.Builder(this).create();
				al.setTitle(new AlertBox().title);
				al.setMessage("Do you want to go to Main Menu?");
				al.setButton("Yes", new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int id) {
	    			// Action for 'Yes' Button
	    			vf.showPrevious();
	    			previous.setVisibility(View.INVISIBLE);
	        		next.setVisibility(View.INVISIBLE);
	        		tvStar.setVisibility(View.VISIBLE);
		    		}
	    			});
				al.setButton2("No", new DialogInterface.OnClickListener() {
			    	public void onClick(DialogInterface dialog, int id) {
		    			// Action for 'No' Button
		    			}
		    			});
				al.setIcon(new AlertBox().msgicon);
				al.show();
			}
			/*else if(vf.getDisplayedChild()==7 && sfund.getSelectedItem().toString()=="Trigger based Portfolio"){
				next.setVisibility(View.VISIBLE);
				tvStar.setVisibility(View.VISIBLE);
				vf.setDisplayedChild(6);
			}*/
			else
			{
				if(sfund.getSelectedItem().toString()=="Trigger based Portfolio")
				{
					if(vf.getDisplayedChild()==6)
						vf.setDisplayedChild(3);
					
					else
						vf.showPrevious();
				}//if
				else
					vf.showPrevious();
			}//else
				
		}//if
	}
   
       
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       /* if (requestCode == CAMERA_PIC_REQUEST) {  
            // do something  
        	
        	thumbnail = userinfo.PhotoID=(Bitmap) data.getExtras().get("data");
        	String path=Environment.getExternalStorageDirectory().toString();
            
            File dir=new File(path+"/POC Data");
            
            dir.mkdir();
            
            try{
    		OutputStream gpxfile= new FileOutputStream(new File(dir,userinfo.fname+"_"+userinfo.lname+".jpg"));
    		userinfo.PhotoID.compress(Bitmap.CompressFormat.JPEG, 90, gpxfile);
            }catch(Exception e){}
            
            image = (ImageView) findViewById(R.id.photoResultView);  
        	image.setImageBitmap(thumbnail); 
        	
        	Intent intent = new Intent(this, MediaStoreTest.class);
	        intent.putExtra("Fname", userinfo.fname);
	        intent.putExtra("Lname", userinfo.lname);
	        startActivity(intent);
        	
        }*/
        //Browse for document
        if(requestCode==FILE_NAME)
        {
        	
        		db=openOrCreateDatabase("POCICICIPRULIFE.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        		Cursor crsrfname=db.query("FileName",new String[]{"filepath","filename"},null,null,null,null,null,null);
    			crsrfname.moveToFirst();
    			filePath=crsrfname.getString(0);
    			fileName=crsrfname.getString(1);
    			tvShowDocPath.setText(fileName);
    			db.delete("FileName",null,null);
    			crsrfname.close();
    			db.close();
        	
        }
        if (requestCode == 0)
        {
        if (resultCode == RESULT_OK)
        {
           Uri uri = data.getData();
           String type = data.getType();
           Log.i("Tag","Pick completed: "+ uri + " "+type);
           if (uri != null)
           {
              String path = uri.toString();
              if (path.toLowerCase().startsWith("file://"))
              {
                 // Selected file/directory path is below
                 path = (new File(URI.create(path))).getAbsolutePath();
              }

           }
        }
        else Log.i("Tag","Back from pick with cancel status");
        }
    }
    
    

    public boolean validatefname(){
    		if(etfname.getText().toString().trim().matches("^[a-zA-Z ]+$"))
    			return true;
    		else
    			return false;
    }
    
    public boolean validatelname(){
    	if(etlname.getText().toString().trim().matches("^[a-zA-Z ]+$"))
			return true;
		else
			return false;
	
    }
    
    public boolean validatedob(){
    	
    	if(tvpickdob.getText().toString().matches("\\.*[[1-31][/]{1}[1-12][/]{1}[1-9]{4}].*"))
			return true;
		else
			return false;
	
    }
    
    public boolean validateanprem(){
    	if(Double.parseDouble(etanprem.getText().toString().trim())>=40000)
    		return true;
    	else
    		return false;
    }
    public boolean validIpFname(){
		if(etIpFname.getText().toString().trim().matches("^[a-zA-Z ]+$"))
			return true;
		else
			return false;
    }
    public boolean validIpLname(){
		if(etIpLname.getText().toString().trim().matches("^[a-zA-Z ]+$"))
			return true;
		else
			return false;
    }
    

    private void generatePDFFile()
    {
	
    /*
    import com.itextpdf.text.Section;
    import com.itextpdf.text.pdf.PdfPCell;
    import com.itextpdf.text.pdf.PdfPTable;
    import com.itextpdf.text.pdf.PdfWriter;
    */

        	try
            {
    	//updated by Balkrishna....

    	     String path=Environment.getExternalStorageDirectory().toString();
                
                File dir=new File(path+"/POC Data");
                dir.mkdir();
    		Paragraph preface = new Paragraph();

        		//OutputStream gpxfile= new FileOutputStream(new File(dir,userinfo.fname+"_"+userinfo.lname+".pdf"));
        		//userinfo.PhotoID.compress(Bitmap.CompressFormat.JPEG, 90, gpxfile);
        		Document document= new Document();
        		
        		PdfWriter.getInstance(document, new FileOutputStream(new File(dir,userinfo.fname+"_"+userinfo.lname+".pdf")));
            	document.open();

    		 Font catFont = new Font(Font.FontFamily.COURIER, 18, Font.BOLD);
    		 Font redFont = new Font(Font.FontFamily.COURIER, 12,	Font.NORMAL, BaseColor.RED);
    		 Font subFont = new Font(Font.FontFamily.COURIER, 16, Font.BOLD);
    		 Font smallBold = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);

    	 	
                

                document.add(new Paragraph("ICICI Prudential Life Insurance User Information", catFont));
    		addEmptyLine(preface, 1);				//empty line
    		document.add(new Paragraph("Personal Information",subFont));	//Title in red

                document.add(new Paragraph("Name : "+userinfo.fname+" "+userinfo.lname,subFont));
                document.add(new Paragraph("Date of Birth : "+userinfo.dob,subFont));
                document.add(new Paragraph("Gender : "+userinfo.gender,subFont));
                document.add(new Paragraph("Marital status : "+userinfo.marstat,subFont));
                document.add(new Paragraph("Multiplier : "+userinfo.multiplier,subFont));
                document.add(new Paragraph("Annual Premium : "+userinfo.anprem,subFont));
                document.add(new Paragraph("Sum Assured : "+userinfo.sum,subFont));
                document.add(new Paragraph("Term of Policy : "+userinfo.top,subFont));
    		
    		addEmptyLine(preface, 1);
    		document.add(new Paragraph("Allocate Your Investments",catFont));
    		addEmptyLine(preface, 1);

                document.add(new Paragraph("Allocation Strategy : "+userinfo.alstrat,subFont));
    			
    			//By balkrishna...
    			addEmptyLine(preface, 1);
    			//add table...
    			/*PdfPTable table = new PdfPTable(2);
    			PdfPCell c1 = new PdfPCell(new Phrase("Fund Allocation"));
    			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
    			table.addCell(c1);

    			c1 = new PdfPCell(new Phrase("Value in %"));
    			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
    			table.addCell(c1);
    			
    			table.setHeaderRows(1);
    			
    			table.addCell("Bluechip Fund");
    			table.addCell(+userinfo.BluFund);
    			table.addCell("Multi Cap Growth Fund");
    			table.addCell(+userinfo.MCGF);
    			table.addCell("Dynamic PEFund");
    			table.addCell(+userinfo.DynPE);
    			table.addCell(Opportunities Fund);
    			table.addCell(+userinfo.OppFund);
    			table.addCell("Multi Cap Balanced Fund");
    			table.addCell(+userinfo.MCBF);
    			table.addCell("Income Fund");
    			table.addCell(+userinfo.IFund);
    			table.addCell("Money Market Fund");
    			table.addCell(+userinfo.MMF);

    			subCatPart.add(table);*/

    			//Date dt=new Date();
    			
        	    document.add(new Paragraph("Bluechip Fund : "+userinfo.BluFund,subFont));
    			document.add(new Paragraph("Multi Cap Growth Fund : "+userinfo.MCGF,subFont));
    			document.add(new Paragraph("Dynamic PEFund : "+userinfo.DynPE,subFont));
    			document.add(new Paragraph("Opportunities Fund : "+userinfo.OppFund,subFont));
    			document.add(new Paragraph("Multi Cap Balanced Fund : "+userinfo.MCBF,subFont ));
    			document.add(new Paragraph("Income Fund : "+userinfo.IFund,subFont));
    			document.add(new Paragraph("Money Market Fund : "+userinfo.MMF,subFont));
    			

    			document.add(new Paragraph(new Date().toString()));
    			addEmptyLine(preface, 5);
    			document.newPage();


    	//File root = Environment.getExternalStorageDirectory();
           
            	//comment by balkrishna....
    		//need a for loop to add images
                //PdfWriter.getInstance(document, gpxfile);
                //document.open();
                /*
                 	Image img=Image.getInstance(path+"/POC Data/"+userinfo.fname+"_"+userinfo.lname+"_"+"ID Proof.jpg");
                	
                 	//if(img.isJpeg())
                	//{
                		document.add(new Paragraph("ID Proof",subFont));
                		document.add(new Paragraph(" "));
                		document.add(new Paragraph(" "));
                		document.add(new Paragraph(" "));
                		document.add(img);
                		document.newPage();
                	//}
                	img=Image.getInstance(path+"/POC Data/"+userinfo.fname+"_"+userinfo.lname+"_"+"Address Proof.jpg");
                	//if(img2!=null)
                	//{
                		document.add(new Paragraph("Address Proof",subFont));
                		document.add(new Paragraph(" "));
                		document.add(new Paragraph(" "));
                		document.add(new Paragraph(" "));
                		document.add(img);
                		document.newPage();
                	//}
                	img=Image.getInstance(path+"/POC Data/"+userinfo.fname+"_"+userinfo.lname+"_"+"Bank Statement.jpg");
                	//if(img3!=null)
                	//{
                		document.add(new Paragraph("Bank Statement",subFont));
                		document.add(new Paragraph(" "));
                		document.add(new Paragraph(" "));
                		document.add(new Paragraph(" "));
                		document.add(img);
                		document.newPage();
                	//}
                	 img=Image.getInstance(path+"/POC Data/"+userinfo.fname+"_"+userinfo.lname+"_"+"Electricity Bill.jpg");
                	//if(img4!=null)
                	//{
                		document.add(new Paragraph("Electricity Bill",subFont));
                		document.add(new Paragraph(" "));
                		document.add(new Paragraph(" "));
                		document.add(new Paragraph(" "));
                		document.add(img);
                		document.newPage();
                	//}
					*/
    		document.add(new Paragraph(new Date().toString()));
    		
    		document.add(new Paragraph(" "));
    		document.add(new Paragraph(" "));
    		document.add(new Paragraph(" "));
    		document.add(new Paragraph(" "));
    		document.add(new Paragraph(" "));
    		document.add(new Paragraph(" "));

    		document.add(new Paragraph("Customer Signature", catFont));
    		addEmptyLine(preface, 3);
    		document.add(new Paragraph(new Date().toString()));


                document.close();
                //gpxfile.close();
                }
            	catch(Exception e)
                {
                   e.printStackTrace();
                }                
        }
    
    public void update(final String fileToUpdate) {
    	
    }


    private static void addEmptyLine(Paragraph paragraph, int number) {
    		for (int i = 0; i < number; i++) {
    			paragraph.add(new Paragraph(" "));
    		}}

}
