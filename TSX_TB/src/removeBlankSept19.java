import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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



public class removeBlankSept19 {
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
	public static FileWriter dumpResult,logTime;


	public boolean cekNull(String fileTif) {
		String result="";
		boolean statNul=false;
		//File tif=new File(fileTif);
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
					logTime.write(fileTif+" = "+lastString+"\n");
					
					statNul=true;
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}

		return statNul;

	}

	public ArrayList <String> listTif(String path) {
		ArrayList <String> resultTif=new ArrayList <String>();
		File[] filexml;

		try {
			filexml = Files.walk(Paths.get(path))
					//.filter(p -> p.toString().contains(folderTile)).distinct()
					.filter(p -> p.toString().endsWith(".tif")).distinct()
					//.filter(p -> p.toString().matches(".xml")).distinct()
					.map(Path::toFile)
					.toArray(File[]::new);

			resultTif=new ArrayList <String>();
			for(int j=0;j<filexml.length;j++) {

				xmlFile =filexml[j].toString();
				//logTime.write("xmlFile["+j+"]="+xmlFile+"\n");
				if(cekNull(xmlFile)==true) {
					resultTif.add(xmlFile);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultTif;

	}

	public void delNull(ArrayList <String> listTif) throws IOException {
		String result="";
		listCommand=new ArrayList<String>();
		for(int i=0;i<listTif.size();i++) {

				listCommand.add("rm "+listTif.get(i));
				logTime.write("rm "+listTif.get(i)+"\n");
		}
		try {
			//Process p=new Process();

			for (int e=0;e<listCommand.size();e++) {
				Runtime r = Runtime.getRuntime();                    
				Process p = r.exec(listCommand.get(e));	
				BufferedReader in =
						new BufferedReader(new InputStreamReader(p.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {

					System.out.println(inputLine);
					result += inputLine;
				}
				in.close();
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	public static void main(String[] args) throws IOException {
		//String mainPath="/media/public80/Mosaik_TSX_TB/";
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		
		logTime=new FileWriter(new File("/media/hd/TSX_TB/makeListTilesLog/log20190831RemoveNull.txt"));
		removeBlankSept19 rem =new removeBlankSept19();
		//ui.frame.setVisible(true);
//		for(int i=1;i<35;i++) {
//			//dtf.format(now).toString()
//			
//			char[] stringLong=String.valueOf(i).toCharArray();
//			if(stringLong.length==1) {
//				codeP="0"+String.valueOf(i);
//			}
//			else {
//				codeP=String.valueOf(i);
//			}
			
			logTime.write("Start Process at "+dtf.format(now).toString()+"\n");
			rem.delNull(rem.listTif("/media/public80/Mosaik_TSX_TB/"));
			dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			now = LocalDateTime.now();
			logTime.write("End prov "+"codeP"+" ="+dtf.format(now).toString()+"\n");
		//}
		logTime.close();
	}
	

}
