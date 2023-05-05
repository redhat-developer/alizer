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

func CheckoutCommit(url, commit, tempDir string) (string, error) {

	r, err := git.PlainClone(tempDir, false, &git.CloneOptions{
		URL: url,
	})
	if err != nil {
		return "", errors.New("Unable to download the repository " + url)
	}

	w, err := r.Worktree()
	if err != nil {
		return "", errors.New("Unable to checking out to commit " + tempDir)
	}

	err = w.Checkout(&git.CheckoutOptions{
		Hash: plumbing.NewHash(commit),
	})
	if err != nil {
		return "", errors.New("Unable to checking out to commit " + commit)
	}

	return tempDir, nil
}
