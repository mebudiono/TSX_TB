

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//###########################################################################
//###        ###
//###                      ###                                
//###########################################################################
//### 
//### 
//### 
//###
//###########################################################################
public class runGamma {
	public String workDir,cornersTxt,pole,year,date,satName,latName,lonName,tileName,targetDir;
	public String inputDirName;//=workDir+"/input";//="/media/public80/SAR_smosaik_prov/2019/sulbar/input_EEC_cal_sample/";
	public String outputDirName;//=workDir+"/output";
	public String demPath;//=workDir+"/DEMIND/indonesia_SRTM30_rev1.tif";
	public String geoDir="/usr/local/GAMMA_SOFTWARE-20181130/GEO/bin/";
	public String scriptDir="/usr/local/scripts/";
	public String dispDir="/usr/local/GAMMA_SOFTWARE-20181130/DISP/bin/";
	public String parTemplate,pathFolder;//=workDir+"/diff_par_template";
	//public  String outputDir="/media/Data/TSX/OUTPUT/";
	public  int foundDIrInput;
	protected String outputMakeDir,mlirs,fileOutputName,xmlFile,cosFile,namePath;
	public double parSRes=6,minX,minY,maxX,maxY;
	public  ArrayList<String> listDir,listXML ;
	public ArrayList<String> listCommand;
	public ArrayList<String> dapetXML;
	public ArrayList<String> dapetCOS;
	public Integer dem_width,mli_width;
	public FileWriter writeDiffOut,wCom;
	//public ArrayList<String> outputMakeDir;
	public runGamma(String workDir,String jobDir,String targetDir){
		this.workDir=workDir;
		this.inputDirName=jobDir;
		this.targetDir=targetDir;
		//Step1(a.ListFolder(workDir));
	}

	//List folder inside inputDirName, convert the name and find xml and cosar,
	public  ArrayList<String> ListFolder (String path){
		listDir=new ArrayList<String>(); 

		File dir=new File(path);
		File[] isiDir=dir.listFiles();


		for(int i=0;i<isiDir.length;i++) {
			if(isiDir[i].isDirectory()&&isiDir[i].exists()) {
				System.out.printf("Dir isiDir%d=%s%n",i,isiDir[i].toString());
				listDir.add(isiDir[i].toString());
			}
		}
		return listDir;

	}
	public  void go() throws IOException {
		//runGamma ins=new runGamma(workDir);
		//		//String path="/media/rossendra/01D23527E552C810/TSX/OUTPUT_TES/TDX1_SAR__SSC______SM_S_SRA_20180308T101947_20180308T101957/TDX1_SAR-20180308T101957-HH.corners.txt";
		//		//String demPath="/media/rossendra/01D23527E552C810/TSX/OUTPUT_TES/TDX1_SAR__SSC______SM_S_SRA_20180308T101947_20180308T101957/TDX1_SAR-20180308T101957-HH.dem.par";
		//
		//ins.Step1(ins.ListFolder(workDir+"/input"));
		//inputDirName=workDir;
		//="/media/public80/SAR_smosaik_prov/2019/sulbar/input_EEC_cal_sample/";
		//targetDi
		outputDirName=workDir+"/output";
		demPath=workDir+"/DEMIND/indonesia_SRTM30_rev1.tif";
		parTemplate=workDir+"/diff_par_template";
		Step1(inputDirName);
	}
	public  void Step1 (String folderName) throws IOException{
		String result = null;
		//for (int i=0;i<listDir.size();i++){
		listCommand=new ArrayList<String>();
		extractSAR(folderName);
		System.out.println("folderName="+folderName);
		//System.out.println("listDir.size()="+listDir.size());
		//System.out.println("listDir.get("+i+").toString()"+listDir.get(i).toString());
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
		listCommand=new ArrayList<String>();
		try {
			mliParam();

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
		String parPath=namePath+".mli.par";
		//
		//
		int satu=1;
		//
		mlirs=getParam(parPath,"range_samples*",satu);
		listCommand=new ArrayList<String>();

		listCommand.add(geoDir+"SLC_corners "+namePath+".slc.par");
		try {
			//Process p=new Process();

			for (int c=0;c<listCommand.size();c++) {
				Runtime r = Runtime.getRuntime();                    
				Process p = r.exec(listCommand.get(c));	
				BufferedReader in =
						new BufferedReader(new InputStreamReader(p.getInputStream()));
				//BufferedWriter bw = null;
				String inputLine;
				cornersTxt=namePath+".corners.txt";
				File corners = new File(cornersTxt);
				FileWriter fw = new FileWriter(corners);
				//bw = new BufferedWriter(fw);
				while ((inputLine = in.readLine()) != null) {



					fw.write(inputLine);
					fw.write("\n");
					System.out.println(inputLine);
					result += inputLine;



				}
				in.close();
				fw.close();
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		listCommand=new ArrayList<String>();
		geoCode1();
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

		listCommand=new ArrayList<String>();
		geoCode2();
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

		listCommand=new ArrayList<String>();
		geoCode3();
		writeDummyDiff coba=new writeDummyDiff(namePath+".mli.par",
				namePath+".diff_par",parTemplate);
		coba.process();
		try {
			//Process p=new Process();

			for (int f=0;f<listCommand.size();f++) {
				Runtime r = Runtime.getRuntime();                    
				Process p = r.exec(listCommand.get(f));	
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
		listCommand=new ArrayList<String>();
		geoCode32();
		try {
			//Process p=new Process();
			writeDiffOut = new FileWriter(namePath+".diff_par.out");
			for (int f=0;f<listCommand.size();f++) {
				Runtime r = Runtime.getRuntime();                    
				Process p = r.exec(listCommand.get(f));	
				BufferedReader in =
						new BufferedReader(new InputStreamReader(p.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					writeDiffOut.write(inputLine);
					writeDiffOut.write("\n");
					System.out.println(inputLine);
					result += inputLine;
				}
				in.close();
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		listCommand=new ArrayList<String>();
		geoCode4();
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
		listCommand=new ArrayList<String>();
		geoCode5();
		try {
			//Process p=new Process();
			//FileWriter fw = new FileWriter(namePath+".diff_par.out");
			for (int e=0;e<listCommand.size();e++) {
				Runtime r = Runtime.getRuntime();                    
				Process p = r.exec(listCommand.get(e));	
				BufferedReader in =
						new BufferedReader(new InputStreamReader(p.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					writeDiffOut.write(inputLine);
					writeDiffOut.write("\n");
					System.out.println(inputLine);
					result += inputLine;
				}
				in.close();

				writeDiffOut.close();
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		listCommand=new ArrayList<String>();
		geoCode6();
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
		listCommand=new ArrayList<String>();
		geoCode7();
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
		listCommand=new ArrayList<String>();
		geoCode8();
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
		listCommand=new ArrayList<String>();
		geoCode9();
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
		listCommand=new ArrayList<String>();
		removeFile();
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
		listCommand=new ArrayList<String>();
		makeTile(getCorners(cornersTxt));
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
		
		//listCommand=new ArrayList<String>();
		String Command="rm -rf "+pathFolder;
		try {
			//Process p=new Process();

			//for (int e=0;e<listCommand.size();e++) {
				Runtime r = Runtime.getRuntime();                    
				Process p = r.exec(Command);	
				BufferedReader in =
						new BufferedReader(new InputStreamReader(p.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {

					System.out.println(inputLine);
					result += inputLine;
				}
				in.close();
			//}
		} catch (IOException e) {
			System.out.println(e);
		}
		Command="rm -rf "+inputDirName;
		try {
			//Process p=new Process();

			//for (int e=0;e<listCommand.size();e++) {
				Runtime r = Runtime.getRuntime();                    
				Process p = r.exec(Command);	
				BufferedReader in =
						new BufferedReader(new InputStreamReader(p.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {

					System.out.println(inputLine);
					result += inputLine;
				}
				in.close();
			//}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	//}
	public void removeFile(){
		//listCommand=new ArrayList<String>();
		//for(int a=0;a<listDir.size();a++){
		File target=new File(pathFolder);
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
		//}
	}
	public void geoCode1() throws IOException{



		double minLat=Double.valueOf(getParam(namePath+".corners.txt","min.*latitude*:*",3))-0.05;
		double maxLat=Double.valueOf(getParam(namePath+".corners.txt","min.*latitude*:*",7))+0.05;
		double minLon=Double.valueOf(getParam(namePath+".corners.txt","min.*longitude*:*",3))-0.05;
		double maxLon=Double.valueOf(getParam(namePath+".corners.txt","min.*longitude*:*",7))+0.05;

		wCom.write(scriptDir+"srtm_gamma2 "+demPath+" "
				+namePath+".dem.bin "+minLon+" "+maxLat+" "+maxLon+" "+minLat);
		wCom.write("\n");

		listCommand.add(scriptDir+"srtm_gamma2 "+demPath+" "
				+namePath+".dem.bin "+minLon+" "+maxLat+" "+maxLon+" "+minLat);

	}

	public void geoCode2() throws IOException{


		double demGrid=Double.valueOf(getParam(namePath+".dem.par","post_lon:*",1))*1852*60;
		double ovr=demGrid/parSRes;

		wCom.write(geoDir+"gc_map "+namePath+".mli.par - "
				+namePath+".dem.par "+namePath+".dem.bin "
				+namePath+".demseg.par "+
				namePath+".demseg.bin "+namePath+".lut "+ovr+" "+ovr+" "
				+namePath+".sim.bin "+namePath+".zen.bin "+
				namePath+".ori.bin - - - - 8 2");
		wCom.write("\n");


		listCommand.add(geoDir+"gc_map "+namePath+".mli.par - "
				+namePath+".dem.par "+namePath+".dem.bin "
				+namePath+".demseg.par "+
				namePath+".demseg.bin "+namePath+".lut "+ovr+" "+ovr+" "
				+namePath+".sim.bin "+namePath+".zen.bin "+
				namePath+".ori.bin - - - - 8 2");

	}
	public void geoCode3() throws IOException{


		mli_width=Integer.valueOf(getParam(namePath+".mli.par","range_samples:*",1));
		Integer mli_lines=Integer.valueOf(getParam(namePath+".mli.par","azimuth_lines:*",1));
		dem_width=Integer.valueOf(getParam(namePath+".demseg.par","width:*",1));
		//double dem_lines=Double.valueOf(getParam(namePath+".demseg.par","nlines:*",1));

		wCom.write(geoDir+"geocode "+namePath+".lut "
				+namePath+".sim.bin "+dem_width+" "+namePath+".sim_rdc.bin "
				+mli_width+" "+mli_lines+" 0 0");
		wCom.write("\n");
		//geocode $lut $simsar $dem_width $simsar_rdc $mli_width $mli_lines 0 0
		listCommand.add(geoDir+"geocode "+namePath+".lut "
				+namePath+".sim.bin "+dem_width+" "+namePath+".sim_rdc.bin "
				+mli_width+" "+mli_lines+" 0 0");
	}

	public void geoCode32() throws IOException{
		wCom.write(geoDir+"init_offsetm "+namePath+".sim_rdc.bin "
				+namePath+".gamma.bin "
				+namePath+".diff_par - - - - - - 0.15 1024 1");
		wCom.write("\n");
		//	init_offsetm $simsar_rdc $gamma.bin $diff_par - - - - - - $coreg_snr 1024 1 > $diff_par.out

		listCommand.add(geoDir+"init_offsetm "+namePath+".sim_rdc.bin "
				+namePath+".gamma.bin "
				+namePath+".diff_par - - - - - - 0.15 1024 1");
	}
	public void geoCode4() throws IOException{
		wCom.write(geoDir+"offset_pwrm "+namePath+".sim_rdc.bin "
				+namePath+".mli.bin "+namePath+".diff_par "
				+namePath+".offs "+namePath+".snr 128 128 "
				+namePath+".offsets 1 64 64 -");
		wCom.write("\n");


		listCommand.add(geoDir+"offset_pwrm "+namePath+".sim_rdc.bin "
				+namePath+".mli.bin "+namePath+".diff_par "
				+namePath+".offs "+namePath+".snr 128 128 "
				+namePath+".offsets 1 64 64 -");
	}

	public void geoCode5() throws IOException{
		wCom.write(geoDir+"offset_fitm "+namePath+".offs "
				+namePath+".snr "+namePath+".diff_par "
				+namePath+".coffs "+namePath+".coffsets ");
		wCom.write("\n");
		//offset_fitm $output/$line/$nameid.offs $output/$line/$nameid.snr $diff_par $output/$line/$nameid.coffs $output/$line/$nameid.coffsets - 3 > $diff_par.out
		listCommand.add(geoDir+"offset_fitm "+namePath+".offs "
				+namePath+".snr "+namePath+".diff_par "
				+namePath+".coffs "+namePath+".coffsets ");
	}
	public void geoCode6() throws IOException{
		wCom.write(geoDir+"gc_map_fine "+namePath+".lut "+
				dem_width+" "+namePath+".diff_par "
				+namePath+".lutf 1");
		wCom.write("\n");
		//gc_map_fine $lut $dem_width $diff_par $lutf 1
		listCommand.add(geoDir+"gc_map_fine "+namePath+".lut "+
				dem_width+" "+namePath+".diff_par "
				+namePath+".lutf 1");

	}
	public void geoCode7() throws IOException{
		wCom.write(geoDir+"geocode_back "+namePath+".cmli.bin "
				+mli_width+" "+namePath+".lutf "+namePath+".cmli.GTC.bin "
				+dem_width+" 0 2 0");
		wCom.write("\n");
		//geocode_back $imfile $mli_width $lutf $gtc.bin $dem_width 0 2 0
		listCommand.add(geoDir+"geocode_back "+namePath+".cmli.bin "
				+mli_width+" "+namePath+".lutf "+namePath+".cmli.GTC.bin "
				+dem_width+" 0 2 0");
	}
	public void geoCode8() throws IOException{
		wCom.write(scriptDir+"gamma2envi "+namePath+".demseg.par "
				+namePath+".cmli.GTC.hdr");
		wCom.write("\n");
		//gamma2envi $demsegpar $gtc.hdr
		listCommand.add(scriptDir+"gamma2envi "+namePath+".demseg.par "
				+namePath+".cmli.GTC.hdr");

		wCom.write(dispDir+"raspwr "+namePath+".cmli.bin "
				+mlirs+" 1 0 1 1 - - - "+namePath+".cmli.ras");
		wCom.write("\n");
		//raspwr $output/$line/$id*.cmli.bin $mlirs 1 0 1 1 - - - $output/$line/$nameid.cmli.ras
		listCommand.add(dispDir+"raspwr "+namePath+".cmli.bin "
				+mlirs+" 1 0 1 1 - - - "+namePath+".cmli.ras");

	}
	public void geoCode9() throws IOException{

		wCom.write(geoDir+"geocode_back "+namePath+".cmli.ras "
				+mlirs+" "+namePath+".lutf "+namePath+".cmli.GTC.image.bmp "
				+dem_width+" - 0 2");
		wCom.write("\n");
		//geocode_back $output/$line/$id*.cmli.ras $mlirs $output/$line/$id*.lutf $gtc.image.bmp $dem_width - 0 2
		listCommand.add(geoDir+"geocode_back "+namePath+".cmli.ras "
				+mlirs+" "+namePath+".lutf "+namePath+".cmli.GTC.image.bmp "
				+dem_width+" - 0 2");
	}
	//offset_pwrm $simsar_rdc $mli $diff_par $output/$line/$nameid.offs $output/$line/$nameid.snr 128 128 $output/$line/$nameid.offsets 1 64 64 -


	public  ArrayList<String> geoCodeTest(String path,String demPath){
		listCommand=new ArrayList<String>();
		double minLat=Double.valueOf(getParam(path,"min.*latitude*:*",3))-0.05;
		double maxLat=Double.valueOf(getParam(path,"min.*latitude*:*",7))+0.05;
		double minLon=Double.valueOf(getParam(path,"min.*longitude*:*",3))-0.05;
		double maxLon=Double.valueOf(getParam(path,"min.*longitude*:*",7))+0.05;
		listCommand.add("this :"+minLon+" "+maxLat+" "+maxLon+" "+minLat);

		//double demGrid=Double.valueOf(getParam(demPath,"post_lon:*",1))*1852*60;
		//double ovr=demGrid/parSRes;
		//listCommand.add(ovr+" "+demGrid+" ");
		return listCommand;
	}




	public String fileName(List<String> nameSplit){
		String diroutputName=nameSplit.get(nameSplit.size()-1);
		//create fileOutputName
		List<String> fileOutputArray=new ArrayList<String>() ;

		fileOutputArray=Arrays.asList(diroutputName.split("_"));
		fileOutputName=fileOutputArray.get(0)+"_"+fileOutputArray.get(1)+"-"+fileOutputArray.get(13)+"-HH";
		return fileOutputName;
	}


	public  void extractSAR(String path){

		//split listDir first,get last part
		List<String> nameSplit=new ArrayList<String>() ;

		nameSplit=Arrays.asList(path.split("\\/"));
		//append command to make dir output
		listCommand.add("mkdir "+outputDirName+"/"+nameSplit.get(nameSplit.size()-1));
		System.out.println("mkdir "+outputDirName+"/"+nameSplit.get(nameSplit.size()-1));
		outputMakeDir=nameSplit.get(nameSplit.size()-1)+"/";
		System.out.println("outputMakeDir ="+outputMakeDir);

		//variable to be passed to batch_TSX_TB class
		char[] outputMakeDirChar= outputMakeDir.toCharArray();
		StringBuffer yearName=new StringBuffer();
		for (int i=28;i<32;i++){

			yearName.append(String.valueOf(outputMakeDirChar[i]));
		}
		year=yearName.toString();
		satName=String.valueOf(outputMakeDirChar[0])+String.valueOf(outputMakeDirChar[1])+String.valueOf(outputMakeDirChar[2]);

		StringBuffer dateName=new StringBuffer();
		StringBuffer tileName=new StringBuffer();
		for (int i=30;i<43;i++){

			dateName.append(String.valueOf(outputMakeDirChar[i]));
		}
		//also append $line to dir output
		date=dateName.toString();

		System.out.println(dateName);
		//batch_TSX_TB.satName=satName;
		//batch_TSX_TB.dateName=dateName.toString();

		//find XML
		String cari=nameSplit.get(nameSplit.size()-1)+".xml";
		System.out.println("cari="+cari);
		File[] filexml;
		try {
			filexml = Files.walk(Paths.get(path))
					.filter(p -> p.toString().endsWith(cari)).distinct()
					//.filter(p -> p.toString().matches(".xml")).distinct()
					.map(Path::toFile)
					.toArray(File[]::new);
			xmlFile =filexml[0].toString();
			System.out.println("xmlFile="+xmlFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		//find Cosar

		File[] fileCos;
		try {
			fileCos = Files.walk(Paths.get(path))
					.filter(p -> p.toString().endsWith(".cos")).distinct()
					//.filter(p -> p.toString().matches(".cos")).distinct()
					.map(Path::toFile)
					.toArray(File[]::new);
			cosFile =fileCos[0].toString();
			System.out.println("cosFile="+cosFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fileName(nameSplit);
		namePath=outputDirName+"/"+outputMakeDir+fileOutputName;
		listCommand.add(geoDir+"par_TX_SLC "+xmlFile+" "+cosFile+" "
				+namePath+".slc.par "+namePath+".slc.bin HH");
		pathFolder=outputDirName+"/"+outputMakeDir;
		//batch_TSX_TB.pathRemove=pathFolder;

	}


	public   String getParam(String parPath,String regex,int column){
		BufferedReader reader;
		String found;
		String[] cut=new String[10];
		try {
			reader = new BufferedReader(new FileReader(parPath));
			String line = reader.readLine();
			while (line != null) {
				//String regex2 = regex;
				//private  final String INPUT = "cat cat cat cattie cat";
				//		//String text    =
				//				"This is the text to be searched " +
				//						"for occurrences of the http:// pattern.";
				//String text3="/home/eucliwood/Documents/TDX1/input/TDX1_SAR__SSC______SM_S_SRA_20180308T101947_20180308T101957";

				//String text5="/home/eucliwood/Documents/TDX1/input/TDX1_SAR__SSC______SM_S_SRA_20180308T101947_20180308T101957";

				//String text2=isiDir[1].toString();
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(line);   // get a matcher object
				//System.out.println("isidir[foundDIrInput]= "+isiDir[foundDIrInput].toString());

				//		int count=0;
				if (m.find()){
					//	
					//	
					//	System.out.println("m.group="+m.group(count));
					//	//count++;
					//}
					//System.out.println(m.group(0).toString());

					cut=line.split("\\s+");
					//										for (int i=0;i<cut.length;i++) {
					//											System.out.println(cut[i]);
					//					//						// read next line
					//										}
				}
				//else {
				line = reader.readLine();
				//}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("cut="+Arrays.toString(cut));
		//System.out.println("cut[1]="+cut[1]);
		return cut[column];
	}

	public void mliParam() throws IOException {
		System.out.println("namePath="+namePath);
		wCom=new FileWriter(new File(namePath+".logCommand"));

		String parPath=namePath+".slc.par";
		//
		//
		int satu=1;
		//
		//		mlirs=getParam(parPath,"range_samples*",satu);
		String parInc=getParam(parPath,"incidence_angle*",satu);
		String parRPix=getParam(parPath,"range_pixel_spacing*",satu);
		String parAPix=getParam(parPath,"azimuth_pixel_spacing*",satu);

		String parMLISamp=getParam(parPath,"range_samples:*",satu);
		String parMLILat=getParam(parPath,"center_latitude:*",satu);
		String parMLILong=getParam(parPath,"center_longitude:*",satu);

		String parCorLat=getParam(parPath,"range_samples:*",satu);
		//String parMLILat=getParam(parPath,"center_latitude:*",satu);
		//String parMLILong=getParam(parPath,"center_longitude:*",satu);

		double sinInc=Math.sin(((Double.valueOf(parInc)*4*Math.atan(1)))/180);


		double parRlks=parSRes*sinInc/Double.valueOf(parRPix);
		if(parRlks<=1) {
			parRlks=1;
		}
		double parAzlks=parSRes/Double.valueOf(parAPix);
		if(parAzlks<=1) {
			parAzlks=1;
		}
		if(parRlks>=1||parAzlks>=1) {
			wCom.write(geoDir+"multi_look "+namePath+".slc.bin "
					+namePath+".slc.par "+namePath+".mli.bin "
					+namePath+".mli.par "+parRlks+" "+parAzlks);
			wCom.write("\n");
			listCommand.add(geoDir+"multi_look "+namePath+".slc.bin "
					+namePath+".slc.par "+namePath+".mli.bin "
					+namePath+".mli.par "+parRlks+" "+parAzlks);

			wCom.write(scriptDir+"gamma2envi "+namePath+".mli.par");
			wCom.write("\n");
			//listCommand.add(parRlks+" "+parAzlks);
			listCommand.add(scriptDir+"gamma2envi "+namePath+".mli.par");

		}
		wCom.write(geoDir+"radcal_MLI "+namePath+".mli.bin "
				+namePath+".slc.par - "+namePath+".cmli.bin - 0 0 1 0.0 -");
		listCommand.add(geoDir+"radcal_MLI "+namePath+".mli.bin "
				+namePath+".slc.par - "+namePath+".cmli.bin - 0 0 1 0.0 -");
		wCom.write("\n");

		wCom.write(geoDir+"SLC_corners "+namePath+".slc.par >"
				+namePath+".corners.txt");
		wCom.write("\n");
		listCommand.add(geoDir+"SLC_corners "+namePath+".slc.par >"
				+namePath+".corners.txt");


		//return commandMLI.toString();
		//return parInc;

	}
	public double[] getCorners(String pathCornersTxt){
		//StringBuffer minXStr=null;
		String[] minXStrAr=getParam(pathCornersTxt,"min.*longitude*:*",3).split("\\.");
		char[] minXStrArRight=minXStrAr[1].toCharArray();
		minX=Double.valueOf(minXStrAr[0].toString()+"."+minXStrArRight[0]);

		String[] maxXStrAr=getParam(pathCornersTxt,"min.*longitude*:*",7).split("\\.");
		char[] maxXStrArRight=maxXStrAr[1].toCharArray();
		maxX=Double.valueOf(maxXStrAr[0].toString()+"."+maxXStrArRight[0]);

		String[] minYStrAr=getParam(pathCornersTxt,"min.*latitude*:*",3).split("\\.");
		char[] minYStrArRight=minYStrAr[1].toCharArray();
		minY=Double.valueOf(minYStrAr[0].toString()+"."+minYStrArRight[0]);

		String[] maxYStrAr=getParam(pathCornersTxt,"min.*latitude*:*",7).split("\\.");
		char[] maxYStrArRight=maxYStrAr[1].toCharArray();
		maxY=Double.valueOf(maxYStrAr[0].toString()+"."+maxYStrArRight[0]);
		double[] result={minX,maxY,maxX,minY};
		//result={minX,minY,maxX,maxY};
		//result
		return result;
	}
	public String createName(double[] dots){
		//find Pole based on max Y (highest north point)
		String[] findPoleStrAr=String.valueOf(dots[1]).split("\\.");//-3.1
		char[] findPoleChAr=String.valueOf(findPoleStrAr[0]).toCharArray();//-3
		if(String.valueOf(findPoleChAr[0]).equals("-")){//-
			pole="S";
		}
		else{//3
			pole="N";
		}
		//find latName based on maxY (lowest west point)
		if(findPoleChAr.length==2){//-9 -1
			latName="0"+String.valueOf(findPoleChAr[1]);
		}
		else if(findPoleChAr.length==3){//-10 -12
			latName=String.valueOf(findPoleChAr[1])+String.valueOf(findPoleChAr[2]);
		}
		else{
			latName="0"+String.valueOf(findPoleChAr[0]);
		}
		//find lonName based on minX (lowest west point)
		String[] findlonStrAr=String.valueOf(dots[0]).split("\\.");//120.1
		char[] findlonNameChAr=String.valueOf(findlonStrAr[0]).toCharArray();//take left value after "." ,make char array
		if(findlonNameChAr.length==3){
			lonName=String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1])+String.valueOf(findlonNameChAr[2]);
		}
		else{
			lonName="0"+String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1]);
		}

		//findTileName, need value after point from minX and maxY 
		char[] getRightMinX=findlonStrAr[1].toCharArray();
		String rightMinX=String.valueOf(getRightMinX[0]);//right value after "." of minX
		char[] getRightMaxY=findPoleStrAr[1].toCharArray();
		String rightMaxY=String.valueOf(getRightMaxY[0]);//right value after "." of maxY
		tileName=rightMinX+rightMaxY;

		String fileName=satName+"_SSC_"+date+"_"+pole+latName+"W"+lonName+"_"+tileName+".tif";
		return fileName;

	}
	public void makeTile(double[] oriLatLon){
		//double[] oriLatLon=points;

		double oriMinX=oriLatLon[0];
		double oriMaxY=oriLatLon[1];
		double oriMaxX=oriLatLon[2];
		double oriMinY=oriLatLon[3];
		//double cutMinX=oriMinX;
		
		
//		double cutMaxX=oriMaxX-0.11;
//		double cutMinY=oriMinY+0.11;
		double sizeMinX=oriMinX;
		double sizeMaxY=oriMaxY;
		double sizeMaxX=oriMaxX;
		double sizeMinY=oriMinY;

		while(sizeMinX<=oriMaxX){
			while(oriMinY<=sizeMaxY){
				double cutMinX=Math.round((sizeMinX-0.01)*100.0);
				cutMinX=cutMinX/100.0;
				//double cutMaxY=oriMaxY;sizeMinX
				double cutMaxY=Math.round((sizeMaxY+0.01)*100.0);
				cutMaxY=cutMaxY/100.0;
				//double[] toName={sizeMinX,sizeMaxY,sizeMaxX,sizeMinY};
				double newCutMinX=Math.round((sizeMinX+0.11)*100.0);
				newCutMinX=newCutMinX/100.0;
				double newCutMaxY=Math.round((sizeMaxY-0.11)*100.0);
				newCutMaxY=newCutMaxY/100.0;
				double[] toCrop={cutMinX,cutMaxY,newCutMinX,newCutMaxY};
				createName(toCrop);
				cropTile(toCrop);
				sizeMaxY=sizeMaxY-0.1;	
			}
			sizeMaxY=oriMaxY;
			sizeMinX=sizeMinX+0.1;
			
		}

	}
	public void cropTile(double[] points){

		listCommand.add("mkdir "+targetDir+"/"+year);
		String targetOutFile=targetDir+"/"+year+"/"+pole+latName+"W"+lonName;
		listCommand.add("mkdir "+targetOutFile);
		listCommand.add("gdal_translate -a_nodata 0 -projwin "+points[0]+" "+points[1]+" "+points[2]+" "+points[3]+" "+
				namePath+".cmli.GTC.bin "+targetOutFile+"/"+createName(points));
//		System.out.println("gdal_translate -a_nodata 0 -projwin "+points[0]+" "+points[1]+" "+points[2]+" "+points[3]+" "+
//				namePath+".cmli.GTC.bin "+targetOutFile+"/"+createName(points));
	}
	//	public  void main (String[] ar) throws IOException {
	//		runGamma ins=new runGamma();
	//		//String path="/media/rossendra/01D23527E552C810/TSX/OUTPUT_TES/TDX1_SAR__SSC______SM_S_SRA_20180308T101947_20180308T101957/TDX1_SAR-20180308T101957-HH.corners.txt";
	//		//String demPath="/media/rossendra/01D23527E552C810/TSX/OUTPUT_TES/TDX1_SAR__SSC______SM_S_SRA_20180308T101947_20180308T101957/TDX1_SAR-20180308T101957-HH.dem.par";
	//
	//		ins.Step1(ins.ListFolder(inputDirName));
	//		//System.out.println("Command Size="+ins.geoCodeTest(path).size());
	//		//		for(int i=0;i<geoCodeTest(path,demPath).size();i++){
	//		//			System.out.println("listCommand["+i+"]="+ins.geoCodeTest(path,demPath).get(i));
	//		//		}
	//
	//	}
}