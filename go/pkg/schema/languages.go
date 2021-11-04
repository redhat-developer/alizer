package schema

type LanguageProperties struct {
	Type               string   `yaml:"type,omitempty"`
	Color              string   `yaml:"color,omitempty"`
	Extensions         []string `yaml:"extensions,omitempty"`
	TmScope            string   `yaml:"tm_scope,omitempty"`
	AceMode            string   `yaml:"ace_mode,omitempty"`
	LanguageID         int      `yaml:"language_id,omitempty"`
	Aliases            []string `yaml:"aliases,omitempty"`
	CodemirrorMode     string   `yaml:"codemirror_mode,omitempty"`
	CodemirrorMimeType string   `yaml:"codemirror_mime_type,omitempty"`
	Group              string   `yaml:"group"`
	Filenames          []string `yaml:"filenames"`
}

type LanguagesProperties map[string]LanguageProperties
