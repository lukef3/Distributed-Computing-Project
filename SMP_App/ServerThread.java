import java.io.*;
/**
 * This module is to be used with a concurrent Echo server.
 * Its run method carries out the logic of a client session.
 * @author M. L. Liu
 */

class ServerThread implements Runnable {
    MyStreamSocket myDataSocket;
    ServerThread(MyStreamSocket myDataSocket) {
        this.myDataSocket = myDataSocket;
    }

    public void run() {
        try {
            String request;
            while ((request = myDataSocket.receiveMessage()) != null) {
                handleRequest(request);
            }
        } catch (Exception ex) {
            System.out.println("Exception caught in thread: " + ex);
        }
    }

    private void handleRequest(String request) throws IOException {
        String[] requestElements = request.split(":");
        String requestCode = requestElements[0];
        String[] requestParams = requestElements[1].split(",");

        switch (requestCode){
            case "100":
                if (requestParams.length < 2) {
                    myDataSocket.sendMessage("Error: Login request missing parameters");
                    break;
                }
                String username = requestParams[0];
                String password = requestParams[1];
                verifyLogin(username, password);
                break;
            default:
                myDataSocket.sendMessage("Error: Unknown Request");
                break;
        }
    }

    private void verifyLogin(String username, String password) throws IOException {
        if ("admin".equals(username) && "password".equals(password)) {
            myDataSocket.sendMessage("101: Login Successful");
        } else {
            myDataSocket.sendMessage("102: Login Failed");
        }
    }
}
