# Java Strong Encapsulation Eliminator (JSEE)

JSEE is a set of tools used for manipulating the JVM. JSEE allows you to "see" and modify the entire Java Virtual
Machine at runtime. This library is meant to act as a Java power user's sandbox without restrictions. This library can
be dangerous, but it can also be extremely useful. Use it at your own risk.

### The main functions of this library are to:

* Allow full reflection access to Java internals
* Allow ASM attachment to the current JVM without adding anything to the command line arguments
* Stop Java from warning or throwing exceptions about unsafe usages of classes during runtime.