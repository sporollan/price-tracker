import axios from "axios"
import { useState, useEffect } from 'react'
import dayjs from "dayjs"

const ProductData = ({product}) => {
    const [products, setProducts] = useState([])
    const [toggle, setToggle] = useState(false)

    const handleFetchProductsData = async() => {
        if(!toggle) {
            fetchProductsData();
        }
        setToggle(!toggle);
    };

    const fetchProductsData = async () => {
        try {
            const response = await axios.get(
                "http://localhost:8080/product/" + product.metadataId,
            )
            setProducts(response.data)
        } catch (error){
            console.log("error fetching products")
        }
    };
    return (
        <div>
            <div onClick={handleFetchProductsData}>{product.name}</div>
                <ul>
                    {products.map(
                        (productData) => (
                            toggle &&
                            <li>
                                <ul>
                                    <li style={{ alignItems: 'stretch' }}> {dayjs(new Date(productData.dateAdded)).format("DD/MM/YY")}</li>
                                    <li>{"$" + productData.price/100}</li>
                                </ul>
                            </li>
                        )
                    )}
                </ul>
        </div>
    )
}



export default ProductData