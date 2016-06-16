package Security;

import Bean.CertificateWrapper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.BufferingContentSigner;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64Encoder;

public class FileUtil {

    public void exportCertificate(Certificate cert, String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            byte[] buffer = cert.getEncoded();
            FileOutputStream output = new FileOutputStream(file);
            Base64Encoder encoder = new Base64Encoder();
            output.write("-----BEGIN CERTIFICATE-----".getBytes());
            encoder.encode(buffer, 0, buffer.length, output);
            output.write("-----END CERTIFICATE-----".getBytes());
            output.close();
        } catch (Exception ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void exportKeyStore(String path, String password, CertificateWrapper cw) {
        try {
            if(!cw.isIsSign()) 
                return;
            
            File file = new File(path);
            if (file.exists()) 
                file.delete();
            FileOutputStream output = new FileOutputStream(file);

            KeyStore keyStore = KeyStore.getInstance("pkcs12");
            keyStore.load(null,null);
            
            Certificate[] certChain = new Certificate[1];	
            certChain[0] = cw.getCertificate();
            keyStore.setKeyEntry("key", cw.getPrivateKey(), "".toCharArray(), certChain);
            
            keyStore.store(output, password.toCharArray());  
            output.close();
            
            aesEncrypt(file, password);
            
        } catch (Exception ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void exportCSR(CertificateWrapper cw, String path) {
        Generator gen = new Generator();
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            //Formiranje imena
            X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
            nameBuilder.addRDN(BCStyle.CN, cw.getCn());
            nameBuilder.addRDN(BCStyle.OU, cw.getOu());
            nameBuilder.addRDN(BCStyle.O, cw.getO());
            nameBuilder.addRDN(BCStyle.L, cw.getL());
            nameBuilder.addRDN(BCStyle.ST, cw.getSt());
            nameBuilder.addRDN(BCStyle.C, cw.getC());

            PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(nameBuilder.build(), cw.getPublicKey());
            JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA1withRSA");
            ContentSigner signer = csBuilder.build(cw.getPrivateKey());
            PKCS10CertificationRequest csr = p10Builder.build(signer);
            ContentSigner sigGen = new BufferingContentSigner(signer);

            byte[] buffer = sigGen.getSignature();

            FileOutputStream output = new FileOutputStream(file);
            Base64Encoder encoder = new Base64Encoder();
            encoder.encode(buffer, 0, buffer.length, output);
            output.close();
        } catch (Exception ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void aesEncrypt(File file, String password){
        try{
            
            FileInputStream input = new FileInputStream(file);
            byte[] content = new byte[(int)file.length()];
            input.read(content);
            
            MessageDigest sha = MessageDigest.getInstance("SHA-1"); 
            byte[] key =  password.getBytes("UTF-8");
            key = Arrays.copyOf(sha.digest(key), 16);
            SecretKey secretKey = new SecretKeySpec(key,"AES");
            
            Cipher aes = Cipher.getInstance("AES");
            aes.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encodedText = aes.doFinal(content);
            
            input.close();
            
            FileOutputStream output = new FileOutputStream(file);
            output.write(encodedText);
            output.close();
            
        }catch (Exception ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void aesDecrypt(File file, String password){ 
        try{
            
            FileInputStream input = new FileInputStream(file);
            byte[] content = new byte[(int)file.length()];
            input.read(content);
            
            MessageDigest sha = MessageDigest.getInstance("SHA-1"); 
            byte[] key =  password.getBytes("UTF-8");
            key = Arrays.copyOf(sha.digest(key), 16);
            SecretKey secretKey = new SecretKeySpec(key,"AES");
            
            Cipher aes = Cipher.getInstance("AES");
            aes.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] encodedText = aes.doFinal(content);
            
            input.close();
            
            FileOutputStream output = new FileOutputStream(file);
            output.write(encodedText);
            output.close();
            
        }catch (Exception ex) {
            //Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }

    public CertificateWrapper importKeyStore(String path, String password) {
        try {
            File file = new File(path);
            if (!file.exists()) 
                return null;
            
            aesDecrypt(file,password);
            
            KeyStore keyStore = KeyStore.getInstance("pkcs12");
            FileInputStream input = new FileInputStream(path);
            keyStore.load(input, password.toCharArray());
                              
            Certificate certificate = keyStore.getCertificate("key");
            PublicKey publicKey = certificate.getPublicKey();
            PrivateKey privateKey = (PrivateKey)keyStore.getKey("key", "".toCharArray());
            KeyPair keyPair = new KeyPair(publicKey, privateKey);
            
            
            InputStream in = new ByteArrayInputStream(certificate.getEncoded());
            BouncyCastleProvider provider = new BouncyCastleProvider();
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509", provider);
            X509Certificate x509cert = (X509Certificate) certificateFactory.generateCertificate(in);
      
            CertificateWrapper cw = new CertificateWrapper(keyPair);
            cw.setKeySize(((RSAPublicKey)publicKey).getModulus().bitLength());
            
            X500Name x500name = new JcaX509CertificateHolder(x509cert).getSubject();
            RDN cn = x500name.getRDNs(BCStyle.CN)[0];
            RDN ou = x500name.getRDNs(BCStyle.OU)[0];
            RDN o = x500name.getRDNs(BCStyle.O)[0];
            RDN l = x500name.getRDNs(BCStyle.L)[0];
            RDN st = x500name.getRDNs(BCStyle.ST)[0];
            RDN c = x500name.getRDNs(BCStyle.C)[0];
            
            cw.setCn(IETFUtils.valueToString(cn.getFirst().getValue()));
            cw.setOu(IETFUtils.valueToString(ou.getFirst().getValue()));
            cw.setO(IETFUtils.valueToString(o.getFirst().getValue()));
            cw.setL(IETFUtils.valueToString(l.getFirst().getValue()));
            cw.setSt(IETFUtils.valueToString(st.getFirst().getValue()));
            cw.setC(IETFUtils.valueToString(c.getFirst().getValue()));
            
            cw.setStartDate(x509cert.getNotBefore());
            cw.setExpiryDate(x509cert.getNotAfter());
            cw.setSerialNumber(x509cert.getSerialNumber());
            cw.setBasicConstraintPath(x509cert.getBasicConstraints());
            if(cw.getBasicConstraintPath() >0) cw.setBasicConstraint(Boolean.TRUE);
            else cw.setBasicConstraint(Boolean.FALSE);
            
            //alternative name
            Collection<List<?>> col = x509cert.getIssuerAlternativeNames();
            if(col != null){
                Iterator i = col.iterator();
                while(i.hasNext()){
                    List<String> list = (List)i.next();
                    cw.setAlternativeName(list.get(1));
                }
            }
            
            //key usage
            boolean usage[] = x509cert.getKeyUsage();
            if(usage != null){
                if(usage.length >8){
                    cw.calculateKeyUsage(usage[0], usage[1], usage[2], usage[3], usage[4], usage[5], usage[6], usage[7], usage[8]);
                }
            }
            
            cw.setCertificate(certificate);
            cw.setIsSign(true);
            
            return cw;
        } catch (Exception ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
