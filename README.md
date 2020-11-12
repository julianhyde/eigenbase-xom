[![Build Status](https://travis-ci.org/julianhyde/eigenbase-xom.png)](https://travis-ci.org/julianhyde/eigenbase-xom)

# eigenbase-xom

XML object model for Java.

# Prerequisites

Eigenbase-XOM requires git, maven (3.0.4 or later), and JDK 1.6 or later (JDK 1.8 preferred).

# Download and build

```bash
$ git clone git://github.com/julianhyde/eigenbase-xom.git
$ cd eigenbase-xom
$ mvn install
```

# Use maven artifacts

Include the following in your `pom.xml`.

```xml
  <dependencies>
    <dependency>
      <groupId>net.hydromatic</groupId>
      <artifactId>eigenbase-xom</artifactId>
      <version>1.3.6</version>
    </dependency>
  </dependencies>
```

# Release (for committers only)

Update version numbers in `README.md`, copyright date in `NOTICE`, and
add release notes to `HISTORY.md`.

Use JDK 1.7.

```bash
$ git clean -nx
$ mvn clean
$ mvn release:clean
$ mvn -Prelease release:prepare
$ mvn -Prelease release:perform
```

# More information

* License: Apache License, Version 2.0
* Author: Julian Hyde
* Project page: http://www.hydromatic.net/xom
* Source code: https://github.com/julianhyde/eigenbase-xom
* Developers list:
  <a href="mailto:dev@calcite.apache.org">dev at calcite.apache.org</a>
  (<a href="https://mail-archives.apache.org/mod_mbox/calcite-dev/">archive</a>,
  <a href="mailto:dev-subscribe@calcite.apache.org">subscribe</a>)
* Continuous integration: https://travis-ci.org/julianhyde/eigenbase-xom
* <a href="HISTORY.md">Release notes and history</a>
