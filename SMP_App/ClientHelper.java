import java.net.*;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * This class is a module which provides the application logic
 * for an Echo client using stream-mode socket.
 * @author M. L. Liu
 */

public class ClientHelper {
   private MyStreamSocket mySocket;
   private InetAddress serverHost;
   private int serverPort;

   ClientHelper(String hostName, String portNum) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
      this.serverHost = InetAddress.getByName(hostName);
      this.serverPort = Integer.parseInt(portNum);
      //Instantiates a stream-mode socket and wait for a connection.
   	  this.mySocket = new MyStreamSocket(this.serverHost, this.serverPort);
   } // end constructor

   private String sendAndReceive(String request) throws IOException {
      mySocket.sendMessage(request);
       return mySocket.receiveMessage();
   }

   public String login(String username, String password) throws IOException {
      String request = MessageCodes.LOGIN + ":" + username + "," + password;
      return sendAndReceive(request);
   }

   public String logOff() throws IOException {
      String request = MessageCodes.LOGOFF + ":";
      return sendAndReceive(request);
   }

   public String uploadMessage(String message) throws IOException {
      String request = MessageCodes.UPLOAD + ":" + message;
      return sendAndReceive(request);
   }

   public String downloadMessage(String messageId) throws IOException {
      String request = MessageCodes.DOWNLOAD + ":" + messageId;
      return sendAndReceive(request);
   }

   public String downloadAllMessages() throws IOException {
      String request = MessageCodes.DOWNLOAD_ALL + ":";
      return sendAndReceive(request);
   }

   public void done() throws IOException{
      mySocket.close();
   } // end done 
} //end class
