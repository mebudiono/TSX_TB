import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class multiWriteText {
	
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
	
//	public multiWriteText(String input1,String input2) {
//		this.mainFolderPath=input1;
//		this.codeProvInput=input2;
//	}

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
	public ArrayList<String> sortDate (ArrayList<String> foundTiles){
		//S08E138_43_TSX_EEC_180508T203016.tif
		//ArrayList<String> sortedTiles=new ArrayList<String> ();
		Collections.sort(foundTiles,new Comparator<String>(){
			public int compare(String str1, String str2) {
				String sample1=str1;
				String substr1="";
				//System.out.println(sample1);
				String[] sampleAr1=sample1.split("\\/");
				String sampleLast1=sampleAr1[sampleAr1.length-1];
				//System.out.println("sampleLast1="+sampleLast1);
				String year1 = sampleLast1.subSequence(19, 21).toString();
				String month1 = sampleLast1.subSequence(21, 23).toString();
				String date1 = sampleLast1.subSequence(23, 25).toString();


				//System.out.println("substr1="+substr1);

				String sample2=str2;
				String substr2="";
				//System.out.println(sample2);
				String[] sampleAr2=sample2.split("\\/");
				String sampleLast2=sampleAr2[sampleAr2.length-1];
				//System.out.println("sampleLast2="+sampleLast2);
				String year2 = sampleLast2.subSequence(19, 21).toString();
				String month2 = sampleLast2.subSequence(21, 23).toString();
				String date2 = sampleLast2.subSequence(23, 25).toString();


				//	System.out.println("substr2="+substr2);
				if(year1.equals(year2)) {
					if(month1.equals(month2)) {
						substr1=date1;
						substr2=date2;
					}
					else {
						substr1=month1;
						substr2=month2;
					}
				}			        
				else {
					substr1=year1;
					substr2=year2;
				}
				//System.out.println("Compare result="+Integer.valueOf(substr2).compareTo(Integer.valueOf(substr1)));
				return Integer.valueOf(substr1).compareTo(Integer.valueOf(substr2));
			}
		});

		return foundTiles;//sssssssssssssssssssssssssssssssssssssssssssssssssssssssss 

	}


	public void makeList(String mainFolderPath,String codeProvInput) throws IOException{
		
		//ArrayList<String> tilesAr=getTilesDB(codeProv);
		ArrayList<String> subTilesAr=getSubTilesDB(codeProvInput);
		ArrayList<String> foundTiles=new ArrayList<String>();
		ArrayList<String> resultTiles=new ArrayList<String>();
		try {
//			dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//			now = LocalDateTime.now();
			dumpResult=new FileWriter(new File("/media/hd/TSX_TB/makeListTilesLog/"+codeProvInput+"_"+codeProv+"_"+provName+".txt"));
//			dumpResult.write(dtf.format(now).toString());
//			dumpResult.write("\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
		//String path="/media/public80/Mosaik_TSX_TB/2017";
		String xmlFile="";
		//File[] filexml;
		for(int i=0;i<subTilesAr.size();i++) {
			String folderTile=subTilesAr.get(i).toString();
//		ArrayList<String> testTiles=new ArrayList<String>();
//		testTiles.add("S05E119_54");
//		testTiles.add("S06E120_43");
//		testTiles.add("S05E106_99");
//		for(int i=0;i<testTiles.size();i++) {
//		String folderTile=testTiles.get(i).toString();
			//String folderTile="S05E119_54";
			logTime.write("folderTile="+folderTile+"\n");
			System.out.println("folderTile="+folderTile);
			File[] filexml = Files.walk(Paths.get(mainFolderPath))
					.filter(p -> p.toString().contains(folderTile)).distinct()
					.filter(p -> p.toString().endsWith(".tif")).distinct()
					//.filter(p -> p.toString().matches(".xml")).distinct()
					.map(Path::toFile)
					.toArray(File[]::new);
			foundTiles=new ArrayList<String>();
			for(int j=0;j<filexml.length;j++) {
				xmlFile =filexml[j].toString();
				logTime.write("xmlFile["+j+"]="+xmlFile+"\n");
				foundTiles.add(xmlFile);
			}
			if(foundTiles.size()>1) {
				for(int k=0;k<foundTiles.size();k++) {
				resultTiles.add(sortDate(foundTiles).get(k));
				
				System.out.println("filterDate(foundTiles)="+sortDate(foundTiles).get(k));
			}}
			else {
				resultTiles.add(xmlFile);
				System.out.println("xmlFile="+xmlFile);
				//}
			}
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		

//		
//		try {
//			//String path="/media/public80/Mosaik_TSX_TB/2017";
//			String xmlFile="";
//			//File[] filexml;
//			for(int i=0;i<subTilesAr.size();i++) {
//				String folderTile=subTilesAr.get(i).toString();
//				System.out.println("folderTile="+folderTile);
//				File[] filexml = Files.walk(Paths.get(mainFolderPath))
//						.filter(p -> p.toString().contains(folderTile)).distinct()
//						.filter(p -> p.toString().endsWith(".tif")).distinct()
//						//.filter(p -> p.toString().matches(".xml")).distinct()
//						.map(Path::toFile)
//						.toArray(File[]::new);
//				for(int j=0;j<filexml.length;j++) {
//					xmlFile =filexml[j].toString();
//					foundTiles.add(xmlFile);
//				}
//				if(foundTiles.size()>1) {
//					resultTiles.add(filterDate(foundTiles));
//				}
//				else {
//					resultTiles.add(xmlFile);
//					//}
//				}
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		for(int z=0;z<resultTiles.size();z++) {
			logTime.write("resultTiles.get("+z+")="+resultTiles.get(z)+"\n");
			//
			dumpResult.write(resultTiles.get(z));
			dumpResult.write("\n");
		}
		//dumpResult.close();
		
//		for(int z=0;z<subTilesAr.size();z++) {
//			dumpResult.write(subTilesAr.get(z));
//			dumpResult.write("\n");
//		}
//		dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		now = LocalDateTime.now();
//		dumpResult.write(dtf.format(now).toString());
//		dumpResult.write("\n");
		dumpResult.close();
	}

	public ArrayList<String> getSubTilesDB (String idProvInput){
		String provCodeSub="";
		String provNameSub="";
		//String tiles="";
		String subTilesSub="";

		//ArrayList<String> tilesAr=new ArrayList<String>();
		ArrayList<String> subTilesAr=new ArrayList<String>();
		//String[] tilesAr=new String[];
		int result=100;
		int result2=100;
		boolean state=true;
		boolean state2=true;
		boolean stateFinal=true;
		try {
			//			String folderCheck = "SELECT count(*) FROM dbRecProc WHERE folderTSX=\""+folder+"\";";
			//			String statusCheck = "SELECT count(*) FROM dbRecProc WHERE status=\""+folder+"Done"+"\";";

			String getCode = "SELECT provCode,provName,subTiles FROM dbMosaicTiles WHERE id=\""+idProvInput+"\";";
			//	System.out.println(getCode);
			//String getName = "SELECT provName FROM dbMosaic WHERE id=\""+"05"+"\";";


			// db parameters
			String url       = "jdbc:mysql://localhost:3306/mosTB";
			String user      = "mosTB";
			String password  = "password";


			//
			//			String ulX="ulX";
			//			String ulY="ulY";
			//			String lrX="lrX";
			//			String lrY="lrY";
			//			String fol="folder";
			//			String insert="INSERT INTO dbase (id,folder,year,ulX,ulY,lrX,lrY,time) "
			//					+ "VALUES (NULL,\""+fol+"\","+ulX+","+ulY+","+lrX+","+lrY+","+dtf.format(now).toString()+"');";
			//			System.out.println(insert);
			//			String insertF="INSERT INTO dbase (id,folder) "
			//					+ "VALUES (NULL,Folder1);";
			//			System.out.println(insertF);
			// create a connection to the database
			connect = DriverManager.getConnection(url, user, password);

			statement = connect.createStatement();
			//statement.executeUpdate(folderCheck);
			ResultSet rFolder = statement.executeQuery(getCode);
			while (rFolder.next()) {
				codeProv=rFolder.getString(1);
				provName=rFolder.getString(2);
				//tiles=rFolder.getString(3);
				subTilesSub=rFolder.getString(3);
			}
			//			System.out.println(provCode);
			//			System.out.println(provName);
			//			System.out.println(tiles);
			//			System.out.println(subTiles);
			//			String[] splitTiles=tiles.split(",");
			//			for(int i=0;i<splitTiles.length;i++) {
			//				tilesAr.add(splitTiles[i]);
			//				//System.out.println(splitTiles[i]);
			//			}
			String[] splitSubTiles=subTilesSub.split(",");
			for(int i=0;i<splitSubTiles.length;i++) {
				subTilesAr.add(splitSubTiles[i]);
				//System.out.println(splitSubTiles[i]);
			}

			//			statement2 = connect.createStatement();
			//			ResultSet rFolder2 = statement2.executeQuery(getName);
			//
			//
			//			while (rFolder2.next()) {
			//				provName=rFolder2.getString(1);
			//			}
			//			System.out.println(provName);
			//rFolder2.close();
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
		return subTilesAr;


	}
	public static void main (String[] args) throws IOException{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String codeP="";
		logTime=new FileWriter(new File("/media/hd/TSX_TB/makeListTilesLog/log20190902BuatTilesMulti1.txt"));
		multiWriteText ui =new multiWriteText();
		//ui.frame.setVisible(true);
		for(int i=1;i<3;i++) {
			//dtf.format(now).toString()
			
			char[] stringLong=String.valueOf(i).toCharArray();
			if(stringLong.length==1) {
				codeP="0"+String.valueOf(i);
			}
			else {
				codeP=String.valueOf(i);
			}
			
			logTime.write("Start prov "+codeP+" ="+dtf.format(now).toString()+"\n");
			ui.makeList("/media/public80/Mosaik_TSX_TB/",codeP);
			dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			now = LocalDateTime.now();
			logTime.write("End prov "+"codeP"+" ="+dtf.format(now).toString()+"\n");
		}
		logTime.close();
	}
}
