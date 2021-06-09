<!--
{% comment %}
Licensed to Julian Hyde under one or more contributor license
agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.

Julian Hyde licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file except in
compliance with the License.  You may obtain a copy of the License at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
{% endcomment %}
-->
[![Build Status](https://travis-ci.com/julianhyde/eigenbase-xom.svg?branch=main)](https://travis-ci.com/julianhyde/eigenbase-xom)

# eigenbase-xom

XML object model for Java.

# Prerequisites

Eigenbase-XOM requires git,
Apache Maven (3.2.1 or later),
and JDK 8 or later.

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

Use JDK 8.

```bash
$ export GPG_TTY=$(tty)
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
