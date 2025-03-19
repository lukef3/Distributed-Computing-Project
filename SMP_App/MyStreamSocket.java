import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.net.*;
import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * A wrapper class of Socket which contains 
 * methods for sending and receiving messages
 * @author M. L. Liu
 */
public class MyStreamSocket extends Socket {
   private Socket socket;
   private BufferedReader input;
   private PrintWriter output;

   String tsName = "clientTrustStore.jks";
   char[] tsPassword = "password".toCharArray( );

   MyStreamSocket(InetAddress acceptorHost, int acceptorPort ) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, KeyManagementException {
      KeyStore trustStore = KeyStore.getInstance("JKS");
      trustStore.load(new FileInputStream(tsName), tsPassword);

      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
      trustManagerFactory.init(trustStore);

      SSLContext sslContext = SSLContext.getInstance("TLSv1.2");

      sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

      SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
      socket = sslSocketFactory.createSocket(acceptorHost, acceptorPort);
      setStreams();
   }

   MyStreamSocket(Socket socket)  throws IOException {
      this.socket = socket;
      setStreams();
   }

   private void setStreams() throws IOException{
      // get an input stream for reading from the data socket
      InputStream inStream = socket.getInputStream();
      input = new BufferedReader(new InputStreamReader(inStream));
      OutputStream outStream = socket.getOutputStream();
      // create a PrinterWriter object for character-mode output
      output = new PrintWriter(new OutputStreamWriter(outStream));
   }

   public void sendMessage(String message) throws IOException {
      output.print(message + "\n");   
      //The ensuing flush method call is necessary for the data to
      // be written to the socket data stream before the
      // socket is closed.
      output.flush();               
   } // end sendMessage

   public String receiveMessage() throws IOException {
      // read a line from the data stream
      String message = input.readLine();
      return message;
   } //end receiveMessage

   public void close() throws IOException {
      socket.close();
   }
} //end class
