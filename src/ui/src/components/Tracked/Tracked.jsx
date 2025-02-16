import axios from 'axios';
import { useState, useEffect } from 'react';
import './Tracked.styles.css'

// Configure axios defaults for the host
axios.defaults.baseURL = 'http://ui.local';

const Tracked = ({setProducts}) => {
    const [trackedProducts, setTrackedProducts] = useState([]);
    const [newTrackedProduct, setNewTrackedProuct] = useState("");

    useEffect(() => {
        fetchTrackedProducts();
    }, []);

    const fetchTrackedProducts = async () => {
        try {
            const response = await axios.get(
                "tracked/?skip=0&limit=100"  // Note: removed leading slash
            );
            setTrackedProducts(response.data);
        }catch (error){
            console.log("error fetching tracked products")
        };
    };

    const toggleTrackedProduct = async (event, id) => {
        try {
            await axios.put(
                `toggle/${id}`  // Note: removed leading slash
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
            await axios.post(
                "tracked",  // Note: removed leading slash
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
            await axios.post("run_scraper")  // Note: removed leading slash
        } catch (error){
            console.log("error updating products")
        }
    };

    const handleFetchProducts = async (event, newProductSearchText) => {
        try {
            const response = await axios.get(
                `productMetadata/${newProductSearchText}`,  // Note: removed leading slash
            )
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
            <div className='tracked_item_list_container'>
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
            </div>
            <div className='tracked_row'>
                <div>
                </div>
            </div>
        </div>
    )
}

export default Tracked;
