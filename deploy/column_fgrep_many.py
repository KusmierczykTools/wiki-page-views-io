"""Reads lines from many files and keeps only these with column value present on the list of allowed values."""

from column_fgrep import *



if __name__=="__main__":

    sys.stdout = sys.stderr

    print "[column fgrep] The program reads lines from files and \
 keeps only these with column value present on the list of allowed values."

    try:
        allowed_values_path = sys.argv[1]
        src_files_path = sys.argv[2]
        column_no = int(sys.argv[3])
    except:
        print "[column fgrep] Three arguments are expected: file with list of allowed values, \
 file with list of files, column number"
        sys.exit(-1)

    print "[column fgrep] allowed_values_path =",allowed_values_path, \
          " src_files_path =", src_files_path, " column_no =",column_no

    allowed = load_allowed(open(allowed_values_path))
    print "[column fgrep]",len(allowed), "allowed values loaded"

    
    for src_path in open(src_files_path).xreadlines():
        src_path = src_path.strip()
        if src_path=="": continue
        dst_path = ".".join(src_path.split(".")[:-1])+".out"

        print "[column fgrep] Processing:",src_path," -> ",dst_path        
        kept,processed = filter_lines(open(src_path), open(dst_path, "w"), column_no, allowed)
        print "[column fgrep]",kept,"kept out of",processed,"loaded"
        #print "[column fgrep] Done."

