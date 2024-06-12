import axios from 'axios';
import { useState, useEffect } from 'react';

const TrackedProducts = () => {
    const [trackedProducts, setTrackedProducts] = useState([]);
    const [newTrackedProduct, setNewTrackedProuct] = useState("");
    useEffect(() => {
        fetchTrackedProducts();
    }, []);
    const fetchTrackedProducts = async () => {
        try {
            const response = await axios.get(
                "http://localhost:8001/tracked/?skip=0&limit=100"
            );
            setTrackedProducts(response.data);
        }catch (error){
            console.log("error fetching tracked products")
        };
    };
    const handleChangeTrackedProduct = (event) => {
        setNewTrackedProuct(event.target.value)
    };
    const handleSubmitTrackedProduct = async () => {
        try {
            console.log(newTrackedProduct)
            const response = await axios.post(
                "http://localhost:8001/tracked",
                {
                    name: newTrackedProduct,
                    sites: "COTO"
                }
            )
            setNewTrackedProuct("")
            fetchTrackedProducts()
        }catch (error){
            console.log("error posting tracked product")
        };
    };
    return (
        <div>
            <h1>Tracked Products</h1>
            <ul>
                {trackedProducts.map(
                    (product) => (
                        <li key={product.name}>
                            {product.name}
                        </li>
                    )
                )}
            </ul>
            <input onChange={handleChangeTrackedProduct}/>
            <button onClick={handleSubmitTrackedProduct}>Submit</button>
        </div>
    )
}

export default TrackedProducts;