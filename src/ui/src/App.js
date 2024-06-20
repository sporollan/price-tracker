import logo from './logo.svg';
import './App.css';
import TrackedProducts from './components/Tracked/Tracked'
import Products from './components/Products/Products'
import Header from './components/Header/Header'

function App() {
  return (
    <div className="main">
        <div><Header/></div>
        <div className='main_container'>
          <div className='main_container_item'>
            <TrackedProducts/>
          </div>
          <div className='main_container_item'>
            <Products/>
          </div>
        </div>
    </div>
  );
}

export default App;
