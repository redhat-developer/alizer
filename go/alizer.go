package main

import (
	"flag"
	"os"

	"github.com/redhat-developer/alizer/go/pkg/cli"
	"github.com/redhat-developer/alizer/go/pkg/utils"
)

func main() {
	alizerCmd := cli.AlizerCommands()
	flag.Usage = func() {
		_ = alizerCmd.Help()
	}
	// parse the flags but hack around to avoid exiting with error code 2 on help
	flag.CommandLine.Init(os.Args[0], flag.ContinueOnError)
	args := os.Args[1:]
	if err := flag.CommandLine.Parse(args); err != nil {
		if err == flag.ErrHelp {
			os.Exit(0)
		}
	}

	if err := alizerCmd.Execute(); err != nil {
		utils.RedirectErrorToStdErrAndExit(err)
	}
}
