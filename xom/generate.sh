# Generates MetaDef.java and meta.dtd. These files are checked
# into git. You should invoke this script to re-generate them when you
# modify Meta.xml, and check in the new versions.
exec java -cp target/classes \
    org.eigenbase.xom.MetaGenerator \
    src/main/java/org/eigenbase/xom/Meta.xml \
    src/main/java

# End generate.sh
