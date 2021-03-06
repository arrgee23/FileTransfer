import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class FileClient {  
	Socket sock=null;
	InputStream in=null;
	OutputStream os = null;
	DataOutputStream dos=null;
	DataInputStream serverData = null;
	OutputStream output = null;
	static final int CHUNK_SIZE = 1024;
	
	public FileClient() {
		try {
			sock = new Socket("127.0.0.1", 13267);
			in = sock.getInputStream();
			os = sock.getOutputStream();
			System.out.println("Connected To: "+sock.getInetAddress());

		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
	
	public void receive(){
		try{
			// scan filename
			System.out.print("Enter the name of the file: ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String filename = br.readLine();
			
			// send filename to the server
			dos = new DataOutputStream(os);     
			dos.writeUTF(filename);     

			// read file size from server
			serverData = new DataInputStream(in);
			long size = serverData.readLong();     
			long temp=size;
			System.out.println("File size: "+size);
			
			// write to file in 1kb chunk
			int bytesRead=0;
			byte[] buffer = new byte[CHUNK_SIZE];     
			output = new FileOutputStream("downloaded_"+filename);
			while (size > 0 && (bytesRead = serverData.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)     
			{   
				System.out.println((temp-size)+" bytes read");
				
				output.write(buffer, 0, bytesRead);     
				size -= bytesRead;     
			}  
			System.out.println("Done...");
			
			//Closing socket
			in.close();
			os.close();
			dos.close();  
			output.close();
			sock.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)  {  
		FileClient f = new FileClient();
		f.receive();
	}  
}