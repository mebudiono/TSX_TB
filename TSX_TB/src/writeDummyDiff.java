



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class writeDummyDiff {
	private String mli;
	private String par;
	private String template;

	public writeDummyDiff(String mli,String par,String template){;
	this.mli=mli;
	this.par=par;
	this.template=template;
	}

	public static  String getMli(String parPath,String regex,int column){
		System.out.println("Start getMli :"+regex);
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



	public String getParam(String input,String regex,int column){
		//BufferedReader reader;
		//String found;
		//System.out.println("Start GETpARAM :"+input);
		String[] cut=new String[10];
		//try {
		//reader = new BufferedReader(new FileReader(parPath));
		String line = input;
		//while (line != null) {
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
			//line = reader.readLine();
			//}
			//			}
			//			//reader.close();
			//		} catch (IOException e) {
			//			e.printStackTrace();
		//}
		//System.out.println("cut="+Arrays.toString(cut));
		for(int i=0;i<cut.length;i++){
	//	System.out.println("cut["+i+"]="+cut[i]);
		}
		return cut[column];
	}

	public void process() throws IOException{
		try{
			//File in= new File(template);
			//File out= new File(par);
			System.out.println("Start void process");
			BufferedReader buffIn =
					new BufferedReader(new FileReader(template));
			//BufferedWriter bw = null;
			String inputLine;
			//File corners = new File(outputDirName+outputMakeDir+fileOutputName+".corners.txt");
			FileWriter fw = new FileWriter(par);
			//bw = new BufferedWriter(fw);
			while ((inputLine = buffIn.readLine()) != null) {
				//System.out.println("Start while :"+inputLine);
				//equals("range_samp_1")
				if(getParam(inputLine,"range_samp_1:*",0)!=null){
					//System.out.println(getParam(inputLine,"range_samp_1:*",0));
					fw.write("range_samp_1:	"+getMli(mli,"range_samples:*",1));
					fw.write("\n");
					//System.out.println(inputLine);
					//result += inputLine;
				}
				else if(getParam(inputLine,"range_samp_2:*",0)!= null){
					fw.write("range_samp_2:	"+getMli(mli,"range_samples:*",1));
					fw.write("\n");
				}
				else if(getParam(inputLine,"az_samp_1:*",0)!= null){
					fw.write("az_samp_1:	"+getMli(mli,"azimuth_lines:*",1));
					fw.write("\n");
				}
				else if(getParam(inputLine,"az_samp_2:*",0)!= null){
					fw.write("az_samp_2:	"+getMli(mli,"azimuth_lines:*",1));
					fw.write("\n");
				}
				else if(getParam(inputLine,"number_of_nonzero_range_pixels_1:*",0)!= null){
					fw.write("number_of_nonzero_range_pixels_1:	"+getMli(mli,"range_samples:*",1));
					fw.write("\n");
				}
				else if(getParam(inputLine,"range_pixel_spacing_1:*",0)!= null){
					fw.write("range_pixel_spacing_1:	"+getMli(mli,"range_pixel_spacing:*",1)+"e+00");
					fw.write("\n");
				}
				else if(getParam(inputLine,"az_pixel_spacing_1:*",0)!= null){
					fw.write("az_pixel_spacing_1:	"+getMli(mli,"azimuth_pixel_spacing:*",1)+"e+00");
					fw.write("\n");
				}

				else if(getParam(inputLine,"number_of_nonzero_range_pixels_2:*",0)!= null){
					fw.write("number_of_nonzero_range_pixels_2:	"+getMli(mli,"range_samples:*",1));
					fw.write("\n");
				}
				else if(getParam(inputLine,"range_pixel_spacing_2:*",0)!= null){
					fw.write("range_pixel_spacing_2:	"+getMli(mli,"range_pixel_spacing:*",1)+"e+00");
					fw.write("\n");
				}
				else if(getParam(inputLine,"az_pixel_spacing_2:*",0)!= null){
					fw.write("az_pixel_spacing_2:	"+getMli(mli,"azimuth_pixel_spacing:*",1)+"e+00");
					fw.write("\n");
				}
				else if(getParam(inputLine,"offset_estimation_range_samples:*",0)!= null){
					fw.write("offset_estimation_range_samples:	32");
					fw.write("\n");
				}
				else if(getParam(inputLine,"offset_estimation_azimuth_samples:*",0)!= null){
					fw.write("offset_estimation_azimuth_samples:	32");
					fw.write("\n");
				}
				else if(getParam(inputLine,"offset_estimation_patch_width:*",0)!= null){
					fw.write("offset_estimation_patch_width:	256");
					fw.write("\n");
				}
				else if(getParam(inputLine,"offset_estimation_patch_height:*",0)!= null){
					fw.write("offset_estimation_patch_height:	256");
					fw.write("\n");
				}
				else if(getParam(inputLine,"offset_estimation_threshold:*",0)!= null){
					fw.write("offset_estimation_threshold:	0.150");
					fw.write("\n");
				}
				else {
					fw.write(inputLine);
					fw.write("\n");
				}

			}

			buffIn.close();
			fw.close();

		} catch (IOException e) {
			System.out.println(e);
		}





		//ArrayList<String> fileNames= new ArrayList<String>();
		//		String dirName=dir.toString();
		//		int dirLength=dirName.length();	
		//		File[] listFiles=dir.listFiles(new FilenameFilter()
		//		{public boolean accept(File dir, String name){
		//			return name.toLowerCase().endsWith(".tif");}
		//		});
		//		for (int i=0;i<listFiles.length;i++){
		//			String fileName=listFiles[i].toString();
		//			int nameLength=fileName.length();
		//			char[] abjad=fileName.toCharArray();
		//			int length=nameLength-dirLength;
		//			StringBuffer image=new StringBuffer ();
		//			for(int a=dirLength+1;a<nameLength-4;a++){
		//
		//				image.append(abjad[a]);
		//
		//			}
		//			char[] letter =image.toString().toCharArray();
		//			String tab=    "    ";
		//			//letter[0].
		//
		//			System.out.println(image);
		//			System.out.println(listFiles[i]);
		//			//put write tif here
		//			String target=dirName+"/"+image+".ers";
		//			FileWriter fw=new FileWriter(target);
		//			BufferedWriter bw =new BufferedWriter(fw);
		//			bw.write("DatasetHeader Begin");
		//			bw.newLine();
		//			bw.write(tab+"Version		= \"15.0\"");
		//			bw.newLine();
		//			bw.write(tab+"Name		= "+"\""+image+".tif\"");
		//			bw.newLine();
		//			bw.write(tab+"LastUpdated	= Thu Aug 02 17:00:03 GMT 2018");
		//			bw.newLine();
		//			bw.write(tab+"DataFile	= "+"\""+image+".tif\"");
		//			bw.newLine();
		//			bw.write(tab+"DataSetType	= Translated");
		//			bw.newLine();
		//			bw.write(tab+"DataType	= Raster");
		//			bw.newLine();
		//			bw.write(tab+"ByteOrder	= LSBFirst");
		//			bw.newLine();
		//			bw.write(tab+"CoordinateSpace Begin");
		//			bw.newLine();
		//			bw.write(tab+tab+"Datum		= \"WGS84\"");
		//			bw.newLine();
		//			bw.write(tab+tab+"Projection	= \"GEODETIC\"");
		//			bw.newLine();
		//			bw.write(tab+tab+"CoordinateType	= EN");
		//			bw.newLine();
		//			bw.write(tab+tab+"Units		= \"dd\"");
		//			bw.newLine();
		//			bw.write(tab+tab+"Rotation	= 0:0:0.0");
		//			bw.newLine();
		//			bw.write(tab+"CoordinateSpace End");
		//			bw.newLine();
		//			bw.write(tab+"RasterInfo Begin");
		//			bw.newLine();
		//			bw.write(tab+tab+"CellType	= Unsigned8BitInteger");
		//			bw.newLine();
		//			bw.write(tab+tab+"CellInfo Begin");
		//			bw.newLine();
		//			bw.write(tab+tab+tab+"Xdimension	= 0.00044444444");
		//			bw.newLine();
		//			bw.write(tab+tab+tab+"Ydimension	= 0.00044444444");
		//			bw.newLine();
		//			bw.write(tab+tab+"CellInfo End");
		//			bw.newLine();
		//			bw.write(tab+tab+"NrOfLines	= 2250");
		//			bw.newLine();
		//			bw.write(tab+tab+"NrOfCellsPerLine	= 2250");
		//			bw.newLine();
		//			bw.write(tab+tab+"RegistrationCoord Begin");
		//			bw.newLine();
		//			bw.write(tab+tab+tab+"Eastings	= "+letter[4]+letter[5]+letter[6]);
		//			bw.newLine();
		//			if(letter[0]=='S') {
		//				bw.write(tab+tab+tab+"Northings	= -"+letter[1]+letter[2]);
		//			}
		//			else {
		//				bw.write(tab+tab+tab+"Northings	= "+letter[1]+letter[2]);
		//			}
		//			//}
		//			bw.newLine();
		//			bw.write(tab+tab+"RegistrationCoord End");
		//			bw.newLine();
		//			bw.write(tab+tab+"NrOfBands	= 1");
		//			bw.newLine();
		//			bw.write(tab+tab+"BandId Begin");
		//			bw.newLine();
		//			bw.write(tab+tab+tab+"Value		= \"Red\"");
		//			bw.newLine();
		//			bw.write(tab+tab+"BandId End");
		//			bw.newLine();
		//			bw.write(tab+"RasterInfo End");
		//			bw.newLine();
		//			bw.write("DatasetHeader End");
		//
		//			bw.close();
		//		}
		//		System.out.println("tif total :"+listFiles.length);
	}


}