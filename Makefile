# Alex Schwarz 0719732
# CIS*2750 A3 Makefile

all: Dialogc

CC = gcc
CFLAGS= 

Dialogc: libs yadc Compiler.class ParameterInterface.class Dialogc.class

Compiler.class: Compiler.java
	javac Compiler.java

Dialogc.class: Dialogc.java
	javac CompileModeDialog.java
	javac Dialogc.java

ParameterInterface.class: ParameterInterface.java
	javac ParameterInterface.java
	
libs: libpm.a libJNIpm.so

libpm.a: list.o ParameterManager.o ParameterList.o
	ar cr libpm.a ParameterManager.o ParameterList.o list.o
	ranlib libpm.a

libJNIpm.so: ParameterInterface.c ParameterInterface.h libpm.a
	gcc -fPIC -g -c ParameterInterface.c -I/usr/lib/jvm/java-1.6.0-openjdk/include -I/usr/lib/jvm/java-1.6.0-openjdk/include/linux
	gcc -shared -Wl,-soname,libJNIpm.so  -I/usr/lib/jvm/java-1.6.0-openjdk/include -I/usr/lib/jvm/java-1.6.0-openjdk/include/linux -o libJNIpm.so ParameterInterface.o -L. -lpm

yadc: yacc.o lex.o list.o hash.o
	gcc lex.o yacc.o list.o hash.o -o yadc -ly -ll
lex.o: yadc.l
	lex yadc.l
	gcc lex.yy.c -c -o lex.o
yacc.o: yadc.y
	yacc yadc.y -d -y
	gcc y.tab.c -c -o yacc.o

	
ParameterManager.o: ParameterManager.c ParameterManager.h list.h Boolean.h ParameterList.h
	gcc -c -g -fPIC ParameterManager.c
ParameterList.o: ParameterList.c ParameterList.h list.h
	gcc -c -g -fPIC ParameterList.c
list.o: list.c list.h Boolean.h
	gcc -c -g -fPIC list.c
hash.o: hash.c hash.h
	gcc -c -g -fPIC hash.c
clean:
	rm -f *.o *.class *.a *.so
