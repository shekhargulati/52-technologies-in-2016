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
