//+build wireinject

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
	"github.com/sample-fasthttp-rest-server/app/config"

	"github.com/google/wire"
)

func InitializeApplication(config config.Config) (application, error) {
	wire.Build(
		serverSet,
		newApplication,
	)
	return application{}, nil
}
