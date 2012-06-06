package net.ctsaconnect.data;

import java.util.Iterator;

/**
 * A draft interface for our data sources. There is one DataSourceSimple
 * implementation that has test data. This class is both an iterable and an
 * iterator, in other words, it returns an iterator (this) that is then used to
 * retrieve one element at a time based on the specific data source
 * implementation.
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

}
