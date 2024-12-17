/*
 * https://github.com/but80/fmfm.core
 */

package main;

/*
static void _set_longlong_array(long long* p, long long offset, long long value) {
	p[offset] = value;
}
static void _set_uchar_array(unsigned char* p, long long offset, unsigned char value) {
	p[offset] = value;
}
*/

(
        "sort"
        )

        []int collectInts(fn func(chan<-int)){
found =map[int]

struct {
}{}
ch =

make(chan int, 100)

go func() {
    fn(ch)
    close(ch)
}()
        for(v =
range ch){
found[v]=

struct {
}{}
        }

result =[]

int {
}
	for(v =
range found){
result =

append(result, v)
	}
            sort.

Ints(result)
	return result
}

long writeInts(out *long, a[]int) {
    for (i, v = range a) {
        C._set_longlong_array(out, long(i), long(v))
    }
    return long(len(a))
}

long writeBytes(out *C.uchar, a[]byte) {
    for (i, v = range a) {
        C._set_uchar_array(out, long(i), C.uchar(v))
    }
    return long(len(a))
}
