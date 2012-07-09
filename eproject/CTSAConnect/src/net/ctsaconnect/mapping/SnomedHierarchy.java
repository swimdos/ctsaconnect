package net.ctsaconnect.mapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.ctsaconnect.common.DBInfo;

public class SnomedHierarchy {

	/**
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {
		new SnomedHierarchy().go();

	}

	private void go() throws SQLException {
		Set<String> toProcessCodes = new HashSet<String>();
		Set<String> procesedCodes = new HashSet<String>();
		Connection con = DBInfo.getWriteCtsadataDbConnection();
		PreparedStatement insertPs = con.prepareStatement("");
		PreparedStatement getParentsPs = con.prepareStatement("select parents of ?");
		ResultSet rs = con.createStatement().executeQuery("select the used snomed codes");
		while (rs.next()) {
			toProcessCodes.add(rs.getString(1));
		}

		// keep looping until nothing to process
		while (toProcessCodes.size() > 0) {
			// save any new code that needs to be processed for parents
			Set<String> newCodes = new HashSet<String>();
			Iterator<String> toProcessIterator = toProcessCodes.iterator();
			while (toProcessIterator.hasNext()) {
				String childCode = toProcessIterator.next();
				// get parents
				getParentsPs.setString(1, childCode);
				ResultSet parentsRs = getParentsPs.executeQuery();
				while (parentsRs.next()) {
					String parentCode = parentsRs.getString(1);
					// save child to parent map
					insertPs.setString(1, childCode);
					insertPs.setString(2, parentCode);
					insertPs.addBatch();
					if (procesedCodes.contains(parentCode)) {
						continue;
					}
					newCodes.add(parentCode);
				}
				toProcessIterator.remove();
			}
			toProcessCodes.addAll(newCodes);
		}
		insertPs.executeBatch();
	}

}
