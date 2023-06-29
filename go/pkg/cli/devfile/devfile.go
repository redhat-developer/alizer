package devfile

import (
	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	"github.com/redhat-developer/alizer/go/pkg/apis/recognizer"
	"github.com/redhat-developer/alizer/go/pkg/utils"
	"github.com/spf13/cobra"
)

var logLevel, registry, minVersion, maxVersion string

func NewCmdDevfile() *cobra.Command {
	devfileCmd := &cobra.Command{
		Use:   "devfile",
		Short: "Select a devfile from a list of existing devfiles (from a devfile registry or other storage) based on the information extracted from the source tree.",
		Long:  "Select a devfile from a list of existing devfiles (from a devfile registry or other storage) based on the information extracted from the source tree.",
		Args:  cobra.MaximumNArgs(1),
		Run:   doSelectDevfile,
	}
	devfileCmd.Flags().StringVar(&logLevel, "log", "", "log level for alizer. Default value: error. Accepted values: [debug, info, warning]")
	devfileCmd.Flags().StringVar(&minVersion, "min-version", "", "minimum version of devfile schemaVersion. Minimum allowed version: 2.0.0")
	devfileCmd.Flags().StringVar(&maxVersion, "max-version", "", "maximum version of devfile schemaVersion. Minimum allowed version: 2.0.0")
	devfileCmd.Flags().StringVarP(&registry, "registry", "r", "", "registry where to download the devfiles. Default value: https://registry.devfile.io")
	return devfileCmd
}

func doSelectDevfile(cmd *cobra.Command, args []string) {
	if len(args) == 0 {
		utils.PrintNoArgsWarningMessage(cmd.Name())
		return
	}
	if registry == "" {
		registry = "https://registry.devfile.io/"
	}
	err := utils.GenLogger(logLevel)
	if err != nil {
		utils.PrintWrongLoggingLevelMessage(cmd.Name())
		return
	}
	filter := model.DevfileFilter{
		MinVersion: minVersion,
		MaxVersion: maxVersion,
	}
	utils.PrintPrettifyOutput(recognizer.MatchDevfiles(args[0], registry, filter))
}
