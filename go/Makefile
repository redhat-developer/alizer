.PHONY: gomod_tidy
gomod_tidy:
	 go mod tidy

.PHONY: gofmt
gofmt:
	go fmt -x ./...

.PHONY: build
build:
	go build -o alizer

.PHONY: buildWin
buildWin:
	go build -o alizer.exe

.PHONY: test
test:
	go test ./...