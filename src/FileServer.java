import java.net.*;     
import java.io.*;     
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
			
			// read filename
			String filename = clientData.readUTF();
			File myFile = new File(filename); // open file named filename

			////////////////// file exists assumed /////////////////

			// read from file into buffer
			byte[] mybytearray = new byte[(int) myFile.length()];  
			FileInputStream fis = new FileInputStream(myFile);  
			BufferedInputStream bis = new BufferedInputStream(fis);  
			DataInputStream dis = new DataInputStream(bis);     
			dis.readFully(mybytearray, 0, mybytearray.length);  

			OutputStream os = clientSocket.getOutputStream();  

			//Sending file size to the client  
			DataOutputStream dos = new DataOutputStream(os);          
			dos.writeLong(mybytearray.length);

			//Sending file data to the server  
			os.write(mybytearray, 0, mybytearray.length);  
			
			os.flush();  

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