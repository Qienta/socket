import java.net.*;
import java.util.*;
import java.io.*;

//Each client connection will be managed in a dedicated Thread
public class TestServer implements Runnable
{
    static final File WEB_ROOT = new File(".");
    static final String DEFAULT_FILE = "mypage.html";
    static final String METHOD_NOT_SUPPORTED = "";

    //part to listen connection
    static final int PORT = 5167;

    //verbase node
    static final boolean verbase = true;

    //client connection via socket class
    private Socket connect;

    public TestServer(Socket c)
    {
        connect = c;
    }

    private byte[] readFileData(File file, int filelength) throws FileNotFoundException
       {
            FileInputStream filein = null;
            byte[] fileData = new byte[filelength];

            try 
            {
                filein = new FileInputStream(file);
                filein.read(fileData);
                
            } finally 
            {
                // TODO: handle exception
                if (filein != null) 
                {
                    filein.close();    
                }
            }

            return fileData;
       }

   public static void main(String[] args) 
   {
        System.out.println("Server starting...");

        try {
            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server started.\nListening for connections on port :"+ PORT +"...\n ");

            while (true) 
            {
             TestServer myServer = new TestServer(serverConnect.accept());
             
             if (verbase) 
             {
                 System.out.println("Connection opened.("+ new Date()+")");  
             }
            
             //create dedicated thread to message the client connection
             Thread thread = new Thread(myServer);
             thread.start();


            }
        } catch (IOException e) {
            // TODO: handle exception
            System.out.println("Server Connection error : "+ e.getMessage());
        }

   }

   @Override
   public void run()
   {
    //we manage our particular client connection
       BufferedReader in = null;
       PrintWriter out = null;
       BufferedOutputStream dataOut = null;

       String fileRequest = null;

        try 
       {
        //we read characters from the client via input stream on the socket
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        //we get character output stream to client (for header)
            out = new PrintWriter(connect.getOutputStream());
        //get binary output stream to client (for request data)
            dataOut = new BufferedOutputStream(connect.getOutputStream());

        //get first line of the request from client
            String input = in.readLine();
        //we parse the request with a string tokenizer
            StringTokenizer parse = new StringTokenizer(input);

        //we get the HTTP of the client            
            String method = parse.nextToken().toUpperCase();
        //we get file requested
            fileRequest = parse.nextToken().toLowerCase();

        //we support only GET and HEAD methods, we check
            if (verbase) {
                System.out.println("501 Not Implemented : "+method+" method.");
            
                //we return the not supportal file to the client
                File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
                int filelength = (int) file.length();

                String contentMimeType = "text/html";

                //read content to return to client
                byte[] fileData = readFileData(file, filelength);

                //we send HTTP Headers with Data to client
                out.println("HTTP/1.1 501 Not Implemented");
                out.println("Server: Java HTTP Server from SSaurel : 1.0");
                out.println("Date : "+ new Date());
                out.println("Content-type: "+contentMimeType);
                out.println("Content-length: "+filelength);

                //blank line between headers and content , very important!
                out.println();
                //flush character output stream buffer
                out.flush();

                //file
                dataOut.write(fileData, 0, filelength);
                dataOut.flush();

            } else {
                
            }

        } catch (IOException ioe) {
        // TODO: handle exception
            System.out.println("Server error : " + ioe);
       }

       
   }


}