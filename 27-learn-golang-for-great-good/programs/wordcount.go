package main

import "fmt"

func wordCount(words []string) map[string]int {
	wordCountMap := make(map[string]int)
	for _, word := range words {
		if count, ok := wordCountMap[word]; ok {
			wordCountMap[word] = count + 1
		} else {
			wordCountMap[word] = 1
		}
	}
	return wordCountMap
}

func main() {
	fmt.Println(wordCount([]string{"a", "b", "a", "c", "b"}))
	fmt.Println(wordCount([]string{"c", "b", "a"}))
	fmt.Println(wordCount([]string{"c", "c", "c", "c"}))
}
