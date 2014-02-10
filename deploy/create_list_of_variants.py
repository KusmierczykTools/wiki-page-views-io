import urllib
import sys


UPPER_CASE_LETTERS = set( map(chr, range(65, 91)) )

def split_on_upper_case(text):
    out = ""
    for i,c in enumerate(text):
        if c in UPPER_CASE_LETTERS and i>0:
            out += " "+c
        else:
            out += c
    return out


def wrap_spaces(text):
    return " ".join(p for p in text.split(" ") if p!="")


if __name__=="__main__":
    #The script loads from stdin list of wikipedia pages names 
    #and prints out the list of all possible variants to be considered.

    for line in sys.stdin.xreadlines():
        line = line.strip()

        variants = set()

        variants.add( line.replace(" ","") )
        variants.add( urllib.quote(line) )
        variants.add( line.replace(" ", "_") )

        line = wrap_spaces( split_on_upper_case(line) )
        variants.add( urllib.quote(line) )
        variants.add( line.replace(" ", "_") )

        for variant in variants:
            print variant

