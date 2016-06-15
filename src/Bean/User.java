package Bean;

import Security.FileUtil;
import Security.Generator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    
    private ArrayList<CertificateWrapper> keys;
    
    public User(){
        keys = new ArrayList<>();
    }
    
    public void addKey(CertificateWrapper cw){
        keys.add(cw);
    }
    
    public CertificateWrapper getKey(int index){
        return keys.get(index);
    }
    
    
    
    /*
    private KeyStore keyStore;
    
    public User(){
        try {
            keyStore = KeyStore.getInstance("pkcs12");
            keyStore.load(null,null);
        } catch (Exception ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addKey(String alias, KeyPair keyPair, String password, Certificate certificate){
        try {
            Certificate[] certChain = new Certificate[1];	
            certChain[0] = certificate;
            keyStore.setKeyEntry(alias, keyPair.getPrivate(), password.toCharArray(), certChain);
        } catch (KeyStoreException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public PrivateKey getPrivateKey(String alias, String password){
        try {
            return (PrivateKey)keyStore.getKey(alias, password.toCharArray());
        } catch (Exception ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public PublicKey getPublicKey(String alias){
        try {
            Certificate certificate = keyStore.getCertificate(alias);
            return certificate.getPublicKey();
        } catch (KeyStoreException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public Certificate getCertificate(String alias){
        try {
            return keyStore.getCertificate(alias);
        } catch (KeyStoreException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public boolean exportKeyStore(String path, String password){       
        try {
            File file = new File(path);
            if(file.exists())
                file.delete();
            FileOutputStream output = new FileOutputStream(file);
            keyStore.store(output, password.toCharArray());
            output.close(); 
            return true;
        } catch (Exception ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean importKeyStore(String path, String password){
        try {
            KeyStore temp = KeyStore.getInstance("pkcs12");
            FileInputStream input = new FileInputStream(path);
            temp.load(input, password.toCharArray());
            return true;
        } catch (Exception ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    

*/
}
