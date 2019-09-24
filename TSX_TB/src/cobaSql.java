import java.io.File;
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
import java.util.concurrent.TimeUnit;

public class cobaSql {
	private static Connection connect = null;
	private static Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;
	public static void main (String[] q) throws IOException, InterruptedException {
		try {
			String folderCheck = "SELECT count(*) FROM dbase WHERE folder=\"folder1\";";
			System.out.println(folderCheck);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			// db parameters
			String url       = "jdbc:mysql://localhost:3306/cobaDB";
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
			int result=100;
			while (rFolder.next()) {
				result=rFolder.getInt(1);
			}
			if(result==0) {
				System.out.println("OK");
			}
			else {
				System.out.println(result);
			}
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

//		String path="/media/public80/Mosaik_TSX_TB/2017";
//		String xmlFile="";
//		ArrayList<String> foundTiles=new ArrayList<String>();
//		//File[] filexml;
//		try {
//			File[] filexml = Files.walk(Paths.get(path))
//					.filter(p -> p.toString().contains("N00E117")).distinct()
//					.filter(p -> p.toString().endsWith(".tif")).distinct()
//					//.filter(p -> p.toString().matches(".xml")).distinct()
//					.map(Path::toFile)
//					.toArray(File[]::new);
//			for(int i=0;i<filexml.length;i++) {
//				xmlFile =filexml[i].toString();
//				//System.out.println("xmlFile="+xmlFile);
//				foundTiles.add(filexml[i].toString());
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		ArrayList<String> foundTiles=new ArrayList<String>();
//		foundTiles.add("/media/public80/Mosaik_TSX_TB/2018/S08E138/S08E138_72_TSX_EEC_170508T203016.tif");
//		foundTiles.add("/media/public80/Mosaik_TSX_TB/2018/S08E138/S08E138_73_TSX_EEC_180508T203016.tif");
//		foundTiles.add("/media/public80/Mosaik_TSX_TB/2018/S08E138/S08E138_80_TSX_EEC_180111T090014.tif");
//		foundTiles.add("/media/public80/Mosaik_TSX_TB/2018/S08E138/S08E138_81_TSX_EEC_170519T203013.tif");
//		foundTiles.add("/media/public80/Mosaik_TSX_TB/2018/S08E138/S08E138_82_TSX_EEC_180519T203013.tif");
//		for(int i=0;i<foundTiles.size();i++) {
//			System.out.println("before("+i+")="+foundTiles.get(i));}
//		String coba="20";
//		int cobaI=Integer.valueOf(coba);
//		//System.out.println("coba="+cobaI);
//
//		Collections.sort(foundTiles,new Comparator<String>(){
//			public int compare(String str1, String str2) {
//				String sample1=str1;
//				String substr1="";
//				//System.out.println(sample1);
//				String[] sampleAr1=sample1.split("\\/");
//				String sampleLast1=sampleAr1[sampleAr1.length-1];
//				//System.out.println("sampleLast1="+sampleLast1);
//				String year1 = sampleLast1.subSequence(19, 21).toString();
//				String month1 = sampleLast1.subSequence(21, 23).toString();
//				String date1 = sampleLast1.subSequence(23, 25).toString();
//
//
//				//System.out.println("substr1="+substr1);
//
//				String sample2=str2;
//				String substr2="";
//				//System.out.println(sample2);
//				String[] sampleAr2=sample2.split("\\/");
//				String sampleLast2=sampleAr2[sampleAr2.length-1];
//				//System.out.println("sampleLast2="+sampleLast2);
//				String year2 = sampleLast2.subSequence(19, 21).toString();
//				String month2 = sampleLast2.subSequence(21, 23).toString();
//				String date2 = sampleLast2.subSequence(23, 25).toString();
//
//
//			//	System.out.println("substr2="+substr2);
//				if(year1.equals(year2)) {
//					if(month1.equals(month2)) {
//						substr1=date1;
//						substr2=date2;
//					}
//					else {
//						substr1=month1;
//						substr2=month2;
//					}
//				}			        
//				else {
//					substr1=year1;
//					substr2=year2;
//				}
//				return Integer.valueOf(substr2).compareTo(Integer.valueOf(substr1));
//			}
//		});
//		for(int i=0;i<foundTiles.size();i++) {
//			System.out.println("after("+i+")="+foundTiles.get(i));}

		
		
		ArrayList<String> coba1=new ArrayList<String>();
		coba1.add("/media/public80/Mosaik_TSX_TB/2018/S08E138/S08E138_72_TSX_EEC_170508T203016.tif");
		//coba1.add("/media/public80/Mosaik_TSX_TB/2018/S08E138/S08E138_73_TSX_EEC_180508T203016.tif");
		System.out.println("coba1="+coba1.size());
		
		ArrayList<String> coba2=new ArrayList<String>();
		System.out.println("coba2="+coba2.size());
		
		
//		File cobaMakeDirFile= new File("/media/hd/TSX_TB/tesLogListTiles/");
//		if(cobaMakeDirFile.mkdir()==true) {
//			System.out.println("dirCreated");
//		}
		
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		int counter=1;
		while(counter<10) {
		
		System.out.println(counter+" ="+dtf.format(now).toString());
		TimeUnit.SECONDS.sleep(10);
		counter=counter++;
		dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		now = LocalDateTime.now();
		System.out.println(counter+" ="+dtf.format(now).toString());
		
		}
//		foundTiles.add("/media/public80/Mosaik_TSX_TB/2018/S08E138/S08E138_72_TSX_EEC_170508T203016.tif");
//		foundTiles.add("/media/public80/Mosaik_TSX_TB/2018/S08E138/S08E138_73_TSX_EEC_180508T203016.tif");
//		
		//		File[] filexml =Files.find(Paths.get(path),
		//				Integer.MAX_VALUE,
		//				(filePath, fileAttr) -> fileAttr.isDirectory())
		//				.filter(p -> p.toString().endsWith(".tif")).distinct()
		//				//.filter(p -> p.toString().matches(".xml")).distinct()
		//				.map(Path::toFile)
		//				.toArray(File[]::new);
		//		for(int i=0;i<filexml.length;i++) {
		//			xmlFile =filexml[i].toString();
		//			System.out.println("xmlFile="+xmlFile);
		//		}
	}	

}
//}