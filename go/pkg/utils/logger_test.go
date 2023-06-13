package utils

import (
	"testing"

	"github.com/stretchr/testify/assert"
	"go.uber.org/zap/zapcore"
)

func TestGetZapcoreLevel(t *testing.T) {
	wrongLevelErr := "log level wronglevel does not exist"
	tests := []struct {
		name          string
		level         string
		expectedLevel zapcore.Level
		expectedError *string
	}{
		{
			name:          "Case 1: debug",
			level:         "debug",
			expectedLevel: zapcore.DebugLevel,
			expectedError: nil,
		},
		{
			name:          "Case 2: warning",
			level:         "warning",
			expectedLevel: zapcore.WarnLevel,
			expectedError: nil,
		},
		{
			name:          "Case 3: info",
			level:         "info",
			expectedLevel: zapcore.InfoLevel,
			expectedError: nil,
		},
		{
			name:          "Case 4: empty",
			level:         "",
			expectedLevel: zapcore.ErrorLevel,
			expectedError: nil,
		},
		{
			name:          "Case 5: wrong",
			level:         "wronglevel",
			expectedLevel: zapcore.ErrorLevel,
			expectedError: &wrongLevelErr,
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			zapcoreLevel, err := getZapcoreLevel(tt.level)

			if zapcoreLevel != tt.expectedLevel {
				t.Errorf("Expected %d level, got %d", tt.expectedLevel, zapcoreLevel)
			}

			if err != nil {
				assert.Regexp(t, *tt.expectedError, err.Error(), "Error message should match")
			}
		})
	}
}

func TestGetOrCreateLogger(t *testing.T) {
	tests := []struct {
		name          string
		activate      bool
		expectedLevel zapcore.Level
	}{
		{
			name:          "Case 1: CliLogger not Existing",
			activate:      false,
			expectedLevel: -2,
		},
		{
			name:          "Case 2: CliLogger Existing",
			activate:      true,
			expectedLevel: 0,
		},
	}
	CliLogger.Activated = false
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if tt.activate {
				GenLogger("info")
			}
			logger := GetOrCreateLogger()
			assert.EqualValues(t, true, logger.GetSink().Enabled(int(tt.expectedLevel)))
		})
	}
}

func TestGenLogger(t *testing.T) {
	tests := []struct {
		name              string
		level             string
		expectedActivated bool
	}{
		{
			name:              "Case 1: No Logger",
			level:             "none",
			expectedActivated: false,
		},
		{
			name:              "Case 2: Gen Logger",
			level:             "",
			expectedActivated: true,
		},
	}
	CliLogger.Activated = false
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			_ = GenLogger(tt.level)
			assert.EqualValues(t, tt.expectedActivated, CliLogger.Activated)
		})
	}
}
