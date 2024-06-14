import axios from "axios"
import { useState } from 'react'
import ProductData from "./ProductData"

const Product = () => {
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
            <ul>
                {products.map(product => (
                    <li>
                        <ProductData
                            product={{
                                name: product.name,
                                metadataId: product.id 
                            }}
                        />
                    </li>
                ))}
            </ul>
        </div>
    )
}



export default Product