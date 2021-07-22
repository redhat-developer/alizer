import path = require('path');
import { assert } from 'sinon';
import { JAVA, PYTHON } from '../src/constants';
import * as recognizer from '../src/recognizer';
import { DevfileType } from '../src/types';

describe('Recognizer', () => {

    const devfileTypes: DevfileType[] = [];

    before(() => {
        devfileTypes.push(createDevfileType("java-maven", "java", "java", ["Java", "Maven"]));
        devfileTypes.push(createDevfileType("java", "java", "java"));
        devfileTypes.push(createDevfileType("java-quarkus", "java", "quarkus", ["Java", "Quarkus"]));
        devfileTypes.push(createDevfileType("java-spring", "java", "spring", ["Java", "Spring"]));
        devfileTypes.push(createDevfileType("java-vertx", "java", "vertx", ["Java", "Vert.x"]));
        devfileTypes.push(createDevfileType("java-wildfly-bootable", "java", "wildfly", ["RHEL8", "Java", "OpenJDK", "Maven", "WildFly", "Microprofile", "WildFly Bootable"]));
        devfileTypes.push(createDevfileType("java-wildfly", "java", "wildfly", ["Java", "WildFly"]));
        devfileTypes.push(createDevfileType("nodejs", "nodejs", "nodejs", ["NodeJS", "Express", "ubi8"]));
        devfileTypes.push(createDevfileType("python-django", "python", "django", ["Python", "pip", "Django"]));
        devfileTypes.push(createDevfileType("python", "python", "python", ["Python", "pip"]));
    });

    function createDevfileType(name: string, language: string, projectType: string, tags?: string[]): DevfileType {
        return {
            getName: () => name,
            getLanguage: () => language,
            getProjectType: () => projectType,
            getTags: () => tags
        };
    }

    it('TestMyself', async () => {
        const languages = await recognizer.detectLanguages('.');
        assert.match(languages.some(l => l.name === 'javascript'), true);
    });

    it('testJAVAMetaDevFile', async () => {
        const devFile = recognizer.selectDevFileFromTypes(path.join(__dirname, '..', '..', '..', 'resources/projects/micronaut'), devfileTypes);
        assert.match((await devFile).getName(), 'java-maven');
    });
    
    it('TestQuarkus', async () => {
        const languages = await recognizer.detectLanguages(path.join(__dirname, '..', '..', '..', 'resources/projects/quarkus'));
        assert.match(languages.some(l => l.name === JAVA), true);
    });

    it('testQuarkusDevFileType', async () => {
        const devFile = recognizer.selectDevFileFromTypes(path.join(__dirname, '..', '..', '..', 'resources/projects/quarkus'), devfileTypes);
        assert.match((await devFile).getName(), 'java-quarkus');
    });

    it('TestMicronaut', async () => {
        const languages = await recognizer.detectLanguages(path.join(__dirname, '..', '..', '..', 'resources/projects/micronaut'));
        assert.match(languages.some(l => l.name === JAVA), true);
    });

    it('testMicronautDevFile', async () => {
        const devFile = recognizer.selectDevFileFromTypes(path.join(__dirname, '..', '..', '..', 'resources/projects/micronaut'), devfileTypes);
        assert.match((await devFile).getName(), 'java-maven');
    });

    it('TestNode', async () => {
        const languages = await recognizer.detectLanguages(path.join(__dirname, '..', '..', '..', 'resources/projects/nodejs-ex'));
        assert.match(languages.some(l => l.name === 'javascript'), true);
    });

    it('testNodeDevFile', async () => {
        const devFile = recognizer.selectDevFileFromTypes(path.join(__dirname, '..', '..', '..', 'resources/projects/nodejs-ex'), devfileTypes);
        assert.match((await devFile).getName(), 'nodejs');
    });

    it('TestDjango', async () => {
        const languages = await recognizer.detectLanguages(path.join(__dirname, '..', '..', '..', 'resources/projects/django'));
        assert.match(languages.some(l => l.name === PYTHON), true);
    });

    it('testDjangoDevFile', async () => {
        const devFile = recognizer.selectDevFileFromTypes(path.join(__dirname, '..', '..', '..', 'resources/projects/django'), devfileTypes);
        assert.match((await devFile).getName(), 'python-django');
    });

    it('TestWithoutGitIgnore', async () => {
        const languages = await recognizer.detectLanguages(path.join(__dirname, '..', '..', '..', 'resources/projects/simple'));
        assert.match(languages.some(l => l.name === 'javascript'), true);
    });

});
