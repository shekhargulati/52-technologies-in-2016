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
