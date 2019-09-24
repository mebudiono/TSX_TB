

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class removeBlankTiles {


	public static String shpProv="/media/hd/TSX_TB/kml_prov/kml_border/banten_border.txt";
	public static String folderTB="/media/public80/Mosaik_TSX_TB/2017/";
	public String workDir,cornersTxt,pole,year,date,dateFile,satName,latName,lonName,tileName,targetDir,result,provCode;
	public String inputDirName;//=workDir+"/input";//="/media/public80/SAR_smosaik_prov/2019/sulbar/input_EEC_cal_sample/";
	public String outputDirName;//=workDir+"/output";
	public String demPath;//=workDir+"/DEMIND/indonesia_SRTM30_rev1.tif";
	public String geoDir="/usr/local/GAMMA_SOFTWARE-20181130/GEO/bin/";
	public String scriptDir="/usr/local/scripts/";
	public String dispDir="/usr/local/GAMMA_SOFTWARE-20181130/DISP/bin/";
	public String parTemplate,pathFolder;//=workDir+"/diff_par_template";
	//public  String outputDir="/media/Data/TSX/OUTPUT/";
	public  int foundDIrInput;
	protected String outputMakeDir,mlirs,fileOutputName,xmlFile,cosFile,namePath,fileName;
	public double parSRes=6,minX,minY,maxX,maxY,minXR,minYR,maxXR,maxYR,ulX,ulY,lrX,lrY;

	public  ArrayList<String> listDir,listXML,listTif ;
	public static ArrayList<String> listCommand,listCommandRemove;
	public ArrayList<String> dapetXML;
	public ArrayList<String> dapetCOS;
	public Integer dem_width,mli_width;
	public FileWriter writeDiffOut;
	public static FileWriter wCom;
	public FileWriter wList;
	private static Connection connect = null;
	private static Statement statement = null;
	private static Statement statement2 = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public void writeKML(String fileKML,String ntile,double[] koordinat ) throws IOException{
		try{
			//File in= new File(template);
			//File out= new File(par);
			System.out.println("Start void process");
			//File corners = new File(outputDirName+outputMakeDir+fileOutputName+".corners.txt");
			FileWriter fw = new FileWriter(fileKML);
			fw.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n");
			fw.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\"> \n");
			fw.write("<Document id=\"root_doc\"> \n");
			fw.write("<Schema name=\"tile_id\" id=\"tile_id\"> \n");
			fw.write("	<SimpleField name=\"id\" type=\"string\"></SimpleField> \n");
			fw.write("</Schema> \n");
			fw.write("<Folder><name>tile_id</name> \n");


			//fw.write("\n");
			fw.write("  <Placemark> \n");
			fw.write("	<name>ddmmyy</name> <description>"+ntile+"</description> \n");
			fw.write("	<Style><LineStyle><color>ff0000ff</color></LineStyle><PolyStyle><fill>0</fill></PolyStyle></Style> \n");
			fw.write("	<ExtendedData><SchemaData schemaUrl=\"#kml_order\"> \n");
			fw.write("		<SimpleData name=\"id\">"+ntile+"</SimpleData> \n");
			fw.write("	</SchemaData></ExtendedData> \n");
			fw.write("      <Polygon><altitudeMode>relativeToGround</altitudeMode><outerBoundaryIs><LinearRing><altitudeMode>relativeToGround</altitudeMode><coordinates>"
					+koordinat[0]+","+koordinat[1]+" "+koordinat[2]+","+koordinat[1]+" "+koordinat[2]+","+koordinat[3]+" "+
					koordinat[0]+","+koordinat[3]+" "+koordinat[0]+","+koordinat[1]+" </coordinates></LinearRing></outerBoundaryIs></Polygon> \n");
			fw.write("  </Placemark> \n");

			fw.write("</Folder> \n");
			fw.write("</Document></kml> \n");
			fw.close();


		} catch (IOException e) {
			System.out.println(e);
		}
	}



	public   String getParam(String parPath,String regex,int column){
		BufferedReader reader;
		String found;	
		String[] cut=new String[10];
		try {
			reader = new BufferedReader(new FileReader(parPath));
			String line = reader.readLine();
			while (line != null) {
				//String regex2 = regex;
				//private  final String INPUT = "cat cat cat cattie cat";
				//		//String text    =
				//				"This is the text to be searched " +
				//						"for occurrences of the http:// pattern.";
				//String text3="/home/eucliwood/Documents/TDX1/input/TDX1_SAR__SSC______SM_S_SRA_20180308T101947_20180308T101957";

				//String text5="/home/eucliwood/Documents/TDX1/input/TDX1_SAR__SSC______SM_S_SRA_20180308T101947_20180308T101957";

				//String text2=isiDir[1].toString();
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(line);   // get a matcher object
				//System.out.println("isidir[foundDIrInput]= "+isiDir[foundDIrInput].toString());

				//		int count=0;
				if (m.find()){
					//	
					//	
					//	System.out.println("m.group="+m.group(count));
					//	//count++;
					//}
					//System.out.println(m.group(0).toString());

					cut=line.split("=");
					//										for (int i=0;i<cut.length;i++) {
					//											System.out.println(cut[i]);
					//					//						// read next line
					//										}
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

	//}
	public double[] getCorners(String pathCornersTxt) throws IOException{

		//get ulX from minlong corners,split by "."

		minX=Double.valueOf(getParam(pathCornersTxt,"min X=*",1));
		//if(minX>=0){
		//ulX=Math.round(minX * 10) / 10.0;
		String[] ulXStrAr=String.valueOf(minX).split("\\.");
		//split the right part after "."
		char[] ulXStrArRight=ulXStrAr[1].toCharArray();
		//combine left part before "." and 1 digit from right part to make ulX
		//int rightComma=Character.getNumericValue(ulXStrArRight[0])+1;
		ulX=Double.valueOf(ulXStrAr[0].toString());
		//	}
		//	else{
		//		//get ulY from maxlat corners,split by "."
		//		String[] ulXStrAr=getParam(pathCornersTxt,"min X=*",1).split("\\.");
		//		//minX=Double.valueOf(getParam(pathCornersTxt,"min.*longitude*:*",3));
		//		//split the right part after "."
		//		char[] ulXStrArRight=ulXStrAr[1].toCharArray();
		//		//combine left part before "." and 1 digit from right part to make ulY
		//		ulX=Double.valueOf(ulXStrAr[0].toString()+"."+ulXStrArRight[0]);
		//
		//	}

		maxY=Double.valueOf(getParam(pathCornersTxt,"max Y=*",1));
		if(maxY>=0){
			//ulY=Math.round(maxY * 10) / 10.0;
			String[] ulYStrAr=String.valueOf(maxY).split("\\.");
			//split the right part after "."
			char[] ulYStrArRight=ulYStrAr[1].toCharArray();
			int rightComma=Character.getNumericValue(ulYStrArRight[0]);
			double ulYTemp=Double.valueOf(ulYStrAr[0].toString());
			//combine left part before "." and 1 digit from right part to make ulX
			ulY=ulYTemp+1;
		}
		else{
			//get ulY from maxlat corners,split by "."
			String[] ulYStrAr=getParam(pathCornersTxt,"max Y=*",1).split("\\.");
			maxY=Double.valueOf(getParam(pathCornersTxt,"max Y=*",1));
			//split the right part after "."
			char[] ulYStrArRight=ulYStrAr[1].toCharArray();
			//combine left part before "." and 1 digit from right part to make ulY
			ulY=Double.valueOf(ulYStrAr[0].toString());

		}



		maxX=Double.valueOf(getParam(pathCornersTxt,"max X=*",1));
		//if(maxX>=0){
		//lrX=Math.round(maxX * 10) / 10.0;
		String[] lrXStrAr=String.valueOf(maxX).split("\\.");
		//split the right part after "."
		char[] lrXStrArRight=lrXStrAr[1].toCharArray();
		int rightComma=Character.getNumericValue(lrXStrArRight[0]);
		double lrXTemp=Double.valueOf(lrXStrAr[0].toString());
		//combine left part before "." and 1 digit from right part to make ulX
		lrX=lrXTemp+1;
		//	}
		//	else{
		//		//get lrX from maxlong corners,split by "."
		//		String[] lrXStrAr=getParam(pathCornersTxt,"max X=*",1).split("\\.");
		//		maxX=Double.valueOf(getParam(pathCornersTxt,"max X=*",1));
		//		//split the right part after "."
		//		char[] lrXStrArRight=lrXStrAr[1].toCharArray();
		//		//combine left part before "." and 1 digit from right part to make lrX
		//		lrX=Double.valueOf(lrXStrAr[0].toString()+"."+lrXStrArRight[0]);
		//
		//	}



		minY=Double.valueOf(getParam(pathCornersTxt,"min Y=*",1));
		if(minY>=0){
			//lrY=Math.round(minY * 10) / 10.0;
			String[] lrYStrAr=String.valueOf(minY).split("\\.");
			//split the right part after "."
			char[] lrYStrArRight=lrYStrAr[1].toCharArray();
			//int rightComma=Character.getNumericValue(lrYStrArRight[0]);

			//combine left part before "." and 1 digit from right part to make ulX
			lrY=Double.valueOf(lrYStrAr[0].toString());
		}
		else{
			//get lrY from minlat corners,split by "."
			String[] lrYStrAr=getParam(pathCornersTxt,"min Y=*",1).split("\\.");
			minY=Double.valueOf(getParam(pathCornersTxt,"min Y=*",1));
			//split the right part after "."
			char[] lrYStrArRight=lrYStrAr[1].toCharArray();
			//combine left part before "." and 1 digit from right part to make lrX
			double lrYTemp=Double.valueOf(lrYStrAr[0].toString());
			lrY=lrYTemp-1;


		}


		//		//get lrY from minlat corners,split by "."
		//		String[] lrYStrAr=getParam(pathCornersTxt,"min.*latitude*:*",3).split("\\.");
		//		minY=Double.valueOf(getParam(pathCornersTxt,"min.*latitude*:*",3));
		//		//split the right part after "."
		//		char[] lrYStrArRight=lrYStrAr[1].toCharArray();
		//		//combine left part before "." and 1 digit from right part to make lrX
		//		lrY=Double.valueOf(lrYStrAr[0].toString()+"."+lrYStrArRight[0]);

		double[] result={ulX,ulY,lrX,lrY};
		//wCom.write("ulX="+ulX+","+"ulY="+ulY+","+"lrX="+lrX+","+"lrY="+lrY);
		//wCom.write("\n");
		//result={minX,minY,maxX,maxY};
		//result
		return result;
	}
	public String createName(double X,double Y) throws IOException{
		pole=new String(); 
		tileName=new String();
		latName=new String();
		lonName=new String();
		satName=new String("TSX");
		date=new String("ddmmyy");
		String fileName=null;
		String[] findPoleStrAr=String.valueOf(Y).split("\\.");//2.1
		char[] findPoleChAr=String.valueOf(findPoleStrAr[0]).toCharArray();//2
		//find Pole based on ulY (highest north point)
		if (Y>0) {
			//wCom.write("This is Y>0");
			//wCom.write("\n");
			pole="N";
			int ILatName=Character.getNumericValue(findPoleChAr[0]);//2
			if(Character.getNumericValue(findPoleStrAr[1].toCharArray()[0])>0){
				ILatName=ILatName+1;//3
			}

			//wCom.write("ILatName+1="+ILatName);
			//wCom.write("\n");
			latName="0"+String.valueOf(ILatName);//03
			String[] findlonStrAr=String.valueOf(X).split("\\.");//120.1
			char[] findlonNameChAr=String.valueOf(findlonStrAr[0]).toCharArray();//take left value before "." ,make char array
			if(findlonNameChAr.length==3){
				lonName=String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1])+String.valueOf(findlonNameChAr[2]);
			}
			else{
				lonName="0"+String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1]);
			}
			//wCom.write("lonName="+lonName);
			//wCom.write("\n");
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
			//wCom.write("tileName="+tileName);
			//wCom.write("\n");

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
		fileName=pole+latName+"E"+lonName;

		//wCom.write("fileName="+pole+latName+"E"+lonName+"_"+tileName+"_"+satName+"_SSC_"+date+".tif");
		//wCom.write("\n");

		//}
		return fileName;
	}
	public void makeTile(double[] oriLatLon) throws IOException{
		//double[] oriLatLon=points;

		double ulXTemp=oriLatLon[0];
		double ulYTemp=oriLatLon[1];
		double lrXTemp=oriLatLon[2];
		double lrYTemp=oriLatLon[3];
		//double cutMinX=oriMinX;


		//		double cutMaxX=oriMaxX-0.11;
		//		double cutMinY=oriMinY+0.11;
		//		double sizeMinX=oriMinX;
		//		double sizeMaxY=oriMaxY;
		//		double sizeMaxX=oriMaxX;
		//		double sizeMinY=oriMinY;
		double[] toPass=new double[6];
		while(ulXTemp<=oriLatLon[2]){
			while(ulYTemp>=oriLatLon[3]){
				double ulCutX=Math.round((ulXTemp-0.01)*100.0);
				ulCutX=ulCutX/100.0;
				//double cutMaxY=oriMaxY;sizeMinX
				double ulCutY=Math.round((ulYTemp+0.01)*100.0);
				ulCutY=ulCutY/100.0;

				double lrCutX=Math.round((ulXTemp+0.11)*100.0);
				lrCutX=lrCutX/100.0;
				double lrCutY=Math.round((ulYTemp-0.11)*100.0);
				lrCutY=lrCutY/100.0;

				//double[] toCrop={ulCutX,ulCutY,lrCutX,lrCutY};
				//				wCom.write("toCrop="+ulCutX+" ,"+ulCutY+" ,"+lrCutX+" ,"+lrCutY);
				//				wCom.write("\n");
				//				double newSizeMaxX=sizeMinX+0.1;
				//				double newSizeMinY=sizeMaxY-0.1;
				toPass[0]=ulXTemp;//
				toPass[1]=ulYTemp;
				toPass[2]=ulCutX;
				toPass[3]=ulCutY;
				toPass[4]=lrCutX;
				toPass[5]=lrCutY;
				//wCom.write("toPass="+ulXTemp+" , "+ulYTemp+","+ulCutX+" ,"+ulCutY+" ,"+lrCutX+" ,"+lrCutY);
				//wCom.write("\n");
				String fileName=createName(ulXTemp,ulYTemp);

				listCommand.add("mkdir "+targetDir+"/"+year);
				String targetOutFile=targetDir+"/"+year+"/"+pole+latName+"E"+lonName;
				listCommand.add("mkdir "+targetOutFile);
				listCommand.add("gdal_translate -a_nodata 0 -projwin "+ulCutX+" "+ulCutY+" "+lrCutX+" "+lrCutY+" "+
						namePath+".cmli.GTC.bin "+targetOutFile+"/"+fileName);
				//wCom.write("gdal_translate -a_nodata 0 -projwin "+ulCutX+" "+ulCutY+" "+lrCutX+" "+lrCutY+" "+
				//	namePath+".cmli.GTC.bin "+targetOutFile+"/"+fileName);
				//wCom.write("\n");
				//createName(toName);
				//cropTile(toCrop);
				ulYTemp=Math.round((ulYTemp-0.1)*100.0);
				ulYTemp=ulYTemp/100.0;	
			}
			ulYTemp=oriLatLon[1];
			ulXTemp=Math.round((ulXTemp+0.1)*100.0);
			ulXTemp=ulXTemp/100.0;	

		}
	}

	public boolean cekNull(String fileTif) {
		boolean Null=false;
		File tif=new File(fileTif);
		listCommand=new ArrayList<String> ();
		listCommand.add("gdalinfo -stats "+fileTif);
		String lastString="";
		try {

			for (int b=0;b<listCommand.size();b++) {
				Runtime r = Runtime.getRuntime();                    
				Process p = r.exec(listCommand.get(b));	
				BufferedReader in =
						new BufferedReader(new InputStreamReader(p.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {

					//System.out.println(inputLine);
					result += inputLine;
					lastString=inputLine;
				}
				
				in.close();
				if(lastString.contains("NoData Value=0")) {
					//fileTif
					System.out.println(fileTif+"  "+lastString);
					Null=true;
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}

		return Null;

	}

	public boolean cekIntersect(String shpProv,String kmlTile) {
		boolean stateInt=true;
		listCommand=new ArrayList<String> ();
		listCommand.add("python /media/hd/TSX_TB/Intersection/cek_intersect.py "+shpProv+" "+kmlTile);
		String lastString=null;
		try {

			for (int b=0;b<listCommand.size();b++) {
				Runtime r = Runtime.getRuntime();                    
				Process p = r.exec(listCommand.get(b));	
				BufferedReader in =
						new BufferedReader(new InputStreamReader(p.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {

					System.out.println(inputLine);
					result += inputLine;
					lastString=inputLine;
				}
				System.out.println("lastString cekIntersect="+lastString);
				in.close();
				if(lastString.contains("not")) {
					stateInt=false;
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		return stateInt;
	}
	public ArrayList<String> writeTile(double[] oriLatLon) throws IOException{
		//double[] oriLatLon=points;
		listCommand=new ArrayList<String> ();
		double ulXTemp=oriLatLon[0];
		double ulYTemp=oriLatLon[1];
		double lrXTemp=oriLatLon[2];
		double lrYTemp=oriLatLon[3];
		//double cutMinX=oriMinX;


		//		double cutMaxX=oriMaxX-0.11;
		//		double cutMinY=oriMinY+0.11;
		//		double sizeMinX=oriMinX;
		//		double sizeMaxY=oriMaxY;
		//		double sizeMaxX=oriMaxX;
		//		double sizeMinY=oriMinY;
		double[] toPass=new double[6];
		while(ulXTemp<=oriLatLon[2]){
			while(ulYTemp>=oriLatLon[3]){
				double ulCutX=Math.round((ulXTemp)*100.0);
				ulCutX=ulCutX/100.0;
				//double cutMaxY=oriMaxY;sizeMinX
				double ulCutY=Math.round((ulYTemp)*100.0);
				ulCutY=ulCutY/100.0;

				double lrCutX=Math.round((ulXTemp+1)*100.0);
				lrCutX=lrCutX/100.0;
				double lrCutY=Math.round((ulYTemp-1)*100.0);
				lrCutY=lrCutY/100.0;

				//double[] toCrop={ulCutX,ulCutY,lrCutX,lrCutY};
				//				wCom.write("toCrop="+ulCutX+" ,"+ulCutY+" ,"+lrCutX+" ,"+lrCutY);
				//				wCom.write("\n");
				//				double newSizeMaxX=sizeMinX+0.1;
				//				double newSizeMinY=sizeMaxY-0.1;
				toPass[0]=ulXTemp;//
				toPass[1]=ulYTemp;
				toPass[2]=ulCutX;
				toPass[3]=ulCutY;
				toPass[4]=lrCutX;
				toPass[5]=lrCutY;
				//wCom.write("toPass="+ulXTemp+" , "+ulYTemp+","+ulCutX+" ,"+ulCutY+" ,"+lrCutX+" ,"+lrCutY);
				//wCom.write("\n");
				String fileName=createName(ulXTemp,ulYTemp);
				//listCommand.add("mkdir "+targetDir+"/"+year);
				String targetOutFile=targetDir+"/"+year+"/"+pole+latName+"E"+lonName;
				//listCommand.add("mkdir "+targetOutFile);
				//listCommand.add("gdal_translate -a_nodata 0 -projwin "+ulCutX+" "+ulCutY+" "+lrCutX+" "+lrCutY+" "+
				//	namePath+".cmli.GTC.bin "+targetOutFile+"/"+fileName);
				listCommand.add(fileName);
				String fileKML="/media/rossendra/01D23527E552C810/TSX/TSX_TB/cobaTileKML/aceh/"+fileName;
				double[] koord=new double[4];
				koord[0]=ulCutX;
				koord[1]=ulCutY;
				koord[2]=lrCutX;
				koord[3]=lrCutY;

				//writeKML(fileKML,fileName,koord);


				//wCom.write("gdal_translate -a_nodata 0 -projwin "+ulCutX+" "+ulCutY+" "+lrCutX+" "+lrCutY+" "+
				//	namePath+".cmli.GTC.bin "+targetOutFile+"/"+fileName);
				//wCom.write("\n");
				//createName(toName);
				//cropTile(toCrop);
				ulYTemp=Math.round((ulYTemp-1)*100.0);
				ulYTemp=ulYTemp/100.0;	
			}
			ulYTemp=oriLatLon[1];
			ulXTemp=Math.round((ulXTemp+1)*100.0);
			ulXTemp=ulXTemp/100.0;	

		}
		return listCommand;

	}

	public ArrayList<String> getTileList(String txtProv) throws IOException{
		ArrayList<String> tiles=new ArrayList<String>();
		ArrayList<String> listTile=writeTile(getCorners(txtProv)); 
		for(int k=0;k<listTile.size();k++){
			//if(k==listTile.size()-1){
			tiles.add(listTile.get(k));
			//tiles.append(",");
		}

		return tiles;

	}



	public ArrayList<String> getListTif(String folderYear,String folderTile,String shpProv){
		listTif=new ArrayList<String>();
		//		for(int i=0;i<folderYear.size();i++) {
		//			for(int j=0;j<folderTile.size();j++) {

		File folderSource=new File(folderYear+"/"+folderTile);
		//ArrayList<String> tempList=new ArrayList<String>();
		System.out.println("getListTiffolderSource="+folderSource);
		if(folderSource.exists()) {
			for(int k=0;k<folderSource.listFiles().length;k++){
				if(folderSource.listFiles()[k].toString().endsWith(".tif")){
					System.out.println("getListTifWith(\".tif\")="+folderSource.listFiles()[k].toString());
					if(cekNull(folderSource.listFiles()[k].toString())==false) {
						String[] tifNameAr=folderSource.listFiles()[k].toString().split("\\.");
						String kmlName=tifNameAr[0]+".kml";
						System.out.println("getListTifkmlName="+kmlName);
						System.out.println("getListTifshpProv="+shpProv);
						if(cekIntersect(shpProv,kmlName)==true) {
							listTif.add(folderSource.listFiles()[k].toString());
						}
					}
				}
			}
		}
		//			}
		//		}
		return listTif;

	}
	public String makeFileName(ArrayList<String> tifList,String txtProv) throws ParseException {
		ArrayList<Date> dateList=new ArrayList<Date>();

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		for(int i=0;i<tifList.size();i++) {
			File tif=new File(tifList.get(i));
			String nameTif=tif.getName();
			char[] dateAr=nameTif.toCharArray();
			String dateStr="20"+dateAr[19]+dateAr[20]+"-"+dateAr[21]+dateAr[22]+"-"+dateAr[23]+dateAr[24];
			System.out.println("makeFileNamenameTif="+nameTif);
			System.out.println("makeFileNamedateStr="+dateStr);
			Date date=sdf.parse(dateStr);
			dateList.add(date);
		}
		File areaShp=new File(txtProv);
		String areaName=areaShp.getName();
		char[] provCodeAr=areaName.toCharArray();
		provCode=new StringBuilder().append(provCodeAr[4]).append(provCodeAr[5]).append(provCodeAr[6]).append(provCodeAr[7]).toString();
		//+provCodeAr[5]+provCodeAr[6]+provCodeAr[7]+provCodeAr[8]);

		Date dateMax=Collections.max(dateList);
		System.out.println("dateMax="+dateMax);
		Date dateMin=Collections.min(dateList);
		System.out.println("dateMin="+dateMin);
		String strDateMax=sdf.format(dateMax).replace("-","");
		System.out.println("strDateMax="+strDateMax);
		String strDateMin=sdf.format(dateMin).replace("-","");
		System.out.println("strDateMin="+strDateMin);

		fileName="LPN_TSX_GTCHH_"+strDateMin.toString()+"_"+strDateMax+"_MOS_"+provCode;
		return fileName;
	}

	public void runMosMulti(ArrayList<String> outputFolder,ArrayList<String> txtProv,ArrayList<String> shpProv,ArrayList<String> folderYear,String folderTile ) {


	}
	public void runMos(String listPath,String inputFolder,String outputFolder,String txtProv,String shpProv,String folderYear,String folderTile ) throws IOException {
		ArrayList<String> listTiles=getTileList(txtProv);


		listCommand=new ArrayList<String> ();
		listCommand.add("gdalbuildvrt -input_file_list "+listPath+" banten.vrt");
		String lastString=null;
		wList = new FileWriter(listPath+"//listFiles_"+fileName+".txt");
		try {

			for (int b=0;b<listCommand.size();b++) {
				Runtime r = Runtime.getRuntime();                    
				Process p = r.exec(listCommand.get(b));	
				BufferedReader in =
						new BufferedReader(new InputStreamReader(p.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {

					//System.out.println(inputLine);
					result += inputLine;
					lastString=inputLine;
				}
				System.out.println("lastStringcekNull="+lastString);
				in.close();

			}
		} catch (IOException e) {
			System.out.println(e);
		}

	}

	public static void main (String[] ar) throws IOException, ParseException{



		String tif="/media/public80/Mosaik_TSX_TB/2017/N00E099/N00E099_30_TSX_SSC_170925T113735.tif";
		String shpProv="/media/hd/TSX_TB/Intersection/prov/Sulawesi_selatan.shp";
		String kmlTile="/media/hd/TSX_TB/Intersection/kml_cek_2019.kml";
		removeBlankTiles coba=new removeBlankTiles();
		//System.out.println("stateNull="+coba.cekNull(tif));
		//System.out.println("stateInt="+coba.cekIntersect(shpProv,kmlTile));

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String starttime=dtf.format(now).toString();


		String yearProject="/media/public80/Mosaik_TSX_TB/2017";
		String yearProject2="/media/public80/Mosaik_TSX_TB/2018";
		String yearProject3="/media/public80/Mosaik_TSX_TB/2019";
		ArrayList<String> multiYearProject=new ArrayList<String>();
		multiYearProject.add(yearProject);
		multiYearProject.add(yearProject2);
		multiYearProject.add(yearProject3);
		String txtProject="/media/hd/TSX_TB/kml_border_TSX_prov/03_ID-BT_Banten.txt";
		String shpProject="/media/hd/TSX_TB/shp_prov/03_ID-BT_Banten.shp";
		//		ArrayList<String> tileList=coba.getTileList(txtProject);
		//		System.out.println("tileList=");
		//		for(int i=0;i<tileList.size();i++) {
		//		System.out.println(tileList.get(i));
		//		}
		wCom=new FileWriter(new File("/media/public80/Mosaik_TSX_TB/error/"+".listRemove"));
		listCommandRemove=new ArrayList<String>();
		for(int h=0;h<multiYearProject.size();h++) {
			File folder=new File(multiYearProject.get(h));
			for(int i=0;i<folder.listFiles().length;i++){
				if(folder.listFiles()[i].isDirectory()){
					//System.out.println("Start folder number="+i+" from "+folder.listFiles().length);
					//if(folder.listFiles()[i].toString().contains("LPN")){
					File folderInside=new File(folder.listFiles()[i].getAbsolutePath());
					if(folderInside.listFiles().length!=0) {
						//System.out.println("folder="+folder.listFiles()[i].getAbsolutePath().toString());
						//System.out.println("folderInside="+folderInside.listFiles()[0].getAbsolutePath());
						for(int j=0;j<folderInside.listFiles().length;j++){
							if(folderInside.listFiles()[j].getName().endsWith(".tif")) {
								//if(folderInside.listFiles()[0].list().length>0) {
								if(coba.cekNull(folderInside.listFiles()[j].getAbsolutePath())==true){
									//listCommandRemove.add("rm "+folderInside.listFiles()[j].getAbsolutePath());
									wCom.write("rm "+folderInside.listFiles()[j].getAbsolutePath());
									wCom.write("\n");
									System.out.println("wFile= "+"rm "+folderInside.listFiles()[j].getAbsolutePath());
									char[] fileExt=folderInside.listFiles()[j].getAbsolutePath().toCharArray();
									StringBuilder sb=new StringBuilder();
									//String fileExtkml="";
									for(int k=0;k<fileExt.length-4;k++) {
										sb.append(fileExt[k]);
									}
									String fileExtkml=sb.toString()+".kml";
									//listCommandRemove.add("rm "+fileExtkml);
									wCom.write("rm "+fileExtkml);
									wCom.write("\n");
									System.out.println("wFile= "+"rm "+fileExtkml);
								}
							}
						}
					}
				}
			}
		}

//		wCom=new FileWriter(new File("/media/public80/Mosaik_TSX_TB/error/"+".listRemove"));
//
//		for (int z=0;z<listCommand.size();z++) {
//			wCom.write(listCommand.get(z));
//			wCom.write("\n");
//		}
		System.out.println("Start Time="+starttime);
		DateTimeFormatter dtfEnd = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime End = LocalDateTime.now();
		String endtime=dtfEnd.format(End).toString();
		System.out.println("End Time="+endtime);
		wCom.close();
	}



}