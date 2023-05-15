package analyze

import (
	"github.com/redhat-developer/alizer/go/pkg/apis/recognizer"
	"github.com/redhat-developer/alizer/go/pkg/utils"
	"github.com/spf13/cobra"
)

func NewCmdAnalyze() *cobra.Command {
	analyzeCmd := &cobra.Command{
		Use:     "analyze",
		Short:   "Analyze the source code and extract informations about the languages, frameworks and tools used",
		Long:    "Analyze the source code and extract informations about the languages, frameworks and tools used",
		Args:    cobra.MaximumNArgs(1),
		Run:     doAnalyze,
		Example: `  alizer analyze /your/local/project/path`,
	}
	return analyzeCmd
}

func doAnalyze(cmd *cobra.Command, args []string) {
	if len(args) == 0 {
		utils.PrintNoArgsWarningMessage(cmd.Name())
		return
	}
	utils.PrintPrettifyOutput(recognizer.Analyze(args[0]))
}
