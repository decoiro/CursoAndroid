package com.mefaltaalgo.coirorssold.parser;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.ContentResolver;

public class RssDownloadHelper {
	public static void updateRssData(String rssUrl, ContentResolver contentResolver) {
		try {
			URL url = new URL(rssUrl);
			// Obtenemos el SAXParser
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser();
			// Creamos el Handler
			RssHandler rssHandler =	new RssHandler();
			// Definimos el manejador léxico
			saxParser.setProperty("http://xml.org/sax/properties/lexical-handler", 
					rssHandler);
			// Obtenemos el Reader
			XMLReader xr = saxParser.getXMLReader();
			xr.setContentHandler(rssHandler);
			// Parseamos el contenido
			InputSource is = new InputSource(url.openStream());
			is.setEncoding("utf-8");
			xr.parse(is);
			// El parseo ha concluido
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
