import sys

def parse_date(date):
	date = str(date)
	year = int(date[:4])
	month = int(date[4]+date[5])
	day = int(date[6]+date[7])
	return year, month, day


def is_possible(prev_date, date):
	prev_year, prev_month, prev_day = parse_date(prev_date)
	year, month, day = parse_date(date)
        return (year==prev_year and month==prev_month and day==prev_day+1) or\
               (year==prev_year and day==1 and month==prev_month+1) or\
               (day==1 and month==1 and year==prev_year+1)
          

if __name__=="__main__":

	print "Reads sorted list of gz files (wikipedia pageviews)"
	print " from the input and validates if it is a proper timeline."
	print "TODO: Currently if there are some days missing at the end of the month it is not detected!"

	errs = 0
	prev_date, prev_time = None, None
	for line in sys.stdin.xreadlines():
		p = line.split('-')
		date = int(p[-2])
		time = int(p[-1][0]+p[-1][1])
		if not prev_date is None: 
			if time==0: #we should have date change
				if not is_possible(prev_date, date):		
					errs += 1
					print "WRONG DATE AFTER DATE ERROR: date=", prev_date,"->",date, " time=",time
					print " line=",line
			else: #we should not have date change and have time changed by 1
				if prev_date != date:
					errs += 1
					print "ILLEGAL DATA CHANGE ERROR: time=",time," date=",prev_date,"->",date
					print " line=",line
				elif prev_time+1 != time:
					errs += 1
					print "ILLEGAL TIME CHANGE ERROR: time=",prev_time,"->",time," date=",date
					print " line",line
	
		prev_date, prev_time = date, time 

	if errs==0: print "NO ERRORS FOUND!"
	else: print errs,"ERRORS FOUND IN THE LIST OF FILES"

