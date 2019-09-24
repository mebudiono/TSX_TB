import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InputTilesDB {
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



	public void getinfoProvinceID(String pathFolderProv){
		File aoiTxt=new File(pathFolderProv);
		StringBuffer provinceTemp=new StringBuffer();
		System.out.println("aoiTxt.getName()="+aoiTxt.getName());
		char[] provinceChar=aoiTxt.getName().toCharArray();
		for(int i=9;i<provinceChar.length;i++) {
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
			codeTemp.append(codeChar[i]);	
		}
		codeProv=codeTemp.toString();

		System.out.println("province="+provName);
		System.out.println("id="+idProv);
		System.out.println("code="+codeProv);
	}

	public void writeDB(String pathFolderProv) throws IOException {
		getinfoProvinceID(pathFolderProv);
		StringBuffer subTilesBuf=new StringBuffer();
		
		ArrayList<String> subTilesAr=new ArrayList<String>();
		File provDir=new File(pathFolderProv);
		for(int i=0;i<provDir.listFiles().length;i++) {
			String subTilesName=provDir.listFiles()[i].getAbsolutePath();
			System.out.println("subTilesName="+subTilesName);
			if(subTilesName.endsWith("_all.kml")){

			}
			else {

				String shpName="/media/hd/TSX_TB/Intersection/prov/"+idProv+"_"+codeProv+"_"+provName+".shp";
				System.out.println("shpName="+shpName);

				if(cekIntersect(shpName,subTilesName)==true) {
					File found=new File(subTilesName);
					StringBuffer subTilesNameGetFinal=new StringBuffer();
					String subTilesNameGet=found.getName();
					System.out.println("subTilesNameGet="+subTilesNameGet);
					char[] subTilesNameGetChar=subTilesNameGet.toCharArray();
					for(int k=0;k<subTilesNameGetChar.length-4;k++) {
						subTilesNameGetFinal.append(subTilesNameGetChar[k]);	
					}
					System.out.println("subTilesNameGetFinal="+subTilesNameGetFinal.toString());
					subTilesAr.add(subTilesNameGetFinal.toString());
				}

				
				//StringBuffer tilesBuf=new StringBuffer();
			}
		}
		for(int j=0;j<subTilesAr.size();j++) {
			subTilesBuf.append(subTilesAr.get(j).toString());
			subTilesBuf.append(",");
		}
		String subTilesDB=subTilesBuf.toString();

		//
		try {
			//
			String url       = "jdbc:mysql://localhost:3306/mosTB";
			String user      = "mosTB";
			String password  = "password";
			//		//
			//		//
			String insert="INSERT INTO dbMosaicTiles (number,id,provCode,provName,subTiles) "
					+ "VALUES (NULL,\""+idProv+"\",\""+codeProv+"\",\""+provName+"\",\""+subTilesDB+"\");";
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
			//		//			
			//		//
			System.out.println("Write start process to DB");
			//		// more processing here
			//		// ... 
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
	//		}
	//	}

	public boolean cekIntersect(String shpProv,String kmlTile) {
		String result="";

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


	public static void main(String[] ar) throws IOException {
		InputTilesDB exe=new InputTilesDB(); 

		File inputDir=new File("/media/hd/TSX_TB/ProvkmlTiles/");
		for(int i=0;i<inputDir.listFiles().length;i++) {
			//if(inputDir.listFiles()[i].getName().endsWith(".txt")) {
			exe.writeDB(inputDir.listFiles()[i].getAbsolutePath());
			//}
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

//}
