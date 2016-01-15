/*
 * Open two terminals
 * javac *.java
 * execute "java FileServer" in one terminal/console window
 * execute "java FileClient" in another
 * write the filename with full path in client side console (the file must exist on server side)
 * specify the speed in server side console 
 * the file is saved in client side as "download_(filename)"
 */


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
public class FileServer{

	public static void main(String args[]) throws Exception
	{
		@SuppressWarnings("resource")
		ServerSocket soc=new ServerSocket(13267);
		System.out.println("FTP Server Started on Port Number 5217");
		while(true)
		{
			System.out.println("Waiting for Connection ...");
			new FileTransfer(soc.accept());
		}
	}
}
class FileTransfer extends Thread{  

	Socket clientSocket = null;
	static final int CHUNK_SIZE = 1024;
	
	public FileTransfer(Socket soc) {
		try{
			clientSocket = soc;
		}catch(Exception e){
			e.printStackTrace();
		}
		start();
	}
	@Override
	public void run() {    
		try{
			InputStream in = clientSocket.getInputStream();  
			DataInputStream clientData = new DataInputStream(in);
			
			// read speed from for this connection
			System.out.println("Enter speed in KBps for "+clientSocket.getInetAddress());
			Scanner s = new Scanner(System.in);
			int speed = s.nextInt();
			s.close();
			
			// read filename
			String filename = clientData.readUTF();
			File myFile = new File(filename); // open file named filename

			////////////////// file exists assumed /////////////////


			OutputStream os = clientSocket.getOutputStream();  

			//Sending file size to the client  
			DataOutputStream dos = new DataOutputStream(os);          
			dos.writeLong(myFile.length());
			
			// write to file in 1kb*speed chunk per second
			int bytesRead=0;
			byte[] buffer = new byte[CHUNK_SIZE*speed];
			long size = myFile.length();
			long temp=size;
			FileInputStream fis = new FileInputStream(myFile);  
			BufferedInputStream bis = new BufferedInputStream(fis);
			DataInputStream dis = new DataInputStream(bis);
			
			while (size > 0 && (bytesRead = dis.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)     
			{   
				System.out.println((temp-size)+" bytes written");
				Thread.sleep(1000);
				os.write(buffer, 0, bytesRead);     
				size -= bytesRead;     
			}  
			// done
			System.out.println("Done...");
			System.out.println("Waiting for new connections");
			// close streams
			os.close();
			dos.close();
			in.close();
			clientData.close();
			dis.close();
			clientSocket.close();

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}  
} 