"""Reads lines and keeps only these with column value present on the list of allowed values."""

import sys

def load_allowed(fin):
    allowed = set()
    for line in fin.xreadlines():
        allowed.add( line.strip().lower() )
    return allowed


def filter_lines(fin, fout, column_no, allowed=set()):
    kept = 0
    column_no -= 1
    for processed,original_line in enumerate(fin.xreadlines()):
        #if processed%100000==0: print kept,"kept out of",processed,"processed"
        line = original_line.lower().strip()
        if line=="": continue
        value = line.split(" ")[column_no]
        if value in allowed:
            fout.write(original_line)
            kept += 1
    return kept, (processed+1)


if __name__=="__main__":

    fin = sys.stdin
    fout = sys.stdout
    sys.stdout = sys.stderr

    print "[column fgrep] The program reads lines from stdin and keeps only these with column value present in the file."
    print "[column fgrep] Two arguments expected: file with list of allowed values, column number"

    try:
        allowed_values_path = sys.argv[1]
        column_no = int(sys.argv[2])
    except:
        print "[column fgrep] Error: Two arguments expected: file with list of allowed values, column number"
        sys.exit(-1)

    print "[column fgrep] allowed_values_path =",allowed_values_path," column_no =",column_no

    allowed = load_allowed(open(allowed_values_path))
    print "[column fgrep]",len(allowed), "allowed values loaded"

    kept,processed = filter_lines(fin, fout, column_no, allowed)
    print "[column fgrep]",kept,"kept out of",processed,"processed"
    print "[column fgrep] Done."

