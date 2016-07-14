package main

import "fmt"

func main() {
	inputArr := [5]int{
		1,
		2,
		3,
		4,
		5,
	}
	var reversedArr [len(inputArr)]int
	for i, j := len(inputArr)-1, 0; i >= 0; i-- {
		reversedArr[j] = inputArr[i]
		j++
	}

	fmt.Println("Input:", inputArr)
	fmt.Println("Reversed:", reversedArr)
}
