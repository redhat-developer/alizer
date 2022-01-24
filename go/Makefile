RELEASE_DIR = ./pkg/utils/langfiles/resources
RELEASE_FILES = ../resources/languages.yml ../resources/languages-customization.yml

ifdef OS
   CP = copy /y
else
   CP = cp -f
endif

init: copy build

copy: 
	mkdir $(RELEASE_DIR); for u in $(RELEASE_FILES); do $(CP) $$u $(RELEASE_DIR); done

build:
	go build ./...
