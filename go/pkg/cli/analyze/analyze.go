package analyze

import (
	"github.com/redhat-developer/alizer/go/pkg/apis/recognizer"
	"github.com/redhat-developer/alizer/go/pkg/utils"
	"github.com/spf13/cobra"
)

var logLevel string

func NewCmdAnalyze() *cobra.Command {
	analyzeCmd := &cobra.Command{
		Use:     "analyze",
		Short:   "Analyze the source code and extract informations about the languages, frameworks and tools used",
		Long:    "Analyze the source code and extract informations about the languages, frameworks and tools used",
		Args:    cobra.MaximumNArgs(1),
		Run:     doAnalyze,
		Example: `  alizer analyze /your/local/project/path`,
	}
	analyzeCmd.Flags().StringVar(&logLevel, "log", "", "log level for alizer. Default value: error. Accepted values: [debug, info, warning]")

	return analyzeCmd
}

func doAnalyze(cmd *cobra.Command, args []string) {
	if len(args) == 0 {
		utils.PrintNoArgsWarningMessage(cmd.Name())
		return
	}
	err := utils.GenLogger(logLevel)
	if err != nil {
		utils.PrintWrongLoggingLevelMessage(cmd.Name())
		return
	}
	utils.PrintPrettifyOutput(recognizer.Analyze(args[0]))
}
