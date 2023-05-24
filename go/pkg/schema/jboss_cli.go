/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc.
 ******************************************************************************/
package schema

type JBossCLIXML struct {
	Text              string `xml:",chardata"`
	DefaultController struct {
		Text     string `xml:",chardata"`
		Protocol string `xml:"protocol"`
		Host     string `xml:"host"`
		Port     string `xml:"port"`
	} `xml:"default-controller"`
	Controllers struct {
		Text       string `xml:",chardata"`
		Controller []struct {
			Text     string `xml:",chardata"`
			Protocol string `xml:"protocol"`
			Host     string `xml:"host"`
			Port     string `xml:"port"`
		} `xml:"controller"`
	} `xml:"controllers"`
}
