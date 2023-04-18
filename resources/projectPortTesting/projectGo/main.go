package main

import (
	"fmt"
	"net/http"
	"os"
)

func main() {
	http.HandleFunc("/", HelloHandler)
	fmt.Println("Listening on localhost:8080")
	http.ListenAndServe(":8080", nil)
}

func HelloHandler(w http.ResponseWriter, r *http.Request) {
	w.WriteHeader(http.StatusOK)
	hostname, err := os.Hostname()
	if err != nil {
		fmt.Println("unable to get hostname")
	}
	fmt.Fprintf(w, "Go Hello on %s\n", hostname)
}
