package org.disl.util.sqlDep

import groovy.json.JsonOutput

import java.security.cert.CertificateException
import java.security.cert.X509Certificate

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class SqlDepService {

	public static final String SQL_DEP_PRODUCTION_URL='https://sqldep.com/api/rest'
	public static final String SQL_DEP_DEBUG_URL='https://private-anon-f16776c8c-sqldep.apiary-proxy.com/api/rest'
	public static final String SQL_DEP_MOCK_URL='https://private-anon-f16776c8c-sqldep.apiary-mock.com/api/rest'

	String restUrl=SQL_DEP_PRODUCTION_URL

	Object post(CreateSqlSetRequest request) {
		URL url=new URL("${restUrl}/sqlset/create/")
		HttpsURLConnection connection=url.openConnection()
		connection.setSSLSocketFactory(getIgnoreSSLSocketFactory())
		connection.setDoOutput(true)
		connection.setDoInput(true)
		connection.setRequestProperty('content-type', 'application/json')

		String body=request.toJson()

		connection.outputStream.withWriter { Writer writer -> writer << body }


		String response = connection.inputStream.withReader { Reader reader -> reader.text }
		try {
			response=JsonOutput.prettyPrint(response)
		} finally {
			return response
		}
	}

	private SSLSocketFactory getIgnoreSSLSocketFactory() {
		TrustManager[] trustAllCerts = [
			new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					// Not implemented
				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					// Not implemented
				}
			}
		]

		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		return sc.getSocketFactory()
	}
}
