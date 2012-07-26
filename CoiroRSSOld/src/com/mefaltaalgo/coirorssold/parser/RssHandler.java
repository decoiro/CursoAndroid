package com.mefaltaalgo.coirorssold.parser;

import java.text.SimpleDateFormat;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import com.mefaltaalgo.coirorssold.db.FeedsDB;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

public class RssHandler extends DefaultHandler implements LexicalHandler {
	// Donde iremos guardando los datos del registro a guardar
	ContentValues rssItem;
	// Flags para saber en que nodo estamos
	private boolean in_item = false;
	private boolean in_title = false;
	private boolean in_link = false;
	private boolean in_comments = false;
	private boolean in_pubDate = false;
	private boolean in_dcCreator = false;
	private boolean in_description = false;
	private boolean in_CDATA;
	// Datos de proveedor de contenidos
	private ContentResolver contentProv;
	Uri uri = Uri.parse("content://es.exitae.blog/post");
	
	public void comment(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void endCDATA() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void endDTD() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void endEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void startCDATA() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	public void startEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void startElement(String namespaceURI, String localName,
	String qName, Attributes atts) throws SAXException {
		// Nos vamos a centrar solo en los items
		if(localName.equalsIgnoreCase("item")) {
			in_item = true;
			rssItem = new ContentValues();
		} else if(localName.equalsIgnoreCase("title")) {
			in_title = true;
		} else if(localName.equalsIgnoreCase("link")) {
			in_link = true;
		} else if(localName.equalsIgnoreCase("comments")){
			in_comments = true;
		} else if(localName.equalsIgnoreCase("pubDate")) {
			in_pubDate = true;
		} else if(localName.equalsIgnoreCase("dc:creator")) {
			in_dcCreator = true;
		} else if(localName.equalsIgnoreCase("description")) {
			in_description = true;
		}
	}
	
	@Override
	public void endElement(String namespaceURI,
	String localName, String qName) throws SAXException {
		if(localName.equalsIgnoreCase("item")) {
			contentProv.insert(uri, rssItem);
			rssItem = new ContentValues();
			in_item = false;
		} else if(localName.equalsIgnoreCase("title")) {
			in_title = false;
		} else if(localName.equalsIgnoreCase("link")) {
			in_link = false;
		} else if(localName.equalsIgnoreCase("comments")){
			in_comments = false;
		} else if(localName.equalsIgnoreCase("pubDate")) {
			in_pubDate = false;
		} else if(localName.equalsIgnoreCase("dc:creator")) {
			in_dcCreator = false;
		} else if(localName.equalsIgnoreCase("description")) {
			in_description = false;
		}
	}	
	@Override
	public void characters(char ch[], int start, int length) {
		if (in_item) { // Estamos dentro de un item
			if (in_title) {
				rssItem.put(FeedsDB.Posts.TITLE, new String
						(ch, start, length));
				} else if (in_link) {
					rssItem.put(FeedsDB.Posts.LINK, new String
							(ch, start, length));
				} else if (in_description) {
					rssItem.put(FeedsDB.Posts.DESCRIPTION, new
							String(ch, start,
									length));
				} else if (in_pubDate) {
					String strDate = new String(ch, start, length);
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM yyyy HH:mm:ss z");
					java.util.Date fechaAux = sdf.parse(strDate);
					long fecha = fechaAux.getTime();
					rssItem.put(FeedsDB.Posts.PUB_DATE, fecha);
				} catch (Exception e) {
					Log.d("RssHandler", "Error al parsear la fecha");
				}
			}
		}
	}
}
