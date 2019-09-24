import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class batchUpdateTSX_TB {
	public static ArrayList<String> listPathRemove,listCommand;
	public static String satName,targetFolder;
	public static String dateName;
	public static String tileName;
	public static String pathRemove,folderTSX;
	public static String sourceTSX="/media/tsr_lpn_std/2017";
	//public static String sourceTSX="/media/hd/TSX_TB/tes_S_N";
	public static String workDir="/media/hd/TSX_TB/workDir";
	public static String target="/media/public80/Mosaik_TSX_TB";
	//public static String workDir="/media/rossendra/01D23527E552C810/TSX/workDir";
	//public static String target="/media/hd/TSX_TB/target";
	private static Connection connect = null;
	private static Statement statement = null;
	private static Statement statement2 = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private static runGamma2 procGamma;
	//public static ArrayList<String> listCommand;
	//public static void listSSC(){
	public double parSRes=6,minX,minY,maxX,maxY,minXR,minYR,maxXR,maxYR;
	public int ulX,ulY,lrX,lrY;


	//	public batchUpdateTSX_TB(String targetFolder){
	//		this.targetFolder=targetFolder;
	//			}

	public   String getParam(String parPath,String regex,int column){
		BufferedReader reader;
		String found;
		String[] cut=new String[10];
		try {
			reader = new BufferedReader(new FileReader(parPath));
			String line = reader.readLine();
			while (line != null) {

				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(line);   // get a matcher object

				if (m.find()){

					cut=line.split("\\s+");

				}

				line = reader.readLine();

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return cut[column];
	}
	public int[] getCorners(String pathCornersTxt) throws IOException{



		maxY=Double.valueOf(getParam(pathCornersTxt,"min.*latitude*:*",7));
		if(maxY>=0){
			//ulY=Math.round(maxY * 10) / 10.0;
			String[] ulYStrAr=String.valueOf(maxY).split("\\.");
			//split the right part after "."
			char[] ulYStrArRight=ulYStrAr[1].toCharArray();
			int rightComma=Character.getNumericValue(ulYStrArRight[0]);


			ulY=rightComma;
		}



		maxX=Double.valueOf(getParam(pathCornersTxt,"min.*longitude*:*",7));
		if(maxX>=0){
			//lrX=Math.round(maxX * 10) / 10.0;
			String[] lrXStrAr=String.valueOf(maxX).split("\\.");
			//split the right part after "."
			char[] lrXStrArRight=lrXStrAr[1].toCharArray();
			int rightCommaMaxX=Character.getNumericValue(lrXStrArRight[0]);
			//combine left part before "." and 1 digit from right part to make ulX
			//			lrX=Double.valueOf(lrXStrAr[0].toString()+"."+String.valueOf(rightComma));
			//			Math.round((lrX+0.1)*100.0);
			lrX=rightCommaMaxX;
		}


		minY=Double.valueOf(getParam(pathCornersTxt,"min.*latitude*:*",3));
		if(minY>=0){
			//lrY=Math.round(minY * 10) / 10.0;
			String[] lrYStrAr=String.valueOf(minY).split("\\.");
			//split the right part after "."
			char[] lrYStrArRight=lrYStrAr[1].toCharArray();
			int rightCommaMinY=Character.getNumericValue(lrYStrArRight[0]);
			//combine left part before "." and 1 digit from right part to make ulX
			//			lrY=Double.valueOf(lrYStrAr[0].toString()+"."+String.valueOf(rightComma));
			//			Math.round((lrY+0.1)*100.0);
			lrY=rightCommaMinY;
		}


		int[] result={ulY,lrX,lrY};
		//		wCom.write("ulX="+ulX+","+"ulY="+ulY+","+"lrX="+lrX+","+"lrY="+lrY);
		//		wCom.write("\n");
		//result={minX,minY,maxX,maxY};
		//result
		return result;
	}




	public static void removeFiles(ArrayList<String> listPathToRemove){
		String result = null;
		listCommand=new ArrayList<String>();
		for(int a=0;a<listPathToRemove.size();a++){
			File target=new File(listPathToRemove.get(a));
			for(int i=0;i<target.listFiles().length;i++){
				if (target.listFiles()[i].toString().endsWith(".cmli.GTC.bin")){

				}
				else if(target.listFiles()[i].toString().endsWith(".cmli.GTC.hdr")){

				}
				else if(target.listFiles()[i].toString().endsWith(".corners.txt")){

				}
				else{

					listCommand.add("rm "+target.listFiles()[i].toString());
				}
			}
			try {
				//Process p=new Process();

				for (int b=0;b<listCommand.size();b++) {
					Runtime r = Runtime.getRuntime();                    
					Process p = r.exec(listCommand.get(b));	
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
	public static void runProcess(String jobDir,String outDir,String targetOutDir) throws IOException{
		procGamma=new runGamma2(jobDir ,outDir,targetOutDir);
		procGamma.go();
		//a.Step1(a.ListFolder(workDir));
	}
	public static void copyDir(String source,String target){
		String result = null;

		listCommand=new ArrayList<String>();
		listCommand.add("cp -rv "+source+" "+target);
		try {
			//Process p=new Process();

			for (int a=0;a<listCommand.size();a++) {
				Runtime r = Runtime.getRuntime();                    
				Process p = r.exec(listCommand.get(a));	
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
		//listCommand=new ArrayList<String>();
	}
	public static boolean cekDB(String fileName) {
		String result="";
		int result2=100;
		String state="";
		boolean state2=true;
		boolean needReproc=true;
		try {
			//String folderCheck = "SELECT count(*) FROM dbRecProc WHERE folderTSX=\""+folder+"\";";
			String statusCheck = "SELECT status FROM dbRecProc WHERE fileName=\""+fileName+"\";";
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

//			statement = connect.createStatement();
//			//statement.executeUpdate(folderCheck);
//			ResultSet rFolder = statement.executeQuery(folderCheck);
//			while (rFolder.next()) {
//				result=rFolder.getInt(1);
//			}
//			if(result==0) {
//				state=true;
//			}
//			else {
//				state=false;
//			}
			//rFolder.close();

			statement2 = connect.createStatement();
			ResultSet rFolder2 = statement2.executeQuery(statusCheck);


			while (rFolder2.next()) {
				result=rFolder2.getString(1);
			}
			char[] resultAr=result.toCharArray();
			char found=resultAr[resultAr.length-1];
			if(found=='c') {
				needReproc=false;
			}
			else {
				needReproc=true;
			}
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
		//needReproc=state2;
		//System.out.println("state="+state+" state2="+state2);
		System.out.println("needReproc="+needReproc);
		return needReproc;
	}
	public static void writeDB(String fileName) {
		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			// db parameters
			String url       = "jdbc:mysql://localhost:3306/mosTB";
			String user      = "mosTB";
			String password  = "password";


			String insert="UPDATE dbRecProc set startTime= '"+dtf.format(now).toString()+"' where fileName=\""+fileName+"\";";
			System.out.println(insert);
			//							String insertF="INSERT INTO dbase (id,folder) "
			//									+ "VALUES (NULL,Folder1);";
			//System.out.println(insertF);
			// create a connection to the database
			connect = DriverManager.getConnection(url, user, password);
			statement = connect.createStatement();
			statement.executeUpdate(insert);

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


	public static String getFolderFromDB(String fileName) {
		String result="";
		try {
			//			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			//			LocalDateTime now = LocalDateTime.now();
			// db parameters
			String url       = "jdbc:mysql://localhost:3306/mosTB";
			String user      = "mosTB";
			String password  = "password";


			String folderName="select folderTSX from dbRecProc where fileName=\""+fileName+"\";";
			System.out.println(folderName);
			//							String insertF="INSERT INTO dbase (id,folder) "
			//									+ "VALUES (NULL,Folder1);";

			// create a connection to the database
			connect = DriverManager.getConnection(url, user, password);

			statement = connect.createStatement();
			//statement.executeUpdate(folderCheck);
			ResultSet rFolder = statement.executeQuery(folderName);
			while (rFolder.next()) {
				result=rFolder.getString(1);
				
			}
			System.out.println("result="+result);
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
		//rFolder.close();
		return result;
	}


	public static void appendDB(String fileName) {
		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			// db parameters
			String url       = "jdbc:mysql://localhost:3306/mosTB";
			String user      = "mosTB";
			String password  = "password";

			double ulX=procGamma.ulX;
			double ulY=procGamma.ulY;
			double lrX=procGamma.lrX;
			double lrY=procGamma.lrY;

			double minX=procGamma.minX;
			double maxY=procGamma.maxY;
			double maxX=procGamma.maxX;
			double minY=procGamma.minY;

			String fol=procGamma.fileOutputName;
			List<String> nameSplit=new ArrayList<String>() ;
			nameSplit=Arrays.asList(sourceTSX.split("\\/"));
			String year=nameSplit.get(nameSplit.size()-1);

			String dateFile=procGamma.dateFile;

			String insert="UPDATE dbRecProc set sceneDate='"+dateFile+"', year="+year+", ulX="+ulX+", ulY="+ulY+
					", lrX="+lrX+", lrY="+lrY+", ulXScene="+minX+", ulYScene="+maxY+", lrXScene="+maxX+", lrYScene="+minY+
					", endTime='"+dtf.format(now).toString()+"', status=\""+getFolderFromDB(fileName)+"DoneReproc\" where fileName=\""+fileName+"\";";
			//
			//,sceneDate,ulX,ulY,lrX,lrY,ulXScene,ulYScene,lrXScene,lrYScene,ulTile,lrTile,timeFinish+						+ "VALUES (NULL,\""+folderTSX+"\""+",\""+fol+"\",'"+dateFile+"',"+ulX+","+ulY+","+lrX+","+lrY+","+minX+","+maxY+","+maxX+","+minY+",'"+dtf.format(now).toString()+"');";
			System.out.println(insert);
			//				String insertF="INSERT INTO dbase (id,folder) "
			//						+ "VALUES (NULL,Folder1);";
			//System.out.println(insertF);
			// create a connection to the database
			connect = DriverManager.getConnection(url, user, password);
			statement = connect.createStatement();
			statement.executeUpdate(insert);

			System.out.println("Written to DB");
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
	public static void main (String[] a) throws IOException{
		File folder=new File(sourceTSX);
		batchUpdateTSX_TB check=new batchUpdateTSX_TB();

		File folderWork=new File("/media/hd/TSX_TB/workDir/output/");
		//	FilenameFilter filter=new FilenameFilter(){
		//		@Override
		//		public boolean accept(File dir, String name){
		//			String str=name.contains("SSC");
		//			
		//		}
		//	}
		String fileName;
		String[] namaTxt;
		File fileCornersTxt;
		int[] errorPts=new int [3] ;
		int total=0;

		for(int i=0;i<folderWork.listFiles().length;i++){
			if(folderWork.listFiles()[i].isDirectory()){
				//System.out.println("Start folder number="+i+" from "+folder.listFiles().length);
				File filesInside=new File(folderWork.listFiles()[i].getAbsolutePath());
				//System.out.println("folder="+folder.listFiles()[i].getAbsolutePath().toString());
				for(int j=0;j<filesInside.listFiles().length;j++){
					if(filesInside.listFiles()[j].getName().contains(".corners.txt")) {
						namaTxt=filesInside.listFiles()[j].getName().split("\\.");
						//for(int l=0;l<namaTxt.length;l++) {
						//String[] fileTxtSplit=namaTxt[0].split("-");
						fileName=namaTxt[0];
						errorPts=check.getCorners(filesInside.listFiles()[j].getAbsolutePath().toString());
						if(errorPts[0]==9||errorPts[1]==9||errorPts[2]==9) {

							System.out.println("Start folder number="+total+" from 313");
							String folderTarget=getFolderFromDB(fileName);
							System.out.println("folderTarget="+folderTarget);
							System.out.println("fileName="+fileName);
							if(cekDB(fileName)==true){
								for(int k=0;k<folder.listFiles().length;k++){
									if(folder.listFiles()[k].isDirectory()){						
										if(folder.listFiles()[k].toString().contains("LPN")){
											File folderInside=new File(folder.listFiles()[k].getAbsolutePath());
											
											//System.out.println("nameFold="+folderInside.listFiles()[0].getName());
											//System.out.println("folder="+folder.listFiles()[k].getAbsolutePath().toString());
											//System.out.println("folderInside="+folderInside.listFiles()[0].getAbsolutePath());
											if(folderInside.listFiles()[0].getName().equals(folderTarget)) {
												//if(folderInside.listFiles()[0].list().length>0) {
												System.out.println("folderInside="+folderInside);
												System.out.println("nameFold="+folderInside.listFiles()[0].getName());
												System.out.println("copyDir="+folderInside.listFiles()[0].getAbsolutePath());
												copyDir(folderInside.listFiles()[0].getAbsolutePath(),workDir+"/input");
												folderTSX=folderInside.listFiles()[0].getName();

												writeDB(fileName);

												runProcess(workDir,workDir+"/input/"+folderInside.listFiles()[0].getName(),target);

												appendDB(fileName);
												total++;
												k=folder.listFiles().length;
												//}
											}
											
										}
									}
									
								}

							}

						}
					}
				}
				System.out.println("Done folder number="+total+" from 313");
				//total++;
			}
			System.out.println("total="+total);
			//}






			//	FilenameFilter filter=new FilenameFilter(){
			//		@Override
			//		public boolean accept(File dir, String name){
			//			String str=name.contains("SSC");
			//			
			//		}
			//	}


			//removeFiles(pathRemove);
			//System.out.println("Done ="+i+"folder from total"+folder.listFiles().length);
			//System.out.println("dateName="+dateName);
			//System.out.println(folder.listFiles()[i].getCanonicalPath());
			//		}
			//	}
		}
	}
}
//}