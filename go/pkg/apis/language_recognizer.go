package apis

import (
	"os"
	"path/filepath"

	"github.com/redhat-developer/alizer/pkg/utils"
)

type languageItem struct {
	item       utils.LanguageFileItem
	percentage int
}

type Language struct {
	Name              string
	aliases           []string
	usageInPercentage float64
	frameworks        []string
	tools             []string
	canBeComponent    bool
}

func Analyze(path string) ([]Language, error) {
	languagesFile := utils.Get()
	languagesDetected := make(map[string]languageItem)

	paths, err := getFilePaths(path)
	if err != nil {
		return []Language{}, err
	}
	extensionsGrouped := extractExtensions(paths)
	for extension := range extensionsGrouped {
		languages := (*languagesFile).GetLanguagesByExtension(extension)
		if len(languages) == 0 {
			continue
		}
		for _, language := range languages {
			if language.Kind == "programming" {
				var languageFileItem utils.LanguageFileItem
				var err error
				if len(language.Group) == 0 {
					languageFileItem = language
				} else {
					languageFileItem, err = languagesFile.GetLanguageByName(language.Group)
					if err != nil {
						continue
					}
				}
				tmpLanguageItem := languageItem{languageFileItem, 0}
				percentage := languagesDetected[tmpLanguageItem.item.Name].percentage + extensionsGrouped[extension]
				tmpLanguageItem.percentage = percentage
				languagesDetected[tmpLanguageItem.item.Name] = tmpLanguageItem
			}
		}
	}

	var languagesFound []Language
	for name, item := range languagesDetected {
		languagesFound = append(languagesFound, Language{name, item.item.Aliases, float64(item.percentage), []string{}, []string{}, false})
	}

	return languagesFound, nil
}

func extractExtensions(paths []string) map[string]int {
	extensions := make(map[string]int)
	for _, path := range paths {
		extension := filepath.Ext(path)
		if len(extension) == 0 {
			continue
		}
		count := extensions[extension] + 1
		extensions[extension] = count
	}
	return extensions
}

func getFilePaths(root string) ([]string, error) {
	var files []string
	err := filepath.Walk(root,
		func(path string, info os.FileInfo, err error) error {
			files = append(files, path)
			return nil
		})
	return files, err
}
