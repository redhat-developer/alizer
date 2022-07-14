## Port Detection in Alizer

Port detection is one of the step included during component detection and it refers to the ports used by the component that should be opened in the container.
Because of the different nature of each framework, Alizer tries to use customized ways to detect them, if necessary.
Only ports with value > 0 and < 65535 are valid.

The process consists of two steps:
1) Alizer looks for a Dockerfile in the root folder and tries to extract ports from it; if it fails it proceeds with (2)
2) Alizer searches for a docker-compose file in the root folder and tries to extract port of the web service from it; if it fails it proceeds with (3)
3) If a framework has been detected during component detection, a customized detection is performed. Below a detailed overview of the different strategies for each supported framework.

If no port is found an empty list is returned.

### Port detection with a docker-compose file

The port detection in a `docker-compose.[yml|yaml]` file targets the `expose` and `ports` fields of the `web` service, if any. 
If no `web` service is found no port is detected.

Example of `expose` - the result is [3000,8000]
```
services:
  web:
    ...
    expose:
      - "3000"
      - "8000"
  myapp2:
    ...
    expose:
      - "5000"
```

Example of short syntax `ports` (`[HOST:]CONTAINER[/PROTOCOL]`) - the result is [3000,3005,8000,8081,8002,6060]
```
services:
  web:
    ...
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
```
services:
  web:
    ...
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

Alizer searches for the `application.[yml|yaml]` file in `src/main/resources` folder and verify if a port is set.

The known schema is:
```
micronaut:
    server:
        port: <port>
```

#### OpenLiberty

Alizer searches for the `server.xml` file and verify if a port is set.

The known schema is:
```
<server>
    <httpEndpoint id="defaultHttpEndpoint"
        httpPort="<port>"
        httpsPort="<port>" />
</server>
```

#### Quarkus 

Alizer searches for the `application.properties` file in `src/main/resources` folder and verify if a port is set.

The known schema is:
```
...
quarkus.http.port=<port>
...
```

#### Springboot

Alizer searches for the `application.[yml|yaml]` or `application.properties` file in `src/main/resources` folder and verify if a port is set.

The known schema for `application.[yml|yaml]` is:
```
server:
    port: <port>
```

and the schema for `application.properties`
```
server.port=<port>
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

#### React

Alizer follows the general rules by React. The strategy used consists of 3 steps:
1) It checks if the environment variable PORT is set
2) It checks for the `port` within the `.env` file, if any, located in the root
3) It checks if the `start` npm script sets a `port` (e.g. `"start": "PORT=<port> react-scripts start"`)

### Python Frameworks

#### Django

Alizer searches for the `default_port` property set within the `manage.py` file

Example
```
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
```
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