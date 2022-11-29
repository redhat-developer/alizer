/*******************************************************************************
 * Copyright (c) 2022 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc.
 ******************************************************************************/
package test

import (
	"errors"
	"io/ioutil"

	"github.com/go-git/go-git/v5"
	"github.com/go-git/go-git/v5/plumbing"
)

type ComponentLanguage struct {
	Name       string   `json:"name"`
	Frameworks []string `json:"frameworks"`
	Tools      []string `json:"tools"`
}

type ComponentProperties struct {
	Languages []ComponentLanguage `json:"languages"`
	Ports     []int               `json:"ports"`
	Name      string              `json:"name"`
}

type GitTestProperties struct {
	Branch     string                `json:"branch"`
	Commit     string                `json:"commit"`
	Components []ComponentProperties `json:"components"`
	Directory  string                `json:"directory"`
}

type GitTests map[string]GitTestProperties

//
func CheckoutCommit(url string, commit string) (string, error) {
	dirName := commit
	if len(dirName) > 7 {
		dirName = dirName[:7]
	}
	dir, err := ioutil.TempDir(".", dirName)
	if err != nil {
		return "", errors.New("Unable to create a directory where to download the git repo " + url)
	}

	r, err := git.PlainClone(dir, false, &git.CloneOptions{
		URL: url,
	})
	if err != nil {
		return "", errors.New("Unable to download the repository " + url)
	}

	w, err := r.Worktree()
	if err != nil {
		return "", errors.New("Unable to checking out to commit " + commit)
	}

	err = w.Checkout(&git.CheckoutOptions{
		Hash: plumbing.NewHash(commit),
	})
	if err != nil {
		return "", errors.New("Unable to checking out to commit " + commit)
	}

	return dir, nil
}
