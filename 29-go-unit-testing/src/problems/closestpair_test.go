package problems_test

import (
	"fmt"
	"github.com/stretchr/testify/assert"
	. "problems"
	"testing"
)

func TestFindClosestPair(t *testing.T) {
	numbers := []int{2, 10, 5, 6, 15}
	pair := ClosestPair(numbers)
	assert.Equal(t, 5, pair.First, fmt.Sprintf("first should be 5 but was %d", pair.First))
	assert.Equal(t, 6, pair.Second, fmt.Sprintf("second should be 6 but was %d", pair.Second))
}

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
		pair := ClosestPair(tt.in)
		assert.Equal(t, tt.out.First, pair.First)
		assert.Equal(t, tt.out.Second, pair.Second)
	}
}
