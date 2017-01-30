Learn GoLang For Great Good Part 2: Unit Testing in Go
---

Welcome to the twenty-ninth post of [52-technologies-in-2016](https://github.com/shekhargulati/52-technologies-in-2016) blog series. This week we will take our Go knowledge to the next level by learning how to perform unit testing in Go. Unit testing has become an essential skill set for every programmer. [Unit testing](https://en.wikipedia.org/wiki/Unit_testing) is a software testing in which we test individual units of source code. Go has inbuilt support for unit testing. It has a `testing` package that provides infrastructure to write unit tests. In this blog we will focus on writing test cases for a couple of programs we wrote in [part 1](../27-learn-golang-for-great-good/README.md).

## Prerequisite

Before you can start with this post make sure you have Go installed on your machine. Once you have Go installed, setup your Go workspace by following [https://golang.org/doc/code.html](https://golang.org/doc/code.html) article. This is the recommended way to setup your Go work directory.

After following the instructions mentioned in the article you will have a Go workspace directory like `$HOME/dev/git/golang`. Inside the workspace directory, you will have `src`,`pkg`, and `bin` directories inside the `$HOME/dev/git/golang`. Inside the `src` directory, create a directory structure as shown below.

```bash
$ mkdir -p src/github.com/shekhargulati
```

Note that `$` is used to signify command-line prompt. You don't have to type `$`.

Restart the terminal to make sure changes are picked. To check, you can run `go env` which will list `GOPATH` among other Go related environment variables. Below is the output of `go env` command on my machine. These might be different for you depending on your operating system.

```bash
$ go env
```
```
GOARCH="amd64"
GOBIN=""
GOEXE=""
GOHOSTARCH="amd64"
GOHOSTOS="darwin"
GOOS="darwin"
GOPATH="/Users/shekhargulati/dev/git/golang"
GORACE=""
GOROOT="/usr/local/go"
GOTOOLDIR="/usr/local/go/pkg/tool/darwin_amd64"
GO15VENDOREXPERIMENT="1"
CC="clang"
GOGCCFLAGS="-fPIC -m64 -pthread -fno-caret-diagnostics -Qunused-arguments -fmessage-length=0 -fno-common"
CXX="clang++"
CGO_ENABLED="1"
```

Once you have done the above mentioned setup, you should create a new directory called `problems` inside the `src/github.com/shekhargulati` directory and change directory to it.

```bash
$ mkdir problems && cd problems
```

The `problems` directory will have the source code for this blog.

Let's get started with unit testing.

## Write your first unit test

Let's start by writing test case for one of the example problems we discussed in part 1 **EqualsOrNotEquals**. The problem statement was as follows: ***Write a program that invokes a function that takes three integers as parameters and return `true` if all of the three numbers are equal, otherwise it returns `false`.***

Let's start by writing a unit test for this program. In Go, to write a test case you have to create a file with name `<program>_test.go`. Here, you have to replace `<program>` with the name of your program i.e. `equalornotequal` in our case. **Only files that end with `_test.go` will be considered for testing.** Create a new file `equalornotequal_test.go` inside the `problems` directory. Copy and paste the content shown below in the `equalornotequal_test.go` file.

```go
package problems

import "testing"

func TestShouldReturnTrueWhenThreeNumbersAreEqual(t *testing.T) {
	areThreeNumbersEqual := equalOrNotEqual(1, 1, 1)
	if !areThreeNumbersEqual {
		t.Error("Expected true, got", areThreeNumbersEqual)
	}
}
```

Let's understand the unit test written above line by line.

1. The first statement defines package which will contain our tests. If you remember, in the first post we used package name as `main` for all our programs. When you have an executable program i.e. program which contains `main` method then you have to use `main` package. As we don't need executable program now, we have used package name as `problems`.

2. Then, we defined an import statement which will import the `testing` package. The `testing` package is provided by Go SDK.

3. Then, we defined our test function `TestShouldReturnTrueWhenThreeNumbersAreEqual`. All tests should start with `Test` string. This is the naming convention for Go test cases. Go will find all the exported functions that have name starting with `Test` and run them. The test method accept a pointer of type `testing.T`. The `testing.T` pointer provides support for reporting the output and status of each test.

4. Inside the `TestShouldReturnTrueWhenThreeNumbersAreEqual` test case, we called `equalOrNotEqual` function passing it three 1's. We assigned result in a variable `areThreeNumbersEqual` boolean variable. One thing you will note that there are no assertions. Later in the post we will use an assertion library.

5. Finally, we checked is `areThreeNumbersEqual` is true if not then we call `Error` function passing it our message. If test return false, then `t.Error` will be called which will report a test case failure.

To run the test case, go has a test command. Run the command shown below to test all the test cases inside the `problems` directory.

```bash
$ go test
```
```
# _/Users/shekhargulati/dev/git/golang/src/github.com/shekhargulati/problems
./equalornotequal_test.go:7: undefined: equalOrNotEqual
FAIL	_/Users/shekhargulati/dev/git/golang/src/github.com/shekhargulati/problems [build failed]
```

As expected, test fails because `equalOrNotEqual` function is undefined as we have not written it yet.

Let's write code for `equalOrNotEqual` function. Create a new file `equalornotequal.go` inside the `problems` directory and copy and paste the code shown below.

```go
package problems

func equalOrNotEqual(first, second, third int) bool {
	if first == second && second == third {
		return true
	} else {
		return false
	}
}
```

The code shown above creates a new function `equalOrNotEqual` that checks if three integers are equal or not.

Now, run the test case again using the `go test` command. This time test will pass as shown below.

```bash
$ go test
```
```
PASS
ok  	github.com/shekhargulati/problems	0.006s
```

By default, go will run all the test cases inside the current directory. If you want to run specific test cases then you can use `-run` option passing it a regex matching test case names. Let's write one more test case that test scenario when numbers are not equal.

```go
func TestShouldReturnFalseWhenThreeNumbersAreNotEqual(t *testing.T) {

	areThreeNumbersEqual := equalOrNotEqual(1, 2, 3)
	if areThreeNumbersEqual {
		t.Error("Expected false, got", areThreeNumbersEqual)
	}
}
```

If you run the test cases using `go test` both the tests will pass.

To run only `TestShouldReturnTrueWhenThreeNumbersAreEqual` test you can use `-run` option as shown below.

```bash
$ go test -run TestShouldReturnTrueWhenThreeNumbersAreEqual
```

Rather than writing full name of a test you can pass a regex as well. For example, to run all the test cases that start with `TestShouldReturnTrue` you can run following test case.

```bash
$ go test -run "TestShouldReturnTrue.*"
```

## Go function names

Before we move ahead there is one important Go feature that we have not discussed so far but that is very important for everyone to understand. Most of you would have noticed that the standard Go functions that we have used so far like `Println` or `Sort` or `Error` all starts with capital letter. In Go, functions that start with capital letter are exported functions. This means these functions are public so you can use them in your program. All internal functions uses camel case naming convention and are not accessible outside the file. So, if you try to access `equalOrNotEqual` function from another package then you will get a compilation error.

```go
package main

import (
    "fmt"
    "github.com/shekhargulati/problems"
  )

func main() {
	fmt.Println(problems.equalOrNotEqual(1, 1, 1))
}
```

When you will run run the code shown above you will get following compilation errors.

```
./a.go:9: cannot refer to unexported name problems.equalOrNotEqual
./a.go:9: undefined: problems.equalOrNotEqual
```

> **Keep in mind that only functions which starts with a capital letter are exported.**

Rename `equalOrNotEqual` function to `EqualOrNotEqual` as this should be exported function.

## Black box testing vs White box testing

Go supports both black box and white box testing. The test that we wrote previously is white box testing as we have access to internal details i.e. private members of a package. Go recommends that you should have tests in the same directory and package allowing you to access the internals. I feel this will lead to tests that don't test the behavior but implementation details. So, I personal like black box testing where I work against the exported functions only.

To perform black box testing, create a test file like we did previously `equalornotequal.go` and copy and paste the following contents.

```go
package problems_test

import (
  "testing"
  . "github.com/shekhargulati/problems"
)

func TestThreeEqualNumbers(t *testing.T) {
	isThreeNumbersEqual := EqualOrNotEqual(1, 1, 1)
	if !isThreeNumbersEqual {
		t.Error("\tExpected true, got ", isThreeNumbersEqual)
	}
}
```

There are couple of important changes to note.

1. We have used a different package name `problems_test` instead or `problems`. This means we will have access to only exported functions.

2. In the import statement, we have to import our package `github.com/shekhargulati/problems`. Also, we used `dot-import` so that exported functions are in the `problems_test` package scope.

## Improving test output readability

You can improve the test readability by adding log statements. The `testing.T` pointer has a lot of logging methods that you can use to make your test output more readable and understandable. Below shown is one such attempt.

```go
func TestThreeEqualNumbers(t *testing.T) {
	t.Log("Given three numbers are equal")
	t.Logf("\tWhen we make a call to EqualOrNotEqual(%d,%d,%d)",1,1,1)
	isThreeNumbersEqual := EqualOrNotEqual(1, 1, 1)
	if isThreeNumbersEqual {
		t.Log("\tThen we should get",isThreeNumbersEqual)
	}else{
		t.Error("\tExpected true, got ", isThreeNumbersEqual)
	}
}
```

When you will run the go test command now with `-v` option you will see a much better test output. Please note you have to run tests in verbose mode to see the log messages. If you remove `-v` option, your tests will not print log statements.

```bash
$ go test -v -run TestThreeEqualNumbers
```
```
=== RUN   TestThreeEqualNumbers
--- PASS: TestThreeEqualNumbers (0.00s)
	equalornotequalblack_test.go:9: Given three numbers are equal
	equalornotequalblack_test.go:10: 	When we make a call to EqualOrNotEqual(1,1,1)
	equalornotequalblack_test.go:13: 	Then we should get true
PASS
ok  	github.com/shekhargulati/problems	0.007s
```

## Running test multiple times

There are times when you would like to run a test case multiple times. We all have seen flaky tests which run most of the times but fail few times. It is very difficult to reproduce failing flaky tests. The only solution is to run test multiple times. `go test` command allows you to specify the count of times you want to run a test as shown below.

```bash
$ go test -v -run TestThreeEqualNumbers -count 3
```
```
=== RUN   TestThreeEqualNumbers
--- PASS: TestThreeEqualNumbers (0.00s)
	equalornotequalblack_test.go:9: Given three numbers are equal
	equalornotequalblack_test.go:10: 	When we make a call to EqualOrNotEqual(1,1,1)
	equalornotequalblack_test.go:13: 	Then we should get true
=== RUN   TestThreeEqualNumbers
--- PASS: TestThreeEqualNumbers (0.00s)
	equalornotequalblack_test.go:9: Given three numbers are equal
	equalornotequalblack_test.go:10: 	When we make a call to EqualOrNotEqual(1,1,1)
	equalornotequalblack_test.go:13: 	Then we should get true
=== RUN   TestThreeEqualNumbers
--- PASS: TestThreeEqualNumbers (0.00s)
	equalornotequalblack_test.go:9: Given three numbers are equal
	equalornotequalblack_test.go:10: 	When we make a call to EqualOrNotEqual(1,1,1)
	equalornotequalblack_test.go:13: 	Then we should get true
PASS
ok  	github.com/shekhargulati/problems	0.006s
```

## Other options

There are many other options provided by `go test` command. You can look at all the options by running the `go test --help` command.

## Using assertions

If you have written tests in any programming language one thing that you will miss is an assertion package. Go SDK does not provide any assertion package but there are many community contributed assertion package. One such popular package is `testify`. You can get the package by using the `go get` command as shown below.

```
$ go get github.com/stretchr/testify
```

This will put the package in the `pkg` directory inside the `$GOPATH`. This is where all packages will be installed.

Now, you can improve your test case by writing assertions as shown below.

```go
package problems

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestShouldAssertThatFunctionReturnsFalseWhenThreeNumbersAreNotEqual(t *testing.T) {
	isThreeNumbersEqual := EqualOrNotEqual(1, 2, 3)
	assert.False(t, isThreeNumbersEqual, "Expected false got true")
}
```

You can learn more about this package by reading its [documentation](https://github.com/stretchr/testify).

## Table tests

Another cool feature provided by Go `testing` package is table tests. Table test allows you to write a test once and run it against a table of data. Table will contain the input and expected output. Let's write test for `closestpair` program. **Given n numbers, find a pair which is closest to each other. For example, given 10, 6, 2, 5 numbers 5,6 is the closest pair.**

One possible solution is shown below.

```go
package problems

import (
	"math"
	"sort"
)

type Pair struct {
	First, Second int
}

func ClosestPair(numbers []int) Pair {
	sort.Ints(numbers)
	var pair Pair
	var diff int = math.MaxInt32
	for index := 0; index < len(numbers)-1; index++ {
		cur := numbers[index]
		next := numbers[index+1]
		if next-cur < diff {
			diff = next - cur
			pair = Pair{cur, next}
		}
	}
	return pair
}
```

To write table tests, we will create a table `closestPairTests`. It is an array of struct with two fields input array and output Pair. We populate the array with the input and output as shown below.

```go
package problems_test

import (
	. "github.com/shekhargulati/problems"
	"github.com/stretchr/testify/assert"
	"testing"
)

var closestPairTests = []struct {
	in  []int
	out Pair
}{
	{[]int{2, 10, 5, 6, 15}, Pair{5, 6}},
	{[]int{2, 4, 5}, Pair{4, 5}},
	{[]int{100, 5, 7, 99, 11}, Pair{99, 100}},
}

func TestClosestPair(t *testing.T) {
	for _, tt := range closestPairTests {
		t.Log("Running test for input", tt.in)
		pair := ClosestPair(tt.in)
		assert.Equal(t, tt.out.First, pair.First)
		assert.Equal(t, tt.out.Second, pair.Second)
	}
}
```

When we run the test case, we iterate over table entries running test for each input and asserting it against expected output.

When you will run the test case, you will see the following output.

```bash
$ go test -v -run TestClosestPair
```
```
=== RUN   TestClosestPair
--- PASS: TestClosestPair (0.00s)
	closestpair_test.go:20: Running test for input [2 10 5 6 15]
	closestpair_test.go:20: Running test for input [2 4 5]
	closestpair_test.go:20: Running test for input [100 5 7 99 11]
PASS
ok  	github.com/shekhargulati/problems	0.009s
```

------

That's all for this week. Please provide your valuable feedback by adding a comment to [https://github.com/shekhargulati/52-technologies-in-2016/issues/42](https://github.com/shekhargulati/52-technologies-in-2016/issues/42).

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-2/shekhargulati/52-technologies-in-2016/29-golang-part2)](https://github.com/igrigorik/ga-beacon)
