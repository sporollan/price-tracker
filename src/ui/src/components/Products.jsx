import axios from "axios"
import { useState } from 'react'
import ProductData from "./ProductData"
import './Products.css'

const Products = () => {
    const [newProductSearchText, setNewProductSearchText] = useState("")
    const [products, setProducts] = useState([])
    const handleUpdateProducts = async () => {
        try {
            const response = await axios.post("http://localhost:8001/run_scraper")
        } catch (error){
            console.log("error updating products")
        }
    };
    const handleNewProductSearchText = (event) => {
        setNewProductSearchText(event.target.value)
    };
    const handleFetchProducts = async () => {
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
        <div>
            <button onClick={handleUpdateProducts}>Update Products</button>
            <input onChange={handleNewProductSearchText}/>
            <button onClick={handleFetchProducts}>Fetch Products</button>
            <div className="product_list">
                {products.map(
                    (product) => (
                    <div key={product.id}>
                        <ProductData
                            product={{
                                name: product.name,
                                metadataId: product.id 
                            }}
                        />
                    </div>
                ))}
            </div>
        </div>
    )
}



export default Products