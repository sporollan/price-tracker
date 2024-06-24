import './App.css';
import Tracked from './components/Tracked/Tracked'
import Products from './components/Products/Products'
import Header from './components/Header/Header'
import { useState } from 'react';


function App() {
  const [products, setProducts] = useState([])

  return (
    <div className="main_container">
        <div className='header'><Header/></div>
        <div className='body_container'>
          <div className='left_banner'>
          </div>
          <div className='left_container'>
            <Tracked setProducts={setProducts}/>
          </div>
          <div className='right_container'>
            <Products products={products}/>
          </div>
          <div className='right_banner'></div>
        </div>
    </div>
  );
}

export default App;
