import logo from './logo.svg';
import './App.css';
import TrackedProducts from './components/TrackedProducts'
import Products from './components/Products'

function App() {
  return (
    <div className="main">
        <h1>Price Tracker</h1>
        <TrackedProducts/>
        <Products/>
    </div>
  );
}

export default App;
