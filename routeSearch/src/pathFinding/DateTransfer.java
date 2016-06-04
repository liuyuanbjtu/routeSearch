package pathFinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateTransfer {
	public long time=0;
	public int day=0;
	public int hour=0;
	public int minute=0;
	public int second=0;
	public String dateStr;
	public void timeToDate()
	{ 
		day=(int)(time/(3600*24));
		hour=(int)((time-day*3600*24)/3600);
		minute=(int)((time-day*3600*24-hour*3600)/60);
		second=(int)(time-day*3600*24-hour*3600-minute*60);
	}

	public void dateTotime()
	{
		time=day*3600*24+hour*3600+minute*60+second;
	}
	public long dateStrTotime(int dayRef)
	{
		try
		{
			DateFormat dateFormat=DateFormat.getDateTimeInstance();
			Date date=dateFormat.parse(dateStr);
			time=date.getHours()*3600+date.getMinutes()*60+date.getSeconds();
         	if(date.getDate()!=dayRef)
         	{
         			time+=24*3600;
         	}
         	return time;
		}
		catch (ParseException e)
	    {
	         e.printStackTrace();
	    }
		return time;
	}
}
