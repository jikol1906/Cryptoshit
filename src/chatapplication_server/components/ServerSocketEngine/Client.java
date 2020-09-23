package chatapplication_server.components.ServerSocketEngine;

public class Client {
    private  String userName;
    private  String publicKey;

    public Client(String userName, String publicKey) {
        this.userName = userName;
        this.publicKey = publicKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
