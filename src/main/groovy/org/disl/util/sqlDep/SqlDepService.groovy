/*
 *
 * Copyright 2015 - 2021 Karel Hübl <karel.huebl@gmail.com>.
 *
 * This file is part of disl.
 *
 * Disl is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Disl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Disl.  If not, see <http://www.gnu.org/licenses/>.
 */
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
