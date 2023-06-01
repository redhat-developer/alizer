package utils

import (
	"context"
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestGetCachedFilePathsFromRoot(t *testing.T) {
	missingPathErr := "no such file or directory"

	tests := []struct {
		name              string
		root              string
		ctx               context.Context
		expectedFilePaths []string
		expectedError     *string
	}{
		{
			name: "Case 1: Cached file paths exist",
			root: "path/to/root",
			ctx: context.WithValue(context.Background(), key("mapFilePathsFromRoot"), map[string][]string{
				"path/to/root": {"f1.txt", "f2.txt"},
			}),
			expectedFilePaths: []string{"f1.txt", "f2.txt"},
			expectedError:     nil,
		},
		{
			name:              "Case 2: Invalid file path",
			root:              "invalid/path",
			ctx:               context.Background(),
			expectedFilePaths: []string{},
			expectedError:     &missingPathErr,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			filePaths, err := GetCachedFilePathsFromRoot(tt.root, &tt.ctx)

			if err != nil {
				assert.Regexp(t, *tt.expectedError, err.Error(), "Error message should match")
			}

			assert.EqualValues(t, tt.expectedFilePaths, filePaths)
		})
	}
}
