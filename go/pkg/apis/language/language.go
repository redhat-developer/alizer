package language

type Language struct {
	Name              string
	Aliases           []string
	UsageInPercentage float64
	Frameworks        []string
	Tools             []string
	CanBeComponent    bool
}
