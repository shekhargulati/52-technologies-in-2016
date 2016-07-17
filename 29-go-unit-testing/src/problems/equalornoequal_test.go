package problems_test

import (
	"github.com/stretchr/testify/assert"
	. "problems"
	"testing"
)

func TestShouldBeTrueWhenThreeNumbersAreEqual(t *testing.T) {
	isThreeNumbersEqual := EqualOrNotEqual(1, 1, 1)
	if !isThreeNumbersEqual {
		t.Error("Expected true, got ", isThreeNumbersEqual)
	}
}

func TestShouldBeFalseWhenThreeNumbersAreNotEqual(t *testing.T) {
	isThreeNumbersEqual := EqualOrNotEqual(1, 2, 3)
	if isThreeNumbersEqual {
		t.Error("Expected false, got", isThreeNumbersEqual)
	}
}

func TestShouldAssertThatFunctionReturnsFalseWhenThreeNumbersAreNotEqual(t *testing.T) {
	isThreeNumbersEqual := EqualOrNotEqual(1, 2, 3)
	assert.False(t, isThreeNumbersEqual, "Expected false got true")
}
