package utils

import (
	"encoding/json"
	"fmt"
	"os"

	"github.com/go-logr/logr"
	"go.uber.org/zap/zapcore"
	"sigs.k8s.io/controller-runtime/pkg/log/zap"
)

func PrintNoArgsWarningMessage(command string) {
	fmt.Printf(`
No arg received. Did you forget to add the project path to analyze?

Expected:
  alizer %s /your/local/project/path [flags]

To find out more, run 'alizer %s --help'
`, command, command)
}

func PrintWrongLoggingLevelMessage(command string) {
	fmt.Printf(`
Argument log has wrong value. Did you choose one of debug, warning, info?

Expected:
  alizer %s /your/local/project/path --log [debug, warning, info]

To find out more, run 'alizer %s --help'
`, command, command)
}

func PrintPrettifyOutput(value interface{}, err error) {
	if err != nil {
		RedirectErrorToStdErrAndExit(err)
	}
	b, err1 := json.MarshalIndent(value, "", "\t")
	if err1 != nil {
		RedirectErrorToStdErrAndExit(err1)
	}
	fmt.Println(string(b))
}

func RedirectErrorToStdErrAndExit(err error) {
	RedirectErrorStringToStdErrAndExit(err.Error())
}

func RedirectErrorStringToStdErrAndExit(err string) {
	fmt.Fprintln(os.Stderr, err)
	os.Exit(1)
}

var Logger logr.Logger

func getZapcoreLevel(level string) (zapcore.Level, error) {
	switch level {
	case "debug":
		return zapcore.DebugLevel, nil
	case "warning":
		return zapcore.WarnLevel, nil
	case "info":
		return zapcore.InfoLevel, nil
	case "":
		return zapcore.ErrorLevel, nil
	default:
		return nil, fmt.Errorf("log leve %s does not exist", level)
	}
}

func GenLogger(logLevel string) error {
	level, err := getZapcoreLevel(logLevel)
	if err != nil {
		return err
	}
	Logger = zap.New(zap.UseFlagOptions(&zap.Options{
		Development: true,
		TimeEncoder: zapcore.ISO8601TimeEncoder,
		Level:       level,
	}))
}
