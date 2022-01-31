package recognizer

import (
	"io/fs"
	"path/filepath"
	"strings"

	enricher "github.com/redhat-developer/alizer/go/pkg/apis/enricher"
	"github.com/redhat-developer/alizer/go/pkg/apis/language"
	"github.com/redhat-developer/alizer/go/pkg/utils/langfiles"
)

type Component struct {
	path      string
	languages []language.Language
}

func DetectComponents(path string) ([]Component, error) {
	files, err := getFilePaths(path)
	if err != nil {
		return []Component{}, err
	}
	components, err := detectComponents(files)
	if err != nil {
		return []Component{}, err
	}

	// it may happen that a language has no a specific configuration file (e.g opposite to JAVA -> pom.xml and Nodejs -> package.json)
	// we then rely on the language recognizer
	//TODO
	directoriesNotBelongingToExistingComponent := getDirectoriesPathsWithoutConfigFile(path, components)
	components = append(components, getComponentsWithoutConfigFile(directoriesNotBelongingToExistingComponent)...)

	return components, nil
}

func getComponentsWithoutConfigFile(directories []string) []Component {
	var components []Component
	for _, dir := range directories {
		component, _ := detectComponent(dir, "")
		if component.path != "" && isValidNoConfigComponent(component) {
			components = append(components, component)
		}
	}
	return components
}

func isValidNoConfigComponent(component Component) bool {
	if len(component.languages) == 0 {
		return false
	}

	lang, err := langfiles.Get().GetLanguageByNameOrAlias(component.languages[0].Name)
	if err != nil {
		return false
	}

	return len(lang.ConfigurationFiles) == 0
}

func getDirectoriesPathsWithoutConfigFile(root string, components []Component) []string {
	if len(components) == 0 {
		return []string{root}
	}
	directories := []string{}
	filepath.WalkDir(root, func(path string, d fs.DirEntry, err error) error {
		if strings.EqualFold(root, path) {
			return filepath.SkipDir
		}
		if !hasDirectoryAnyComponent(path, components) {
			directories = getParentFolders(path, directories)
		}
		return nil
	})
	return directories
}

/**
* Return all paths which are not sub-folders of some other path within the list
* Target will be added to the list if it is not a sub-folder of any other path within the list
* If a path in the list is sub-folder of Target, that path will be removed.
*
* @param target new path to be added
* @param directories list of all previously added paths
* @return the list containing all paths which are not sub-folders of any other
 */
func getParentFolders(path string, directories []string) []string {
	updatedDirectories := []string{}
	for _, dir := range directories {
		if isFirstPathParentOfSecond(dir, path) {
			return directories
		}

		if isFirstPathParentOfSecond(path, dir) {
			continue
		}
		updatedDirectories = append(updatedDirectories, dir)
	}

	if len(updatedDirectories) != len(directories) {
		updatedDirectories = append(updatedDirectories, path)
	}
	return updatedDirectories
}

func hasDirectoryAnyComponent(path string, components []Component) bool {
	for _, component := range components {
		if strings.EqualFold(path, component.path) || isFirstPathParentOfSecond(component.path, path) {
			return true
		}
	}
	return false
}

func isFirstPathParentOfSecond(firstPath string, secondPath string) bool {
	return strings.Contains(secondPath, firstPath)
}

func detectComponents(files []string) ([]Component, error) {
	configurationPerLanguage := langfiles.Get().GetConfigurationPerLanguageMapping()
	var components []Component
	for _, file := range files {
		if language, isConfig := configurationPerLanguage[file]; isConfig && isConfigurationValid(language, file) {
			dir, _ := filepath.Split(file)
			component, err := detectComponent(dir, language)
			if err != nil {
				return []Component{}, err
			}
			if component.path != "" {
				components = append(components, component)
			}
		}
	}
	return components, nil
}

func detectComponent(root string, language string) (Component, error) {
	languages, err := Analyze(root)
	if err != nil {
		return Component{}, err
	}
	languages = getLanguagesWeightedByConfigFile(languages, language)
	if mainLang := languages[0]; mainLang.CanBeComponent && len(mainLang.Frameworks) > 0 {
		return Component{
			path:      root,
			languages: languages,
		}, nil
	}
	return Component{}, nil

}

func getLanguagesWeightedByConfigFile(languages []language.Language, languageByConfig string) []language.Language {
	if languageByConfig == "" {
		return languages
	}

	for index, lang := range languages {
		if strings.EqualFold(lang.Name, languageByConfig) {
			sliceWithoutLang := append(languages[:index], languages[index+1:]...)
			return append([]language.Language{lang}, sliceWithoutLang...)
		}
	}
	return languages
}

func isConfigurationValid(language string, file string) bool {
	langEnricher := enricher.GetEnricherByLanguage(language)
	if langEnricher != nil {
		return langEnricher.IsConfigValidForComponentDetection(language, file)
	}
	return false
}
