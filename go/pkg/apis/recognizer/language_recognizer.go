/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc.
 ******************************************************************************/
package recognizer

import (
	"os"
	"path/filepath"
	"sort"

	enricher "github.com/redhat-developer/alizer/pkg/apis/enricher"
	"github.com/redhat-developer/alizer/pkg/apis/language"
	"github.com/redhat-developer/alizer/pkg/utils"
)

type languageItem struct {
	item       utils.LanguageFileItem
	percentage int
}

func Analyze(path string) ([]language.Language, error) {
	languagesFile := utils.Get()
	languagesDetected := make(map[string]languageItem)

	paths, err := getFilePaths(path)
	if err != nil {
		return []language.Language{}, err
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

	var languagesFound []language.Language
	for name, item := range languagesDetected {
		tmpLanguage := language.Language{name, item.item.Aliases, float64(item.percentage), []string{}, []string{}, false}
		langEnricher := enricher.GetEnricherByLanguage(&tmpLanguage)
		if langEnricher != nil {
			langEnricher.DoEnrichLanguage(&tmpLanguage, &paths)
		}
		languagesFound = append(languagesFound, tmpLanguage)
	}

	sort.SliceStable(languagesFound, func(i, j int) bool {
		return languagesFound[i].UsageInPercentage > languagesFound[j].UsageInPercentage
	})

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
