package langfiles

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestGetLanguagesByExtension(t *testing.T) {
	tests := []struct {
		name         string
		extension    string
		expectedSize int
	}{
		{
			name:         "Not a language extension",
			extension:    ".notaextension",
			expectedSize: 0,
		},
		{
			name:         "Go",
			extension:    ".go",
			expectedSize: 1,
		},
		{
			name:         "Java",
			extension:    ".java",
			expectedSize: 1,
		},
		{
			name:      "C#",
			extension: ".cs",
			// Smalltalk, C#
			expectedSize: 2,
		},
		{
			name:      "F#",
			extension: ".fs",
			// F#, GLSL, Forth, Filterscript
			expectedSize: 4,
		},
		{
			name:         "Visual Basic .NET",
			extension:    ".vb",
			expectedSize: 1,
		},
		{
			name:         "JavaScript",
			extension:    ".js",
			expectedSize: 1,
		},
		{
			name:         "Python",
			extension:    ".py",
			expectedSize: 1,
		},
		{
			name:      "Rust",
			extension: ".rs",
			// RenderScript, Rust
			expectedSize: 2,
		},
		{
			name:      "PHP",
			extension: ".php",
			// Hack, PHP
			expectedSize: 2,
		},
	}

	languageFile := Get()

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			languages := languageFile.GetLanguagesByExtension(tt.extension)
			if len(languages) != tt.expectedSize {
				t.Errorf("For extension %s, expected %d languages, but got %d", tt.extension, tt.expectedSize, len(languages))
			}
		})
	}
}

func TestGetLanguageByName(t *testing.T) {
	noLangErr := "no language found with this name"
	tests := []struct {
		name         string
		expectedItem LanguageItem
		expectedErr  *string
	}{
		{
			name:         "Not a language",
			expectedItem: LanguageItem{},
			expectedErr:  &noLangErr,
		},
		{
			name:         "Go",
			expectedItem: LanguageItem{Name: "Go", Aliases: []string{"golang"}, Kind: "programming", Group: "", ConfigurationFiles: []string{"go.mod"}, ExcludeFolders: []string{"vendor"}, Component: true, disabled: false},
			expectedErr:  nil,
		},
		{
			name:         "Java",
			expectedItem: LanguageItem{Name: "Java", Aliases: []string(nil), Kind: "programming", Group: "", ConfigurationFiles: []string{"pom.xml", "build.gradle"}, ExcludeFolders: []string(nil), Component: true, disabled: false},
			expectedErr:  nil,
		},
		{
			name:         "C#",
			expectedItem: LanguageItem{Name: "C#", Aliases: []string{"csharp", "dotnet", ".NET"}, Kind: "programming", Group: "", ConfigurationFiles: []string{".*\\.\\w+proj", "appsettings.json"}, ExcludeFolders: []string(nil), Component: true, disabled: false},
			expectedErr:  nil,
		},
		{
			name:         "F#",
			expectedItem: LanguageItem{Name: "F#", Aliases: []string{"fsharp", "dotnet", ".NET"}, Kind: "programming", Group: "", ConfigurationFiles: []string{".*\\.\\w+proj", "appsettings.json"}, ExcludeFolders: []string(nil), Component: true, disabled: false},
			expectedErr:  nil,
		},
		{
			name:         "Visual Basic .NET",
			expectedItem: LanguageItem{Name: "Visual Basic .NET", Aliases: []string{"visual basic", "vbnet", "vb .net", "vb.net", "dotnet", ".NET"}, Kind: "programming", Group: "", ConfigurationFiles: []string{".*\\.\\w+proj", "appsettings.json"}, ExcludeFolders: []string(nil), Component: true, disabled: false},
			expectedErr:  nil,
		},
		{
			name:         "JavaScript",
			expectedItem: LanguageItem{Name: "JavaScript", Aliases: []string{"js", "node", "nodejs", "TypeScript"}, Kind: "programming", Group: "", ConfigurationFiles: []string{"[^-]package.json"}, ExcludeFolders: []string{"node_modules"}, Component: true, disabled: false},
			expectedErr:  nil,
		},
		{
			name:         "Python",
			expectedItem: LanguageItem{Name: "Python", Aliases: []string{"python3", "rusthon"}, Kind: "programming", Group: "", ConfigurationFiles: []string{"requirements.txt", "pyproject.toml"}, ExcludeFolders: []string(nil), Component: true, disabled: false},
			expectedErr:  nil,
		},
		{
			name:         "Rust",
			expectedItem: LanguageItem{Name: "Rust", Aliases: []string(nil), Kind: "programming", Group: "", ConfigurationFiles: []string{"Cargo.toml"}, ExcludeFolders: []string(nil), Component: true, disabled: false},
			expectedErr:  nil,
		},
		{
			name:         "PHP",
			expectedItem: LanguageItem{Name: "PHP", Aliases: []string{"inc"}, Kind: "programming", Group: "", ConfigurationFiles: []string{"composer.json", "[^-]package.json"}, ExcludeFolders: []string(nil), Component: true, disabled: false},
			expectedErr:  nil,
		},
	}

	languageFile := Get()

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			langItem, err := languageFile.GetLanguageByName(tt.name)

			if err != nil {
				assert.Regexp(t, *tt.expectedErr, err.Error(), "Error message should match")
			}

			assert.EqualValues(t, tt.expectedItem, langItem)
		})
	}
}

func TestGetLanguageByAlias(t *testing.T) {
	noLangErr := "no language found with this alias"
	tests := []struct {
		name         string
		alias        string
		expectedItem LanguageItem
		expectedErr  *string
	}{
		{
			name:         "Not a language",
			alias:        "not a language",
			expectedItem: LanguageItem{},
			expectedErr:  &noLangErr,
		},
		{
			name:         "Go",
			alias:        "golang",
			expectedItem: LanguageItem{Name: "Go", Aliases: []string{"golang"}, Kind: "programming", Group: "", ConfigurationFiles: []string{"go.mod"}, ExcludeFolders: []string{"vendor"}, Component: true, disabled: false},
			expectedErr:  nil,
		},
		{
			name:         "C#",
			alias:        "csharp",
			expectedItem: LanguageItem{Name: "C#", Aliases: []string{"csharp", "dotnet", ".NET"}, Kind: "programming", Group: "", ConfigurationFiles: []string{".*\\.\\w+proj", "appsettings.json"}, ExcludeFolders: []string(nil), Component: true, disabled: false},
			expectedErr:  nil,
		},
		{
			name:         "F#",
			alias:        "fsharp",
			expectedItem: LanguageItem{Name: "F#", Aliases: []string{"fsharp", "dotnet", ".NET"}, Kind: "programming", Group: "", ConfigurationFiles: []string{".*\\.\\w+proj", "appsettings.json"}, ExcludeFolders: []string(nil), Component: true, disabled: false},
			expectedErr:  nil,
		},
		{
			name:         "Visual Basic .NET",
			alias:        "visual basic",
			expectedItem: LanguageItem{Name: "Visual Basic .NET", Aliases: []string{"visual basic", "vbnet", "vb .net", "vb.net", "dotnet", ".NET"}, Kind: "programming", Group: "", ConfigurationFiles: []string{".*\\.\\w+proj", "appsettings.json"}, ExcludeFolders: []string(nil), Component: true, disabled: false},
			expectedErr:  nil,
		},
		{
			name:         "JavaScript",
			alias:        "TypeScript",
			expectedItem: LanguageItem{Name: "JavaScript", Aliases: []string{"js", "node", "nodejs", "TypeScript"}, Kind: "programming", Group: "", ConfigurationFiles: []string{"[^-]package.json"}, ExcludeFolders: []string{"node_modules"}, Component: true, disabled: false},
			expectedErr:  nil,
		},
		{
			name:         "Python",
			alias:        "python3",
			expectedItem: LanguageItem{Name: "Python", Aliases: []string{"python3", "rusthon"}, Kind: "programming", Group: "", ConfigurationFiles: []string{"requirements.txt", "pyproject.toml"}, ExcludeFolders: []string(nil), Component: true, disabled: false},
			expectedErr:  nil,
		},
		{
			name:         "PHP",
			alias:        "inc",
			expectedItem: LanguageItem{Name: "PHP", Aliases: []string{"inc"}, Kind: "programming", Group: "", ConfigurationFiles: []string{"composer.json", "[^-]package.json"}, ExcludeFolders: []string(nil), Component: true, disabled: false},
			expectedErr:  nil,
		},
	}

	languageFile := Get()

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			langItem, err := languageFile.GetLanguageByAlias(tt.alias)

			if err != nil {
				assert.Regexp(t, *tt.expectedErr, err.Error(), "Error message should match")
			}

			assert.EqualValues(t, tt.expectedItem, langItem)
		})
	}
}

func TestGetExcludedFolders(t *testing.T) {
	languageFile := Get()
	expectedFolders := []string{"node_modules", "vendor"}
	excludedFolders := languageFile.GetExcludedFolders()
	assert.ElementsMatch(t, excludedFolders, expectedFolders)
}
