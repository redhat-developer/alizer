package utils

var filePathsFromRoot = make(map[string][]string)

func GetCachedFilePathsFromRoot(root string) ([]string, error) {
	if files, hasRoot := filePathsFromRoot[root]; hasRoot {
		return files, nil
	}

	filePaths, err := GetFilePathsFromRoot(root)
	if err != nil {
		return []string{}, err
	}
	filePathsFromRoot[root] = filePaths
	return filePaths, nil
}
