package com.mefaltaalgo.coirorssold.db;

import android.provider.BaseColumns;

public class FeedsDB {
	/*
	 * Nombre de la Base de Datos
	 */
	public static final String DB_NAME = "rss.db";
	/*
	 * Versión de la Base de Datos
	 */
	public static final int DB_VERSION = 1;
	/**
	 * Esta clase no debe ser instanciada
	 */
	private FeedsDB () {}
	/*
	 * Definición de la tabla posts
	 */
	public static final class Posts implements BaseColumns {
		/**
		 * Esta clase no debe ser instanciada
		 */
		private Posts () {}
		/**
		 * Orden por defecto
		 */
		public static final String DEFAULT_SORT_ORDER = "_ID DESC";
		/**
		 * Abstracción de los nombres de campos y tablas
		 * a constantes, para facilitar cambios en la estructura
		 * interna de la BD
		 */
		public static final String NOMBRE_TABLA = "feeds";
		public static final String _ID = "_id";
		public static final String TITLE = "title";
		public static final String LINK = "link";
		public static final String COMMENTS = "comments";
		public static final String PUB_DATE = "pub_date";
		public static final String CREATOR = "creator";
		public static final String DESCRIPTION = "description";
		public static final String _COUNT = "7";
	}
}
