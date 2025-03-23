import javax.net.ssl.*;
import javax.swing.*;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * This module contains the application logic of an echo server
 * which uses a stream-mode socket for interprocess communication.
 * Unlike EchoServer2, this server services clients concurrently.
 * A command-line argument is required to specify the server port.
 * @author M. L. Liu
 */

public class Server {
   public static void main(String[] args) {

      //Create server form
      ServerForm serverForm = new ServerForm();

      int port = 12345;
      if (args.length == 1) {
         port = Integer.parseInt(args[0]);
      }

      String ksName = "server.jks";
      char[] ksPass = "password".toCharArray();
      char[] ctPass = "password".toCharArray();

      try {
         // Load server keystore
         KeyStore keyStore = KeyStore.getInstance("JKS");
         keyStore.load(new FileInputStream(ksName), ksPass);

         // Initialize KeyManagerFactory
         KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
         kmf.init(keyStore, ctPass); // same placeholder password

         // Create and initialize SSLContext
         SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
         sslContext.init(kmf.getKeyManagers(), null, null);

         // Create SSLServerSocket
         SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
         SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

         serverForm.log("Server listening on port " + port);

         // Forever loop
         while (true) {
            // wait to accept a connection
            serverForm.log("Waiting for a connection.");
            MyStreamSocket socket = new MyStreamSocket(sslServerSocket.accept());
            serverForm.log("Connection accepted from " + socket.getInetAddress());

            // Spawn a new thread for this client
            Thread theThread = new Thread(new ServerThread(socket, serverForm));
            theThread.start();
            // and then go on to the next client
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }
}

