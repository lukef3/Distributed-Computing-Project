import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * This module is to be used with a concurrent Echo server.
 * Its run method carries out the logic of a client session.
 * @author M. L. Liu
 */

class ServerThread implements Runnable {
    MyStreamSocket myDataSocket;
    ServerForm serverForm;
    private String currentUser = "";

    ServerThread(MyStreamSocket myDataSocket, ServerForm serverForm) {
        this.myDataSocket = myDataSocket;
        this.serverForm = serverForm;
    }

    public void run() {
        boolean done = false;
        String request;
        try {
            while (!done) { // forever loop until log off
                request = myDataSocket.receiveMessage();
                serverForm.log("Request received from [" + currentUser + "]: " + request);
                done = handleRequest(request); // returns true on log off
            }
        } catch (Exception ex) {
            serverForm.log("Exception caught in thread: " + ex);
        } finally {
            try {
                myDataSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // method to send message and log to server form
    private void sendMessage(String message) {
        try {
            myDataSocket.sendMessage(message);
            serverForm.log("Response sent to [" + currentUser + "]: " + message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean handleRequest(String request) throws IOException {
        String[] requestElements = request.split(":", 2); // split limit of 2, in the case that messages contain ":"
        int requestCode = Integer.parseInt(requestElements[0]);
        String[] requestParams = requestElements[1].split(","); // split request parameters by ","

        switch (requestCode){
            // LOGIN:username,password
            case MessageCodes.LOGIN:
                if (requestParams.length != 2) {
                    sendMessage(MessageCodes.INVALID_REQUEST_FORMAT + ": Invalid message format: " + request + ". \nMust be " + MessageCodes.LOGIN + ":<username>,<password>");
                    break;
                }
                handleLogin(requestParams[0], requestParams[1]);
                break;
            // UPLOAD:message
            case MessageCodes.UPLOAD:
                if (requestParams.length != 1) {
                    sendMessage(MessageCodes.INVALID_REQUEST_FORMAT + ": Invalid message format: " + request + ". \nMust be: " + MessageCodes.UPLOAD + ":<message>");
                    break;
                }
                // handle upload request
                handleUpload(requestElements[1]);   // pass in full request element in case message contains commas
                break;
            // DOWNLOAD:messageID
            case MessageCodes.DOWNLOAD:
                if (requestParams.length != 1) {
                    sendMessage(MessageCodes.INVALID_REQUEST_FORMAT + ": Invalid message format: " + request + ". \nMust be: " + MessageCodes.DOWNLOAD + ":<messageId>");
                    break;
                }
                // convert id to integer and handle download request
                handleDownload(Integer.parseInt(requestParams[0]));
                break;
            // DOWNLOAD_ALL:
            case MessageCodes.DOWNLOAD_ALL:
                if (requestParams.length != 1) {
                    sendMessage(MessageCodes.INVALID_REQUEST_FORMAT + ": Invalid message format: " + request + ". \nMust be: " + MessageCodes.DOWNLOAD_ALL + ":");
                    break;
                }
                handleDownloadAll();
                break;
            // LOGOFF:
            case MessageCodes.LOGOFF:
                if (requestParams.length != 1) {
                    sendMessage(MessageCodes.INVALID_REQUEST_FORMAT + ": Invalid message format: " + request + ". \nMust be: " + MessageCodes.LOGOFF + ":");
                    break;
                }
                handleLogoff(currentUser);
                return true; // return true to stop receiving messages for this session
            default:
                sendMessage(MessageCodes.INVALID_REQUEST + ": " + request);
                break;
        }
        return false;
    }

    // login handler
    private void handleLogin(String username, String password) throws IOException {
        if (!username.isEmpty() && !password.isEmpty()) {
            // set user variable for this session
            currentUser = username;

            // send confirmation message
            sendMessage(MessageCodes.LOGIN_SUCCESS + ": Welcome " + username + ".");
        } else {
            // send failure message
            sendMessage(MessageCodes.LOGIN_FAILED + ": Invalid username or password");
        }
    }

    // upload handler
    private void handleUpload(String message) throws IOException {
        int id = getNextMessageId();
        String currentTime = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("messages.txt", true))) {

            // save message as id-[time]-[user] : message
            bw.write(id + "-[" + currentTime + "]-[" + currentUser + "] : " + message);
            bw.newLine();

            // send confirmation to client
            sendMessage(MessageCodes.UPLOAD_SUCCESS + ": Upload Successful. Message ID: " + id);
        } catch (IOException e) {
            // error writing to messages file
            sendMessage(MessageCodes.SERVER_ERROR + ": Unable to upload message: " + e.getMessage());
        }
    }

    // download handler
    private void handleDownload(int messageId) {
        try (BufferedReader reader = new BufferedReader(new FileReader("messages.txt"))) {
            String message;
            while ((message = reader.readLine()) != null) {
                String[] parts = message.split("-", 2);
                int currentId = Integer.parseInt(parts[0]);
                if (currentId == messageId) {
                    // send message to client if ID is found
                    sendMessage(MessageCodes.MESSAGE_DATA + ":" + message);
                    return;
                }
            }
            // unable to find message with requested ID
            sendMessage(MessageCodes.MESSAGE_NOT_FOUND + ": Unable to find message with ID: " + messageId);
        } catch (IOException e) {
            // error reading messages file
            sendMessage(MessageCodes.SERVER_ERROR + ": Unable to download message: " + e.getMessage());
        }
    }

    // download all messages handler
    private void handleDownloadAll() {
        try (BufferedReader br = new BufferedReader(new FileReader("messages.txt"))) {
            List<String> messages = new ArrayList<>();
            String message;
            while ((message = br.readLine()) != null) {
                messages.add(message);
            }
            // add delimiter between each message
            String messagesDelimited = String.join("|", messages);

            // send delimited message back to client
            sendMessage(MessageCodes.ALL_MESSAGES_DATA + ":" + messagesDelimited);
        } catch (IOException e) {
            // error reading messages file
            sendMessage(MessageCodes.SERVER_ERROR + ": Unable to download messages: " + e.getMessage());
        }
    }

    //log  off handler
    private void handleLogoff(String user) throws IOException {
        this.currentUser = "";
        String message = MessageCodes.LOGOFF_SUCCESS + ": Log Off Successful. Goodbye " + user + ".";
        myDataSocket.sendMessage(message);
        serverForm.log("Response sent to [" + user + "]: " + message);
    }

    // generate next ID. last highest +1
    private int getNextMessageId() throws IOException {
        int highestId = 0;
        File file = new File("messages.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("-", 2);
                int currentId = Integer.parseInt(parts[0]);
                highestId = Math.max(highestId, currentId);
            }
        }
        return highestId + 1;
    }
}