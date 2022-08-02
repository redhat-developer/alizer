package component

import (
	"github.com/redhat-developer/alizer/go/pkg/apis/recognizer"
	"github.com/redhat-developer/alizer/go/pkg/utils"
	"github.com/spf13/cobra"
)

func NewCmdComponent() *cobra.Command {
	componentCmd := &cobra.Command{
		Use:   "component",
		Short: "Detects all components in the source tree. ",
		Long: `Detects all components in the source tree, where a component is a small, independent piece of an application.
Examples of components: API Backend, Web Frontend, Payment Backend`,
		Args:    cobra.MaximumNArgs(1),
		Run:     doDetection,
		Example: `  alizer component /your/local/project/path`,
	}
	return componentCmd
}

func doDetection(cmd *cobra.Command, args []string) {
	if len(args) == 0 {
		utils.PrintNoArgsWarningMessage(cmd.Name())
		return
	}
	utils.PrintPrettifyOutput(recognizer.DetectComponents(args[0]))
}
