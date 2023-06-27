# Port detection optimizations

## Related Github Issues
[Alizer port detection logic improvement](https://github.com/redhat-developer/alizer/issues/250)

## Background
The current port detection logic is prone to performance issues. An example past incident can be found in the issue [alizer#208](https://github.com/redhat-developer/alizer/issues/208) where alizer spent a big amount of time on port detection.

When [alizer#208](https://github.com/redhat-developer/alizer/issues/208) was closed, we have introduced the `excluded_folders` in [language_customization.yaml](https://github.com/redhat-developer/alizer/blob/main/go/pkg/utils/langfiles/resources/languages-customization.yml) as a workaround to reduce time of execution. This solution was focusing on the component detection part. As component detection is a different analysis process than port detection we will have to create a new more optimized solution for port detection too.

This proposal is based on the idea that for port detection we need only **some** paths and not **all** paths. More detailed, upon port detection we already know the component we are detecting ports. Another idea could be to make `port-detection` configurable and of course we can also define different strategies/algorithms for port detection.

## Optimizations
The main idea of the proposal will be to introduce 2 different optimizations regarding port detection (potential child issues in an EPIC):

* [Add specific port detection paths to each detector](#detector-port-detection-paths).
* [Define combining strategies and no strategy for port detection]()

## Detector Port Detection Paths
As a first step for better performance during port detection we can define certain paths and files for port detection and each detector. So, upon port detection process the detector will only check for configured ports inside these paths. We can apply this to all of the detectors in order to have a consistent file read process for each `DoPortDetection` function.

### Current Detector Status
One thing worth mentioning is the current detector status. We have different status for each detector and we can identify some groups of detector policies:

| Policy                                              | Detector                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
|-----------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Fetches all cached paths and checks each file.      | [echo](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/go/echo_detector.go#L36), [fasthttp](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/go/fasthttp_detector.go#L36), [gin](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/go/gin_detector.go#L36), [go-runtime](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/go/go_detector.go#L36), [fiber](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/go/gofiber_detector.go#L36), [mux](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/go/mux_detector.go#L36)                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| Looks for a specific file inside the component path | [beego](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/go/gin_detector.go#LL36C30-L36C30), [jboss-eap](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/java/jboss_eap_detector.go#L33), [micronaut](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/java/micronaut_detector.go#L49), [openliberty](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/java/openliberty_detector.go#L43), [quarkus](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/java/quarkus_detector.go#L55), [spring](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/java/spring_detector.go#L49), [vert.x](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/java/vertx_detector.go#L44), [wildfly](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/java/wildfly_detector.go#L33),  [laravel](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/php/laravel_detector.go#L32),  |
| Looks for specific paths                            | [django](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/python/django_detector.go#L30), [flask](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/python/flask_detector.go)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| Port detection not implemented yet                  | [dotnet](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/dotnet/dotnet_detector.go#L54)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| Port detection not implemented yet                  | [dotnet](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/enricher/framework/dotnet/dotnet_detector.go#L54)    

*As a result the golang related frameworks are using all component paths in order to apply port detection.*

### GetPortDetectionPaths
A way to ensure consistency on doing port detection, could be to define `GetPortDetectionPaths` inside every detector. This function could provide a list of files to the `DoPortDetection`.

```golang
// It returns a list of accepted paths or specific files from a given rootPath
func (e BeegoDetector) GetPortDetectionFiles(rootPath string) []string {
	paths := []string{"app.conf"}
	return utils.ReadComponentFiles(rootPath, paths)
}

func ReadComponentFiles(rootPath string, paths []string) []string {
// This function should merge the logic of utils.readAnyApplicationFile()
}
```

*After this update, as we will already have read all files, we will have to update the go_detector.GetPortFromFilesGo in order not to perform the same process again.*

As a result all detectors will use the same process to read specific files for port detection.

### Process Overview
The final process for each detector will be:

## Implement Combined and No Strategy Options
Currently, upon `component` command we have the `port-detection` arg which specifies the port-detection strategy

### Current State
On the current state as we allow only three values for this arg:

* `docker`: Alizer will look only for dockerfile/containerfile to detect ports
* `compose`: Alizer will look only to docker-compose.yaml
* `source`: Alizer will go directly to source code for port detection.

### The "none" value
As an additional value for the port detection arg, we can add the “none”. That said we can add the following code inside [getPortDetectionStrategy()](https://github.com/redhat-developer/alizer/blob/main/go/pkg/cli/component/component.go#L43):

```golang
for _, algo := range portDetectionAlgorithms {
	if algo == "none" {
		return []model.PortDetectionAlgorithm{}
      } else if algo == "compose" {
		portDetectionStrategy = append(portDetectionStrategy, model.Compose)
	} else if algo == "source" {
		portDetectionStrategy = append(portDetectionStrategy, model.Source)
	} else if algo == "docker" {
		portDetectionStrategy = append(portDetectionStrategy, model.DockerFile)

}
```

So, if a user doesn't want to apply port detection they will have to run:
```./alizer component --port-detection none <path>```

## The library
For users using the alizer library we can add simple documentation for using the [component_recognizer.DetectComponentsInRootWithPathAndPortStartegy](https://github.com/redhat-developer/alizer/blob/main/go/pkg/apis/recognizer/component_recognizer.go#L41) with an empty port detection strategy:

```golang
emptyStrategy := []model.PortDetectionAlgorithm{}
recognizer.DetectComponentsInRootWithPathAndPortStartegy(path, emptyStrategy, , &ctx)
```