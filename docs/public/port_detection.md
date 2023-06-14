## Port Detection in Alizer

Port detection is one of the step included during component detection and it refers to the ports used by the component that should be opened in the container. Because of the different nature of frameworks supported, Alizer tries to use customized ways to detect ports from source code, if necessary. **Only ports with value > 0 and < 65535 are valid**

There are three detection strategies currently available:
1) Docker file - Alizer looks for a Dockerfile, or Containerfile, in the root folder and tries to extract ports from it.
2) Compose file - Alizer searches for a docker-compose file in the root folder and tries to extract port of the service from it
3) Source - If a framework has been detected during component detection, a customized detection is performed. Below a detailed overview of the different strategies for each supported framework.

By default, Alizer perform a first a docker,  then a compose and finally source detection. **If one or more ports are found in one step, the rest are skipped.** (E.g - if a port is found in the docker file, Alizer will not search and analyze 
any docker-compose file and the source code).

It is also possible to customize the way Alizer searches for ports by defining the detection strategies to use and their order.
For example, by using a source and docker strategies, Alizer analyzes the source code and then the docker file, if any. Or docker and compose, to avoid analyzing the source.

If no port is found an empty list is returned.

### Port detection with a docker-compose file

The port detection in a `[compose|docker-compose].[yml|yaml]` file targets the `expose` and `ports` fields of the service related to the component, if any. To search for the specific component, Alizer look at the `build` field.

Example of `expose` - the result is [3000,8000]
```yaml
services:
  web:
    ...
    build: .
    expose:
      - "3000"
      - "8000"
  myapp2:
    ...
    expose:
      - "5000"
```

Example of short syntax `ports` (`[HOST:]CONTAINER[/PROTOCOL]`) - the result is [3000,3005,8000,8081,8002,6060]
```yaml
services:
  web:
    ...
    build: .
    ports:
      - "3000"                             # container port (3000), assigned to random host port
      - "8000:8000"                        # container port (8000), assigned to given host port (8000)
      - "127.0.0.1:8002:8002"              # container port (8002), assigned to given host port (8002) and bind to 127.0.0.1
      - "6060:6060/udp"                    # container port (6060) restricted to UDP protocol, assigned to given host (6060)
  myapp2:
    ...
    expose:
      - "5000"
```

Example of long syntax `ports` - the result is [6060]
```yaml
services:
  web:
    ...
    build: .
    ports:
      - target: 6060
        host_ip: 127.0.0.1
        published: 6060
        protocol: udp
        mode: host
  myapp2:
    ...
    expose:
      - "5000"
```

### Port detection with frameworks
### Java Frameworks

#### Micronaut

Alizer checks if the environment variable MICRONAUT_SERVER_SSL_ENABLED is set to true. If so, both MICRONAUT_SERVER_SSL_PORT and MICRONAUT_SERVER_PORT are checked (if false, only the MICRONAUT_SERVER_PORT is used for verification). If they are not set or they do not contain valid port values, Alizer searches for the `application.[yml|yaml]` file in `src/main/resources` folder and verify if one or more ports are set.

The known schema is:
```yaml
micronaut:
    server:
        port: <port>
        ssl:
            enabled: <true|false>
            port: <port>
```

#### OpenLiberty

Alizer searches for the `server.xml` file within the root or `src/main/liberty/config` folder and verify if a port is set.

The known schema is:
```xml
<server>
    <httpEndpoint id="defaultHttpEndpoint"
        httpPort="<port>"
        httpsPort="<port>" />
</server>
```

#### Quarkus 

Alizer follows the default behavior of Quarkus which reads configuration properties from multiple sources (by descending ordinal).

N.B: If insecure requests are disabled only the HTTP SSL port will be detected

1) It checks if the environment variable `QUARKUS_HTTP_SSL_PORT`, `QUARKUS_HTTP_INSECURE_REQUESTS` and `QUARKUS_HTTP_PORT` are set
2) It checks for `QUARKUS_HTTP_SSL_PORT`, `QUARKUS_HTTP_INSECURE_REQUESTS` and `QUARKUS_HTTP_PORT` within the `.env` file, if any, located in the root
3) It searches for any `application.[properties|yaml|yml]` file in `src/main/resources` folder and verify if a port is set.

The known schemas for application files are:

For `.properties`
```
...
quarkus.http.port=<port>
quarkus.http.insecure-requests=<enabled|redirect|disabled>
quarkus.http.ssl-port=<port>
...
```

For `.yaml|.yml`
```yaml
...
quarkus
  http
    port=<port>
    insecure-requests=<enabled|redirect|disabled>
    ssl-port=<port>
...
```

#### Springboot

Alizer checks if the environment variable SERVER_PORT or SERVER_HTTP_PORT are set. If not or they do not contain valid port values, it searches for the `application.[yml|yaml]` or `application.properties` file in `src/main/resources` folder and verify if a port is set.

The known schema for `application.[yml|yaml]` is:
```
server:
    port: <port>
    http:
        port <port>
```

and the schema for `application.properties`
```
server.port=<port>
server.http.port=<port>
```

#### Vertx

Alizer searches for any `json` file in `src/main/conf` folder and verify if a port is set by using this schema
```
{
    ...
    "http.port": <port>,
    "http.server": {
        "http.server.port": <port>,
    },
    ...
}
```

### Javascript Frameworks

### Angular

Alizer uses three ways to detect ports configuration in an Angular project
1) It checks if the file `angular.json` exists in the root and analyze it to see if a port is set, using the following schema
```
{
  ...
   "projects": {
    "project-name": {
      ...
      "architect": {
        "serve": {
          "options": {
            "host": "...",
            "port": "...",
  ...
}
```
N.B: `project-name` is the actual app name retrieved within the `package.json`

2) It checks if the `start` npm script sets a `port` (e.g. `"start": ".... --port <port>"`)
3) It checks if the file `angular-cli.json` exists in the root and analyze it to see if a port is set, using the following schema
```
{
  ...
  "defaults": {
    "serve": {
      "host": "...",
      "port": "...",
    }
  }
  ...
}
```

#### Express

Alizer searches for function `listen` calls as Express uses it to define `port` and `host` to be used use. 
Once all occurrences are found it tries to get the ports in three step:
1) The port is written in clear within the `listen` function (e.g `.listen(3000)`), Alizer extracts it.
2) The port argument is an env variable and Alizer tries to look for its value locally.
3) The port argument is a variable set within the code and Alizer tries to find it in the code that exists before the `.listen` call

Example of all 3 cases
```
const express = require('express')
const app = express()
const port = 3000

// variable - case 3
app.listen(port, () => {
  console.log(`Example app listening on port ${port}`)
})

// env variable - case 2
app.listen(process.env.PORT, () => {
  console.log(`Example app listening on port ${port}`)
})

// value in clear - case 1
app.listen(8080, () => {
  console.log(`Example app listening on port ${port}`)
})
```

### Next

Alizer searches for any port set within the start and dev scripts when dealing with a Next project
1) It checks if the `start` npm script sets a `port` (e.g. `"start": ".... -p <port>"`)
2) It checks if the `dev` npm script sets a `port` (e.g. `"dev": ".... -p <port>"`)

### Nuxt

In a Nuxt project, port detection works by analyzing the `package.json` and the `nuxt.config.js` files
1) It checks if the `start` npm script sets a `port` (e.g. `"start": ".... --port=<port>"`)
2) It checks if the `dev` npm script sets a `port` (e.g. `"dev": ".... --port=<port>"`)
3) It checks if the file `nuxt.config.js` exists in the root and analyze it to see if a port is set (e.g `port: <port>`)

#### React

Alizer follows the general rules by React. The strategy used consists of 3 steps:
1) It checks if the environment variable PORT is set
2) It checks for the `port` within the `.env` file, if any, located in the root
3) It checks if the `start` npm script sets a `port` (e.g. `"start": "PORT=<port> react-scripts start"`)

### Svelte

Alizer searches for any port configured within the `dev` npm script (e.g. `"dev": ".... --port <port>"` or `"dev": ".... PORT=<port>"`)

### Vue

Alizer uses four ways to detect ports configuration in a Vue project
1) It checks if the `start` npm script sets a `port` (e.g. `"start": ".... --port <port>"` or `"start": ".... PORT=<port>"`)
1) It checks if the `dev` npm script sets a `port` (e.g. `"dev": ".... --port <port>"` or `"dev": ".... PORT=<port>"`)
2) It checks if the port is configured within the `.env` file, if any, located in the root
3) It checks if the file `vue.config.js` exists in the root and analyze it to see if a port is set, using the following schema
```
exports = {
  ...
  port: <port>
  ...
}  
```

### Python Frameworks

#### Django

Alizer searches for the `default_port` property set within the `manage.py` file

Example
```python
    import django
    django.setup()

    # Override default port for `runserver` command
    from django.core.management.commands.runserver import Command as runserver
    runserver.default_port = "<port>"

```

### GoLang Frameworks

#### Beego

Alizer parses the `conf/app.conf` file looking for the `httpport` variable

Example
```
appname = beepkg
httpaddr = "127.0.0.1"
httpport = 9090
runmode ="dev"
autorender = false
recoverpanic = false
viewspath = "myview"
```

#### Echo

Alizer searches either for 2 different function calls - `ListenAndServe(:<port>)` and `Start(:<port>)` - or for the initialization of the `Addr` property of Server struct.
If `<port>` is a variable, it tries to find its value within the code.

Example
```go
func main() {
  e := echo.New()
  // add middleware and routes
  // ...
  s := http.Server{
    Addr:        ":8080",
    Handler:     e,
  }
  if err := s.ListenAndServe(); err != http.ErrServerClosed {
    log.Fatal(err)
  }
}
```

#### FastHttp

Alizer searches for the function `ListenAndServe(:<port>)` call. If `<port>` is a variable, it tries to find its value within the code.

```
fasthttp.ListenAndServe(":8080", myHandler.HandleFastHTTP)
```

#### Gin

Alizer searches for the function `Run(:<port>)` call. If `<port>` is a variable, it tries to find its value within the code.

```
router.Run(":3000")
```

#### Gofiber

Alizer searches for the function `Listen(:<port>)` call. If `<port>` is a variable, it tries to find its value within the code.

```
app.Listen(":3000")
```

#### Mux

Alizer searches either for the function `ListenAndServe(:<port>)`call or for the initialization of the `Addr` property of Server struct.
If `<port>` is a variable, it tries to find its value within the code.

```
srv := &http.Server{
        Handler:      r,
        Addr:         "127.0.0.1:8000",
        WriteTimeout: 15 * time.Second,
        ReadTimeout:  15 * time.Second,
    }

log.Fatal(srv.ListenAndServe())
```

#### Go (Raw)

In case a project doesn't use one of the above frameworks, and no ports have been found, alizer will search for 2 different function calls - `ListenAndServe(:<port>)` and `Start(:<port>)` - or for the initialization of the `Addr` property of Server struct. 