/*
* This needs to be compiled and put in to the $CATALINA_HOME/lib
* as a class file. Then Tomcat 7 can be configured to use this
* as a TrustManager (see README)
*/
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class AllTrustingTrustManager implements X509TrustManager {

	public void checkClientTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
		// TODO Auto-generated method stub

	}

	public void checkServerTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
		// TODO Auto-generated method stub

	}

	public X509Certificate[] getAcceptedIssuers() {
		// TODO Auto-generated method stub
		X509Certificate[] ret = new X509Certificate[0];
		return ret;
	}

}
