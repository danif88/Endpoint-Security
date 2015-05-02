import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.InitialContext;

import org.apache.commons.codec.binary.Base64;

public class SimpleEncryption
{
  private ResourceBundle beProperties = null;

  private String encryptKey = "";
  private String ivx = "8888888888888888";

  public void setEncryptKey(String encryptKey)
  {
    this.encryptKey = encryptKey;
  }

  public String getEncryptKey() {
    return this.encryptKey;
  }

  private void setEncryptKey()
  {
    try
    {
      InitialContext ctx = new InitialContext();
      this.encryptKey = ctx.lookup("java:comp/env/ENCRIPT_KEY").toString();
    } catch (Exception e) {
      System.out.println("Error al obtener la url del backoffice." + e);
    }

    if (this.encryptKey.trim().equals(""))
      this.encryptKey = this.beProperties.getString("encryptKey");
  }

  public byte[] encrypt(String toEnc)
  {
    setEncryptKey("0123456789ABCDEF");

    byte[] raw = (byte[])null;
    try {
      SecretKeySpec keySpec = new SecretKeySpec(this.encryptKey.getBytes(), "AES");
      IvParameterSpec ivSpec = new IvParameterSpec(this.ivx.getBytes());

      Cipher cipher = getCypher(keySpec, ivSpec, 1);

      byte[] stringBytes = toEnc.getBytes("UTF8");
      raw = cipher.doFinal(stringBytes);
    }
    catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    }
    catch (BadPaddingException e) {
      e.printStackTrace();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return raw;
  }

  public String decrypt(byte[] toDec)
  {
    setEncryptKey();

    String clear = "";
    try {
      SecretKeySpec keySpec = new SecretKeySpec(this.encryptKey.getBytes(), "AES");
      IvParameterSpec ivSpec = new IvParameterSpec(this.ivx.getBytes());

      Cipher cipher = getCypher(keySpec, ivSpec, 2);

      byte[] stringBytes = cipher.doFinal(toDec);

      clear = new String(stringBytes, "UTF8");
    }
    catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    }
    catch (BadPaddingException e) {
      e.printStackTrace();
    }
    catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return clear;
  }

  public String encode(byte[] toEncode)
  {
    if (toEncode == null)
      System.out.println("[SimpleEncryption][encode] Mensaje : El parámetro viene Nulo.");
    String encoded = "";
    try {
      encoded = new String(Base64.encodeBase64(toEncode, false, true), "UTF8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return encoded;
  }

  public byte[] decode(String toDecode) throws UnsupportedEncodingException
  {

    if ((toDecode != null) && (!toDecode.equals(""))) {
        byte[] decoded = Base64.decodeBase64(toDecode.getBytes("UTF8"));
        return decoded;
    }

    return null;
  }

  public String encodePath(String path)
  {
    if (path == null)
      System.out.println("[SimpleEncryption][encodePath] Mensaje : El parámetro viene Nulo.");
    StringTokenizer st = new StringTokenizer(path, "/");
    String result = "";
    while (st.hasMoreElements()) {
      String toEnc = (String)st.nextElement();
      result = result + encode(encrypt(toEnc)) + "|";
    }
    return result.substring(0, result.length() - 1);
  }

  public String decodePath(String path) throws UnsupportedEncodingException
  {
    StringTokenizer st = new StringTokenizer(path, "|");
    String result = "";
    while (st.hasMoreElements()) {
      String toEnc = (String)st.nextElement();
      result = result + decrypt(decode(toEnc)) + "/";
    }
    return result.substring(0, result.length() - 1);
  }

  public static String daVueltaRut(String rut) {
    String nuevoRut = "";
    for (int i = rut.length() - 1; i >= 0; i--)
      nuevoRut = nuevoRut + rut.charAt(i);
    return nuevoRut;
  }

  public static void main(String[] args)
  {
	  long timeS = System.currentTimeMillis();
	  
	  SimpleEncryption web_cryptpass = new SimpleEncryption();
	  System.out.println(web_cryptpass.encode(web_cryptpass.encrypt(args[0] + "&" + args[1] + "&" + timeS)));
  }

  public static Cipher getCypher(SecretKeySpec keySpec, IvParameterSpec ivSpec, int mode)
    throws Exception
  {
	Cipher cipher;
    try
    {
      cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    }
    catch (NoSuchAlgorithmException e)
    {
      throw new RuntimeException("invalid algorithm", e);
    } catch (NoSuchPaddingException e) {
      throw new RuntimeException("invalid padding", e);
    }
    try { cipher.init(mode, keySpec, ivSpec);
    } catch (InvalidKeyException e) {
      throw new InvalidKeyException("invalid key", e);
    } catch (InvalidAlgorithmParameterException e) {
      throw new RuntimeException("invalid algorithm parameter.", e);
    }
    return cipher;
  }
}
