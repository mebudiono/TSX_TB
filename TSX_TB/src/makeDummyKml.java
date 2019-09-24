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

public class makeDummyKml {
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

	public void writeKML(String ouputKML,String ntile,double[] koordinat ) throws IOException{
		try{
			//File in= new File(template);
			//File out= new File(par);
			System.out.println("Start void process");
			//File corners = new File(outputDirName+outputMakeDir+fileOutputName+".corners.txt");
			FileWriter fw = new FileWriter(ouputKML);
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

	public String[] createName(double X,double Y) throws IOException{
		String[] returnName=new String[2];
		String fileName=null;
		String folderName=null;
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
			//
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
		folderName=pole+latName+"E"+lonName;
		returnName[0]=folderName;
		returnName[1]=fileName;
		//		wCom.write("fileName="+pole+latName+"E"+lonName+"_"+tileName+"_"+satName+"_SSC_"+date+".tif");
		//		wCom.write("\n");

		//}
		return returnName;
	}
	public void makeTile(double[] oriLatLon) throws IOException{
		//double[] oriLatLon=points;

		double ulXTemp=oriLatLon[0];
		double ulYTemp=oriLatLon[1];
		double lrXTemp=oriLatLon[2];
		double lrYTemp=oriLatLon[3];

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
				String[] returnName=createName(ulXTemp,ulYTemp);

				new File("/media/hd/TSX_TB/tiles_indo2/"+returnName[0]).mkdir();

				String outputKML="/media/hd/TSX_TB/tiles_indo2/"+returnName[0]+"/"+returnName[1]+".kml";
				double[] koord=new double[4];
				koord[0]=ulCutX;
				koord[1]=ulCutY;
				koord[2]=lrCutX;
				koord[3]=lrCutY;

				writeKML(outputKML,returnName[1],koord);

				//				
				ulYTemp=Math.round((ulYTemp-0.1)*100.0);
				ulYTemp=ulYTemp/100.0;	
			}
			ulYTemp=oriLatLon[1];
			ulXTemp=Math.round((ulXTemp+0.1)*100.0);
			ulXTemp=ulXTemp/100.0;	

		}
	}
	public static void main (String[] a) throws IOException {
		double[] latlon=new double[4];
		latlon[0]=94.9;
		latlon[1]=5.9;
		latlon[2]=141.1;
		latlon[3]=-11.1;
		makeDummyKml makeKml=new makeDummyKml();
		makeKml.makeTile(latlon);
	}
}
