import axios from 'axios';
import { useState, useEffect } from 'react';
import './Tracked.styles.css'

const Tracked = ({setProducts}) => {
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

    const toggleTrackedProduct = async (event, id) => {
        try {
            await axios.put(
                "http://localhost:8001/toggle/" + id
            )
        }catch (error){
            console.log("error toggling product " + id)
        }
        fetchTrackedProducts()
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

    const handleUpdateProducts = async () => {
        try {
            const response = await axios.post("http://localhost:8001/run_scraper")
        } catch (error){
            console.log("error updating products")
        }
    };


    const handleFetchProducts = async (event, newProductSearchText) => {
        try {
            const response = await axios.get(
                "http://localhost:8080/productMetadata/" + newProductSearchText,
            )
//            setNewProductSearchText("")
            setProducts(response.data)
        } catch (error){
            console.log("error fetching products")
        }
    };
    return (
        <div className='tracked_container_list'>
            <div className='tracked_header_container'>
                <div className='tracked_header'>
                    <span>Tracked Products</span>
                </div>
                <button onClick={handleUpdateProducts}>Update</button>
            </div>
            <div className='tracked_row'>
                <input  className="tracked_left" onChange={handleChangeTrackedProduct} placeholder='New Product...'/>
                <button onClick={handleSubmitTrackedProduct}>Add</button>
            </div>
            {trackedProducts.map(
                (product) => (
                    <div className='tracked_row'>
                        <div className='tracked_left' onClick={event => handleFetchProducts(event, product.name)} key={product.name} style={{
                            textDecoration: product.is_active ? 'none' : 'line-through'
                        }}>
                            <span>
                                    {product.name}
                            </span>
                        </div>
                        <div className='tracked_right'>
                            <button onClick={event => toggleTrackedProduct(event, product.id)}>Track</button>
                        </div>
                    </div>
                )
            )}
            <div className='tracked_row'>
                <div>
                </div>
            </div>
        </div>
    )
}

export default Tracked;