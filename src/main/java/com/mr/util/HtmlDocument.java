package com.mr.util;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class HtmlDocument extends Document {

	public HtmlDocument(String baseUri) {
		super(baseUri);
	}

	public static Document getHtmlDocument(String url) {
			
			return	getHtmlDocumentDirect(url);
	}

	public static Document getHtmlDocumentDirect(String url) {
		try {
			return Jsoup
					.connect(url)
					.userAgent(
							"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; BIDUBrowser 2.x)")
					.timeout(30000).get();
		} catch (SocketTimeoutException se) {
			System.out.println("you request http \"" + url
					+ "\" time out,please wait." + se.getMessage());
		} catch (ExceptionInInitializerError ie) {
			System.out
					.println("jobContext init url error,please check jobContext "
							+ url);
		} catch (IOException e) {
			System.out.println(url);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * unicod转化为字符串
	 * 
	 * @param theString
	 * @return
	 */
	public static String unicode2String(String theString) {

		char aChar;

		int len = theString.length();

		StringBuffer outBuffer = new StringBuffer(len);

		for (int x = 0; x < len;) {

			aChar = theString.charAt(x++);

			if (aChar == '\\') {

				aChar = theString.charAt(x++);

				if (aChar == 'u') {

					// Read the xxxx

					int value = 0;

					for (int i = 0; i < 4; i++) {

						aChar = theString.charAt(x++);

						switch (aChar) {

						case '0':

						case '1':

						case '2':

						case '3':

						case '4':

						case '5':

						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}

					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';

					else if (aChar == 'n')

						aChar = '\n';

					else if (aChar == 'f')

						aChar = '\f';

					outBuffer.append(aChar);

				}

			} else

				outBuffer.append(aChar);

		}

		return outBuffer.toString();

	}

}
