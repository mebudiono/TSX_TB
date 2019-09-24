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
		boolean state1=false;
		boolean state2=true;
		boolean state3=state1|state2;
		System.out.println(state3);


		char ch='0';
		int IRightMaxY=5;
		IRightMaxY=10-Character.getNumericValue(ch);

		if(IRightMaxY==10){
			IRightMaxY=0;
		}


		double dob=1.9;
		//Math.round((ulXTemp+0.1)*100.0)
		dob=Math.round((dob+0.1)*100.0);
		dob=dob/100.0;
		System.out.println("dob="+dob);

		String cobaSplit="TSX1_SAR-20171225T223912-HH.corners.txt";
		String[] separate=cobaSplit.split("\\.");
		for(int i=0;i<separate.length;i++) {
			System.out.println("separate="+separate[i]);
		}

		String foldName="TDX1_SAR__EEC_SE___HS_S_SRA_20171106T103504_20171106T103505";

		String reg="TSX1_SAR.*20171106T103504.*";

		if(foldName.matches(reg)) {
			System.out.println(foldName);
		}
		else {
			System.out.println("wrong");
		}

		char found='c';
		boolean yes=true;
		if(found=='d') {

			System.out.println(yes);
		}
		else {
			yes=false;
			System.out.println(yes);
		}

		String get="ghost";
		char[] getAr=get.toCharArray();
		if(getAr[getAr.length-1]=='t') {
			System.out.println("yes,t");
		}
		else {
			System.out.println("not 't'");
		}

		String tesName="TDX1_SAR__EEC_SE___HS_S_SRA_20171106T103504_20171106T103505";
		String tesName2="TDX1_SAR__EEC_SE___HS_S_SRA_20171106T103504_20171106T103505";
		if(tesName.equals(tesName2)) {
			System.out.println("equals");
		}
		else {
			System.out.println("not equals");
		}
		
		
		double[] latlon=new double[4];
		double ulXTemp=94.9;
		double ulYTemp=5.9;
		latlon[2]=141.1;
		latlon[3]=-11.1;
		double ulCutX=Math.round((ulXTemp-0.01)*100.0);
		ulCutX=ulCutX/100.0;
		//double cutMaxY=oriMaxY;sizeMinX
		double ulCutY=Math.round((ulYTemp+0.01)*100.0);
		ulCutY=ulCutY/100.0;

		double lrCutX=Math.round((ulXTemp+0.11)*100.0);
		lrCutX=lrCutX/100.0;
		double lrCutY=Math.round((ulYTemp-0.11)*100.0);
		lrCutY=lrCutY/100.0;
		
		System.out.println("ulCutX="+ulCutX+"\n"+"ulCutY="+ulCutY+"\n"+"lrCutX="+lrCutY+"\n"+"lrCutY="+lrCutY);
		String cobaTiles="N01E121_10,N01E121_11,N01E121_12,N01E121_13,N01E121_14,";
		String[] splitTiles=cobaTiles.split(",");
		for (int i=0;i<splitTiles.length;i++) {
			System.out.println(splitTiles[i]);
		}
		
	}
	
	
}
