# Eigenbase-XOM release history

For a full list of releases, see <a href="https://github.com/julianhyde/eigenbase-xom/releases">github</a>.

## <a href="https://github.com/julianhyde/eigenbase-xom/releases/tag/eigenbase-xom-1.3.7">1.3.7</a> / 2022-01-27

* Upgrade Apache Xerces (xercesImpl) from 2.12.0 to 2.12.2
* Upgrade Apache Ant from 1.10.9 to 1.10.11
* Maven wrapper
* Travis: switch from Oracle JDK 8 to OpenJDK 8
* Rename 'master' branch to 'main'

## <a href="https://github.com/julianhyde/eigenbase-xom/releases/tag/eigenbase-xom-1.3.6">1.3.6</a> / 2020-11-12

* Upgrade junit to 4.13.1, xerces to 2.12.0
* Publish releases to <a href="https://search.maven.org/artifact/net.hydromatic/eigenbase-xom">Maven Central</a>
  (previous releases are in <a href="http://www.conjars.org/">Conjars</a>)
* Sign jars
* Change maven groupId from "eigenbase" to "net.hydromatic"

## <a href="https://github.com/julianhyde/eigenbase-xom/releases/tag/eigenbase-xom-1.3.5">1.3.5</a> / 2016-11-09

* Set Apache Felix version, for compatibility with JDK 1.6
* [<a href="https://github.com/julianhyde/eigenbase-xom/issues/6">XOM-6</a>]
  Add `createSecureDocBuilderFactory` to `JaxpDOMParser`; upgrade Xerces
* Upgrade to junit-4.11
* [<a href="https://github.com/julianhyde/eigenbase-xom/issues/5">XOM-5</a>]
  Mark ant dependency as optional

## <a href="https://github.com/julianhyde/eigenbase-xom/releases/tag/eigenbase-xom-1.3.4">1.3.4</a> / 2014-01-08

* Add release notes and history.
* Make ant an optional dependency for the OSGI bundle.

## <a href="https://github.com/julianhyde/eigenbase-xom/releases/tag/eigenbase-xom-1.3.3">1.3.3</a> / 2013-12-06

* Switch to conjars as distribution repository.

## <a href="https://github.com/julianhyde/eigenbase-xom/releases/tag/eigenbase-xom-1.3.2">1.3.2</a> / 2013-12-06

* Enable Travis CI.
* Changes made to POM to make the packaged jar a valid OSGI bundle.
* Remove Intellij files from git.
* Package sources and javadoc.
* Migrate from DynamoBI.
