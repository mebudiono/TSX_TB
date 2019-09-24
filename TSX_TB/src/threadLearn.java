import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class threadLearn {
	 public static void main(String[] args) {
		
		// final number=0;
		 for(int i=1;i<4;i++) {
			 String codeP="";
			 char[] stringLong=String.valueOf(i).toCharArray();
				if(stringLong.length==1) {
					codeP="0"+String.valueOf(i);
				}
				else {
					codeP=String.valueOf(i);
				}
			 final String number=codeP;
	        System.out.println("Inside : " + Thread.currentThread().getName()+i);

	        System.out.println("Creating Runnable...");
	        Runnable runnable = new Runnable() {
	        	
	            @Override
	            public void run() {
	            	multiWriteText exe=new multiWriteText();
	            	try {
						exe.makeList("/media/public80/Mosaik_TSX_TB/",number);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        };

	        System.out.println("Creating Thread...");
	        Thread thread = new Thread(runnable);

	        System.out.println("Starting Thread...");
	        thread.start();
	    }
	 }
}
