import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class mosGuiWin extends JPanel {
	/**
	 * 
	 */

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

	//	public mosGui(String dirTiles,String dirOut) {
	//		this.dirTiles=dirTiles;
	//		this.dirOut=dirOut;
	//		//this.codeProv=codeProv;
	//		
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
				String[] sampleAr1=sample1.split("\\"+File.separator);
				String sampleLast1=sampleAr1[sampleAr1.length-1];
				//System.out.println("sampleLast1="+sampleLast1);
				String year1 = sampleLast1.subSequence(19, 21).toString();
				String month1 = sampleLast1.subSequence(21, 23).toString();
				String date1 = sampleLast1.subSequence(23, 25).toString();


				//System.out.println("substr1="+substr1);

				String sample2=str2;
				String substr2="";
				//System.out.println(sample2);
				String[] sampleAr2=sample2.split("\\"+File.separator);
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
				System.out.println("substr1="+substr1);
				System.out.println("substr2="+substr2);
				return Integer.valueOf(substr1).compareTo(Integer.valueOf(substr2));
			}
		});

		return foundTiles;//sssssssssssssssssssssssssssssssssssssssssssssssssssssssss 

	}


	public String[] getDateInfo (ArrayList<String> foundTiles){
		//S08E138_43_TSX_EEC_180508T203016.tif

		String[] sortDate=new String[2];
		Collections.sort(foundTiles,new Comparator<String>(){
			public int compare(String str1, String str2) {

				String sample1=str1;
				String substr1="";
				//System.out.println(sample1);
				String[] sampleAr1=sample1.split("\\"+File.separator);
				String sampleLast1=sampleAr1[sampleAr1.length-1];
				//System.out.println("sampleLast1="+sampleLast1);
				//System.out.println("sampleLast1="+sampleLast1);
				String year1 = sampleLast1.subSequence(19, 21).toString();
				String month1 = sampleLast1.subSequence(21, 23).toString();
				String date1 = sampleLast1.subSequence(23, 25).toString();


				//System.out.println("substr1="+substr1);

				String sample2=str2;
				String substr2="";
				//System.out.println(sample2);
				String[] sampleAr2=sample2.split("\\"+File.separator);
				String sampleLast2=sampleAr2[sampleAr2.length-1];
				System.out.println("sampleLast2="+sampleLast2);
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
				System.out.println("substr1="+substr1);
				System.out.println("substr2="+substr2);
				//System.out.println("Compare result="+Integer.valueOf(substr2).compareTo(Integer.valueOf(substr1)));
				return Integer.valueOf(substr1).compareTo(Integer.valueOf(substr2));
			}
		});
		String sample1=foundTiles.get(0);

		String[] sampleAr1=sample1.split("\\"+File.separator);
		String sampleLast1=sampleAr1[sampleAr1.length-1];
		//System.out.println("sampleLast1="+sampleLast1);
		String year1 = sampleLast1.subSequence(19, 21).toString();
		String month1 = sampleLast1.subSequence(21, 23).toString();
		String date1 = sampleLast1.subSequence(23, 25).toString();
		String oldest="20"+year1+month1+date1;
		sortDate[0]=oldest;
		String sample2=foundTiles.get(foundTiles.size()-1);

		String[] sampleAr2=sample2.split("\\"+File.separator);
		String sampleLast2=sampleAr2[sampleAr2.length-1];
		//System.out.println("sampleLast1="+sampleLast1);
		String year2 = sampleLast2.subSequence(19, 21).toString();
		String month2 = sampleLast2.subSequence(21, 23).toString();
		String date2 = sampleLast2.subSequence(23, 25).toString();
		String newest="20"+year2+month2+date2;
		sortDate[1]=newest;

		return sortDate;

	}




	public void makeList(String mainFolderPath,String outMos,String codeProvInput) throws IOException{
		provNameDB db=new provNameDB();
		codeProv=db.provN.get(codeProvInput);
		//String codeProvince=db.provT.get(codeProvInput);



		ArrayList<String> subTilesAr=getSubTiles(codeProvInput);
		ArrayList<String> foundTiles=new ArrayList<String>();
		ArrayList<String> resultTiles=new ArrayList<String>();
		concName=codeProvInput+"_"+codeProv;
		System.out.println("Prov ID="+concName);
		txtTiles=outMos+File.separator+concName+File.separator+concName+".txt";
		//listCommand=new ArrayList<String>();
		//listCommand.add("mkdir "+outMos+concName);
		new File(outMos+File.separator+concName).mkdir();
		try {
			dumpResult=new FileWriter(new File(txtTiles));
			//		} catch (IOException e1) {
			//			// TODO Auto-generated catch block
			//			e1.printStackTrace();
			//		}
			//		
			//		
			//		try {
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
				//logtimelogTime.write("folderTile="+folderTile+"\n");
				//System.out.println("folderTile="+folderTile);
				File[] filexml = Files.walk(Paths.get(mainFolderPath))
						.filter(p -> p.toString().contains(folderTile)).distinct()
						.filter(p -> p.toString().endsWith(".tif")).distinct()
						//.filter(p -> p.toString().matches(".xml")).distinct()
						.map(Path::toFile)
						.toArray(File[]::new);
				foundTiles=new ArrayList<String>();
				for(int j=0;j<filexml.length;j++) {
					xmlFile =filexml[j].toString();
					//logTime.write("xmlFile["+j+"]="+xmlFile+"\n");
					char[] xmlLong=xmlFile.toCharArray();
					System.out.println("xmlLong.length="+xmlLong.length);
					if(xmlLong.length>0) {
						System.out.println("xmlFile["+j+"]="+xmlFile);
						foundTiles.add(xmlFile);
					}
				}
				if(foundTiles.size()>1) {
					for(int k=0;k<foundTiles.size();k++) {

						resultTiles.add(sortDate(foundTiles).get(k));

						//System.out.println("filterDate(foundTiles)="+sortDate(foundTiles).get(k));
					}}
				else {
					resultTiles.add(xmlFile);
					//System.out.println("xmlFile="+xmlFile);
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
			//logTime.write("resultTiles.get("+z+")="+resultTiles.get(z)+"\n");
			//if()
			dumpResult.write(resultTiles.get(z));
			dumpResult.write("\n");
		}
		sortedDate=getDateInfo (resultTiles);
		dumpResult.close();
	}
	//	public void makeList(String mainFolderPath,String codeProvInput) throws IOException{
	//		ArrayList<String> subTilesAr=getSubTilesDB(codeProvInput);
	//		ArrayList<String> foundTiles=new ArrayList<String>();
	//		ArrayList<String> resultTiles=new ArrayList<String>();
	//		concName=codeProvInput+"_"+codeProv+"_"+provName;
	//		listCommand=new ArrayList<String>();
	//		listCommand.add("mkdir /media/public80/Mosaik_TSX/Mos_TSX_TB/"+concName);
	//		String result="";
	//		try {
	//			//Process p=new Process();
	//
	//			for (int d=0;d<listCommand.size();d++) {
	//				Runtime r = Runtime.getRuntime();                    
	//				Process p = r.exec(listCommand.get(d));	
	//				BufferedReader in =
	//						new BufferedReader(new InputStreamReader(p.getInputStream()));
	//				String inputLine;
	//				while ((inputLine = in.readLine()) != null) {
	//
	//					System.out.println(inputLine);
	//					result += inputLine;
	//				}
	//				in.close();
	//			}
	//		} catch (IOException e) {
	//			System.out.println(e);
	//		}
	//		//ArrayList<String> tilesAr=getTilesDB(codeProv);
	//		
	//		try {
	//			dumpResult=new FileWriter(new File("/media/hd/TSX_TB/listTilesProv/"+concName+".txt"));
	//		} catch (IOException e1) {
	//			// TODO Auto-generated catch block
	//			e1.printStackTrace();
	//		}
	//
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
	//		for(int z=0;z<resultTiles.size();z++) {
	//			dumpResult.write(resultTiles.get(z));
	//			dumpResult.write("\n");
	//		}
	//		dumpResult.close();
	//	}
	//


	public ArrayList<String> getSubTiles (String idProvInput){
		ArrayList<String> subTilesAr=new ArrayList<String>();
		provTilesDB db=new provTilesDB();
		String[] splitSubTiles=db.provT.get(idProvInput).split(",");
		for(int i=0;i<splitSubTiles.length;i++) {
			subTilesAr.add(splitSubTiles[i]);
			//System.out.println(splitSubTiles[i]);
		}
		return subTilesAr;

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



	public void makeMos(String mainFolderPath,String outMos,String codeProvInput) throws IOException {
		makeList(mainFolderPath,outMos,codeProvInput);
		listCommand=new ArrayList<String>();

		String tifOutName="LPN_TSX_GTCHH_"+sortedDate[0]+"_"+sortedDate[1]+"_MOS_"+codeProv;
		listCommand.add("gdalbuildvrt -input_file_list "+txtTiles+" "+outMos+concName+File.separator+tifOutName+".vrt");
		listCommand.add("gdalwarp -cutline W:\\marendra_temp\\TSX\\TSX_TB\\Intersection\\prov\\"+concName+".shp -crop_to_cutline "+outMos+concName+File.separator+tifOutName+".vrt "+outMos+concName+File.separator+tifOutName+".tif");
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
	public boolean cekBoxStat() {
		boolean stat=false;

		return stat;
	}
	public void exe() throws IOException{

		//	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		//			LocalDateTime now = LocalDateTime.now();
		String codeP="";
		//			logTime=new FileWriter(new File("/media/hd/TSX_TB/makeListTilesLog/log20190905_12-15.txt"));

		for(int i=0;i<selectedProv.size();i++) {
			//dtf.format(now).toString()

			char[] stringLong=String.valueOf(selectedProv.get(i)).toCharArray();
			if(stringLong.length==1) {
				codeP="0"+selectedProv.get(i);
			}
			else {
				codeP=selectedProv.get(i);
			}
			textAr.append("Start process");
			//				logTime.write("Start prov "+codeP+" ="+dtf.format(now).toString()+"\n");
			makeMos(dirTiles,dirOut,codeP);
			//				dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			//				now = LocalDateTime.now();
			//				logTime.write("End prov "+codeP+" ="+dtf.format(now).toString()+"\n");
		}
		//logTime.close();
	}
	private static final long serialVersionUID = 1L;
	protected JFrame frame;

	public static void main (String[] args) throws IOException{
		//		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		//		LocalDateTime now = LocalDateTime.now();
		//		String codeP="";
		//		logTime=new FileWriter(new File("/media/hd/TSX_TB/makeListTilesLog/log20190905_12-15.txt"));
		mosGuiWin mosUi =new mosGuiWin();
		mosUi.frame.setVisible(true);
		//		for(int i=12;i<16;i++) {
		//			//dtf.format(now).toString()
		//
		//			char[] stringLong=String.valueOf(i).toCharArray();
		//			if(stringLong.length==1) {
		//				codeP="0"+String.valueOf(i);
		//			}
		//			else {
		//				codeP=String.valueOf(i);
		//			}
		//
		//			logTime.write("Start prov "+codeP+" ="+dtf.format(now).toString()+"\n");
		//			ui.makeMos("/media/public80/Mosaik_TSX_TB/","/media/public80/Mosaik_TSX/Mos_TSX_TB/",codeP);
		//			dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		//			now = LocalDateTime.now();
		//			logTime.write("End prov "+codeP+" ="+dtf.format(now).toString()+"\n");
		//		}
		//		logTime.close();
	}

	public mosGuiWin(){
		initFrame();
	}
	//	@Override
	//	public void actionPerformed(ActionEvent arg0) {
	//		
	//	}
	public void initFrame(){
		gradient grad=new gradient();
		grad.setBounds(0, 0, 1000, 1000);
		String dirIn="";

		JTextField txtFld=new JTextField();
		txtFld.setEditable(false);
		JTextField txtFld2=new JTextField();
		txtFld2.setEditable(false);
		txtFld.setBounds(0, 30, 100, 30);
		txtFld2.setBounds(0, 90, 100, 30);
		JFrame.setDefaultLookAndFeelDecorated(true);
		// JFrame frame = new JFrame();
		frame = new JFrame("Mosaic TSX/TDX TileBased");
		frame.setSize(1000, 1000);
		frame.setLayout(new GridLayout(7, 1));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		JFileChooser inPath=new JFileChooser();
		JFileChooser outPath=new JFileChooser();

//		JPanel panel1=new JPanel();
//	    panel1.setLayout(new GridLayout(1, 2);
//	    JPanel panel2=new JPanel();
//	    panel2.setLayout(new GridLayout(2, 1));
//	    JPanel panel3=new JPanel();
//	    panel3.setLayout(new GridLayout(1, 1));
		
		
		JButton inPathBu=new JButton("Input");
		inPathBu.setBounds(120, 30, 100, 30);
		inPathBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==inPathBu) {
					inPath.setCurrentDirectory(new java.io.File("."));
					inPath.setDialogTitle("Open");
					inPath.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					inPath.setAcceptAllFileFilterUsed(false);
					if(inPath.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
						//dirIn=inPath.getSelectedFile().getPath();
						txtFld.setText(inPath.getSelectedFile().getPath());
						dirTiles=inPath.getSelectedFile().getPath();
					}
				}
			}
		});



		JButton outPathBu=new JButton("Output");
		outPathBu.setBounds(120, 90, 100, 30);
		outPathBu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==outPathBu) {
					outPath.setCurrentDirectory(new java.io.File("."));
					outPath.setDialogTitle("Open");
					outPath.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					outPath.setAcceptAllFileFilterUsed(false);
					if(outPath.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
						//dirIn=inPath.getSelectedFile().getPath();
						txtFld2.setText(outPath.getSelectedFile().getPath());
						dirOut=outPath.getSelectedFile().getPath();
					}
				}
			}
		});
		JCheckBox[] checkboxes=new JCheckBox[34];
		//checkboxes=new JCheckbox[35];
		String[] provinces={"ID-AC","ID-BA","ID-BT","ID-BE","ID-GO","ID-JK","ID-JA",
				"ID-JB","ID-JT","ID-JI","ID-KB","ID-KS",
				"ID-KT","ID-KI","ID-KU","ID-BB",
				"ID-KR","ID-LA","ID-MA","ID-MU","ID-NB",
				"ID-NT","ID-PA","ID-PB","ID-RI","ID-SR","ID-SN",
				"ID-ST","ID-SG","ID-SA","ID-SB","ID-SS",
				"ID-SU","ID-YO"};
		//System.out.println("lengthProvAr="+provinces.length);
		//String[] selectProv=new String[34];
		int boundX=0;
		int boundY=130;
		int boundW=100;
		int boundH=30;
		String codeP="";
		int provNum=0;

		for(int i=0;i<7;i++) {
			for(int j=0;j<5;j++) {
				if(i*j<24) {
					//char[] stringLong=String.valueOf(provNum).toCharArray();
					if(provNum<9) {
						codeP="0"+String.valueOf(provNum+1);
					}
					else {
						codeP=String.valueOf(provNum+1);
					}
					//final String selectCode=codeP;
					checkboxes[provNum]=new JCheckBox(codeP+"_"+provinces[provNum]);
					checkboxes[provNum].setBounds(boundX+j*150, boundY+i*30, boundW, boundH);
					//final int numFin=provNum;
					checkboxes[provNum].setOpaque(false);;
					frame.getContentPane().add(checkboxes[provNum]);

					//				checkboxes[provNum].addItemListener(new ItemListener() {
					//					//final int numFin=provNum;
					//					public void itemStateChanged(ItemEvent e) {
					//						selectProv[numFin]=selectCode;
					//						//		             statusLabel.setText("Mango Checkbox: " 
					//						//		             + (e.getStateChange()==1?"checked":"unchecked"));
					//					}
					//				});
					//}
					if(provNum<provinces.length-1) {
						provNum++;
					}
				}
			}
		}

		JCheckBox all=new JCheckBox("All"); 
		all.setBounds(600, 310, 100, 30);
		all.setOpaque(false);
		all.addItemListener(new ItemListener() {
			//final int numFin=provNum;
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 1) {
					for(int i=0;i<34;i++) {

						checkboxes[i].setSelected(true);
						checkboxes[i].setEnabled(false);
					}
				}
				else {
					for(int i=0;i<34;i++) {

						checkboxes[i].setSelected(false);
						checkboxes[i].setEnabled(true);
					}
				}
				//selectProv[numFin]=selectCode;
				//		             statusLabel.setText("Mango Checkbox: " 
				//		             + (e.getStateChange()==1?"checked":"unchecked"));
			}
		});



		textAr=new JTextArea();
		textAr.setBounds(110, 400, 300, 300);
		textAr.setLineWrap(true);


		JButton ok=new JButton("OK");
		//ok.setName(name);
		ok.setBounds(500, 600, 80, 30);
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				selectedProv=new ArrayList<String>();
				if(e.getSource()==ok) {
					if(txtFld.getText().isEmpty()) {
						JOptionPane.showMessageDialog(frame, "Please choose input folder first",
								"Input Error",JOptionPane.ERROR_MESSAGE);
					}
					else {
						if(txtFld2.getText().isEmpty()) {
							JOptionPane.showMessageDialog(frame, "Please choose output folder first",
									"Output Error",JOptionPane.ERROR_MESSAGE);
						}
						else {
							boolean statBox=false;
							for(int i=0;i<checkboxes.length;i++){
								statBox=statBox |checkboxes[i].isSelected();
								//statBox=statBox |statBox;

							}
							System.out.println("statBox="+statBox);
							if(statBox==false){
								JOptionPane.showMessageDialog(frame, "Please choose province first",
										"Province Error",JOptionPane.ERROR_MESSAGE);
							}

							else {
								//codesW=new String[5];
								//textAr.setText("");
								//textAr.replaceSelection("");
								String codeP="";
								String msg2="";
								for(int i=0;i<checkboxes.length;i++){
									if(checkboxes[i].isSelected()){
										if(i<9) {
											codeP="0"+String.valueOf(i+1);
										}
										else {
											codeP=String.valueOf(i+1);
										}
										selectedProv.add(codeP);
									}

									// msg+=checkboxes[0].getName(); 

								}
								for(int i=0;i<selectedProv.size();i++) {
									textAr.append(selectedProv.get(i));
								}
								textAr.append(dirTiles+" "+dirOut+" "+codeP);
								//mosGui proc=new mosGui(dirTiles,dirOut);
								try {
									exe();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}
					}
				}
			}
			//}
		});


		JPanel leftPane=new JPanel();
		JPanel rightPane=new JPanel(new BorderLayout());

		//leftPane
		JLabel lblNewLabel = new JLabel("Choose Dir Input");
		JLabel lblNewLabel2 = new JLabel("Choose Dir Output");
		lblNewLabel.setBounds(80, 0, 200, 20);
		lblNewLabel2.setBounds(80, 70, 200, 20);

		frame.getContentPane().add(lblNewLabel);
		frame.getContentPane().add(lblNewLabel2);
		frame.getContentPane().add(txtFld);
		frame.getContentPane().add(inPathBu);
		frame.getContentPane().add(txtFld2);
		frame.getContentPane().add(outPathBu);
		frame.getContentPane().add(textAr);
		frame.getContentPane().add(ok);
		frame.getContentPane().add(all);
		frame.getContentPane().add(grad);
	}



}