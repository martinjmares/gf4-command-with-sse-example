gf4-command-with-sse-example
============================

Simple example of command for GlassFish 4 which use server send events. 

It is pair of commands. One is for server (domain) and one is for client (asadmin) utility. SSE is used for contiuos 
list of existing threads.

## Build

It is maven script. Use 
~~~
mvn clean install
~~~
to build the application

## Install

Copy created JAR file from __target__ directory into GlassFish 4 subdirectoris:
* glassfish/modules
* glassfish/lib/asadmin

## Use

~~~
asadmin start-domain
asadmin list-threads
# break using ctr+c
~~~

