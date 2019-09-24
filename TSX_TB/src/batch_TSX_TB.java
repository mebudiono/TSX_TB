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

public class batch_TSX_TB {
	public static ArrayList<String> listPathRemove,listCommand;
	public static String satName;
	public static String dateName;
	public static String tileName;
	public static String pathRemove,folderTSX;
	public static String sourceTSX="/media/tsr_lpn_std/2019";
	//public static String sourceTSX="/media/hd/TSX_TB/tes_S_N";
	public static String workDir="/media/hd/TSX_TB/workDir";
	public static String target="/media/public80/Mosaik_TSX_TB";
	//public static String workDir="/media/hd/TSX_TB/workDirTestEnv/";
	//public static String target="/media/hd/TSX_TB/target";
	private static Connection connect = null;
	private static Statement statement = null;
	private static Statement statement2 = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	private static runGamma2 procGamma;
	//public static ArrayList<String> listCommand;
	//public static void listSSC(){
	public static FileWriter writeLogDb;



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
	public static boolean cekDB(String folder) {
		int result=100;
		int result2=100;
		boolean state=true;
		boolean state2=true;
		boolean stateFinal=true;
		try {
			//			String folderCheck = "SELECT count(*) FROM dbRecProc WHERE folderTSX=\""+folder+"\";";
			//			String statusCheck = "SELECT count(*) FROM dbRecProc WHERE status=\""+folder+"Done"+"\";";

			String folderCheck = "SELECT count(*) FROM dbRecProc WHERE folderTSX=\""+folder+"\";";
			String statusCheck = "SELECT count(*) FROM dbRecProc WHERE status=\""+folder+"Done"+"\";";


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
			ResultSet rFolder = statement.executeQuery(folderCheck);
			while (rFolder.next()) {
				result=rFolder.getInt(1);
			}
			if(result==0) {
				state=true;
			}
			else {
				state=false;
			}
			//rFolder.close();

			statement2 = connect.createStatement();
			ResultSet rFolder2 = statement2.executeQuery(statusCheck);


			while (rFolder2.next()) {
				result2=rFolder2.getInt(1);
			}
			if(result2==0) {
				state2=true;
			}
			else {
				state2=false;
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
		
		stateFinal=state&state2;
		try {
			writeLogDb.write("stateFinal="+stateFinal);
			writeLogDb.write("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stateFinal;
	}
	public static void writeDB(String folderInput) {
		try {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			// db parameters
			String url       = "jdbc:mysql://localhost:3306/mosTB";
			String user      = "mosTB";
			String password  = "password";


			String insert="INSERT INTO dbRecProc (id,folderTSX,startTime) "
					+ "VALUES (NULL,\""+folderInput+"\",'"+dtf.format(now).toString()+"');";
			writeLogDb.write(insert);
			writeLogDb.write("\n");
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public static void appendDB(String folderInput) {
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
			List<String> nameSplit=new ArrayList<String>();
			nameSplit=Arrays.asList(sourceTSX.split("\\/"));
			String year=nameSplit.get(nameSplit.size()-1);

			String dateFile=procGamma.dateFile;

			String insert="UPDATE dbRecProc set fileName=\""+fol+"\", sceneDate='"+dateFile+"', year="+year+", ulX="+ulX+", ulY="+ulY+
					", lrX="+lrX+", lrY="+lrY+", ulXScene="+minX+", ulYScene="+maxY+", lrXScene="+maxX+", lrYScene="+minY+
					", endTime='"+dtf.format(now).toString()+"', status=\""+folderInput+"Done\" where folderTSX=\""+folderInput+"\";";
			//
			//,sceneDate,ulX,ulY,lrX,lrY,ulXScene,ulYScene,lrXScene,lrYScene,ulTile,lrTile,timeFinish+						+ "VALUES (NULL,\""+folderTSX+"\""+",\""+fol+"\",'"+dateFile+"',"+ulX+","+ulY+","+lrX+","+lrY+","+minX+","+maxY+","+maxX+","+minY+",'"+dtf.format(now).toString()+"');";
			writeLogDb.write(insert);
			writeLogDb.write("\n");
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		writeLogDb=new FileWriter(new File("/media/hd/TSX_TB/workDir/logDb.txt"));

		try {
			//Process p=new Process();
			//			writeDiffOut = new FileWriter(namePath+".diff_par.out");
			//			for (int f=0;f<listCommand.size();f++) {
			//				Runtime r = Runtime.getRuntime();                    
			//				Process p = r.exec(listCommand.get(f));	
			//				BufferedReader in =
			//						new BufferedReader(new InputStreamReader(p.getInputStream()));
			//				String inputLine;
			//				while ((inputLine = in.readLine()) != null) {
			//					writeDiffOut.write(inputLine);
			//					writeDiffOut.write("\n");
			//					System.out.println(inputLine);
			//					result += inputLine;
			//				}
			//				in.close();
			//			}


			File folder=new File(sourceTSX);
			//	FilenameFilter filter=new FilenameFilter(){
			//		@Override
			//		public boolean accept(File dir, String name){
			//			String str=name.contains("SSC");
			//			
			//		}
			//	}
			for(int i=0;i<folder.listFiles().length;i++){
				if(folder.listFiles()[i].isDirectory()){
					System.out.println("Start folder number="+i+" from "+folder.listFiles().length);
					if(folder.listFiles()[i].toString().contains("LPN")){
						File folderInside=new File(folder.listFiles()[i].getAbsolutePath());
						System.out.println("folder="+folder.listFiles()[i].getAbsolutePath().toString());
						System.out.println("folderInside="+folderInside.listFiles()[0].getAbsolutePath());
						if(folderInside.listFiles()[0].getName().contains("SAR__SSC______SM")) {
							if(folderInside.listFiles()[0].list().length>0) {
								if(cekDB(folderInside.listFiles()[0].getName())==true){

									//}
									//make command copy folder.listFiles()[i] to workDir/input
									//File input=new File("a");
									System.out.println("copyDir="+folderInside.listFiles()[0].getAbsolutePath());
									copyDir(folderInside.listFiles()[0].getAbsolutePath(),workDir+"/input");
									folderTSX=folderInside.listFiles()[0].getName();

									writeDB(folderTSX);

									runProcess(workDir,workDir+"/input/"+folderInside.listFiles()[0].getName(),target);

									appendDB(folderTSX);

								}
							}
						}
					}
					else {
						if(folder.listFiles()[i].toString().contains("SAR__SSC______SM")) {
							if(cekDB(folder.listFiles()[i].toString())==true){
								copyDir(folder.listFiles()[i].toString(),workDir+"/input");
								folderTSX=folder.listFiles()[i].getName();


								writeDB(folderTSX);

								runProcess(workDir,workDir+"/input/"+folder.listFiles()[i].getName().toString(),target);

								appendDB(folderTSX);


							}
						}
					}
				}
				System.out.println("Done ="+i+"folder from total"+folder.listFiles().length);
			}
			writeLogDb.close();
		} catch (IOException e) {
			System.out.println(e);
		}
		//removeFiles(pathRemove);
		
		//System.out.println("dateName="+dateName);
		//System.out.println(folder.listFiles()[i].getCanonicalPath());
		//		}
		//	}
	}
}

//}