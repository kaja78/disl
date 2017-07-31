import java.security.KeyStore

def keyAlias = "keyAlias"
def keystorePassword = 'tajne'

def key = "key"
def keyPassword='secure'

File f=new File(System.getProperty("user.home"),'.dislKeyStore' )

KeyStore ks = KeyStore.getInstance("JKS");
ks.setKeyEntry(keyAlias, key, keyPassword.toCharArray(),null);
OutputStream writeStream = new FileOutputStream(f);
ks.store(writeStream, keystorePassword.toCharArray());
writeStream.close()