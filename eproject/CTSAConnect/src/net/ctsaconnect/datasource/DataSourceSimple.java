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
	public List<SimpleDataObject> SimpleDataList = new ArrayList<SimpleDataObject>();
	public Iterator<SimpleDataObject> i;

	DataSourceSimple() {
		// SimpleDataList.add(new SimpleDataObject("1234567", "", "552.00", 1, 1));
		// SimpleDataList.add(new SimpleDataObject("1234567", "", "555.1", 4, 1));
		// SimpleDataList.add(new SimpleDataObject("1234567", "", "553.02", 8, 6));
		// SimpleDataList.add(new SimpleDataObject("1234568", "", "745.12", 10, 5));
		i = SimpleDataList.iterator();
	}

	DataSourceSimple(Boolean withTestData) {
		if (withTestData) {
			// SimpleDataList.add(new SimpleDataObject("1234567", "", "552.00", 1,
			// 1));
			// SimpleDataList.add(new SimpleDataObject("1234567", "", "555.1", 4, 1));
			// SimpleDataList.add(new SimpleDataObject("1234567", "", "553.02", 8,
			// 6));
			// SimpleDataList.add(new SimpleDataObject("1234568", "", "745.12", 10,
			// 5));
			i = SimpleDataList.iterator();
		} else {
			i = SimpleDataList.iterator();
		}

	}

	public void addSimpleData(SimpleDataObject newObject) {
		SimpleDataList.add(newObject);
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

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	public int length() {
		return SimpleDataList.size();
	}

	public String print() {
		StringBuffer strBuff = new StringBuffer();
		for (SimpleDataObject sdo : SimpleDataList) {
			strBuff.append(sdo.print() + "\n");
		}
		return strBuff.toString();
	}

}
