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
