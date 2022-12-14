/*******************************************************************************
 * Copyright (c) 2022 Red Hat, Inc.
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
	"context"
	"encoding/json"
	"errors"
	"io/ioutil"
	"net/http"
	"regexp"
	"strings"

	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	"github.com/redhat-developer/alizer/go/pkg/utils"
)

func SelectDevFilesFromTypes(path string, devFileTypes []model.DevFileType, ctx *context.Context) ([]int, error) {
	devFilesIndexes := []int{}
	components, _ := DetectComponentsInRoot(path)
	for _, component := range components {
		devFiles, err := selectDevFilesByLanguage(component.Languages[0], devFileTypes)
		if err == nil {
			devFilesIndexes = append(devFilesIndexes, devFiles...)
		}
	}
	if len(devFilesIndexes) > 0 {
		return devFilesIndexes, nil
	}

	components, _ = DetectComponents(path)
	for _, component := range components {
		devFiles, err := selectDevFilesByLanguage(component.Languages[0], devFileTypes)
		if err == nil {
			devFilesIndexes = append(devFilesIndexes, devFiles...)
		}
	}
	if len(devFilesIndexes) > 0 {
		return devFilesIndexes, nil
	}

	languages, err := analyze(path, ctx)
	if err != nil {
		return []int{}, err
	}
	devfile, err := SelectDevFileUsingLanguagesFromTypes(languages, devFileTypes)
	if err != nil {
		return []int{}, errors.New("No valid devfile found for project in " + path)
	}
	return []int{devfile}, nil
}

func SelectDevFileFromTypes(path string, devFileTypes []model.DevFileType) (int, error) {
	ctx := context.Background()
	devfiles, err := SelectDevFilesFromTypes(path, devFileTypes, &ctx)
	if err != nil {
		return -1, err
	}
	return devfiles[0], nil
}

func SelectDevFilesUsingLanguagesFromTypes(languages []model.Language, devFileTypes []model.DevFileType) ([]int, error) {
	devFilesIndexes := []int{}
	for _, language := range languages {
		devFiles, err := selectDevFilesByLanguage(language, devFileTypes)
		if err == nil {
			devFilesIndexes = append(devFilesIndexes, devFiles...)
		}
	}
	if len(devFilesIndexes) > 0 {
		return devFilesIndexes, nil
	}
	return []int{}, errors.New("no valid devfile found by using those languages")
}

func SelectDevFileUsingLanguagesFromTypes(languages []model.Language, devFileTypes []model.DevFileType) (int, error) {
	devFilesIndexes, err := SelectDevFilesUsingLanguagesFromTypes(languages, devFileTypes)
	if err != nil {
		return -1, err
	}
	return devFilesIndexes[0], nil
}

func SelectDevFilesFromRegistry(path string, url string) ([]model.DevFileType, error) {
	ctx := context.Background()
	devFileTypesFromRegistry, err := downloadDevFileTypesFromRegistry(url)
	if err != nil {
		return []model.DevFileType{}, err
	}

	indexes, err := SelectDevFilesFromTypes(path, devFileTypesFromRegistry, &ctx)
	if err != nil {
		return []model.DevFileType{}, err
	}

	devFileTypes := []model.DevFileType{}
	for _, index := range indexes {
		devFileTypes = append(devFileTypes, devFileTypesFromRegistry[index])
	}

	return devFileTypes, nil
}

func SelectDevFileFromRegistry(path string, url string) (model.DevFileType, error) {
	devFileTypes, err := downloadDevFileTypesFromRegistry(url)
	if err != nil {
		return model.DevFileType{}, err
	}

	index, err := SelectDevFileFromTypes(path, devFileTypes)
	if err != nil {
		return model.DevFileType{}, err
	}
	return devFileTypes[index], nil
}

func downloadDevFileTypesFromRegistry(url string) ([]model.DevFileType, error) {
	url = adaptUrl(url)
	// Get the data
	resp, err := http.Get(url)
	if err != nil {
		// retry by appending index to url
		url = appendIndexPath(url)
		resp, err = http.Get(url)
		if err != nil {
			return []model.DevFileType{}, err
		}
	}
	defer resp.Body.Close()

	// Check server response
	if resp.StatusCode != http.StatusOK {
		return []model.DevFileType{}, errors.New("unable to fetch devfiles from the registry")
	}

	body, err2 := ioutil.ReadAll(resp.Body)
	if err2 != nil {
		return []model.DevFileType{}, errors.New("unable to fetch devfiles from the registry")
	}

	var devFileTypes []model.DevFileType
	err = json.Unmarshal(body, &devFileTypes)
	if err != nil {
		return []model.DevFileType{}, errors.New("unable to fetch devfiles from the registry")
	}

	return devFileTypes, nil
}

func appendIndexPath(url string) string {
	if strings.HasSuffix(url, "/") {
		return url + "index"
	}
	return url + "/index"
}

func adaptUrl(url string) string {
	re := regexp.MustCompile(`^https://github.com/(.*)/blob/(.*)$`)
	url = re.ReplaceAllString(url, `https://raw.githubusercontent.com/$1/$2`)

	re = regexp.MustCompile(`^https://gitlab.com/(.*)/-/blob/(.*)$`)
	url = re.ReplaceAllString(url, `https://gitlab.com/$1/-/raw/$2`)

	re = regexp.MustCompile(`^https://bitbucket.org/(.*)/src/(.*)$`)
	url = re.ReplaceAllString(url, `https://bitbucket.org/$1/raw/$2`)

	return url
}

func selectDevFilesByLanguage(language model.Language, devFileTypes []model.DevFileType) ([]int, error) {
	devFileIndexes := []int{}
	scoreTarget := 0

	for index, devFile := range devFileTypes {
		score := 0
		if strings.EqualFold(devFile.Language, language.Name) || matches(language.Aliases, devFile.Language) {
			score++
			if matches(language.Frameworks, devFile.ProjectType) {
				score += utils.FRAMEWORK_WEIGHT
			}
			for _, tag := range devFile.Tags {
				if matches(language.Frameworks, tag) {
					score += utils.FRAMEWORK_WEIGHT
				}
				if matches(language.Tools, tag) {
					score += utils.TOOL_WEIGHT
				}
			}
		}
		if score == scoreTarget {
			devFileIndexes = append(devFileIndexes, index)
		} else if score > scoreTarget {
			scoreTarget = score
			devFileIndexes = []int{index}
		}
	}

	if scoreTarget == 0 {
		return devFileIndexes, errors.New("No valid devfile found for current language " + language.Name)
	}
	return devFileIndexes, nil
}

func matches(values []string, valueToFind string) bool {
	for _, value := range values {
		if strings.EqualFold(value, valueToFind) {
			return true
		}
	}
	return false
}
