package recognizer

import (
	"encoding/json"
	"io/ioutil"
	"os"
	"path/filepath"
	"sort"
	"strconv"
	"strings"
	"testing"

	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	"github.com/redhat-developer/alizer/go/pkg/utils"
	"github.com/redhat-developer/alizer/go/test"
)

func TestExternalRepos(t *testing.T) {
	// read git test file to retrieve all git repos and their expected properties
	jsonFile, err := ioutil.ReadFile("../git_test.json")
	if err != nil {
		t.Fatal("Unable to fetch git repositories file to run tests to")
	}
	var data test.GitTests
	err = json.Unmarshal(jsonFile, &data)
	if err != nil {
		t.Fatal("Unable to fetch git repositories file to run tests to")
	}

	// loop over all repositories and verify expected results are correct
	for repo, properties := range data {
		err := checkoutAndTest(t, repo, properties)
		if err != nil {
			t.Errorf("Unable to download git repo %s", err.Error())
		}
	}

}
func checkoutAndTest(t *testing.T, repo string, properties test.GitTestProperties) error {
	root, err := test.CheckoutCommit(repo, properties.Commit)
	defer os.RemoveAll(root)
	if err != nil {
		return err
	} else {
		dir := filepath.Join(root, properties.Directory)
		assertComponentsBelongToGitProject(t, dir, properties.Components)
	}
	return nil
}

func assertComponentsBelongToGitProject(t *testing.T, gitProjectPath string, expectedComponents []test.ComponentProperties) {
	components := getComponentsFromProjectInner(t, gitProjectPath)
	assertNumberOfComponents := len(components) == len(expectedComponents)
	if assertNumberOfComponents {
		// sort both slices by component name
		sort.Slice(components, func(i, j int) bool {
			return strings.ToLower(components[i].Name) < strings.ToLower(components[j].Name)
		})
		sort.Slice(expectedComponents, func(i, j int) bool {
			return strings.ToLower(expectedComponents[i].Name) < strings.ToLower(expectedComponents[j].Name)
		})

		cont := 0
		for cont < len(components) {
			if expectedComponents[cont].Name != "ignore" && !strings.EqualFold(components[cont].Name, expectedComponents[cont].Name) {
				t.Errorf("Expected to find component %s but it was found %s", expectedComponents[cont].Name, components[cont].Name)
			}
			if !assertExpectedLangsAreFound(expectedComponents[cont].Languages, components[cont].Languages) {
				expectedPretty := printPrettyStruct(expectedComponents[cont].Languages)
				foundPretty := printPrettyStruct(components[cont].Languages)
				t.Errorf("Languages found are different from those expected.\nExpected: %s\nFound: %s ", expectedPretty, foundPretty)
			}
			if !assertExpectedPortsAreFound(expectedComponents[cont].Ports, components[cont].Ports) {
				expectedPretty := printPrettyStruct(expectedComponents[cont].Ports)
				foundPretty := printPrettyStruct(components[cont].Ports)
				t.Errorf("Ports found are different from those expected.\nExpected: %s\nFound: %s ", expectedPretty, foundPretty)
			}
			cont++
		}
	} else {
		t.Errorf("Expected " + strconv.Itoa(len(expectedComponents)) + " components but they were " + strconv.Itoa(len(components)))
	}
}

func printPrettyStruct(v interface{}) string {
	pretty, _ := json.MarshalIndent(v, "", "\t")
	return string(pretty)
}

func assertExpectedLangsAreFound(expectedLangs []test.ComponentLanguage, foundLangs []model.Language) bool {
	if !strings.EqualFold(expectedLangs[0].Name, foundLangs[0].Name) {
		return false //the main language is different
	}
	for _, expectedLang := range expectedLangs {
		lang := func() model.Language {
			for _, foundLang := range foundLangs {
				if strings.EqualFold(expectedLang.Name, foundLang.Name) {
					return foundLang
				}
			}
			return model.Language{}
		}()
		if lang.Name == "" {
			return false
		}
		if !assertExpectedAreFound(expectedLang.Frameworks, lang.Frameworks) {
			return false
		}
		if !assertExpectedAreFound(expectedLang.Tools, lang.Tools) {
			return false
		}
	}
	return true
}

func assertExpectedPortsAreFound(expectedPorts []int, foundPorts []int) bool {
	return assertExpectedAreFound(intSliceToStringSlice(expectedPorts), intSliceToStringSlice(foundPorts))
}

func intSliceToStringSlice(ints []int) []string {
	var sliceString []string
	for _, val := range ints {
		sliceString = append(sliceString, strconv.Itoa(val))
	}
	return sliceString
}

func assertExpectedAreFound(expected []string, found []string) bool {
	for i := 0; i < len(expected); i++ {
		if !utils.Contains(found, expected[i]) {
			return false
		}
	}
	return true
}
