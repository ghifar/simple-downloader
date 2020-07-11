# simple-downloader using java 8 supports native-image


to run:
- just run Main class

to build native image:
- install graalvm and setup your javahome to your downloaded graalvm (dont forget to install `gu install native-image`)

- `mvn clean package` will build native image appication inside target directory
  - after build success turn off the server `$JAVA_HOME/bin/native-image --server-shutdown`
- to run native image simply `./target/example`


