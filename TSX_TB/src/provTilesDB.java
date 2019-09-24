import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.ImageIcon;

public class provTilesDB {
	//private static final String CONFIG_FILE = "/dbMosTiles.txt";
	//private String path;
	public Map<String, String> provT;


	public provTilesDB (){
		File file = new File(
				getClass().getClassLoader().getResource("dbMosTiles.txt").getFile()
			);
		//this.path=path;
		this.provT=new HashMap<String, String>();
		//String id;
		//File textPath= new File(path);
		ArrayList<String> lines=new ArrayList<String>();
		try{
			Scanner input = new Scanner(file);
			while (input.hasNext()) {
				String line=input.next();
				lines.add(line);

			}
			input.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Lines Size="+lines.size());
		for(int i=0;i<lines.size();i++) {

			//System.out.println("lines["+i+"]="+lines.get(i));
			//System.out.println("map2["+i+1+"]="+lines.get(i+1));
			//provN.put(lines.get(i),lines.get(i+1));
		}
		//}


		for(int i=0;i<lines.size();i+=3) {
			if(i<lines.size()-1) {
				//System.out.println("map1["+i+"]="+lines.get(i));
				//System.out.println("map2["+i+1+"]="+lines.get(i+1));
				provT.put(lines.get(i),lines.get(i+2));
			}
		}
		
		
	}
	
	
	
	public static void main (String[] ar) {
		
		//URL fileUrl = getClass().getResource(CONFIG_FILE);
		//File file = resource.getFile();
		//db.readFileFromClasspath;
		provTilesDB db=new provTilesDB();
		//db.
		System.out.println("get01="+db.provT.get("01"));
		System.out.println("get15="+db.provT.get("15"));
	}
}
