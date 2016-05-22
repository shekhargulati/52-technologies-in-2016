strman - A Java 8 String manipulation library
------

A Java 8 library for working with String. It is inspired by [dleitee/strman](https://github.com/dleitee/strman).

Getting Started
--------

To use strman in your application, you have to add `strman` in your classpath. strman is available on [Maven Central](http://search.maven.org/) so you just need to add dependency to your favorite build tool as show below.

For Apache Maven users, please add following to your pom.xml.

```xml
<dependencies>
    <dependency>
        <groupId>com.shekhargulati</groupId>
        <artifactId>strman</artifactId>
        <version>0.1.0</version>
        <type>jar</type>
    </dependency>
</dependencies>
```

Gradle users can add following to their build.gradle file.

```
compile(group: 'com.shekhargulati', name: 'strman', version: '0.1.0', ext: 'jar'){
        transitive=true
}
```

## Available Functions

These are the available functions in current version of library:

## append

Appends Strings to value

```java
import static strman.append
append("f", "o", "o", "b", "a", "r")
// result => "foobar"
```

## appendArray

Append an array of String to value

```java
import static strman.appendArray
appendArray("f", new String[]{"o", "o", "b", "a", "r"}
// result => "foobar"
```

## at

Get the character at index. This method will take care of negative indexes.

```java
import static strman.at
at("foobar", 0)
// result => Optional("f")
```

## between

Returns an array with strings between start and end.

```java
import static strman.between
between("[abc][def]", "[", "]")
// result => ["abc","def"]
```

## chars

Returns a String array consisting of the characters in the String.

```java
import static strman.chars
chars("title")
// result => ["t", "i", "t", "l", "e"]
```

## collapseWhitespace

Replace consecutive whitespace characters with a single space.

```java
import static strman.collapseWhitespace
collapseWhitespace("foo    bar")
// result => "foo bar"
```

## contains

Verifies that the needle is contained in the value.

```java
import static strman.contains
contains("foo bar","foo")
// result => true

contains("foo bar","FOO", false) // turning off case sensitivity
// result => true
```

## containsAll

Verifies that all needles are contained in value

```java
import static strman.containsAll
containsAll("foo bar", new String[]{"foo", "bar"})
// result => true

containsAll("foo bar", new String[]{"FOO", "bar"},false)
// result => true
```

## containsAny

Verifies that one or more of needles are contained in value.

```java
import static strman.containsAny
containsAny("bar foo", new String[]{"FOO", "BAR", "Test"}, true)
// result => true
```

## countSubstr

Count the number of times substr appears in value

```java
import static strman.countSubstr
countSubstr("aaaAAAaaa", "aaa")
// result => 2
countSubstr("aaaAAAaaa", "aaa", false, false)
// result => 3
```

## endsWith

Test if value ends with search.

```java
import static strman.endsWith
endsWith("foo bar", "bar")
// result => true
endsWith("foo Bar", "BAR", false)
// result => true
```

## ensureLeft

Ensures that the value begins with prefix. If it doesn't exist, it's prepended.

```java
import static strman.ensureLeft
ensureLeft("foobar", "foo")
// result => "foobar"
ensureLeft("bar", "foo")
// result => "foobar"
ensureLeft("foobar", "FOO", false)
// result => "foobar"
```

## base64Decode

Decodes data encoded with MIME base64

```java
import static strman.base64Decode
base64Decode("c3RybWFu")
// result => "strma"
```

## base64Encode

Encodes data with MIME base64.

```java
import static strman.base64Encode
base64Encode("strman")
// result => "c3RybWFu"
```

## binDecode

Convert binary unicode (16 digits) string to string chars

```java
import static strman.binDecode
binDecode("0000000001000001")
// result => "A"
```

## binEncode

Convert string chars to binary unicode (16 digits)

```java
import static strman.binEncode
binEncode("A")
// result => "0000000001000001"
```

## decDecode

Convert decimal unicode (5 digits) string to string chars

```java
import static strman.decDecode
decDecode("00065")
// result => "A"
```

## decEncode

Convert string chars to decimal unicode (5 digits)

```java
import static strman.decEncode
decEncode("A")
// result => "00065"
```

## ensureRight

Ensures that the value ends with suffix. If it doesn't, it's appended.

```java
import static strman.ensureRight
ensureRight("foo", "bar")
// result => "foobar"

ensureRight("foobar", "bar")
// result => "foobar"

ensureRight("fooBAR", "bar", false)
// result => "foobar"
```

## first

Returns the first n chars of String

```java
import static strman.first
first("foobar", 3)
// result => "foo"
```

## head

Return the first char of String

```java
import static strman.head
head("foobar")
// result => "f"
```

## hexDecode

Convert hexadecimal unicode (4 digits) string to string chars

```java
import static strman.hexDecode
hexDecode("0041")
// result => "A"
```

## hexEncode

Convert string chars to hexadecimal unicode (4 digits)

```java
import static strman.hexEncode
hexEncode("A")
// result => "0041"
```

## inequal

Tests if two Strings are inequal

```java
import static strman.inequal
inequal("a", "b")
// result => true
```

## insert

Inserts 'substr' into the 'value' at the 'index' provided.

```java
import static strman.insert
insert("fbar", "oo", 1)
// result => "foobar"
```

## last

Return the last n chars of String

```java
import static strman.last
last("foobarfoo", 3)
// result => "foo"
```

## leftPad

Returns a new string of a given length such that the beginning of the string is padded.

```java
import static strman.leftPad
leftPad("1", "0", 5)
// result => "00001"
```

## lastIndexOf

This method returns the index within the calling String object of the last occurrence of the specified value, searching backwards from the offset.

```java
import static strman.lastIndexOf
lastIndexOf("foobarfoobar", "F", false)
// result => 6
```

## leftTrim

Removes all spaces on left

```java
import static strman.leftTrim
leftTrim("     strman")
// result => "strman"
```

## prepend

Return a new String starting with prepends

```java
prepend("r", "f", "o", "o", "b", "a")
// "foobar"
```

## removeEmptyStrings

Remove empty Strings from string array

```java
removeEmptyStrings(new String[]{"aa", "", "   ", "bb", "cc", null})
// result => ["aa", "bb", "cc"]
```

## removeLeft

Returns a new String with the prefix removed, if present.

```java
removeLeft("foofoo", "foo")
// "foo"
```

## removeNonWords

Remove all non word characters.

```java
removeNonWords("foo&bar-")
// result => "foobar"
```

## removeRight

Returns a new string with the 'suffix' removed, if present.

```java
removeRight("foobar", "bar")
// result => "foo"
removeRight("foobar", "BAR",false)
// result => "foo"
```

## removeSpaces

Remove all spaces and replace for value.

```java
removeSpaces("foo bar")
// result => "foobar"
```

## repeat

Returns a repeated string given a multiplier.

```
repeat("1", 3)
// result  => "111"
```

## reverse

Reverse the input String

```java
reverse("foo")
// result => "oof"
```

## rightPad

Returns a new string of a given length such that the ending of the string is padded.

```java
rightPad("1", "0", 5)
// result => "10000"
```

## rightTrim

Remove all spaces on right.

```java
rightTrim("strman   ")
// result => "strman"
```

## safeTruncate

Truncate the string securely, not cutting a word in half. It always returns the last full word.

```java
safeTruncate("foo bar", 4, ".")
// result => "foo."
safeTruncate("A Javascript string manipulation library.", 16, "...")
// result => "A Javascript..."
```

## truncate

Truncate the unsecured form string, cutting the independent string of required position.

```java
truncate("A Javascript string manipulation library.", 14, "...")
// result => "A Javascrip..."
```

## htmlDecode

Converts all HTML entities to applicable characters.

```java
htmlDecode("&SHcy;")
// result => Ш
```

## htmlEncode

Convert all applicable characters to HTML entities.

```java
htmlEncode("Ш")
// result => "&SHcy;"
```

## shuffle

It returns a string with its characters in random order.

```java
shuffle("shekhar")
```

## slugify

Convert a String to a slug

```java
slugify("foo bar")
// result => "foo-bar"
```

## transliterate

Remove all non valid characters. Example: change á => a or ẽ => e.

```java
transliterate("fóõ bár")
// result => "foo bar"
```

## surround

Surrounds a 'value' with the given 'prefix' and 'suffix'.

```java
surround("div", "<", ">"
// result => "<div>s"
```

## tail

```java
tail("foobar")
// result => "oobar"
```

## toCamelCase

Transform to camelCase

```java
toCamelCase("CamelCase")
// result => "camelCase"
toCamelCase("camel-case")
// result => "camelCase"
```

## toStudlyCase

Transform to StudlyCaps.

```java
toStudlyCase("hello world")
// result => "HelloWorld"
```

## toDecamelize

Decamelize String

```java
toDecamelize("helloWorld",null)
// result => "hello world"
```

## toKebabCase

Transform to kebab-case.

```java
toKebabCase("hello World")
// result => "hello-world"
```

## toSnakeCase

Transform to snake_case.

```java
toSnakeCase("hello world")
// result => "hello_world"
```

License
-------
strman is licensed under the MIT License - see the `LICENSE` file for details.
