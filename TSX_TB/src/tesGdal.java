import java.io.File;

public class tesGdal {
	public static void main (String[] ar) {
		//	String result = null;
		//String Command="gdal_translate -a_nodata 0 -projwin 107.36180603 -2.53289633 107.66460915 -2.68438522 "
		//		+ "/media/hd/TSX_TB/workDir/output/TDX1_SAR__SSC______SM_S_SRA_20180402T110227_20180402T110229/TDX1_SAR-20180402T110229-HH.cmli.GTC.bin"
		//		+ " /media/hd/TSX_TB/workDir/output/TDX1_SAR__SSC______SM_S_SRA_20180402T110227_20180402T110229/TDX1_SAR-20180402T110229-HH.cmli.GTC.Tiff";
		//	try {
		//		//Process p=new Process();
		//
		//		//for (int e=0;e<listCommand.size();e++) {
		//			Runtime r = Runtime.getRuntime();                    
		//			Process p = r.exec(Command);	
		//			BufferedReader in =
		//					new BufferedReader(IRightMaxYnew InputStreamReader(p.getInputStream()));
		//			String inputLine;
		//			while ((inputLine = in.readLine()) != null) {
		//
		//				System.out.println(inputLine);
		//				result += inputLine;
		//			}
		//			in.close();
		//		//}
		//	} catch (IOException e) {
		//		System.out.println(e);
		//	}

		//double a=-2.68438522;
		//double b=a-0.11;
		//DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		//LocalDateTime now = LocalDateTime.now();
		//System.out.println(dtf.format(now)); //2016/11/16 12:08:43
		//System.out.println(b);
		//	try {
		//		DateTimeFormatter dtf = DateTimeFormatter.ofPattIRightMaxYern("yyyy-MM-dd HH:mm:ss");
		//		LocalDateTime now = LocalDateTime.now();
		//		// db parameters
		//		String url       = "jdbc:mysql://localhost:3306/cobaDB";
		//		String user      = "mosTB";
		//		String password  = "password";
		//
		//
		//		String insert="INSERT INTO dbRecProc (id,folderTSX,startTime) "
		//				+ "VALUES (NULL,\""+folderTSX+"\",'"+dtf.format(now).toString()+"');";
		//		System.out.println(insert);
		////		String insertF="INSERT INTO dbase (id,folder) "
		////				+ "VALUES (NULL,Folder1);";
		//		//System.out.println(insertF);
		//		// create a connection to the database
		//		connect = DriverManager.getConnection(url, user, password);
		//		statement = connect.createStatement();
		//		statement.executeUpdate(insert);
		//		
		//		System.out.println("Write start process to DB");
		//		// more processing here
		//		// ... 
		//	} catch(SQLException e) {
		//		System.out.println(e.getMessage());
		//	} finally {
		//		try{
		//			if(connect !=null) {
		//				connect.close();
		//			}
		//		}
		//		catch(SQLException ex){
		//			System.out.println(ex.getMessage());
		//		}
		//	}
	File mainFold=new File("F:\\2019\\TSX_TB\\tesError");
	File[] mainFoldCont=mainFold.listFiles(); 
		String Err=mainFoldCont[0].getAbsolutePath();
		System.out.println("Err="+Err);
		String[] sampleAr2=Err.split("\\"+File.separator);
		String sampleLast2=sampleAr2[sampleAr2.length-1];
		for(int i=0;i<sampleAr2.length;i++) {
			System.out.println("sampleAr2["+i+"]="+sampleAr2[i]);
		}
	}
	
	
}
