package main

import "fmt"

func equalOrNotEqual(first, second, third int) bool {
  if first == second && second == third {
    return true
  }else{
    return false
  }
}

func main() {
  fmt.Println(equalOrNotEqual(1,2,3))
  fmt.Println(equalOrNotEqual(7,7,7))
}
