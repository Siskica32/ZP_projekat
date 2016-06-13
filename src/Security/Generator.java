package Security;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class Generator {
    
    //Izlazni podaci
    private KeyPair keyPair;
    private X509Certificate certificate;
    
    //Ulazni podaci
    private int keySize;
    private Date startDate; 
    private Date expiryDate;
    private BigInteger serialNumber;
    private String subject;
    private String cn;
    private String ou;
    private String o;
    private String l;
    private String st;
    private String c;
    private String e;
    private Boolean basicConstraint;
    private Boolean basicConstraintIsCritical;
    private String alternativeName;
    private Boolean alternativeNameIsCritical;
    private int keyUsage;
    private Boolean keyUsageIsCritical;
    private String path;
    
    public Generator(){
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
    
    public void generate(){
        try {
            
            //Generisanje kljuceva
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
            keyPairGenerator.initialize(keySize, SecureRandom.getInstance("SHA1PRNG"));
            keyPair = keyPairGenerator.generateKeyPair();
            
            //Formiranje imena
            X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
            nameBuilder.addRDN(BCStyle.CN, cn);
            nameBuilder.addRDN(BCStyle.OU, ou);
            nameBuilder.addRDN(BCStyle.O, o);
            nameBuilder.addRDN(BCStyle.L, l);
            nameBuilder.addRDN(BCStyle.ST, st);
            nameBuilder.addRDN(BCStyle.C, c);
            nameBuilder.addRDN(BCStyle.E, e);
            
            //Generisanje sertifikata
            X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(nameBuilder.build(), serialNumber,startDate, expiryDate,nameBuilder.build(),keyPair.getPublic());
            
            //Dodavanje basic constraint polja (ako je zadato)
            if(basicConstraint != null){
                BasicConstraints bs = new BasicConstraints(basicConstraint);
                Extension bsExtension = new Extension(Extension.basicConstraints, basicConstraintIsCritical, bs.getEncoded());           
                builder.addExtension(bsExtension);
            }
            
            //Dodavanje issuer alternative name polja (ako je zadato)
            if(alternativeName != null){
                X500Name an = new X500Name(alternativeName);
                Extension anExtension = new Extension(Extension.issuerAlternativeName, alternativeNameIsCritical, an.getEncoded());
                builder.addExtension(anExtension);
            }
            
            //Dodavanje key usage polja (ako je zadato)
            if(keyUsageIsCritical != null){
                X509KeyUsage ku = new X509KeyUsage(keyUsage);
                Extension kuExtension = new Extension(Extension.keyUsage, keyUsageIsCritical, ku.getEncoded());
                builder.addExtension(kuExtension);
            }
            
            //Potpis sertifikata
            ContentSigner sigGen = new JcaContentSignerBuilder("SHA1WithRSA").build(keyPair.getPrivate());
            
            //Nastavak generisanja sertifikata
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            
            X509CertificateHolder holder = builder.build(sigGen);

            //Konvertovanje sertifikata u java.security.cert.X509Certificate
            JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
            certificate = converter.getCertificate(holder);
            
            exportCertificate(certificate);
            
        } catch (Exception e) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void exportCertificate(X509Certificate cert){
        FileOutputStream output = null;
        
        try {
            File file = new File(path);
            if (file.exists()) 
                file.delete();
            byte[] buffer = cert.getEncoded();
            output = new FileOutputStream(file);
            Base64Encoder encoder = new Base64Encoder();
            encoder.encode(buffer, 0, buffer.length, output);
            output.close(); 
        } catch (Exception ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
           
        }
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public X509Certificate getCertificate() {
        return certificate;
    }

    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setSerialNumber(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public void setOu(String ou) {
        this.ou = ou;
    }

    public void setO(String o) {
        this.o = o;
    }

    public void setL(String l) {
        this.l = l;
    }

    public void setSt(String st) {
        this.st = st;
    }

    public void setC(String c) {
        this.c = c;
    }

    public void setE(String e) {
        this.e = e;
    }

    public void setBasicConstraint(Boolean basicConstraint) {
        this.basicConstraint = basicConstraint;
    }

    public void setBasicConstraintIsCritical(Boolean basicConstraintIsCritical) {
        this.basicConstraintIsCritical = basicConstraintIsCritical;
    }

    public void setAlternativeName(String alternativeName) {
        this.alternativeName = alternativeName;
    }

    public void setAlternativeNameIsCritical(Boolean alternativeNameIsCritical) {
        this.alternativeNameIsCritical = alternativeNameIsCritical;
    }

    public void setKeyUsage(int keyUsage) {
        this.keyUsage = keyUsage;
    }

    public void setKeyUsageIsCritical(Boolean keyUsageIsCritical) {
        this.keyUsageIsCritical = keyUsageIsCritical;
    }

    public void setPath(String path) {
        this.path = path;
    }
  
}
