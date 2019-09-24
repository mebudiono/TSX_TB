import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class cekCorners {
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
	public double parSRes=6,minX,minY,maxX,maxY,minXR,minYR,maxXR,maxYR;

	public int ulX,ulY,lrX,lrY;

	public  ArrayList<String> listDir,listXML ;
	public ArrayList<String> listCommand;
	public ArrayList<String> dapetXML;
	public ArrayList<String> dapetCOS;
	public Integer dem_width,mli_width;
	public FileWriter writeDiffOut,wCom;

	
	public String getFolderTSX(String nameTXT){
		//read DB
		String folderTSX="";
		return folderTSX;
	}
	
	
	
	
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

	public static void main (String[] a) throws IOException{
		cekCorners check=new cekCorners();

		File folder=new File("/media/hd/TSX_TB/workDir/output/");
		//	FilenameFilter filter=new FilenameFilter(){
		//		@Override
		//		public boolean accept(File dir, String name){
		//			String str=name.contains("SSC");
		//			
		//		}
		//	}
		String fileTxt;
		String[] namaTxt;
		File fileCornersTxt;
		int[] target=new int [3] ;
		int total=0;
		
		for(int i=0;i<folder.listFiles().length;i++){
			if(folder.listFiles()[i].isDirectory()){
				//System.out.println("Start folder number="+i+" from "+folder.listFiles().length);
				File filesInside=new File(folder.listFiles()[i].getAbsolutePath());
				//System.out.println("folder="+folder.listFiles()[i].getAbsolutePath().toString());
				for(int j=0;j<filesInside.listFiles().length;j++){

					if(filesInside.listFiles()[j].getName().contains(".corners.txt")) {
						//fileCornersTxt=;

						//File cornersTxt=new File(folder.listFiles()[i].getAbsolutePath());
						//System.out.println("cornersTxt="+cornersTxt);
						namaTxt=filesInside.listFiles()[j].getName().split("\\.");
						//for(int l=0;l<namaTxt.length;l++) {
						fileTxt=namaTxt[0];
						
						target=check.getCorners(filesInside.listFiles()[j].getAbsolutePath().toString());
						if(target[0]==9||target[1]==9||target[2]==9) {
							//System.out.println("cornersTxt="+filesInside.listFiles()[j].getAbsolutePath().toString());
							//String[] fileTxtSplit=namaTxt[0].split("-");
							//fileTxt=fileTxtSplit[0]+"%"+fileTxtSplit[1]+"%";
							System.out.println(fileTxt);
							//System.out.println("ulY="+target[0]+",lrX="+target[1]+",lrY="+target[2]);
							total++;
							
						}

					}





					//if(folder.listFiles()[i].listFiles().toString().contains(".corners.txt")){


				}
			}
		}
		System.out.println("total="+total);
	}





}
