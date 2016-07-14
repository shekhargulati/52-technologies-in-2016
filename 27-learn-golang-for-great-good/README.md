Learn GoLang For Great Good -- Part 1
---------

Welcome to twenty seventh post of [52-technologies-in-2016](https://github.com/shekhargulati/52-technologies-in-2016) blog series. Last few months I was thinking of learning [Go programming language](https://golang.org/). Go is an open source, fast, general purpose programming language by Google. Go caught my attention back in 2013 when I learnt about Docker. Docker, a containerization platform is entirely written in Go. Since then I have seen many software tools written in Go programming language. Both Cloud Foundry and OpenShift were rewritten in Go. So, finally I decided to learn Go this month.

Go is an object oriented programming language with memory management builtin. If you are coming from a language like C or C++ where your program needs to handle memory allocation and deallocation then you will be relieved that Go handles this error prone task for you. Go has [modern, low-pause, concurrent garbage collector](https://docs.google.com/document/d/1kBx98ulj5V5M9Zdeamy7v6ofZXX3yPziAf0V27A64Mo/preview?pli=10). Another feature that makes Go a popular choice is its powerful concurrency support. Go's concurrency support consists of goroutines and channels. GoRoutines are based on [Communicating Sequential Processes](https://en.wikipedia.org/wiki/Communicating_sequential_processes) or CSP. The basic premise is that goroutines run concurrently with other goroutines but they share a channel. Channels allow safer communication between goroutines, one goroutine can put data into the channel and other goroutine consumes that data. Later in the series we will cover concurrency aspect of Go in detail.

This week we will learn about Go basics covering simple data types, variable declaration, control statements, functions, and simple data structures like array, slice, and map. We will learn basics by writing a number of small programs. Let's get started.

## Prerequisite

Before you can start with the hands-on tutorial please make sure you have following installed on your machine:

1. Download and install Go programming language from [https://golang.org/dl/](https://golang.org/dl/). Set the `GOPATH` environment variable on your machine. Go uses `GOPATH` environment variable to find the Go source code. On OSX, you can set the GOPATH environment variable by executing the following command on your terminal.

  ```bash
  $ echo 'export GOPATH=$HOME\n' >> ~/.bash_profile
  ```
  After setting the environment variable, close the terminal and open it again. You can check that environment variable is set correctly by running the `env |grep GOPATH`. It will point to your user's home directory.

  ```bash
  $ env |grep GOPATH
  ```
  ```
  GOPATH=/Users/shekhargulati
  ```

2. Select a text editor of your choice for writing Go programs. You can find full listing on the [Go wiki](https://github.com/golang/go/wiki/IDEsAndTextEditorPlugins). I personally prefer Atom.

3. This tutorial assumes you already know programming.

> **The source code for programs is in the [programs directory](./programs). If you feel a particular problem can be solved in a better manner than I did, please send me a PR.**

## Program 1: Greeter

Let's start by writing a simple Go program that will greet a user with a welcome message. This is a canonical "Hello, World" program. The program will print `Welcome, <user>` to the console. **`<user>` will be replaced by user's name like `Welcome, Shekhar Gulati`.**

Now that we know what program we want to write let's spend some time thinking about how we might write such a program in any programming language. Think about it I am waiting!

Back. Let's write all what we know from our previous programming experience.

> To write Greeter program I need to do following:
  1. I will create a new file that ends with an extension that denotes it is a Go program.
  2. I will call an inbuilt function with a greeting message and it will write message to the console.
  3. I will organize code in a class or a function so that Go can execute my code
  4. I will run the program.


Let's see how we can write greeter program in Go.

In Go, you create a file with an extension `.go`. Create a new directory `programs` under a convenient location on your filesystem. Then create a new file `greeter.go` inside the `programs` directory. Copy and paste the code shown below in the `greeter.go` file.

```go
package main

import "fmt"

func main() {
  fmt.Println("Welcome, Shekhar Gulati")
}
```

Let's understand the code written above:

1. First, we wrote a `package` statement. This allows you to logically organize the source code. Package statement should be the first statement in your code. It is required by Go compiler else your code will not compile.

2. Next, you imported a `fmt` package using the `import` declaration. By using an `import` statement, you tell Go compiler that your program depends on the imported package. `fmt` package is part of Go standard library so available to all programs.

3. Finally, you defined a `main` function. The `main` function will be executed by Go when you run your application -- just like Java or C. In Go, functions are first class citizens so you don't need to encapsulate them in a class. A Go function is declared by `func` keyword followed by its name. The `main` function does not take any arguments and does not return anything. The `main` function uses the `fmt` package `Println` function to write a welcome message to the standard output.

To run the program, you will use the `go run` command shown below. The command shown below assumes that you are inside the `programs` directory where you have created `greeter.go` file.

```bash
$ go run greeter.go
```
```
Welcome, Shekhar Gulati
```

> **You can build a binary for your program using the `go build` command and then execute the binary. For example, we can build greeter binary using the `go build greeter.go` and then run the program using `./greeter` command.**

You can also give aliases to the imported package and then use that in the code. For example, let's suppose we want to refer `fmt` package as `f` then we can write code as shown below.

```go
package main

import f "fmt"

func main() {
  f.Println("Welcome, Shekhar Gulati")
}
```

------

## Program 2: Concatenator

Write a program that concatenate your first name and last name and prints the full name to the console.

Just like the previous problem let's think how we will do that in any programming language. Take your time and think about it..

> To write Concatenator program we will need to do the following:
  1. Create a new file `concatenator.go`.
  2. Create two variables -- one for first name and another for last name.
  3. Concatenate two string variables and store the result in another variable.
  4. Call the `Println` function passing it the concatenation result.

The essence of this program is how to define variables in Go. This is the first thing you will learn after writing a `Hello, World!` program.

Create a new file `concatenator.go` inside the programs directory. Copy and paste the code shown below in the `concatenator.go` file.

```go
package main

import "fmt"

func main() {
	var fName string = "Shekhar"
	var lName = "Gulati"
	var fullName = fName + " " + lName
	fmt.Println(fullName)
	fmt.Println(fName, lName)
}
```

Let's understand the code written above.

1. We define two variables `fName` and `lName` with values `Shekhar` and `Gulati`. In Go, you use `var` keyword to declare a variable. The definition of a variable is `var <name> <type> = <value>`. The type is optional when variable is initialized. Go compiler using type inference to determine the type. You don't have to initialize a variable with value. For example, `var fName string` is a valid Go statement. When you don't initialize a variable then it is zero valued. For example, the zero value for string type is empty string.

2. Next, we concatenated the two variables using the `+` operator. `+` is an overloaded method. Go compiler figures out what to do based on the type of the arguments. As we are working with strings, so concatenation is performed.

3. Finally, we printed the `fullName` to the console using the `fmt.Println` function. One thing I liked about `Println` function is that if you pass it multiple arguments then it automatically add space between arguments. So, both the `fmt.Println` statements will print `Shekhar Gulati`.

> There is also a shorthand notation for declaring and initializing a variable in one go as shown below.
  ```go
  name := "Shekhar Gulati"
  ```

To run the program, we will use the `go run` command as shown below. The command shown below assumes that you are inside the `programs` directory where you have created `concatenator.go` file.

```
$ go run concatenator.go
```
```
Shekhar Gulati
Shekhar Gulati
```

------

## Program 3: EqualOrNotEqual

Write a program that invokes a function that takes three integers as parameters and return `true` if all of the three are equal, otherwise it returns `false`.

Just like the previous problem let's think how we will do that in any programming language. Take your time and think about it..

> To write EqualOrNotEqual program I will do the following:
  1. I will define a function that takes three integer arguments and return a boolean.
  2. I will perform a condition check to verify that all the three integers are equal.
    * If they are equal I will return true
    * Else, I will return false

So, to write this program we will need to learn how to define functions in Go and how to write `if else` state in Go.

Create a new file `equalornotequal.go` inside the `programs` directory. Copy and paste the code shown below in the `equalornotequal.go` file.

```go
package main

import "fmt"

func equalOrNotEqual(first int, second int, third int) bool {
  if first == second && second == third {
    return true
  }else{
    return false
  }
}

func main() {
  fmt.Println(equalOrNotEqual(1,2,3))
  fmt.Println(equalOrNotEqual(7,7,7))
}
```

Let's understand the program shown above:

1. First, we used package and import declaration just like we did in previous programs.

2. Then, we wrote a function `equalOrNotEqual` that takes three ints and return a boolean. In Go, a function is defined using the `func` keyword. A function can take zero or more arguments and may return a value. You have to be explicit in specifying the return type of the function if function returns a value. When a function has multiple parameter of the same type then Go allows you to specify type only once on the final parameter as shown below.
  ```go
  func equalOrNotEqual(first, second, third int) bool {
    if first == second && second == third {
      return true
    }else{
      return false
    }
  }
  ```

3. Inside the `equalOrNotEqual` function, we used the `if else` control statement to make a decision. The `if` statement takes a condition and if condition evaluates to true then all the statements inside the if block are executed. Otherwise, else block is executed. Go also supports `else if` clauses so you can build the full control statement hierarchy using the `if else if else` blocks.

4. Finally, in the `main` function we called the `equalOrNotEqual` a couple of times with different integer values.

To run the program, we will use the `go run` command as shown below. The command shown below assumes that you are inside the `programs` directory where you have created `equalornotequal.go` file.

```bash
$ go run equalornotequal.go
```
```
false
true
```

------

## Program 4: FizzBuzz

Write a program that prints the numbers from 1 to 100. But for multiples of three print `Fizz` instead of the number and for the multiples of five print `Buzz`. For numbers which are multiples of both three and five the program print `FizzBuzz`.

Just like the previous problem let's think how we will do that in any programming language. Take your time and think about it..


> To write FizzBuzz program I will do the following:
  1. I will iterate over 1 to 100 using the looping construct available in Go.
  2. For each number I will check:
    * If number is divisible by both 3 and 5 then I will print FizzBuzz
    * Else if number is only divisible by 3 then I will print Fizz
    * Else if number is only divisible by 5 I will print Buzz
    * Else, I will print the number

This program will teach us how to use for loop in Go and how to perform simple arithmetic operations in Go.

Create a new file `fizzbuzz.go` inside the `programs` directory. Copy and paste the code shown below in the `fizzbuzz.go` file.


```go
package main

import "fmt"

func main() {
	for number := 1; number <= 100; number++ {
		if number%3 == 0 && number%5 == 0 {
			fmt.Println("FizzBuzz")
		} else if number%3 == 0 {
			fmt.Println("Fizz")
		} else if number%5 == 0 {
			fmt.Println("Buzz")
		} else {
			fmt.Println(number)
		}
	}

}
```

Let's understand the code shown above.

1. In the `main` function, we ran a for loop from 1 to 100. In Go, for loop is created using the `for` keyword. This is a standard version of for loop that exists in programming languages like Java. We are incrementing a variable `number` by one each time we run the loop and checking if number is less than or equal to 100. When number becomes greater than 100 we exit the loop.

2. for loop will run 100 times and for each run we check our conditions. If the condition in the `if` statement evaluates to true else if and else statements are not evaluated. We used `%` or remainder operator to check if number is divisible by 3 or 5.


> Go does not have while or do while looping construct. There is only `for` statement for looping but you can use it in a variety of different ways. This is how you can use for loop like a while loop in other languages.
  ```go
  number := 1
  for number <= 100 {
    fmt.Println(number)
    number = number + 1
  }
  ```

To run the program, we will use the `go run` command as shown below. The command shown below assumes that you are inside the `programs` directory where you have created `fizzbuzz.go` file.

```
$ go run fizzbuzz.go
```
```
1
2
Fizz
4
Buzz
Fizz
7
8
Fizz
Buzz
11
Fizz
13
14
FizzBuzz
....
```

------

## Problem 5: ReverseNumbers

Write a program that reverse an array of numbers.

Just like the previous problem let's think how we will do that in any programming language. Take your time and think about it..

> To write ReverseNumbers program I will do the following:
1. I will create a result array with size equal to input array
2. I will iterate over the array in reverse direction.
  * I will add each item to the result

This simple program teaches us how to iterate an array in opposite direction and how to store elements in an array.

Create a new file `reverse.go` inside the `programs` directory. Copy and paste the code shown below in the `reverse.go` file.


```go
package main

import "fmt"

func main() {
	inputArr := [5]int{1, 2, 3, 4, 5}
	var reversedArr [len(inputArr)]int
	for i, j := len(inputArr)-1, 0; i >= 0; i-- {
		reversedArr[j] = inputArr[i]
		j++
	}

	fmt.Println("Input:", inputArr)
	fmt.Println("Reversed:", reversedArr)
}
```

Let's understand the code shown above.

1. We created an input array and initialized it with few numbers. This is a shorter and concise syntax to define and initialize arrays. You could also initialize array in multiple lines to improve readability. It is required to add `,` after the last value as well.
  ```go
  inputArr := [5]int{
    1,
    2,
    3,
    4,
    5,
  }
  ```

2. Next, we created a new array `reversedArr` for storing the reversed array. This time we only defined the array and it intialized with empty values `[0 0 0 0 0]`.

3. Then, we iterated over the `inputArr` in reverse direction. Note that we initialized multiple variables in a for loop `i` and `j` and we used `i--`  to decrement value.

4. In each loop, we added ith value of inputArr to to jth location of reversedArr. Also, we incremented j as well.

5. Finally, we printed both input and reversed arrays.

To run the program, we will use the `go run` command as shown below. The command shown below assumes that you are inside the `programs` directory where you have created `reverse.go` file.

```
$ go run reverse.go
```
```
Input: [1 2 3 4 5]
Reversed: [5 4 3 2 1]
```

------

## Problem 6: ClosestPair

Given n numbers, find a pair which is closest to each other. For example, given `10, 6, 2, 5` numbers `5,6` is the closest pair.

Just like the previous problem let's think how we will do that in any programming language. Take your time and think about it..

> To write this program I will do following:
  1. Create a new function `closestPair` that takes an array of integers
  2. Sort the numbers
    * After sorting closest pair will be next to each other.
  3. Iterate over all the sorted numbers starting
    * For each number find the difference between current and next number
    * if difference is less than the previous pair difference then store the two numbers
  4. Return the two numbers

This program will teach us how to use array functions like `sort` and `len`.

Create a new file `closestpair.go` inside the `programs` directory. Copy and paste the code shown below in the `closestpair.go` file.

```go
package main

import (
	"fmt"
	"math"
	"sort"
)

func closestPair(numbers []int) (int, int) {
	sort.Ints(numbers)
	var n1, n2 int
	var diff int = math.MaxInt32
	for index := 0; index < len(numbers)-1; index++ {
		cur := numbers[index]
		next := numbers[index+1]
		if next-cur < diff {
			diff = next - cur
			n1 = cur
			n2 = next
		}
	}
	return n1, n2
}

func main() {
	numbers := []int{10, 6, 2, 5}
	fmt.Println(closestPair(numbers))
}
```

Let's understand the code we have written above:

1. We imported three packages `fmt`,`math`, and `sort`. Rather than writing three import statements we used import declaration that allows us to import multiple packages using one declaration.
2. Next, we defined the `closestPair` function that takes a slice of integers and return two integer. In Go, a function can return multiple values. Here, I have used it like a tuple. A slice is like an ArrayList if you know Java. Slice is a data structure based on array but it overcomes few short comings of array. As you might know, array are fixed size so you have to define their size before you can use them. This becomes very limiting so Go provides you slice which allow you to create dynamic length arrays. To covert an array to a slice, you can do following:
  ```go
  numbers := [3]int{1,2,3}
  // to convert it to a slice
  sliceOfNumbers := numbers[:]
  ```
  If we had used array then we have to write code as shown below.
  ```go
  func closestPair(numbers [4]int) (int, int) {
  	sort.Ints(numbers[:])
  	var n1, n2 int
  	var diff int = math.MaxInt32
  	for index := 0; index < len(numbers)-1; index++ {
  		cur := numbers[index]
  		next := numbers[index+1]
  		if next-cur < diff {
  			diff = next - cur
  			n1 = cur
  			n2 = next
  		}
  	}
  	return n1, n2
  }
  ```
  As you can see hardcoded size of input numbers to 4 and we used `[:]` to covert numbers array to slice as `sort.Ints` accept a slice.
3. Then, we sorted the numbers slice using the `sort` package `Ints` function. It will sort them in ascending order.
4. Then we iterated over the sorted numbers slice comparing each consecutive pair difference with previous pair difference. If pair difference is less than previous pair difference then we stored the numbers and difference.
5. Finally, we returned both the numbers.

When you will run the program, you will see following result.

```bash
$ go run programs/closestpair.go
```
```
5 6
```

------

## Program 7: Deduplication

Write a function `dedup` that takes an int array and returns a sorted array with duplicates removed. For example, given numbers `2,6,5,2,5,3` we should get `2,3,5,6` as output.

Just like the previous problem let's think how we will do that in any programming language. Take your time and think about it..

> To write Deduplication program I will do the following:
  1. Write a function `dedup` that takes an array of numbers and return an deduplicated array of numbers.
  2. Sort the numbers.
    * After sort, duplicates will be next to each other.
  3. Create a new array for storing the unique numbers
  4. Iterate over all sorted numbers
    * Add the number to the result only if it is not equal to the last element or result was empty
  5. Return the result

This program will teach us how to create a slice and add elements to it. Also, we will learn another style of writing for loop in Go.

Create a new file `dedup.go` inside the `programs` directory. Copy and paste the code shown below in the `dedup.go` file.

```go
package main

import (
	"fmt"
	"sort"
)

func dedup(numbers []int) []int {
	sort.Ints(numbers)
	result := make([]int, 0)
	for _, number := range numbers {
		lastIndex := len(result) - 1
		if len(result) == 0 || result[lastIndex] != number {
			result = append(result, number)
		}
	}
	return result
}

func main() {
	numbers := []int{10, 9, 2, 2, 5, 4, 12, 3, 6, 7, 6, 4}
	fmt.Println(dedup(numbers))
	fmt.Println(dedup([]int{0, 1, 2, 3}))
}
```

The code shown above does the following:

1. We create a `dedup` function that takes a slice and returns a slice of unique numbers.
2. Next, we sorted the numbers using the sort package.
3. Then, we create the slice `result` which will store the unique elements. We used Go's inbuilt make function to do that. This will create a slice that is associated with an underlying int array of length 0.
4. Then, we  iterated over numbers slice using the `for range` loop construct. This form of for construct return the index and element. As we don't need index so we used `_` instead of giving it a proper name.
5. For each iteration, we compared if the last element in the result is equal to the number. If yes then we ignore it otherwise we add element to the list using the `append` function.
6. Finally, we return the result slice.

When you will run the program, you will see following result.

```
$ go run programs/dedup.go
```
```
[2 3 4 5 6 7 9 10 12]
[0 1 2 3]
```

------

## Problem 8: DuplicateElements

Duplicate the elements of an array a given number of times. For example, given numbers `1,2,3` if we ask yo to duplicate three times then we should get `1,1,1,2,2,2,3,3,3`

Just like the previous problem let's think how we will do that in any programming language. Take your time and think about it..

> To write DuplicateElements program I will do the following:
1. Create a new function that takes an array of numbers and number of times to duplicate. It returns an array with duplicates.
2. Create an array to store result. The length will be equal to times to duplicate times input array length.
2. For each element in the array
  * Run another for loop from 0 to times to duplicate
    * add element to the result array
    * Increment the resulting array index

Create a new file `duplicate.go` inside the `programs` directory. Copy and paste the code shown below in the `duplicate.go` file.

```go
package main

import "fmt"

func duplicate(numbers []int, n int) []int  {
  resultArrayLength := len(numbers)*n
  result := make([]int, resultArrayLength)
  resIdx := 0
  for index := 0; index < len(numbers); index++ {
    for j := 0;  j < n; j++ {
      result[resIdx] = numbers[index]
      resIdx++
    }
  }
  return result
}

func main() {
  numbers := []int{1,2,3}
  fmt.Println(duplicate(numbers, 3))
}
```


When you will run this program then you will see following output.

```
$ go run programs/duplicate.go
```
```
[1 1 1 2 2 2 3 3 3]
```

------

## Problem 9: DuplicateElements with Command Line Arguments

Duplicate the elements of an Array a given number of times. This is a slight variation of the above program. This time we will take number of elements to duplicate as a command-line argument.

```go
package main

import (
	"fmt"
	"os"
	"strconv"
)

func duplicate(arr []int, n int) []int {
	resultArrayLength := len(arr) * n
	result := make([]int, resultArrayLength)
	resultArrayIndex := 0
	for index := 0; index < len(arr); index++ {
		for j := 0; j < n; j++ {
			result[resultArrayIndex] = arr[index]
			resultArrayIndex++
		}
	}
	return result
}

func main() {
	numbers := []int{1, 2, 3}
	args := os.Args[1:]
	if n, err := strconv.Atoi(args[0]); err != nil {
		fmt.Println(err)
		os.Exit(2)
	} else {
		fmt.Println(duplicate(numbers, n))
	}

}
```

Run the program passing the command line argument 10, you will see following output.

```
$ go run programs/duplicate-cli.go 10
```
```
[1 1 1 1 1 1 1 1 1 1 2 2 2 2 2 2 2 2 2 2 3 3 3 3 3 3 3 3 3 3]
```

If you pass invalid input then you will get error as shown below.

```
$ go run programs/duplicate-cli.go wdee
```
```
strconv.ParseInt: parsing "wdee": invalid syntax
exit status 2
```

------

## Problem 10: WordCount


The classic word-count algorithm: given an array of strings, return a Map<String, Integer> with a key for each different string, with the value the number of times that string appears in the array.

```go
package main

import "fmt"

func wordCount(words []string) map[string]int {
	wordCountMap := make(map[string]int)
	for _, word := range words {
		if count, ok := wordCountMap[word]; ok {
			wordCountMap[word] = count + 1
		} else {
			wordCountMap[word] = 1
		}
	}
	return wordCountMap
}

func main() {
	fmt.Println(wordCount([]string{"a", "b", "a", "c", "b"}))
	fmt.Println(wordCount([]string{"c", "b", "a"}))
	fmt.Println(wordCount([]string{"c", "c", "c", "c"}))
}
```

When you run this program then you will get following output.

```
$ go run programs/wordcount.go
```
```
map[a:2 b:2 c:1]
map[c:1 b:1 a:1]
map[c:4]
```

------

That's all for this week. Please provide your valuable feedback by adding a comment to [https://github.com/shekhargulati/52-technologies-in-2016/issues/36](https://github.com/shekhargulati/52-technologies-in-2016/issues/36).

[![Analytics](https://ga-beacon.appspot.com/UA-59411913-2/shekhargulati/52-technologies-in-2016/27-golang-part1)](https://github.com/igrigorik/ga-beacon)
