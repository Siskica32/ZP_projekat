
package Swing;

import Bean.CertificateWrapper;
import Bean.User;
import Security.FileUtil;
import Security.Generator;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.Certificate;
import java.util.Calendar;
import java.util.Date;

public class Test {
    
    //Ovo je test
    public static void main(String args[]){
        
        CertificateWrapper cw = new CertificateWrapper();
        Generator gen = new Generator();
        FileUtil fu = new FileUtil();
        cw.setKeyPair(gen.generateKeyPair(1024));
        cw.setCn("mitar");
        cw.setSerialNumber(BigInteger.ZERO);
        cw.setExpiryDate(new Date(2020,2,2));
        cw.setStartDate(new Date());
        cw.setL("asd");
        cw.setO("asdf");
        cw.setOu("af");
        cw.setSt("asfd");
        cw.setC("f");
        cw.setAlternativeName("dns sam ja");
        cw.setAlternativeNameIsCritical(Boolean.TRUE);
        cw.setKeyUsage(3);
        cw.setKeyUsageIsCritical(Boolean.TRUE);
        cw.setIsSign(true);
        
        Certificate c = gen.generateCertificate(cw);
        cw.setCertificate(c);
        //fu.exportCertificate(c, "C://Users//Admin//Desktop//cert.cer");
        //fu.exportKeyStore("C://Users//Admin//Desktop//s.bin", "pass", cw);
       
        
        CertificateWrapper cw1 = fu.importKeyStore("C://Users//Admin//Desktop//test.p12", "misa");
        System.out.println(cw1.getCn());
        
        
        
        
        
        
        
        
        
        
        
        
        /**
         * 
         * KOD ZA GENERISANJE NOVOG PARA KLJUCEVA (I SERTIFIKATA)
         * UZ POTPISIVANJE I IZVOZ SERTIFIKATA
         * 
         */
        
        //Generator gen = new Generator();
        
        Calendar cal = Calendar.getInstance();
        cal.set(2016, 9, 25);
 /*
        gen.setKeySize(1024);
        gen.setStartDate(new Date());
        gen.setExpiryDate(new Date(cal.getTimeInMillis()));
        gen.setSerialNumber(new BigInteger("123456"));
        gen.setCn("Dragance");
        gen.setOu("Opatija");
        gen.setO("Opatija");
        gen.setL("Pakao");
        gen.setSt("Beograd");
        gen.setC("Srbija");
        gen.setBasicConstraint(true);
        gen.setBasicConstraintIsCritical(true);
        gen.setAlternativeName(null);
        gen.setAlternativeNameIsCritical(null);
        gen.setKeyUsage(0);
        gen.setKeyUsageIsCritical(true);
        gen.setPath("C://Users//Admin//Desktop//sertifikat.cer");
        
        //gen.generate();
        
        //Dohvatanje prethodno izgenerisanog kljuca i sertifikata
        KeyPair keyPair = gen.getKeyPair();
        Certificate certificate = gen.getCertificate();
        
        //Kreiranje korisnika
        User user = new User();
        
        //Dodela kljuca i sertifikata korisniku
        user.addKey("alias2",keyPair,"password",certificate);
        
        //Dohvatanje kljuca
        user.getPrivateKey("alias2", "password");
        
        //Izvoz keyStore-a
        user.exportKeyStore("C://Users//Admin//Desktop//sertifikat.bin", "pass");

*/
    }
     
}
