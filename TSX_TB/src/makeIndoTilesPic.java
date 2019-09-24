import java.io.BufferedReader;
import java.io.File;
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
import java.util.ArrayList;

import javax.swing.JTextArea;

public class makeIndoTilesPic {
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


	public FileWriter writeDiffOut,wCom,wList;


	public String provName,idProv,codeProv,concName,txtTiles,dirTiles,dirOut;


	public ArrayList<String> listCommand,selectedProv;
	//public JFileChooser jC;
	private static Connection connect = null;
	private static Statement statement = null;
	private static Statement statement2 = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	String[] sortedDate;
	public static FileWriter dumpResult,logTime;
	public JTextArea textAr;


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

					//System.out.println(inputLine);
					result += inputLine;
					lastString=inputLine;
				}
				//System.out.println("lastString cekIntersect="+lastString);
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


	public void makeList(String provTilesFolder,String shpFolder,String outKmlProv,String codeProvInput) throws IOException{
		provNameDB db=new provNameDB();
		codeProv=db.provN.get(codeProvInput);
		System.out.println("codeProv="+codeProv);
		concName=codeProvInput+"_"+codeProv;
		System.out.println("concName="+concName);
		String shpName=shpFolder+concName+".shp";
		System.out.println("shpName="+shpName);

		//list all kml in prov Folder kml
		ArrayList<String> indoTiles=new ArrayList<String>();

		File[] filexml = Files.walk(Paths.get(provTilesFolder+concName))
				//.filter(p -> p.toString().contains(folderTile)).distinct()
				.filter(p -> p.toString().endsWith(".kml")).distinct()
				//.filter(p -> p.toString().matches(".xml")).distinct()
				.map(Path::toFile)
				.toArray(File[]::new);
		//indoTiles=new ArrayList<String>();
		for(int j=0;j<filexml.length;j++) {
			xmlFile =filexml[j].toString();
			if(xmlFile.endsWith("_all.kml")) {

			}
			//cek tiap tiles intersect with shp
			if(cekIntersect(shpName,xmlFile)==true){

				indoTiles.add(xmlFile);
			}
		}

		for(int i=0;i<indoTiles.size();i++) {
			listCommand=new ArrayList<String>();
			String provFolderName=outKmlProv+concName+"/";
			System.out.println("provFolderName="+provFolderName);
			listCommand.add("mkdir "+provFolderName);
			listCommand.add("cp "+indoTiles.get(i)+" "+provFolderName);
			String result="";
			try {
				//Process p=new Process();

				for (int d=0;d<listCommand.size();d++) {
					Runtime r = Runtime.getRuntime();                    
					Process p = r.exec(listCommand.get(d));	
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

	}


public static void main (String[] ar) throws IOException {
	makeIndoTilesPic proc =new makeIndoTilesPic();
	String provTilesFolder="/media/hd/TSX_TB/ProvkmlTiles/";
	String shpFolder="/media/hd/TSX_TB/Intersection/prov/";
	String outKmlProv="/media/hd/TSX_TB/indoProvTiles/";
	String codeProvInput="";
	int j=0;
	for(int i=32;i<34;i++) {

		if(i<9) {
			j=i+1;

			codeProvInput="0"+String.valueOf(j);
		}
		else {
			j=i+1;

			codeProvInput=String.valueOf(j);
		}
		proc.makeList(provTilesFolder,shpFolder,outKmlProv,codeProvInput);
	}

}

}