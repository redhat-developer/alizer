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

package server

import (
	"github.com/fate-lovely/phi"
	"github.com/valyala/fasthttp"
	"golang.org/x/sync/errgroup"
)

// A Server defines parameters for running an HTTP server.
type Server struct {
	Host    string
	Handler phi.Handler
}

// ListenAndServe initializes a server to respond to HTTP network requests.
func (s Server) ListenAndServe() (error) {
	var g errgroup.Group

	g.Go(func() error {
		return fasthttp.ListenAndServe(s.Host, s.Handler.ServeFastHTTP)
	})

	return g.Wait()
}