package net.ctsaconnect.common;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Util {

	public static DateTime randomDate(String startDate, String endDate) {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy");
		DateTime startdt = fmt.parseDateTime(startDate);
		DateTime enddt = fmt.parseDateTime(endDate);
		Date jdkstartDate = startdt.toDate();
		Date jdkendDate = enddt.toDate();
		long start = jdkstartDate.getTime();
		long end = jdkendDate.getTime();
		long diff = end - start;
		Random random = new Random();
		long rand = random.nextLong();
		rand = (rand % diff) + start;
		Date randomDate = new Date(rand);
		DateTime mydate = new DateTime(randomDate);
		return mydate;

	}

	public static String getRandomId(String someCode ){
		
		return UUID.randomUUID().toString();
	}

}
