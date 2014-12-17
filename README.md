# Compiling/running from the command line

```bash
cd src
javac jiconv/Converter.java
# EBCDIC to UTF-8
java jiconv/Converter -f 'x-IBM930' -t 'UTF-8' < ~/EBCDIC-data.txt
# UTF-8 to EBCDIC
java jiconv/Converter -f 'UTF-8' -t 'x-IBM930' < ~/UTF-8-data.txt
```
