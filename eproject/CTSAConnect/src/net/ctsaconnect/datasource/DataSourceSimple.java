package net.ctsaconnect.datasource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple example of a DataSource for testing. This datasource creates
 * instances of the SimpleDataObject class and adds them to a list. The list is
 * then used as the data source to return the data objects.
 */
public class DataSourceSimple extends DataSource {
	private List<SimpleDataObject> testData = new ArrayList<SimpleDataObject>();
	private Iterator<SimpleDataObject> i;

	DataSourceSimple() {

		testData.add(new SimpleDataObject("1234567", "", "552.00", 1, 1));
		testData.add(new SimpleDataObject("1234567", "", "555.1", 4, 1));
		testData.add(new SimpleDataObject("1234567", "", "553.02", 8, 6));
		testData.add(new SimpleDataObject("1234568", "", "745.12", 10, 5));
		i = testData.iterator();

	}

	@Override
	public Iterator<SimpleDataObject> iterator() {
		return i;
	}

	@Override
	public boolean hasNext() {
		return i.hasNext();
	}

	@Override
	public SimpleDataObject next() {
		return i.next();
	}

}
