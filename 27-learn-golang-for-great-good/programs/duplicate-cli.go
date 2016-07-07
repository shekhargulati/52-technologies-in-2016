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
