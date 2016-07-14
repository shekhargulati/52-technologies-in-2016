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
