package ou.edu.herbarium.thread2;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.channels.FileChannel;

import org.eclipse.jface.dialogs.MessageDialog;

import ou.edu.herbarium.Activator;
import ou.edu.herbarium.sharedata.ErrorData;
import ou.edu.herbarium.sharedata.ErrorData3;
import ou.edu.herbarium.sharedata.ShareData5;
import ou.edu.herbarium.sharedata.ShareData6;
import ou.edu.herbarium.sharedata.TransferData5;
import ou.edu.herbarium.sharedata.TransferData6;
import ou.edu.herbarium.util.ASyncUDPSvr;

public class ImageThread extends Thread{
	ShareData5 s5;
	ShareData6 s6;
	ErrorData de;
	ErrorData3 de3;
	//Socket
	int index = 0;
	
	public ImageThread(ShareData5 s5,ShareData6 s6,ErrorData de, ErrorData3 de3){
		this.s5 = s5;
		this.s6 = s6;
		this.de = de;
		this.de3 = de3;
	}
	
	public void run(){
		try {
		String sentence = "";
//		DatagramSocket serverSocket = new DatagramSocket(60005);
			while (true) {
				TransferData5 d5 = s5.getShareData();
				System.out.println(" 000000");
				System.out.println(" ---=== ImageThread started ===---");
				if(d5.getSpec().getIsFolder()==false){
					String ret = "";
					if(Activator.testmode == 0){
						String host = "localhost";
						int port = 60002;
						System.out.println( d5.getOriginalFileName());
						String tempDir = "C:/ImageStorage";
						byte[] message = tempDir.getBytes();
						InetAddress address = InetAddress.getByName(host);
						DatagramPacket packet = new DatagramPacket(message,
								message.length, address, port);
						DatagramSocket dsocket = new DatagramSocket();
						dsocket.send(packet);//copyFile
						dsocket.close();
						long startTime = System.currentTimeMillis();
						ASyncUDPSvr svr = new ASyncUDPSvr(60005);
						ret = svr.process();
						System.out.println("RET="+ret);
						long span = System.currentTimeMillis()-startTime;
						System.out.println("SPAN="+span);
						if(ret==null||ret.startsWith("ERROR")){
							System.out.println(ret);
							java.awt.Toolkit.getDefaultToolkit().beep();
							de.setShareData("ERROR_CAMERA_5::ERROR");
							Thread.sleep(1000);
							String buttonReply = de3.getShareData();
							System.out.println(buttonReply);
							if(buttonReply.equals("0")){
								svr.close();
								continue;
							}
							else if(buttonReply.equals("1")){
								s5.setShareFirstData(d5);
								svr.close();
								continue;
							}
							else if(buttonReply.equals("2")){
								
							}
						}
						System.out.println("return="+ret);
						svr.close();
					}
					else{
						ret = Activator.testDirectory+"full_0.jpg";
					}
					
					
					String path = d5.getOriginalFileName();
					String [] pathArray = path.split("/");
					String pathSentence = "";
					for(int i=0;i<pathArray.length;i++){
						pathSentence+=pathArray[i]+"/";
						System.out.println("pathSentence="+pathSentence);
						File tempDirFile = new File(pathSentence);//converter
						if(tempDirFile.exists()==false){
							tempDirFile.mkdir();
						}
					}
					ret = ret.replaceAll("\\\\", "/");
					File temp = new File(ret);
					System.out.println(temp.exists());
					copyFile(ret,d5.getOriginalFileName()+"full.jpg");
					File oldJPG = new File(ret);
					if(Activator.testmode == 0){
						//oldJPG.delete();
					}
					
					System.out.println("jpg ret="+ret);
					
					ret = ret.replaceAll("jpg", "cr2");
					ret = ret.replaceAll("full", "test");
					temp = new File(ret);
					System.out.println("dng ret="+ret+""+temp.exists());
					copyFile(ret,d5.getOriginalFileName()+"full.cr2");
					File oldCR2 = new File(ret);
					if(Activator.testmode == 0){
						//oldCR2.delete();
					}
					DNGAgent agent = new DNGAgent(d5.getOriginalFileName()+"full.cr2");
					agent.run();
					File dngFile = new File(d5.getOriginalFileName()+"full.dng");
					boolean convertflag = true;
					long currentMilli = System.currentTimeMillis();
					while(dngFile.exists()==false){
						if(System.currentTimeMillis()>currentMilli+20000){
							convertflag = false;
							break;
						}
					}
					if(convertflag==true){
						File cr2File = new File(d5.getOriginalFileName()+"full.cr2");
						cr2File.delete();
						if(Activator.testmode == 0){
							oldJPG.delete();
							oldCR2.delete();
						}
					}
					FileInputStream fis = new FileInputStream(new File(d5.getOriginalFileName()+"full.dng"));
					String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
					d5.getSpec().setChecksum(md5);
					
					TransferData6 d6 = new TransferData6(d5.getSpec(),d5.getS2(),d5.getS3(),d5.getOriginalFileName(),"");
					this.s6.setShareData(d6);
					Thread.sleep(1000);
				}
				else{
					//create the folder and return 
				}
					//stub
			}
		} catch (Exception e) {
			try {
				FileOutputStream fos = new FileOutputStream(new File("errorLog.log"));
				PrintStream ps = new PrintStream(fos);
				e.printStackTrace(ps);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}
	
	public long copyFile(String srcFileName, String newFileName) {  
		File srcFile = new File(srcFileName);//Date
        long copySizes = 0;  
        if (!srcFile.exists()) {  
            copySizes = -1;  
        } else if (newFileName == null) {  
            copySizes = -1;  
        } else {  
            try {  
                FileChannel fcin = new FileInputStream(srcFile).getChannel();  
                FileChannel fcout = new FileOutputStream(new File(newFileName)).getChannel();  
                long size = fcin.size();  
                fcin.transferTo(0, fcin.size(), fcout);  
                fcin.close();  
                fcout.close();  
                copySizes = size;  
            } catch (FileNotFoundException e) {  
                e.printStackTrace();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return copySizes;  
    }  
}
