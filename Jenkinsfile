#!/usr/bin/env groovy

node('rhel7'){
	stage('Checkout repo') {
		deleteDir()
		git url: 'https://github.com/redhat-developer/alizer',
			branch: "${sha1}"
	}

	stage('Build') {
		sh "./mvnw verify"
	}

	stage('Deploy') {
		sh "./mvnw deploy"
	}

}