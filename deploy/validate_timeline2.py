import sys

def parse_date(date):
    date = str(date)
    year = int(date[:4])
    month = int(date[4]+date[5])
    day = int(date[6]+date[7])
    return year, month, day


def change_date(line, m, d, t):
    p = line.split("-")
    prefix = "-".join(p[:-2])
    date = p[-2]
    time = p[-1]

    date = date[:4]+("%.2d" % m)+("%.2d" % d)
    time = time[:4]+'c'+time[5:]
    time = ("%.2d" % t)+time[2:]
    return prefix+"-"+date+"-"+time


DAYS_PER_MONTH = [31,28,31,30,31,30,31,31,30,31,30,31]

if __name__=="__main__":

    print "[validate timeline] Reads list of gz files (wikipedia pageviews)"
    print "[validate timeline] from the input and validates if it is a proper year."

    dt2line = {}
    for line in sys.stdin.readlines():
        try:
            p = line.split('-')
            date = p[-2]
            time = int(p[-1][0]+p[-1][1])
            year, month, day = parse_date(date)
            dt2line[(month, day, time)] = line.strip()
        except:
            print "[validate timeline] Error while parsing filename:"+line.strip()
    #print dt2line 

    prevline = None
    for month in xrange(12):
        days = DAYS_PER_MONTH[month]
        for day in xrange(days):
            for time in xrange(24):
                m,d,t = month+1, day+1, time
                if (m, d, t) in dt2line:
                    prevline = dt2line[(m, d, t)]
                else:
                    print "[validate timeline] No file for: month=%i day=%i time=%i. Try fixing:" % (m,d,t)
                    print " cp",prevline,change_date(prevline, m, d, t)
    

    
