import axios from 'axios';
import Tracked from "../Tracked/Tracked"
import Products from "../Products/Products"
import './Home.styles.css'
import { useState, useEffect } from 'react';
axios.defaults.baseURL = 'http://ui.local';


const Home = () => {
    const [products, setProducts] = useState([])
    const token = localStorage.getItem("token");

    useEffect( () => {
            fetchRandomProducts();
    }, []);

    const fetchRandomProducts = async () => {
        try {
            const response = await axios.get(
                'productMetadata/cafe'); // Adjust endpoint
            setProducts(response.data);
        } catch (error) {
            console.error("Error fetching random products:", error);
        }
    };

    return (
            <div className="main_container">
            <div className='body_container'>
            <div className='left_banner'>
            </div>
            {token && (
                <div className='left_container'>
                    <Tracked setProducts={setProducts}/>
                </div>
            )}
            <div className='right_container'>
                <Products products={products}/>
            </div>
            <div className='right_banner'></div>
            </div>
            </div>
    )
}

export default Home