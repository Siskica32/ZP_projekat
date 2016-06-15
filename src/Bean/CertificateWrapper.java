package Bean;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.beans.property.SimpleStringProperty;

public class CertificateWrapper {
    
    private KeyPair keyPair;
    private Certificate certificate;
    
    private int keySize;
    private Date startDate; 
    private Date expiryDate;
    private BigInteger serialNumber;
    private String cn;
    private String ou;
    private String o;
    private String l;
    private String st;
    private String c;
    private Boolean basicConstraint;
    private int basicConstraintPath;
    private Boolean basicConstraintIsCritical;
    private String alternativeName;
    private Boolean alternativeNameIsCritical;
    private int keyUsage;
    private Boolean keyUsageIsCritical;
    private boolean isSign;
    
    //Za tabelu
    private SimpleStringProperty keySizeString;
    private SimpleStringProperty startDateString; 
    private SimpleStringProperty expiryDateString;
    private SimpleStringProperty serialNumberString;
    private SimpleStringProperty cnString;
    private SimpleStringProperty ouString;
    private SimpleStringProperty oString;
    private SimpleStringProperty lString;
    private SimpleStringProperty stString;
    private SimpleStringProperty cString;
    private SimpleStringProperty basicConstraintString;
    private SimpleStringProperty basicConstraintPathString;
    private SimpleStringProperty alternativeNameString;
    private SimpleStringProperty keyUsageString;
    private SimpleStringProperty isSignString;
    
    public CertificateWrapper(){
        keySizeString = new SimpleStringProperty();
        startDateString = new SimpleStringProperty();
        expiryDateString = new SimpleStringProperty();
        serialNumberString = new SimpleStringProperty();
        cnString = new SimpleStringProperty();
        ouString = new SimpleStringProperty();
        oString = new SimpleStringProperty();
        lString = new SimpleStringProperty();
        stString = new SimpleStringProperty();
        cString = new SimpleStringProperty();
        basicConstraintString = new SimpleStringProperty();
        basicConstraintPathString = new SimpleStringProperty();
        alternativeNameString = new SimpleStringProperty();
        isSignString = new SimpleStringProperty();
    }

    public CertificateWrapper(KeyPair keyPair) {
        super();
        this.keyPair = keyPair;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }
    
    public PublicKey getPublicKey(){
        return keyPair.getPublic() ;
    }
    
    public PrivateKey getPrivateKey(){
        return keyPair.getPrivate();
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public int getKeySize() {
        return keySize;
    }

    public void setKeySize(int keySize) {
        setKeySizeString(keySize + "");
        this.keySize = keySize;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        setStartDateString(df.format(startDate));
        this.startDate = startDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        setExpiryDateString(df.format(expiryDate));
        this.expiryDate = expiryDate;
    }

    public BigInteger getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(BigInteger serialNumber) {
        setSerialNumberString(serialNumber + "");
        this.serialNumber = serialNumber;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        setCnString(cn);
        this.cn = cn;
    }

    public String getOu() {
        return ou;
    }

    public void setOu(String ou) {
        setOuString(ou);
        this.ou = ou;
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        setoString(o);
        this.o = o;
    }

    public String getL() {
        return l;
    }

    public void setL(String l) {
        setlString(l);
        this.l = l;
    }

    public String getSt() {
        return st;
    }

    public void setSt(String st) {
        setStString(st);
        this.st = st;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        setcString(c);
        this.c = c;
    }

    public Boolean getBasicConstraint() {
        return basicConstraint;
    }

    public void setBasicConstraint(Boolean basicConstraint) {
        setBasicConstraintString(basicConstraint + "");
        this.basicConstraint = basicConstraint;
    }

    public int getBasicConstraintPath() {
        return basicConstraintPath;
    }

    public void setBasicConstraintPath(int basicConstraintPath) {
        setBasicConstraintPathString(basicConstraintPath +"");
        this.basicConstraintPath = basicConstraintPath;
    }

    public Boolean getBasicConstraintIsCritical() {
        return basicConstraintIsCritical;
    }

    public void setBasicConstraintIsCritical(Boolean basicConstraintIsCritical) {
        this.basicConstraintIsCritical = basicConstraintIsCritical;
    }

    public String getAlternativeName() {
        return alternativeName;
    }

    public void setAlternativeName(String alternativeName) {
        setAlternativeNameString(alternativeName);
        this.alternativeName = alternativeName;
    }

    public Boolean getAlternativeNameIsCritical() {
        return alternativeNameIsCritical;
    }

    public void setAlternativeNameIsCritical(Boolean alternativeNameIsCritical) {
        this.alternativeNameIsCritical = alternativeNameIsCritical;
    }

    public int getKeyUsage() {
        return keyUsage;
    }

    public void setKeyUsage(int keyUsage) {
        setKeyUsageString(keyUsage + "");
        this.keyUsage = keyUsage;
    }

    public Boolean getKeyUsageIsCritical() {
        return keyUsageIsCritical;
    }

    public void setKeyUsageIsCritical(Boolean keyUsageIsCritical) {
        this.keyUsageIsCritical = keyUsageIsCritical;
    }

    public boolean isIsSign() {
        return isSign;
    }

    public void setIsSign(boolean isSign) {
        setIsSignString(isSign + "");
        this.isSign = isSign;
    }
      
    /*
     *
     * Za tabelu
     *
     */

    public String getKeySizeString() {
        return keySizeString.get();
    }

    public void setKeySizeString(String keySizeString) {
        this.keySizeString.set(keySizeString);;
    }

    public String getStartDateString() {
        return startDateString.get();
    }

    public void setStartDateString(String startDateString) {
        this.startDateString.set(startDateString);
    }

    public String getExpiryDateString() {
        return expiryDateString.get();
    }

    public void setExpiryDateString(String expiryDateString) {
        this.expiryDateString.set(expiryDateString);
    }

    public String getSerialNumberString() {
        return serialNumberString.get();
    }

    public void setSerialNumberString(String serialNumberString) {
        this.serialNumberString.set(serialNumberString);
    }

    public String getCnString() {
        return cnString.get();
    }

    public void setCnString(String cnString) {
        this.cnString.set(cnString);
    }

    public String getOuString() {
        return ouString.get();
    }

    public void setOuString(String ouString) {
        this.ouString.set(ouString);
    }

    public String getoString() {
        return oString.get();
    }

    public void setoString(String oString) {
        this.oString.set(oString);
    }

    public String getlString() {
        return lString.get();
    }

    public void setlString(String lString) {
        this.lString.set(lString);
    }

    public String getStString() {
        return stString.get();
    }

    public void setStString(String stString) {
        this.stString.set(stString);
    }

    public String getcString() {
        return cString.get();
    }

    public void setcString(String cString) {
        this.cString.set(cString);
    }

    public String getBasicConstraintString() {
        return basicConstraintString.get();
    }

    public void setBasicConstraintString(String basicConstraintString) {
        this.basicConstraintString.set(basicConstraintString);
    }

    public String getBasicConstraintPathString() {
        return basicConstraintPathString.get();
    }

    public void setBasicConstraintPathString(String basicConstraintPathString) {
        this.basicConstraintPathString.set(basicConstraintPathString);
    }

    public String getAlternativeNameString() {
        return alternativeNameString.get();
    }

    public void setAlternativeNameString(String alternativeNameString) {
        this.alternativeNameString.set(alternativeNameString);
    }

    public String getKeyUsageString() {
        return keyUsageString.get();
    }

    public void setKeyUsageString(String keyUsageString) {
        this.keyUsageString.set(keyUsageString);
    }

    public String getIsSignString() {
        return isSignString.get();
    }

    public void setIsSignString(String isSignString) {
        this.isSignString.set(isSignString);
    }

}
