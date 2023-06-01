package utils

import (
	"context"
	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	"github.com/redhat-developer/alizer/go/pkg/schema"
	"github.com/stretchr/testify/assert"
	"os"
	"path/filepath"
	"regexp"
	"testing"
)

func TestGetFilesByRegex(t *testing.T) {
	tests := []struct {
		name          string
		filePaths     []string
		regexFile     string
		expectedPaths []string
	}{
		{
			name:          "Case 1: Matching file paths",
			filePaths:     []string{"f1.csproj", "f2.fsproj", "f3.txt"},
			regexFile:     ".*\\.\\w+proj",
			expectedPaths: []string{"f1.csproj", "f2.fsproj"},
		},
		{
			name:          "Case 2: No matching file paths",
			filePaths:     []string{"f1.csproj", "f2.fsproj", "f3.txt"},
			regexFile:     "pattern",
			expectedPaths: []string{},
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			matchedPaths := GetFilesByRegex(&tt.filePaths, tt.regexFile)

			if len(matchedPaths) != len(tt.expectedPaths) {
				t.Errorf("Expected %d matching paths, got %d", len(tt.expectedPaths), len(matchedPaths))
			}
			assert.ElementsMatch(t, tt.expectedPaths, matchedPaths)
		})
	}
}

func TestGetFile(t *testing.T) {
	tests := []struct {
		name         string
		filePaths    []string
		wantedFile   string
		expectedPath string
	}{
		{
			name:         "Case 1: Matching file path",
			filePaths:    []string{"manage.py", "app.py", "requirements.txt"},
			wantedFile:   "app.py",
			expectedPath: "app.py",
		},
		{
			name:         "Case 2: No matching file path",
			filePaths:    []string{"manage.py", "app.py", "requirements.txt"},
			wantedFile:   "go.mod",
			expectedPath: "",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := GetFile(&tt.filePaths, tt.wantedFile)

			if result != tt.expectedPath {
				t.Errorf("Expected path %q, got %q", tt.expectedPath, result)
			}
		})
	}
}

func TestHasFile(t *testing.T) {
	tests := []struct {
		name       string
		files      []string
		wantedFile string
		expected   bool
	}{
		{
			name:       "Case 1: Matching file path",
			files:      []string{"f1.txt", "f2.txt", "f3.txt"},
			wantedFile: "f2.txt",
			expected:   true,
		},
		{
			name:       "Case 2: No matching file path",
			files:      []string{"f1.txt", "f2.txt", "f3.txt"},
			wantedFile: "f4.txt",
			expected:   false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := HasFile(&tt.files, tt.wantedFile)

			if result != tt.expected {
				t.Errorf("Expected value %v, got %v", tt.expected, result)
			}
		})
	}
}

func TestIsPathOfWantedFile(t *testing.T) {
	tests := []struct {
		name       string
		path       string
		wantedFile string
		expected   bool
	}{
		{
			name:       "Case 1: Matching file name",
			path:       "path/to/build.gradle",
			wantedFile: "build.gradle",
			expected:   true,
		},
		{
			name:       "Case 2: Mismatched file name",
			path:       "path/to/f1.txt",
			wantedFile: "build.gradle",
			expected:   false,
		},
		{
			name:       "Case 3: Capital case file name",
			path:       "path/to/File.txt",
			wantedFile: "file.txt",
			expected:   true,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := IsPathOfWantedFile(tt.path, tt.wantedFile)

			if result != tt.expected {
				t.Errorf("Expected value %v, got %v", tt.expected, result)
			}
		})
	}
}

func TestIsTagInFile(t *testing.T) {
	tests := []struct {
		name           string
		fileContent    string
		tag            string
		expectedResult bool
		expectedError  *string
	}{
		{
			name:           "Case 1: Tag exists in file",
			fileContent:    "File with tag flask",
			tag:            "flask",
			expectedResult: true,
			expectedError:  nil,
		},
		{
			name:           "Case 2: Tag does not exist in file",
			fileContent:    "File without tag",
			tag:            "django",
			expectedResult: false,
			expectedError:  nil,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			tempDir := t.TempDir()
			tempFile, err := os.Create(filepath.Join(tempDir, "testfile"))
			if err != nil {
				t.Errorf("Failed to create test file: %v", err)
			}

			_, err = tempFile.WriteString(tt.fileContent)
			if err != nil {
				t.Errorf("failed to write to temp file. err: %v", err)
			}

			result, err := IsTagInFile(tempFile.Name(), tt.tag)

			if result != tt.expectedResult {
				t.Errorf("Expected result %v, got %v", tt.expectedResult, result)
			}

			if !tt.expectedResult && err != nil {
				assert.Regexp(t, *tt.expectedError, err.Error(), "Error message should match")
			}
		})
	}
}

func TestIsTagInPomXMLFileArtifactId(t *testing.T) {
	missingFileErr := "no such file or directory"

	tests := []struct {
		name           string
		pomFilePath    string
		groupID        string
		artifactID     string
		expectedResult bool
		expectedError  *string
	}{
		{
			name:           "Case 1: Matching dependency artifactId and groupId",
			pomFilePath:    "testdata/pom-dependency.xml",
			groupID:        "org.acme",
			artifactID:     "dependency",
			expectedResult: true,
		},
		{
			name:           "Case 2: Matching plugin artifactId and groupId",
			pomFilePath:    "testdata/pom-plugin.xml",
			groupID:        "com.example",
			artifactID:     "plugin",
			expectedResult: true,
		},
		{
			name:           "Case 3: Matching plugin artifactId and groupId",
			pomFilePath:    "testdata/pom-profile.xml",
			groupID:        "com.example",
			artifactID:     "plugin",
			expectedResult: true,
		},
		{
			name:           "Case 4: No matching artifactId and groupId",
			pomFilePath:    "testdata/pom-dependency.xml",
			groupID:        "com.example",
			artifactID:     "nonexistent",
			expectedResult: false,
		},
		{
			name:           "Case 5: Error reading pom file",
			pomFilePath:    "nonexistent/pom-dependency.xml",
			groupID:        "com.example",
			artifactID:     "dependency",
			expectedResult: false,
			expectedError:  &missingFileErr,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result, err := IsTagInPomXMLFileArtifactId(tt.pomFilePath, tt.groupID, tt.artifactID)

			if result != tt.expectedResult {
				t.Errorf("Expected result %v, got %v", tt.expectedResult, result)
			}

			if err != nil {
				assert.Regexp(t, *tt.expectedError, err.Error(), "Error message should match")
			}
		})
	}
}

func TestIsTagInPomXMLFile(t *testing.T) {
	missingFileErr := "no such file or directory"

	tests := []struct {
		name           string
		pomFilePath    string
		tag            string
		expectedResult bool
		expectedError  *string
	}{
		{
			name:           "Case 1: Matching dependency tag",
			pomFilePath:    "testdata/pom-dependency.xml",
			tag:            "org.acme",
			expectedResult: true,
		},
		{
			name:           "Case 2: Matching plugin tag",
			pomFilePath:    "testdata/pom-plugin.xml",
			tag:            "com.example",
			expectedResult: true,
		},
		{
			name:           "Case 4: No matching tag",
			pomFilePath:    "testdata/pom-dependency.xml",
			tag:            "nonexistent",
			expectedResult: false,
		},
		{
			name:           "Case 5: Error reading pom file",
			pomFilePath:    "nonexistent/pom-dependency.xml",
			tag:            "dependency",
			expectedResult: false,
			expectedError:  &missingFileErr,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result, err := IsTagInPomXMLFile(tt.pomFilePath, tt.tag)

			if result != tt.expectedResult {
				t.Errorf("Expected result %v, got %v", tt.expectedResult, result)
			}

			if err != nil {
				assert.Regexp(t, *tt.expectedError, err.Error(), "Error message should match")
			}
		})
	}
}

func TestGetPomFileContent(t *testing.T) {
	missingFileErr := "no such file or directory"

	testCases := []struct {
		name           string
		filePath       string
		expectedResult schema.Pom
		expectedError  *string
	}{
		{
			name:     "Case 1: Valid file",
			filePath: "testdata/pom-dependency.xml",
			expectedResult: schema.Pom{
				Dependencies: struct {
					Text       string `xml:",chardata"`
					Dependency []struct {
						Text       string `xml:",chardata"`
						GroupId    string `xml:"groupId"`
						ArtifactId string `xml:"artifactId"`
						Version    string `xml:"version"`
						Scope      string `xml:"scope"`
					} `xml:"dependency"`
				}{
					Text: "\n        \n    ",
					Dependency: []struct {
						Text       string `xml:",chardata"`
						GroupId    string `xml:"groupId"`
						ArtifactId string `xml:"artifactId"`
						Version    string `xml:"version"`
						Scope      string `xml:"scope"`
					}{
						{
							Text:       "\n            \n            \n        ",
							GroupId:    "org.acme",
							ArtifactId: "dependency",
							Version:    "",
							Scope:      "",
						},
					},
				},
			},
			expectedError: nil,
		},
		{
			name:           "Case 2: File does not exist",
			filePath:       "path/to/nonexistent/file.xml",
			expectedResult: schema.Pom{},
			expectedError:  &missingFileErr,
		},
	}

	for _, tt := range testCases {
		t.Run(tt.name, func(t *testing.T) {
			result, err := GetPomFileContent(tt.filePath)

			if err != nil {
				assert.Regexp(t, *tt.expectedError, err.Error(), "Error message should match")
			}

			assert.EqualValues(t, tt.expectedResult, result)
		})
	}
}

func TestIsTagInPackageJsonFile(t *testing.T) {
	tests := []struct {
		name           string
		file           string
		tag            string
		expectedResult bool
	}{
		{
			name:           "Case 1: Tag exists in Dependencies",
			file:           "testdata/package.json",
			tag:            "dep-tag",
			expectedResult: true,
		},
		{
			name:           "Case 2: Tag exists in DevDependencies",
			file:           "testdata/package.json",
			tag:            "dev-tag",
			expectedResult: true,
		},
		{
			name:           "Case 3: Tag exists in PeerDependencies",
			file:           "testdata/package.json",
			tag:            "peer-tag",
			expectedResult: true,
		},
		{
			name:           "Case 4: Tag does not exist",
			file:           "testdata/package.json",
			tag:            "nonexistent",
			expectedResult: false,
		},
		{
			name:           "Case 5: Error reading package.json file",
			file:           "nonexistent/package.json",
			tag:            "react",
			expectedResult: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := IsTagInPackageJsonFile(tt.file, tt.tag)

			if result != tt.expectedResult {
				t.Errorf("Expected result %v, got %v", tt.expectedResult, result)
			}
		})
	}
}

func TestGetPackageJsonSchemaFromFile(t *testing.T) {
	missingFileErr := "no such file or directory"

	tests := []struct {
		name           string
		filePath       string
		expectedResult schema.PackageJson
		expectedError  *string
	}{
		{
			name:     "Case 1: Valid package.json",
			filePath: "testdata/package.json",
			expectedResult: schema.PackageJson{
				Name: "app",
				Dependencies: map[string]string{
					"dep-tag": "1.0.0",
				},
				DevDependencies: map[string]string{
					"@dev-tag": "1.1.0",
				},
				PeerDependencies: map[string]string{
					"peer-tag": "1.x",
				},
			},
			expectedError: nil,
		},
		{
			name:           "Case 2: Nonexistent package.json",
			filePath:       "nonexistent/package.json",
			expectedResult: schema.PackageJson{},
			expectedError:  &missingFileErr,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result, err := GetPackageJsonSchemaFromFile(tt.filePath)

			assert.EqualValues(t, tt.expectedResult, result)

			if err != nil {
				assert.Regexp(t, *tt.expectedError, err.Error(), "Error message should match")
			}
		})
	}
}

func TestIsTagInComposerJsonFile(t *testing.T) {
	tests := []struct {
		name           string
		file           string
		tag            string
		expectedResult bool
	}{
		{
			name:           "Case 1: Tag exists in require",
			file:           "testdata/composer.json",
			tag:            "php",
			expectedResult: true,
		},
		{
			name:           "Case 2: Tag exists in require-dev",
			file:           "testdata/composer.json",
			tag:            "dev",
			expectedResult: true,
		},
		{
			name:           "Case 3: Tag does not exist",
			file:           "testdata/composer.json",
			tag:            "nonexistent",
			expectedResult: false,
		},
		{
			name:           "Case 4: Error reading composer.json file",
			file:           "nonexistent/composer.json",
			tag:            "php",
			expectedResult: false,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := IsTagInComposerJsonFile(tt.file, tt.tag)

			if result != tt.expectedResult {
				t.Errorf("Expected result %v, got %v", tt.expectedResult, result)
			}
		})
	}
}

func TestGetComposerJsonSchemaFromFile(t *testing.T) {
	missingFileErr := "no such file or directory"

	tests := []struct {
		name           string
		filePath       string
		expectedResult schema.ComposerJson
		expectedError  *string
	}{
		{
			name:     "Case 1: Valid composer.json",
			filePath: "testdata/composer.json",
			expectedResult: schema.ComposerJson{
				Name: "laravel/laravel",
				Require: map[string]string{
					"php": "^8.0.2",
				},
				RequireDev: map[string]string{
					"dev": "^9.5.10",
				},
			},
			expectedError: nil,
		},
		{
			name:           "Case 2: Nonexistent package.json",
			filePath:       "nonexistent/package.json",
			expectedResult: schema.ComposerJson{},
			expectedError:  &missingFileErr,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result, err := GetComposerJsonSchemaFromFile(tt.filePath)

			assert.EqualValues(t, tt.expectedResult, result)

			if err != nil {
				assert.Regexp(t, *tt.expectedError, err.Error(), "Error message should match")
			}
		})
	}
}

func TestGetFilePathsFromRoot(t *testing.T) {
	tempDir := t.TempDir()

	testFiles := []string{
		".gitignore",
		"f1.txt",
		"f2.txt",
		"ignored_file.txt",
		"ignoredDir/f1.ignore",
		"ignoredDir/f2.ignore",
		"subdir/f3.txt",
		"vendor/f4.txt",
		"node_modules/f5.txt",
		"subdir/vendor/f6.txt",
		"subdir/node_modules/f6.txt",
	}

	expectedFiles := []string{
		filepath.Join(tempDir),
		filepath.Join(tempDir, ".gitignore"),
		// will return the root of ignored dir, but nothing inside
		filepath.Join(tempDir, "ignoredDir"),
		filepath.Join(tempDir, "f1.txt"),
		filepath.Join(tempDir, "f2.txt"),
		filepath.Join(tempDir, "subdir"),
		filepath.Join(tempDir, "subdir/f3.txt"),
	}

	gitIgnoreContent := `# .gitignore contents
ignored_file.txt
ignoredDir/
	`

	for _, path := range testFiles {
		err := os.MkdirAll(filepath.Join(tempDir, filepath.Dir(path)), 0755)
		if err != nil {
			t.Errorf("Failed to create test dir: %v", err)
		}
		_, err = os.Create(filepath.Join(tempDir, path))
		if err != nil {
			t.Errorf("Failed to create test file: %v", err)
		}

		gitIgnore, err := os.OpenFile(filepath.Clean(tempDir)+"/.gitignore", os.O_APPEND|os.O_WRONLY|os.O_CREATE, 0600)
		if err != nil {
			t.Errorf("Failed to open file: %v", err)
		}
		_, err = gitIgnore.WriteString(gitIgnoreContent)
		if err != nil {
			t.Errorf("Failed to write to file: %v", err)
		}
	}

	filePaths, err := GetFilePathsFromRoot(tempDir)
	if err != nil {
		t.Errorf("Expected no error, got: %v", err)
	}

	assert.ElementsMatch(t, expectedFiles, filePaths)
}

func TestGetFilePathsInRoot(t *testing.T) {
	tempDir := t.TempDir()

	testFiles := []string{
		".hiddenfile",
		"f1.txt",
		"f2.txt",
		"subdir/f3.txt",
		"subdir/nested/f6.txt",
	}

	expectedFiles := []string{
		filepath.Join(tempDir, ".hiddenfile"),
		filepath.Join(tempDir, "f1.txt"),
		filepath.Join(tempDir, "f2.txt"),
		filepath.Join(tempDir, "subdir"),
	}

	for _, path := range testFiles {
		err := os.MkdirAll(filepath.Join(tempDir, filepath.Dir(path)), 0755)
		if err != nil {
			t.Errorf("Failed to create test dir: %v", err)
		}
		_, err = os.Create(filepath.Join(tempDir, path))
		if err != nil {
			t.Errorf("Failed to create test file: %v", err)
		}
	}

	filePaths, err := GetFilePathsInRoot(tempDir)
	if err != nil {
		t.Errorf("Expected no error, got: %v", err)
	}

	assert.ElementsMatch(t, expectedFiles, filePaths)
}

func TestConvertPropertiesFileToMap(t *testing.T) {
	testCases := []struct {
		name           string
		fileContent    []byte
		expectedResult map[string]string
		expectedError  error
	}{
		{
			name:           "Case 1: Empty file",
			fileContent:    []byte(""),
			expectedResult: map[string]string{},
			expectedError:  nil,
		},
		{
			name:           "Case 2: Valid properties file",
			fileContent:    []byte("key1=value1\nkey2=value2\nkey3=value3"),
			expectedResult: map[string]string{"key1": "value1", "key2": "value2", "key3": "value3"},
			expectedError:  nil,
		},
		{
			name:           "Case 3: File with empty lines and comments",
			fileContent:    []byte("\n# Comment\nkey1=value1\n\nkey2=value2\n"),
			expectedResult: map[string]string{"key1": "value1", "key2": "value2"},
			expectedError:  nil,
		},
	}

	for _, tt := range testCases {
		t.Run(tt.name, func(t *testing.T) {

			result, err := ConvertPropertiesFileToMap(tt.fileContent)

			if err != tt.expectedError {
				t.Errorf("Expected error %v, got %v", tt.expectedError, err)
			}

			assert.EqualValues(t, tt.expectedResult, result)
		})
	}
}

func TestGetValidPortsFromEnvs(t *testing.T) {
	tests := []struct {
		name           string
		envs           []string
		mockEnvValues  map[string]string
		expectedResult []int
	}{
		{
			name:           "Case 1: Valid environment variables",
			envs:           []string{"PORT1", "PORT2"},
			mockEnvValues:  map[string]string{"PORT1": "8080", "PORT2": "9000"},
			expectedResult: []int{8080, 9000},
		},
		{
			name:           "Case 2: Invalid environment variable",
			envs:           []string{"PORT3"},
			mockEnvValues:  map[string]string{"PORT3": "invalid"},
			expectedResult: nil,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			for env, value := range tt.mockEnvValues {
				t.Setenv(env, value)
			}

			result := GetValidPortsFromEnvs(tt.envs)

			assert.EqualValues(t, tt.expectedResult, result)
		})
	}
}

func TestGetValidPorts(t *testing.T) {
	tests := []struct {
		name           string
		ports          []string
		expectedResult []int
	}{
		{
			name:           "Case 1: Valid ports",
			ports:          []string{"8080", "9000", "3030", "invalid"},
			expectedResult: []int{8080, 9000, 3030},
		},
		{
			name:           "Case 2: Invalid ports",
			ports:          []string{"invalid", "f3030"},
			expectedResult: nil,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := GetValidPorts(tt.ports)

			assert.EqualValues(t, tt.expectedResult, result)
		})
	}
}

func TestGetAnyApplicationFilePath(t *testing.T) {
	tempDir := t.TempDir()
	ctx := context.Background()

	testFiles := []string{
		"f1.file",
		"f2.txt",
	}

	for _, path := range testFiles {
		err := os.MkdirAll(filepath.Join(tempDir, filepath.Dir(path)), 0755)
		if err != nil {
			t.Errorf("Failed to create test dir: %v", err)
		}
		_, err = os.Create(filepath.Join(tempDir, path))
		if err != nil {
			t.Errorf("Failed to create test file: %v", err)
		}
	}

	tests := []struct {
		name           string
		root           string
		propsFiles     []model.ApplicationFileInfo
		expectedResult string
	}{
		{
			name: "Case 1: Matching file found with regex",
			root: tempDir,
			propsFiles: []model.ApplicationFileInfo{
				{File: ".*.txt", Dir: ""},
			},
			expectedResult: filepath.Join(tempDir, "f2.txt"),
		},
		{
			name: "Case 2: Matching file found exact",
			root: tempDir,
			propsFiles: []model.ApplicationFileInfo{
				{File: "f1.file", Dir: ""},
			},
			expectedResult: filepath.Join(tempDir, "f1.file"),
		},
		{
			name: "Case 3: No matching file found",
			root: tempDir,
			propsFiles: []model.ApplicationFileInfo{
				{File: "missing.file", Dir: "."},
			},
			expectedResult: "",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := GetAnyApplicationFilePath(tt.root, tt.propsFiles, &ctx)
			if result != tt.expectedResult {
				t.Errorf("Expected result %s, got %s", tt.expectedResult, result)
			}
		})
	}
}

func TestGetAnyApplicationFilePathExactMatch(t *testing.T) {
	tempDir := t.TempDir()

	testFiles := []string{
		"f1.txt",
		"subdir/f1.txt",
	}

	for _, path := range testFiles {
		err := os.MkdirAll(filepath.Join(tempDir, filepath.Dir(path)), 0755)
		if err != nil {
			t.Errorf("Failed to create test dir: %v", err)
		}
		_, err = os.Create(filepath.Join(tempDir, path))
		if err != nil {
			t.Errorf("Failed to create test file: %v", err)
		}
	}

	tests := []struct {
		name           string
		root           string
		propsFiles     []model.ApplicationFileInfo
		expectedResult string
	}{
		{
			name: "Case 1: Matching file found in nested dir",
			root: tempDir,
			propsFiles: []model.ApplicationFileInfo{
				{File: "f1.txt", Dir: "subdir"},
				{File: "f1.txt", Dir: ""},
			},
			expectedResult: filepath.Join(tempDir, "subdir/f1.txt"),
		},
		{
			name: "Case 2: Matching file found in root",
			root: tempDir,
			propsFiles: []model.ApplicationFileInfo{
				{File: "f1.txt", Dir: ""},
				{File: "f1.txt", Dir: "subdir"},
			},
			expectedResult: filepath.Join(tempDir, "f1.txt"),
		},
		{
			name: "Case 3: No matching file found",
			root: tempDir,
			propsFiles: []model.ApplicationFileInfo{
				{File: ".*.txt", Dir: ""},
			},
			expectedResult: "",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := GetAnyApplicationFilePathExactMatch(tt.root, tt.propsFiles)
			if result != tt.expectedResult {
				t.Errorf("Expected result %s, got %s", tt.expectedResult, result)
			}
		})
	}
}

func Test_readAnyApplicationFile(t *testing.T) {
	tempDir := t.TempDir()

	file1Content := []byte("file1 content")
	file2Content := []byte("file2 content")
	file1Path := filepath.Join(tempDir, "f1.txt")
	file2Path := filepath.Join(tempDir, "f2.txt")
	err := os.WriteFile(file1Path, file1Content, 0644)
	if err != nil {
		t.Errorf("failed to write to temp file. err: %v", err)
	}
	err = os.WriteFile(file2Path, file2Content, 0644)
	if err != nil {
		t.Errorf("failed to write to temp file. err: %v", err)
	}

	noFileFoundErr := "no file found"

	tests := []struct {
		name          string
		root          string
		propsFiles    []model.ApplicationFileInfo
		exactMatch    bool
		ctx           context.Context
		expectedBytes []byte
		expectedError *string
	}{
		{
			name:          "Case 1: Exact match, file exists",
			root:          tempDir,
			propsFiles:    []model.ApplicationFileInfo{{Dir: "", File: "f1.txt"}},
			exactMatch:    true,
			ctx:           context.Background(),
			expectedBytes: file1Content,
			expectedError: nil,
		},
		{
			name:          "Case 2: Exact match, file does not exist",
			root:          tempDir,
			propsFiles:    []model.ApplicationFileInfo{{Dir: "", File: "f3.txt"}},
			exactMatch:    true,
			ctx:           context.Background(),
			expectedBytes: nil,
			expectedError: &noFileFoundErr,
		},
		{
			name:          "Case 3: Non-exact match, file exists",
			root:          tempDir,
			propsFiles:    []model.ApplicationFileInfo{{Dir: "", File: "f2.txt"}},
			exactMatch:    false,
			ctx:           context.Background(),
			expectedBytes: file2Content,
			expectedError: nil,
		},
		{
			name:          "Case 4: Non-exact match, file does not exist",
			root:          tempDir,
			propsFiles:    []model.ApplicationFileInfo{{Dir: "", File: "f3.txt"}},
			exactMatch:    false,
			ctx:           context.Background(),
			expectedBytes: nil,
			expectedError: &noFileFoundErr,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			bytes, err := readAnyApplicationFile(tt.root, tt.propsFiles, tt.exactMatch, &tt.ctx)

			if err != nil && tt.expectedError != nil {
				assert.Regexp(t, *tt.expectedError, err.Error(), "Error message should match")
			}

			assert.EqualValues(t, tt.expectedBytes, bytes)
		})
	}
}

func TestFindPortSubmatch(t *testing.T) {
	testCases := []struct {
		name         string
		re           *regexp.Regexp
		text         string
		group        int
		expectedPort int
	}{
		{
			name:         "Case 1: Valid port in text",
			re:           regexp.MustCompile(`port:\s*(\d+)*`),
			text:         "port: 1234",
			group:        1,
			expectedPort: 1234,
		},
		{
			name:         "Case 2: No valid port in text",
			re:           regexp.MustCompile(`(\d{4})`),
			text:         "Text without any valid ports",
			group:        1,
			expectedPort: -1,
		},
		{
			name:         "Case 3: Invalid port in text",
			re:           regexp.MustCompile(`port:\s*(\d+)*`),
			text:         "port: 65535",
			group:        1,
			expectedPort: -1,
		},
	}

	for _, tt := range testCases {
		t.Run(tt.name, func(t *testing.T) {
			port := FindPortSubmatch(tt.re, tt.text, tt.group)
			assert.EqualValues(t, tt.expectedPort, port)
		})
	}
}

func TestFindPotentialPortGroup(t *testing.T) {
	testCases := []struct {
		name         string
		re           *regexp.Regexp
		text         string
		group        int
		expectedPort string
	}{
		{
			name:         "Case 1: First occurrence of valid port in text",
			re:           regexp.MustCompile(`port:\s*(\d+)*`),
			text:         "port: 1234, port: 5678",
			group:        1,
			expectedPort: "1234",
		},
		{
			name:         "Case 2: No valid port in text",
			re:           regexp.MustCompile(`(\d{4})`),
			text:         "Text without any valid ports",
			group:        1,
			expectedPort: "",
		},
	}

	for _, tt := range testCases {
		t.Run(tt.name, func(t *testing.T) {
			port := FindPotentialPortGroup(tt.re, tt.text, tt.group)
			assert.EqualValues(t, tt.expectedPort, port)
		})
	}
}

func TestFindAllPortsSubmatch(t *testing.T) {
	testCases := []struct {
		name          string
		re            *regexp.Regexp
		text          string
		group         int
		expectedPorts []int
	}{
		{
			name:          "Case 1: Valid ports in text",
			re:            regexp.MustCompile(`port:\s*(\d+)*`),
			text:          "port: 1234, port: 5678, invalid: 3444",
			group:         1,
			expectedPorts: []int{1234, 5678},
		},
		{
			name:          "Case 2: No valid ports in text",
			re:            regexp.MustCompile(`(\d{4})`),
			text:          "Text without any valid ports",
			group:         1,
			expectedPorts: nil,
		},
	}

	for _, tt := range testCases {
		t.Run(tt.name, func(t *testing.T) {
			ports := FindAllPortsSubmatch(tt.re, tt.text, tt.group)
			assert.EqualValues(t, tt.expectedPorts, ports)
		})
	}
}

func TestGetPortValuesFromEnvFile(t *testing.T) {
	testCases := []struct {
		name           string
		root           string
		regexes        []string
		envFileContent string
		expectedPorts  []int
	}{
		{
			name:           "Case 1: Valid port values",
			root:           "/path/to/root",
			regexes:        []string{"PORT=(\\d*)", "ANOTHER_PORT=(\\d*)"},
			envFileContent: "PORT=3030\nANOTHER_PORT=8080\n",
			expectedPorts:  []int{3030, 8080},
		},
		{
			name:           "Case 2: No valid port values",
			root:           "/path/to/root",
			regexes:        []string{"PORT=(\\d*)", "ANOTHER_PORT=(\\d*)"},
			envFileContent: "SOME_VARIABLE=abc\nANOTHER_VARIABLE=123\n",
			expectedPorts:  nil,
		},
	}

	for _, tt := range testCases {
		t.Run(tt.name, func(t *testing.T) {
			// mock getEnvFileContent
			getEnvFileContent = func(root string) (string, error) {
				return tt.envFileContent, nil
			}

			ports := GetPortValuesFromEnvFile(tt.root, tt.regexes)
			assert.EqualValues(t, tt.expectedPorts, ports)
		})
	}
}

func TestGetStringValueFromEnvFile(t *testing.T) {
	testCases := []struct {
		name           string
		root           string
		regex          string
		envFileContent string
		expectedValue  string
	}{
		{
			name:           "Case 1: Valid value",
			root:           "/path/to/root",
			regex:          "SOME_VARIABLE=(\\w+)",
			envFileContent: "SOME_VARIABLE=value123\n",
			expectedValue:  "value123",
		},
		{
			name:           "Case 2: No valid value",
			root:           "/path/to/root",
			regex:          "SOME_VARIABLE=(\\w+)",
			envFileContent: "ANOTHER_VARIABLE=123\n",
			expectedValue:  "",
		},
	}

	for _, tt := range testCases {
		t.Run(tt.name, func(t *testing.T) {
			// mock getEnvFileContent
			getEnvFileContent = func(root string) (string, error) {
				return tt.envFileContent, nil
			}

			value := GetStringValueFromEnvFile(tt.root, tt.regex)

			if value != tt.expectedValue {
				t.Errorf("Expected value %q, got %q", tt.expectedValue, value)
			}
		})
	}
}

func TestNormalizeSplit(t *testing.T) {
	tests := []struct {
		name         string
		file         string
		expectedDir  string
		expectedFile string
	}{
		{
			name:         "Case 1: File with directory",
			file:         "path/to/f1.txt",
			expectedDir:  "path/to/",
			expectedFile: "f1.txt",
		},
		{
			name:         "Case 2: File in root directory",
			file:         "f1.txt",
			expectedDir:  "./",
			expectedFile: "f1.txt",
		},
		{
			name:         "Case 3: Empty file",
			file:         "",
			expectedDir:  "./",
			expectedFile: "",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			dir, file := NormalizeSplit(tt.file)

			if dir != tt.expectedDir {
				t.Errorf("Expected dir %q, got %q", tt.expectedDir, dir)
			}
			if file != tt.expectedFile {
				t.Errorf("Expected file %q, got %q", tt.expectedFile, file)
			}
		})
	}
}
