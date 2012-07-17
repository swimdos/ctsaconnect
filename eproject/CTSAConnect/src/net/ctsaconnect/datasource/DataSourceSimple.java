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
	private List<SimpleDataObject> SimpleDataList = new ArrayList<SimpleDataObject>();
	private Iterator<SimpleDataObject> i;

	DataSourceSimple() {
		SimpleDataList.add(new SimpleDataObject("1234567", "91120", "", 1, 1));
		SimpleDataList.add(new SimpleDataObject("1234567", "", "555.1", 4, 1));
		SimpleDataList.add(new SimpleDataObject("1234567", "91120", "", 8, 6));
		SimpleDataList.add(new SimpleDataObject("1234568", "76376", "", 10, 5));
		i = SimpleDataList.iterator();
	}
	
	DataSourceSimple(Boolean withTestData){
		if (withTestData){
			SimpleDataList.add(new SimpleDataObject("1234567", "91120", "", 1, 1));
			SimpleDataList.add(new SimpleDataObject("1234567", "", "555.1", 4, 1));
			SimpleDataList.add(new SimpleDataObject("1234567", "91120", "", 8, 6));
			SimpleDataList.add(new SimpleDataObject("1234568", "76376", "", 10, 5));
			i = SimpleDataList.iterator();
		}
		else{
			i = SimpleDataList.iterator();
		}
			
	}
	
	public void addSimpleData(SimpleDataObject newObject){
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
	
	public int length(){
		return SimpleDataList.size();
	}

}
