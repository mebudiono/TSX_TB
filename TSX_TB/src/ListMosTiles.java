import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListMosTiles {
	public String workDir,cornersTxt,pole,year,date,dateFile,satName,latName,lonName,tileName,targetDir;
	public String inputDirName;//=workDir+"/input";//="/media/public80/SAR_smosaik_prov/2019/sulbar/input_EEC_cal_sample/";
	public String outputDirName;//=workDir+"/output";
	public String demPath;//=workDir+"/DEMIND/indonesia_SRTM30_rev1.tif";
	public String geoDir="/usr/local/GAMMA_SOFTWARE-20181130/GEO/bin/";
	public String scriptDir="/usr/local/scripts/";
	public String dispDir="/usr/local/GAMMA_SOFTWARE-20181130/DISP/bin/";
	public String parTemplate,pathFolder;//=workDir+"/diff_par_template";
	//public  String outputDir="/media/Data/TSX/OUTPUT/";
	public  int foundDIrInput;
	protected String outputMakeDir,mlirs,fileOutputName,xmlFile,cosFile,namePath,provName,idProv,codeProv;
	public double parSRes=6,minX,minY,maxX,maxY,minXR,minYR,maxXR,maxYR,ulX,ulY,lrX,lrY,ulXBox,ulYBox,lrXBox,lrYBox;

	public  ArrayList<String> listDir,listXML ;
	public ArrayList<String> listCommand;
	public ArrayList<String> dapetXML;
	public ArrayList<String> dapetCOS;
	public Integer dem_width,mli_width;
	public FileWriter writeDiffOut,wCom,kmlW,aKmlW;
	private static Connection connect = null;
	private static Statement statement = null;
	private static Statement statement2 = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private static runGamma2 procGamma;
	//public static ArrayList<String> listCommand;
	//public static void listSSC(){
	public static FileWriter writeLogDb;


	public void getinfoProvinceID(String aoi){
		File aoiTxt=new File(aoi);
		StringBuffer provinceTemp=new StringBuffer();
		System.out.println("aoiTxt.getName()="+aoiTxt.getName());
		char[] provinceChar=aoiTxt.getName().toCharArray();
		for(int i=9;i<provinceChar.length-4;i++) {
			provinceTemp.append(provinceChar[i]);	
		}
		provName=provinceTemp.toString();

		StringBuffer idTemp=new StringBuffer();
		char[] idChar=aoiTxt.getName().toCharArray();
		for(int i=0;i<2;i++) {
			idTemp.append(idChar[i]);	
		}
		idProv=idTemp.toString();

		StringBuffer codeTemp=new StringBuffer();
		char[] codeChar=aoiTxt.getName().toCharArray();
		for(int i=3;i<8;i++) {
			codeTemp.append(idChar[i]);	
		}
		codeProv=codeTemp.toString();

		System.out.println("province="+provName);
		System.out.println("id="+idProv);
		System.out.println("code="+codeProv);
	}
	public void writeDB(String aoi) throws IOException {
		getinfoProvinceID(aoi);
		ArrayList<String> tilesAr=makeTileFolder(getInsideCorners(getCorners(aoi)));
		ArrayList<String> subTilesAr=makeTile(getCorners(aoi),getInsideCorners(getCorners(aoi)));

		StringBuffer subTilesBuf=new StringBuffer();
		StringBuffer tilesBuf=new StringBuffer();

		for(int i=0;i<subTilesAr.size();i++) {
			subTilesBuf.append(subTilesAr.get(i).toString());
			subTilesBuf.append(",");
		}
		String subTilesDB=subTilesBuf.toString();

		for(int i=0;i<tilesAr.size();i++) {
			tilesBuf.append(tilesAr.get(i).toString());
			tilesBuf.append(",");
		}
		String tilesDB=tilesBuf.toString();

		try {

			String url       = "jdbc:mysql://localhost:3306/mosTB";
			String user      = "mosTB";
			String password  = "password";
			//
			//
			String insert="INSERT INTO dbMosaic (number,id,provCode,provName,tiles,subTiles) "
					+ "VALUES (NULL,\""+idProv+"\",\""+codeProv+"\",\""+provName+"\",\""+tilesDB+"\",\""+subTilesDB+"\");";
			System.out.println("insert="+insert);
			//			writeLogDb.write(insert);
			//			writeLogDb.write("\n");
			//							String insertF="INSERT INTO dbase (id,folder) "
			//									+ "VALUES (NULL,Folder1);";
			//System.out.println(insertF);
			// create a connection to the database
			connect = DriverManager.getConnection(url, user, password);
			statement = connect.createStatement();
			statement.executeUpdate(insert);
			//			
			//
			System.out.println("Write start process to DB");
			// more processing here
			// ... 
		} catch(SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try{
				if(connect !=null) {
					connect.close();
				}
			}
			catch(SQLException ex){
				System.out.println(ex.getMessage());
			}
		}
	}
	//	

	public   String getParam(String parPath,String regex,int column){
		BufferedReader reader;
		String found;
		String[] cut=new String[10];
		try {
			reader = new BufferedReader(new FileReader(parPath));
			String line = reader.readLine();
			while (line != null) {

				//String text2=isiDir[1].toString();
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(line);   // get a matcher object
				//System.out.println("isidir[foundDIrInput]= "+isiDir[foundDIrInput].toString());

				//		int count=0;
				if (m.find()){

					cut=line.split("=");

				}
				//else {
				line = reader.readLine();
				//}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("cut="+Arrays.toString(cut));
		//System.out.println("cut[1]="+cut[1]);
		return cut[column];
	}


	public double[] getCorners(String pathCornersTxt) throws IOException{

		//get ulX from minlong corners,split by "."

		minX=Double.valueOf(getParam(pathCornersTxt,"min X=*",1));
		System.out.println("minX="+minX);
		if(minX>=0){
			//ulX=Math.round(minX * 10) / 10.0;
			String[] ulXStrAr=String.valueOf(minX).split("\\.");
			//split the right part after "."
			char[] ulXStrArRight=ulXStrAr[1].toCharArray();
			//combine left part before "." and 1 digit from right part to make ulX
			//int rightComma=Character.getNumericValue(ulXStrArRight[0])+1;
			ulX=Double.valueOf(ulXStrAr[0].toString()+"."+String.valueOf(ulXStrArRight[0]));
		}
		else{
			//get ulY from maxlat corners,split by "."
			String[] ulXStrAr=getParam(pathCornersTxt,"min X=*",1).split("\\.");
			//minX=Double.valueOf(getParam(pathCornersTxt,"min.*longitude*:*",3));
			//split the right part after "."
			char[] ulXStrArRight=ulXStrAr[1].toCharArray();
			//combine left part before "." and 1 digit from right part to make ulY
			ulX=Double.valueOf(ulXStrAr[0].toString()+"."+ulXStrArRight[0]);

		}

		maxY=Double.valueOf(getParam(pathCornersTxt,"max Y=*",1));
		System.out.println("maxY="+maxY);
		if(maxY>=0){
			//ulY=Math.round(maxY * 10) / 10.0;
			String[] ulYStrAr=String.valueOf(maxY).split("\\.");
			//split the right part after "."
			char[] ulYStrArRight=ulYStrAr[1].toCharArray();
			int rightComma=Character.getNumericValue(ulYStrArRight[0]);

			//combine left part before "." and 1 digit from right part to make ulX
			ulY=Double.valueOf(ulYStrAr[0].toString()+"."+String.valueOf(rightComma));
			//System.out.println("ulY before="+ulY);
			ulY=Math.round((ulY+0.1)*100.0);
			//System.out.println("ulY mid="+ulY);
			ulY=ulY/100.0;
			//System.out.println("ulY after="+ulY);
		}
		else{
			//get ulY from maxlat corners,split by "."
			String[] ulYStrAr=getParam(pathCornersTxt,"max Y=*",1).split("\\.");
			maxY=Double.valueOf(getParam(pathCornersTxt,"max Y=*",1));
			//split the right part after "."
			char[] ulYStrArRight=ulYStrAr[1].toCharArray();
			//combine left part before "." and 1 digit from right part to make ulY
			ulY=Double.valueOf(ulYStrAr[0].toString()+"."+ulYStrArRight[0]);

		}





		maxX=Double.valueOf(getParam(pathCornersTxt,"max X=*",1));
		System.out.println("maxX="+maxX);
		if(maxX>=0){
			//lrX=Math.round(maxX * 10) / 10.0;
			String[] lrXStrAr=String.valueOf(maxX).split("\\.");
			//split the right part after "."
			char[] lrXStrArRight=lrXStrAr[1].toCharArray();
			int rightComma=Character.getNumericValue(lrXStrArRight[0]);
			//combine left part before "." and 1 digit from right part to make ulX
			lrX=Double.valueOf(lrXStrAr[0].toString()+"."+String.valueOf(rightComma));
			//System.out.println("lrX before="+lrX);
			lrX=Math.round((lrX+0.1)*100.0);
			//System.out.println("lrX mid="+lrX);
			lrX=lrX/100.0;
			//System.out.println("lrX after="+lrX);
		}
		else{
			//get lrX from maxlong corners,split by "."
			String[] lrXStrAr=getParam(pathCornersTxt,"max X=*",1).split("\\.");
			maxX=Double.valueOf(getParam(pathCornersTxt,"max X=*",1));
			//split the right part after "."
			char[] lrXStrArRight=lrXStrAr[1].toCharArray();
			//combine left part before "." and 1 digit from right part to make lrX
			lrX=Double.valueOf(lrXStrAr[0].toString()+"."+lrXStrArRight[0]);

		}



		minY=Double.valueOf(getParam(pathCornersTxt,"min Y=*",1));
		System.out.println("minY="+minY);
		if(minY>=0){
			//lrY=Math.round(minY * 10) / 10.0;
			String[] lrYStrAr=String.valueOf(minY).split("\\.");
			//split the right part after "."
			char[] lrYStrArRight=lrYStrAr[1].toCharArray();
			int rightComma=Character.getNumericValue(lrYStrArRight[0]);
			//combine left part before "." and 1 digit from right part to make ulX
			lrY=Double.valueOf(lrYStrAr[0].toString()+"."+String.valueOf(rightComma));

		}
		else{
			//lrY=Math.round(minY * 10) / 10.0;
			String[] lrYStrAr=String.valueOf(minY).split("\\.");
			//split the right part after "."
			char[] lrYStrArRight=lrYStrAr[1].toCharArray();
			int rightComma=Character.getNumericValue(lrYStrArRight[0]);
			//combine left part before "." and 1 digit from right part to make ulX
			lrY=Double.valueOf(lrYStrAr[0].toString()+"."+String.valueOf(rightComma));
			//	System.out.println("lrY before="+lrY);
			lrY=Math.round((lrY-0.1)*100.0);
			//System.out.println("lrY mid="+lrY);
			lrY=lrY/100.0;
			//System.out.println("lrY after="+lrY);

		}




		double[] result={ulX,ulY,lrX,lrY};
		//		wCom.write("ulX="+ulX+","+"ulY="+ulY+","+"lrX="+lrX+","+"lrY="+lrY);
		//		wCom.write("\n");
		//result={minX,minY,maxX,maxY};
		//result
		return result;
	}

	public double[] getInsideCorners(double[] corners) throws IOException{
		//Corners= {ulX,ulY,lrX,lrY};

		//ulX=Math.round(minX * 10) / 10.0;
		String[] ulXBoxStrAr=String.valueOf(corners[0]).split("\\.");
		//combine left part before "." and 1 digit from right part to make ulX
		int ulXBoxInt=Integer.valueOf(ulXBoxStrAr[0])+1;
		ulXBox=Double.valueOf(ulXBoxInt);
		System.out.println("ulXBox="+ulXBox);

		//ulX=Math.round(minX * 10) / 10.0;
		String[] ulYBoxStrAr=String.valueOf(corners[1]).split("\\.");
		//combine left part before "." and 1 digit from right part to make ulX
		int ulYBoxInt=Integer.valueOf(ulYBoxStrAr[0]);
		ulYBox=Double.valueOf(ulYBoxInt);
		System.out.println("ulYBox="+ulYBox);


		String[] lrXBoxStrAr=String.valueOf(corners[2]).split("\\.");
		//combine left part before "." and 1 digit from right part to make ulX
		int lrXBoxInt=Integer.valueOf(lrXBoxStrAr[0]);
		lrXBox=Double.valueOf(lrXBoxInt);
		System.out.println("lrXBox="+lrXBox);

		//ulX=Math.round(minX * 10) / 10.0;
		String[] lrYBoxStrAr=String.valueOf(corners[3]).split("\\.");
		//combine left part before "." and 1 digit from right part to make ulX
		int lrYBoxInt=Integer.valueOf(lrYBoxStrAr[0])+1;
		lrYBox=Double.valueOf(lrYBoxInt);
		System.out.println("lrYBox="+lrYBox);

		double[] resultBox={ulXBox,ulYBox,lrXBox,lrYBox};
		//		wCom.write("ulX="+ulX+","+"ulY="+ulY+","+"lrX="+lrX+","+"lrY="+lrY);
		//		wCom.write("\n");
		//result={minX,minY,maxX,maxY};
		//result
		return resultBox;
	}

	public String createName(double X,double Y) throws IOException{
		String fileName=null;
		String[] findPoleStrAr=String.valueOf(Y).split("\\.");//2.1
		char[] findPoleChAr=String.valueOf(findPoleStrAr[0]).toCharArray();//2
		//find Pole based on ulY (highest north point)
		if (Y>0) {
			//			wCom.write("This is Y>0");
			//			wCom.write("\n");
			pole="N";
			int ILatName=Character.getNumericValue(findPoleChAr[0]);//2
			if(Character.getNumericValue(findPoleStrAr[1].toCharArray()[0])>0){
				ILatName=ILatName+1;//3
			}

			//			wCom.write("ILatName+1="+ILatName);
			//			wCom.write("\n");
			latName="0"+String.valueOf(ILatName);//03
			String[] findlonStrAr=String.valueOf(X).split("\\.");//120.1
			char[] findlonNameChAr=String.valueOf(findlonStrAr[0]).toCharArray();//take left value before "." ,make char array
			if(findlonNameChAr.length==3){
				lonName=String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1])+String.valueOf(findlonNameChAr[2]);
			}
			else{
				lonName="0"+String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1]);
			}
			//			wCom.write("lonName="+lonName);
			//			wCom.write("\n");
			//findTileName, need value after point from minX and maxY 
			char[] getRightMinX=findlonStrAr[1].toCharArray();//1
			String rightMinX=String.valueOf(getRightMinX[0]);//right value after "." of minX
			char[] getRightMaxY=findPoleStrAr[1].toCharArray();
			int IRightMaxY=10-Character.getNumericValue(getRightMaxY[0]);
			if(IRightMaxY==10){
				IRightMaxY=0;
			}
			String rightMaxY=String.valueOf(IRightMaxY);//right value after "." of maxY
			tileName=rightMinX+rightMaxY;
			//			wCom.write("tileName="+tileName);
			//			wCom.write("\n");

		}
		else if(Y>-1&&Y<=0) {
			pole="N";
			//int ILatName=Integer.valueOf(findPoleChAr[1]);
			latName="00";//+String.valueOf(ILatName);
			String[] findlonStrAr=String.valueOf(X).split("\\.");//120.1
			char[] findlonNameChAr=String.valueOf(findlonStrAr[0]).toCharArray();//take left value after "." ,make char array
			if(findlonNameChAr.length==3){
				lonName=String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1])+String.valueOf(findlonNameChAr[2]);
			}
			else{
				lonName="0"+String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1]);
			}


			//findTileName, need value after point from minX and maxY 
			char[] getRightMinX=findlonStrAr[1].toCharArray();
			String rightMinX=String.valueOf(getRightMinX[0]);//right value after "." of minX
			char[] getRightMaxY=findPoleStrAr[1].toCharArray();
			String rightMaxY=String.valueOf(getRightMaxY[0]);//right value after "." of maxY
			tileName=rightMinX+rightMaxY;
		}
		else {
			pole="S";
			//			if(String.valueOf(findPoleChAr[0]).equals("-")){//-
			//				pole="S";
			//			}
			//			else{//3
			//				pole="N";
			//			}
			if(findPoleChAr.length==2){//-9 -1
				latName="0"+String.valueOf(findPoleChAr[1]);
			}
			else {//-10 -12
				latName=String.valueOf(findPoleChAr[1])+String.valueOf(findPoleChAr[2]);
			}
			//			else{
			//				latName="0"+String.valueOf(findPoleChAr[0]);
			//			}
			//find lonName based on minX (lowest west point)
			String[] findlonStrAr=String.valueOf(X).split("\\.");//120.1
			char[] findlonNameChAr=String.valueOf(findlonStrAr[0]).toCharArray();//take left value after "." ,make char array
			if(findlonNameChAr.length==3){
				lonName=String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1])+String.valueOf(findlonNameChAr[2]);
			}
			else{
				lonName="0"+String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1]);
			}

			//findTileName, need value after point from minX and maxY 
			char[] getRightMinX=findlonStrAr[1].toCharArray();
			String rightMinX=String.valueOf(getRightMinX[0]);//right value after "." of minX
			char[] getRightMaxY=findPoleStrAr[1].toCharArray();
			String rightMaxY=String.valueOf(getRightMaxY[0]);//right value after "." of maxY
			tileName=rightMinX+rightMaxY;

		}


		fileName=pole+latName+"E"+lonName+"_"+tileName;
		//		wCom.write("fileName="+pole+latName+"E"+lonName+"_"+tileName+"_"+satName+"_SSC_"+date+".tif");
		//		wCom.write("\n");
		System.out.println(fileName);
		//}
		return fileName;

	}



	public String createNameFolder(double X,double Y) throws IOException{
		String fileName="";
		String[] findPoleStrAr=String.valueOf(Y).split("\\.");//2.1
		char[] findPoleChAr=String.valueOf(findPoleStrAr).toCharArray();//2
		//find Pole based on ulY (highest north point)
		if (Y>0) {
			//			wCom.write("This is Y>0");
			//			wCom.write("\n");
			pole="N";
			//double w=Y;
			int ILatName=Integer.valueOf(findPoleStrAr[0]);
			ILatName=ILatName+1;
			//			if(Character.getNumericValue(findPoleStrAr[1].toCharArray()[0])>0){
			//				ILatName=ILatName+1;//3
			//			}

			//			wCom.write("ILatName+1="+ILatName);
			//			wCom.write("\n");
			latName="0"+String.valueOf(ILatName);//03
			String findlonStrAr=String.valueOf(X);//120.1
			char[] findlonNameChAr=String.valueOf(findlonStrAr).toCharArray();//take left value before "." ,make char array
			if(findlonNameChAr.length==3){
				lonName=String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1])+String.valueOf(findlonNameChAr[2]);
			}
			else{
				lonName="0"+String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1]);
			}
			//			

		}
		else if(Y>-1&&Y<=0) {
			pole="N";
			//int ILatName=Integer.valueOf(findPoleChAr[1]);
			latName="00";//+String.valueOf(ILatName);
			String[] findlonStrAr=String.valueOf(X).split("\\.");//120.1
			char[] findlonNameChAr=String.valueOf(findlonStrAr[0]).toCharArray();//take left value after "." ,make char array
			if(findlonNameChAr.length==3){
				lonName=String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1])+String.valueOf(findlonNameChAr[2]);
			}
			else{
				lonName="0"+String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1]);
			}
			//			//findTileName, need value after point from minX and maxY 
			//			char[] getRightMinX=findlonStrAr[1].toCharArray();
			//			String rightMinX=String.valueOf(getRightMinX[0]);//right value after "." of minX
			//			char[] getRightMaxY=findPoleStrAr[1].toCharArray();
			//			int IRightMaxY=10-Character.getNumericValue(getRightMaxY[0]);
			//			if(IRightMaxY==10){
			//				IRightMaxY=0;
			//			}
			//			String rightMaxY=String.valueOf(IRightMaxY);//right value after "." of maxY
			//			tileName=rightMinX+rightMaxY;


		}
		else {
			pole="S";
			//			if(String.valueOf(findPoleChAr[0]).equals("-")){//-
			//				pole="S";
			//			}
			//			else{//3
			//				pole="N";
			//			}
			String[] findlatStrAr=String.valueOf(Y).split("\\.");//120.1
			char[] findlatNameChAr=String.valueOf(findlatStrAr[0]).toCharArray();
			if(findlatNameChAr.length==2){//-9 -1
				latName="0"+String.valueOf(findlatNameChAr[1]);
			}
			else {//-10 -12
				latName=String.valueOf(findlatNameChAr[1])+String.valueOf(findlatNameChAr[2]);
			}
			//			else{
			//				latName="0"+String.valueOf(findPoleChAr[0]);
			//			}
			//find lonName based on minX (lowest west point)
			String[] findlonStrAr=String.valueOf(X).split("\\.");//120.1
			char[] findlonNameChAr=String.valueOf(findlonStrAr[0]).toCharArray();//take left value after "." ,make char array
			if(findlonNameChAr.length==3){
				lonName=String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1])+String.valueOf(findlonNameChAr[2]);
			}
			else{
				lonName="0"+String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1]);
			}



		}


		fileName=pole+latName+"E"+lonName;
		System.out.println(fileName);
		//		wCom.write("fileName="+pole+latName+"E"+lonName+"_"+tileName+"_"+satName+"_SSC_"+date+".tif");
		//		wCom.write("\n");

		//}
		return fileName;
	}

	public ArrayList<String> makeTile(double[] oriLatLon,double[] boxLatLon) throws IOException{

		ArrayList<String> subTiles=new ArrayList<String>();
		//double[] oriLatLon=points;
		kmlW = new FileWriter(new File("/media/hd/TSX_TB/tesKmlProv/"+idProv+"_"+provName+"_subTiles.kml"));
		kmlW.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n");
		kmlW.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\"> \n");
		kmlW.write("<Document id=\"root_doc\"> \n");
		kmlW.write("<Schema name=\"tile_id\" id=\"tile_id\"> \n");
		kmlW.write("	<SimpleField name=\"id\" type=\"string\"></SimpleField> \n");
		kmlW.write("</Schema> \n");
		kmlW.write("<Folder><name>tile_id</name> \n");

		double ulXTemp=oriLatLon[0];
		double ulYTemp=oriLatLon[1];
		double lrXTemp=oriLatLon[2];
		double lrYTemp=oriLatLon[3];

		double ulXLimit=boxLatLon[0];
		double ulYLimit=boxLatLon[1];
		double lrXLimit=boxLatLon[2];
		double lrYLimit=boxLatLon[3];


		try{
			//File in= new File(template);
			//File out= new File(par);
			System.out.println("Start making Box.kml");
			//File corners = new File(outputDirName+outputMakeDir+fileOutputName+".corners.txt");
			aKmlW = new FileWriter(new File("/media/hd/TSX_TB/tesKmlProv/"+idProv+"_"+provName+"_box.kml"));
			aKmlW.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n");
			aKmlW.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\"> \n");
			aKmlW.write("<Document id=\"root_doc\"> \n");
			aKmlW.write("<Schema name=\"tile_id\" id=\"tile_id\"> \n");
			aKmlW.write("	<SimpleField name=\"id\" type=\"string\"></SimpleField> \n");
			aKmlW.write("</Schema> \n");
			aKmlW.write("<Folder><name>tile_id</name> \n");
			//double cutMinX=oriMinX;


			aKmlW.write("  <Placemark> \n");
			aKmlW.write("	<name>"+fileOutputName+"</name> <description>"+fileOutputName+"</description> \n");
			aKmlW.write("	<Style><LineStyle><color>ff0000ff</color></LineStyle><PolyStyle><fill>0</fill></PolyStyle></Style> \n");
			aKmlW.write("	<ExtendedData><SchemaData schemaUrl=\"#kml_order\"> \n");
			aKmlW.write("		<SimpleData name=\"id\">"+fileOutputName+"</SimpleData> \n");
			aKmlW.write("	</SchemaData></ExtendedData> \n");
			aKmlW.write("      <Polygon><altitudeMode>relativeToGround</altitudeMode><outerBoundaryIs><LinearRing><altitudeMode>relativeToGround</altitudeMode><coordinates>"
					+boxLatLon[0]+","+boxLatLon[1]+" "+boxLatLon[2]+","+boxLatLon[1]+" "+boxLatLon[2]+","+boxLatLon[3]+" "+
					boxLatLon[0]+","+boxLatLon[3]+" "+boxLatLon[0]+","+boxLatLon[1]+" </coordinates></LinearRing></outerBoundaryIs></Polygon> \n");
			aKmlW.write("  </Placemark> \n");

			aKmlW.write("</Folder> \n");
			aKmlW.write("</Document></kml> \n");
			aKmlW.close();

		} catch (IOException e) {
			System.out.println(e);
		}
		//		double cutMaxX=oriMaxX-0.11;
		//		double cutMinY=oriMinY+0.11;
		//		double sizeMinX=oriMinX;
		//		double sizeMaxY=oriMaxY;
		//		double sizeMaxX=oriMaxX;
		//		double sizeMinY=oriMinY;


		//KIRI 
		while(ulXTemp<ulXLimit){
			//			System.out.println("While 1 of KIRI.kml");
			//			System.out.println("ulXTemp="+ulXTemp+"<ulXLimit="+ulXLimit);
			//			System.out.println("ulYTemp="+ulYTemp+">lrYTemp"+lrYTemp);
			while(ulYTemp>lrYTemp){
				//				System.out.println("While 2 of KIRI.kml");
				//				System.out.println("ulYTemp="+ulYTemp+">lrYTemp"+lrYTemp);
				double ulCutX=Math.round((ulXTemp-0.01)*100.0);
				ulCutX=ulCutX/100.0;
				//double cutMaxY=oriMaxY;sizeMinX
				double ulCutY=Math.round((ulYTemp+0.01)*100.0);
				ulCutY=ulCutY/100.0;

				double lrCutX=Math.round((ulXTemp+0.11)*100.0);
				lrCutX=lrCutX/100.0;
				double lrCutY=Math.round((ulYTemp-0.11)*100.0);
				lrCutY=lrCutY/100.0;
				ulYTemp=Math.round((ulYTemp-0.1)*100.0);
				ulYTemp=ulYTemp/100.0;	
				//double[] toCrop={ulCutX,ulCutY,lrCutX,lrCutY};
				//				wCom.write("toCrop="+ulCutX+" ,"+ulCutY+" ,"+lrCutX+" ,"+lrCutY);
				//				wCom.write("\n");
				//				double newSizeMaxX=sizeMinX+0.1;
				//				double newSizeMinY=sizeMaxY-0.1;
				//				toPass[0]=ulXTemp;//
				//				toPass[1]=ulYTemp;
				//				toPass[2]=ulCutX;
				//				toPass[3]=ulCutY;
				//				toPass[4]=lrCutX;
				//				toPass[5]=lrCutY;
				//				wCom.write("toPass="+ulXTemp+" , "+ulYTemp+","+ulCutX+" ,"+ulCutY+" ,"+lrCutX+" ,"+lrCutY);
				//				wCom.write("\n");
				String fileName=createName(ulXTemp,ulYTemp);
				subTiles.add(fileName);
				//listCommand.add("mkdir "+targetDir+"/"+year);
				String targetOutFile=targetDir+"/"+year+"/"+pole+latName+"E"+lonName;
				//				listCommand.add("mkdir "+targetOutFile);
				//				listCommand.add("gdal_translate -a_nodata 0 -projwin "+ulCutX+" "+ulCutY+" "+lrCutX+" "+lrCutY+" "+
				//						namePath+".cmli.GTC.bin "+targetOutFile+"/"+fileName);
				//				wCom.write("gdal_translate -a_nodata 0 -projwin "+ulCutX+" "+ulCutY+" "+lrCutX+" "+lrCutY+" "+
				//						namePath+".cmli.GTC.bin "+targetOutFile+"/"+fileName);
				//				wCom.write("\n");


				System.out.println("Start making kiri.kml");
				kmlW.write("  <Placemark> \n");
				kmlW.write("	<name>"+"nameTag"+"</name> <description>"+"descTag"+"</description> \n");
				kmlW.write("	<Style><LineStyle><color>ff0000ff</color></LineStyle><PolyStyle><fill>0</fill></PolyStyle></Style> \n");
				kmlW.write("	<ExtendedData><SchemaData schemaUrl=\"#kml_order\"> \n");
				kmlW.write("		<SimpleData name=\"id\">"+fileName+"</SimpleData> \n");
				kmlW.write("	</SchemaData></ExtendedData> \n");
				kmlW.write("      <Polygon><altitudeMode>relativeToGround</altitudeMode><outerBoundaryIs><LinearRing><altitudeMode>relativeToGround</altitudeMode><coordinates>"
						+ulCutX+","+ulCutY+" "+lrCutX+","+ulCutY+" "+lrCutX+","+lrCutY+" "+
						ulCutX+","+lrCutY+" "+ulCutX+","+ulCutY+" </coordinates></LinearRing></outerBoundaryIs></Polygon> \n");
				kmlW.write("  </Placemark> \n");





				//createName(toName);
				//cropTile(toCrop);

			}
			ulYTemp=oriLatLon[1];
			ulXTemp=Math.round((ulXTemp+0.1)*100.0);
			ulXTemp=ulXTemp/100.0;	

		}


		ulXTemp=oriLatLon[0];
		ulYTemp=oriLatLon[1];
		lrXTemp=oriLatLon[2];
		lrYTemp=oriLatLon[3];

		ulXLimit=boxLatLon[0];
		ulYLimit=boxLatLon[1];
		lrXLimit=boxLatLon[2];
		lrYLimit=boxLatLon[3];
		//ATAS
		while(ulXLimit<lrXLimit){
			//			System.out.println("While 1 of ATAS.kml");
			//			System.out.println("ulXLimit="+ulXLimit+"<lrXLimit="+lrXLimit);
			//			System.out.println("ulYTemp="+ulYTemp+">ulYLimit"+ulYLimit);
			while(ulYTemp>ulYLimit){
				//				System.out.println("While 2 of ATAS.kml");
				//				System.out.println("ulYTemp="+ulYTemp+">ulYLimit"+ulYLimit);
				double ulCutX=Math.round((ulXLimit-0.01)*100.0);
				ulCutX=ulCutX/100.0;
				//double cutMaxY=oriMaxY;sizeMinX
				double ulCutY=Math.round((ulYTemp+0.01)*100.0);
				ulCutY=ulCutY/100.0;

				double lrCutX=Math.round((ulXLimit+0.11)*100.0);
				lrCutX=lrCutX/100.0;
				double lrCutY=Math.round((ulYTemp-0.11)*100.0);
				lrCutY=lrCutY/100.0;

				//double[] toCrop={ulCutX,ulCutY,lrCutX,lrCutY};
				//				wCom.write("toCrop="+ulCutX+" ,"+ulCutY+" ,"+lrCutX+" ,"+lrCutY);
				//				wCom.write("\n");
				//				double newSizeMaxX=sizeMinX+0.1;
				//				double newSizeMinY=sizeMaxY-0.1;
				//				toPass[0]=ulXTemp;//
				//				toPass[1]=ulYTemp;
				//				toPass[2]=ulCutX;
				//				toPass[3]=ulCutY;
				//				toPass[4]=lrCutX;
				//				toPass[5]=lrCutY;
				//				wCom.write("toPass="+ulXTemp+" , "+ulYTemp+","+ulCutX+" ,"+ulCutY+" ,"+lrCutX+" ,"+lrCutY);
				//				wCom.write("\n");
				String fileName=createName(ulXLimit,ulYTemp);
				subTiles.add(fileName);
				//listCommand.add("mkdir "+targetDir+"/"+year);
				String targetOutFile=targetDir+"/"+year+"/"+pole+latName+"E"+lonName;
				//				listCommand.add("mkdir "+targetOutFile);
				//				listCommand.add("gdal_translate -a_nodata 0 -projwin "+ulCutX+" "+ulCutY+" "+lrCutX+" "+lrCutY+" "+
				//						namePath+".cmli.GTC.bin "+targetOutFile+"/"+fileName);
				//				wCom.write("gdal_translate -a_nodata 0 -projwin "+ulCutX+" "+ulCutY+" "+lrCutX+" "+lrCutY+" "+
				//						namePath+".cmli.GTC.bin "+targetOutFile+"/"+fileName);
				//				wCom.write("\n");


				System.out.println("Start making atas.kml");
				kmlW.write("  <Placemark> \n");
				kmlW.write("	<name>"+"nameTag"+"</name> <description>"+"descTag"+"</description> \n");
				kmlW.write("	<Style><LineStyle><color>ff0000ff</color></LineStyle><PolyStyle><fill>0</fill></PolyStyle></Style> \n");
				kmlW.write("	<ExtendedData><SchemaData schemaUrl=\"#kml_order\"> \n");
				kmlW.write("		<SimpleData name=\"id\">"+fileName+"</SimpleData> \n");
				kmlW.write("	</SchemaData></ExtendedData> \n");
				kmlW.write("      <Polygon><altitudeMode>relativeToGround</altitudeMode><outerBoundaryIs><LinearRing><altitudeMode>relativeToGround</altitudeMode><coordinates>"
						+ulCutX+","+ulCutY+" "+lrCutX+","+ulCutY+" "+lrCutX+","+lrCutY+" "+
						ulCutX+","+lrCutY+" "+ulCutX+","+ulCutY+" </coordinates></LinearRing></outerBoundaryIs></Polygon> \n");
				kmlW.write("  </Placemark> \n");



				ulYTemp=Math.round((ulYTemp-0.1)*100.0);
				ulYTemp=ulYTemp/100.0;	

				//createName(toName);
				//cropTile(toCrop);

			}
			ulYTemp=oriLatLon[1];
			ulXLimit=Math.round((ulXLimit+0.1)*100.0);
			ulXLimit=ulXLimit/100.0;	

		}

		ulXTemp=oriLatLon[0];
		ulYTemp=oriLatLon[1];
		lrXTemp=oriLatLon[2];
		lrYTemp=oriLatLon[3];

		ulXLimit=boxLatLon[0];
		ulYLimit=boxLatLon[1];
		lrXLimit=boxLatLon[2];
		lrYLimit=boxLatLon[3];

		//KANAN 
		while(lrXLimit<lrXTemp){
			//					System.out.println("While 1 of KANAN.kml");
			//					System.out.println("lrXLimit="+lrXLimit+"<lrXTemp="+lrXTemp);
			//					System.out.println("ulYTemp="+ulYTemp+">lrYTemp"+lrYTemp);
			while(ulYTemp>lrYTemp){
				//						System.out.println("While 2 of KANAN.kml");
				//						System.out.println("ulYTemp="+ulYTemp+">lrYTemp"+lrYTemp);
				double ulCutX=Math.round((lrXLimit-0.01)*100.0);
				ulCutX=ulCutX/100.0;
				//double cutMaxY=oriMaxY;sizeMinX
				double ulCutY=Math.round((ulYTemp+0.01)*100.0);
				ulCutY=ulCutY/100.0;

				double lrCutX=Math.round((lrXLimit+0.11)*100.0);
				lrCutX=lrCutX/100.0;
				double lrCutY=Math.round((ulYTemp-0.11)*100.0);
				lrCutY=lrCutY/100.0;
				ulYTemp=Math.round((ulYTemp-0.1)*100.0);
				ulYTemp=ulYTemp/100.0;	
				//double[] toCrop={ulCutX,ulCutY,lrCutX,lrCutY};
				//				wCom.write("toCrop="+ulCutX+" ,"+ulCutY+" ,"+lrCutX+" ,"+lrCutY);
				//				wCom.write("\n");
				//				double newSizeMaxX=sizeMinX+0.1;
				//				double newSizeMinY=sizeMaxY-0.1;
				//						toPass[0]=ulXTemp;//
				//						toPass[1]=ulYTemp;
				//						toPass[2]=ulCutX;
				//						toPass[3]=ulCutY;
				//						toPass[4]=lrCutX;
				//						toPass[5]=lrCutY;
				//				wCom.write("toPass="+ulXTemp+" , "+ulYTemp+","+ulCutX+" ,"+ulCutY+" ,"+lrCutX+" ,"+lrCutY);
				//				wCom.write("\n");
				String fileName=createName(lrXLimit,ulYTemp);
				subTiles.add(fileName);
				//listCommand.add("mkdir "+targetDir+"/"+year);
				String targetOutFile=targetDir+"/"+year+"/"+pole+latName+"E"+lonName;
				//				listCommand.add("mkdir "+targetOutFile);
				//				listCommand.add("gdal_translate -a_nodata 0 -projwin "+ulCutX+" "+ulCutY+" "+lrCutX+" "+lrCutY+" "+
				//						namePath+".cmli.GTC.bin "+targetOutFile+"/"+fileName);
				//				wCom.write("gdal_translate -a_nodata 0 -projwin "+ulCutX+" "+ulCutY+" "+lrCutX+" "+lrCutY+" "+
				//						namePath+".cmli.GTC.bin "+targetOutFile+"/"+fileName);
				//				wCom.write("\n");


				System.out.println("Start making kanan.kml");
				kmlW.write("  <Placemark> \n");
				kmlW.write("	<name>"+"nameTag"+"</name> <description>"+"descTag"+"</description> \n");
				kmlW.write("	<Style><LineStyle><color>ff0000ff</color></LineStyle><PolyStyle><fill>0</fill></PolyStyle></Style> \n");
				kmlW.write("	<ExtendedData><SchemaData schemaUrl=\"#kml_order\"> \n");
				kmlW.write("		<SimpleData name=\"id\">"+fileName+"</SimpleData> \n");
				kmlW.write("	</SchemaData></ExtendedData> \n");
				kmlW.write("      <Polygon><altitudeMode>relativeToGround</altitudeMode><outerBoundaryIs><LinearRing><altitudeMode>relativeToGround</altitudeMode><coordinates>"
						+ulCutX+","+ulCutY+" "+lrCutX+","+ulCutY+" "+lrCutX+","+lrCutY+" "+
						ulCutX+","+lrCutY+" "+ulCutX+","+ulCutY+" </coordinates></LinearRing></outerBoundaryIs></Polygon> \n");
				kmlW.write("  </Placemark> \n");





				//createName(toName);
				//cropTile(toCrop);

			}
			ulYTemp=oriLatLon[1];
			lrXLimit=Math.round((lrXLimit+0.1)*100.0);
			lrXLimit=lrXLimit/100.0;	

		}

		ulXTemp=oriLatLon[0];
		ulYTemp=oriLatLon[1];
		lrXTemp=oriLatLon[2];
		lrYTemp=oriLatLon[3];

		ulXLimit=boxLatLon[0];
		ulYLimit=boxLatLon[1];
		lrXLimit=boxLatLon[2];
		lrYLimit=boxLatLon[3];


		//BAWAH
		while(ulXLimit<lrXLimit){
			System.out.println("While 1 of BAWAH.kml");
			System.out.println("ulXLimit="+ulXLimit+"<lrXLimit="+lrXLimit);
			System.out.println("lrYTemp="+lrYTemp+">lrYLimit"+lrYLimit);
			while(lrYLimit>lrYTemp){
				System.out.println("While 2 of BAWAH.kml");
				System.out.println("lrYTemp="+lrYTemp+">lrYLimit"+lrYLimit);
				double ulCutX=Math.round((ulXLimit-0.01)*100.0);
				ulCutX=ulCutX/100.0;
				//double cutMaxY=oriMaxY;sizeMinX
				double ulCutY=Math.round((lrYLimit+0.01)*100.0);
				ulCutY=ulCutY/100.0;

				double lrCutX=Math.round((ulXLimit+0.11)*100.0);
				lrCutX=lrCutX/100.0;
				double lrCutY=Math.round((lrYLimit-0.11)*100.0);
				lrCutY=lrCutY/100.0;

				//double[] toCrop={ulCutX,ulCutY,lrCutX,lrCutY};
				//				wCom.write("toCrop="+ulCutX+" ,"+ulCutY+" ,"+lrCutX+" ,"+lrCutY);
				//				wCom.write("\n");
				//				double newSizeMaxX=sizeMinX+0.1;
				//				double newSizeMinY=sizeMaxY-0.1;
				//						toPass[0]=ulXTemp;//
				//						toPass[1]=ulYTemp;
				//						toPass[2]=ulCutX;
				//						toPass[3]=ulCutY;
				//						toPass[4]=lrCutX;
				//						toPass[5]=lrCutY;
				//				wCom.write("toPass="+ulXTemp+" , "+ulYTemp+","+ulCutX+" ,"+ulCutY+" ,"+lrCutX+" ,"+lrCutY);
				//				wCom.write("\n");
				String fileName=createName(ulXLimit,lrYLimit);
				subTiles.add(fileName);
				//listCommand.add("mkdir "+targetDir+"/"+year);
				String targetOutFile=targetDir+"/"+year+"/"+pole+latName+"E"+lonName;
				//				listCommand.add("mkdir "+targetOutFile);
				//				listCommand.add("gdal_translate -a_nodata 0 -projwin "+ulCutX+" "+ulCutY+" "+lrCutX+" "+lrCutY+" "+
				//						namePath+".cmli.GTC.bin "+targetOutFile+"/"+fileName);
				//				wCom.write("gdal_translate -a_nodata 0 -projwin "+ulCutX+" "+ulCutY+" "+lrCutX+" "+lrCutY+" "+
				//						namePath+".cmli.GTC.bin "+targetOutFile+"/"+fileName);
				//				wCom.write("\n");


				System.out.println("Start making bawah.kml");
				kmlW.write("  <Placemark> \n");
				kmlW.write("	<name>"+"nameTag"+"</name> <description>"+"descTag"+"</description> \n");
				kmlW.write("	<Style><LineStyle><color>ff0000ff</color></LineStyle><PolyStyle><fill>0</fill></PolyStyle></Style> \n");
				kmlW.write("	<ExtendedData><SchemaData schemaUrl=\"#kml_order\"> \n");
				kmlW.write("		<SimpleData name=\"id\">"+fileName+"</SimpleData> \n");
				kmlW.write("	</SchemaData></ExtendedData> \n");
				kmlW.write("      <Polygon><altitudeMode>relativeToGround</altitudeMode><outerBoundaryIs><LinearRing><altitudeMode>relativeToGround</altitudeMode><coordinates>"
						+ulCutX+","+ulCutY+" "+lrCutX+","+ulCutY+" "+lrCutX+","+lrCutY+" "+
						ulCutX+","+lrCutY+" "+ulCutX+","+ulCutY+" </coordinates></LinearRing></outerBoundaryIs></Polygon> \n");
				kmlW.write("  </Placemark> \n");



				lrYLimit=Math.round((lrYLimit-0.1)*100.0);
				lrYLimit=lrYLimit/100.0;	

				//createName(toName);
				//cropTile(toCrop);

			}
			lrYLimit=boxLatLon[3];
			ulXLimit=Math.round((ulXLimit+0.1)*100.0);
			ulXLimit=ulXLimit/100.0;	

		}


		kmlW.write("</Folder> \n");
		kmlW.write("</Document></kml> \n");
		kmlW.close();
		//return toPass;
		return subTiles;

	}


	public ArrayList<String> makeTileFolder(double[] oriLatLon) throws IOException{
		//double[] oriLatLon=points;
		ArrayList<String> tiles=new ArrayList<String>();
		kmlW = new FileWriter(new File("/media/hd/TSX_TB/tesKmlProv/"+idProv+"_"+provName+"_tiles.kml"));
		kmlW.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n");
		kmlW.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\"> \n");
		kmlW.write("<Document id=\"root_doc\"> \n");
		kmlW.write("<Schema name=\"tile_id\" id=\"tile_id\"> \n");
		kmlW.write("	<SimpleField name=\"id\" type=\"string\"></SimpleField> \n");
		kmlW.write("</Schema> \n");
		kmlW.write("<Folder><name>tile_id</name> \n");

		double ulXTemp=oriLatLon[0];
		double ulYTemp=oriLatLon[1];
		double lrXTemp=oriLatLon[2];
		double lrYTemp=oriLatLon[3];
		//
		//		double ulXLimit=boxLatLon[0];
		//		double ulYLimit=boxLatLon[1];
		//		double lrXLimit=boxLatLon[2];
		//		double lrYLimit=boxLatLon[3];



		double[] toPass=new double[6];
		while(ulXTemp<oriLatLon[2]){
			while(ulYTemp>oriLatLon[3]){
				double ulCutX=ulXTemp;
				//ulCutX=ulCutX/100.0;
				//double cutMaxY=oriMaxY;sizeMinX
				double ulCutY=ulYTemp;
				//ulCutY=ulCutY/100.0;

				double lrCutX=Math.round((ulXTemp+1)*100.0);
				lrCutX=lrCutX/100.0;
				double lrCutY=Math.round((ulYTemp-1)*100.0);
				lrCutY=lrCutY/100.0;




				String fileName=createNameFolder(ulXTemp,ulYTemp);
				tiles.add(fileName);


				kmlW.write("  <Placemark> \n");
				kmlW.write("	<name>"+"nameTag"+"</name> <description>"+"descTag"+"</description> \n");
				kmlW.write("	<Style><LineStyle><color>ff0000ff</color></LineStyle><PolyStyle><fill>0</fill></PolyStyle></Style> \n");
				kmlW.write("	<ExtendedData><SchemaData schemaUrl=\"#kml_order\"> \n");
				kmlW.write("		<SimpleData name=\"id\">"+fileName+"</SimpleData> \n");
				kmlW.write("	</SchemaData></ExtendedData> \n");
				kmlW.write("      <Polygon><altitudeMode>relativeToGround</altitudeMode><outerBoundaryIs><LinearRing><altitudeMode>relativeToGround</altitudeMode><coordinates>"
						+ulCutX+","+ulCutY+" "+lrCutX+","+ulCutY+" "+lrCutX+","+lrCutY+" "+
						ulCutX+","+lrCutY+" "+ulCutX+","+ulCutY+" </coordinates></LinearRing></outerBoundaryIs></Polygon> \n");
				kmlW.write("  </Placemark> \n");


				ulYTemp=Math.round((ulYTemp-1)*100.0);
				ulYTemp=ulYTemp/100.0;	
				//tiles.add(",");
			}
			ulYTemp=oriLatLon[1];
			ulXTemp=Math.round((ulXTemp+1)*100.0);
			ulXTemp=ulXTemp/100.0;	

		}
		kmlW.write("</Folder> \n");
		kmlW.write("</Document></kml> \n");
		kmlW.close();
		return tiles;

	}



	public static void main(String[] ar) throws IOException {
		ListMosTiles exe=new ListMosTiles(); 
String codeP="";
		File inputDir=new File("/media/hd/TSX_TB/ProvkmlTiles/");
		for(int i=1;i<35;i++) {
		char[] stringLong=String.valueOf(i).toCharArray();
		if(stringLong.length==1) {
			codeP="0"+String.valueOf(stringLong[0]);
		}
		else {
			codeP=String.valueOf(stringLong[0]);
		}
		}
		for(int i=0;i<inputDir.listFiles().length;i++) {
			
			
			if(inputDir.listFiles()[i].getName().endsWith(".txt")) {
				exe.writeDB(inputDir.listFiles()[i].getAbsolutePath());
			}
		}
		//		//double[] corners=exe.getInsideCorners(exe.getCorners("/media/hd/TSX_TB/kml_border_TSX_prov/01_ID-AC_Aceh.txt"));
		//		double[] oriLatlon=exe.getCorners("/media/hd/TSX_TB/kml_border_TSX_prov/01_ID-AC_Aceh.txt");
		//		double[] boxLatlon=exe.getInsideCorners(exe.getCorners("/media/hd/TSX_TB/kml_border_TSX_prov/01_ID-AC_Aceh.txt"));
		//
		//		exe.makeTileFolder(exe.getInsideCorners(exe.getCorners("/media/hd/TSX_TB/kml_border_TSX_prov/01_ID-AC_Aceh.txt")));
		//		exe.makeTile(exe.getCorners("/media/hd/TSX_TB/kml_border_TSX_prov/01_ID-AC_Aceh.txt"),exe.getInsideCorners(exe.getCorners("/media/hd/TSX_TB/kml_border_TSX_prov/01_ID-AC_Aceh.txt")));
		//		for (int i=0;i<oriLatlon.length;i++) {
		//			System.out.println("oriLatlon["+i+"]"+oriLatlon[i]);
		//		}
		//
		//		for (int i=0;i<boxLatlon.length;i++) {
		//			System.out.println("boxLatlon["+i+"]"+boxLatlon[i]);
		//		}
		//		exe.getinfoProvinceID(inputTest);
	}
}
