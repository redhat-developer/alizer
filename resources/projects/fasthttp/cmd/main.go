// Copyright 2019 Muhammet Arslan
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
//
// Written by Muhammet Arslan <https://medium.com/@muhammet.arslan>, August 2019

package main

import (
	"fmt"
	"flag"

	"github.com/sample-fasthttp-rest-server/app/config"
	"github.com/sample-fasthttp-rest-server/app/server"

	"github.com/joho/godotenv"
	"github.com/sirupsen/logrus"
	"golang.org/x/sync/errgroup"
)

func main() {
	// Load Configurations
	var envfile string
	flag.StringVar(&envfile, "env-file", "../.env", "Read in a file of environment variables")
	flag.Parse()

	godotenv.Load(envfile)
	config, err := config.Environ()
	if err != nil {
		logger := logrus.WithError(err)
		logger.Fatalln("main: invalid configuration")
	}
	//Init logging
	initLogging(config)

	// if trace level logging is enabled, output the
	// configuration parameters.
	if logrus.IsLevelEnabled(logrus.TraceLevel) {
		fmt.Println(config.String())
	}

	// Init application
	app, err := InitializeApplication(config)
	if err != nil {
		logger := logrus.WithError(err)
		logger.Fatalln("main: cannot initialize server")
	}
	
	// Start the server
	g := errgroup.Group{}
	g.Go(func() error {
		logrus.WithFields(
			logrus.Fields{
				"Host": config.Server.Host,
			},
		).Info("starting the http server")
		return app.Server.ListenAndServe()
	})

	// Wait the gorouitine
	if err := g.Wait(); err != nil {
		logrus.WithError(err).Fatalln("program terminated")
	}
}

// helper function configures the logging.
func initLogging(c config.Config) {
	if c.Logging.Debug {
		logrus.SetLevel(logrus.DebugLevel)
	}
	if c.Logging.Trace {
		logrus.SetLevel(logrus.TraceLevel)
	}
	if c.Logging.Text {
		logrus.SetFormatter(&logrus.TextFormatter{
			ForceColors:   c.Logging.Color,
			DisableColors: !c.Logging.Color,
		})
	} else {
		logrus.SetFormatter(&logrus.JSONFormatter{
			PrettyPrint: c.Logging.Pretty,
		})
	}
}

// application is the main struct for the OpsCrow / Bob server.
type application struct {
	Server *server.Server
}

// newApplication creates a new application struct.
func newApplication(
	Server *server.Server,
	) application {
	return application{
		Server: Server,
	}
}
