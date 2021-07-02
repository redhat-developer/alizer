import path = require('path');
import { assert } from 'sinon';
import { JAVA, PYTHON } from '../src/constants';
import * as recognizer from '../src/recognizer';

describe('Recognizer', () => {

    it('TestMyself', async () => {
        const languages = await recognizer.detectLanguages('.');
        assert.match(languages.some(l => l.name === 'javascript'), true);
    });
    
    it('TestQuarkus', async () => {
        const languages = await recognizer.detectLanguages(path.join(__dirname, '..', '..', '..', 'resources/projects/quarkus'));
        assert.match(languages.some(l => l.name === JAVA), true);
    });

    it('TestMicronaut', async () => {
        const languages = await recognizer.detectLanguages(path.join(__dirname, '..', '..', '..', 'resources/projects/micronaut'));
        assert.match(languages.some(l => l.name === JAVA), true);
    });

    it('TestNode', async () => {
        const languages = await recognizer.detectLanguages(path.join(__dirname, '..', '..', '..', 'resources/projects/nodejs-ex'));
        assert.match(languages.some(l => l.name === 'javascript'), true);
    });

    it('TestDjango', async () => {
        const languages = await recognizer.detectLanguages(path.join(__dirname, '..', '..', '..', 'resources/projects/django'));
        assert.match(languages.some(l => l.name === PYTHON), true);
    });

});
