package component

import (
	"github.com/redhat-developer/alizer/go/pkg/apis/model"
	"github.com/redhat-developer/alizer/go/pkg/apis/recognizer"
	"github.com/redhat-developer/alizer/go/pkg/utils"
	"github.com/spf13/cobra"
)

var (
	logLevel                string
	portDetectionAlgorithms []string
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
	componentCmd.Flags().StringVar(&logLevel, "log", "", "log level for alizer. Default value: error. Accepted values: [debug, info, warning]")
	componentCmd.Flags().StringSliceVarP(&portDetectionAlgorithms, "port-detection", "p", []string{}, "port detection strategy to use when detecting a port. Currently supported strategies are 'docker', 'compose' and 'source'. You can pass more strategies at the same time. They will be executed in order. By default Alizer will execute docker, compose and source.")
	return componentCmd
}

func doDetection(cmd *cobra.Command, args []string) {
	if len(args) == 0 {
		utils.PrintNoArgsWarningMessage(cmd.Name())
		return
	}
	utils.PrintPrettifyOutput(recognizer.DetectComponentsWithPathAndPortStartegy(args[0], getPortDetectionStrategy()))
}

func getPortDetectionStrategy() []model.PortDetectionAlgorithm {
	portDetectionStrategy := []model.PortDetectionAlgorithm{}
	for _, algo := range portDetectionAlgorithms {
		if algo == "docker" {
			portDetectionStrategy = append(portDetectionStrategy, model.DockerFile)
		} else if algo == "compose" {
			portDetectionStrategy = append(portDetectionStrategy, model.Compose)
		} else if algo == "source" {
			portDetectionStrategy = append(portDetectionStrategy, model.Source)
		}
	}

	if len(portDetectionStrategy) > 0 {
		return portDetectionStrategy
	}

	return []model.PortDetectionAlgorithm{model.DockerFile, model.Compose, model.Source}
}
