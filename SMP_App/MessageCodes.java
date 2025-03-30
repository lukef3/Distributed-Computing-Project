public class MessageCodes {
    // client request codes
    public static final int LOGIN = 100;
    public static final int UPLOAD = 200;
    public static final int DOWNLOAD = 300;
    public static final int DOWNLOAD_ALL = 400;
    public static final int LOGOFF = 500;

    // success response codes
    public static final int LOGIN_SUCCESS = 101;
    public static final int UPLOAD_SUCCESS = 102;
    public static final int MESSAGE_DATA = 103;
    public static final int ALL_MESSAGES_DATA = 104;
    public static final int LOGOFF_SUCCESS = 105;

    // error response codes
    public static final int LOGIN_FAILED = 201;
    public static final int MESSAGE_NOT_FOUND = 202;
    public static final int INVALID_REQUEST = 203;
    public static final int INVALID_REQUEST_FORMAT = 204;
    public static final int SERVER_ERROR = 205;
}


