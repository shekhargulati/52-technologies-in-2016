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
		if next - cur < diff {
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
