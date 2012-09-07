package net.ctsaconnect.datasource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import net.ctsaconnect.common.DBInfo;

public class OhsuIcd9DataSource extends DataSource {

	Connection con;
	Statement statement;
	ResultSet rs;

	public OhsuIcd9DataSource() throws SQLException {
		con = DBInfo.getReadUmlsDbConnection();
		statement = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		statement.setFetchSize(Integer.MIN_VALUE);
	}

	@Override
	public Iterator<SimpleDataObject> iterator() {
		if (rs == null) {
			try {
				rs = statement
						.executeQuery("select i.provider_id, i.icd9_code, count(distinct i.patient_id), count(i.icd9_code) from ohsu_data.icd9 i group by i.provider_id, i.icd9_code order by i.provider_id, i.icd9_code");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return this;
	}

	@Override
	public boolean hasNext() {
		boolean hasNext;
		try {
			hasNext = rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return hasNext;
	}

	@Override
	public SimpleDataObject next() {
		SimpleDataObject o = new SimpleDataObject();
		try {
			o.practitionerID = rs.getString(1);
			o.ICD9Code = rs.getString(2);
			o.uniquePatient = rs.getInt(3);
			o.codeOccurrences = rs.getInt(4);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return o;
	}

	@Override
	public void close() {
		try {
			rs.close();
			statement.close();
			con.close();
		} catch (SQLException e) {
			try {
				con.close();
			} catch (SQLException e1) {
			}
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

}
