//package GammScript;

//package GammScript;



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
public class runGamma2 {
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
	protected String outputMakeDir,mlirs,fileOutputName,xmlFile,cosFile,namePath;
	public double parSRes=6,minX,minY,maxX,maxY,minXR,minYR,maxXR,maxYR,ulX,ulY,lrX,lrY;

	public  ArrayList<String> listDir,listXML ;
	public ArrayList<String> listCommand;
	public ArrayList<String> dapetXML;
	public ArrayList<String> dapetCOS;
	public Integer dem_width,mli_width;
	public FileWriter writeDiffOut,wCom,kmlW,aKmlW;
	//public ArrayList<String> outputMakeDir;
	public runGamma2(String workDir,String jobDir,String targetDir){
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
		outputDirName=workDir+"/output/2019";
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
		try{
			//File in= new File(template);
			//File out= new File(par);
			System.out.println("Start void process");
			//File corners = new File(outputDirName+outputMakeDir+fileOutputName+".corners.txt");
			kmlW = new FileWriter(new File(namePath+"_tiles.kml"));
			kmlW.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n");
			kmlW.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\"> \n");
			kmlW.write("<Document id=\"root_doc\"> \n");
			kmlW.write("<Schema name=\"tile_id\" id=\"tile_id\"> \n");
			kmlW.write("	<SimpleField name=\"id\" type=\"string\"></SimpleField> \n");
			kmlW.write("</Schema> \n");
			kmlW.write("<Folder><name>tile_id</name> \n");


			//fw.write("\n");


		} catch (IOException e) {
			System.out.println(e);
		}
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
		//delete output folder (temp) except for log
		listCommand=new ArrayList<String>();
		System.out.println("pathFolder="+pathFolder);
		File target=new File(pathFolder);
		for(int i=0;i<target.listFiles().length;i++){
			if(target.listFiles()[i].toString().endsWith(".corners.txt")){

			}
			else if(target.listFiles()[i].toString().endsWith(".logCommand")){

			}
			else if(target.listFiles()[i].toString().endsWith(".kml")){

			}
			else{

				listCommand.add("rm "+target.listFiles()[i].toString());
			}
		}
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
		//delete the input folder (temp)
		String Command="rm -rf "+inputDirName;
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

		wCom.close();

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
			else if(target.listFiles()[i].toString().endsWith(".logCommand")){

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
		StringBuffer dateFileName=new StringBuffer();
		for (int i=30;i<43;i++){

			dateName.append(String.valueOf(outputMakeDirChar[i]));
		}
		dateFile="20"+String.valueOf(outputMakeDirChar[30])+String.valueOf(outputMakeDirChar[31])+"-"
				+String.valueOf(outputMakeDirChar[32])+String.valueOf(outputMakeDirChar[33])+"-"
				+String.valueOf(outputMakeDirChar[34])+String.valueOf(outputMakeDirChar[35]);

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
	public double[] getCorners(String pathCornersTxt) throws IOException{

		//get ulX from minlong corners,split by "."

		minX=Double.valueOf(getParam(pathCornersTxt,"min.*longitude*:*",3));
		if(minX>=0){
			//ulX=Math.round(minX * 10) / 10.0;
			String[] ulXStrAr=String.valueOf(minX).split("\\.");
			//split the right part after "."
			char[] ulXStrArRight=ulXStrAr[1].toCharArray();
			//combine left part before "." and 1 digit from right part to make ulX
			//int rightComma=Character.getNumericValue(ulXStrArRight[0])+1;
			ulX=Double.valueOf(ulXStrAr[0].toString()+"."+String.valueOf(ulXStrArRight[0]));
		}
		else{
			//get ulY from maxlat corners,split by "."
			String[] ulXStrAr=getParam(pathCornersTxt,"min.*longitude*:*",3).split("\\.");
			//minX=Double.valueOf(getParam(pathCornersTxt,"min.*longitude*:*",3));
			//split the right part after "."
			char[] ulXStrArRight=ulXStrAr[1].toCharArray();
			//combine left part before "." and 1 digit from right part to make ulY
			ulX=Double.valueOf(ulXStrAr[0].toString()+"."+ulXStrArRight[0]);

		}

		maxY=Double.valueOf(getParam(pathCornersTxt,"min.*latitude*:*",7));
		if(maxY>=0){
			//ulY=Math.round(maxY * 10) / 10.0;
			String[] ulYStrAr=String.valueOf(maxY).split("\\.");
			//split the right part after "."
			char[] ulYStrArRight=ulYStrAr[1].toCharArray();
			int rightComma=Character.getNumericValue(ulYStrArRight[0]);

			//combine left part before "." and 1 digit from right part to make ulX
			ulY=Double.valueOf(ulYStrAr[0].toString()+"."+String.valueOf(rightComma));
			//System.out.println("ulY before="+ulY);
			ulY=Math.round((ulY+0.1)*100.0);
			//System.out.println("ulY mid="+ulY);
			ulY=ulY/100.0;
			System.out.println("ulY after="+ulY);
		}
		else{
			//get ulY from maxlat corners,split by "."
			String[] ulYStrAr=getParam(pathCornersTxt,"min.*latitude*:*",7).split("\\.");
			maxY=Double.valueOf(getParam(pathCornersTxt,"min.*latitude*:*",7));
			//split the right part after "."
			char[] ulYStrArRight=ulYStrAr[1].toCharArray();
			//combine left part before "." and 1 digit from right part to make ulY
			ulY=Double.valueOf(ulYStrAr[0].toString()+"."+ulYStrArRight[0]);

		}



		//		//get ulY from maxlat corners,split by "."
		//		String[] ulYStrAr=getParam(pathCornersTxt,"min.*latitude*:*",7).split("\\.");
		//		maxY=Double.valueOf(getParam(pathCornersTxt,"min.*latitude*:*",7));
		//		//split the right part after "."
		//		char[] ulYStrArRight=ulYStrAr[1].toCharArray();
		//		//combine left part before "." and 1 digit from right part to make ulY
		//		ulY=Double.valueOf(ulYStrAr[0].toString()+"."+ulYStrArRight[0]);


		maxX=Double.valueOf(getParam(pathCornersTxt,"min.*longitude*:*",7));
		if(maxX>=0){
			//lrX=Math.round(maxX * 10) / 10.0;
			String[] lrXStrAr=String.valueOf(maxX).split("\\.");
			//split the right part after "."
			char[] lrXStrArRight=lrXStrAr[1].toCharArray();
			int rightComma=Character.getNumericValue(lrXStrArRight[0]);
			//combine left part before "." and 1 digit from right part to make ulX
			lrX=Double.valueOf(lrXStrAr[0].toString()+"."+String.valueOf(rightComma));
			//System.out.println("lrX before="+lrX);
			lrX=Math.round((lrX+0.1)*100.0);
			//System.out.println("lrX mid="+lrX);
			lrX=lrX/100.0;
			//System.out.println("lrX after="+lrX);
		}
		else{
			//get lrX from maxlong corners,split by "."
			String[] lrXStrAr=getParam(pathCornersTxt,"min.*longitude*:*",7).split("\\.");
			maxX=Double.valueOf(getParam(pathCornersTxt,"min.*longitude*:*",7));
			//split the right part after "."
			char[] lrXStrArRight=lrXStrAr[1].toCharArray();
			//combine left part before "." and 1 digit from right part to make lrX
			lrX=Double.valueOf(lrXStrAr[0].toString()+"."+lrXStrArRight[0]);

		}


		//		//get lrX from maxlong corners,split by "."
		//		String[] lrXStrAr=getParam(pathCornersTxt,"min.*longitude*:*",7).split("\\.");
		//		maxX=Double.valueOf(getParam(pathCornersTxt,"min.*longitude*:*",7));
		//		//split the right part after "."
		//		char[] lrXStrArRight=lrXStrAr[1].toCharArray();
		//		//combine left part before "." and 1 digit from right part to make lrX
		//		lrX=Double.valueOf(lrXStrAr[0].toString()+"."+lrXStrArRight[0]);


		minY=Double.valueOf(getParam(pathCornersTxt,"min.*latitude*:*",3));
		if(minY>=0){
			//lrY=Math.round(minY * 10) / 10.0;
			String[] lrYStrAr=String.valueOf(minY).split("\\.");
			//split the right part after "."
			char[] lrYStrArRight=lrYStrAr[1].toCharArray();
			int rightComma=Character.getNumericValue(lrYStrArRight[0]);
			//combine left part before "." and 1 digit from right part to make ulX
			lrY=Double.valueOf(lrYStrAr[0].toString()+"."+String.valueOf(rightComma));
			//	System.out.println("lrY before="+lrY);
			//lrY=Math.round((lrY+0.1)*100.0);
			//System.out.println("lrY mid="+lrY);
			//lrY=lrY/100.0;
			//System.out.println("lrY after="+lrY);
		}
		else{
			//lrY=Math.round(minY * 10) / 10.0;
			String[] lrYStrAr=String.valueOf(minY).split("\\.");
			//split the right part after "."
			char[] lrYStrArRight=lrYStrAr[1].toCharArray();
			int rightComma=Character.getNumericValue(lrYStrArRight[0]);
			//combine left part before "." and 1 digit from right part to make ulX
			lrY=Double.valueOf(lrYStrAr[0].toString()+"."+String.valueOf(rightComma));
			//	System.out.println("lrY before="+lrY);
			lrY=Math.round((lrY-0.1)*100.0);
			//System.out.println("lrY mid="+lrY);
			lrY=lrY/100.0;
			//System.out.println("lrY after="+lrY);

		}


		//		//get lrY from minlat corners,split by "."
		//		String[] lrYStrAr=getParam(pathCornersTxt,"min.*latitude*:*",3).split("\\.");
		//		minY=Double.valueOf(getParam(pathCornersTxt,"min.*latitude*:*",3));
		//		//split the right part after "."
		//		char[] lrYStrArRight=lrYStrAr[1].toCharArray();
		//		//combine left part before "." and 1 digit from right part to make lrX
		//		lrY=Double.valueOf(lrYStrAr[0].toString()+"."+lrYStrArRight[0]);

		double[] result={ulX,ulY,lrX,lrY};
		wCom.write("ulX="+ulX+","+"ulY="+ulY+","+"lrX="+lrX+","+"lrY="+lrY);
		wCom.write("\n");
		//result={minX,minY,maxX,maxY};
		//result
		return result;
	}
	public String createName(double X,double Y) throws IOException{
		String fileName=null;
		String[] findPoleStrAr=String.valueOf(Y).split("\\.");//2.1
		char[] findPoleChAr=String.valueOf(findPoleStrAr[0]).toCharArray();//2
		//find Pole based on ulY (highest north point)
		if (Y>0) {
			wCom.write("This is Y>0");
			wCom.write("\n");
			pole="N";
			int ILatName=Character.getNumericValue(findPoleChAr[0]);//2
			if(Character.getNumericValue(findPoleStrAr[1].toCharArray()[0])>0){
				ILatName=ILatName+1;//3
			}

			wCom.write("ILatName+1="+ILatName);
			wCom.write("\n");
			latName="0"+String.valueOf(ILatName);//03
			String[] findlonStrAr=String.valueOf(X).split("\\.");//120.1
			char[] findlonNameChAr=String.valueOf(findlonStrAr[0]).toCharArray();//take left value before "." ,make char array
			if(findlonNameChAr.length==3){
				lonName=String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1])+String.valueOf(findlonNameChAr[2]);
			}
			else{
				lonName="0"+String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1]);
			}
			wCom.write("lonName="+lonName);
			wCom.write("\n");
			//findTileName, need value after point from minX and maxY 
			char[] getRightMinX=findlonStrAr[1].toCharArray();//1
			String rightMinX=String.valueOf(getRightMinX[0]);//right value after "." of minX
			char[] getRightMaxY=findPoleStrAr[1].toCharArray();
			int IRightMaxY=10-Character.getNumericValue(getRightMaxY[0]);
			if(IRightMaxY==10){
				IRightMaxY=0;
			}
			String rightMaxY=String.valueOf(IRightMaxY);//right value after "." of maxY
			tileName=rightMinX+rightMaxY;
			wCom.write("tileName="+tileName);
			wCom.write("\n");

		}
		else if(Y>-1&&Y<=0) {
			pole="N";
			//int ILatName=Integer.valueOf(findPoleChAr[1]);
			latName="00";//+String.valueOf(ILatName);
			String[] findlonStrAr=String.valueOf(X).split("\\.");//120.1
			char[] findlonNameChAr=String.valueOf(findlonStrAr[0]).toCharArray();//take left value after "." ,make char array
			if(findlonNameChAr.length==3){
				lonName=String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1])+String.valueOf(findlonNameChAr[2]);
			}
			else{
				lonName="0"+String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1]);
			}
			//			//findTileName, need value after point from minX and maxY 
			//			char[] getRightMinX=findlonStrAr[1].toCharArray();
			//			String rightMinX=String.valueOf(getRightMinX[0]);//right value after "." of minX
			//			char[] getRightMaxY=findPoleStrAr[1].toCharArray();
			//			int IRightMaxY=10-Character.getNumericValue(getRightMaxY[0]);
			//			if(IRightMaxY==10){
			//				IRightMaxY=0;
			//			}
			//			String rightMaxY=String.valueOf(IRightMaxY);//right value after "." of maxY
			//			tileName=rightMinX+rightMaxY;

			//findTileName, need value after point from minX and maxY 
			char[] getRightMinX=findlonStrAr[1].toCharArray();
			String rightMinX=String.valueOf(getRightMinX[0]);//right value after "." of minX
			char[] getRightMaxY=findPoleStrAr[1].toCharArray();
			String rightMaxY=String.valueOf(getRightMaxY[0]);//right value after "." of maxY
			tileName=rightMinX+rightMaxY;
		}
		else {
			pole="S";
			//			if(String.valueOf(findPoleChAr[0]).equals("-")){//-
			//				pole="S";
			//			}
			//			else{//3
			//				pole="N";
			//			}
			if(findPoleChAr.length==2){//-9 -1
				latName="0"+String.valueOf(findPoleChAr[1]);
			}
			else {//-10 -12
				latName=String.valueOf(findPoleChAr[1])+String.valueOf(findPoleChAr[2]);
			}
			//			else{
			//				latName="0"+String.valueOf(findPoleChAr[0]);
			//			}
			//find lonName based on minX (lowest west point)
			String[] findlonStrAr=String.valueOf(X).split("\\.");//120.1
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

		}
		//		//find latName based on lrY (lowest west point)
		//		if(findPoleChAr.length==2){//-9 -1,-0
		//			latName="0"+String.valueOf(findPoleChAr[1]);
		//		}
		//		else if(findPoleChAr.length==3){//-10 -12
		//			latName=String.valueOf(findPoleChAr[1])+String.valueOf(findPoleChAr[2]);
		//		}
		//		else{
		//			latName="0"+String.valueOf(findPoleChAr[0]);
		//		}
		//		//find lonName based on minX (lowest west point)
		//		String[] findlonStrAr=String.valueOf(X).split("\\.");//120.1
		//		char[] findlonNameChAr=String.valueOf(findlonStrAr[0]).toCharArray();//take left value after "." ,make char array
		//		if(findlonNameChAr.length==3){
		//			lonName=String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1])+String.valueOf(findlonNameChAr[2]);
		//		}
		//		else{
		//			lonName="0"+String.valueOf(findlonNameChAr[0])+String.valueOf(findlonNameChAr[1]);
		//		}
		//
		//		//findTileName, need value after point from minX and maxY 
		//		char[] getRightMinX=findlonStrAr[1].toCharArray();
		//		String rightMinX=String.valueOf(getRightMinX[0]);//right value after "." of minX
		//		char[] getRightMaxY=findPoleStrAr[1].toCharArray();
		//		String rightMaxY=String.valueOf(getRightMaxY[0]);//right value after "." of maxY
		//		tileName=rightMinX+rightMaxY;

		fileName=pole+latName+"E"+lonName+"_"+tileName+"_"+satName+"_SSC_"+date+".tif";
		wCom.write("fileName="+pole+latName+"E"+lonName+"_"+tileName+"_"+satName+"_SSC_"+date+".tif");
		wCom.write("\n");

		//}
		return fileName;
	}
	public void makeTile(double[] oriLatLon) throws IOException{
		//double[] oriLatLon=points;

		double ulXTemp=oriLatLon[0];
		double ulYTemp=oriLatLon[1];
		double lrXTemp=oriLatLon[2];
		double lrYTemp=oriLatLon[3];


		try{
			//File in= new File(template);
			//File out= new File(par);
			System.out.println("Start making All.kml");
			//File corners = new File(outputDirName+outputMakeDir+fileOutputName+".corners.txt");
			aKmlW = new FileWriter(new File(namePath+"_all.kml"));
			aKmlW.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?> \n");
			aKmlW.write("<kml xmlns=\"http://www.opengis.net/kml/2.2\"> \n");
			aKmlW.write("<Document id=\"root_doc\"> \n");
			aKmlW.write("<Schema name=\"tile_id\" id=\"tile_id\"> \n");
			aKmlW.write("	<SimpleField name=\"id\" type=\"string\"></SimpleField> \n");
			aKmlW.write("</Schema> \n");
			aKmlW.write("<Folder><name>tile_id</name> \n");
			//double cutMinX=oriMinX;


			aKmlW.write("  <Placemark> \n");
			aKmlW.write("	<name>"+fileOutputName+"</name> <description>"+fileOutputName+"</description> \n");
			aKmlW.write("	<Style><LineStyle><color>ff0000ff</color></LineStyle><PolyStyle><fill>0</fill></PolyStyle></Style> \n");
			aKmlW.write("	<ExtendedData><SchemaData schemaUrl=\"#kml_order\"> \n");
			aKmlW.write("		<SimpleData name=\"id\">"+fileOutputName+"</SimpleData> \n");
			aKmlW.write("	</SchemaData></ExtendedData> \n");
			aKmlW.write("      <Polygon><altitudeMode>relativeToGround</altitudeMode><outerBoundaryIs><LinearRing><altitudeMode>relativeToGround</altitudeMode><coordinates>"
					+oriLatLon[0]+","+oriLatLon[1]+" "+oriLatLon[2]+","+oriLatLon[1]+" "+oriLatLon[2]+","+oriLatLon[3]+" "+
					oriLatLon[0]+","+oriLatLon[3]+" "+oriLatLon[0]+","+oriLatLon[1]+" </coordinates></LinearRing></outerBoundaryIs></Polygon> \n");
			aKmlW.write("  </Placemark> \n");

			aKmlW.write("</Folder> \n");
			aKmlW.write("</Document></kml> \n");
			aKmlW.close();

		} catch (IOException e) {
			System.out.println(e);
		}
		//		double cutMaxX=oriMaxX-0.11;
		//		double cutMinY=oriMinY+0.11;
		//		double sizeMinX=oriMinX;
		//		double sizeMaxY=oriMaxY;
		//		double sizeMaxX=oriMaxX;
		//		double sizeMinY=oriMinY;
		double[] toPass=new double[6];
		while(ulXTemp<=oriLatLon[2]){
			while(ulYTemp>=oriLatLon[3]){
				double ulCutX=Math.round((ulXTemp-0.01)*100.0);
				ulCutX=ulCutX/100.0;
				//double cutMaxY=oriMaxY;sizeMinX
				double ulCutY=Math.round((ulYTemp+0.01)*100.0);
				ulCutY=ulCutY/100.0;

				double lrCutX=Math.round((ulXTemp+0.11)*100.0);
				lrCutX=lrCutX/100.0;
				double lrCutY=Math.round((ulYTemp-0.11)*100.0);
				lrCutY=lrCutY/100.0;

				//double[] toCrop={ulCutX,ulCutY,lrCutX,lrCutY};
				//				wCom.write("toCrop="+ulCutX+" ,"+ulCutY+" ,"+lrCutX+" ,"+lrCutY);
				//				wCom.write("\n");
				//				double newSizeMaxX=sizeMinX+0.1;
				//				double newSizeMinY=sizeMaxY-0.1;
				toPass[0]=ulXTemp;//
				toPass[1]=ulYTemp;
				toPass[2]=ulCutX;
				toPass[3]=ulCutY;
				toPass[4]=lrCutX;
				toPass[5]=lrCutY;
				wCom.write("toPass="+ulXTemp+" , "+ulYTemp+","+ulCutX+" ,"+ulCutY+" ,"+lrCutX+" ,"+lrCutY);
				wCom.write("\n");
				String fileName=createName(ulXTemp,ulYTemp);
				listCommand.add("mkdir "+targetDir+"/"+year);
				String targetOutFile=targetDir+"/"+year+"/"+pole+latName+"E"+lonName;
				listCommand.add("mkdir "+targetOutFile);
				listCommand.add("gdal_translate -a_nodata 0 -projwin "+ulCutX+" "+ulCutY+" "+lrCutX+" "+lrCutY+" "+
						namePath+".cmli.GTC.bin "+targetOutFile+"/"+fileName);
				wCom.write("gdal_translate -a_nodata 0 -projwin "+ulCutX+" "+ulCutY+" "+lrCutX+" "+lrCutY+" "+
						namePath+".cmli.GTC.bin "+targetOutFile+"/"+fileName);
				wCom.write("\n");

				char[] fN=fileName.toCharArray();
				StringBuffer nameTag=new StringBuffer();
				StringBuffer descTag=new StringBuffer();
				for(int i=0;i<10;i++) {
					nameTag.append(fN[i]);
				}
				for(int i=12;i<32;i++) {
					descTag.append(fN[i]);
				}

				kmlW.write("  <Placemark> \n");
				kmlW.write("	<name>"+nameTag+"</name> <description>"+descTag+"</description> \n");
				kmlW.write("	<Style><LineStyle><color>ff0000ff</color></LineStyle><PolyStyle><fill>0</fill></PolyStyle></Style> \n");
				kmlW.write("	<ExtendedData><SchemaData schemaUrl=\"#kml_order\"> \n");
				kmlW.write("		<SimpleData name=\"id\">"+fileName+"</SimpleData> \n");
				kmlW.write("	</SchemaData></ExtendedData> \n");
				kmlW.write("      <Polygon><altitudeMode>relativeToGround</altitudeMode><outerBoundaryIs><LinearRing><altitudeMode>relativeToGround</altitudeMode><coordinates>"
						+ulCutX+","+ulCutY+" "+lrCutX+","+ulCutY+" "+lrCutX+","+lrCutY+" "+
						ulCutX+","+lrCutY+" "+ulCutX+","+ulCutY+" </coordinates></LinearRing></outerBoundaryIs></Polygon> \n");
				kmlW.write("  </Placemark> \n");

				



				//createName(toName);
				//cropTile(toCrop);
				ulYTemp=Math.round((ulYTemp-0.1)*100.0);
				ulYTemp=ulYTemp/100.0;	
			}
			ulYTemp=oriLatLon[1];
			ulXTemp=Math.round((ulXTemp+0.1)*100.0);
			ulXTemp=ulXTemp/100.0;	

		}
		kmlW.write("</Folder> \n");
		kmlW.write("</Document></kml> \n");
		kmlW.close();
		//return toPass;

	}

	//
	//    ALERT...Problem HERE...
	//
	//	public void cropTile(double[] points) throws IOException{
	//
	//		listCommand.add("mkdir "+targetDir+"/"+year);
	//		String targetOutFile=targetDir+"/"+year+"/"+pole+latName+"E"+lonName;
	//		listCommand.add("mkdir "+targetOutFile);
	//		listCommand.add("gdal_translate -a_nodata 0 -projwin "+points[2]+" "+points[3]+" "+points[4]+" "+points[5]+" "+
	//				namePath+".cmli.GTC.bin "+targetOutFile+"/"+createName(points[0],points[1]));
	//		//		System.out.println("gdal_translate -a_nodata 0 -projwin "+points[0]+" "+points[1]+" "+points[2]+" "+points[3]+" "+
	//		//				namePath+".cmli.GTC.bin "+targetOutFile+"/"+createName(points));
	//	}
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