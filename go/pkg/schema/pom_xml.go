package schema

type Pom struct {
	Dependencies struct {
		Text       string `xml:",chardata"`
		Dependency []struct {
			Text       string `xml:",chardata"`
			GroupId    string `xml:"groupId"`
			ArtifactId string `xml:"artifactId"`
			Version    string `xml:"version"`
			Scope      string `xml:"scope"`
		} `xml:"dependency"`
	} `xml:"dependencies"`
}
