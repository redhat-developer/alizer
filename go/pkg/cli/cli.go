package cli

import (
	"errors"
	"flag"
	"fmt"

	"github.com/redhat-developer/alizer/go/pkg/cli/analyze"
	"github.com/redhat-developer/alizer/go/pkg/cli/component"
	"github.com/redhat-developer/alizer/go/pkg/cli/devfile"
	"github.com/redhat-developer/alizer/go/pkg/utils"
	"github.com/spf13/cobra"
	"github.com/spf13/pflag"
)

var (
	alizerLong = `
Alizer is a CLI tool for extracting informations about an application source code.
Find out more at https://github.com/redhat-developer/alizer
	`

	alizerExample = `
  # Analyze the source languages, frameworks and tools:
    alizer analyze /your/local/project/path

  # Detect one or more components starting from the root:
    alizer component /your/local/project/path

  # Select one devfile based on the informations found in the source tree:
    alizer devfile /your/local/project/path
	`

	rootHelpMessage = "To see a full list of commands, run 'alizer --help'"

	rootDefaultHelp = fmt.Sprintf("%s\n\nExamples:\n%s\n\n%s", alizerLong, alizerExample, rootHelpMessage)
)

func AlizerCommands() *cobra.Command {
	rootCmd := &cobra.Command{
		Use:     "alizer",
		Short:   "alizer",
		Long:    alizerLong,
		RunE:    ShowHelp,
		Example: alizerExample,
	}
	rootCmd.CompletionOptions.DisableDefaultCmd = true

	flag.CommandLine.String("o", "", "Specify output format, supported format: json")
	_ = pflag.CommandLine.MarkHidden("o")

	// Create a custom help function that will exit when we enter an invalid command, for example:
	// alizer foobar --help
	// which will exit with an error message: "unknown command 'foobar', type --help for a list of all commands"
	helpCmd := rootCmd.HelpFunc()
	rootCmd.SetHelpFunc(func(command *cobra.Command, args []string) {
		// Simple way of checking to see if the command has a parent (if it doesn't, it does not exist)
		if !command.HasParent() && len(args) > 0 {
			utils.RedirectErrorStringToStdErrAndExit(fmt.Sprintf("unknown command '%s', type --help for a list of all commands\n", args[0]))
		}
		helpCmd(command, args)
	})

	rootCmdList := append([]*cobra.Command{},
		analyze.NewCmdAnalyze(),
		component.NewCmdComponent(),
		devfile.NewCmdDevfile(),
	)

	rootCmd.AddCommand(rootCmdList...)

	return rootCmd
}

// ShowHelp will show the help correctly (and whether the command is invalid...)
// Taken from: https://github.com/redhat-developer/odo/blob/f55a4f0a7af4cd5f7c4e56dd70a66d38be0643cf/pkg/odo/cli/cli.go#L272
func ShowHelp(cmd *cobra.Command, args []string) error {

	if len(args) == 0 {
		// We will show a custom help when typing JUST `alizer`, directing the user to use `alizer --help` for a full help.
		// Thus, we will set cmd.SilenceUsage and cmd.SilenceErrors both to true so we do not output the usage or error out.
		cmd.SilenceUsage = true
		cmd.SilenceErrors = true

		// Print out the default "help" usage
		fmt.Println(rootDefaultHelp)
		return nil
	}

	//revive:disable:error-strings This is a top-level error message displayed as is to the end user
	return errors.New("invalid command - see available commands/subcommands above")
	//revive:enable:error-strings
}
