

#include <stdio.h>	
#include <stdlib.h> 

#define MAX_LENGTH 1024000

int find_space(const char *str) {
    int offset = 0;
    for (offset=0; offset<MAX_LENGTH && str[offset]!=' '; ++offset);    
    if (offset >= MAX_LENGTH) return -1;
    return offset;
}

int hash(const char *buf, int len_limit, int prime) {
    int code = 0, offset = 0;
    //printf("buf=%s>", buf);
    for (offset=0; offset<len_limit; ++offset) {        
        code = ( code + (buf[offset]+128)*(offset+1) )%prime;	
        //printf("%c", buf[offset]);
    }
    //printf("<\n");
    return code;
}

int main(int argc, char* argv[]) {

    int num_shards = 83; 
    if (argc>1) {
        num_shards = atoi(argv[1]);
    }
    printf("The script loads from wikipeda pageviews (stdin) and divides data into %d shards.\n", num_shards);

    FILE *out[num_shards];
    FILE *src_file;
    char buf[MAX_LENGTH];

    int i = 0;
    for (i=0; i<num_shards; ++i) {
        sprintf(buf, "shard_%.2d", i);
        //printf("Opening %s\n", buf);
        out[i] = fopen(buf, "w");
    }

    src_file = stdin;    
    while (fgets(buf,MAX_LENGTH, src_file)!=NULL) {
	//printf("line:[%s]\n", buf);
        int offset1 = find_space(buf);
        int offset2 = find_space(&buf[offset1+1]);
        int code = hash(&buf[offset1+1], offset2, num_shards);
        fprintf(out[code], "%s", buf);
    }
    fclose(src_file);

    return 0; 
}
