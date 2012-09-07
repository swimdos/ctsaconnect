package net.ctsaconnect.datasource;

import java.util.Iterator;

/**
 * An abstract draft interface for our data sources. There is one
 * DataSourceSimple implementation that has test data. This class is both an
 * iterable and an iterator, in other words, it returns an iterator (this) that
 * is then used to retrieve one data element at a time based on the specific
 * data source implementation.
 * 
 * Each institution will code a site-specific DataSource that reads the local
 * data and populates and returns instnaces of the SimpleDataObject class.
 * 
 * The getDataSource() method needs to be changed to return the desired data
 * source. For now it returns the DataSourceSimple data source for testing.
 */
public abstract class DataSource implements Iterable<SimpleDataObject>, Iterator<SimpleDataObject> {

	public static DataSource getDataSource() {
		return new DataSourceSimple();
	}

	protected DataSource() {
	}

	@Override
	public abstract Iterator<SimpleDataObject> iterator();

	@Override
	public abstract boolean hasNext();

	@Override
	public abstract SimpleDataObject next();

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	public abstract void close();

}
