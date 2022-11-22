ifdef OS
   CP = copy
   DEST_DIR = .\pkg\utils\langfiles\resources
   LANG_FILE = ..\resources\languages.yml
   CUSTOM_LANG_FILE = ..\resources\languages-customization.yml
else
   CP = cp -f
   DEST_DIR = ./pkg/utils/langfiles/resources
   LANG_FILES = ../resources/languages.yml ../resources/languages-customization.yml
endif

init: copy build
initWin: copyWin buildWin

copy: 
	mkdir -p $(DEST_DIR); for u in $(LANG_FILES); do $(CP) $$u $(DEST_DIR); done

copyWin:
	mkdir $(DEST_DIR)
	$(CP) $(LANG_FILE) $(DEST_DIR)
	$(CP) $(CUSTOM_LANG_FILE) $(DEST_DIR)

build:
	go build -o alizer

buildWin:
	go build -o alizer.exe