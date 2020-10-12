/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapplication_server.components.ClientSocketEngine;

import chatapplication_server.ComponentManager;
import chatapplication_server.components.DHKey;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author atgianne
 */
public class ListenFromServer extends Thread {
    private PublicKey stringToPublicKey(String s) {
        try {
            //converting string to Bytes
            byte[] byte_pubkey = Base64.getDecoder().decode(s);
            System.out.println("BYTE KEY::" + byte_pubkey);


        //converting it back to public key
            KeyFactory factory = KeyFactory.getInstance("DH");
            PublicKey public_key = (PublicKey) factory.generatePublic(new X509EncodedKeySpec(byte_pubkey));
            System.out.println("stringToPublicKey. !!!! FINAL OUTPUT" + public_key);
            return public_key;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void run() {
        while (true) {
            ObjectInputStream sInput = ClientEngine.getInstance().getStreamReader();
            DHKey dhKey = ClientEngine.getInstance().getDhKey();

            synchronized (sInput) {
                try {
                    String msg = (String) sInput.readObject();
                    if (msg.contains("&PUBLICKEY&")) {
                        String pkString = msg.replaceFirst("&PUBLICKEY&", "");
                        PublicKey pk = this.stringToPublicKey(pkString);
                        dhKey.receivePublicKeyFromString(pk);
                        dhKey.generateCommonSecretKey();

                    } else if (msg.contains("#")) {
                        ClientSocketGUI.getInstance().appendPrivateChat(msg + "\n");
                    } else {
                        System.out.println("\n\ndhKey.toString()"+dhKey.toString()+"\n\n");
                        System.out.println("ListenFromServer.if msg " + msg);
                        String decryptedMessage = dhKey.decryptMessage(msg);
                        System.out.println("ListenFromServer.in decryptedMessage " + msg);

                        ClientSocketGUI.getInstance().append(decryptedMessage + "\n");
                    }
                } catch (IOException e) {
                    ClientSocketGUI.getInstance().append("Server has closed the connection: " + e.getMessage() + "\n");
                    ComponentManager.getInstance().fatalException(e);
                } catch (ClassNotFoundException cfe) {
                    ClientSocketGUI.getInstance().append("Server has closed the connection: " + cfe.getMessage());
                    ComponentManager.getInstance().fatalException(cfe);
                }
            }
        }
    }
}
