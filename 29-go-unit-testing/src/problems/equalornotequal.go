package problems

func EqualOrNotEqual(first, second, third int) bool {
	if first == second && second == third {
		return true
	} else {
		return false
	}
}
