# Java Strong Encapsulation Eliminator (JSEE)

JSEE is a set of tools used for manipulating the JVM. JSEE allows you to "see" and modify the entire Java Virtual
Machine at runtime. This library is meant to act as a Java power user's sandbox without restrictions. This library can
be dangerous, but it can also be extremely useful. Use it at your own risk.

### The main functions of this library are to:

* Allow full reflection access to Java internals
* Allow ASM attachment to the current JVM without adding anything to the command line arguments
* Stop Java from warning or throwing exceptions about unsafe usages of classes during runtime.

### Motivations
I'm always trying to break things in Java and running into walls. With Java 8, it was the inability to modify code at
runtime without specifying an agent in the command-line parameters. In Java 9, they've removed the ability to reflect
into anything, leaving me with nothing but illegal access exceptions. I wanted a simple library that I could use in a 
couple lines of code to completely remove those barriers. This library is the solution to my problems and maybe yours
too. In one line, module security for Java 9 can be disabled. With little boilerplate, an ASM agent can be created and
attached to the runtime without any extra stuff in the command-line parameters. It's exactly what I wanted.