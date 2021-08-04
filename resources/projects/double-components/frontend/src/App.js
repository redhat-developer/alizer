import logo from './logo.svg';
import './App.css';
import { getHello } from './fetch.api';
import React, {useEffect, useState} from 'react';

const App = () => {

  const [hello, setHello] = useState('');
  const fetchPosts = async () => {
		const response = await getHello();
		if (response.status === 200) {
			const data = await response.data;
			setHello(data)
		}
		
	};

	useEffect(() => {
		fetchPosts()
	}, []);

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
          {hello}
        </a>
      </header>
    </div>
  );
}

export default App;
