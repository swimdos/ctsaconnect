package net.ctsaconnect.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataSourceSimple extends DataSource {
	List<SimpleDataObject> testData = new ArrayList<SimpleDataObject>();
	Iterator<SimpleDataObject> i;

	DataSourceSimple() {

		testData.add(new SimpleDataObject("1234567", "91120", "", 1, 1));
		testData.add(new SimpleDataObject("1234567", "", "555.1", 4, 1));
		testData.add(new SimpleDataObject("1234567", "91120", "", 8, 6));
		testData.add(new SimpleDataObject("1234568", "91322", "", 10, 5));
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
