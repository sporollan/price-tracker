import logo from './logo.svg';
import './App.css';
import Tracked from './components/Tracked/Tracked'
import Products from './components/Products/Products'
import Header from './components/Header/Header'
import { useState, useEffect } from 'react';


function App() {
  const [products, setProducts] = useState([])

  return (
    <div className="main">
        <div className='header_container'><Header/></div>
        <div className='main_container'>
          <div className='main_tracked'>
            <Tracked setProducts={setProducts}/>
          </div>
          <div className='main_container_item'>
            <Products products={products}/>
          </div>
        </div>
    </div>
  );
}

export default App;
