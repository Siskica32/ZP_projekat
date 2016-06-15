package Security;

import Bean.CertificateWrapper;
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
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.jce.X509KeyUsage;
import org.bouncycastle.operator.BufferingContentSigner;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class Generator {
    

    
    public Generator(){
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }
    
    public KeyPair generateKeyPair(int keySize){   
        try{
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
            keyPairGenerator.initialize(keySize, SecureRandom.getInstance("SHA1PRNG"));
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
    
    public X509Certificate generateCertificate(CertificateWrapper cw){
        try {     
            
            //Formiranje imena
            X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
            nameBuilder.addRDN(BCStyle.CN, cw.getCn());
            nameBuilder.addRDN(BCStyle.OU, cw.getOu());
            nameBuilder.addRDN(BCStyle.O, cw.getO());
            nameBuilder.addRDN(BCStyle.L, cw.getL());
            nameBuilder.addRDN(BCStyle.ST, cw.getSt());
            nameBuilder.addRDN(BCStyle.C, cw.getC());
            
            //Generisanje sertifikata
            X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(nameBuilder.build(), cw.getSerialNumber(),cw.getStartDate(),
                                                                                cw.getExpiryDate(),nameBuilder.build(),cw.getPublicKey());
            
            //Dodavanje basic constraint polja (ako je zadato)
            if(cw.getBasicConstraint() != null){
                BasicConstraints bs;
                if(cw.getBasicConstraint() == true) bs= new BasicConstraints(cw.getBasicConstraintPath());
                else bs= new BasicConstraints(false);
                Extension bsExtension = new Extension(Extension.basicConstraints, cw.getBasicConstraintIsCritical(), bs.getEncoded());           
                builder.addExtension(bsExtension);
            }
            
            //Dodavanje issuer alternative name polja (ako je zadato)
            if(cw.getAlternativeName() != null){
                X500Name an = new X500Name(cw.getAlternativeName());
                Extension anExtension = new Extension(Extension.issuerAlternativeName, cw.getAlternativeNameIsCritical(), an.getEncoded());
                builder.addExtension(anExtension);
            }
            
            //Dodavanje key usage polja (ako je zadato)
            if(cw.getKeyUsageIsCritical() != null){
                X509KeyUsage ku = new X509KeyUsage(cw.getKeyUsage());
                Extension kuExtension = new Extension(Extension.keyUsage, cw.getKeyUsageIsCritical(), ku.getEncoded());
                builder.addExtension(kuExtension);
            }
            
            //Potpis sertifikata
            //ContentSigner sigGen = new JcaContentSignerBuilder("SHA1WithRSA").build(cw.getPrivateKey());
            PKCS10CertificationRequestBuilder p10Builder = new JcaPKCS10CertificationRequestBuilder(nameBuilder.build(), cw.getPublicKey());
            JcaContentSignerBuilder csBuilder = new JcaContentSignerBuilder("SHA1withRSA");
            ContentSigner signer = csBuilder.build(cw.getPrivateKey());
            PKCS10CertificationRequest csr = p10Builder.build(signer);
            ContentSigner sigGen = new BufferingContentSigner(signer);
            
            //Nastavak generisanja sertifikata
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            
            X509CertificateHolder holder = builder.build(sigGen);

            //Konvertovanje sertifikata u java.security.cert.X509Certificate
            JcaX509CertificateConverter converter = new JcaX509CertificateConverter();
            return converter.getCertificate(holder);
            
        } catch (Exception e) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    } 
  
}
